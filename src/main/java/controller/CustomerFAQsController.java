package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.text.Text;
import model.User;
import util.NavigationManager;
import util.SessionManager;

/**
 * Controller for the Customer FAQs screen
 */
public class CustomerFAQsController implements Initializable {
    
    @FXML private Button backButton;
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button clearSearchButton;
    @FXML private VBox faqContainer;
    
    // Category buttons
    @FXML private Button bookingFAQButton;
    @FXML private Button flightFAQButton;
    @FXML private Button paymentFAQButton;
    @FXML private Button supportFAQButton;
    @FXML private Button accountFAQButton;
    
    // FAQ sections
    @FXML private VBox bookingSection;
    @FXML private VBox flightSection;
    @FXML private VBox paymentSection;
    @FXML private VBox supportSection;
    @FXML private VBox accountSection;
    
    // Help buttons
    @FXML private Button contactSupportButton;
    @FXML private Button aiChatButton;
    @FXML private Button viewTicketsButton;
    
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
        }
        
        // Set up button handlers
        setupButtonHandlers();
        
        // Set up search functionality
        setupSearchFunctionality();
    }
    
    /**
     * Set up button event handlers
     */
    private void setupButtonHandlers() {
        if (backButton != null) {
            backButton.setOnAction(e -> handleBack());
        }
        
        // Category navigation buttons
        if (bookingFAQButton != null) {
            bookingFAQButton.setOnAction(e -> scrollToSection(bookingSection));
        }
        
        if (flightFAQButton != null) {
            flightFAQButton.setOnAction(e -> scrollToSection(flightSection));
        }
        
        if (paymentFAQButton != null) {
            paymentFAQButton.setOnAction(e -> scrollToSection(paymentSection));
        }
        
        if (supportFAQButton != null) {
            supportFAQButton.setOnAction(e -> scrollToSection(supportSection));
        }
        
        if (accountFAQButton != null) {
            accountFAQButton.setOnAction(e -> scrollToSection(accountSection));
        }
        
        // Help section buttons
        if (contactSupportButton != null) {
            contactSupportButton.setOnAction(e -> navigateToTicketSubmission());
        }
        
        if (aiChatButton != null) {
            aiChatButton.setOnAction(e -> navigateToAIChat());
        }
        
        if (viewTicketsButton != null) {
            viewTicketsButton.setOnAction(e -> navigateToTicketManagement());
        }
    }
    
    /**
     * Set up search functionality
     */
    private void setupSearchFunctionality() {
        if (searchButton != null) {
            searchButton.setOnAction(e -> handleSearch());
        }
        
        if (clearSearchButton != null) {
            clearSearchButton.setOnAction(e -> handleClearSearch());
        }
        
        // Allow search on Enter key press
        if (searchField != null) {
            searchField.setOnAction(e -> handleSearch());
        }
    }
    
    /**
     * Handle back button
     */
    private void handleBack() {
        navigationManager.showCustomerOverview();
    }
    
    /**
     * Scroll to a specific FAQ section
     */
    private void scrollToSection(VBox section) {
        if (section != null && section.getParent() != null) {
            // Simple approach: make the section visible and bring focus to it
            section.requestFocus();
            
            // Show success message
            System.out.println("Navigated to FAQ section: " + section.getId());
        }
    }
    
    /**
     * Handle search functionality
     */
    private void handleSearch() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        
        if (searchTerm.isEmpty()) {
            showAllSections();
            return;
        }
        
        // Simple search implementation
        hideAllSections();
        
        // Search through each section and show matching ones
        boolean found = false;
        
        if (searchInSection(bookingSection, searchTerm)) {
            bookingSection.setVisible(true);
            bookingSection.setManaged(true);
            found = true;
        }
        
        if (searchInSection(flightSection, searchTerm)) {
            flightSection.setVisible(true);
            flightSection.setManaged(true);
            found = true;
        }
        
        if (searchInSection(paymentSection, searchTerm)) {
            paymentSection.setVisible(true);
            paymentSection.setManaged(true);
            found = true;
        }
        
        if (searchInSection(supportSection, searchTerm)) {
            supportSection.setVisible(true);
            supportSection.setManaged(true);
            found = true;
        }
        
        if (searchInSection(accountSection, searchTerm)) {
            accountSection.setVisible(true);
            accountSection.setManaged(true);
            found = true;
        }
        
        if (!found) {
            System.out.println("No FAQs found matching: " + searchTerm);
        }
    }
    
    /**
     * Check if a section contains the search term
     */
    private boolean searchInSection(VBox section, String searchTerm) {
        if (section == null) return false;
        
        // Search in all text elements within the section
        return searchInNode(section, searchTerm);
    }
    
    /**
     * Recursively search for text in nodes
     */
    private boolean searchInNode(Node node, String searchTerm) {
        if (node instanceof Text) {
            Text textNode = (Text) node;
            return textNode.getText().toLowerCase().contains(searchTerm);
        }
        
        if (node instanceof VBox) {
            VBox vbox = (VBox) node;
            for (Node child : vbox.getChildren()) {
                if (searchInNode(child, searchTerm)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Handle clear search
     */
    private void handleClearSearch() {
        if (searchField != null) {
            searchField.clear();
        }
        showAllSections();
    }
    
    /**
     * Show all FAQ sections
     */
    private void showAllSections() {
        setAllSectionsVisibility(true);
    }
    
    /**
     * Hide all FAQ sections
     */
    private void hideAllSections() {
        setAllSectionsVisibility(false);
    }
    
    /**
     * Set visibility for all sections
     */
    private void setAllSectionsVisibility(boolean visible) {
        VBox[] sections = {bookingSection, flightSection, paymentSection, supportSection, accountSection};
        
        for (VBox section : sections) {
            if (section != null) {
                section.setVisible(visible);
                section.setManaged(visible);
            }
        }
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
}
