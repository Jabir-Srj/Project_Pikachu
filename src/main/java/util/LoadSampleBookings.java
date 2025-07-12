package util;

import java.util.List;

import dao.BookingDAO;
import model.Booking;

/**
 * Test class to generate and load sample booking data
 * Run this to populate the booking system with sample data for testing
 */
public class LoadSampleBookings {
    
    public static void main(String[] args) {
        System.out.println("Loading sample booking data...");
        
        try {
            // Create BookingDAO instance
            BookingDAO bookingDAO = new BookingDAO();
            
            // Generate sample bookings
            List<Booking> sampleBookings = SampleBookingDataGenerator.generateSampleBookings();
            
            // Generate family booking
            Booking familyBooking = SampleBookingDataGenerator.generateFamilyBooking();
            sampleBookings.add(familyBooking);
            
            // Save all sample bookings
            int successCount = 0;
            int totalCount = sampleBookings.size();
            
            System.out.println("Generated " + totalCount + " sample bookings. Saving to database...");
            
            for (Booking booking : sampleBookings) {
                if (bookingDAO.save(booking)) {
                    successCount++;
                    System.out.println("✓ Saved booking " + booking.getBookingId() + 
                        " for customer " + booking.getCustomerId() + 
                        " on flight " + booking.getFlightId());
                } else {
                    System.err.println("✗ Failed to save booking " + booking.getBookingId());
                }
            }
            
            System.out.println("\nSample booking data loading completed!");
            System.out.println("Successfully loaded " + successCount + " out of " + totalCount + " sample bookings.");
            System.out.println("\nThe sample data includes:");
            System.out.println("• Various booking statuses (Confirmed, Pending, Cancelled, Completed)");
            System.out.println("• Single and multiple passenger bookings");
            System.out.println("• Different flight routes (Tokyo-Seoul, Singapore-KL, Bangkok-Manila)");
            System.out.println("• Realistic passenger and payment information");
            System.out.println("• One family booking with adults and child");
            System.out.println("• Bookings spread across the last 30 days");
            
            if (successCount == totalCount) {
                System.out.println("\n🎉 All sample bookings loaded successfully!");
            } else {
                System.out.println("\n⚠️  Some bookings failed to load. Check the error messages above.");
            }
            
        } catch (Exception e) {
            System.err.println("Error loading sample booking data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
