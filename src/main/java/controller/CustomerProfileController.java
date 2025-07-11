package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import model.Customer;
import service.UserService;
import util.NavigationManager;
import util.ServiceLocator;

public class CustomerProfileController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private DatePicker dobPicker;
    @FXML private TextField addressField;
    @FXML private Button editButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Button backButton;

    private Customer currentCustomer;
    private UserService userService;

    @FXML
    public void initialize() {
        userService = ServiceLocator.getInstance().getUserService();
        Object userObj = NavigationManager.getInstance().getSharedData("currentUser");
        if (userObj instanceof Customer) {
            currentCustomer = (Customer) userObj;
        }
        loadCustomerData();
        setFieldsEditable(false);
        editButton.setOnAction(e -> setFieldsEditable(true));
        saveButton.setOnAction(e -> saveProfile());
        cancelButton.setOnAction(e -> {
            setFieldsEditable(false);
            loadCustomerData();
        });
        backButton.setOnAction(e -> NavigationManager.getInstance().showCustomerOverview());
    }

    private void setFieldsEditable(boolean editable) {
        nameField.setEditable(editable);
        emailField.setEditable(editable);
        phoneField.setEditable(editable);
        dobPicker.setDisable(!editable);
        addressField.setEditable(editable);
        saveButton.setDisable(!editable);
    }

    private void loadCustomerData() {
        if (currentCustomer != null) {
            nameField.setText(currentCustomer.getFirstName() + " " + currentCustomer.getLastName());
            emailField.setText(currentCustomer.getEmail());
            phoneField.setText(currentCustomer.getPhoneNumber());
            // dobPicker.setValue(...); // If DOB is available in Customer
            // addressField.setText(...); // If address is available in Customer
        }
    }

    private void saveProfile() {
        if (currentCustomer == null) {
            showAlert("No customer is logged in.", Alert.AlertType.ERROR);
            return;
        }
        String[] nameParts = nameField.getText().trim().split(" ", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        // LocalDate dob = dobPicker.getValue();
        // String address = addressField.getText().trim();
        currentCustomer.setFirstName(firstName);
        currentCustomer.setLastName(lastName);
        currentCustomer.setEmail(email);
        currentCustomer.setPhoneNumber(phone);
        // Set DOB/address if available in Customer
        boolean success = userService.updateUserProfile(currentCustomer);
        if (success) {
            showAlert("Profile updated!", Alert.AlertType.INFORMATION);
            setFieldsEditable(false);
        } else {
            showAlert("Failed to update profile. Please try again.", Alert.AlertType.ERROR);
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