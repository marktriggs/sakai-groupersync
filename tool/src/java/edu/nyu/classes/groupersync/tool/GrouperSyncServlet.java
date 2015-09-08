package edu.nyu.classes.groupersync.tool;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;
import edu.nyu.classes.groupersync.api.GrouperSyncException;
import edu.nyu.classes.groupersync.api.GrouperSyncService;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GrouperSyncServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(GrouperSyncServlet.class);

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        checkAccessControl();

        GrouperSyncService grouper = getGrouperSyncService();
        String siteId = ToolManager.getCurrentPlacement().getContext();

        if (request.getPathInfo().indexOf("/create_group") >= 0) {
            try {
                Site site = SiteService.getSite(siteId);

                String requiredSuffix = buildRequiredSuffix(site);

                String groupId = request.getParameter("groupId");
                String sakaiGroupId = request.getParameter("sakaiGroupId");
                String description = request.getParameter("description");

                if (siteId.equals(sakaiGroupId)) {
                    // Creating the 'all' users link.
                } else {
                    Group group = site.getGroup(sakaiGroupId);

                    if (group == null) {
                        throw new ServletException("Group not found");
                    }

                    sakaiGroupId = group.getId();
                }

                grouper.markGroupForSync(groupId + requiredSuffix,
                        sakaiGroupId,
                        description);
            } catch (IdUnusedException e) {
                throw new ServletException("Failed to find site", e);
            } catch (GrouperSyncException e) {
                throw new ServletException("Failed to mark group for sync", e);
            }
        } else {
            throw new ServletException("Unrecognized request");
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        checkAccessControl();

        GrouperSyncService grouper = getGrouperSyncService();

        response.setHeader("Content-Type", "text/html");

        URL baseUrl = determineBaseURL();

        Handlebars handlebars = loadHandlebars();

        String siteId = ToolManager.getCurrentPlacement().getContext();

        try {
            Site site = SiteService.getSite(siteId);
            Collection<GroupView> groups = new ArrayList<GroupView>();

            groups.add(new GroupView(site, "All site members", grouper));
            for (AuthzGroup group : site.getGroups()) {
                groups.add(new GroupView(group, grouper));
            }

            Map<String, Object> context = new HashMap<String, Object>();
            context.put("groups", groups);
            context.put("subpage", "index");

            context.put("requiredSuffix", buildRequiredSuffix(site));
            context.put("baseUrl", baseUrl);
            context.put("skinRepo", ServerConfigurationService.getString("skin.repo", ""));
            context.put("randomSakaiHeadStuff", request.getAttribute("sakai.html.head"));

            Template template = handlebars.compile("edu/nyu/classes/groupersync/tool/views/layout");
            response.getWriter().write(template.apply(context));
        } catch (IdUnusedException e) {
            LOG.warn("Site not found:" + siteId, e);
            throw new ServletException("Site not found:" + siteId);
        } catch (IOException e) {
            LOG.warn("Write failed", e);
        }
    }

    private String buildRequiredSuffix(Site site) {
        String termEid = site.getProperties().getProperty(Site.PROP_SITE_TERM_EID);

        if (termEid == null) {
            termEid = "prj";
        }

        return ":" + termEid + ":classes:" + site.getId().substring(0, 4);
    }


    private void checkAccessControl() throws ServletException {
        String siteId = ToolManager.getCurrentPlacement().getContext();

        if (!SecurityService.unlock("site.manage", "/site/" + siteId)) {
            LOG.error("Access denied to GrouperSync management tool for user " + SessionManager.getCurrentSessionUserId());
            throw new ServletException("Access denied");
        }
    }


    private URL determineBaseURL() throws ServletException {
        String siteId = ToolManager.getCurrentPlacement().getContext();
        String toolId = ToolManager.getCurrentPlacement().getId();

        try {
            return new URL(ServerConfigurationService.getPortalUrl() + "/site/" + siteId + "/tool/" + toolId + "/");
        } catch (MalformedURLException e) {
            throw new ServletException("Couldn't determine tool URL", e);
        }
    }


    private Handlebars loadHandlebars() {
        final Handlebars handlebars = new Handlebars();

        handlebars.registerHelper("subpage", new Helper<Object>() {
            @Override
            public CharSequence apply(final Object context, final Options options) {
                String subpage = options.param(0);
                try {
                    Template template = handlebars.compile("edu/nyu/classes/groupersync/tool/views/" + subpage);
                    return template.apply(context);
                } catch (IOException e) {
                    LOG.warn("IOException while loading subpage", e);
                    return "";
                }
            }
        });

        return handlebars;
    }


    private GrouperSyncService getGrouperSyncService() {
        GrouperSyncService result = (GrouperSyncService) ComponentManager.get("edu.nyu.classes.groupersync.api.GrouperSyncService");

        if (result == null) {
            throw new RuntimeException("Couldn't get the GrouperSyncService");
        }

        return result;

    }

}
