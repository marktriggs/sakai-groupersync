package edu.nyu.classes.groupersync.api;

import org.sakaiproject.authz.api.AuthzGroup;

public interface GrouperSyncService {

    GroupInfo getGroupInfo(AuthzGroup group) throws GrouperSyncException;

    void markGroupForSync(final String groupId, final String sakaiGroupId, final String description) throws GrouperSyncException;

    GrouperSyncStorage getStorage();

    void init();

    void destroy();
}
