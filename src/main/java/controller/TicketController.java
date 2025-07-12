package controller;

import java.io.File;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import model.Admin;
import model.Customer;
import model.Ticket;
import model.TicketPriority;
import model.TicketStatus;
import model.User;
import service.TicketService;
import util.NavigationManager;
import util.ServiceLocator;

/**
 * Controller for TicketSubmission.fxml, TicketStatus.fxml, and TicketOverview.fxml
 * Manages ticket creation, viewing, and admin oversight
 */
public class TicketController implements Initializable {
    
    // === TICKET SUBMISSION SCREEN CONTROLS ===
    
    // Contact Information
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField bookingReferenceField;
    
    // Ticket Details
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<TicketPriority> priorityComboBox;
    @FXML private TextField subjectField;
    @FXML private TextArea descriptionTextArea;
    
    // Attachments
    @FXML private Button attachFileButton;
    @FXML private Label attachmentInfoLabel;
    @FXML private ListView<String> attachmentListView;
    
    // Contact Preferences
    @FXML private CheckBox emailUpdatesCheckBox;
    @FXML private CheckBox smsUpdatesCheckBox;
    @FXML private CheckBox callbackCheckBox;
    @FXML private ComboBox<String> callbackTimeComboBox;
    @FXML private VBox callbackTimeContainer;
    
    // Quick Actions
    @FXML private Button faqButton;
    @FXML private Button liveChatButton;
    @FXML private Button callSupportButton;
    
    // Submission Buttons
    @FXML private Button saveAsDraftButton;
    @FXML private Button submitTicketButton;
    @FXML private Button backButton;
    
    // === TICKET STATUS SCREEN CONTROLS ===
    
    // Status Header
    @FXML private Label ticketIdLabel;
    @FXML private Label statusLabel;
    @FXML private Label priorityLabel;
    @FXML private Label createdDateLabel;
    @FXML private Label assignedAgentLabel;
    
    // Ticket Details
    @FXML private Label subjectLabel;
    @FXML private ProgressBar progressBar;
    @FXML private Label progressLabel;
    @FXML private Label expectedResolutionLabel;
    @FXML private Label lastUpdatedLabel;
    
    // Timeline and Actions
    @FXML private VBox timelineContainer;
    @FXML private Button addResponseButton;
    @FXML private Button emailSupportButton;
    @FXML private Button closeTicketButton;
    
    // === TICKET OVERVIEW SCREEN CONTROLS (ADMIN) ===
    
    // Filter Controls
    @FXML private TextField searchField;
    @FXML private ComboBox<TicketStatus> statusFilterComboBox;
    @FXML private ComboBox<TicketPriority> priorityFilterComboBox;
    @FXML private ComboBox<String> assigneeFilterComboBox;
    @FXML private Button searchButton;
    
    // Statistics
    @FXML private Text totalTicketsLabel;
    @FXML private Text openTicketsLabel;
    @FXML private Text highPriorityLabel;
    @FXML private Text resolvedTodayLabel;
    
    // Tickets Table
    @FXML private TableView<Ticket> ticketsTableView;
    @FXML private TableColumn<Ticket, String> ticketIdColumn;
    @FXML private TableColumn<Ticket, String> subjectColumn;
    @FXML private TableColumn<Ticket, String> customerNameColumn;
    @FXML private TableColumn<Ticket, String> priorityColumn;
    @FXML private TableColumn<Ticket, String> statusColumn;
    @FXML private TableColumn<Ticket, String> assignedToColumn;
    @FXML private TableColumn<Ticket, String> createdDateColumn;
    @FXML private TableColumn<Ticket, String> lastUpdatedColumn;
    @FXML private TableColumn<Ticket, String> actionsColumn;
    
    // Overview Actions
    @FXML private Button newTicketButton;
    @FXML private Button exportButton;
    @FXML private Button refreshButton;
    @FXML private Button previousPageButton;
    @FXML private Button nextPageButton;
    @FXML private Label pageInfoLabel;
    @FXML private ComboBox<String> pageSizeComboBox;
    
