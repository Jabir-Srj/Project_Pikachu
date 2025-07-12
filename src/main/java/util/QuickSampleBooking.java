package util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import dao.BookingDAO;
import model.Booking;
import model.BookingStatus;
import model.Passenger;
import model.PaymentDetails;

/**
 * Simple utility to add individual sample bookings for testing
 */
public class QuickSampleBooking {
    
    /**
     * Add a single sample booking quickly for testing
     */
    public static boolean addQuickSampleBooking() {
        try {
            BookingDAO bookingDAO = new BookingDAO();
            
            // Create a simple sample booking
            Booking sampleBooking = new Booking();
            sampleBooking.setBookingId("PKA24099"); // Use a unique ID
            sampleBooking.setCustomerId("CUST_001"); // Existing customer
            sampleBooking.setFlightId("PKA101"); // Existing flight
            sampleBooking.setBookingDate(LocalDateTime.now().minusDays(5));
            sampleBooking.setStatus(BookingStatus.CONFIRMED);
            
            // Create a passenger
            List<Passenger> passengers = new ArrayList<>();
            Passenger passenger = new Passenger("Test", "Passenger", 
                LocalDate.of(1990, 5, 15), "P99999999", "USA");
            passenger.setPassengerId("PASS099");
            passenger.setSeatNumber("15A");
            passengers.add(passenger);
            sampleBooking.setPassengers(passengers);
            
            // Create payment details
            PaymentDetails paymentDetails = new PaymentDetails(
                "**** **** **** 9999",
                "Test Passenger",
                LocalDate.of(2027, 12, 1),
                "123"
            );
            paymentDetails.setAmount(350.00);
            paymentDetails.setBillingAddress("123 Test Street, Test City, TC 12345");
            sampleBooking.setPaymentDetails(paymentDetails);
            sampleBooking.setTotalPrice(350.00);
            
            // Save the booking
            boolean success = bookingDAO.save(sampleBooking);
            
            if (success) {
                System.out.println("✓ Sample booking added successfully: " + sampleBooking.getBookingId());
            } else {
                System.err.println("✗ Failed to add sample booking");
            }
            
            return success;
            
        } catch (Exception e) {
            System.err.println("Error adding sample booking: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
