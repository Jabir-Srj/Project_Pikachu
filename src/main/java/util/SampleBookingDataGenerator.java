package util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.Booking;
import model.BookingStatus;
import model.Passenger;
import model.PaymentDetails;

/**
 * Utility class to generate comprehensive sample booking data for testing
 * the booking management system with various scenarios.
 */
public class SampleBookingDataGenerator {
    
    private static final Random random = new Random();
    
    // Available flight numbers from existing data
    private static final String[] FLIGHT_NUMBERS = {
        "PKA101", "PKA201", "PKA301"
    };
    
    // Customer IDs from existing users
    private static final String[] CUSTOMER_IDS = {
        "CUST_001", "CUST_002", "CUST_003", "CUST_004", "CUST_005",
        "CUST_006", "CUST_007", "CUST_008", "CUST_009"
    };
    
    // Sample passenger data for realistic bookings
    private static final String[][] PASSENGER_DATA = {
        {"James", "Anderson", "1988-03-15", "P12345678", "USA"},
        {"Maria", "Garcia", "1992-07-22", "P87654321", "Spain"},
        {"Hiroshi", "Tanaka", "1985-11-10", "P11223344", "Japan"},
        {"Sophie", "Martin", "1990-05-18", "P55667788", "France"},
        {"Chen", "Wei", "1987-09-03", "P99887766", "China"},
        {"Emma", "Thompson", "1993-01-28", "P44556677", "UK"},
        {"Carlos", "Rodriguez", "1984-12-07", "P33445566", "Mexico"},
        {"Yuki", "Nakamura", "1991-06-14", "P22334455", "Japan"},
        {"Lucas", "Silva", "1989-04-25", "P66778899", "Brazil"},
        {"Anna", "Kowalski", "1986-10-11", "P77889900", "Poland"},
        {"Ryan", "O'Connor", "1994-08-30", "P11224433", "Ireland"},
        {"Priya", "Sharma", "1988-02-17", "P55443322", "India"},
        {"Alexander", "Petrov", "1983-12-01", "P88776655", "Russia"},
        {"Isabella", "Romano", "1995-09-09", "P99665544", "Italy"},
        {"Ahmed", "Hassan", "1987-03-26", "P77553311", "Egypt"}
    };
    
    // Sample seat numbers (realistic aircraft seating)
    private static final String[] SEAT_NUMBERS = {
        "1A", "1B", "1C", "2A", "2B", "2C", "3A", "3B", "3C",
        "4A", "4B", "4C", "5A", "5B", "5C", "6A", "6B", "6C",
        "7A", "7B", "7C", "8A", "8B", "8C", "9A", "9B", "9C",
        "10A", "10B", "10C", "11A", "11B", "11C", "12A", "12B", "12C",
        "13A", "13B", "13C", "14A", "14B", "14C", "15A", "15B", "15C",
        "16A", "16B", "16C", "17A", "17B", "17C", "18A", "18B", "18C",
        "19A", "19B", "19C", "20A", "20B", "20C", "21A", "21B", "21C",
        "22A", "22B", "22C", "23A", "23B", "23C", "24A", "24B", "24C"
    };
    
    // Card number endings for realistic payment data
    private static final String[] CARD_ENDINGS = {
        "1234", "5678", "9012", "3456", "7890", "2468", "1357", "8642",
        "4321", "8765", "2109", "6543", "0987", "8024", "7531", "4862"
    };
    
