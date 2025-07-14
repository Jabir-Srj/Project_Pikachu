package controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import model.Customer;
import model.User;
import service.UserService;
import util.NavigationManager;
import util.ServiceLocator;

/**
 * Controller for CustomerManagement.fxml
 * Manages customer data display, search, filtering, and actions
 */
public class CustomerManagementController implements Initializable {
    
    // Header Controls
    @FXML private Button backButton;
    @FXML private Button addCustomerButton;
    @FXML private Button exportButton;
    
    // Search and Filter Controls
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilterComboBox;
    @FXML private ComboBox<String> dateRangeComboBox;
    @FXML private Button searchButton;
    @FXML private Button clearButton;
    
    // Statistics Labels
    @FXML private Text totalCustomersLabel;
    @FXML private Text activeCustomersLabel;
    @FXML private Text newCustomersLabel;
    @FXML private Text inactiveCustomersLabel;
    
    // Table and Controls
    @FXML private TableView<Customer> customersTableView;
    @FXML private TableColumn<Customer, String> customerIdColumn;
    @FXML private TableColumn<Customer, String> firstNameColumn;
    @FXML private TableColumn<Customer, String> lastNameColumn;
    @FXML private TableColumn<Customer, String> emailColumn;
    @FXML private TableColumn<Customer, String> phoneColumn;
    @FXML private TableColumn<Customer, String> joinDateColumn;
    @FXML private TableColumn<Customer, String> totalBookingsColumn;
    @FXML private TableColumn<Customer, String> totalSpentColumn;
    @FXML private TableColumn<Customer, String> statusColumn;
    @FXML private TableColumn<Customer, String> actionsColumn;
    
    // Pagination Controls
    @FXML private Button previousPageButton;
    @FXML private Button nextPageButton;
    @FXML private Label pageInfoLabel;
    @FXML private ComboBox<String> pageSizeComboBox;
    @FXML private Button refreshButton;
    
    // Services and Data
    private UserService userService;
    private NavigationManager navigationManager;
    private User currentUser;
    private ObservableList<Customer> allCustomers;
    private ObservableList<Customer> filteredCustomers;
    private int currentPage = 1;
    private int pageSize = 50;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize services
        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        userService = serviceLocator.getUserService();
        navigationManager = NavigationManager.getInstance();
        currentUser = (User) navigationManager.getSharedData("currentUser");
        
        // Setup UI components
        setupTableColumns();
        setupFilterControls();
        setupEventHandlers();
        
