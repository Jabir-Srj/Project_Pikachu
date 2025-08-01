package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.text.Text;
import model.User;
import service.BookingService;
import service.FlightService;
import service.TicketService;
import util.NavigationManager;
import util.ServiceLocator;

/**
 * Controller for the Admin Dashboard screen
 */
public class AdminDashboardController implements Initializable {
    
    @FXML private Label usernameLabel;
    @FXML private MenuButton adminMenuButton;
    @FXML private MenuItem settingsMenuItem;
    @FXML private MenuItem logoutMenuItem;
    @FXML private Button notificationsButton;
    @FXML private Button dashboardButton;
    @FXML private Button flightsButton;
    @FXML private Button bookingsButton;
    @FXML private Button customersButton;
    @FXML private Button ticketsButton;
    @FXML private Button refundsButton;
    @FXML private Button reportsButton;
    @FXML private Button settingsButton;
    @FXML private Text totalFlightsLabel;
    @FXML private Text activeBookingsLabel;
    @FXML private Text totalCustomersLabel;
    @FXML private Text pendingTicketsLabel;
    @FXML private Button refreshButton;
    
    // Quick Action Buttons
    @FXML private Button addFlightButton;
    @FXML private Button viewBookingsButton;
    @FXML private Button manageUsersButton;
    @FXML private Button generateReportButton;
    
    private NavigationManager navigationManager;
    private FlightService flightService;
    private BookingService bookingService;
    private TicketService ticketService;
    private User currentUser;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("AdminDashboardController: Initializing...");
        navigationManager = NavigationManager.getInstance();
        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        flightService = serviceLocator.getFlightService();
        bookingService = serviceLocator.getBookingService();
        ticketService = serviceLocator.getTicketService();
        
        // Get current user
        currentUser = (User) navigationManager.getSharedData("currentUser");
        if (currentUser != null && usernameLabel != null) {
            usernameLabel.setText(currentUser.getUsername());
        }
        
        // Set up button handlers
        setupButtonHandlers();
        
        // Load dashboard data
        loadDashboardData();
        
