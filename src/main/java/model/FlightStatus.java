package model;

/**
 * Enum for flight status
 */
public enum FlightStatus {
    SCHEDULED("Scheduled"),
    BOARDING("Boarding"),
    DEPARTED("Departed"),
    ARRIVED("Arrived"),
    DELAYED("Delayed"),
    CANCELLED("Cancelled"),
    ON_TIME("On Time");

    private final String displayName;

    FlightStatus(String displayName) {
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