package edu.nyu.classes.groupersync.api;

import org.sakaiproject.authz.api.AuthzGroup;

public interface GrouperSyncService {
    
    public GroupInfo getGroupInfo(AuthzGroup group) throws GrouperSyncException;

    public void markGroupForSync(final String groupId, final String sakaiGroupId, final String description) throws GrouperSyncException;

    public GrouperSyncStorage getStorage();

    public void init();

    public void destroy();
}
