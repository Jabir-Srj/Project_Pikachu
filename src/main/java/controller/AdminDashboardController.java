package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    @FXML private Button logoutButton;
    @FXML private Button dashboardButton;
    @FXML private Button flightsButton;
    @FXML private Button bookingsButton;
    @FXML private Button customersButton;
    @FXML private Button ticketsButton;
    @FXML private Button reportsButton;
    @FXML private Button settingsButton;
    @FXML private Text totalFlightsText;
    @FXML private Text totalBookingsText;
    @FXML private Text totalCustomersText;
    @FXML private Text pendingTicketsText;
    @FXML private Button refreshButton;
    
    private NavigationManager navigationManager;
    private FlightService flightService;
    private BookingService bookingService;
    private TicketService ticketService;
    private User currentUser;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
    }
    
    /**
     * Set up button event handlers
     */
    private void setupButtonHandlers() {
        if (logoutButton != null) {
            logoutButton.setOnAction(e -> handleLogout());
        }
        
        if (dashboardButton != null) {
            dashboardButton.setOnAction(e -> loadDashboardData());
        }
        
        if (flightsButton != null) {
            flightsButton.setOnAction(e -> navigateToFlightManagement());
        }
        
        if (bookingsButton != null) {
            bookingsButton.setOnAction(e -> navigateToBookingManagement());
        }
        
        if (customersButton != null) {
            customersButton.setOnAction(e -> navigateToCustomerManagement());
        }
        
        if (ticketsButton != null) {
            ticketsButton.setOnAction(e -> navigateToTicketManagement());
        }
        
        if (refreshButton != null) {
            refreshButton.setOnAction(e -> loadDashboardData());
        }
    }
    
    /**
     * Load dashboard statistics
     */
    private void loadDashboardData() {
        try {
            // Update flight count
            if (totalFlightsText != null) {
                int flightCount = flightService.getAllFlights().size();
                totalFlightsText.setText(String.valueOf(flightCount));
            }
            
            // Update booking count
            if (totalBookingsText != null) {
                int bookingCount = bookingService.getAllBookings().size();
                totalBookingsText.setText(String.valueOf(bookingCount));
            }
            
            // Update customer count
            if (totalCustomersText != null) {
                // For now, we'll use a placeholder
                totalCustomersText.setText("150");
            }
            
            // Update pending tickets count
            if (pendingTicketsText != null) {
                // For now, we'll use a placeholder
                pendingTicketsText.setText("12");
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
        navigationManager.navigateTo(NavigationManager.FLIGHT_INFORMATION);
    }
    
    /**
     * Navigate to booking management
     */
    private void navigateToBookingManagement() {
        navigationManager.navigateTo(NavigationManager.BOOKING_OVERVIEW);
    }
    
    /**
     * Navigate to customer management
     */
    private void navigateToCustomerManagement() {
        navigationManager.navigateTo(NavigationManager.CUSTOMER_OVERVIEW);
    }
    
    /**
     * Navigate to ticket management
     */
    private void navigateToTicketManagement() {
        navigationManager.navigateTo(NavigationManager.TICKET_OVERVIEW);
    }
} 