        System.out.println("AdminDashboardController: Initialization complete");
    }
    
    /**
     * Set up button event handlers
     */
    private void setupButtonHandlers() {
        System.out.println("AdminDashboardController: Setting up button handlers...");
        
        // Menu items
        if (logoutMenuItem != null) {
            logoutMenuItem.setOnAction(e -> {
                System.out.println("Logout clicked");
                handleLogout();
            });
        }
        
        if (settingsMenuItem != null) {
            settingsMenuItem.setOnAction(e -> {
                System.out.println("Settings clicked");
                navigateToSettings();
            });
        }
        
        // Header buttons
        if (notificationsButton != null) {
            notificationsButton.setOnAction(e -> {
                System.out.println("Notifications clicked");
                // Notifications feature will be implemented in future updates
            });
        }
        
        // Navigation buttons
        if (dashboardButton != null) {
            dashboardButton.setOnAction(e -> {
                System.out.println("Dashboard clicked");
                navigationManager.showAdminDashboard();
            });
        }
        
        if (flightsButton != null) {
            flightsButton.setOnAction(e -> {
                System.out.println("Flights clicked");
                navigationManager.showFlightInformation();
            });
        }
        
        if (bookingsButton != null) {
            bookingsButton.setOnAction(e -> {
                System.out.println("Bookings clicked");
                navigationManager.showBookingOverview();
            });
        }
        
        if (customersButton != null) {
            customersButton.setOnAction(e -> {
                System.out.println("Customers clicked");
                navigationManager.showCustomerManagement();
            });
        }
        
        if (ticketsButton != null) {
            ticketsButton.setOnAction(e -> {
                System.out.println("Tickets clicked");
                navigationManager.showTicketManagement();
            });
        }
        
        if (refundsButton != null) {
            refundsButton.setOnAction(e -> {
                System.out.println("Refunds clicked");
                navigationManager.navigateTo(NavigationManager.REFUND_APPROVAL);
            });
        }
        
        if (reportsButton != null) {
            reportsButton.setOnAction(e -> {
                System.out.println("Reports clicked");
                showReportNotImplementedMessage();
            });
        }
        
        if (settingsButton != null) {
            settingsButton.setOnAction(e -> {
                System.out.println("Settings button clicked");
                navigationManager.showAdminSettings();
            });
        }
        
        if (refreshButton != null) {
            refreshButton.setOnAction(e -> {
                System.out.println("Refresh clicked");
                loadDashboardData();
            });
        }
        
        // Quick action buttons
        if (addFlightButton != null) {
            addFlightButton.setOnAction(e -> {
                System.out.println("Add Flight clicked");
                navigationManager.showFlightInformation();
            });
        }
        
        if (viewBookingsButton != null) {
            viewBookingsButton.setOnAction(e -> {
                System.out.println("View Bookings clicked");
                navigationManager.showBookingOverview();
            });
        }
        
        if (manageUsersButton != null) {
            manageUsersButton.setOnAction(e -> {
                System.out.println("Manage Users clicked");
                navigationManager.showCustomerManagement();
            });
        }
        
        if (generateReportButton != null) {
            generateReportButton.setOnAction(e -> {
                System.out.println("Generate Report clicked");
                showReportNotImplementedMessage();
            });
        }
        
        System.out.println("AdminDashboardController: Button handlers setup complete");
    }
    
    /**
     * Load dashboard statistics
     */
    private void loadDashboardData() {
        try {
            // Update flight count
            if (totalFlightsLabel != null) {
                int flightCount = flightService.getAllFlights().size();
                totalFlightsLabel.setText(String.valueOf(flightCount));
            }
            
            // Update booking count
            if (activeBookingsLabel != null) {
                int bookingCount = bookingService.getAllBookings().size();
                activeBookingsLabel.setText(String.valueOf(bookingCount));
            }
            
            // Update customer count
            if (totalCustomersLabel != null) {
                // For now, we'll use a placeholder
                totalCustomersLabel.setText("150");
            }
            
            // Update pending tickets count
            if (pendingTicketsLabel != null) {
                // For now, we'll use a placeholder
                pendingTicketsLabel.setText("12");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Handle logout
     */
    private void handleLogout() {
        navigationManager.clearSharedData();
        navigationManager.navigateTo(NavigationManager.LOGIN_SCREEN);
    }
    
    /**
     * Navigate to flight management
     */
    private void navigateToFlightManagement() {
        navigationManager.showFlightInformation();
    }
    
    /**
     * Navigate to booking management
     */
    private void navigateToBookingManagement() {
        navigationManager.showBookingOverview();
    }
    
    /**
     * Navigate to customer management
     */
    private void navigateToCustomerManagement() {
        navigationManager.showCustomerOverview();
    }
    
    /**
     * Navigate to ticket management
     */
    private void navigateToTicketManagement() {
        navigationManager.showTicketOverview();
    }
    
    /**
     * Navigate to refund management
     */
    private void navigateToRefundManagement() {
        navigationManager.navigateTo(NavigationManager.REFUND_APPROVAL);
    }
    
    /**
     * Navigate to reports
     */
    private void navigateToReports() {
        // For now, redirect to ticket overview as reports aren't implemented yet
        navigationManager.showTicketOverview();
    }
    
    /**
     * Navigate to settings
     */
    private void navigateToSettings() {
        // Navigate to admin settings page
        navigationManager.showAdminSettings();
    }
    
    /**
     * Show message dialog for report functionality not implemented
     */
    private void showReportNotImplementedMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Report Feature");
        alert.setHeaderText("Feature Not Available");
        alert.setContentText("Report functionality will be implemented in future version.");
        alert.showAndWait();
    }
} 