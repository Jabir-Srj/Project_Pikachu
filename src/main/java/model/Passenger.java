package model;

import java.time.LocalDate;

/**
 * Represents a passenger in a flight booking.
 */
public class Passenger {
    private String passengerId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String passportNumber;
    private String nationality;
    private String gender;
    private String seatNumber;
    private PassengerType type; // Adult, Child, Infant

    public Passenger() {}

    public Passenger(String firstName, String lastName, LocalDate dateOfBirth, 
                    String passportNumber, String nationality) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.passportNumber = passportNumber;
        this.nationality = nationality;
        this.type = PassengerType.ADULT; // Default
    }

    /**
     * Get full name of passenger
     * @return Full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Calculate age based on date of birth
     * @return Age in years
     */
    public int getAge() {
        if (dateOfBirth == null) return 0;
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }

    // Getters and Setters
    public String getPassengerId() { return passengerId; }
    public void setPassengerId(String passengerId) { this.passengerId = passengerId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getPassportNumber() { return passportNumber; }
    public void setPassportNumber(String passportNumber) { this.passportNumber = passportNumber; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public PassengerType getType() { return type; }
    public void setType(PassengerType type) { this.type = type; }

    @Override
    public String toString() {
        return "Passenger{" +
                "name='" + getFullName() + '\'' +
                ", passportNumber='" + passportNumber + '\'' +
                ", nationality='" + nationality + '\'' +
                ", type=" + type +
                '}';
    }
}

/**
 * Enum for passenger types
 */
enum PassengerType {
    ADULT("Adult"),
    CHILD("Child"),
    INFANT("Infant");

    private final String displayName;

    PassengerType(String displayName) {
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