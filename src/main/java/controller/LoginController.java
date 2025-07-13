package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.User;
import model.UserRole;
import service.UserService;
import util.NavigationManager;
import util.ServiceLocator;

/**
 * Controller for the Login screen
 */
public class LoginController implements Initializable {
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Hyperlink registerLink;
    @FXML private Hyperlink forgotPasswordLink;
    
    private UserService userService;
    private NavigationManager navigationManager;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userService = ServiceLocator.getInstance().getUserService();
        navigationManager = NavigationManager.getInstance();
        
        // Set up event handlers
        loginButton.setOnAction(e -> handleLogin());
        registerLink.setOnAction(e -> navigateToRegistration());
        forgotPasswordLink.setOnAction(e -> handleForgotPassword());
        
        // Allow enter key to submit login
        passwordField.setOnAction(e -> handleLogin());
    }
    
    /**
     * Handle login button click
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Login Error", "Please enter both username and password", Alert.AlertType.ERROR);
            return;
        }
        
        User user = userService.authenticate(username, password);
        
        if (user != null) {
            // Store current user in shared data
            navigationManager.setSharedData("currentUser", user);
            
            // Navigate based on user role
            if (user.getRole() == UserRole.ADMIN) {
                navigationManager.navigateTo(NavigationManager.ADMIN_DASHBOARD);
            } else {
                navigationManager.navigateTo(NavigationManager.CUSTOMER_OVERVIEW);
            }
        } else {
            showAlert("Login Failed", "Invalid username or password", Alert.AlertType.ERROR);
            clearFields();
        }
    }
    
    /**
     * Navigate to registration screen
     */
    private void navigateToRegistration() {
        navigationManager.navigateTo(NavigationManager.REGISTRATION_SCREEN);
    }
    
    /**
     * Handle forgot password link click
     */
    private void handleForgotPassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Forgot Password");
        alert.setHeaderText("Password Reset Instructions");
        alert.setContentText(
            "To reset your password, please follow these steps:\n\n" +
            "1. Enter your email address or username\n" +
            "2. Check your email for a password reset link\n" +
            "3. Click the link to create a new password\n" +
            "4. The reset link expires in 24 hours\n\n" +
            "📧 Email: support@pikachuairlines.com\n" +
            "📞 Phone: 1-800-PIKACHU (1-800-742-2248)\n\n" +
            "If you don't receive an email within 10 minutes, please check your spam folder or contact customer support."
        );
        
        // Add custom styling to the alert
        alert.getDialogPane().getStylesheets().add(
            getClass().getResource("/css/application.css").toExternalForm()
        );
        
        alert.showAndWait();
    }
    
    /**
     * Clear input fields
     */
    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        usernameField.requestFocus();
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