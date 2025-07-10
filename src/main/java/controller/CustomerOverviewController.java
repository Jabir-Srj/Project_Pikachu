package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.User;
import util.NavigationManager;
import util.ServiceLocator;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Customer Overview screen
 */
public class CustomerOverviewController implements Initializable {
    
    @FXML private Label usernameLabel;
    @FXML private Button logoutButton;
    @FXML private Button searchFlightsButton;
    @FXML private Button myBookingsButton;
    @FXML private Button submitTicketButton;
    @FXML private Button aiChatButton;
    
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
        
        if (myBookingsButton != null) {
            myBookingsButton.setOnAction(e -> navigateToBookings());
        }
        
        if (submitTicketButton != null) {
            submitTicketButton.setOnAction(e -> navigateToTicketSubmission());
        }
        
        if (aiChatButton != null) {
            aiChatButton.setOnAction(e -> navigateToAIChat());
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
        navigationManager.navigateTo(NavigationManager.FLIGHT_INFORMATION);
    }
    
    /**
     * Navigate to bookings
     */
    private void navigateToBookings() {
        navigationManager.navigateTo(NavigationManager.BOOKING_OVERVIEW);
    }
    
    /**
     * Navigate to ticket submission
     */
    private void navigateToTicketSubmission() {
        navigationManager.navigateTo(NavigationManager.TICKET_SUBMISSION);
    }
    
    /**
     * Navigate to AI chat
     */
    private void navigateToAIChat() {
        navigationManager.navigateTo(NavigationManager.AI_CHATBOT);
    }
}
