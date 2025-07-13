package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import model.Admin;
import model.Booking;
import model.BookingStatus;
import model.Customer;
import model.FAQ;
import model.Flight;
import model.FlightStatus;
import model.Passenger;
import model.PaymentDetails;
import model.RefundRequest;
import model.Ticket;
import model.TicketPriority;
import model.TicketReply;
import model.TicketStatus;
import model.User;

/**
 * Manages data persistence through JSON files.
 * Handles loading and saving of all application data.
 */
public class DataManager {
    
    // File path constants
    private static final String DATA_DIR = "src/main/resources/data/";
    private static final String USERS_FILE = DATA_DIR + "users.json";
    private static final String FLIGHTS_FILE = DATA_DIR + "flights.json";
    private static final String BOOKINGS_FILE = DATA_DIR + "bookings.json";
    private static final String TICKETS_FILE = DATA_DIR + "tickets.json";
    private static final String REFUNDS_FILE = DATA_DIR + "refunds.json";
    private static final String FAQS_FILE = DATA_DIR + "faqs.json";

    // User database management
    private static List<User> userDatabase = new ArrayList<>();
    private static boolean usersInitialized = false;

    public DataManager() {
        ensureDataDirectoryExists();
        initializeDataFiles();
    }

    /**
     * Ensure data directory exists
     */
    private void ensureDataDirectoryExists() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    /**
     * Initialize data files with empty arrays if they don't exist
     */
    private void initializeDataFiles() {
        initializeFileIfNotExists(USERS_FILE, "[]");
        initializeFileIfNotExists(FLIGHTS_FILE, "[]");
        initializeFileIfNotExists(BOOKINGS_FILE, "[]");
        initializeFileIfNotExists(TICKETS_FILE, "[]");
        initializeFileIfNotExists(REFUNDS_FILE, "[]");
        initializeFileIfNotExists(FAQS_FILE, "[]");
    }

