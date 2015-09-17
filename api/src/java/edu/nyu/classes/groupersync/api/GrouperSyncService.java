package edu.nyu.classes.groupersync.api;

public interface GrouperSyncService {

    GroupInfo getGroupInfo(String sakaiGroupId) throws GrouperSyncException;

    void markGroupForSync(final String groupId, final String grouperGroupId, final String sakaiGroupId, final String description) throws GrouperSyncException;

    void updateDescription(final String groupId, final String description) throws GrouperSyncException;

    void deleteGroup(final String groupId) throws GrouperSyncException;

    boolean isGroupAvailable(final String groupId) throws GrouperSyncException;

    GrouperSyncStorage getStorage();

    void init();

    void destroy();
}
