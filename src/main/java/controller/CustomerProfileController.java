package controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import model.Customer;
import model.User;
import service.UserService;
import util.NavigationManager;
import util.ServiceLocator;

import java.net.URL;
import java.util.ResourceBundle;

public class CustomerProfileController implements Initializable {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private DatePicker dobPicker;
    @FXML private TextField addressField;
    @FXML private TextField customerIdField;
    @FXML private TextField memberSinceField;
    @FXML private TextField accountStatusField;
    @FXML private Button editButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Button backButton;

    private User currentUser;
    private UserService userService;
    private NavigationManager navigationManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            userService = ServiceLocator.getInstance().getUserService();
        } catch (Exception e) {
            System.err.println("Warning: UserService not available: " + e.getMessage());
        }
        
        navigationManager = NavigationManager.getInstance();
        currentUser = (User) navigationManager.getSharedData("currentUser");
        
        loadCustomerData();
        setFieldsEditable(false);
        setupEventHandlers();
    }
    
    private void setupEventHandlers() {
        editButton.setOnAction(e -> setFieldsEditable(true));
        saveButton.setOnAction(e -> saveProfile());
        cancelButton.setOnAction(e -> {
            setFieldsEditable(false);
            loadCustomerData();
        });
        backButton.setOnAction(e -> navigationManager.showCustomerOverview());
    }

    private void setFieldsEditable(boolean editable) {
        nameField.setEditable(editable);
        emailField.setEditable(editable);
        phoneField.setEditable(editable);
        dobPicker.setDisable(!editable);
        addressField.setEditable(editable);
        saveButton.setDisable(!editable);
        editButton.setDisable(editable);
    }

    private void loadCustomerData() {
        if (currentUser != null) {
            // Load basic user information
            nameField.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
            emailField.setText(currentUser.getEmail());
            phoneField.setText(currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "");
            
            // Load account information
            customerIdField.setText(currentUser.getUserId());
            memberSinceField.setText("January 2024"); // Sample data
            accountStatusField.setText("Active");
            
            // Load sample additional information
            addressField.setText("123 Main Street, New York, NY 10001"); // Sample address
            dobPicker.setValue(LocalDate.of(1990, 5, 15)); // Sample DOB
        } else {
            loadSampleData();
        }
    }
    
    private void loadSampleData() {
        // Load sample customer data for demonstration
        nameField.setText("John Smith");
        emailField.setText("john.smith@email.com");
        phoneField.setText("+1 (555) 123-4567");
        addressField.setText("123 Main Street, New York, NY 10001");
        dobPicker.setValue(LocalDate.of(1990, 5, 15));
        
        // Account information
        customerIdField.setText("CUST001");
        memberSinceField.setText("January 2024");
        accountStatusField.setText("Active");
    }

    private void saveProfile() {
        if (currentUser == null) {
            showAlert("No user is logged in.", Alert.AlertType.ERROR);
            return;
        }
        
        try {
            // Parse name
            String[] nameParts = nameField.getText().trim().split(" ", 2);
            String firstName = nameParts[0];
            String lastName = nameParts.length > 1 ? nameParts[1] : "";
            
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            
            // Update user information
            currentUser.setFirstName(firstName);
            currentUser.setLastName(lastName);
            currentUser.setEmail(email);
            currentUser.setPhoneNumber(phone);
            
            // In a real application, you would save to database here
            boolean success = true; // Simulate successful save
            if (userService != null) {
                success = userService.updateUserProfile(currentUser);
            }
            
            if (success) {
                showAlert("Profile updated successfully!", Alert.AlertType.INFORMATION);
                setFieldsEditable(false);
            } else {
                showAlert("Failed to update profile. Please try again.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            showAlert("Error updating profile: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Profile");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 