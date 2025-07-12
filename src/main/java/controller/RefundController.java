package controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import model.Admin;
import model.RefundRequest;
import model.User;
import util.NavigationManager;

/**
 * Controller for RefundApproval.fxml
 * Manages refund request review and approval process
 */
public class RefundController implements Initializable {
    
    // Header Controls
    @FXML private Button backButton;
    @FXML private Button approveButton;
    @FXML private Button rejectButton;
    @FXML private Button printButton;
    
    // Refund Header Information
    @FXML private Text refundIdLabel;
    @FXML private Label statusLabel;
    @FXML private Text requestDateLabel;
    @FXML private Text refundAmountLabel;
    
    // Customer Information
    @FXML private Text customerNameLabel;
    @FXML private Text customerIdLabel;
    @FXML private Text emailLabel;
    @FXML private Text phoneLabel;
    
    // Booking Information
    @FXML private Text bookingReferenceLabel;
    @FXML private Text flightNumberLabel;
    @FXML private Text routeLabel;
    @FXML private Text departureDateLabel;
    @FXML private Text passengersLabel;
    @FXML private Text originalAmountLabel;
    
    // Refund Reason
    @FXML private Text reasonCategoryLabel;
    @FXML private TextArea customerExplanationArea;
    
    // Supporting Documents
    @FXML private ListView<String> documentsListView;
    @FXML private Button viewDocumentButton;
    @FXML private Button downloadAllButton;
    
    // Admin Review Section
    @FXML private TextArea reviewNotesArea;
    @FXML private TextArea approvalNotesArea;
    @FXML private TextArea rejectReasonArea;
    @FXML private ComboBox<String> refundMethodComboBox;
    @FXML private ComboBox<String> processingTimeComboBox;
    @FXML private Button saveReviewButton;
    @FXML private Button rejectRefundButton;
    @FXML private Button approveRefundButton;
    
    // Services and Data
    private NavigationManager navigationManager;
    private RefundRequest currentRefund;
    private User currentUser;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        navigationManager = NavigationManager.getInstance();
        currentUser = (User) navigationManager.getSharedData("currentUser");
        
