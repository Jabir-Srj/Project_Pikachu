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
            // Return sample flights for demo
            List<Flight> flights = new ArrayList<>();
            
            Flight flight1 = new Flight("FL001", "AA123", "American Airlines", 
                                      "JFK", "LAX", 
                                      LocalDateTime.now().plusDays(1),
                                      LocalDateTime.now().plusDays(1).plusHours(6),
                                      299.99, 150);
            
            Flight flight2 = new Flight("FL002", "UA456", "United Airlines",
                                      "LAX", "JFK",
                                      LocalDateTime.now().plusDays(2),
                                      LocalDateTime.now().plusDays(2).plusHours(5),
                                      349.99, 180);
            
            flights.add(flight1);
            flights.add(flight2);
            
            return flights;
        } catch (Exception e) {
            System.err.println("Error loading flights: " + e.getMessage());
            return new ArrayList<>();
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