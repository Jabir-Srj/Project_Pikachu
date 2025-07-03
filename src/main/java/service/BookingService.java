package service;

import model.Booking;
import model.Flight;
import model.Customer;
import model.Passenger;
import model.PaymentDetails;
import model.BookingStatus;
import dao.BookingDAO;
import dao.FlightDAO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for booking-related operations including booking creation, confirmation, and cancellation.
 */
public class BookingService {
    private BookingDAO bookingDAO;
    private FlightDAO flightDAO;
    private FlightService flightService;

    public BookingService() {
        this.bookingDAO = new BookingDAO();
        this.flightDAO = new FlightDAO();
        this.flightService = new FlightService();
    }

    /**
     * Create a new booking
     * @param customer Customer making the booking
     * @param flight Flight to book
     * @param passengers List of passengers
     * @param paymentDetails Payment information
     * @return Booking if successful, null otherwise
     */
    public Booking createBooking(Customer customer, Flight flight, List<Passenger> passengers, PaymentDetails paymentDetails) {
        try {
            // Validate booking data
            if (!validateBookingData(customer, flight, passengers, paymentDetails)) {
                return null;
            }

            // Check seat availability
            if (!flightService.checkSeatAvailability(flight.getFlightNumber(), passengers.size())) {
                System.err.println("Not enough seats available");
                return null;
            }

            // Reserve seats
            if (!flightService.reserveSeats(flight.getFlightNumber(), passengers.size())) {
                System.err.println("Failed to reserve seats");
                return null;
            }

            // Create booking
            Booking booking = new Booking();
            booking.setBookingId(generateBookingId());
            booking.setCustomerId(customer.getUserId());
            booking.setFlightId(flight.getFlightId());
            booking.setPassengers(passengers);
            booking.setPaymentDetails(paymentDetails);
            booking.setBookingDate(LocalDateTime.now());
            booking.setStatus(BookingStatus.PENDING);
            booking.setTotalPrice(calculateTotalAmount(flight, passengers));

            // Save booking
            if (bookingDAO.save(booking)) {
                return booking;
            } else {
                // Release seats if booking failed
                flightService.releaseSeats(flight.getFlightNumber(), passengers.size());
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error creating booking: " + e.getMessage());
            return null;
        }
    }

    /**
     * Confirm a booking (complete payment)
     * @param bookingId Booking ID to confirm
     * @return true if confirmation successful
     */
    public boolean confirmBooking(String bookingId) {
        try {
            Optional<Booking> bookingOpt = bookingDAO.findById(bookingId);
            if (bookingOpt.isEmpty()) {
                return false;
            }

            Booking booking = bookingOpt.get();
            if (booking.getStatus() != BookingStatus.PENDING) {
                return false;
            }

            // Process payment (simplified)
            if (processPayment(booking.getPaymentDetails())) {
                booking.setStatus(BookingStatus.CONFIRMED);
                booking.setConfirmationDate(LocalDateTime.now());
                booking.setBookingReference(generateBookingReference());
                return bookingDAO.update(booking);
            }

            return false;
        } catch (Exception e) {
            System.err.println("Error confirming booking: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cancel a booking
     * @param bookingId Booking ID to cancel
     * @param customerId Customer ID (for security)
     * @return true if cancellation successful
     */
    public boolean cancelBooking(String bookingId, String customerId) {
        try {
            Optional<Booking> bookingOpt = bookingDAO.findById(bookingId);
            if (bookingOpt.isEmpty()) {
                return false;
            }

            Booking booking = bookingOpt.get();
            
            // Verify ownership
            if (!booking.getCustomerId().equals(customerId)) {
                return false;
            }

            // Check if cancellation is allowed
            if (booking.getStatus() == BookingStatus.CANCELLED) {
                return false;
            }

            // Release seats
            Optional<Flight> flightOpt = flightDAO.findById(booking.getFlightId());
            if (flightOpt.isPresent()) {
                flightService.releaseSeats(flightOpt.get().getFlightNumber(), booking.getPassengers().size());
            }

            // Update booking status
            booking.setStatus(BookingStatus.CANCELLED);
            booking.setCancellationDate(LocalDateTime.now());
            
            return bookingDAO.update(booking);
        } catch (Exception e) {
            System.err.println("Error cancelling booking: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get customer bookings
     * @param customerId Customer ID
     * @return List of customer bookings
     */
    public List<Booking> getCustomerBookings(String customerId) {
        return bookingDAO.findByCustomerId(customerId);
    }

    /**
     * Get booking by ID
     * @param bookingId Booking ID
     * @return Booking if found
     */
    public Optional<Booking> getBookingById(String bookingId) {
        return bookingDAO.findById(bookingId);
    }

    /**
     * Get booking by reference
     * @param bookingReference Booking reference
     * @return Booking if found
     */
    public Optional<Booking> getBookingByReference(String bookingReference) {
        return bookingDAO.findByReference(bookingReference);
    }

    /**
     * Get all bookings (for admin use)
     * @return List of all bookings
     */
    public List<Booking> getAllBookings() {
        return bookingDAO.findAll();
    }

    /**
     * Modify booking (for airline management)
     * @param bookingId Booking ID
     * @param newFlight New flight
     * @return true if modification successful
     */
    public boolean modifyBooking(String bookingId, Flight newFlight) {
        try {
            Optional<Booking> bookingOpt = bookingDAO.findById(bookingId);
            if (bookingOpt.isEmpty()) {
                return false;
            }

            Booking booking = bookingOpt.get();
            
            // Release seats from old flight
            Optional<Flight> oldFlightOpt = flightDAO.findById(booking.getFlightId());
            if (oldFlightOpt.isPresent()) {
                flightService.releaseSeats(oldFlightOpt.get().getFlightNumber(), booking.getPassengers().size());
            }

            // Reserve seats on new flight
            if (!flightService.reserveSeats(newFlight.getFlightNumber(), booking.getPassengers().size())) {
                // Re-reserve seats on old flight if new reservation fails
                if (oldFlightOpt.isPresent()) {
                    flightService.reserveSeats(oldFlightOpt.get().getFlightNumber(), booking.getPassengers().size());
                }
                return false;
            }

            // Update booking
            booking.setFlightId(newFlight.getFlightId());
            booking.setTotalAmount(calculateTotalAmount(newFlight, booking.getPassengers()));
            
            return bookingDAO.update(booking);
        } catch (Exception e) {
            System.err.println("Error modifying booking: " + e.getMessage());
            return false;
        }
    }

    /**
     * Validate booking data
     * @param customer Customer
     * @param flight Flight
     * @param passengers Passenger list
     * @param paymentDetails Payment details
     * @return true if valid
     */
    private boolean validateBookingData(Customer customer, Flight flight, List<Passenger> passengers, PaymentDetails paymentDetails) {
        if (customer == null || flight == null || passengers == null || paymentDetails == null) {
            return false;
        }
        if (passengers.isEmpty()) {
            return false;
        }
        return passengers.size() <= 9; // Maximum 9 passengers per booking
    }

    /**
     * Calculate total amount for booking
     * @param flight Flight
     * @param passengers Passenger list
     * @return Total amount
     */
    private double calculateTotalAmount(Flight flight, List<Passenger> passengers) {
        return flight.getBasePrice() * passengers.size();
    }

    /**
     * Process payment (simplified implementation)
     * @param paymentDetails Payment details
     * @return true if payment successful
     */
    private boolean processPayment(PaymentDetails paymentDetails) {
        // Simplified payment processing
        // In real implementation, integrate with payment gateway
        return paymentDetails != null && paymentDetails.getAmount() > 0;
    }

    /**
     * Generate unique booking ID
     * @return Generated booking ID
     */
    private String generateBookingId() {
        return "BKG_" + System.currentTimeMillis();
    }

    /**
     * Generate booking reference
     * @return Generated booking reference
     */
    private String generateBookingReference() {
        return "PK" + System.currentTimeMillis() % 1000000;
    }
} 