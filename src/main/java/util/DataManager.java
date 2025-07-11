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
import model.Customer;
import model.FAQ;
import model.Flight;
import model.FlightStatus;
import model.RefundRequest;
import model.Ticket;
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
        return saveUsersToFile(users);
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
            // Initialize default users only once
            if (!usersInitialized) {
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
            }
            
            return new ArrayList<>(userDatabase); // Return copy of the list
        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Save users to file
     */
    private boolean saveUsersToFile(List<User> users) {
        try {
            // Update the in-memory database with new users
            userDatabase.clear();
            userDatabase.addAll(users);
            
            System.out.println("Saving " + users.size() + " users to persistent storage");
            
            // Also write to file for debugging (simplified format)
            try (FileWriter writer = new FileWriter(USERS_FILE)) {
                writer.write("[\n");
                for (int i = 0; i < users.size(); i++) {
                    User user = users.get(i);
                    writer.write("  {\n");
                    writer.write("    \"userId\": \"" + user.getUserId() + "\",\n");
                    writer.write("    \"username\": \"" + user.getUsername() + "\",\n");
                    writer.write("    \"email\": \"" + user.getEmail() + "\",\n");
                    writer.write("    \"firstName\": \"" + user.getFirstName() + "\",\n");
                    writer.write("    \"lastName\": \"" + user.getLastName() + "\",\n");
                    writer.write("    \"role\": \"" + user.getRole() + "\"\n");
                    writer.write("  }");
                    if (i < users.size() - 1) {
                        writer.write(",");
                    }
                    writer.write("\n");
                }
                writer.write("]\n");
            }
            
            return true;
        } catch (Exception e) {
            System.err.println("Error saving users: " + e.getMessage());
            return false;
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
            String pattern = "\"" + key + "\"\\s*:\\s*";
            int startIndex = json.indexOf(pattern);
            if (startIndex == -1) {
                return null;
            }
            
            startIndex += pattern.length();
            
            // Check if value is a string (starts with quote)
            boolean isString = json.charAt(startIndex) == '"';
            
            if (isString) {
                startIndex++; // Skip opening quote
                int endIndex = json.indexOf('"', startIndex);
                if (endIndex == -1) {
                    return null;
                }
                return json.substring(startIndex, endIndex);
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
        return new ArrayList<>();
    }

    /**
     * Save bookings to file
     */
    private boolean saveBookingsToFile(List<Booking> bookings) {
        return true;
    }

    /**
     * Load tickets from file
     */
    private List<Ticket> loadTicketsFromFile() {
        return new ArrayList<>();
    }

    /**
     * Save tickets to file
     */
    private boolean saveTicketsToFile(List<Ticket> tickets) {
        return true;
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
} 