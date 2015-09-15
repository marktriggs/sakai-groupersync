package edu.nyu.classes.groupersync.api;

import java.util.Date;
import java.util.Set;

public interface GrouperSyncStorage {
    GroupInfo getGroupInfo(final String sakaiGroupId) throws GrouperSyncException;

    Set<UserWithRole> getMembers(final String groupId) throws GrouperSyncException;

    void recordChanges(final String groupId,
                       final Set<UserWithRole> addedUsers,
                       final Set<UserWithRole> droppedUsers,
                       final Set<UserWithRole> changedRoles) throws GrouperSyncException;

    Date getLastRunDate() throws GrouperSyncException;

    void setLastRunDate(final Date date) throws GrouperSyncException;

    void markGroupForSync(final String groupId, final String sakaiGroupId, final String description) throws GrouperSyncException;

    void deleteGroup(final String groupId) throws GrouperSyncException;

    void updateDescription(final String groupId, final String description) throws GrouperSyncException;
}