    /**
     * Generate comprehensive sample booking data with various scenarios
     * @return List of sample bookings with different statuses, passenger counts, and payment states
     */
    public static List<Booking> generateSampleBookings() {
        List<Booking> sampleBookings = new ArrayList<>();
        
        // Starting from booking 11 to continue from existing data
        int bookingCounter = 11;
        int passengerCounter = 11;
        
        // Generate 15 diverse booking scenarios
        for (int i = 0; i < 15; i++) {
            String bookingId = String.format("PKA2400%02d", bookingCounter);
            String customerId = CUSTOMER_IDS[random.nextInt(CUSTOMER_IDS.length)];
            String flightNumber = FLIGHT_NUMBERS[random.nextInt(FLIGHT_NUMBERS.length)];
            
            // Create booking with random date within last 30 days
            LocalDateTime bookingDate = LocalDateTime.now().minusDays(random.nextInt(30));
            
            // Determine number of passengers (1-4, with bias toward 1-2)
            int passengerCount = random.nextInt(100) < 70 ? (random.nextInt(2) + 1) : (random.nextInt(2) + 3);
            
            // Create passengers
            List<Passenger> passengers = new ArrayList<>();
            double totalAmount = 0.0;
            
            for (int j = 0; j < passengerCount; j++) {
                String[] passengerInfo = PASSENGER_DATA[random.nextInt(PASSENGER_DATA.length)];
                String passengerId = String.format("PASS%03d", passengerCounter++);
                String seatNumber = SEAT_NUMBERS[random.nextInt(SEAT_NUMBERS.length)];
                
                // Create passenger using correct constructor
                Passenger passenger = new Passenger(
                    passengerInfo[0], // firstName
                    passengerInfo[1], // lastName
                    LocalDate.parse(passengerInfo[2]), // dateOfBirth - parse to LocalDate
                    passengerInfo[3], // passportNumber
                    passengerInfo[4]  // nationality
                );
                
                passenger.setPassengerId(passengerId);
                passenger.setSeatNumber(seatNumber);
                // Note: Type is set to ADULT by default in constructor
                
                passengers.add(passenger);
                
                // Calculate price based on flight
                double basePrice = getBasePriceForFlight(flightNumber);
                totalAmount += basePrice * (1.0 + random.nextDouble() * 0.3); // Add some variation for taxes/fees
            }
            
            // Create payment details
            PaymentDetails paymentDetails = createSamplePaymentDetails(
                passengers.get(0).getFirstName() + " " + passengers.get(0).getLastName(),
                totalAmount,
                bookingDate
            );
            
            // Determine booking status with realistic distribution
            BookingStatus status = determineBookingStatus();
            
            // Note: PaymentStatus will be set during payment processing in the existing system
            
            // Create booking
            Booking booking = new Booking();
            booking.setBookingId(bookingId);
            booking.setCustomerId(customerId);
            booking.setFlightId(flightNumber);
            booking.setBookingDate(bookingDate);
            booking.setStatus(status);
            booking.setPassengers(passengers);
            booking.setPaymentDetails(paymentDetails);
            booking.setTotalPrice(Math.round(totalAmount * 100.0) / 100.0); // Round to 2 decimal places
            
            sampleBookings.add(booking);
            bookingCounter++;
        }
        
        return sampleBookings;
    }
    
    /**
     * Get base price for a flight
     */
    private static double getBasePriceForFlight(String flightNumber) {
        switch (flightNumber) {
            case "PKA101": return 299.99; // Tokyo to Seoul
            case "PKA201": return 89.99;  // Singapore to Kuala Lumpur
            case "PKA301": return 199.99; // Bangkok to Manila
            default: return 150.00;
        }
    }
    
    /**
     * Create realistic payment details
     */
    private static PaymentDetails createSamplePaymentDetails(String cardholderName, double amount, LocalDateTime paymentDate) {
        String cardEnding = CARD_ENDINGS[random.nextInt(CARD_ENDINGS.length)];
        String maskedCardNumber = "**** **** **** " + cardEnding;
        
        // Random expiry date (6 months to 3 years in future)
        int expiryMonth = random.nextInt(12) + 1;
        int expiryYear = LocalDateTime.now().getYear() + random.nextInt(3) + 1;
        LocalDate expiryDate = LocalDate.of(expiryYear, expiryMonth, 1);
        
        PaymentDetails paymentDetails = new PaymentDetails(
            maskedCardNumber,
            cardholderName,
            expiryDate,
            "123" // CVV will be encrypted
        );
        
        paymentDetails.setAmount(amount);
        paymentDetails.setBillingAddress(generateRandomAddress());
        
        return paymentDetails;
    }
    
