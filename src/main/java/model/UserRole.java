package model;

/**
 * Enum defining the different user roles in the scholarly publishing system.
 * Maintains backward compatibility with airline system roles.
 */
public enum UserRole {
    READER("Reader"),           // General user who can read and comment (was CUSTOMER)
    PUBLISHER("Publisher"),      // Author/Editor who can submit articles
    ADMIN("Admin"),             // Administrator with full editorial control
    
    // Backward compatibility aliases
    CUSTOMER("Reader"),         // Alias for READER
    AIRLINE_MANAGEMENT("Publisher"); // Alias for PUBLISHER

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