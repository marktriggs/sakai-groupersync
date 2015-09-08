package edu.nyu.classes.groupersync.api;

public class GroupInfo {

    private GroupStatus status;
    private String label;
    private String grouperId;
    private String sakaiId;
    

    public enum GroupStatus {
        AVAILABLE_FOR_SYNC,
        MARKED_FOR_SYNC,
        UNKNOWN,
    }

    public GroupInfo() {
        // Null object
        this(GroupStatus.AVAILABLE_FOR_SYNC, "", null, null);
    }

    public static GroupInfo unknown() {
        return new GroupInfo(GroupStatus.UNKNOWN, "", null, null);
    }

    public GroupInfo(String label, String grouperId, String sakaiId) {
        this(GroupStatus.MARKED_FOR_SYNC, label, grouperId, sakaiId);
    }

    public GroupInfo(GroupStatus status, String label, String grouperId, String sakaiId) {
        this.status = status;
        this.label = label;
        this.grouperId = grouperId;
        this.sakaiId = sakaiId;
    }

    public String getGrouperId() {
        return grouperId;
    }

    public String getSakaiId() {
        return sakaiId;
    }

    public GroupStatus getStatus() {
        return status;
    }

    public String getLabel() {
        return label;
    }
}