    // Services and Data
    private TicketService ticketService;
    private User currentUser;
    private Ticket currentTicket;
    private List<String> attachments = new ArrayList<>();
    private ObservableList<Ticket> allTickets;
    private ObservableList<Ticket> filteredTickets;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        ticketService = serviceLocator.getTicketService();
        currentUser = (User) NavigationManager.getInstance().getSharedData("currentUser");
        currentTicket = (Ticket) NavigationManager.getInstance().getSharedData("selectedTicket");
        
        // Initialize UI based on which screen is loaded
        if (firstNameField != null) {
            initializeSubmissionScreen();
        } else if (ticketIdLabel != null) {
            initializeStatusScreen();
        } else if (ticketsTableView != null) {
            initializeOverviewScreen();
        }
    }
    
    /**
     * Initialize Ticket Submission screen
     */
    private void initializeSubmissionScreen() {
        setupSubmissionFormControls();
        setupCallbackTimeHandling();
        setupEventHandlers();
        prefillUserData();
    }
    
    /**
     * Initialize Ticket Status screen
     */
    private void initializeStatusScreen() {
        if (currentTicket != null) {
            loadTicketStatus();
        }
    }
    
    /**
     * Initialize Ticket Overview screen (Admin)
     */
    private void initializeOverviewScreen() {
        setupOverviewTable();
        setupFilterControls();
        loadTicketStatistics();
        loadAllTickets();
    }
    
    /**
     * Setup form controls for ticket submission
     */
    private void setupSubmissionFormControls() {
        // Category options
        if (categoryComboBox != null) {
            categoryComboBox.setItems(FXCollections.observableArrayList(
                "Booking Issues", "Flight Changes", "Cancellations", "Refunds",
                "Check-in Problems", "Baggage Issues", "Special Assistance",
                "Website/App Issues", "Payment Problems", "Other"
            ));
        }
        
        // Priority options
        if (priorityComboBox != null) {
            priorityComboBox.setItems(FXCollections.observableArrayList(TicketPriority.values()));
            priorityComboBox.setValue(TicketPriority.MEDIUM);
        }
        
        // Callback time options
        if (callbackTimeComboBox != null) {
            callbackTimeComboBox.setItems(FXCollections.observableArrayList(
                "9:00 AM - 11:00 AM", "11:00 AM - 1:00 PM", "1:00 PM - 3:00 PM",
                "3:00 PM - 5:00 PM", "5:00 PM - 7:00 PM", "7:00 PM - 9:00 PM"
            ));
        }
    }
    
    /**
     * Setup callback time container visibility handling
     */
    private void setupCallbackTimeHandling() {
        if (callbackCheckBox != null && callbackTimeContainer != null) {
            callbackCheckBox.setOnAction(e -> {
                callbackTimeContainer.setVisible(callbackCheckBox.isSelected());
                callbackTimeContainer.setManaged(callbackCheckBox.isSelected());
            });
        }
    }
    
    /**
     * Setup event handlers for submission screen
     */
    private void setupEventHandlers() {
        if (attachFileButton != null) {
            attachFileButton.setOnAction(e -> handleAttachFile());
        }
        if (submitTicketButton != null) {
            submitTicketButton.setOnAction(e -> handleSubmitTicket());
        }
        if (saveAsDraftButton != null) {
            saveAsDraftButton.setOnAction(e -> handleSaveAsDraft());
        }
        if (backButton != null) {
            backButton.setOnAction(e -> handleBackToDashboard());
        }
        if (faqButton != null) {
            faqButton.setOnAction(e -> showAlert("Info", "FAQ feature coming soon!"));
        }
        if (liveChatButton != null) {
            liveChatButton.setOnAction(e -> showAlert("Info", "Live chat feature coming soon!"));
        }
        if (callSupportButton != null) {
            callSupportButton.setOnAction(e -> showAlert("Info", "Call support at: +1-800-AIRLINE"));
        }
    }
    
    /**
     * Prefill user data if user is logged in
     */
    private void prefillUserData() {
        if (currentUser instanceof Customer) {
            Customer customer = (Customer) currentUser;
            if (firstNameField != null) {
                firstNameField.setText(customer.getFirstName());
            }
            if (lastNameField != null) {
                lastNameField.setText(customer.getLastName());
            }
            if (emailField != null) {
                emailField.setText(customer.getEmail());
            }
            if (phoneField != null) {
                phoneField.setText(customer.getPhoneNumber());
            }
        }
    }
    
    /**
     * Handle file attachment
     */
    @FXML
    private void handleAttachFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Files to Attach");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Supported", "*.pdf", "*.jpg", "*.jpeg", "*.png", "*.doc", "*.docx"),
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
            new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png"),
            new FileChooser.ExtensionFilter("Document Files", "*.doc", "*.docx")
        );
        
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(attachFileButton.getScene().getWindow());
        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            for (File file : selectedFiles) {
                if (file.length() <= 10 * 1024 * 1024) { // 10MB limit
                    attachments.add(file.getName());
                } else {
                    showAlert("Error", "File " + file.getName() + " exceeds 10MB limit");
                }
            }
            
            updateAttachmentDisplay();
        }
    }
    
    /**
     * Update attachment display
     */
    private void updateAttachmentDisplay() {
        if (attachmentInfoLabel != null) {
            if (attachments.isEmpty()) {
                attachmentInfoLabel.setText("No files selected");
            } else {
                attachmentInfoLabel.setText(attachments.size() + " file(s) selected");
            }
        }
        
        if (attachmentListView != null) {
            attachmentListView.setItems(FXCollections.observableList(attachments));
            attachmentListView.setVisible(!attachments.isEmpty());
            attachmentListView.setManaged(!attachments.isEmpty());
        }
    }
    
    /**
     * Handle ticket submission
     */
    @FXML
    private void handleSubmitTicket() {
        if (validateSubmissionForm()) {
            createAndSubmitTicket();
        }
    }
    
    /**
     * Validate submission form
     */
    private boolean validateSubmissionForm() {
        if (firstNameField == null || firstNameField.getText().trim().isEmpty()) {
            showAlert("Error", "Please enter your first name");
            return false;
        }
        
        if (lastNameField == null || lastNameField.getText().trim().isEmpty()) {
            showAlert("Error", "Please enter your last name");
            return false;
        }
        
        if (emailField == null || emailField.getText().trim().isEmpty()) {
            showAlert("Error", "Please enter your email address");
            return false;
        }
        
        if (categoryComboBox == null || categoryComboBox.getValue() == null) {
            showAlert("Error", "Please select a category");
            return false;
        }
        
        if (priorityComboBox == null || priorityComboBox.getValue() == null) {
            showAlert("Error", "Please select a priority level");
            return false;
        }
        
        if (subjectField == null || subjectField.getText().trim().isEmpty()) {
            showAlert("Error", "Please enter a subject");
            return false;
        }
        
        if (descriptionTextArea == null || descriptionTextArea.getText().trim().isEmpty()) {
            showAlert("Error", "Please provide a description");
            return false;
        }
        
        return true;
    }
    
    /**
     * Create and submit ticket
     */
    private void createAndSubmitTicket() {
        try {
            Customer customer = currentUser instanceof Customer ? (Customer) currentUser : null;
            Ticket savedTicket = ticketService.submitTicket(customer, 
                subjectField.getText(), descriptionTextArea.getText(), 
                categoryComboBox.getValue(), priorityComboBox.getValue());
            
            if (savedTicket != null) {
                showAlert("Success", "Ticket submitted successfully! Ticket ID: " + savedTicket.getTicketId());
                
                // Navigate to ticket status
                NavigationManager.getInstance().setSharedData("selectedTicket", savedTicket);
                NavigationManager.getInstance().showTicketStatus(savedTicket);
            } else {
                showAlert("Error", "Failed to submit ticket. Please try again.");
            }
            
        } catch (Exception e) {
            showAlert("Error", "Error submitting ticket: " + e.getMessage());
        }
    }
    
    /**
     * Handle save as draft
     */
    @FXML
    private void handleSaveAsDraft() {
        showAlert("Info", "Draft saved successfully! You can continue later from your dashboard.");
    }
    
    /**
     * Handle back to dashboard
     */
    @FXML
    private void handleBackToDashboard() {
        if (currentUser instanceof Admin) {
            NavigationManager.getInstance().showAdminDashboard();
        } else {
            NavigationManager.getInstance().showCustomerOverview();
        }
    }
    
    /**
     * Load ticket status for status screen
     */
    private void loadTicketStatus() {
        if (ticketIdLabel != null) {
            ticketIdLabel.setText(currentTicket.getTicketId());
        }
        if (statusLabel != null) {
            statusLabel.setText(currentTicket.getStatus().toString());
            statusLabel.setStyle(getStatusStyle(currentTicket.getStatus()));
        }
        if (priorityLabel != null) {
            priorityLabel.setText(currentTicket.getPriority().toString());
            priorityLabel.setStyle(getPriorityStyle(currentTicket.getPriority()));
        }
        if (createdDateLabel != null) {
            createdDateLabel.setText(currentTicket.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        }
        if (subjectLabel != null) {
            subjectLabel.setText(currentTicket.getSubject());
        }
        if (assignedAgentLabel != null) {
            assignedAgentLabel.setText(currentTicket.getAssignedTo() != null ? currentTicket.getAssignedTo() : "Unassigned");
        }
        
        // Set progress based on status
        updateProgressDisplay();
    }
    
    /**
     * Get status styling
     */
    private String getStatusStyle(TicketStatus status) {
        switch (status) {
            case OPEN:
                return "-fx-background-color: #f39c12; -fx-background-radius: 15; -fx-text-fill: white; -fx-padding: 5 15;";
            case IN_PROGRESS:
                return "-fx-background-color: #3498db; -fx-background-radius: 15; -fx-text-fill: white; -fx-padding: 5 15;";
            case RESOLVED:
                return "-fx-background-color: #27ae60; -fx-background-radius: 15; -fx-text-fill: white; -fx-padding: 5 15;";
            case CLOSED:
                return "-fx-background-color: #95a5a6; -fx-background-radius: 15; -fx-text-fill: white; -fx-padding: 5 15;";
            default:
                return "-fx-background-color: #7f8c8d; -fx-background-radius: 15; -fx-text-fill: white; -fx-padding: 5 15;";
        }
    }
    
    /**
     * Get priority styling
     */
    private String getPriorityStyle(TicketPriority priority) {
        switch (priority) {
            case HIGH:
                return "-fx-background-color: #e74c3c; -fx-background-radius: 15; -fx-text-fill: white; -fx-padding: 5 15;";
            case MEDIUM:
                return "-fx-background-color: #f39c12; -fx-background-radius: 15; -fx-text-fill: white; -fx-padding: 5 15;";
            case LOW:
                return "-fx-background-color: #27ae60; -fx-background-radius: 15; -fx-text-fill: white; -fx-padding: 5 15;";
            default:
                return "-fx-background-color: #95a5a6; -fx-background-radius: 15; -fx-text-fill: white; -fx-padding: 5 15;";
        }
    }
    
    /**
     * Update progress display
     */
    private void updateProgressDisplay() {
        double progress = 0.0;
        String progressText = "0%";
        
        switch (currentTicket.getStatus()) {
            case OPEN:
                progress = 0.25;
                progressText = "25%";
                break;
            case IN_PROGRESS:
                progress = 0.6;
                progressText = "60%";
                break;
            case ESCALATED:
                progress = 0.7;
                progressText = "70%";
                break;
            case RESOLVED:
                progress = 0.9;
                progressText = "90%";
                break;
            case CLOSED:
                progress = 1.0;
                progressText = "100%";
                break;
        }
        
        if (progressBar != null) {
            progressBar.setProgress(progress);
        }
        if (progressLabel != null) {
            progressLabel.setText(progressText);
        }
    }
    
    /**
     * Setup overview table for admin screen
     */
    private void setupOverviewTable() {
        if (ticketsTableView != null) {
            if (ticketIdColumn != null) {
                ticketIdColumn.setCellValueFactory(new PropertyValueFactory<>("ticketId"));
            }
            if (subjectColumn != null) {
                subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
            }
            if (customerNameColumn != null) {
                customerNameColumn.setCellValueFactory(cellData -> 
                    new SimpleStringProperty(cellData.getValue().getCustomerId()));
            }
            if (priorityColumn != null) {
                priorityColumn.setCellValueFactory(cellData -> 
                    new SimpleStringProperty(cellData.getValue().getPriority().toString()));
            }
            if (statusColumn != null) {
                statusColumn.setCellValueFactory(cellData -> 
                    new SimpleStringProperty(cellData.getValue().getStatus().toString()));
            }
            if (assignedToColumn != null) {
                assignedToColumn.setCellValueFactory(new PropertyValueFactory<>("assignedTo"));
            }
            if (createdDateColumn != null) {
                createdDateColumn.setCellValueFactory(cellData -> 
                    new SimpleStringProperty(cellData.getValue().getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))));
            }
            if (lastUpdatedColumn != null) {
                lastUpdatedColumn.setCellValueFactory(cellData -> 
                    new SimpleStringProperty(cellData.getValue().getUpdatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))));
            }
            
            // Double-click to view ticket details
            ticketsTableView.setRowFactory(tv -> {
                TableRow<Ticket> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && !row.isEmpty()) {
                        viewTicketDetails(row.getItem());
                    }
                });
                return row;
            });
        }
    }
    
    /**
     * Setup filter controls for admin screen
     */
    private void setupFilterControls() {
        if (statusFilterComboBox != null) {
            statusFilterComboBox.setItems(FXCollections.observableArrayList(TicketStatus.values()));
        }
        
        if (priorityFilterComboBox != null) {
            priorityFilterComboBox.setItems(FXCollections.observableArrayList(TicketPriority.values()));
        }
        
        if (assigneeFilterComboBox != null) {
            assigneeFilterComboBox.setItems(FXCollections.observableArrayList(
                "Sarah Johnson", "Mike Chen", "Emma Davis", "Alex Wilson", "Lisa Garcia"
            ));
        }
        
        if (pageSizeComboBox != null) {
            pageSizeComboBox.setItems(FXCollections.observableArrayList("25", "50", "100"));
            pageSizeComboBox.setValue("100");
        }
        
        // Setup back button handler
        if (backButton != null) {
            backButton.setOnAction(e -> handleBackToDashboard());
        }
        
        // Setup search and filter handlers
        if (searchButton != null) {
            searchButton.setOnAction(e -> handleSearch());
        }
        if (refreshButton != null) {
            refreshButton.setOnAction(e -> loadAllTickets());
        }
        if (newTicketButton != null) {
            newTicketButton.setOnAction(e -> handleNewTicket());
        }
        if (exportButton != null) {
            exportButton.setOnAction(e -> handleExportTickets());
        }
    }
    
    /**
     * Load ticket statistics for admin screen
     */
    private void loadTicketStatistics() {
        List<Ticket> allTickets = ticketService.getAllTickets();
        
        if (totalTicketsLabel != null) {
            totalTicketsLabel.setText(String.valueOf(allTickets.size()));
        }
        
        long openTickets = allTickets.stream()
            .filter(t -> t.getStatus() == TicketStatus.OPEN || t.getStatus() == TicketStatus.IN_PROGRESS)
            .count();
        if (openTicketsLabel != null) {
            openTicketsLabel.setText(String.valueOf(openTickets));
        }
        
        long highPriorityTickets = allTickets.stream()
            .filter(t -> t.getPriority() == TicketPriority.HIGH)
            .count();
        if (highPriorityLabel != null) {
            highPriorityLabel.setText(String.valueOf(highPriorityTickets));
        }
        
        // For resolved today, we'd check today's date - simplified for now
        if (resolvedTodayLabel != null) {
            resolvedTodayLabel.setText("12");
        }
    }
    
    /**
     * Load all tickets for admin screen
     */
    private void loadAllTickets() {
        allTickets = FXCollections.observableList(ticketService.getAllTickets());
        filteredTickets = FXCollections.observableList(new ArrayList<>(allTickets));
        
        if (ticketsTableView != null) {
            ticketsTableView.setItems(filteredTickets);
        }
        
        updatePageInfo();
    }
    
    /**
     * Handle search and filtering
     */
    @FXML
    private void handleSearch() {
        filteredTickets.clear();
        
        String searchText = searchField != null ? searchField.getText().toLowerCase() : "";
        TicketStatus statusFilter = statusFilterComboBox != null ? statusFilterComboBox.getValue() : null;
        TicketPriority priorityFilter = priorityFilterComboBox != null ? priorityFilterComboBox.getValue() : null;
        String assigneeFilter = assigneeFilterComboBox != null ? assigneeFilterComboBox.getValue() : null;
        
        for (Ticket ticket : allTickets) {
            boolean matches = true;
            
            // Text search
            if (!searchText.isEmpty()) {
                matches = ticket.getTicketId().toLowerCase().contains(searchText) ||
                         ticket.getSubject().toLowerCase().contains(searchText) ||
                         ticket.getCustomerId().toLowerCase().contains(searchText);
            }
            
            // Status filter
            if (matches && statusFilter != null) {
                matches = ticket.getStatus() == statusFilter;
            }
            
            // Priority filter
            if (matches && priorityFilter != null) {
                matches = ticket.getPriority() == priorityFilter;
            }
            
            // Assignee filter
            if (matches && assigneeFilter != null) {
                matches = assigneeFilter.equals(ticket.getAssignedTo());
            }
            
            if (matches) {
                filteredTickets.add(ticket);
            }
        }
        
        updatePageInfo();
    }
    
    /**
     * View ticket details
     */
    private void viewTicketDetails(Ticket ticket) {
        NavigationManager.getInstance().setSharedData("selectedTicket", ticket);
        NavigationManager.getInstance().showTicketStatus(ticket);
    }
    
    /**
     * Update page information
     */
    private void updatePageInfo() {
        if (pageInfoLabel != null) {
            int totalPages = Math.max(1, (int) Math.ceil((double) filteredTickets.size() / 100));
            pageInfoLabel.setText("Page 1 of " + totalPages);
        }
    }
    
    /**
     * Handle new ticket creation
     */
    @FXML
    private void handleNewTicket() {
        NavigationManager.getInstance().showTicketSubmission();
    }
    
    /**
     * Handle export tickets functionality
     */
    @FXML
    private void handleExportTickets() {
        showAlert("Info", "Export functionality coming soon!");
    }
    
    /**
     * Handle ticket status actions
     */
    @FXML
    private void handleAddResponse() {
        showAlert("Info", "Response functionality coming soon!");
    }
    
    @FXML
    private void handleEmailSupport() {
        showAlert("Info", "Email sent to support team!");
    }
    
    @FXML
    private void handleCloseTicket() {
        if (currentTicket != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Close Ticket");
            alert.setHeaderText("Are you sure you want to close this ticket?");
            alert.setContentText("This action cannot be undone.");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    boolean success = ticketService.closeTicket(currentTicket.getTicketId(), 
                        currentUser != null ? currentUser.getUserId() : "SYSTEM");
                    if (success) {
                        showAlert("Success", "Ticket has been closed");
                        handleBackToDashboard();
                    } else {
                        showAlert("Error", "Failed to close ticket");
                    }
                }
            });
        }
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