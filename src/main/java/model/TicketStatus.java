package model;

/**
 * Enum for ticket status
 */
public enum TicketStatus {
    OPEN("Open"),
    IN_PROGRESS("In Progress"),
    ESCALATED("Escalated"),
    RESOLVED("Resolved"),
    CLOSED("Closed");

    private final String displayName;

    TicketStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
} 