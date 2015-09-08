package edu.nyu.classes.groupersync.tool;

import java.io.IOException;
import java.lang.CharSequence;
import java.lang.Override;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Helper;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.component.cover.ComponentManager;
import edu.nyu.classes.groupersync.api.GrouperSyncService;

public class GrouperSyncServlet extends HttpServlet {

	private static final Logger LOG = LoggerFactory.getLogger(GrouperSyncServlet.class);

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		checkAccessControl();

		GrouperSyncService grouper = getGrouperSyncService();

		response.setHeader("Content-Type", "text/html");

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

	private void checkAccessControl() {
		//String siteId = ToolManager.getCurrentPlacement().getContext();
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
