package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Customer class extending User for customers who book flights and use services.
 */
public class Customer extends User {
    private List<String> bookingHistory;
    private String passportNumber;
    private String nationality;
    private String emergencyContact;
    private boolean isFrequentFlyer;
    private int loyaltyPoints;

    public Customer() {
        super();
        this.bookingHistory = new ArrayList<>();
        this.loyaltyPoints = 0;
        this.isFrequentFlyer = false;
        this.setRole(UserRole.CUSTOMER);
    }

    public Customer(String userId, String username, String password, String email,
                    String firstName, String lastName, String phoneNumber,
                    String passportNumber, String nationality) {
        super(userId, username, password, email, firstName, lastName, phoneNumber, UserRole.CUSTOMER);
        this.bookingHistory = new ArrayList<>();
        this.passportNumber = passportNumber;
        this.nationality = nationality;
        this.loyaltyPoints = 0;
        this.isFrequentFlyer = false;
    }

    @Override
    public void updateProfile() {
        // Customer-specific profile update logic
        System.out.println("Updating customer profile for: " + getFirstName() + " " + getLastName());
        // This would typically update customer-specific information in the database
    }

    /**
     * Book a flight for this customer
     * @param booking The booking to add to customer's history
     */
    public void bookFlight(Booking booking) {
        if (booking != null) {
            bookingHistory.add(booking.getBookingId());
            addLoyaltyPoints(calculatePointsForBooking(booking));
        }
    }

    /**
     * Calculate loyalty points for a booking
     * @param booking The booking to calculate points for
     * @return Number of loyalty points earned
     */
    private int calculatePointsForBooking(Booking booking) {
        // Simple calculation: 1 point per $10 spent
        return (int) (booking.getTotalPrice() / 10);
    }

    /**
     * Add loyalty points to customer account
     * @param points Points to add
     */
    public void addLoyaltyPoints(int points) {
        this.loyaltyPoints += points;
        // Update frequent flyer status if threshold is reached
        if (this.loyaltyPoints >= 1000 && !this.isFrequentFlyer) {
            this.isFrequentFlyer = true;
        }
    }

    /**
     * Check if customer can request a refund for a booking
     * @param bookingId The booking ID to check
     * @return true if refund can be requested
     */
    public boolean canRequestRefund(String bookingId) {
        return bookingHistory.contains(bookingId);
    }

    // Getters and Setters
    public List<String> getBookingHistory() { return new ArrayList<>(bookingHistory); }
    public void setBookingHistory(List<String> bookingHistory) { this.bookingHistory = bookingHistory; }

    public String getPassportNumber() { return passportNumber; }
    public void setPassportNumber(String passportNumber) { this.passportNumber = passportNumber; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public boolean isFrequentFlyer() { return isFrequentFlyer; }
    public void setFrequentFlyer(boolean frequentFlyer) { isFrequentFlyer = frequentFlyer; }

    public int getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(int loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }

    @Override
    public String toString() {
        return "Customer{" +
                "userId='" + getUserId() + '\'' +
                ", name='" + getFirstName() + " " + getLastName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", isFrequentFlyer=" + isFrequentFlyer +
                ", loyaltyPoints=" + loyaltyPoints +
                '}';
    }
} 