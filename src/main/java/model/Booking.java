package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a flight booking with passengers, add-ons, and payment details.
 */
public class Booking {
    private String bookingId;
    private String customerId;
    private String flightId;
    private LocalDateTime bookingDate;
    private BookingStatus status;
    private List<Passenger> passengers;
    private PaymentDetails paymentDetails;
    private List<AddOn> addOns;
    private double basePrice;
    private double totalPrice;
    private boolean isRoundTrip;
    private String returnFlightId; // For round trip bookings
    private LocalDateTime confirmationDate;
    private String bookingReference;
    private LocalDateTime cancellationDate;

    public Booking() {
        this.bookingDate = LocalDateTime.now();
        this.status = BookingStatus.PENDING;
        this.passengers = new ArrayList<>();
        this.addOns = new ArrayList<>();
    }

    public Booking(String bookingId, String customerId, String flightId) {
        this();
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.flightId = flightId;
    }

    /**
     * Calculate total price including base price and add-ons
     */
    public void calculateTotalPrice() {
        double addOnPrice = addOns.stream()
                                  .mapToDouble(AddOn::getPrice)
                                  .sum();
        this.totalPrice = basePrice + addOnPrice;
    }

    /**
     * Add a passenger to this booking
     * @param passenger The passenger to add
     */
    public void addPassenger(Passenger passenger) {
        if (passenger != null) {
            this.passengers.add(passenger);
        }
    }

    /**
     * Add an add-on service to this booking
     * @param addOn The add-on to include
     */
    public void addAddOn(AddOn addOn) {
        if (addOn != null) {
            this.addOns.add(addOn);
            calculateTotalPrice(); // Recalculate total
        }
    }

    /**
     * Confirm the booking
     */
    public void confirmBooking() {
        if (passengers.isEmpty()) {
            throw new IllegalStateException("Cannot confirm booking without passengers");
        }
        if (paymentDetails == null) {
            throw new IllegalStateException("Cannot confirm booking without payment details");
        }
        this.status = BookingStatus.CONFIRMED;
    }

    /**
     * Cancel the booking
     */
    public void cancelBooking() {
        this.status = BookingStatus.CANCELLED;
    }

    // Getters and Setters
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getFlightId() { return flightId; }
    public void setFlightId(String flightId) { this.flightId = flightId; }

    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public List<Passenger> getPassengers() { return new ArrayList<>(passengers); }
    public void setPassengers(List<Passenger> passengers) { this.passengers = passengers; }

    public PaymentDetails getPaymentDetails() { return paymentDetails; }
    public void setPaymentDetails(PaymentDetails paymentDetails) { this.paymentDetails = paymentDetails; }

    public List<AddOn> getAddOns() { return new ArrayList<>(addOns); }
    public void setAddOns(List<AddOn> addOns) { this.addOns = addOns; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { 
        this.basePrice = basePrice; 
        calculateTotalPrice();
    }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    
    public void setTotalAmount(double totalAmount) { this.totalPrice = totalAmount; }

    public boolean isRoundTrip() { return isRoundTrip; }
    public void setRoundTrip(boolean roundTrip) { isRoundTrip = roundTrip; }

    public String getReturnFlightId() { return returnFlightId; }
    public void setReturnFlightId(String returnFlightId) { this.returnFlightId = returnFlightId; }

    public LocalDateTime getConfirmationDate() { return confirmationDate; }
    public void setConfirmationDate(LocalDateTime confirmationDate) { this.confirmationDate = confirmationDate; }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public LocalDateTime getCancellationDate() { return cancellationDate; }
    public void setCancellationDate(LocalDateTime cancellationDate) { this.cancellationDate = cancellationDate; }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId='" + bookingId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", flightId='" + flightId + '\'' +
                ", status=" + status +
                ", passengers=" + passengers.size() +
                ", totalPrice=" + totalPrice +
                '}';
    }
}

 