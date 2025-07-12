package controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.RefundRequest;
import model.RefundStatus;
import util.NavigationManager;

/**
 * Controller for RefundApproval.fxml
 * Manages refund request review and approval process
 */
public class RefundController implements Initializable {
    
    // Table Controls
    @FXML private TableView<RefundRequest> refundRequestsTable;
    @FXML private TableColumn<RefundRequest, String> bookingIdColumn;
    @FXML private TableColumn<RefundRequest, String> customerNameColumn;
    @FXML private TableColumn<RefundRequest, String> flightNumberColumn;
    @FXML private TableColumn<RefundRequest, String> refundAmountColumn;
    @FXML private TableColumn<RefundRequest, String> requestDateColumn;
    @FXML private TableColumn<RefundRequest, String> reasonColumn;
    @FXML private TableColumn<RefundRequest, String> statusColumn;
    
    // Action Buttons
    @FXML private Button approveRefundButton;
    @FXML private Button rejectRefundButton;
    @FXML private Button backToDashboardButton;
    
    // Data
    private ObservableList<RefundRequest> refundRequests;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("RefundController: Initializing...");
        setupTableColumns();
        loadSampleRefundRequests();
        setupEventHandlers();
        System.out.println("RefundController: Initialization complete");
    }
    
    private void setupTableColumns() {
        // Set up cell value factories for each column
        bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        customerNameColumn.setCellValueFactory(cellData -> {
            String customerId = cellData.getValue().getCustomerId();
            return new javafx.beans.property.SimpleStringProperty(getCustomerName(customerId));
        });
        flightNumberColumn.setCellValueFactory(cellData -> {
            String bookingId = cellData.getValue().getBookingId();
            return new javafx.beans.property.SimpleStringProperty(getFlightNumber(bookingId));
        });
        refundAmountColumn.setCellValueFactory(cellData -> {
            double amount = cellData.getValue().getRefundAmount();
            return new javafx.beans.property.SimpleStringProperty(String.format("$%.2f", amount));
        });
        requestDateColumn.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getRequestDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            return new javafx.beans.property.SimpleStringProperty(date.format(formatter));
        });
        reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        statusColumn.setCellValueFactory(cellData -> {
            String status = cellData.getValue().getStatus().toString();
            return new javafx.beans.property.SimpleStringProperty(status);
        });
    }
    
    private void loadSampleRefundRequests() {
        refundRequests = FXCollections.observableArrayList();
        
        // Create sample refund requests
        RefundRequest request1 = new RefundRequest("REF001", "PKA240001", "CUST001", 
            "Flight cancelled by airline", 344.99);
        request1.setRequestDate(LocalDateTime.now().minusDays(2));
        
        RefundRequest request2 = new RefundRequest("REF002", "PKA240002", "CUST002", 
            "Medical emergency", 299.50);
        request2.setRequestDate(LocalDateTime.now().minusDays(1));
        
        RefundRequest request3 = new RefundRequest("REF003", "PKA240003", "CUST003", 
            "Change of travel plans", 450.75);
        request3.setRequestDate(LocalDateTime.now().minusHours(8));
        
        RefundRequest request4 = new RefundRequest("REF004", "PKA240004", "CUST004", 
            "Duplicate booking error", 389.25);
        request4.setRequestDate(LocalDateTime.now().minusHours(3));
        
        RefundRequest request5 = new RefundRequest("REF005", "PKA240005", "CUST005", 
            "Weather-related cancellation", 525.00);
        request5.setRequestDate(LocalDateTime.now().minusMinutes(45));
        
        // Add all requests to the list
        refundRequests.addAll(request1, request2, request3, request4, request5);
        
        // Set the table data
        refundRequestsTable.setItems(refundRequests);
        
        System.out.println("Loaded " + refundRequests.size() + " sample refund requests");
    }
    
    private String getCustomerName(String customerId) {
        // Map customer IDs to names (in a real app, this would query the database)
        switch (customerId) {
            case "CUST001": return "John Smith";
            case "CUST002": return "Sarah Johnson";
            case "CUST003": return "Michael Brown";
            case "CUST004": return "Emily Davis";
            case "CUST005": return "David Wilson";
            default: return "Unknown Customer";
        }
    }
    
    private String getFlightNumber(String bookingId) {
        // Map booking IDs to flight numbers (in a real app, this would query the database)
        switch (bookingId) {
            case "PKA240001": return "PKA101";
            case "PKA240002": return "PKA102";
            case "PKA240003": return "PKA103";
            case "PKA240004": return "PKA104";
            case "PKA240005": return "PKA105";
            default: return "Unknown Flight";
        }
    }
    
    private void setupEventHandlers() {
        // Setup button event handlers
        if (approveRefundButton != null) {
            approveRefundButton.setOnAction(event -> handleApproveRefund());
        }
        
        if (rejectRefundButton != null) {
            rejectRefundButton.setOnAction(event -> handleRejectRefund());
        }
        
        if (backToDashboardButton != null) {
            backToDashboardButton.setOnAction(event -> handleBackToDashboard());
        }
    }
    
    @FXML
    private void handleApproveRefund() {
        RefundRequest selectedRequest = refundRequestsTable.getSelectionModel().getSelectedItem();
        if (selectedRequest == null) {
            showAlert("No Selection", "Please select a refund request to approve.", Alert.AlertType.WARNING);
            return;
        }
        
        if (selectedRequest.getStatus() != RefundStatus.PENDING) {
            showAlert("Invalid Action", "Only pending refund requests can be approved.", Alert.AlertType.WARNING);
            return;
        }
        
        selectedRequest.approve("admin");
        refundRequestsTable.refresh();
        
        System.out.println("Approved refund: " + selectedRequest.getRefundId());
        showAlert("Refund Approved", 
            "Refund request " + selectedRequest.getRefundId() + " has been approved successfully.\n" +
            "Amount: $" + String.format("%.2f", selectedRequest.getRefundAmount()), 
            Alert.AlertType.INFORMATION);
    }
    
    @FXML
    private void handleRejectRefund() {
        RefundRequest selectedRequest = refundRequestsTable.getSelectionModel().getSelectedItem();
        if (selectedRequest == null) {
            showAlert("No Selection", "Please select a refund request to reject.", Alert.AlertType.WARNING);
            return;
        }
        
        if (selectedRequest.getStatus() != RefundStatus.PENDING) {
            showAlert("Invalid Action", "Only pending refund requests can be rejected.", Alert.AlertType.WARNING);
            return;
        }
        
        selectedRequest.reject("admin", "Administrative decision");
        refundRequestsTable.refresh();
        
        System.out.println("Rejected refund: " + selectedRequest.getRefundId());
        showAlert("Refund Rejected", 
            "Refund request " + selectedRequest.getRefundId() + " has been rejected.", 
            Alert.AlertType.INFORMATION);
    }
    
    @FXML
    private void handleBackToDashboard() {
        System.out.println("Back to dashboard clicked");
        NavigationManager navigationManager = NavigationManager.getInstance();
        navigationManager.navigateTo(NavigationManager.ADMIN_DASHBOARD);
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
