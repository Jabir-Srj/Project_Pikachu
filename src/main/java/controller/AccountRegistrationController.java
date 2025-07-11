package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.Customer;
import model.UserRole;
import service.UserService;
import util.NavigationManager;
import util.ServiceLocator;

/**
 * Controller for the Account Registration screen
 */
public class AccountRegistrationController implements Initializable {
    
    // Step containers
    @FXML private VBox step1Container;
    @FXML private VBox step2Container;
    @FXML private VBox step3Container;
    
    // Progress components
    @FXML private ProgressBar progressBar;
    @FXML private Text stepLabel;
    
    // Step 1: Personal Information
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private DatePicker dobPicker;
    @FXML private ComboBox<String> genderComboBox;
    
    // Step 2: Account Security
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> securityQuestionComboBox;
    @FXML private TextField securityAnswerField;
    @FXML private Text lengthRequirement;
    @FXML private Text uppercaseRequirement;
    @FXML private Text numberRequirement;
    @FXML private Text specialRequirement;
    
    // Step 3: Preferences & Terms
    @FXML private ComboBox<String> languageComboBox;
    @FXML private ComboBox<String> currencyComboBox;
    @FXML private CheckBox emailUpdatesCheckBox;
    @FXML private CheckBox smsNotificationsCheckBox;
    @FXML private CheckBox specialOffersCheckBox;
    @FXML private CheckBox termsCheckBox;
    @FXML private CheckBox privacyCheckBox;
    @FXML private CheckBox marketingCheckBox;
    
    // Navigation buttons
    @FXML private Button backButton;
    @FXML private Button nextButton;
    @FXML private Button registerButton;
    @FXML private Hyperlink loginButton;
    
    private UserService userService;
    private NavigationManager navigationManager;
    private int currentStep = 1;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userService = ServiceLocator.getInstance().getUserService();
        navigationManager = NavigationManager.getInstance();
        
        // Initialize combo boxes
        initializeComboBoxes();
        
        // Set up initial state
        showStep(1);
        updateProgressBar();
        updateStepLabel();
    }
    
    /**
     * Initialize all combo boxes with data
     */
    private void initializeComboBoxes() {
        // Gender options
        genderComboBox.getItems().addAll("Male", "Female", "Other", "Prefer not to say");
        
        // Security questions
        securityQuestionComboBox.getItems().addAll(
            "What is your mother's maiden name?",
            "What was the name of your first pet?",
            "What city were you born in?",
            "What is your favorite movie?",
            "What was your first car?"
        );
        
        // Languages
        languageComboBox.getItems().addAll("English", "Spanish", "French", "German", "Chinese", "Japanese");
        languageComboBox.setValue("English");
        
        // Currencies
        currencyComboBox.getItems().addAll("USD", "EUR", "GBP", "JPY", "CNY", "CAD", "AUD");
        currencyComboBox.setValue("USD");
    }
    
    /**
     * Handle Next button click
     */
    @FXML
    private void handleNext() {
        if (validateCurrentStep()) {
            if (currentStep < 3) {
                currentStep++;
                showStep(currentStep);
                updateProgressBar();
                updateStepLabel();
            }
        }
    }
    
    /**
     * Handle Back button click
     */
    @FXML
    private void handleBack() {
        if (currentStep > 1) {
            currentStep--;
            showStep(currentStep);
            updateProgressBar();
            updateStepLabel();
        }
    }
    
    /**
     * Handle Register button click
     */
    @FXML
    private void handleRegister() {
        if (validateCurrentStep()) {
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
     * Show specific step
     */
    private void showStep(int step) {
        // Hide all steps
        step1Container.setVisible(false);
        step2Container.setVisible(false);
        step3Container.setVisible(false);
        
        // Show current step
        switch (step) {
            case 1:
                step1Container.setVisible(true);
                backButton.setVisible(false);
                nextButton.setVisible(true);
                registerButton.setVisible(false);
                break;
            case 2:
                step2Container.setVisible(true);
                backButton.setVisible(true);
                nextButton.setVisible(true);
                registerButton.setVisible(false);
                break;
            case 3:
                step3Container.setVisible(true);
                backButton.setVisible(true);
                nextButton.setVisible(false);
                registerButton.setVisible(true);
                break;
        }
    }
    
    /**
     * Update progress bar
     */
    private void updateProgressBar() {
        progressBar.setProgress((double) currentStep / 3.0);
    }
    
    /**
     * Update step label
     */
    private void updateStepLabel() {
        switch (currentStep) {
            case 1:
                stepLabel.setText("Step 1 of 3: Personal Information");
                break;
            case 2:
                stepLabel.setText("Step 2 of 3: Account Security");
                break;
            case 3:
                stepLabel.setText("Step 3 of 3: Preferences and Terms");
                break;
        }
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
     * Validate current step
     */
    private boolean validateCurrentStep() {
        String errorMessage = "";
        
        switch (currentStep) {
            case 1:
                errorMessage = validateStep1();
                break;
            case 2:
                errorMessage = validateStep2();
                break;
            case 3:
                errorMessage = validateStep3();
                break;
        }
        
        if (!errorMessage.isEmpty()) {
            showAlert("Validation Error", errorMessage, Alert.AlertType.ERROR);
            return false;
        }
        
        return true;
    }
    
    /**
     * Validate Step 1: Personal Information
     */
    private String validateStep1() {
        String errorMessage = "";
        
        if (firstNameField.getText().trim().isEmpty()) {
            errorMessage += "First name is required.\n";
        }
        if (lastNameField.getText().trim().isEmpty()) {
            errorMessage += "Last name is required.\n";
        }
        if (emailField.getText().trim().isEmpty()) {
            errorMessage += "Email is required.\n";
        } else if (!isValidEmail(emailField.getText().trim())) {
            errorMessage += "Please enter a valid email address.\n";
        }
        if (phoneField.getText().trim().isEmpty()) {
            errorMessage += "Phone number is required.\n";
        }
        
        return errorMessage;
    }
    
    /**
     * Validate Step 2: Account Security
     */
    private String validateStep2() {
        String errorMessage = "";
        
        if (passwordField.getText().isEmpty()) {
            errorMessage += "Password is required.\n";
        } else if (passwordField.getText().length() < 8) {
            errorMessage += "Password must be at least 8 characters long.\n";
        }
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            errorMessage += "Passwords do not match.\n";
        }
        
        return errorMessage;
    }
    
    /**
     * Validate Step 3: Preferences and Terms
     */
    private String validateStep3() {
        String errorMessage = "";
        
        if (!termsCheckBox.isSelected()) {
            errorMessage += "You must accept the Terms and Conditions.\n";
        }
        if (!privacyCheckBox.isSelected()) {
            errorMessage += "You must accept the Privacy Policy.\n";
        }
        
        return errorMessage;
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
