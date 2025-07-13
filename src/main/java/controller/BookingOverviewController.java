package controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import model.UserRole;
import service.BookingService;
import service.FlightService;
import util.NavigationManager;
import util.ServiceLocator;

/**
 * Controller for BookingOverview.fxml
 * Manages booking overview and admin booking management
 */
public class BookingOverviewController implements Initializable {
    
    // Header Controls
    @FXML private Button backButton;
    @FXML private Button newBookingButton;
    @FXML private Button exportButton;
    @FXML private Text titleText;

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
    
    // Cache for flight data to avoid repeated lookups
    private Map<String, Flight> flightCache = new HashMap<>();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        navigationManager = NavigationManager.getInstance();
        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        bookingService = serviceLocator.getBookingService();
        flightService = serviceLocator.getFlightService();
        
        // Get current user
        currentUser = (User) navigationManager.getSharedData("currentUser");
        
        // Set dynamic title based on user role
        updateTitleBasedOnUserRole();
        
        setupTableColumns();
        setupEventHandlers();
        loadBookings();
        setupFilterOptions();
        
        System.out.println("BookingOverviewController: Initialization complete");
    }
    
    /**
     * Update title based on current user role
     */
    private void updateTitleBasedOnUserRole() {
        if (currentUser != null && titleText != null) {
            if (currentUser.getRole() == UserRole.ADMIN || currentUser.getRole() == UserRole.AIRLINE_MANAGEMENT) {
                titleText.setText("⚡ All Bookings (Admin View)");
            } else {
                titleText.setText("⚡ My Bookings");
            }
        }
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
            if (flightId == null) return new SimpleStringProperty("N/A");
            
            // Use cached flight data to avoid repeated lookups
            Flight flight = flightCache.computeIfAbsent(flightId, 
                id -> flightService.getFlightDetails(id).orElse(null));
            
            return new SimpleStringProperty(flight != null ? flight.getFlightNumber() : flightId);
        });
        
        routeColumn.setCellValueFactory(cellData -> {
            String flightId = cellData.getValue().getFlightId();
            if (flightId == null) return new SimpleStringProperty("Unknown Route");
            
            // Use cached flight data to avoid repeated lookups
            Flight flight = flightCache.computeIfAbsent(flightId, 
                id -> flightService.getFlightDetails(id).orElse(null));
            
            if (flight != null) {
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
            bookingsTableView.setRowFactory(_ -> {
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
            List<Booking> bookings;
            
            if (currentUser != null) {
                // Admin and airline management users see all bookings for management purposes
                if (currentUser.getRole() == UserRole.ADMIN || currentUser.getRole() == UserRole.AIRLINE_MANAGEMENT) {
                    bookings = bookingService.getAllBookings();
                    System.out.println("Loaded all " + bookings.size() + " bookings for " + currentUser.getRole().getDisplayName() + ": " + currentUser.getUsername());
                } else {
                    // Regular customers see only their own bookings
                    bookings = bookingService.getCustomerBookings(currentUser.getUserId());
                    System.out.println("Loaded " + bookings.size() + " personal bookings for customer: " + currentUser.getUsername() + " (ID: " + currentUser.getUserId() + ")");
                }
            } else {
                // Fallback to all bookings if no current user (shouldn't happen in normal flow)
                bookings = bookingService.getAllBookings();
                System.out.println("No current user found, loaded all " + bookings.size() + " bookings");
            }
            
            allBookings.clear();
            allBookings.addAll(bookings);
            
            // Apply current filters
            applyFilters();
            updateStatistics();
            
            System.out.println("After filtering: " + filteredBookings.size() + " bookings visible");
            
        } catch (Exception e) {
            System.err.println("Error loading bookings: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to load bookings: " + e.getMessage());
        }
    }
    
    /**
     * Apply current filters to booking list
     */
    private void applyFilters() {
        System.out.println("Applying filters to " + allBookings.size() + " bookings");
        List<Booking> filtered = allBookings.stream()
            .filter(this::matchesSearchCriteria)
            .filter(this::matchesStatusFilter)
            .filter(this::matchesDateFilter)
            .collect(Collectors.toList());
            
        System.out.println("Filtered result: " + filtered.size() + " bookings");
        filteredBookings.clear();
        filteredBookings.addAll(filtered);
        System.out.println("filteredBookings now contains: " + filteredBookings.size() + " items");
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
        // Navigate based on user role
        if (currentUser != null && currentUser.getRole() != null) {
            if (currentUser.getRole().name().equals("ADMIN")) {
                navigationManager.showAdminDashboard();
            } else {
                navigationManager.showCustomerOverview();
            }
        } else {
            // If user role is unclear, go to login for security
            navigationManager.navigateTo(NavigationManager.LOGIN_SCREEN);
        }
    }
    
    /**
     * Handle new booking
     */
    private void handleNewBooking() {
        // Navigate to flight information for new booking
        navigationManager.showFlightInformation();
    }
    
    /**
     * Handle export bookings
     */
    private void handleExportBookings() {
        showAlert("Export Feature", "Export functionality will be implemented in future version.");
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
