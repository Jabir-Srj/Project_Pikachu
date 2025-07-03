package dao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import model.Booking;
import util.DataManager;

/**
 * Data Access Object for Booking operations
 */
public class BookingDAO {
    private DataManager dataManager;

    public BookingDAO() {
        this.dataManager = new DataManager();
    }

    /**
     * Save a new booking
     * @param booking Booking to save
     * @return true if save successful
     */
    public boolean save(Booking booking) {
        try {
            List<Booking> bookings = dataManager.loadBookings();
            bookings.add(booking);
            return dataManager.saveBookings(bookings);
        } catch (Exception e) {
            System.err.println("Error saving booking: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update an existing booking
     * @param booking Booking to update
     * @return true if update successful
     */
    public boolean update(Booking booking) {
        try {
            List<Booking> bookings = dataManager.loadBookings();
            for (int i = 0; i < bookings.size(); i++) {
                if (bookings.get(i).getBookingId().equals(booking.getBookingId())) {
                    bookings.set(i, booking);
                    return dataManager.saveBookings(bookings);
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error updating booking: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete a booking
     * @param bookingId Booking ID to delete
     * @return true if delete successful
     */
    public boolean delete(String bookingId) {
        try {
            List<Booking> bookings = dataManager.loadBookings();
            bookings.removeIf(booking -> booking.getBookingId().equals(bookingId));
            return dataManager.saveBookings(bookings);
        } catch (Exception e) {
            System.err.println("Error deleting booking: " + e.getMessage());
            return false;
        }
    }

    /**
     * Find booking by ID
     * @param bookingId Booking ID
     * @return Booking if found
     */
    public Optional<Booking> findById(String bookingId) {
        try {
            List<Booking> bookings = dataManager.loadBookings();
            return bookings.stream()
                .filter(booking -> booking.getBookingId().equals(bookingId))
                .findFirst();
        } catch (Exception e) {
            System.err.println("Error finding booking by ID: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Find bookings by customer ID
     * @param customerId Customer ID
     * @return List of customer bookings
     */
    public List<Booking> findByCustomerId(String customerId) {
        try {
            List<Booking> bookings = dataManager.loadBookings();
            return bookings.stream()
                .filter(booking -> booking.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error finding bookings by customer ID: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Find booking by reference
     * @param bookingReference Booking reference
     * @return Booking if found
     */
    public Optional<Booking> findByReference(String bookingReference) {
        try {
            List<Booking> bookings = dataManager.loadBookings();
            return bookings.stream()
                .filter(booking -> booking.getBookingId().contains(bookingReference))
                .findFirst();
        } catch (Exception e) {
            System.err.println("Error finding booking by reference: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Find all bookings
     * @return List of all bookings
     */
    public List<Booking> findAll() {
        try {
            return dataManager.loadBookings();
        } catch (Exception e) {
            System.err.println("Error loading all bookings: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Find bookings by flight ID
     * @param flightId Flight ID
     * @return List of bookings for the flight
     */
    public List<Booking> findByFlightId(String flightId) {
        try {
            List<Booking> bookings = dataManager.loadBookings();
            return bookings.stream()
                .filter(booking -> booking.getFlightId().equals(flightId))
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error finding bookings by flight ID: " + e.getMessage());
            return List.of();
        }
    }
} 