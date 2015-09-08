package edu.nyu.classes.groupersync.tool;

import java.io.IOException;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;
import org.sakaiproject.authz.api.AuthzGroup;
import java.util.ArrayList;
import java.util.Collection;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.tool.cover.ToolManager;
import edu.nyu.classes.groupersync.api.GrouperSyncService;
import javax.servlet.ServletException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.sakaiproject.exception.IdUnusedException;
import com.github.jknack.handlebars.Helper;
import java.net.MalformedURLException;
import com.github.jknack.handlebars.Template;
import org.sakaiproject.component.cover.ServerConfigurationService;
import java.util.HashMap;
import com.github.jknack.handlebars.Handlebars;
import java.net.URL;

public class IndexHandler extends BaseHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setHeader("Content-Type", "text/html");

        GrouperSyncService grouper = getGrouperSyncService();
        String siteId = ToolManager.getCurrentPlacement().getContext();

	try {
	    Site site = SiteService.getSite(siteId);
	    Collection<GroupView> groups = new ArrayList<GroupView>();

	    groups.add(new GroupView(site, "All site members", grouper));
	    for (AuthzGroup group : site.getGroups()) {
		groups.add(new GroupView(group, grouper));
	    }

	    Map<String, Object> context = new HashMap<String, Object>();
	    context.put("baseUrl", determineBaseURL());
	    context.put("skinRepo", ServerConfigurationService.getString("skin.repo", ""));
	    context.put("randomSakaiHeadStuff", request.getAttribute("sakai.html.head"));
	    context.put("requiredSuffix", buildRequiredSuffix(site));
	    context.put("groups", groups);
	    context.put("subpage", "index");

	    Handlebars handlebars = loadHandlebars();
	    Template template = handlebars.compile("edu/nyu/classes/groupersync/tool/views/layout");
	    response.getWriter().write(template.apply(context));
	} catch (IdUnusedException e) {
	    throw new ServletException("Couldn't find site", e);
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
                    return "";
                }
            }
        });

        return handlebars;
    }

}
