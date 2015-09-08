package edu.nyu.classes.groupersync.api;

import java.util.Set;
import java.util.Date;

public interface GrouperSyncStorage {
    public GroupInfo getGroupInfo(final String sakaiGroupId) throws GrouperSyncException;

    public Set<UserWithRole> getMembers(final String groupId) throws GrouperSyncException;

    public void recordChanges(final String groupId,
                              final Set<UserWithRole> addedUsers,
                              final Set<UserWithRole> droppedUsers,
                              final Set<UserWithRole> changedRoles) throws GrouperSyncException;

    public Date getLastRunDate() throws GrouperSyncException;

    public void setLastRunDate(final Date date) throws GrouperSyncException;

    public void prepopulateGroupsBasedOnThisOneWeirdTrick() throws GrouperSyncException;
}
