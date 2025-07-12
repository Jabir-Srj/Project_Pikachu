package controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import model.Booking;
import model.BookingStatus;
import model.Flight;
import model.User;
import service.BookingService;
import service.FlightService;
import util.NavigationManager;
import util.ServiceLocator;
import util.SampleBookingDataGenerator;
import dao.BookingDAO;

/**
 * Controller for BookingOverview.fxml
 * Manages booking overview and admin booking management
 */
public class BookingOverviewController implements Initializable {
    
    // Header Controls
    @FXML private Button backButton;
    @FXML private Button newBookingButton;
    @FXML private Button exportButton;
    
    // Search and Filter Controls
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilterComboBox;
    @FXML private ComboBox<String> dateRangeComboBox;
    @FXML private Button searchButton;
    @FXML private Button clearButton;
    @FXML private Button refreshButton;
    
    // Statistics Labels
    @FXML private Text totalBookingsLabel;
    @FXML private Text confirmedBookingsLabel;
    @FXML private Text pendingBookingsLabel;
    @FXML private Text cancelledBookingsLabel;
    
    // Table and Columns
    @FXML private TableView<Booking> bookingsTableView;
    @FXML private TableColumn<Booking, String> bookingIdColumn;
    @FXML private TableColumn<Booking, String> customerNameColumn;
    @FXML private TableColumn<Booking, String> flightNumberColumn;
    @FXML private TableColumn<Booking, String> routeColumn;
    @FXML private TableColumn<Booking, String> departureDateColumn;
    @FXML private TableColumn<Booking, String> passengersColumn;
    @FXML private TableColumn<Booking, String> statusColumn;
    
    // Services
    private BookingService bookingService;
    private FlightService flightService;
    private User currentUser;
    private NavigationManager navigationManager;
    
    // Data
    private ObservableList<Booking> allBookings = FXCollections.observableArrayList();
    private ObservableList<Booking> filteredBookings = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        navigationManager = NavigationManager.getInstance();
        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        bookingService = serviceLocator.getBookingService();
        flightService = serviceLocator.getFlightService();
        
        // Get current user
        currentUser = (User) navigationManager.getSharedData("currentUser");
        
        setupTableColumns();
        setupEventHandlers();
        loadBookings();
        setupFilterOptions();
        
