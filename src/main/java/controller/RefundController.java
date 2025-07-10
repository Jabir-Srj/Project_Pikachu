package controller;

import java.net.URL;
import java.util.Arrays;
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
    
    // Supporting Documents
    @FXML private ListView<String> documentsListView;
    @FXML private Button viewDocumentButton;
    @FXML private Button downloadAllButton;
    
    // Admin Review Section
    @FXML private TextArea reviewNotesArea;
    @FXML private ComboBox<String> refundMethodComboBox;
    @FXML private ComboBox<String> processingTimeComboBox;
    @FXML private Button saveReviewButton;
    @FXML private Button rejectRefundButton;
    @FXML private Button approveRefundButton;
    
    // Data
    private User currentUser;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentUser = (User) NavigationManager.getInstance().getSharedData("currentUser");
        
        setupDropdowns();
        setupEventHandlers();
        loadSampleData();
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
     * Load sample refund data
     */
    private void loadSampleData() {
        if (refundIdLabel != null) {
            refundIdLabel.setText("REF-2024-001234");
        }
        
        if (statusLabel != null) {
            statusLabel.setText("PENDING REVIEW");
            statusLabel.setStyle("-fx-background-color: #f39c12; -fx-background-radius: 15; -fx-text-fill: white; -fx-padding: 5 15;");
        }
        
        if (requestDateLabel != null) {
            requestDateLabel.setText("March 20, 2024");
        }
        
        if (refundAmountLabel != null) {
            refundAmountLabel.setText("$1,247.50");
        }
        
        if (documentsListView != null) {
            documentsListView.setItems(FXCollections.observableArrayList(Arrays.asList(
                "📄 Booking Confirmation - BK-2024-001234.pdf",
                "🧾 Payment Receipt - PAY-2024-001234.pdf", 
                "✉️ Flight Cancellation Notice - AA-1234.pdf",
                "📋 Customer ID Verification.pdf"
            )));
        }
    }
    
    /**
     * Handle back to refunds
     */
    @FXML
    private void handleBackToRefunds() {
        if (currentUser instanceof Admin) {
            NavigationManager.getInstance().showAdminDashboard();
        } else {
            NavigationManager.getInstance().showCustomerOverview();
        }
    }
    
    /**
     * Handle approve refund
     */
    @FXML
    private void handleApproveRefund() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Approve Refund");
        confirmation.setHeaderText("Approve this refund request?");
        confirmation.setContentText("Refund amount: " + refundAmountLabel.getText());
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (statusLabel != null) {
                    statusLabel.setText("APPROVED");
                    statusLabel.setStyle("-fx-background-color: #27ae60; -fx-background-radius: 15; -fx-text-fill: white; -fx-padding: 5 15;");
                }
                
                showAlert("Success", "Refund request has been approved successfully!");
                disableActionButtons();
            }
        });
    }
    
    /**
     * Handle reject refund
     */
    @FXML
    private void handleRejectRefund() {
        if (reviewNotesArea != null && reviewNotesArea.getText().trim().isEmpty()) {
            showAlert("Error", "Please provide rejection reason in the review notes.");
            return;
        }
        
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Reject Refund");
        confirmation.setHeaderText("Reject this refund request?");
        confirmation.setContentText("This action cannot be undone.");
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (statusLabel != null) {
                    statusLabel.setText("REJECTED");
                    statusLabel.setStyle("-fx-background-color: #e74c3c; -fx-background-radius: 15; -fx-text-fill: white; -fx-padding: 5 15;");
                }
                
                showAlert("Success", "Refund request has been rejected.");
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