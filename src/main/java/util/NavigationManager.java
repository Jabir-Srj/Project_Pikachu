package util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * NavigationManager handles all screen transitions in the Pikachu Airlines application.
 * It provides a centralized way to load FXML files and switch between screens.
 */
public class NavigationManager {
    private static NavigationManager instance;
    private Stage primaryStage;
    private Scene currentScene;
    private final Map<String, Object> sharedData;
    
    // Screen constants
    public static final String LOGIN_SCREEN = "/fxml/Login.fxml";
    public static final String REGISTRATION_SCREEN = "/fxml/AccountRegistration.fxml";
    public static final String ADMIN_DASHBOARD = "/fxml/AdminDashboard.fxml";
    public static final String CUSTOMER_OVERVIEW = "/fxml/CustomerOverview.fxml";
    public static final String FLIGHT_INFORMATION = "/fxml/FlightInformation.fxml";
    public static final String BOOKING_OVERVIEW = "/fxml/BookingOverview.fxml";
    public static final String BOOKING_DETAILS = "/fxml/BookingDetails.fxml";
    public static final String PAYMENT_DETAILS = "/fxml/PaymentDetails.fxml";
    public static final String TICKET_SUBMISSION = "/fxml/TicketSubmission.fxml";
    public static final String TICKET_OVERVIEW = "/fxml/TicketOverview.fxml";
    public static final String TICKET_STATUS = "/fxml/TicketStatus.fxml";
    public static final String AI_CHATBOT = "/fxml/AIChatbot.fxml";
    public static final String MODERN_AI_CHATBOT = "/fxml/ModernAIChatbot.fxml";
    public static final String REFUND_APPROVAL = "/fxml/RefundApproval.fxml";
    public static final String CUSTOMER_DETAILS = "/fxml/CustomerDetails.fxml";
    public static final String CUSTOMER_MANAGEMENT = "/fxml/CustomerManagement.fxml";
    public static final String FLIGHT_DETAILS = "/fxml/FlightDetails.fxml";
    
    private NavigationManager() {
        sharedData = new HashMap<>();
    }
    
    /**
     * Get the singleton instance of NavigationManager
     */
    public static NavigationManager getInstance() {
        if (instance == null) {
            instance = new NavigationManager();
        }
        return instance;
    }
    
    /**
     * Set the primary stage for the application
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    
    /**
     * Navigate to a screen specified by the FXML path
     */
    public void navigateTo(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            // Create scene if it doesn't exist, or update the root
            if (currentScene == null) {
                currentScene = new Scene(root);
                primaryStage.setScene(currentScene);
            } else {
                currentScene.setRoot(root);
            }
            
            // Apply the CSS stylesheet
            String cssPath = "/css/application.css";
            currentScene.getStylesheets().clear();
            currentScene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
            
            primaryStage.show();
            primaryStage.centerOnScreen();
            
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load FXML: " + fxmlPath);
        }
    }
    
    /**
     * Navigate to a screen with a custom controller
     */
    public <T> T navigateToWithController(String fxmlPath, Object controller) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (controller != null) {
                loader.setController(controller);
            }
            
            Parent root = loader.load();
            
            if (currentScene == null) {
                currentScene = new Scene(root);
                primaryStage.setScene(currentScene);
            } else {
                currentScene.setRoot(root);
            }
            
            // Apply CSS
            String cssPath = "/css/application.css";
            currentScene.getStylesheets().clear();
            currentScene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
            
            primaryStage.show();
            primaryStage.centerOnScreen();
            
            return loader.getController();
            
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load FXML: " + fxmlPath);
            return null;
        }
    }
    
    /**
     * Get controller for a loaded FXML
     */
    public <T> T getController(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.load();
            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Store data to be shared between screens
     */
    public void setSharedData(String key, Object data) {
        sharedData.put(key, data);
    }
    
    /**
     * Retrieve shared data
     */
    public Object getSharedData(String key) {
        return sharedData.get(key);
    }
    
    /**
     * Clear shared data
     */
    public void clearSharedData() {
        sharedData.clear();
    }
    
    /**
     * Get the primary stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    // Convenience navigation methods
    
    public void showLogin() {
        navigateTo(LOGIN_SCREEN);
    }
    
    public void showRegistration() {
        navigateTo(REGISTRATION_SCREEN);
    }
    
    public void showAdminDashboard() {
        navigateTo(ADMIN_DASHBOARD);
    }
    
    public void showCustomerDashboard() {
        navigateTo(CUSTOMER_OVERVIEW);
    }
    
    public void showTicketManagement() {
        navigateTo("/fxml/TicketManagement.fxml");
    }
    
    public void showCustomerOverview() {
        navigateTo(CUSTOMER_OVERVIEW);
    }
    
    public void showFlightInformation() {
        navigateTo(FLIGHT_INFORMATION);
    }
    
    public void showFlightDetails(model.Flight flight) {
        setSharedData("selectedFlight", flight);
        navigateTo(FLIGHT_DETAILS);
    }
    
    public void showPaymentDetails(model.Flight flight) {
        setSharedData("selectedFlight", flight);
        navigateTo(PAYMENT_DETAILS);
    }
    
    public void showBookingOverview() {
        navigateTo(BOOKING_OVERVIEW);
    }
    
    public void showBookingDetails(model.Booking booking) {
        setSharedData("selectedBooking", booking);
        navigateTo(BOOKING_DETAILS);
    }
    
    public void showTicketSubmission() {
        navigateTo(TICKET_SUBMISSION);
    }
    
    public void showTicketOverview() {
        navigateTo(TICKET_OVERVIEW);
    }
    
    public void showTicketStatus(model.Ticket ticket) {
        setSharedData("selectedTicket", ticket);
        navigateTo(TICKET_STATUS);
    }
    
    public void showAIChatbot() {
        navigateTo(MODERN_AI_CHATBOT);
    }
    
    public void showCustomerDetails(model.Customer customer) {
        setSharedData("selectedCustomer", customer);
        navigateTo(CUSTOMER_DETAILS);
    }
    
    public void showCustomerManagement() {
        navigateTo(CUSTOMER_MANAGEMENT);
    }
    
    public void showRefundApproval(model.RefundRequest refund) {
        setSharedData("selectedRefund", refund);
        navigateTo(REFUND_APPROVAL);
    }
    
    public void setSelectedFlightClass(String flightClass) {
        setSharedData("selectedFlightClass", flightClass);
    }

    public void showAddCustomer() {
        navigateTo("/fxml/AddCustomer.fxml");
    }
    public void showAdminSettings() {
        navigateTo("/fxml/AdminSettings.fxml");
    }
    public void showCustomerProfile() {
        navigateTo("/fxml/CustomerProfile.fxml");
    }
    public void showSupportTicketSubmission() {
        navigateTo("/fxml/SupportTicketSubmission.fxml");
    }
}