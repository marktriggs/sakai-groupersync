package edu.nyu.classes.groupersync.impl;

import edu.nyu.classes.groupersync.api.GroupInfo;
import edu.nyu.classes.groupersync.api.GrouperSyncException;
import edu.nyu.classes.groupersync.api.GrouperSyncService;
import edu.nyu.classes.groupersync.api.GrouperSyncStorage;

import org.sakaiproject.authz.api.AuthzGroup;

public class GrouperSyncServiceImpl implements GrouperSyncService {

    @Override
    public GroupInfo getGroupInfo(AuthzGroup group) throws GrouperSyncException {
        GrouperSyncStorage storage = getStorage();

        return storage.getGroupInfo(group.getId());
    }

    @Override
    public GrouperSyncStorage getStorage() {
        return new GrouperSyncStorageImpl();
    }

    @Override
    public void markGroupForSync(final String groupId, final String sakaiGroupId, final String description) throws GrouperSyncException {
        getStorage().markGroupForSync(groupId, sakaiGroupId, description);
    }

    @Override
    public void init() {
    }

    @Override
    public void destroy() {
    }

}
