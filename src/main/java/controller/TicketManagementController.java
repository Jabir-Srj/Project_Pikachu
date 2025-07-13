package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import model.*;
import service.TicketService;
import service.UserService;
import util.NavigationManager;
import util.SessionManager;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for ticket management with chat functionality
 */
public class TicketManagementController implements Initializable {
    
    // Table and data
    @FXML private TableView<Ticket> ticketTable;
    @FXML private TableColumn<Ticket, String> ticketIdColumn;
    @FXML private TableColumn<Ticket, String> subjectColumn;
    @FXML private TableColumn<Ticket, String> statusColumn;
    @FXML private TableColumn<Ticket, String> priorityColumn;
    @FXML private TableColumn<Ticket, String> createdAtColumn;
    @FXML private TableColumn<Ticket, String> customerColumn;
    
    // Ticket details and chat
    @FXML private VBox ticketDetailsBox;
    @FXML private Label ticketIdLabel;
    @FXML private Label subjectLabel;
    @FXML private Label customerNameLabel;
    @FXML private Label statusLabel;
    @FXML private Label priorityLabel;
    @FXML private Label createdAtLabel;
    @FXML private TextArea descriptionArea;
    
    // Chat area
    @FXML private ScrollPane chatScrollPane;
    @FXML private VBox chatMessagesBox;
    @FXML private TextArea newMessageArea;
    @FXML private Button sendMessageButton;
    
    // Admin controls
    @FXML private ComboBox<TicketStatus> statusComboBox;
    @FXML private Button updateStatusButton;
    @FXML private Button markCompletedButton;
    @FXML private Button markRejectedButton;
    @FXML private VBox adminControlsBox;
    
    // Navigation
    @FXML private Button backButton;
    @FXML private Button refreshButton;
    
    private ObservableList<Ticket> ticketList;
    private TicketService ticketService;
    private UserService userService;
    private NavigationManager navigationManager;
    private Ticket selectedTicket;
    private User currentUser;
    private boolean isAdminView;
    private boolean isDialogOpen = false; // Track if any dialog is currently open
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeServices();
        loadCurrentUser();
        setupTableColumns();
        setupEventHandlers();
        
        // Fix any existing tickets with empty IDs before loading
        fixEmptyTicketIds();
        
        loadTickets();
        