        // Load data
        loadCustomerData();
        updateStatistics();
    }
    
    /**
     * Setup table columns with proper cell value factories
     */
    private void setupTableColumns() {
        if (customerIdColumn != null) {
            customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        }
        if (firstNameColumn != null) {
            firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        }
        if (lastNameColumn != null) {
            lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        }
        if (emailColumn != null) {
            emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        }
        if (phoneColumn != null) {
            phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        }
        if (joinDateColumn != null) {
            joinDateColumn.setCellValueFactory(cellData -> {
                LocalDateTime joinDate = cellData.getValue().getCreatedAt();
                if (joinDate != null) {
                    return new SimpleStringProperty(joinDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
                }
                return new SimpleStringProperty("N/A");
            });
        }
        if (totalBookingsColumn != null) {
            totalBookingsColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(String.valueOf(cellData.getValue().getBookingHistory().size())));
        }
        if (totalSpentColumn != null) {
            totalSpentColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(String.format("$%.2f", calculateTotalSpent(cellData.getValue()))));
        }
        if (statusColumn != null) {
            statusColumn.setCellValueFactory(cellData -> {
                Customer customer = cellData.getValue();
                String status = customer.isActive() ? "Active" : "Inactive";
                return new SimpleStringProperty(status);
            });
        }
        if (actionsColumn != null) {
            actionsColumn.setCellFactory(column -> new TableCell<Customer, String>() {
                private final Button editButton = new Button("✏️ Edit");
                private final Button toggleButton = new Button("🔄 Toggle");
                private final HBox buttonBox = new HBox(5, editButton, toggleButton);

                {
                    buttonBox.setAlignment(Pos.CENTER);
                    editButton.getStyleClass().add("action-button-warning");
                    toggleButton.getStyleClass().add("action-button-danger");
                    
                    editButton.setOnAction(e -> {
                        Customer customer = getTableView().getItems().get(getIndex());
                        handleEditCustomer(customer);
                    });
                    
                    toggleButton.setOnAction(e -> {
                        Customer customer = getTableView().getItems().get(getIndex());
                        handleToggleCustomerStatus(customer);
                    });
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        Customer customer = getTableView().getItems().get(getIndex());
                        toggleButton.setText(customer.isActive() ? "❌ Deactivate" : "✅ Activate");
                        toggleButton.getStyleClass().clear();
                        toggleButton.getStyleClass().add(customer.isActive() ? "action-button-danger" : "action-button-success");
                        setGraphic(buttonBox);
                    }
                }
            });
        }
    }
    
    /**
     * Setup filter controls with default values
     */
    private void setupFilterControls() {
        if (statusFilterComboBox != null) {
            statusFilterComboBox.setItems(FXCollections.observableArrayList(
                "All Status", "Active", "Inactive"
            ));
            statusFilterComboBox.setValue("All Status");
        }
        
        if (dateRangeComboBox != null) {
            dateRangeComboBox.setItems(FXCollections.observableArrayList(
                "All Time", "Last 30 Days", "Last 3 Months", "Last 6 Months", "Last Year"
            ));
            dateRangeComboBox.setValue("All Time");
        }
        
        if (pageSizeComboBox != null) {
            pageSizeComboBox.setItems(FXCollections.observableArrayList("25", "50", "100"));
            pageSizeComboBox.setValue("50");
            pageSizeComboBox.setOnAction(e -> {
                pageSize = Integer.parseInt(pageSizeComboBox.getValue());
                currentPage = 1;
                updateTableView();
            });
        }
    }
    
    /**
     * Setup event handlers for all interactive components
     */
    private void setupEventHandlers() {
        if (backButton != null) {
            backButton.setOnAction(e -> handleBackToDashboard());
        }
        if (addCustomerButton != null) {
            addCustomerButton.setOnAction(e -> util.NavigationManager.getInstance().showAddCustomer());
        }
        if (exportButton != null) {
            exportButton.setOnAction(e -> handleExportCustomers());
        }
        if (searchButton != null) {
            searchButton.setOnAction(e -> handleSearch());
        }
        if (clearButton != null) {
            clearButton.setOnAction(e -> handleClearFilters());
        }
        if (refreshButton != null) {
            refreshButton.setOnAction(e -> handleRefresh());
        }
        if (previousPageButton != null) {
            previousPageButton.setOnAction(e -> handlePreviousPage());
        }
        if (nextPageButton != null) {
            nextPageButton.setOnAction(e -> handleNextPage());
        }
        
        // Auto-search on text input
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.length() >= 3 || newValue.isEmpty()) {
                    handleSearch();
                }
            });
        }
    }
    
    /**
     * Load customer data from the service
     */
    private void loadCustomerData() {
        List<Customer> customers = userService.getAllCustomers();
        allCustomers = FXCollections.observableList(customers);
        filteredCustomers = FXCollections.observableList(new ArrayList<>(allCustomers));
        updateTableView();
    }
    
    /**
     * Update customer statistics
     */
    private void updateStatistics() {
        if (allCustomers != null) {
            int total = allCustomers.size();
            int active = (int) allCustomers.stream().filter(Customer::isActive).count();
            int inactive = total - active;
            
            // Calculate new customers (last 30 days)
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            int newCustomers = (int) allCustomers.stream()
                .filter(c -> c.getCreatedAt() != null && c.getCreatedAt().isAfter(thirtyDaysAgo))
                .count();
            
            if (totalCustomersLabel != null) {
                totalCustomersLabel.setText(String.format("%,d", total));
            }
            if (activeCustomersLabel != null) {
                activeCustomersLabel.setText(String.format("%,d", active));
            }
            if (inactiveCustomersLabel != null) {
                inactiveCustomersLabel.setText(String.format("%,d", inactive));
            }
            if (newCustomersLabel != null) {
                newCustomersLabel.setText(String.format("%,d", newCustomers));
            }
        }
    }
    
    /**
     * Update table view with current filtered data and pagination
     */
    private void updateTableView() {
        if (customersTableView != null && filteredCustomers != null) {
            int totalItems = filteredCustomers.size();
            int totalPages = Math.max(1, (int) Math.ceil((double) totalItems / pageSize));
            
            // Ensure current page is valid
            if (currentPage > totalPages) {
                currentPage = totalPages;
            }
            
            // Calculate start and end indices for current page
            int startIndex = (currentPage - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalItems);
            
            // Get items for current page
            List<Customer> pageItems = filteredCustomers.subList(startIndex, endIndex);
            customersTableView.setItems(FXCollections.observableList(pageItems));
            
            // Update pagination controls
            updatePaginationControls(totalPages, totalItems);
        }
    }
    
    /**
     * Update pagination control states and labels
     */
    private void updatePaginationControls(int totalPages, int totalItems) {
        if (pageInfoLabel != null) {
            pageInfoLabel.setText(String.format("Page %d of %d", currentPage, totalPages));
        }
        
        if (previousPageButton != null) {
            previousPageButton.setDisable(currentPage <= 1);
        }
        
        if (nextPageButton != null) {
            nextPageButton.setDisable(currentPage >= totalPages);
        }
    }
    
    /**
     * Handle search and filtering
     */
    @FXML
    private void handleSearch() {
        if (allCustomers == null) return;
        
        String searchText = searchField != null ? searchField.getText().toLowerCase().trim() : "";
        String statusFilter = statusFilterComboBox != null ? statusFilterComboBox.getValue() : "All Status";
        String dateFilter = dateRangeComboBox != null ? dateRangeComboBox.getValue() : "All Time";
        
        filteredCustomers = FXCollections.observableList(
            allCustomers.stream()
                .filter(customer -> matchesSearchCriteria(customer, searchText, statusFilter, dateFilter))
                .collect(Collectors.toList())
        );
        
        currentPage = 1;
        updateTableView();
    }
    
    /**
     * Check if customer matches search criteria
     */
    private boolean matchesSearchCriteria(Customer customer, String searchText, String statusFilter, String dateFilter) {
        // Text search
        if (!searchText.isEmpty()) {
            boolean matchesText = customer.getFirstName().toLowerCase().contains(searchText) ||
                                customer.getLastName().toLowerCase().contains(searchText) ||
                                customer.getEmail().toLowerCase().contains(searchText) ||
                                customer.getPhoneNumber().toLowerCase().contains(searchText) ||
                                customer.getUserId().toLowerCase().contains(searchText);
            if (!matchesText) return false;
        }
        
        // Status filter
        if (!"All Status".equals(statusFilter)) {
            boolean isActive = customer.isActive();
            if ("Active".equals(statusFilter) && !isActive) return false;
            if ("Inactive".equals(statusFilter) && isActive) return false;
        }
        
        // Date filter
        if (!"All Time".equals(dateFilter) && customer.getCreatedAt() != null) {
            LocalDateTime cutoffDate = LocalDateTime.now();
            switch (dateFilter) {
                case "Last 30 Days":
                    cutoffDate = cutoffDate.minusDays(30);
                    break;
                case "Last 3 Months":
                    cutoffDate = cutoffDate.minusMonths(3);
                    break;
                case "Last 6 Months":
                    cutoffDate = cutoffDate.minusMonths(6);
                    break;
                case "Last Year":
                    cutoffDate = cutoffDate.minusYears(1);
                    break;
            }
            if (customer.getCreatedAt().isBefore(cutoffDate)) return false;
        }
        
        return true;
    }
    
    /**
     * Handle clear filters
     */
    @FXML
    private void handleClearFilters() {
        if (searchField != null) {
            searchField.clear();
        }
        if (statusFilterComboBox != null) {
            statusFilterComboBox.setValue("All Status");
        }
        if (dateRangeComboBox != null) {
            dateRangeComboBox.setValue("All Time");
        }
        handleSearch();
    }
    
    /**
     * Handle refresh data
     */
    @FXML
    private void handleRefresh() {
        loadCustomerData();
        updateStatistics();
        showAlert("Success", "Customer data refreshed successfully!");
    }
    
    /**
     * Handle previous page
     */
    @FXML
    private void handlePreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            updateTableView();
        }
    }
    
    /**
     * Handle next page
     */
    @FXML
    private void handleNextPage() {
        int totalPages = Math.max(1, (int) Math.ceil((double) filteredCustomers.size() / pageSize));
        if (currentPage < totalPages) {
            currentPage++;
            updateTableView();
        }
    }
    
    /**
     * Handle back to dashboard
     */
    @FXML
    private void handleBackToDashboard() {
        // Only admins should access customer management - enforce role-based navigation
        if (currentUser != null && currentUser.getRole() != null && currentUser.getRole().name().equals("ADMIN")) {
            navigationManager.showAdminDashboard();
        } else {
            // Non-admin users should not be here - redirect to login for security
            navigationManager.navigateTo(NavigationManager.LOGIN_SCREEN);
        }
    }
    
    /**
     * Handle add customer
     */
    @FXML
    private void handleAddCustomer() {
        showAlert("Info", "Add Customer functionality will be implemented in a future update.");
    }
    
    /**
     * Handle export customers
     */
    @FXML
    private void handleExportCustomers() {
        showAlert("Export Feature", "Export functionality will be implemented in future version.");
    }
    
    /**
     * Handle edit customer action
     */
    private void handleEditCustomer(Customer customer) {
        if (customer != null) {
            // Create the edit dialog
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Edit Customer");
            dialog.setHeaderText("Edit Customer Information for: " + customer.getFirstName() + " " + customer.getLastName());
            
            // Create the dialog content
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
            
            // Create form fields
            TextField firstNameField = new TextField(customer.getFirstName());
            TextField lastNameField = new TextField(customer.getLastName());
            TextField emailField = new TextField(customer.getEmail());
            TextField phoneField = new TextField(customer.getPhoneNumber());
            
            // Add labels and fields to grid
            grid.add(new Label("First Name:"), 0, 0);
            grid.add(firstNameField, 1, 0);
            grid.add(new Label("Last Name:"), 0, 1);
            grid.add(lastNameField, 1, 1);
            grid.add(new Label("Email:"), 0, 2);
            grid.add(emailField, 1, 2);
            grid.add(new Label("Phone:"), 0, 3);
            grid.add(phoneField, 1, 3);
            
            dialog.getDialogPane().setContent(grid);
            
            // Add buttons
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            
            // Show dialog and handle result
            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Validate input
                    String firstName = firstNameField.getText().trim();
                    String lastName = lastNameField.getText().trim();
                    String email = emailField.getText().trim();
                    String phone = phoneField.getText().trim();
                    
                    if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Validation Error");
                        errorAlert.setHeaderText("Required fields missing");
                        errorAlert.setContentText("First Name, Last Name, and Email are required fields.");
                        errorAlert.showAndWait();
                        return;
                    }
                    
                    // Email validation
                    if (!email.contains("@") || !email.contains(".")) {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Validation Error");
                        errorAlert.setHeaderText("Invalid Email");
                        errorAlert.setContentText("Please enter a valid email address.");
                        errorAlert.showAndWait();
                        return;
                    }
                    
                    // Update customer information
                    customer.setFirstName(firstName);
                    customer.setLastName(lastName);
                    customer.setEmail(email);
                    customer.setPhoneNumber(phone);
                    
                    // Save to database
                    try {
                        userService.updateUserProfile(customer);
                        
                        // Refresh the table
                        loadCustomerData();
                        
                        // Show success message
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Success");
                        successAlert.setHeaderText("Customer Updated");
                        successAlert.setContentText("Customer information has been updated successfully.");
                        successAlert.showAndWait();
                        
                    } catch (Exception e) {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Error");
                        errorAlert.setHeaderText("Failed to update customer");
                        errorAlert.setContentText("An error occurred while updating the customer: " + e.getMessage());
                        errorAlert.showAndWait();
                    }
                }
            });
        }
    }
    
    /**
     * Handle toggle customer status (activate/deactivate)
     */
    private void handleToggleCustomerStatus(Customer customer) {
        if (customer != null) {
            boolean newStatus = !customer.isActive();
            String action = newStatus ? "activate" : "deactivate";
            String actionPast = newStatus ? "activated" : "deactivated";
            
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Action");
            confirmAlert.setHeaderText("Customer Status Change");
            confirmAlert.setContentText("Are you sure you want to " + action + " customer:\n" +
                customer.getFirstName() + " " + customer.getLastName() + " (" + customer.getEmail() + ")?");
            
            confirmAlert.showAndWait().ifPresent(response -> {
                if (response.getButtonData().isDefaultButton()) {
                    // Update customer status
                    customer.setActive(newStatus);
                    
                    // Update in service
                    userService.updateUserProfile(customer);
                    
                    // Refresh table view
                    updateTableView();
                    customersTableView.refresh();
                    updateStatistics();
                    
                    // Show success message
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Success");
                    successAlert.setHeaderText("Customer Status Updated");
                    successAlert.setContentText("Customer " + customer.getFirstName() + " " + 
                        customer.getLastName() + " has been " + actionPast + " successfully.");
                    successAlert.showAndWait();
                }
            });
        }
    }

    /**
     * Calculate total amount spent by customer (placeholder implementation)
     */
    private double calculateTotalSpent(Customer customer) {
        // Placeholder implementation - in real system would query booking database
        // For now, return a random amount based on booking history size
        return customer.getBookingHistory().size() * 150.0 + (customer.getLoyaltyPoints() * 0.1);
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