        setupDropdowns();
        setupEventHandlers();
        loadRefundData();
    }
    
    /**
     * Setup dropdown options
     */
    private void setupDropdowns() {
        if (refundMethodComboBox != null) {
            refundMethodComboBox.setItems(FXCollections.observableArrayList(
                "Original Payment Method", "Bank Transfer", "Check", "Store Credit"
            ));
            refundMethodComboBox.setValue("Original Payment Method");
        }
        
        if (processingTimeComboBox != null) {
            processingTimeComboBox.setItems(FXCollections.observableArrayList(
                "3-5 Business Days", "5-7 Business Days", "7-10 Business Days", "10-14 Business Days"
            ));
            processingTimeComboBox.setValue("5-7 Business Days");
        }
    }
    
    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        if (backButton != null) {
            backButton.setOnAction(e -> handleBackToRefunds());
        }
        if (approveButton != null) {
            approveButton.setOnAction(e -> handleApproveRefund());
        }
        if (rejectButton != null) {
            rejectButton.setOnAction(e -> handleRejectRefund());
        }
        if (printButton != null) {
            printButton.setOnAction(e -> handlePrintRefund());
        }
        if (viewDocumentButton != null) {
            viewDocumentButton.setOnAction(e -> handleViewDocument());
        }
        if (downloadAllButton != null) {
            downloadAllButton.setOnAction(e -> handleDownloadAll());
        }
        if (saveReviewButton != null) {
            saveReviewButton.setOnAction(e -> handleSaveReview());
        }
        if (approveRefundButton != null) {
            approveRefundButton.setOnAction(e -> handleApproveRefund());
        }
        if (rejectRefundButton != null) {
            rejectRefundButton.setOnAction(e -> handleRejectRefund());
        }
    }
    
    /**
     * Load refund data from service or show sample data
     */
    private void loadRefundData() {
        loadSampleRefundData();
    }
    
    /**
     * Load sample refund data for demonstration
     */
    private void loadSampleRefundData() {
        // Refund header
        if (refundIdLabel != null) {
            refundIdLabel.setText("REF-2024-001234");
        }
        if (statusLabel != null) {
            statusLabel.setText("PENDING REVIEW");
            updateStatusStyle("PENDING");
        }
        if (requestDateLabel != null) {
            requestDateLabel.setText(LocalDateTime.now().minusDays(2).format(
                DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
        }
        if (refundAmountLabel != null) {
            refundAmountLabel.setText("$1,247.50");
        }
        
        // Customer information
        if (customerNameLabel != null) {
            customerNameLabel.setText("John Smith");
        }
        if (customerIdLabel != null) {
            customerIdLabel.setText("CUST001");
        }
        if (emailLabel != null) {
            emailLabel.setText("john.smith@email.com");
        }
        if (phoneLabel != null) {
            phoneLabel.setText("+1 (555) 123-4567");
        }
        
        // Booking information
        if (bookingReferenceLabel != null) {
            bookingReferenceLabel.setText("PKA240001");
        }
        if (flightNumberLabel != null) {
            flightNumberLabel.setText("PKA101");
        }
        if (routeLabel != null) {
            routeLabel.setText("New York (JFK) → Los Angeles (LAX)");
        }
        if (departureDateLabel != null) {
            departureDateLabel.setText(LocalDateTime.now().plusDays(5).format(
                DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
        }
        if (passengersLabel != null) {
            passengersLabel.setText("1 Passenger");
        }
        if (originalAmountLabel != null) {
            originalAmountLabel.setText("$1,498.00");
        }
        
        // Refund details
        if (reasonCategoryLabel != null) {
            reasonCategoryLabel.setText("Flight Cancellation by Airline");
        }
        if (customerExplanationArea != null) {
            customerExplanationArea.setText("Flight PKA101 was cancelled due to technical issues. " +
                "I was offered rebooking but the alternative flights don't work with my schedule. " +
                "Requesting full refund as per airline policy for technical cancellations.");
        }
        
        // Documents
        if (documentsListView != null) {
            documentsListView.setItems(FXCollections.observableArrayList(
                "Booking Confirmation - PKA240001.pdf",
                "Flight Cancellation Notice.pdf",
                "Customer ID Copy.jpg",
                "Payment Receipt.pdf"
            ));
        }
    }
    
    /**
     * Update status label styling
     */
    private void updateStatusStyle(String status) {
        if (statusLabel != null) {
            statusLabel.getStyleClass().removeAll("status-pending", "status-approved", "status-rejected");
            switch (status.toUpperCase()) {
                case "PENDING" -> statusLabel.getStyleClass().add("status-pending");
                case "APPROVED" -> statusLabel.getStyleClass().add("status-approved"); 
                case "REJECTED" -> statusLabel.getStyleClass().add("status-rejected");
            }
        }
    }
    
    /**
     * Handle back to refunds
     */
    @FXML
    private void handleBackToRefunds() {
        if (currentUser instanceof Admin) {
            navigationManager.showAdminDashboard();
        } else {
            navigationManager.showCustomerOverview();
        }
    }
    
    /**
     * Handle approve refund
     */
    @FXML
    private void handleApproveRefund() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Approve Refund");
        confirmAlert.setHeaderText("Approve this refund request?");
        confirmAlert.setContentText("This will process the refund and cannot be undone.");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                showAlert("Success", "Refund approved successfully!");
                if (statusLabel != null) {
                    statusLabel.setText("APPROVED");
                    updateStatusStyle("APPROVED");
                }
                disableActionButtons();
            }
        });
    }
    
    /**
     * Handle reject refund
     */
    @FXML
    private void handleRejectRefund() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Reject Refund");
        confirmAlert.setHeaderText("Reject this refund request?");
        confirmAlert.setContentText("Please provide a reason for rejection.");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                showAlert("Success", "Refund rejected.");
                if (statusLabel != null) {
                    statusLabel.setText("REJECTED");
                    updateStatusStyle("REJECTED");
                }
                disableActionButtons();
            }
        });
    }
    
    /**
     * Handle print refund
     */
    @FXML
    private void handlePrintRefund() {
        showAlert("Info", "Refund report has been sent to the printer.");
    }
    
    /**
     * Handle view document
     */
    @FXML
    private void handleViewDocument() {
        String selectedDocument = documentsListView.getSelectionModel().getSelectedItem();
        if (selectedDocument != null) {
            showAlert("Info", "Opening document: " + selectedDocument);
        } else {
            showAlert("Info", "Please select a document to view.");
        }
    }
    
    /**
     * Handle download all documents
     */
    @FXML
    private void handleDownloadAll() {
        showAlert("Info", "All supporting documents have been downloaded to your Downloads folder.");
    }
    
    /**
     * Handle save review
     */
    @FXML
    private void handleSaveReview() {
        if (reviewNotesArea != null && reviewNotesArea.getText().trim().isEmpty()) {
            showAlert("Error", "Please enter review notes before saving.");
            return;
        }
        
        showAlert("Success", "Review notes have been saved successfully.");
    }
    
    /**
     * Disable action buttons after approval/rejection
     */
    private void disableActionButtons() {
        if (approveButton != null) approveButton.setDisable(true);
        if (rejectButton != null) rejectButton.setDisable(true);
        if (approveRefundButton != null) approveRefundButton.setDisable(true);
        if (rejectRefundButton != null) rejectRefundButton.setDisable(true);
        if (reviewNotesArea != null) reviewNotesArea.setEditable(false);
        if (refundMethodComboBox != null) refundMethodComboBox.setDisable(true);
        if (processingTimeComboBox != null) processingTimeComboBox.setDisable(true);
    }
    
    /**
     * Show alert dialog
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