        System.out.println("BookingOverviewController: Initialization complete");
    }
    
    /**
     * Setup table columns
     */
    private void setupTableColumns() {
        bookingIdColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getBookingId()));
            
        customerNameColumn.setCellValueFactory(cellData -> {
            // Try to get customer name from the booking
            String customerId = cellData.getValue().getCustomerId();
            return new SimpleStringProperty(customerId != null ? customerId : "Unknown");
        });
        
        flightNumberColumn.setCellValueFactory(cellData -> {
            String flightId = cellData.getValue().getFlightId();
            // Use getFlightDetails instead of getFlightById
            Optional<Flight> flightOpt = flightService.getFlightDetails(flightId);
            return new SimpleStringProperty(flightOpt.map(Flight::getFlightNumber).orElse(flightId));
        });
        
        routeColumn.setCellValueFactory(cellData -> {
            String flightId = cellData.getValue().getFlightId();
            // Use getFlightDetails instead of getFlightById and getDepartureAirport/getArrivalAirport
            Optional<Flight> flightOpt = flightService.getFlightDetails(flightId);
            if (flightOpt.isPresent()) {
                Flight flight = flightOpt.get();
                return new SimpleStringProperty(flight.getDepartureAirport() + " → " + flight.getArrivalAirport());
            }
            return new SimpleStringProperty("Unknown Route");
        });
        
        departureDateColumn.setCellValueFactory(cellData -> {
            LocalDateTime bookingDate = cellData.getValue().getBookingDate();
            if (bookingDate != null) {
                return new SimpleStringProperty(bookingDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
            return new SimpleStringProperty("N/A");
        });
        
        passengersColumn.setCellValueFactory(cellData -> {
            int passengerCount = cellData.getValue().getPassengers() != null ? 
                cellData.getValue().getPassengers().size() : 0;
            return new SimpleStringProperty(String.valueOf(passengerCount));
        });
        
        statusColumn.setCellValueFactory(cellData -> {
            BookingStatus status = cellData.getValue().getStatus();
            return new SimpleStringProperty(status != null ? status.getDisplayName() : "Unknown");
        });
        
        // Set table data
        bookingsTableView.setItems(filteredBookings);
    }
    
    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        if (backButton != null) {
            backButton.setOnAction(_ -> handleBackToDashboard());
        }
        
        if (newBookingButton != null) {
            newBookingButton.setOnAction(_ -> handleNewBooking());
        }
        
        if (exportButton != null) {
            exportButton.setOnAction(_ -> handleExportBookings());
        }
        
        if (searchButton != null) {
            searchButton.setOnAction(_ -> handleSearch());
        }
        
        if (clearButton != null) {
            clearButton.setOnAction(_ -> handleClearFilters());
        }
        
        if (refreshButton != null) {
            refreshButton.setOnAction(_ -> handleRefresh());
        }
        
        // Double-click to view booking details
        if (bookingsTableView != null) {
            bookingsTableView.setRowFactory(tv -> {
                var row = new javafx.scene.control.TableRow<Booking>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && !row.isEmpty()) {
                        handleViewBookingDetails(row.getItem());
                    }
                });
                return row;
            });
        }
    }
    
    /**
     * Setup filter options
     */
    private void setupFilterOptions() {
        if (statusFilterComboBox != null) {
            statusFilterComboBox.getItems().addAll(
                "All Status",
                "CONFIRMED",
                "PENDING", 
                "CANCELLED"
            );
            statusFilterComboBox.setValue("All Status");
        }
        
        if (dateRangeComboBox != null) {
            dateRangeComboBox.getItems().addAll(
                "Last 30 days",
                "Last 7 days",
                "Today",
                "This Month",
                "All Time"
            );
            dateRangeComboBox.setValue("Last 30 days");
        }
    }
    
    /**
     * Load bookings from service
     */
    private void loadBookings() {
        try {
            List<Booking> bookings = bookingService.getAllBookings();
            allBookings.clear();
            allBookings.addAll(bookings);
            
            // Apply current filters
            applyFilters();
            updateStatistics();
            
            System.out.println("Loaded " + bookings.size() + " bookings");
        } catch (Exception e) {
            System.err.println("Error loading bookings: " + e.getMessage());
            showAlert("Error", "Failed to load bookings: " + e.getMessage());
        }
    }
    
    /**
     * Apply current filters to booking list
     */
    private void applyFilters() {
        List<Booking> filtered = allBookings.stream()
            .filter(this::matchesSearchCriteria)
            .filter(this::matchesStatusFilter)
            .filter(this::matchesDateFilter)
            .collect(Collectors.toList());
            
        filteredBookings.clear();
        filteredBookings.addAll(filtered);
    }
    
    /**
     * Check if booking matches search criteria
     */
    private boolean matchesSearchCriteria(Booking booking) {
        if (searchField == null || searchField.getText().trim().isEmpty()) {
            return true;
        }
        
        String searchText = searchField.getText().toLowerCase().trim();
        
        // Search in booking ID
        if (booking.getBookingId() != null && 
            booking.getBookingId().toLowerCase().contains(searchText)) {
            return true;
        }
        
        // Search in customer ID (would be better with actual customer name)
        if (booking.getCustomerId() != null && 
            booking.getCustomerId().toLowerCase().contains(searchText)) {
            return true;
        }
        
        // Search in flight ID
        if (booking.getFlightId() != null && 
            booking.getFlightId().toLowerCase().contains(searchText)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Check if booking matches status filter
     */
    private boolean matchesStatusFilter(Booking booking) {
        if (statusFilterComboBox == null || 
            "All Status".equals(statusFilterComboBox.getValue())) {
            return true;
        }
        
        String selectedStatus = statusFilterComboBox.getValue();
        if (selectedStatus == null) {
            return true; // If no status is selected, show all bookings
        }
        
        BookingStatus bookingStatus = booking.getStatus();
        
        return bookingStatus != null && 
               selectedStatus.equals(bookingStatus.name());
    }
    
    /**
     * Check if booking matches date filter
     */
    private boolean matchesDateFilter(Booking booking) {
        if (dateRangeComboBox == null || 
            "All Time".equals(dateRangeComboBox.getValue())) {
            return true;
        }
        
        LocalDateTime bookingDate = booking.getBookingDate();
        if (bookingDate == null) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        String selectedRange = dateRangeComboBox.getValue();
        
        if (selectedRange == null) {
            return true; // If no range is selected, show all bookings
        }
        
        return switch (selectedRange) {
            case "Today" -> bookingDate.toLocalDate().equals(now.toLocalDate());
            case "Last 7 days" -> bookingDate.isAfter(now.minusDays(7));
            case "Last 30 days" -> bookingDate.isAfter(now.minusDays(30));
            case "This Month" -> bookingDate.getMonth() == now.getMonth() && 
                               bookingDate.getYear() == now.getYear();
            default -> true;
        };
    }
    
    /**
     * Update statistics labels
     */
    private void updateStatistics() {
        if (totalBookingsLabel != null) {
            totalBookingsLabel.setText(String.valueOf(allBookings.size()));
        }
        
        if (confirmedBookingsLabel != null) {
            long confirmed = allBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .count();
            confirmedBookingsLabel.setText(String.valueOf(confirmed));
        }
        
        if (pendingBookingsLabel != null) {
            long pending = allBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.PENDING)
                .count();
            pendingBookingsLabel.setText(String.valueOf(pending));
        }
        
        if (cancelledBookingsLabel != null) {
            long cancelled = allBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CANCELLED)
                .count();
            cancelledBookingsLabel.setText(String.valueOf(cancelled));
        }
    }
    
    /**
     * Handle back to dashboard
     */
    private void handleBackToDashboard() {
        navigationManager.showAdminDashboard();
    }
    
    /**
     * Handle new booking
     */
    private void handleNewBooking() {
        // Navigate to flight search for new booking
        navigationManager.navigateTo("FlightSearch.fxml");
    }
    
    /**
     * Handle export bookings
     */
    private void handleExportBookings() {
        showAlert("Info", "Export feature coming soon!");
    }
    
    /**
     * Handle search
     */
    private void handleSearch() {
        applyFilters();
    }
    
    /**
     * Handle clear filters
     */
    private void handleClearFilters() {
        if (searchField != null) {
            searchField.clear();
        }
        if (statusFilterComboBox != null) {
            statusFilterComboBox.setValue("All Status");
        }
        if (dateRangeComboBox != null) {
            dateRangeComboBox.setValue("Last 30 days");
        }
        applyFilters();
    }
    
    /**
     * Handle refresh
     */
    private void handleRefresh() {
        loadBookings();
    }
    
    /**
     * Load sample booking data for testing purposes
     * This method generates and saves comprehensive sample booking data
     */
    public void loadSampleBookingData() {
        try {
            // Create BookingDAO instance to save bookings directly
            BookingDAO bookingDAO = new BookingDAO();
            
            // Generate sample bookings
            List<Booking> sampleBookings = SampleBookingDataGenerator.generateSampleBookings();
            
            // Generate family booking
            Booking familyBooking = SampleBookingDataGenerator.generateFamilyBooking();
            sampleBookings.add(familyBooking);
            
            // Save all sample bookings
            int successCount = 0;
            for (Booking booking : sampleBookings) {
                if (bookingDAO.save(booking)) {
                    successCount++;
                }
            }
            
            // Refresh the table to show new data
            loadBookings();
            
            // Show success message
            showAlert("Sample Data Loaded", 
                String.format("Successfully loaded %d out of %d sample bookings.\n\n" +
                "The sample data includes:\n" +
                "• Various booking statuses (Confirmed, Pending, Cancelled, Completed)\n" +
                "• Single and multiple passenger bookings\n" +
                "• Different flight routes (Tokyo-Seoul, Singapore-KL, Bangkok-Manila)\n" +
                "• Realistic passenger and payment information\n" +
                "• One family booking with adults and child\n" +
                "• Bookings spread across the last 30 days", 
                successCount, sampleBookings.size()));
                
        } catch (Exception e) {
            showAlert("Error Loading Sample Data", 
                "Failed to load sample booking data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handle view booking details
     */
    private void handleViewBookingDetails(Booking booking) {
        if (booking != null) {
            navigationManager.setSharedData("selectedBooking", booking);
            navigationManager.navigateTo("BookingDetails.fxml");
        }
    }
    
    /**
     * Show alert dialog
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
