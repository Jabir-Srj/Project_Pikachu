package model;

/**
 * Enum defining the different user roles in the airline ticketing system.
 */
public enum UserRole {
    CUSTOMER("Customer"),
    ADMIN("Admin"),
    AIRLINE_MANAGEMENT("Airline Management");

    private final String displayName;

    UserRole(String displayName) {
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