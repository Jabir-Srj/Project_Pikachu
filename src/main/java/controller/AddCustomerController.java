package controller;

import java.time.LocalDate;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import model.Customer;
import service.UserService;
import util.NavigationManager;
import util.ServiceLocator;

public class AddCustomerController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private DatePicker dobPicker;
    @FXML private TextField addressField;
    @FXML private Button submitButton;
    @FXML private Button cancelButton;
    @FXML private Button backButton;

    private UserService userService;

    @FXML
    public void initialize() {
        userService = ServiceLocator.getInstance().getUserService();
        submitButton.setOnAction(this::handleSubmit);
        cancelButton.setOnAction(e -> NavigationManager.getInstance().showCustomerManagement());
        backButton.setOnAction(e -> NavigationManager.getInstance().showCustomerManagement());
    }

    private void handleSubmit(ActionEvent event) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        LocalDate dob = dobPicker.getValue();
        String address = addressField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || dob == null || address.isEmpty()) {
            showAlert("Please fill in all fields.");
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            showAlert("Please enter a valid email address.");
            return;
        }
        if (!phone.matches("[0-9+\\-() ]{7,}")) {
            showAlert("Please enter a valid phone number.");
            return;
        }
        // Split name into first and last
        String[] nameParts = name.split(" ", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";
        // Generate username (email prefix) and a default password
        String username = email.split("@")[0];
        String password = "changeme123";
        // Dummy values for passport/nationality (could be added to form later)
        String passportNumber = "N/A";
        String nationality = "N/A";
        Customer customer = new Customer(null, username, password, email, firstName, lastName, phone, passportNumber, nationality);
        boolean success = userService.registerUser(customer);
        if (success) {
            showAlert("Customer added successfully!", Alert.AlertType.INFORMATION);
            NavigationManager.getInstance().showCustomerManagement();
        } else {
            showAlert("A customer with this email or username already exists.");
        }
    }

    private void showAlert(String message) {
        showAlert(message, Alert.AlertType.WARNING);
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Add Customer");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 