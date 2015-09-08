package edu.nyu.classes.groupersync.api;

import org.sakaiproject.authz.api.AuthzGroup;

public interface GrouperSyncService {
    
    public GroupInfo getGroupInfo(AuthzGroup group) throws GrouperSyncException;

    public GrouperSyncStorage getStorage();

    public void init();

    public void destroy();
}
