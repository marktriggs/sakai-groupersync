package edu.nyu.classes.groupersync.api;

public interface GrouperSyncService {

    GroupInfo getGroupInfo(String sakaiGroupId) throws GrouperSyncException;

    void markGroupForSync(final String groupId, final String sakaiGroupId, final String description) throws GrouperSyncException;

    void updateDescription(final String groupId, final String description) throws GrouperSyncException;

    GrouperSyncStorage getStorage();

    void init();

    void destroy();
}
