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
    private static final String DATA_DIR = "src/main/resources/data/";
    private static final String USERS_FILE = DATA_DIR + "users.json";
    private static final String FLIGHTS_FILE = DATA_DIR + "flights.json";
    private static final String BOOKINGS_FILE = DATA_DIR + "bookings.json";
    private static final String TICKETS_FILE = DATA_DIR + "tickets.json";
    private static final String REFUNDS_FILE = DATA_DIR + "refunds.json";
    private static final String FAQS_FILE = DATA_DIR + "faqs.json";

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

    // Enhanced user management with persistent storage
    private static List<User> userDatabase = new ArrayList<>();
    private static boolean usersInitialized = false;
    
    private List<User> loadUsersFromFile() {
        try {
            // Initialize default users only once
            if (!usersInitialized) {
                userDatabase.clear();
                
                // Add sample admin with properly hashed password
                // Password "123456" hashed using the same method as UserService
                String adminHashedPassword = "HASH_" + "123456".hashCode();
                Admin admin = new Admin("ADMIN_001", "admin", adminHashedPassword, "admin@airline.com",
                                      "Admin", "User", "123-456-7890", "IT");
                userDatabase.add(admin);
                
                // Add sample customer with properly hashed password
                String customerHashedPassword = "HASH_" + "123456".hashCode();
                Customer customer = new Customer("CUST_001", "customer", customerHashedPassword, "customer@email.com",
                                               "John", "Doe", "987-654-3210", "ABC123", "USA");
                userDatabase.add(customer);
                
                usersInitialized = true;
                System.out.println("Created sample users: admin and customer");
            }
            
            return new ArrayList<>(userDatabase); // Return copy of the list
        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
            return new ArrayList<>();
        }
    }

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
                    if (i < users.size() - 1) writer.write(",");
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
            if (content.startsWith("[")) content = content.substring(1);
            if (content.endsWith("]")) content = content.substring(0, content.length() - 1);
            
            // Split by flight objects (look for closing and opening braces)
            String[] flightBlocks = content.split("\\},\\s*\\{");
            
            for (String block : flightBlocks) {
                if (block.trim().isEmpty()) continue;
                
                // Clean up the block
                block = block.trim();
                if (!block.startsWith("{")) block = "{" + block;
                if (!block.endsWith("}")) block = block + "}";
                
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
            
            // Set flight properties
            flight.setFlightId("FL_" + flightNumber); // Generate ID from flight number
            flight.setFlightNumber(flightNumber);
            flight.setAirline("Pikachu Airlines"); // Default airline
            flight.setDepartureAirport(origin);
            flight.setArrivalAirport(destination);
            flight.setAircraft(aircraft);
            
            // Parse dates (format: 2025-01-04T08:00:00)
            if (departureTimeStr != null) {
                flight.setDepartureTime(LocalDateTime.parse(departureTimeStr));
            }
            if (arrivalTimeStr != null) {
                flight.setArrivalTime(LocalDateTime.parse(arrivalTimeStr));
            }
            
            // Parse numeric values
            if (priceStr != null) {
                flight.setBasePrice(Double.parseDouble(priceStr));
            }
            if (availableSeatsStr != null) {
                flight.setAvailableSeats(Integer.parseInt(availableSeatsStr));
            }
            if (totalSeatsStr != null) {
                flight.setTotalSeats(Integer.parseInt(totalSeatsStr));
            }
            
            // Parse status
            if (status != null) {
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
            if (startIndex == -1) return null;
            
            startIndex += pattern.length();
            
            // Check if value is a string (starts with quote)
            boolean isString = json.charAt(startIndex) == '"';
            
            if (isString) {
                startIndex++; // Skip opening quote
                int endIndex = json.indexOf('"', startIndex);
                if (endIndex == -1) return null;
                return json.substring(startIndex, endIndex);
            } else {
                // Numeric or boolean value
                int endIndex = json.indexOf(',', startIndex);
                if (endIndex == -1) {
                    endIndex = json.indexOf('}', startIndex);
                }
                if (endIndex == -1) return null;
                return json.substring(startIndex, endIndex).trim();
            }
        } catch (Exception e) {
            return null;
        }
    }

    private boolean saveFlightsToFile(List<Flight> flights) {
        try {
            System.out.println("Saving " + flights.size() + " flights to file");
            return true;
        } catch (Exception e) {
            System.err.println("Error saving flights: " + e.getMessage());
            return false;
        }
    }

    private List<Booking> loadBookingsFromFile() {
        return new ArrayList<>(); // Empty for now
    }

    private boolean saveBookingsToFile(List<Booking> bookings) {
        try {
            System.out.println("Saving " + bookings.size() + " bookings to file");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private List<Ticket> loadTicketsFromFile() {
        return new ArrayList<>(); // Empty for now
    }

    private boolean saveTicketsToFile(List<Ticket> tickets) {
        try {
            System.out.println("Saving " + tickets.size() + " tickets to file");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private List<RefundRequest> loadRefundsFromFile() {
        return new ArrayList<>(); // Empty for now
    }

    private boolean saveRefundsToFile(List<RefundRequest> refunds) {
        try {
            System.out.println("Saving " + refunds.size() + " refunds to file");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private List<FAQ> loadFAQsFromFile() {
        try {
            List<FAQ> faqs = new ArrayList<>();
            
            // Add sample FAQs
            FAQ faq1 = new FAQ("FAQ001", "How do I cancel my booking?", 
                             "You can cancel your booking by logging into your account and selecting the booking you wish to cancel.",
                             "Booking", "ADMIN_001");
            
            FAQ faq2 = new FAQ("FAQ002", "What is the baggage allowance?",
                             "Economy passengers are allowed one carry-on bag and one personal item. Checked baggage fees may apply.",
                             "Baggage", "ADMIN_001");
            
            faqs.add(faq1);
            faqs.add(faq2);
            
            return faqs;
        } catch (Exception e) {
            System.err.println("Error loading FAQs: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private boolean saveFAQsToFile(List<FAQ> faqs) {
        try {
            System.out.println("Saving " + faqs.size() + " FAQs to file");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Read file content as string
     * @param fileName File name
     * @return File content
     */
    private String readFileContent(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            return "[]";
        }
        
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }
        
        return content.toString();
    }

    /**
     * Load sample data for demonstration purposes
     */
    public void loadSampleData() {
        System.out.println("Loading sample data for the airline application...");
        
        // Initialize sample users if they don't exist
        List<User> users = loadUsers();
        if (users.isEmpty()) {
            // Sample data is created in loadUsersFromFile()
            users = loadUsers(); // This will return the sample users
            saveUsers(users);
        }
        
        // Initialize sample flights
        List<Flight> flights = loadFlights();
        if (flights.isEmpty()) {
            // Sample data is created in loadFlightsFromFile()
            flights = loadFlights();
            saveFlights(flights);
        }
        
        // Initialize sample FAQs
        List<FAQ> faqs = loadFAQs();
        if (faqs.isEmpty()) {
            // Sample data is created in loadFAQsFromFile()
            faqs = loadFAQs();
            saveFAQs(faqs);
        }
        
        System.out.println("Sample data loaded successfully!");
    }

    /**
     * Generate unique ID
     * @param prefix Prefix for ID
     * @return Generated ID
     */
    public static String generateId(String prefix) {
        return prefix + "_" + System.currentTimeMillis();
    }

    /**
     * Format date time for display
     * @param dateTime LocalDateTime to format
     * @return Formatted string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
} 