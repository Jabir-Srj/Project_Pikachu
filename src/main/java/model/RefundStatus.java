package model;

/**
 * Enum for refund status
 */
public enum RefundStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    PROCESSED("Processed");

    private final String displayName;

    RefundStatus(String displayName) {
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
