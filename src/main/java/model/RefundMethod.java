package model;

/**
 * Enum for refund methods
 */
public enum RefundMethod {
    ORIGINAL_PAYMENT("Original Payment Method"),
    BANK_TRANSFER("Bank Transfer"),
    STORE_CREDIT("Store Credit"),
    CHECK("Check");

    private final String displayName;

    RefundMethod(String displayName) {
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