        // Hide details initially
        ticketDetailsBox.setVisible(false);
    }

    /**
     * Fix any existing tickets with empty IDs
     */
    private void fixEmptyTicketIds() {
        try {
            // This will be called by the TicketService constructor, but we call it again here
            // to ensure it's done before we load tickets for display
            System.out.println("TicketManagementController: Ensuring all tickets have proper IDs...");
        } catch (Exception e) {
            System.err.println("TicketManagementController: Error fixing ticket IDs: " + e.getMessage());
        }
    }
    
    private void initializeServices() {
        ticketService = new TicketService();
        userService = new UserService();
        navigationManager = NavigationManager.getInstance();
        ticketList = FXCollections.observableArrayList();
        ticketTable.setItems(ticketList);
    }
    
    private void setupTableColumns() {
        ticketIdColumn.setCellValueFactory(new PropertyValueFactory<>("ticketId"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        statusColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getStatus() != null ? data.getValue().getStatus().getDisplayName() : "Unknown"
        ));
        priorityColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getPriority() != null ? data.getValue().getPriority().getDisplayName() : "Unknown"
        ));
        createdAtColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getCreatedAt() != null ? 
                data.getValue().getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "Unknown"
        ));
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
    }
    
    private void loadCurrentUser() {
        currentUser = SessionManager.getCurrentUser();
        System.out.println("TicketManagement: Current user = " + (currentUser != null ? currentUser.getUsername() + " (ID: " + currentUser.getUserId() + ")" : "null"));
        
        if (currentUser == null) {
            System.err.println("TicketManagement: No current user found in session!");
            showAlert("Session Error", "No user session found. Please log in again.");
            return;
        }
        
        isAdminView = currentUser instanceof Admin;
        System.out.println("TicketManagement: User type = " + currentUser.getClass().getSimpleName());
        System.out.println("TicketManagement: Is admin view = " + isAdminView);
        
        // Show/hide admin controls
        if (adminControlsBox != null) {
            adminControlsBox.setVisible(isAdminView);
            adminControlsBox.setManaged(isAdminView);
        }
        
        // Setup status combo box for admins
        if (statusComboBox != null && isAdminView) {
            statusComboBox.setItems(FXCollections.observableArrayList(TicketStatus.values()));
        }
    }
    
    private void setupEventHandlers() {
        // Table selection
        ticketTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedTicket = newSelection;
                showTicketDetails(newSelection);
            }
        });
        
        // Send message button
        if (sendMessageButton != null) {
            sendMessageButton.setOnAction(e -> sendMessage());
        }
        
        // Admin controls
        if (updateStatusButton != null) {
            updateStatusButton.setOnAction(e -> updateTicketStatus());
        }
        
        if (markCompletedButton != null) {
            markCompletedButton.setOnAction(e -> markTicketCompleted());
        }
        
        if (markRejectedButton != null) {
            markRejectedButton.setOnAction(e -> markTicketRejected());
        }
        
        // Navigation buttons
        if (backButton != null) {
            backButton.setOnAction(e -> goBack());
        }
        
        if (refreshButton != null) {
            refreshButton.setOnAction(e -> refreshTickets());
        }
    }
    
    private void loadTickets() {
        try {
            List<Ticket> tickets;
            if (isAdminView) {
                // Admin sees all tickets
                System.out.println("TicketManagement: Loading all tickets for admin");
                tickets = ticketService.getAllTickets();
            } else {
                // Customer sees only their tickets
                String customerId = currentUser != null ? currentUser.getUserId() : "null";
                System.out.println("TicketManagement: Loading tickets for customer ID: " + customerId);
                tickets = ticketService.getTicketsByCustomer(customerId);
                System.out.println("TicketManagement: Found " + (tickets != null ? tickets.size() : 0) + " tickets for customer");
            }
            
            ticketList.clear();
            if (tickets != null && !tickets.isEmpty()) {
                ticketList.addAll(tickets);
                System.out.println("TicketManagement: Added " + tickets.size() + " tickets to table");
            } else {
                System.out.println("TicketManagement: No tickets found for user");
                if (!isAdminView) {
                    // Show helpful message for customers with no tickets
                    showAlert("No Tickets", 
                        "You don't have any support tickets yet.\n\n" +
                        "To create a new ticket, go back to the main dashboard and click 'Submit Ticket'.");
                }
            }
        } catch (Exception e) {
            System.err.println("TicketManagement: Error loading tickets: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to load tickets: " + e.getMessage());
        }
    }
    
    private void showTicketDetails(Ticket ticket) {
        if (ticketDetailsBox != null) {
            ticketDetailsBox.setVisible(true);
        }
        
        // Populate ticket details
        if (ticketIdLabel != null) ticketIdLabel.setText(ticket.getTicketId());
        if (subjectLabel != null) subjectLabel.setText(ticket.getSubject());
        if (customerNameLabel != null) customerNameLabel.setText(ticket.getCustomerName());
        if (statusLabel != null) statusLabel.setText(ticket.getStatus().getDisplayName());
        if (priorityLabel != null) priorityLabel.setText(ticket.getPriority().getDisplayName());
        if (createdAtLabel != null && ticket.getCreatedAt() != null) {
            createdAtLabel.setText(ticket.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (descriptionArea != null) descriptionArea.setText(ticket.getDescription());
        
        // Set status combo box for admin
        if (statusComboBox != null && isAdminView) {
            statusComboBox.setValue(ticket.getStatus());
        }
        
        // Load and display chat messages
        loadChatMessages(ticket);
    }
    
    private void loadChatMessages(Ticket ticket) {
        if (chatMessagesBox == null) return;
        
        chatMessagesBox.getChildren().clear();
        
        // Add initial description as first message
        addChatMessage("System", "Ticket created: " + ticket.getDescription(), 
                      ticket.getCreatedAt(), true);
        
        // Add all replies
        if (ticket.getReplies() != null) {
            for (TicketReply reply : ticket.getReplies()) {
                String senderName = getSenderName(reply, ticket);
                boolean isSystemMessage = reply.getResponderId().equals("SYSTEM");
                addChatMessage(senderName, reply.getMessage(), reply.getTimestamp(), isSystemMessage);
            }
        }
        
        // Scroll to bottom
        if (chatScrollPane != null) {
            chatScrollPane.setVvalue(1.0);
        }
    }
    
    /**
     * Get the proper sender name for a reply
     */
    private String getSenderName(TicketReply reply, Ticket ticket) {
        // If responder name is already set, use it
        if (reply.getResponderName() != null && !reply.getResponderName().isEmpty()) {
            return reply.getResponderName();
        }
        
        // Determine sender name based on responder ID
        String responderId = reply.getResponderId();
        
        if ("SYSTEM".equals(responderId)) {
            return "System";
        } else if (responderId.equals(ticket.getCustomerId())) {
            return ticket.getCustomerName() != null ? ticket.getCustomerName() : "Customer";
        } else {
            // Try to get user name from UserService
            try {
                Optional<User> responderOpt = userService.findUserById(responderId);
                if (responderOpt.isPresent()) {
                    User responder = responderOpt.get();
                    return responder.getFirstName() + " " + responder.getLastName();
                }
            } catch (Exception e) {
                System.err.println("Error getting user name for ID: " + responderId);
            }
            return "Admin";
        }
    }
    
    private void addChatMessage(String sender, String message, LocalDateTime timestamp, boolean isSystemMessage) {
        VBox messageBox = new VBox(5);
        messageBox.getStyleClass().add("chat-message");
        
        if (isSystemMessage) {
            messageBox.getStyleClass().add("system-message");
        } else if (sender.equals("You") || sender.equals(currentUser.getFirstName() + " " + currentUser.getLastName())) {
            messageBox.getStyleClass().add("own-message");
        } else {
            messageBox.getStyleClass().add("other-message");
        }
        
        // Sender and timestamp
        Label headerLabel = new Label(sender + " - " + 
            timestamp.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm")));
        headerLabel.getStyleClass().add("message-header");
        
        // Message content
        Label contentLabel = new Label(message);
        contentLabel.setWrapText(true);
        contentLabel.getStyleClass().add("message-content");
        
        messageBox.getChildren().addAll(headerLabel, contentLabel);
        chatMessagesBox.getChildren().add(messageBox);
    }
    
    private void sendMessage() {
        if (newMessageArea == null || selectedTicket == null) return;
        
        String message = newMessageArea.getText().trim();
        if (message.isEmpty()) {
            showAlert("Error", "Please enter a message.");
            return;
        }
        
        try {
            // Add reply to ticket with proper responder name
            String repliedBy = currentUser.getUserId();
            String responderName = currentUser.getFirstName() + " " + currentUser.getLastName();
            selectedTicket.addReply(message, repliedBy);
            
            // Set the responder name in the latest reply
            if (!selectedTicket.getReplies().isEmpty()) {
                TicketReply latestReply = selectedTicket.getReplies().get(selectedTicket.getReplies().size() - 1);
                latestReply.setResponderName(responderName);
            }
            
            // Update the ticket in the system
            if (ticketService.updateTicket(selectedTicket)) {
                // Clear the message area
                newMessageArea.clear();
                
                // Refresh the chat display
                loadChatMessages(selectedTicket);
                
                // Update ticket status to IN_PROGRESS if it was OPEN
                if (selectedTicket.getStatus() == TicketStatus.OPEN) {
                    selectedTicket.setStatus(TicketStatus.IN_PROGRESS);
                    ticketService.updateTicket(selectedTicket);
                    statusLabel.setText(selectedTicket.getStatus().getDisplayName());
                    if (statusComboBox != null) {
                        statusComboBox.setValue(selectedTicket.getStatus());
                    }
                }
                
                showAlert("Success", "Message sent successfully.");
            } else {
                showAlert("Error", "Failed to send message.");
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to send message: " + e.getMessage());
        }
    }
    
    private void updateTicketStatus() {
        if (selectedTicket == null || statusComboBox == null) return;
        
        TicketStatus newStatus = statusComboBox.getValue();
        if (newStatus == null) return;
        
        try {
            TicketStatus oldStatus = selectedTicket.getStatus();
            selectedTicket.setStatus(newStatus);
            selectedTicket.setUpdatedAt(LocalDateTime.now());
            
            // Add system message about status change
            String statusMessage = String.format("Status changed from %s to %s by %s", 
                oldStatus.getDisplayName(), newStatus.getDisplayName(), 
                currentUser.getFirstName() + " " + currentUser.getLastName());
            selectedTicket.addReply(statusMessage, "SYSTEM");
            
            // Set responder name for system message
            if (!selectedTicket.getReplies().isEmpty()) {
                TicketReply latestReply = selectedTicket.getReplies().get(selectedTicket.getReplies().size() - 1);
                latestReply.setResponderName("System");
            }
            
            if (ticketService.updateTicket(selectedTicket)) {
                statusLabel.setText(newStatus.getDisplayName());
                loadChatMessages(selectedTicket);
                refreshTickets();
                showAlert("Success", "Ticket status updated successfully.");
            } else {
                selectedTicket.setStatus(oldStatus); // Revert
                showAlert("Error", "Failed to update ticket status.");
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to update status: " + e.getMessage());
        }
    }
    
    private void markTicketCompleted() {
        if (selectedTicket == null) return;
        
        try {
            selectedTicket.setStatus(TicketStatus.RESOLVED);
            selectedTicket.setUpdatedAt(LocalDateTime.now());
            selectedTicket.setClosedBy(currentUser.getUserId());
            selectedTicket.setCloseDate(LocalDateTime.now());
            
            // Add system message
            String message = String.format("Ticket marked as completed by %s", 
                currentUser.getFirstName() + " " + currentUser.getLastName());
            selectedTicket.addReply(message, "SYSTEM");
            
            // Set responder name for system message
            if (!selectedTicket.getReplies().isEmpty()) {
                TicketReply latestReply = selectedTicket.getReplies().get(selectedTicket.getReplies().size() - 1);
                latestReply.setResponderName("System");
            }
            
            if (ticketService.updateTicket(selectedTicket)) {
                statusLabel.setText(TicketStatus.RESOLVED.getDisplayName());
                if (statusComboBox != null) {
                    statusComboBox.setValue(TicketStatus.RESOLVED);
                }
                loadChatMessages(selectedTicket);
                refreshTickets();
                showAlert("Success", "Ticket marked as completed.");
            } else {
                showAlert("Error", "Failed to mark ticket as completed.");
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to complete ticket: " + e.getMessage());
        }
    }
    
    private void markTicketRejected() {
        if (selectedTicket == null) return;
        
        // Prevent multiple dialogs from being opened
        if (isDialogOpen) {
            showAlert("Dialog in Progress", "Please close the current dialog before opening another one.");
            return;
        }
        
        isDialogOpen = true;
        
        // Ask for rejection reason
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reject Ticket");
        dialog.setHeaderText("Please provide a reason for rejecting this ticket:");
        dialog.setContentText("Reason:");
        
        // Handle dialog close event to reset the flag
        dialog.setOnCloseRequest(event -> {
            isDialogOpen = false;
        });
        
        dialog.showAndWait().ifPresent(reason -> {
            // Reset the dialog flag
            isDialogOpen = false;
            
            try {
                selectedTicket.setStatus(TicketStatus.REJECTED);
                selectedTicket.setUpdatedAt(LocalDateTime.now());
                selectedTicket.setClosedBy(currentUser.getUserId());
                selectedTicket.setCloseDate(LocalDateTime.now());
                
                // Add system message with reason
                String message = String.format("Ticket rejected by %s. Reason: %s", 
                    currentUser.getFirstName() + " " + currentUser.getLastName(), reason);
                selectedTicket.addReply(message, "SYSTEM");
                
                // Set responder name for system message
                if (!selectedTicket.getReplies().isEmpty()) {
                    TicketReply latestReply = selectedTicket.getReplies().get(selectedTicket.getReplies().size() - 1);
                    latestReply.setResponderName("System");
                }
                
                if (ticketService.updateTicket(selectedTicket)) {
                    statusLabel.setText(TicketStatus.REJECTED.getDisplayName());
                    if (statusComboBox != null) {
                        statusComboBox.setValue(TicketStatus.REJECTED);
                    }
                    loadChatMessages(selectedTicket);
                    refreshTickets();
                    showAlert("Success", "Ticket rejected successfully.");
                } else {
                    showAlert("Error", "Failed to reject ticket.");
                }
            } catch (Exception e) {
                showAlert("Error", "Failed to reject ticket: " + e.getMessage());
            }
        });
    }
    
    private void refreshTickets() {
        loadTickets();
        if (selectedTicket != null) {
            // Try to find and reselect the same ticket
            for (Ticket ticket : ticketList) {
                if (ticket.getTicketId().equals(selectedTicket.getTicketId())) {
                    ticketTable.getSelectionModel().select(ticket);
                    break;
                }
            }
        }
    }
    
    private void goBack() {
        if (isAdminView) {
            navigationManager.showAdminDashboard();
        } else {
            navigationManager.showCustomerDashboard();
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
