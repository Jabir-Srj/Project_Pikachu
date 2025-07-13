package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.Customer;
import model.UserRole;
import service.UserService;
import util.NavigationManager;
import util.ServiceLocator;

/**
 * Controller for the Account Registration screen
 */
public class AccountRegistrationController implements Initializable {
    
    // Form fields
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private CheckBox termsCheckBox;
    
    // Navigation buttons
    @FXML private Button registerButton;
    @FXML private Hyperlink loginButton;
    
    private UserService userService;
    private NavigationManager navigationManager;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userService = ServiceLocator.getInstance().getUserService();
        navigationManager = NavigationManager.getInstance();
    }
    
    /**
     * Handle Register button click
     */
    @FXML
    private void handleRegister() {
        if (validateForm()) {
            performRegistration();
        }
    }
    
    /**
     * Handle Login redirect
     */
    @FXML
    private void handleLoginRedirect() {
        navigateToLogin();
    }
    
    /**
     * Perform the actual registration
     */
    private void performRegistration() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = passwordField.getText();
        
        // Generate username from email
        String username = email.split("@")[0];
        
        // Create new customer
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
                     "Your account has been created successfully! You can now sign in with your email and password.", 
                     Alert.AlertType.INFORMATION);
            navigateToLogin();
        } else {
            showAlert("Registration Failed", 
                     "An account with this email already exists. Please use a different email address.", 
                     Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Validate the registration form
     */
    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();
        
        if (firstNameField.getText().trim().isEmpty()) {
            errors.append("First name is required.\n");
        }
        if (lastNameField.getText().trim().isEmpty()) {
            errors.append("Last name is required.\n");
        }
        if (emailField.getText().trim().isEmpty()) {
            errors.append("Email is required.\n");
        } else if (!isValidEmail(emailField.getText().trim())) {
            errors.append("Please enter a valid email address.\n");
        }
        if (phoneField.getText().trim().isEmpty()) {
            errors.append("Phone number is required.\n");
        }
        if (passwordField.getText().isEmpty()) {
            errors.append("Password is required.\n");
        } else if (passwordField.getText().length() < 6) {
            errors.append("Password must be at least 6 characters long.\n");
        }
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            errors.append("Passwords do not match.\n");
        }
        if (!termsCheckBox.isSelected()) {
            errors.append("You must accept the Terms and Conditions.\n");
        }
        
        if (errors.length() > 0) {
            showAlert("Validation Error", errors.toString(), Alert.AlertType.ERROR);
            return false;
        }
        
        return true;
    }
    
    /**
     * Validate email format
     */
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
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
