package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.User;
import util.NavigationManager;

/**
 * Controller for the Customer Overview screen
 */
public class CustomerOverviewController implements Initializable {
    
    @FXML private Label usernameLabel;
    @FXML private Button logoutButton;
    @FXML private Button searchFlightsButton;
    @FXML private Button viewBookingsButton;
    @FXML private Button submitTicketButton;
    @FXML private Button aiChatButton;
    @FXML private Button viewProfileButton;
    
    // Quick action buttons (duplicate buttons in the cards)
    @FXML private Button searchFlightsAction;
    @FXML private Button viewBookingsAction;
    @FXML private Button submitTicketAction;
    @FXML private Button aiChatAction;
    
    private NavigationManager navigationManager;
    private User currentUser;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        navigationManager = NavigationManager.getInstance();
        
        // Get current user
        currentUser = (User) navigationManager.getSharedData("currentUser");
        if (currentUser != null && usernameLabel != null) {
            usernameLabel.setText(currentUser.getUsername());
        }
        
        // Set up button handlers
        setupButtonHandlers();
    }
    
    /**
     * Set up button event handlers
     */
    private void setupButtonHandlers() {
        if (logoutButton != null) {
            logoutButton.setOnAction(e -> handleLogout());
        }
        
        if (searchFlightsButton != null) {
            searchFlightsButton.setOnAction(e -> navigateToFlightSearch());
        }
        
        if (viewBookingsButton != null) {
            viewBookingsButton.setOnAction(e -> navigateToBookings());
        }
        
        if (submitTicketButton != null) {
            submitTicketButton.setOnAction(e -> navigateToTicketSubmission());
        }
        
        if (aiChatButton != null) {
            aiChatButton.setOnAction(e -> navigateToAIChat());
        }
        
        if (viewProfileButton != null) {
            viewProfileButton.setOnAction(e -> handleViewProfile());
        }
        
        // Quick action buttons
        if (searchFlightsAction != null) {
            searchFlightsAction.setOnAction(e -> navigateToFlightSearch());
        }
        
        if (viewBookingsAction != null) {
            viewBookingsAction.setOnAction(e -> navigateToBookings());
        }
        
        if (submitTicketAction != null) {
            submitTicketAction.setOnAction(e -> navigateToTicketSubmission());
        }
        
        if (aiChatAction != null) {
            aiChatAction.setOnAction(e -> navigateToAIChat());
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
     * Navigate to flight search
     */
    private void navigateToFlightSearch() {
        navigationManager.showFlightInformation();
    }
    
    /**
     * Navigate to bookings
     */
    private void navigateToBookings() {
        navigationManager.showBookingOverview();
    }
    
    /**
     * Navigate to ticket submission
     */
    private void navigateToTicketSubmission() {
        navigationManager.showTicketSubmission();
    }
    
    /**
     * Navigate to AI chat
     */
    private void navigateToAIChat() {
        navigationManager.showAIChatbot();
    }
    
    /**
     * Handle view profile
     */
    private void handleViewProfile() {
        // For now, just stay on customer overview as profile isn't implemented yet
        navigationManager.showCustomerOverview();
    }
}
