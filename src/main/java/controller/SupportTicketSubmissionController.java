package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.Customer;
import model.Ticket;
import model.TicketPriority;
import service.TicketService;
import util.NavigationManager;
import util.ServiceLocator;

public class SupportTicketSubmissionController {
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField subjectField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<String> priorityComboBox;
    @FXML private TextField bookingIdField;
    @FXML private Button submitButton;
    @FXML private Button cancelButton;
    @FXML private Button backButton;

    private TicketService ticketService;
    private Customer currentCustomer;

    @FXML
    public void initialize() {
        ticketService = ServiceLocator.getInstance().getTicketService();
        Object userObj = NavigationManager.getInstance().getSharedData("currentUser");
        if (userObj instanceof Customer) {
            currentCustomer = (Customer) userObj;
        }
        typeComboBox.getItems().addAll("Support", "Refund");
        priorityComboBox.getItems().addAll("Low", "Medium", "High");
        submitButton.setOnAction(e -> handleSubmit());
        cancelButton.setOnAction(e -> NavigationManager.getInstance().showCustomerOverview());
        backButton.setOnAction(e -> NavigationManager.getInstance().showCustomerOverview());
    }

    private void handleSubmit() {
        String type = typeComboBox.getValue();
        String subject = subjectField.getText().trim();
        String description = descriptionArea.getText().trim();
        String priorityStr = priorityComboBox.getValue();
        String bookingId = bookingIdField.getText().trim();

        if (type == null || subject.isEmpty() || description.isEmpty() || priorityStr == null) {
            showAlert("Please fill in all required fields.");
            return;
        }
        if (currentCustomer == null) {
            showAlert("No customer is logged in.");
            return;
        }
        TicketPriority priority = TicketPriority.valueOf(priorityStr.toUpperCase());
        Ticket ticket = ticketService.submitTicket(currentCustomer, subject, description, priority);
        if (ticket != null) {
            ticket.setDescription("[" + type + "] " + description);
            if (!bookingId.isEmpty()) {
                ticket.setSubject(subject + " (Booking: " + bookingId + ")");
            }
            showAlert("Your request has been submitted!", Alert.AlertType.INFORMATION);
            NavigationManager.getInstance().showCustomerOverview();
        } else {
            showAlert("Failed to submit your request. Please try again.");
        }
    }

    private void showAlert(String message) {
        showAlert(message, Alert.AlertType.WARNING);
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Support/Refund Submission");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 