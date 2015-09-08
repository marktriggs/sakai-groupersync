package edu.nyu.classes.groupersync.tool;

import java.io.IOException;
import edu.nyu.classes.groupersync.api.GrouperSyncException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.tool.cover.ToolManager;
import javax.servlet.ServletException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import edu.nyu.classes.groupersync.api.GrouperSyncService;

public class CreateGroupHandler extends BaseHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	try {
	    GrouperSyncService grouper = getGrouperSyncService();
	    String siteId = ToolManager.getCurrentPlacement().getContext();

	    Site site = SiteService.getSite(siteId);

	    String requiredSuffix = buildRequiredSuffix(site);

	    String groupId = request.getParameter("groupId");
	    String sakaiGroupId = request.getParameter("sakaiGroupId");
	    String description = request.getParameter("description");

	    if (groupId == null || "".equals(groupId.trim())) {
		throw new ServletException("Invalid group provided!");
	    }

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

	    response.sendRedirect(determineBaseURL().toString() + "?success=group_created");

	} catch (IdUnusedException e) {
	    throw new ServletException("Failed to find site", e);
	} catch (GrouperSyncException e) {
	    response.sendRedirect(determineBaseURL().toString() + "?error=group_in_use");
	}
    }
}
