package edu.nyu.classes.groupersync.api;

public class GroupInfo {

    private final GroupStatus status;
    private final String label;
    private final String grouperId;
    private final String sakaiId;


    public GroupInfo() {
        // Null object
        this(GroupStatus.AVAILABLE_FOR_SYNC, "", null, null);
    }

    public GroupInfo(String label, String grouperId, String sakaiId) {
        this(GroupStatus.MARKED_FOR_SYNC, label, grouperId, sakaiId);
    }

    private GroupInfo(GroupStatus status, String label, String grouperId, String sakaiId) {
        this.status = status;
        this.label = label;
        this.grouperId = grouperId;
        this.sakaiId = sakaiId;
    }

    public static GroupInfo unknown() {
        return new GroupInfo(GroupStatus.UNKNOWN, "", null, null);
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

    public enum GroupStatus {
        AVAILABLE_FOR_SYNC("Inactive"),
        MARKED_FOR_SYNC("Active"),
        UNKNOWN("Unknown");

        private final String label;

        GroupStatus(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }
}