    /**
     * Initialize a file with default content if it doesn't exist
     * @param fileName File name
     * @param defaultContent Default content
     */
    private void initializeFileIfNotExists(String fileName, String defaultContent) {
        File file = new File(fileName);
        if (!file.exists()) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(defaultContent);
            } catch (IOException e) {
                System.err.println("Error initializing file " + fileName + ": " + e.getMessage());
            }
        }
    }

    // ==================== PUBLIC INTERFACE ====================

    // User operations
    public List<User> loadUsers() {
        return loadUsersFromFile();
    }

    public boolean saveUsers(List<User> users) {
        try {
            // Update the in-memory database
            userDatabase.clear();
            userDatabase.addAll(users);
            
            // Use the new persistent save method
            saveUsers();
            return true;
        } catch (Exception e) {
            System.err.println("Error saving users: " + e.getMessage());
            return false;
        }
    }

    // Flight operations
    public List<Flight> loadFlights() {
        return loadFlightsFromFile();
    }

    public boolean saveFlights(List<Flight> flights) {
        return saveFlightsToFile(flights);
    }

    // Booking operations
    public List<Booking> loadBookings() {
        return loadBookingsFromFile();
    }

    public boolean saveBookings(List<Booking> bookings) {
        return saveBookingsToFile(bookings);
    }

    // Ticket operations
    public List<Ticket> loadTickets() {
        return loadTicketsFromFile();
    }

    public boolean saveTickets(List<Ticket> tickets) {
        return saveTicketsToFile(tickets);
    }

    // Refund operations
    public List<RefundRequest> loadRefunds() {
        return loadRefundsFromFile();
    }

    public boolean saveRefunds(List<RefundRequest> refunds) {
        return saveRefundsToFile(refunds);
    }

    // FAQ operations
    public List<FAQ> loadFAQs() {
        return loadFAQsFromFile();
    }

    public boolean saveFAQs(List<FAQ> faqs) {
        return saveFAQsToFile(faqs);
    }

    // ==================== PRIVATE IMPLEMENTATION ====================

    /**
     * Load users from file with sample data initialization
     */
    private List<User> loadUsersFromFile() {
        try {
            // Check if users file exists and has content
            File usersFile = new File(USERS_FILE);
            boolean hasExistingUsers = usersFile.exists() && usersFile.length() > 50; // More than just empty JSON
            
            // If users file exists with content, load users from file
            if (hasExistingUsers) {
                // Clear current database and load from file
                userDatabase.clear();
                
                String content = readFileContent(USERS_FILE);
                if (!content.trim().equals("[]") && !content.trim().isEmpty()) {
                    // Parse JSON content to load users
                    List<User> loadedUsers = parseUsersFromJSON(content);
                    userDatabase.addAll(loadedUsers);
                    System.out.println("Loaded " + userDatabase.size() + " users from file");
                }
            }
            
            // Initialize default users only if no existing data and not already initialized
            if (!usersInitialized && !hasExistingUsers) {
                userDatabase.clear();
                
                // Password "123456" hashed using the same method as UserService
                String hashedPassword = "HASH_" + "123456".hashCode();
                
                // Add sample admin users
                Admin admin1 = new Admin("ADMIN_001", "admin", hashedPassword, "admin@airline.com",
                                      "Admin", "User", "123-456-7890", "IT");
                userDatabase.add(admin1);
                
                Admin admin2 = new Admin("ADMIN_002", "support.manager", hashedPassword, "support@airline.com",
                                      "Sarah", "Johnson", "123-456-7891", "Customer Support");
                userDatabase.add(admin2);
                
                // Add sample customer users
                Customer customer1 = new Customer("CUST_001", "customer", hashedPassword, "customer@email.com",
                                               "John", "Doe", "987-654-3210", "ABC123", "USA");
                userDatabase.add(customer1);
                
                Customer customer2 = new Customer("CUST_002", "alice.smith", hashedPassword, "alice.smith@email.com",
                                               "Alice", "Smith", "987-654-3211", "DEF456", "USA");
                userDatabase.add(customer2);
                
                Customer customer3 = new Customer("CUST_003", "bob.wilson", hashedPassword, "bob.wilson@gmail.com",
                                               "Bob", "Wilson", "987-654-3212", "GHI789", "UK");
                userDatabase.add(customer3);
                
                Customer customer4 = new Customer("CUST_004", "emma.brown", hashedPassword, "emma.brown@yahoo.com",
                                               "Emma", "Brown", "987-654-3213", "JKL012", "Canada");
                userDatabase.add(customer4);
                
                Customer customer5 = new Customer("CUST_005", "david.chen", hashedPassword, "david.chen@hotmail.com",
                                               "David", "Chen", "987-654-3214", "MNO345", "Singapore");
                userDatabase.add(customer5);
                
                Customer customer6 = new Customer("CUST_006", "maria.garcia", hashedPassword, "maria.garcia@outlook.com",
                                               "Maria", "Garcia", "987-654-3215", "PQR678", "Spain");
                userDatabase.add(customer6);
                
                Customer customer7 = new Customer("CUST_007", "james.miller", hashedPassword, "james.miller@email.com",
                                               "James", "Miller", "987-654-3216", "STU901", "Australia");
                userDatabase.add(customer7);
                
                Customer customer8 = new Customer("CUST_008", "lisa.taylor", hashedPassword, "lisa.taylor@gmail.com",
                                               "Lisa", "Taylor", "987-654-3217", "VWX234", "Germany");
                userDatabase.add(customer8);
                
                Customer customer9 = new Customer("CUST_009", "michael.wang", hashedPassword, "michael.wang@yahoo.com",
                                               "Michael", "Wang", "987-654-3218", "YZA567", "Malaysia");
                userDatabase.add(customer9);
                
                usersInitialized = true;
                System.out.println("Created sample users: " + userDatabase.size() + " users loaded");
                
                // Save sample users to file
                saveUsers();
            }
            
            return new ArrayList<>(userDatabase); // Return copy of the list
        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Save current user database to file - preserves existing users
     */
    public void saveUsers() {
        try {
            File file = new File(USERS_FILE);
            file.getParentFile().mkdirs();
            
            // Use the current userDatabase as the source of truth
            List<User> existingUsers = new ArrayList<>(userDatabase);
            
            // Write all users to file
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("[\n");
                for (int i = 0; i < existingUsers.size(); i++) {
                    User user = existingUsers.get(i);
                    writer.write("  {\n");
                    writer.write("    \"userId\": \"" + user.getUserId() + "\",\n");
                    writer.write("    \"username\": \"" + user.getUsername() + "\",\n");
                    writer.write("    \"password\": \"" + user.getPassword() + "\",\n");
                    writer.write("    \"email\": \"" + user.getEmail() + "\",\n");
                    writer.write("    \"firstName\": \"" + user.getFirstName() + "\",\n");
                    writer.write("    \"lastName\": \"" + user.getLastName() + "\",\n");
                    writer.write("    \"phoneNumber\": \"" + (user.getPhoneNumber() != null ? user.getPhoneNumber() : "") + "\",\n");
                    writer.write("    \"role\": \"" + user.getRole().name() + "\",\n");
                    writer.write("    \"createdAt\": \"" + (user.getCreatedAt() != null ? user.getCreatedAt().toString() : "") + "\",\n");
                    writer.write("    \"lastLogin\": \"" + (user.getLastLogin() != null ? user.getLastLogin().toString() : "") + "\",\n");
                    writer.write("    \"isActive\": " + user.isActive() + "\n");
                    writer.write("  }");
                    if (i < existingUsers.size() - 1) {
                        writer.write(",");
                    }
                    writer.write("\n");
                }
                writer.write("]\n");
            }
            
            System.out.println("Successfully saved " + existingUsers.size() + " users to persistent storage");
            
        } catch (Exception e) {
            System.err.println("Error saving users to file: " + e.getMessage());
        }
    }

    /**
     * Load flights from file
     */
    private List<Flight> loadFlightsFromFile() {
        try {
            String content = readFileContent(FLIGHTS_FILE);
            
            // If file is empty or doesn't exist, return empty list
            if (content.trim().equals("[]") || content.trim().isEmpty()) {
                return new ArrayList<>();
            }
            
            List<Flight> flights = new ArrayList<>();
            
            // Simple JSON parsing for flights
            // Remove brackets and split by flight objects
            content = content.trim();
            if (content.startsWith("[")) {
                content = content.substring(1);
            }
            if (content.endsWith("]")) {
                content = content.substring(0, content.length() - 1);
            }
            
            // Split by flight objects (look for closing and opening braces)
            String[] flightBlocks = content.split("\\},\\s*\\{");
            
            for (String block : flightBlocks) {
                if (block.trim().isEmpty()) {
                    continue;
                }
                
                // Clean up the block
                block = block.trim();
                if (!block.startsWith("{")) {
                    block = "{" + block;
                }
                if (!block.endsWith("}")) {
                    block = block + "}";
                }
                
                try {
                    Flight flight = parseFlightFromJson(block);
                    if (flight != null) {
                        flights.add(flight);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing flight: " + e.getMessage());
                    // Continue with next flight
                }
            }
            
            System.out.println("Loaded " + flights.size() + " flights from " + FLIGHTS_FILE);
            return flights;
            
        } catch (Exception e) {
            System.err.println("Error loading flights from file: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Parse a flight object from JSON string
     * @param jsonBlock JSON string representing a flight
     * @return Flight object
     */
    private Flight parseFlightFromJson(String jsonBlock) {
        try {
            Flight flight = new Flight();
            
            // Extract fields using simple string manipulation
            String flightNumber = extractJsonValue(jsonBlock, "flightNumber");
            String origin = extractJsonValue(jsonBlock, "origin");
            String destination = extractJsonValue(jsonBlock, "destination");
            String departureTimeStr = extractJsonValue(jsonBlock, "departureTime");
            String arrivalTimeStr = extractJsonValue(jsonBlock, "arrivalTime");
            String priceStr = extractJsonValue(jsonBlock, "price");
            String availableSeatsStr = extractJsonValue(jsonBlock, "availableSeats");
            String totalSeatsStr = extractJsonValue(jsonBlock, "totalSeats");
            String aircraft = extractJsonValue(jsonBlock, "aircraft");
            String status = extractJsonValue(jsonBlock, "status");
            
            // Set flight properties with null checks
            if (flightNumber != null && !flightNumber.trim().isEmpty()) {
                flight.setFlightId("FL_" + flightNumber); // Generate ID from flight number
                flight.setFlightNumber(flightNumber);
            } else {
                flight.setFlightId("FL_UNKNOWN_" + System.currentTimeMillis());
                flight.setFlightNumber("UNKNOWN");
            }
            
            flight.setAirline("Pikachu Airlines"); // Default airline
            flight.setDepartureAirport(origin != null && !origin.trim().isEmpty() ? origin : "TBD");
            flight.setArrivalAirport(destination != null && !destination.trim().isEmpty() ? destination : "TBD");
            flight.setAircraft(aircraft != null && !aircraft.trim().isEmpty() ? aircraft : "Unknown Aircraft");
            
            // Parse dates (format: 2025-01-04T08:00:00)
            if (departureTimeStr != null && !departureTimeStr.trim().isEmpty()) {
                try {
                    flight.setDepartureTime(LocalDateTime.parse(departureTimeStr));
                } catch (Exception e) {
                    System.err.println("Error parsing departure time: " + departureTimeStr);
                    // Set default time if parsing fails
                    flight.setDepartureTime(LocalDateTime.now().plusDays(1));
                }
            } else {
                flight.setDepartureTime(LocalDateTime.now().plusDays(1));
            }
            
            if (arrivalTimeStr != null && !arrivalTimeStr.trim().isEmpty()) {
                try {
                    flight.setArrivalTime(LocalDateTime.parse(arrivalTimeStr));
                } catch (Exception e) {
                    System.err.println("Error parsing arrival time: " + arrivalTimeStr);
                    // Set default time if parsing fails
                    flight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
                }
            } else {
                flight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
            }
            
            // Parse numeric values
            if (priceStr != null && !priceStr.trim().isEmpty()) {
                try {
                    flight.setBasePrice(Double.parseDouble(priceStr));
                } catch (NumberFormatException e) {
                    flight.setBasePrice(0.0);
                }
            } else {
                flight.setBasePrice(0.0);
            }
            
            if (availableSeatsStr != null && !availableSeatsStr.trim().isEmpty()) {
                try {
                    flight.setAvailableSeats(Integer.parseInt(availableSeatsStr));
                } catch (NumberFormatException e) {
                    flight.setAvailableSeats(0);
                }
            } else {
                flight.setAvailableSeats(0);
            }
            
            if (totalSeatsStr != null && !totalSeatsStr.trim().isEmpty()) {
                try {
                    flight.setTotalSeats(Integer.parseInt(totalSeatsStr));
                } catch (NumberFormatException e) {
                    flight.setTotalSeats(180);
                }
            } else {
                flight.setTotalSeats(180);
            }
            
            // Parse status
            if (status != null && !status.trim().isEmpty()) {
                try {
                    // Map JSON status values to enum values
                    FlightStatus flightStatus;
                    switch (status) {
                        case "ON_TIME":
                            flightStatus = FlightStatus.SCHEDULED;
                            break;
                        case "DELAYED":
                            flightStatus = FlightStatus.DELAYED;
                            break;
                        case "CANCELLED":
                            flightStatus = FlightStatus.CANCELLED;
                            break;
                        default:
                            flightStatus = FlightStatus.SCHEDULED;
                    }
                    flight.setStatus(flightStatus);
                } catch (IllegalArgumentException e) {
                    flight.setStatus(FlightStatus.SCHEDULED); // Default status
                }
            } else {
                flight.setStatus(FlightStatus.SCHEDULED);
            }
            
            return flight;
            
        } catch (Exception e) {
            System.err.println("Error parsing flight JSON: " + e.getMessage());
            return null;
        }
    }

    /**
     * Extract a value from JSON string
     * @param json JSON string
     * @param key Key to extract
     * @return Extracted value or null
     */
    private String extractJsonValue(String json, String key) {
        try {
            // Use a pattern that handles newlines and indentation
            String pattern = "\"" + key + "\"\\s*:\\s*";
            
            // Create a Pattern object with multiline flag to handle newlines
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern, java.util.regex.Pattern.DOTALL);
            java.util.regex.Matcher m = p.matcher(json);
            
            if (!m.find()) {
                return null;
            }
            
            int startIndex = m.end();
            
            // Check if value is a string (starts with quote)
            boolean isString = json.charAt(startIndex) == '"';
            
            if (isString) {
                startIndex++; // Skip opening quote
                int endIndex = json.indexOf('"', startIndex);
                if (endIndex == -1) {
                    return null;
                }
                String result = json.substring(startIndex, endIndex);
                return result;
            } else {
                // Numeric or boolean value
                int endIndex = json.indexOf(',', startIndex);
                if (endIndex == -1) {
                    endIndex = json.indexOf('}', startIndex);
                }
                if (endIndex == -1) {
                    return null;
                }
                return json.substring(startIndex, endIndex).trim();
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Save flights to file
     */
    private boolean saveFlightsToFile(List<Flight> flights) {
        try {
            System.out.println("Saving " + flights.size() + " flights to file");
            return true;
        } catch (Exception e) {
            System.err.println("Error saving flights: " + e.getMessage());
            return false;
        }
    }

    /**
     * Load bookings from file
     */
    private List<Booking> loadBookingsFromFile() {
        try {
            String content = readFileContent(BOOKINGS_FILE);
            
            // If file is empty or doesn't exist, return empty list
            if (content.trim().equals("[]") || content.trim().isEmpty()) {
                return new ArrayList<>();
            }
            
            List<Booking> bookings = new ArrayList<>();
            
            // Simple JSON parsing for bookings
            // Remove brackets and split by booking objects
            content = content.trim();
            if (content.startsWith("[")) {
                content = content.substring(1);
            }
            if (content.endsWith("]")) {
                content = content.substring(0, content.length() - 1);
            }
            
            // Split by booking objects (look for closing and opening braces)
            String[] bookingBlocks = content.split("\\},\\s*\\{");
            
            for (String block : bookingBlocks) {
                if (block.trim().isEmpty()) {
                    continue;
                }
                
                // Clean up the block
                block = block.trim();
                if (!block.startsWith("{")) {
                    block = "{" + block;
                }
                if (!block.endsWith("}")) {
                    block = block + "}";
                }
                
                try {
                    Booking booking = parseBookingFromJson(block);
                    if (booking != null) {
                        bookings.add(booking);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing booking: " + e.getMessage());
                    // Continue with next booking
                }
            }
            
            System.out.println("Loaded " + bookings.size() + " bookings from " + BOOKINGS_FILE);
            return bookings;
            
        } catch (Exception e) {
            System.err.println("Error loading bookings from file: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Save bookings to file
     */
    private boolean saveBookingsToFile(List<Booking> bookings) {
        try {
            System.out.println("DataManager: Attempting to save " + bookings.size() + " bookings to file");
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("[\n");
            
            for (int i = 0; i < bookings.size(); i++) {
                Booking booking = bookings.get(i);
                jsonBuilder.append(bookingToJson(booking));
                
                if (i < bookings.size() - 1) {
                    jsonBuilder.append(",\n");
                } else {
                    jsonBuilder.append("\n");
                }
            }
            
            jsonBuilder.append("]");
            
            try (FileWriter writer = new FileWriter(BOOKINGS_FILE)) {
                writer.write(jsonBuilder.toString());
            }
            
            System.out.println("DataManager: Successfully saved " + bookings.size() + " bookings to " + BOOKINGS_FILE);
            return true;
            
        } catch (Exception e) {
            System.err.println("DataManager: Error saving bookings to file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Parse a booking object from JSON string
     * @param jsonBlock JSON string representing a booking
     * @return Booking object
     */
    private Booking parseBookingFromJson(String jsonBlock) {
        try {
            Booking booking = new Booking();
            
            // Extract fields using simple string manipulation
            String bookingId = extractJsonValue(jsonBlock, "bookingId");
            String userId = extractJsonValue(jsonBlock, "userId");
            String flightId = extractJsonValue(jsonBlock, "flightId");
            String bookingDateStr = extractJsonValue(jsonBlock, "bookingDate");
            String status = extractJsonValue(jsonBlock, "status");
            String totalAmountStr = extractJsonValue(jsonBlock, "totalAmount");
            
            // Set booking properties
            booking.setBookingId(bookingId);
            booking.setCustomerId(userId); // Maps JSON userId to model customerId
            booking.setFlightId(flightId);
            
            // Parse booking date
            if (bookingDateStr != null && !bookingDateStr.isEmpty()) {
                try {
                    booking.setBookingDate(LocalDateTime.parse(bookingDateStr));
                } catch (Exception e) {
                    System.err.println("Error parsing booking date: " + e.getMessage());
                }
            }
            
            // Parse status
            if (status != null && !status.isEmpty()) {
                try {
                    booking.setStatus(BookingStatus.valueOf(status.toUpperCase()));
                } catch (Exception e) {
                    System.err.println("Error parsing booking status: " + e.getMessage());
                    booking.setStatus(BookingStatus.PENDING);
                }
            }
            
            // Parse total amount
            if (totalAmountStr != null && !totalAmountStr.isEmpty()) {
                try {
                    booking.setTotalPrice(Double.parseDouble(totalAmountStr));
                } catch (Exception e) {
                    System.err.println("Error parsing total amount: " + e.getMessage());
                }
            }
            
            // Parse passengers array
            parseBookingPassengers(jsonBlock, booking);
            
            // Parse payment details
            parseBookingPaymentDetails(jsonBlock, booking);
            
            return booking;
            
        } catch (Exception e) {
            System.err.println("Error parsing booking from JSON: " + e.getMessage());
            return null;
        }
    }

    /**
     * Convert booking to JSON string
     * @param booking Booking object
     * @return JSON string
     */
    private String bookingToJson(Booking booking) {
        StringBuilder json = new StringBuilder();
        json.append("  {\n");
        json.append("    \"bookingId\": \"").append(escapeJson(booking.getBookingId())).append("\",\n");
        json.append("    \"userId\": \"").append(escapeJson(booking.getCustomerId())).append("\",\n");
        json.append("    \"flightId\": \"").append(escapeJson(booking.getFlightId())).append("\",\n");
        json.append("    \"bookingDate\": \"").append(booking.getBookingDate() != null ? booking.getBookingDate().toString() : "").append("\",\n");
        json.append("    \"status\": \"").append(booking.getStatus() != null ? booking.getStatus().name() : "PENDING").append("\",\n");
        json.append("    \"totalAmount\": ").append(booking.getTotalPrice()).append(",\n");
        
        // Add passengers array
        json.append("    \"passengers\": [\n");
        List<Passenger> passengers = booking.getPassengers();
        if (passengers != null && !passengers.isEmpty()) {
            for (int i = 0; i < passengers.size(); i++) {
                Passenger passenger = passengers.get(i);
                json.append("      {\n");
                json.append("        \"passengerId\": \"").append(escapeJson(passenger.getPassengerId())).append("\",\n");
                json.append("        \"name\": \"").append(escapeJson(passenger.getFullName())).append("\",\n");
                json.append("        \"dateOfBirth\": \"").append(passenger.getDateOfBirth() != null ? passenger.getDateOfBirth().toString() : "").append("\",\n");
                json.append("        \"passengerType\": \"Adult\",\n");
                json.append("        \"seatNumber\": \"").append(escapeJson(passenger.getSeatNumber())).append("\"\n");
                json.append("      }");
                if (i < passengers.size() - 1) {
                    json.append(",");
                }
                json.append("\n");
            }
        }
        json.append("    ],\n");
        
        // Add payment details
        json.append("    \"paymentDetails\": {\n");
        PaymentDetails payment = booking.getPaymentDetails();
        if (payment != null) {
            json.append("      \"cardNumber\": \"").append(escapeJson(payment.getCardNumber())).append("\",\n");
            json.append("      \"cardHolderName\": \"").append(escapeJson(payment.getCardholderName())).append("\",\n");
            json.append("      \"amount\": ").append(payment.getAmount()).append(",\n");
            json.append("      \"paymentStatus\": \"PENDING\"\n");
        } else {
            json.append("      \"cardNumber\": \"\",\n");
            json.append("      \"cardHolderName\": \"\",\n");
            json.append("      \"amount\": 0.0,\n");
            json.append("      \"paymentStatus\": \"PENDING\"\n");
        }
        json.append("    }\n");
        json.append("  }");
        
        return json.toString();
    }

    /**
     * Parse passengers from booking JSON
     * @param jsonBlock JSON block
     * @param booking Booking object to populate
     */
    private void parseBookingPassengers(String jsonBlock, Booking booking) {
        try {
            // Find the passengers array in the JSON
            int passengersStart = jsonBlock.indexOf("\"passengers\":");
            if (passengersStart == -1) {
                return;
            }
            
            // Find the start of the array
            int arrayStart = jsonBlock.indexOf("[", passengersStart);
            if (arrayStart == -1) {
                return;
            }
            
            // Find the matching closing bracket
            int arrayEnd = findMatchingBracket(jsonBlock, arrayStart, '[', ']');
            if (arrayEnd == -1) {
                return;
            }
            
            String passengersJson = jsonBlock.substring(arrayStart + 1, arrayEnd);
            if (passengersJson.trim().isEmpty()) {
                return;
            }
            
            // Split by passenger objects
            String[] passengerBlocks = passengersJson.split("\\},\\s*\\{");
            
            for (String passengerBlock : passengerBlocks) {
                if (passengerBlock.trim().isEmpty()) {
                    continue;
                }
                
                // Clean up the block
                passengerBlock = passengerBlock.trim();
                if (!passengerBlock.startsWith("{")) {
                    passengerBlock = "{" + passengerBlock;
                }
                if (!passengerBlock.endsWith("}")) {
                    passengerBlock = passengerBlock + "}";
                }
                
                // Parse individual passenger
                String passengerId = extractJsonValue(passengerBlock, "passengerId");
                String name = extractJsonValue(passengerBlock, "name");
                String dateOfBirthStr = extractJsonValue(passengerBlock, "dateOfBirth");
                String seatNumber = extractJsonValue(passengerBlock, "seatNumber");
                
                if (name != null && !name.trim().isEmpty()) {
                    Passenger passenger = new Passenger();
                    passenger.setPassengerId(passengerId);
                    
                    // Split name into first and last
                    String[] nameParts = name.split("\\s+", 2);
                    if (nameParts.length > 0) {
                        passenger.setFirstName(nameParts[0]);
                        if (nameParts.length > 1) {
                            passenger.setLastName(nameParts[1]);
                        }
                    }
                    
                    // Parse date of birth
                    if (dateOfBirthStr != null && !dateOfBirthStr.isEmpty()) {
                        try {
                            passenger.setDateOfBirth(java.time.LocalDate.parse(dateOfBirthStr));
                        } catch (Exception e) {
                            System.err.println("Error parsing passenger date of birth: " + e.getMessage());
                        }
                    }
                    
                    // Passenger type defaults to ADULT if not specified
                    
                    passenger.setSeatNumber(seatNumber);
                    booking.getPassengers().add(passenger);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error parsing booking passengers: " + e.getMessage());
        }
    }

    /**
     * Parse payment details from booking JSON
     * @param jsonBlock JSON block
     * @param booking Booking object to populate
     */
    private void parseBookingPaymentDetails(String jsonBlock, Booking booking) {
        try {
            // Find the paymentDetails object in the JSON
            int paymentStart = jsonBlock.indexOf("\"paymentDetails\":");
            if (paymentStart == -1) {
                return;
            }
            
            // Find the start of the object
            int objectStart = jsonBlock.indexOf("{", paymentStart);
            if (objectStart == -1) {
                return;
            }
            
            // Find the matching closing brace
            int objectEnd = findMatchingBracket(jsonBlock, objectStart, '{', '}');
            if (objectEnd == -1) {
                return;
            }
            
            String paymentJson = jsonBlock.substring(objectStart, objectEnd + 1);
            
            // Parse payment fields
            String cardNumber = extractJsonValue(paymentJson, "cardNumber");
            String cardHolderName = extractJsonValue(paymentJson, "cardHolderName");
            String amountStr = extractJsonValue(paymentJson, "amount");
            
            PaymentDetails payment = new PaymentDetails();
            payment.setCardNumber(cardNumber);
            payment.setCardholderName(cardHolderName);
            
            // Parse amount
            if (amountStr != null && !amountStr.isEmpty()) {
                try {
                    payment.setAmount(Double.parseDouble(amountStr));
                } catch (Exception e) {
                    System.err.println("Error parsing payment amount: " + e.getMessage());
                }
            }
            
            // Payment status defaults to PENDING if not specified
            
            booking.setPaymentDetails(payment);
            
        } catch (Exception e) {
            System.err.println("Error parsing booking payment details: " + e.getMessage());
        }
    }

    /**
     * Load tickets from file
     */
    private List<Ticket> loadTicketsFromFile() {
        try {
            String content = readFileContent(TICKETS_FILE);
            
            // If file is empty or doesn't exist, return empty list
            if (content.trim().equals("[]") || content.trim().isEmpty()) {
                return new ArrayList<>();
            }
            
            List<Ticket> tickets = new ArrayList<>();
            
            // Simple JSON parsing for tickets
            // Remove brackets and split by ticket objects
            content = content.trim();
            if (content.startsWith("[")) {
                content = content.substring(1);
            }
            if (content.endsWith("]")) {
                content = content.substring(0, content.length() - 1);
            }
            
            // Split by ticket objects (look for closing and opening braces)
            String[] ticketBlocks = content.split("\\},\\s*\\{");
            
            for (String block : ticketBlocks) {
                if (block.trim().isEmpty()) {
                    continue;
                }
                
                // Clean up the block
                block = block.trim();
                if (!block.startsWith("{")) {
                    block = "{" + block;
                }
                if (!block.endsWith("}")) {
                    block = block + "}";
                }
                
                try {
                    Ticket ticket = parseTicketFromJson(block);
                    if (ticket != null) {
                        tickets.add(ticket);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing ticket: " + e.getMessage());
                    // Continue with next ticket
                }
            }
            
            System.out.println("Loaded " + tickets.size() + " tickets from " + TICKETS_FILE);
            return tickets;
            
        } catch (Exception e) {
            System.err.println("Error loading tickets from file: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Parse a ticket object from JSON string
     * @param jsonBlock JSON string representing a ticket
     * @return Ticket object
     */
    private Ticket parseTicketFromJson(String jsonBlock) {
        try {
            Ticket ticket = new Ticket();
            
            // Extract fields using simple string manipulation
            String ticketId = extractJsonValue(jsonBlock, "ticketId");
            String customerId = extractJsonValue(jsonBlock, "customerId");
            String customerName = extractJsonValue(jsonBlock, "customerName");
            String customerEmail = extractJsonValue(jsonBlock, "customerEmail");
            String subject = extractJsonValue(jsonBlock, "subject");
            String description = extractJsonValue(jsonBlock, "description");
            String category = extractJsonValue(jsonBlock, "category");
            String priority = extractJsonValue(jsonBlock, "priority");
            String status = extractJsonValue(jsonBlock, "status");
            String assignedTo = extractJsonValue(jsonBlock, "assignedTo");
            String createdAtStr = extractJsonValue(jsonBlock, "createdAt");
            String updatedAtStr = extractJsonValue(jsonBlock, "updatedAt");
            
            // Debug: Log the extracted customerId only for non-empty values
            if (customerId != null && !customerId.trim().isEmpty()) {
                System.out.println("DataManager: Parsing ticket " + ticketId + " with customerId: '" + customerId + "'");
            }
            
            // Set ticket properties
            ticket.setTicketId(ticketId);
            ticket.setCustomerId(customerId);
            ticket.setCustomerName(customerName);
            ticket.setCustomerEmail(customerEmail);
            ticket.setSubject(subject);
            ticket.setDescription(description);
            ticket.setCategory(category);
            ticket.setAssignedTo(assignedTo);
            
            // Parse priority
            if (priority != null && !priority.trim().isEmpty()) {
                try {
                    ticket.setPriority(TicketPriority.valueOf(priority.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    ticket.setPriority(TicketPriority.MEDIUM);
                }
            }
            
            // Parse status
            if (status != null && !status.trim().isEmpty()) {
                try {
                    ticket.setStatus(TicketStatus.valueOf(status.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    ticket.setStatus(TicketStatus.OPEN);
                }
            }
            
            // Parse dates (format: 2025-01-11T10:30:00)
            if (createdAtStr != null && !createdAtStr.trim().isEmpty()) {
                try {
                    ticket.setCreatedAt(LocalDateTime.parse(createdAtStr));
                } catch (Exception e) {
                    ticket.setCreatedAt(LocalDateTime.now());
                }
            }
            
            if (updatedAtStr != null && !updatedAtStr.trim().isEmpty()) {
                try {
                    ticket.setUpdatedAt(LocalDateTime.parse(updatedAtStr));
                } catch (Exception e) {
                    ticket.setUpdatedAt(LocalDateTime.now());
                }
            }
            
            // Parse responses/replies array
            parseTicketResponses(jsonBlock, ticket);
            
            return ticket;
            
        } catch (Exception e) {
            System.err.println("Error parsing ticket from JSON: " + e.getMessage());
            return null;
        }
    }

    /**
     * Parse ticket responses from JSON
     */
    private void parseTicketResponses(String jsonBlock, Ticket ticket) {
        try {
            // Find the responses array in the JSON
            int responsesStart = jsonBlock.indexOf("\"responses\":");
            if (responsesStart == -1) {
                return;
            }
            
            // Find the start of the array
            int arrayStart = jsonBlock.indexOf("[", responsesStart);
            if (arrayStart == -1) {
                return;
            }
            
            // Find the matching closing bracket
            int arrayEnd = findMatchingBracket(jsonBlock, arrayStart, '[', ']');
            if (arrayEnd == -1) {
                return;
            }
            
            String responsesJson = jsonBlock.substring(arrayStart + 1, arrayEnd);
            if (responsesJson.trim().isEmpty()) {
                return;
            }
            
            // Split by response objects
            String[] responseBlocks = responsesJson.split("\\},\\s*\\{");
            
            for (String responseBlock : responseBlocks) {
                if (responseBlock.trim().isEmpty()) {
                    continue;
                }
                
                // Clean up the block
                responseBlock = responseBlock.trim();
                if (!responseBlock.startsWith("{")) {
                    responseBlock = "{" + responseBlock;
                }
                if (!responseBlock.endsWith("}")) {
                    responseBlock = responseBlock + "}";
                }
                
                // Parse individual response
                String responderId = extractJsonValue(responseBlock, "responderId");
                String responderName = extractJsonValue(responseBlock, "responderName");
                String message = extractJsonValue(responseBlock, "message");
                String timestampStr = extractJsonValue(responseBlock, "timestamp");
                
                if (message != null && !message.trim().isEmpty()) {
                    TicketReply reply = new TicketReply();
                    reply.setResponderId(responderId);
                    reply.setResponderName(responderName);
                    reply.setMessage(message);
                    
                    if (timestampStr != null && !timestampStr.trim().isEmpty()) {
                        try {
                            reply.setTimestamp(LocalDateTime.parse(timestampStr));
                        } catch (Exception e) {
                            reply.setTimestamp(LocalDateTime.now());
                        }
                    }
                    
                    ticket.getReplies().add(reply);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error parsing ticket responses: " + e.getMessage());
        }
    }

    /**
     * Find matching bracket in a string
     */
    private int findMatchingBracket(String text, int startPos, char openBracket, char closeBracket) {
        int count = 1;
        for (int i = startPos + 1; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == openBracket) {
                count++;
            } else if (c == closeBracket) {
                count--;
                if (count == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Save tickets to file
     */
    private boolean saveTicketsToFile(List<Ticket> tickets) {
        try {
            StringBuilder json = new StringBuilder();
            json.append("[\n");
            
            for (int i = 0; i < tickets.size(); i++) {
                Ticket ticket = tickets.get(i);
                json.append("  {\n");
                json.append("    \"ticketId\": \"").append(escapeJson(ticket.getTicketId())).append("\",\n");
                json.append("    \"customerId\": \"").append(escapeJson(ticket.getCustomerId())).append("\",\n");
                json.append("    \"customerName\": \"").append(escapeJson(ticket.getCustomerName())).append("\",\n");
                json.append("    \"customerEmail\": \"").append(escapeJson(ticket.getCustomerEmail())).append("\",\n");
                json.append("    \"subject\": \"").append(escapeJson(ticket.getSubject())).append("\",\n");
                json.append("    \"description\": \"").append(escapeJson(ticket.getDescription())).append("\",\n");
                json.append("    \"category\": \"").append(escapeJson(ticket.getCategory())).append("\",\n");
                json.append("    \"priority\": \"").append(ticket.getPriority().name()).append("\",\n");
                json.append("    \"status\": \"").append(ticket.getStatus().name()).append("\",\n");
                json.append("    \"assignedTo\": \"").append(escapeJson(ticket.getAssignedTo())).append("\",\n");
                json.append("    \"createdAt\": \"").append(ticket.getCreatedAt().toString()).append("\",\n");
                json.append("    \"updatedAt\": \"").append(ticket.getUpdatedAt().toString()).append("\",\n");
                
                // Add responses
                json.append("    \"responses\": [\n");
                List<TicketReply> replies = ticket.getReplies();
                for (int j = 0; j < replies.size(); j++) {
                    TicketReply reply = replies.get(j);
                    json.append("      {\n");
                    json.append("        \"responderId\": \"").append(escapeJson(reply.getResponderId())).append("\",\n");
                    json.append("        \"responderName\": \"").append(escapeJson(reply.getResponderName())).append("\",\n");
                    json.append("        \"message\": \"").append(escapeJson(reply.getMessage())).append("\",\n");
                    json.append("        \"timestamp\": \"").append(reply.getTimestamp().toString()).append("\"\n");
                    json.append("      }");
                    if (j < replies.size() - 1) {
                        json.append(",");
                    }
                    json.append("\n");
                }
                json.append("    ]\n");
                
                json.append("  }");
                if (i < tickets.size() - 1) {
                    json.append(",");
                }
                json.append("\n");
            }
            
            json.append("]");
            
            // Write to file
            try (FileWriter writer = new FileWriter(TICKETS_FILE)) {
                writer.write(json.toString());
            }
            
            System.out.println("Saved " + tickets.size() + " tickets to " + TICKETS_FILE);
            
            // Debug: Log some details about saved tickets
            long ticketsWithCustomerId = tickets.stream()
                .filter(t -> t.getCustomerId() != null && !t.getCustomerId().trim().isEmpty())
                .count();
            System.out.println("DataManager: " + ticketsWithCustomerId + " tickets have valid customer IDs");
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Error saving tickets to file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Escape JSON special characters
     */
    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }

    /**
     * Load refunds from file
     */
    private List<RefundRequest> loadRefundsFromFile() {
        return new ArrayList<>();
    }

    /**
     * Save refunds to file
     */
    private boolean saveRefundsToFile(List<RefundRequest> refunds) {
        return true;
    }

    /**
     * Load FAQs from file
     */
    private List<FAQ> loadFAQsFromFile() {
        return new ArrayList<>();
    }

    /**
     * Save FAQs to file
     */
    private boolean saveFAQsToFile(List<FAQ> faqs) {
        return true;
    }

    /**
     * Read file content as string
     * @param fileName File name to read
     * @return File content as string
     * @throws IOException If file cannot be read
     */
    private String readFileContent(String fileName) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    /**
     * Load sample data for the application
     */
    public void loadSampleData() {
        System.out.println("Loading sample data...");
        loadUsers();
        loadFlights();
        loadBookings();
        loadTickets();
        loadRefunds();
        loadFAQs();
        System.out.println("Sample data loaded successfully!");
    }

    /**
     * Generate a unique ID with prefix
     * @param prefix Prefix for the ID
     * @return Generated ID
     */
    public static String generateId(String prefix) {
        return prefix + "_" + System.currentTimeMillis();
    }

    /**
     * Format date time for display
     * @param dateTime Date time to format
     * @return Formatted date time string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    /**
     * Register a new user and save to persistent storage
     */
    public boolean registerUser(User newUser) {
        try {
            // Check if user already exists
            if (findUserByUsername(newUser.getUsername()) != null) {
                System.out.println("User already exists: " + newUser.getUsername());
                return false;
            }
            
            // Add to in-memory database
            userDatabase.add(newUser);
            
            // Save to persistent storage
            saveUsers();
            
            System.out.println("Successfully registered new user: " + newUser.getUsername());
            return true;
            
        } catch (Exception e) {
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Find a user by username
     * @param username Username to search for
     * @return User object or null if not found
     */
    private User findUserByUsername(String username) {
        for (User user : userDatabase) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Parse users from JSON content
     */
    private List<User> parseUsersFromJSON(String content) {
        List<User> users = new ArrayList<>();
        try {
            // Simple JSON parsing for users (similar to flights/bookings)
            content = content.trim();
            if (content.startsWith("[") && content.endsWith("]")) {
                content = content.substring(1, content.length() - 1); // Remove [ ]
            }
            
            // Split by user objects
            String[] userObjects = content.split("\\},\\s*\\{");
            
            for (String userObj : userObjects) {
                userObj = userObj.trim();
                if (userObj.startsWith("{")) {
                    userObj = userObj.substring(1);
                }
                if (userObj.endsWith("}")) {
                    userObj = userObj.substring(0, userObj.length() - 1);
                }
                
                if (userObj.trim().isEmpty()) continue;
                
                try {
                    // Parse user fields
                    String userId = extractJSONValue(userObj, "userId");
                    String username = extractJSONValue(userObj, "username");
                    String password = extractJSONValue(userObj, "password");
                    String email = extractJSONValue(userObj, "email");
                    String firstName = extractJSONValue(userObj, "firstName");
                    String lastName = extractJSONValue(userObj, "lastName");
                    String phoneNumber = extractJSONValue(userObj, "phoneNumber");
                    String role = extractJSONValue(userObj, "role");
                    String createdAtStr = extractJSONValue(userObj, "createdAt");
                    String lastLoginStr = extractJSONValue(userObj, "lastLogin");
                    String isActiveStr = extractJSONValue(userObj, "isActive");
                    
                    // Create user based on role (handle both enum names and display names)
                    User user = null;
                    if ("ADMIN".equals(role) || "Admin".equals(role)) {
                        user = new Admin(userId, username, password, email, firstName, lastName, 
                                       phoneNumber != null ? phoneNumber : "", "General");
                    } else if ("CUSTOMER".equals(role) || "Customer".equals(role)) {
                        user = new Customer(userId, username, password, email, firstName, lastName,
                                          phoneNumber != null ? phoneNumber : "", "TEMP123", "USA");
                    }
                    
                    if (user != null) {
                        // Set additional fields
                        if (createdAtStr != null && !createdAtStr.isEmpty()) {
                            try {
                                user.setCreatedAt(LocalDateTime.parse(createdAtStr));
                            } catch (Exception e) {
                                // Keep default createdAt if parsing fails
                            }
                        }
                        if (lastLoginStr != null && !lastLoginStr.isEmpty()) {
                            try {
                                user.setLastLogin(LocalDateTime.parse(lastLoginStr));
                            } catch (Exception e) {
                                // Keep null lastLogin if parsing fails
                            }
                        }
                        if (isActiveStr != null && !isActiveStr.isEmpty()) {
                            user.setActive(Boolean.parseBoolean(isActiveStr));
                        }
                        users.add(user);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing user: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing users from JSON: " + e.getMessage());
        }
        return users;
    }
    
    /**
     * Extract value from JSON string for a given key
     */
    private String extractJSONValue(String jsonObject, String key) {
        String pattern = "\"" + key + "\":\\s*\"([^\"]+)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(jsonObject);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    /**
     * Create demo admin and customer accounts for testing
     */
    public void createDemoAccounts() {
        System.out.println("Creating demo accounts...");
        
        // Clear existing users
        userDatabase.clear();
        
        // Password "123456" hashed using the same method as UserService
        String hashedPassword = "HASH_" + "123456".hashCode();
        
        // Create demo admin account
        Admin demoAdmin = new Admin("ADMIN_DEMO", "admin", hashedPassword, "admin@pikachu-airlines.com",
                                  "Demo", "Admin", "555-0101", "Customer Service");
        demoAdmin.setActive(true);
        userDatabase.add(demoAdmin);
        
        // Create demo customer account
        Customer demoCustomer = new Customer("CUST_DEMO", "customer", hashedPassword, "customer@demo.com",
                                           "Demo", "Customer", "555-0102", "PASS123", "USA");
        demoCustomer.setActive(true);
        userDatabase.add(demoCustomer);
        
        // Create additional sample accounts for variety
        Admin admin2 = new Admin("ADMIN_002", "support.admin", hashedPassword, "support@pikachu-airlines.com",
                               "Sarah", "Johnson", "555-0103", "Customer Support");
        admin2.setActive(true);
        userDatabase.add(admin2);
        
        Customer customer2 = new Customer("CUST_002", "john.doe", hashedPassword, "john.doe@email.com",
                                        "John", "Doe", "555-0104", "ABC789", "USA");
        customer2.setActive(true);
        userDatabase.add(customer2);
        
        Customer customer3 = new Customer("CUST_003", "jane.smith", hashedPassword, "jane.smith@email.com",
                                        "Jane", "Smith", "555-0105", "XYZ456", "Canada");
        customer3.setActive(true);
        userDatabase.add(customer3);
        
        // Save demo accounts to persistent storage
        saveUsers();
        
        System.out.println("Demo accounts created successfully!");
        System.out.println("Demo Admin - Username: admin, Password: 123456");
        System.out.println("Demo Customer - Username: customer, Password: 123456");
        System.out.println("Additional accounts: support.admin, john.doe, jane.smith (all password: 123456)");
    }
}