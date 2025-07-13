package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.User;
import util.NavigationManager;
import util.SessionManager;

/**
 * Controller for the Customer Overview screen
 */
public class CustomerOverviewController implements Initializable {
    
    @FXML private Label usernameLabel;
    @FXML private Button viewProfileButton;
    @FXML private Button logoutButton;
    
    // Quick action buttons (duplicate buttons in the cards)
    @FXML private Button searchFlightsAction;
    @FXML private Button viewBookingsAction;
    @FXML private Button submitTicketAction;
    @FXML private Button viewTicketsAction;
    @FXML private Button aiChatAction;
    @FXML private Button faqsAction;
    @FXML private Button contactInfoAction;
    
    private NavigationManager navigationManager;
    private User currentUser;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        navigationManager = NavigationManager.getInstance();
        
        // Get current user
        currentUser = (User) navigationManager.getSharedData("currentUser");
        if (currentUser != null) {
            // Set current user in SessionManager for ticket system
            SessionManager.setCurrentUser(currentUser);
            
            if (usernameLabel != null) {
                usernameLabel.setText(currentUser.getUsername());
            }
        }
        
        // Set up button handlers
        setupButtonHandlers();
    }
    
    /**
     * Set up button event handlers
     */
    private void setupButtonHandlers() {
        if (viewProfileButton != null) {
            viewProfileButton.setOnAction(e -> handleViewProfile());
        }
        
        if (logoutButton != null) {
            logoutButton.setOnAction(e -> handleLogout());
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
        
        if (viewTicketsAction != null) {
            viewTicketsAction.setOnAction(e -> navigateToTicketManagement());
        }
        
        if (aiChatAction != null) {
            aiChatAction.setOnAction(e -> navigateToAIChat());
        }
        
        if (faqsAction != null) {
            faqsAction.setOnAction(e -> navigateToFAQs());
        }
        
        if (contactInfoAction != null) {
            contactInfoAction.setOnAction(e -> showContactInfo());
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
     * Handle view profile
     */
    private void handleViewProfile() {
        navigationManager.showCustomerProfile();
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
     * Navigate to ticket management
     */
    private void navigateToTicketManagement() {
        navigationManager.showTicketManagement();
    }
      /**
     * Navigate to AI chat
     */
    private void navigateToAIChat() {
        navigationManager.showAIChatbot();
    }
    
    /**
     * Navigate to FAQs
     */
    private void navigateToFAQs() {
        util.FAQAlert.show(currentUser);
    }
    
    /**
     * Show contact information
     */
    private void showContactInfo() {
        // Create a simple contact info dialog
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Contact Information");
        alert.setHeaderText("Pikachu Airlines Customer Service");
        alert.setContentText("📞 Phone: 1-800-PIKACHU (1-800-742-2248)\n" +
                            "📧 Email: support@pikachuairlines.com\n" +
                            "🌐 Website: www.pikachuairlines.com\n\n" +
                            "⏰ Customer Service Hours:\n" +
                            "Monday - Friday: 6:00 AM - 11:00 PM\n" +
                            "Saturday - Sunday: 7:00 AM - 9:00 PM\n\n" +
                            "🆘 Emergency Support: Available 24/7\n" +
                            "📱 Text Support: Text 'HELP' to 555-PIKA");
        alert.showAndWait();
    }

}