    /**
     * Generate a random billing address
     */
    private static String generateRandomAddress() {
        String[] addresses = {
            "123 Main Street, New York, NY 10001",
            "456 Oak Avenue, Los Angeles, CA 90210",
            "789 Pine Road, Chicago, IL 60601",
            "321 Elm Drive, Houston, TX 77001",
            "654 Maple Lane, Phoenix, AZ 85001",
            "987 Cedar Court, Philadelphia, PA 19101",
            "147 Birch Boulevard, San Antonio, TX 78201",
            "258 Willow Way, San Diego, CA 92101",
            "369 Spruce Street, Dallas, TX 75201",
            "741 Poplar Place, San Jose, CA 95101"
        };
        return addresses[random.nextInt(addresses.length)];
    }
    
    /**
     * Determine booking status with realistic distribution
     */
    private static BookingStatus determineBookingStatus() {
        int rand = random.nextInt(100);
        if (rand < 75) {
            return BookingStatus.CONFIRMED; // 75% confirmed
        } else if (rand < 85) {
            return BookingStatus.COMPLETED; // 10% completed
        } else if (rand < 95) {
            return BookingStatus.PENDING;   // 10% pending
        } else {
            return BookingStatus.CANCELLED; // 5% cancelled
        }
    }
    
    /**
     * Generate family booking (2-4 passengers with related names)
     */
    public static Booking generateFamilyBooking() {
        String bookingId = "PKA24026"; // Family booking
        String customerId = CUSTOMER_IDS[random.nextInt(CUSTOMER_IDS.length)];
        String flightNumber = FLIGHT_NUMBERS[random.nextInt(FLIGHT_NUMBERS.length)];
        LocalDateTime bookingDate = LocalDateTime.now().minusDays(random.nextInt(15));
        
        // Create family members
        List<Passenger> family = new ArrayList<>();
        
        // Adult 1 (Primary)
        Passenger adult1 = new Passenger("Michael", "Johnson", 
            LocalDate.of(1985, 6, 15), "P12345001", "USA");
        adult1.setPassengerId("PASS026");
        adult1.setSeatNumber("12A");
        family.add(adult1);
        
        // Adult 2 (Spouse)
        Passenger adult2 = new Passenger("Sarah", "Johnson", 
            LocalDate.of(1987, 3, 22), "P12345002", "USA");
        adult2.setPassengerId("PASS027");
        adult2.setSeatNumber("12B");
        family.add(adult2);
        
        // Child
        Passenger child = new Passenger("Emma", "Johnson", 
            LocalDate.of(2015, 8, 10), "P12345003", "USA");
        child.setPassengerId("PASS028");
        child.setSeatNumber("12C");
        family.add(child);
        
        double basePrice = getBasePriceForFlight(flightNumber);
        double totalAmount = (basePrice * 2) + (basePrice * 0.5); // Child discount
        
        PaymentDetails paymentDetails = createSamplePaymentDetails("Michael Johnson", totalAmount, bookingDate);
        
        Booking familyBooking = new Booking();
        familyBooking.setBookingId(bookingId);
        familyBooking.setCustomerId(customerId);
        familyBooking.setFlightId(flightNumber);
        familyBooking.setBookingDate(bookingDate);
        familyBooking.setStatus(BookingStatus.CONFIRMED);
        familyBooking.setPassengers(family);
        familyBooking.setPaymentDetails(paymentDetails);
        familyBooking.setTotalPrice(Math.round(totalAmount * 100.0) / 100.0);
        
        return familyBooking;
    }
}
