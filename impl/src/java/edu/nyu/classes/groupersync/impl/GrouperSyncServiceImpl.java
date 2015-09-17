// FIXME: Currently seems like this class isn't earning its keep.  Might as well
// just rename the GrouperSyncStorageImpl to GrouperSyncServiceImpl?
//
package edu.nyu.classes.groupersync.impl;

import edu.nyu.classes.groupersync.api.GroupInfo;
import edu.nyu.classes.groupersync.api.GrouperSyncException;
import edu.nyu.classes.groupersync.api.GrouperSyncService;
import edu.nyu.classes.groupersync.api.GrouperSyncStorage;

public class GrouperSyncServiceImpl implements GrouperSyncService {

    @Override
    public GroupInfo getGroupInfo(String sakaiGroupId) throws GrouperSyncException {
        GrouperSyncStorage storage = getStorage();

        return storage.getGroupInfo(sakaiGroupId);
    }

    @Override
    public GrouperSyncStorage getStorage() {
        return new GrouperSyncStorageImpl();
    }

    @Override
    public void markGroupForSync(final String groupId, final String grouperGroupId, final String sakaiGroupId, final String description) throws GrouperSyncException {
        getStorage().markGroupForSync(groupId, grouperGroupId, sakaiGroupId, description);
    }

    @Override
    public void updateDescription(final String groupId, final String description) throws GrouperSyncException {
        getStorage().updateDescription(groupId, description);
    }

    @Override
    public void deleteGroup(final String groupId) throws GrouperSyncException {
        getStorage().deleteGroup(groupId);
    }

    @Override
    public boolean isGroupAvailable(final String groupId) throws GrouperSyncException {
        return getStorage().isGroupAvailable(groupId);
    }

    @Override
    public void init() {
    }

    @Override
    public void destroy() {
    }

}
