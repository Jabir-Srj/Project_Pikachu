package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.Customer;
import model.User;
import model.UserRole;
import service.UserService;
import util.NavigationManager;
import util.ServiceLocator;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Account Registration screen
 */
public class AccountRegistrationController implements Initializable {
    
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> countryComboBox;
    @FXML private DatePicker dateOfBirthPicker;
    @FXML private CheckBox termsCheckBox;
    @FXML private Button createAccountButton;
    @FXML private Hyperlink signInLink;
    
    private UserService userService;
    private NavigationManager navigationManager;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userService = ServiceLocator.getInstance().getUserService();
        navigationManager = NavigationManager.getInstance();
        
        // Set up event handlers
        createAccountButton.setOnAction(e -> handleRegistration());
        signInLink.setOnAction(e -> navigateToLogin());
        
        // Initialize country combo box
        countryComboBox.getItems().addAll(
            "United States", "United Kingdom", "Canada", "Australia", 
            "Germany", "France", "Japan", "China", "India", "Malaysia"
        );
    }
    
    /**
     * Handle registration
     */
    private void handleRegistration() {
        // Validate input
        if (!validateInput()) {
            return;
        }
        
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        // Create new user
        Customer customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        customer.setPhoneNumber(phone);
        customer.setUsername(username);
        customer.setPassword(password);
        customer.setRole(UserRole.CUSTOMER);
        
        // Register user
        boolean registered = userService.registerUser(customer);
        
        if (registered) {
            showAlert("Registration Successful", 
                     "Your account has been created successfully!", 
                     Alert.AlertType.INFORMATION);
            navigateToLogin();
        } else {
            showAlert("Registration Failed", 
                     "Username already exists. Please choose a different username.", 
                     Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Validate input fields
     */
    private boolean validateInput() {
        String errorMessage = "";
        
        if (firstNameField.getText().trim().isEmpty()) {
            errorMessage += "First name is required.\n";
        }
        if (lastNameField.getText().trim().isEmpty()) {
            errorMessage += "Last name is required.\n";
        }
        if (emailField.getText().trim().isEmpty()) {
            errorMessage += "Email is required.\n";
        }
        if (phoneField.getText().trim().isEmpty()) {
            errorMessage += "Phone number is required.\n";
        }
        if (usernameField.getText().trim().isEmpty()) {
            errorMessage += "Username is required.\n";
        }
        if (passwordField.getText().isEmpty()) {
            errorMessage += "Password is required.\n";
        }
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            errorMessage += "Passwords do not match.\n";
        }
        if (!termsCheckBox.isSelected()) {
            errorMessage += "You must accept the terms and conditions.\n";
        }
        
        if (!errorMessage.isEmpty()) {
            showAlert("Validation Error", errorMessage, Alert.AlertType.ERROR);
            return false;
        }
        
        return true;
    }
    
    /**
     * Navigate to login screen
     */
    private void navigateToLogin() {
        navigationManager.navigateTo(NavigationManager.LOGIN_SCREEN);
    }
    
    /**
     * Show alert dialog
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
