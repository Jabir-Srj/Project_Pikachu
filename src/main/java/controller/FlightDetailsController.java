package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Flight;
import model.FlightStatus;
import model.Booking;
import model.UserRole;
import util.NavigationManager;
import util.SessionManager;
import dao.FlightDAO;
import dao.BookingDAO;
import service.FlightService;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for FlightDetails.fxml
 * Handles display and editing of flight information
 */
public class FlightDetailsController implements Initializable {

    // Flight Information Fields
    @FXML private TextField flightNumberField;
    @FXML private TextField aircraftTypeField;
    @FXML private ComboBox<String> departureAirportComboBox;
    @FXML private ComboBox<String> arrivalAirportComboBox;
    @FXML private DatePicker departureDatePicker;
    @FXML private TextField departureTimeField;
    @FXML private TextField arrivalTimeField;
    @FXML private ComboBox<String> statusComboBox;

    // Pricing Fields
    @FXML private TextField basePriceField;
    @FXML private TextField economyPriceField;
    @FXML private TextField businessPriceField;

    // Capacity Fields
    @FXML private TextField totalCapacityField;
    @FXML private TextField availableSeatsField;
    @FXML private TextField bookedSeatsField;

    // Notes Field
    @FXML private TextArea flightNotesArea;

    // Statistics Labels
    @FXML private Label totalRevenueLabel;
    @FXML private Label occupancyRateLabel;
    @FXML private Label averagePriceLabel;

    // Buttons
    @FXML private Button backButton;
    @FXML private Button editButton;
    @FXML private Button saveButton;
    @FXML private Button cancelEditButton;
    @FXML private Button cancelFlightButton;
    @FXML private Button refreshButton;
    @FXML private Button duplicateFlightButton;
    @FXML private Button exportPassengersButton;
    @FXML private Button checkInAllButton;

    // Passengers Table
    @FXML private TableView<Booking> passengersTableView;

    private Flight currentFlight;
    private FlightDAO flightDAO;
    private BookingDAO bookingDAO;
    private boolean isEditMode = false;
    private boolean isAdminUser = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("FlightDetailsController: Initializing...");
        
        flightDAO = new FlightDAO();
        bookingDAO = new BookingDAO();
        
        // Check if current user is admin
        checkUserPermissions();
        
        setupComboBoxes();
        setupPassengersTable();
        loadFlightData();
        setupUserPermissions();
        setEditMode(false);
        System.out.println("FlightDetailsController: Initialization complete");
    }

    private void setupComboBoxes() {
        // Setup airport combo boxes
        departureAirportComboBox.getItems().addAll(
            "JFK - John F. Kennedy International Airport",
            "LAX - Los Angeles International Airport",
            "LHR - London Heathrow Airport",
            "CDG - Charles de Gaulle Airport",
            "NRT - Narita International Airport",
            "DXB - Dubai International Airport"
        );

        arrivalAirportComboBox.getItems().addAll(
            "JFK - John F. Kennedy International Airport",
            "LAX - Los Angeles International Airport",
            "LHR - London Heathrow Airport",
            "CDG - Charles de Gaulle Airport",
            "NRT - Narita International Airport",
            "DXB - Dubai International Airport"
        );

        // Setup status combo box with all available FlightStatus values
        statusComboBox.getItems().addAll(
            "Scheduled",
            "Boarding",
            "Departed",
            "Arrived",
            "Delayed",
            "Cancelled",
            "On Time"
        );
    }

    private void setupPassengersTable() {
        // Create table columns
        TableColumn<Booking, String> bookingIdCol = new TableColumn<>("Booking ID");
        bookingIdCol.setCellValueFactory(new PropertyValueFactory<>("bookingId"));

        TableColumn<Booking, String> passengerNameCol = new TableColumn<>("Passenger Name");
        passengerNameCol.setCellValueFactory(new PropertyValueFactory<>("passengerName"));

        TableColumn<Booking, String> seatCol = new TableColumn<>("Seat");
        seatCol.setCellValueFactory(new PropertyValueFactory<>("seatNumber"));

        TableColumn<Booking, String> classCol = new TableColumn<>("Class");
        classCol.setCellValueFactory(new PropertyValueFactory<>("seatClass"));

        TableColumn<Booking, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        passengersTableView.getColumns().clear();
        passengersTableView.getColumns().add(bookingIdCol);
        passengersTableView.getColumns().add(passengerNameCol);
        passengersTableView.getColumns().add(seatCol);
        passengersTableView.getColumns().add(classCol);
        passengersTableView.getColumns().add(statusCol);
    }

    private void loadFlightData() {
        // Get the selected flight from NavigationManager
        Flight oldFlight = (Flight) NavigationManager.getInstance().getSharedData("selectedFlight");
        if (oldFlight != null) {
            currentFlight = new FlightService().getFlightDetails(oldFlight.getFlightNumber()).orElse(null);
        }
        
        if (currentFlight != null) {
            System.out.println("FlightDetailsController: Loading data for flight " + currentFlight.getFlightNumber());
            populateFlightFields();
            loadPassengerData();
            calculateStatistics();
        } else {
            System.err.println("FlightDetailsController: No flight data found");
        }
    }

    private void populateFlightFields() {
        System.out.println("DEBUG: Populating fields with status: " + currentFlight.getStatus() + " (" + currentFlight.getStatus().getDisplayName() + ")");
        
        flightNumberField.setText(currentFlight.getFlightNumber());
        aircraftTypeField.setText(currentFlight.getAircraft() != null ? currentFlight.getAircraft() : "");
        departureAirportComboBox.setValue(currentFlight.getDepartureAirport());
        arrivalAirportComboBox.setValue(currentFlight.getArrivalAirport());
        departureDatePicker.setValue(currentFlight.getDepartureTime().toLocalDate());
        departureTimeField.setText(currentFlight.getDepartureTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        arrivalTimeField.setText(currentFlight.getArrivalTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        statusComboBox.setValue(currentFlight.getStatus().getDisplayName());
        
        System.out.println("DEBUG: Set ComboBox value to: " + currentFlight.getStatus().getDisplayName());
        
        basePriceField.setText(String.valueOf(currentFlight.getBasePrice()));
        // Load economy and business prices from the model
        economyPriceField.setText(String.valueOf(currentFlight.getEconomyPrice() > 0 ? currentFlight.getEconomyPrice() : currentFlight.getBasePrice()));
        businessPriceField.setText(String.valueOf(currentFlight.getBusinessPrice() > 0 ? currentFlight.getBusinessPrice() : currentFlight.getBasePrice() * 1.5));
        
        totalCapacityField.setText(String.valueOf(currentFlight.getTotalSeats()));
        availableSeatsField.setText(String.valueOf(currentFlight.getAvailableSeats()));
        bookedSeatsField.setText(String.valueOf(currentFlight.getTotalSeats() - currentFlight.getAvailableSeats()));
        
        // Load flight notes
        flightNotesArea.setText(currentFlight.getNotes() != null ? currentFlight.getNotes() : "");
    }

    private void loadPassengerData() {
        // Only load passenger data for admin users (privacy protection)
        if (!isAdminUser) {
            System.out.println("FlightDetailsController: Skipping passenger data load for non-admin user");
            return;
        }
        
        List<Booking> bookings = bookingDAO.findByFlightId(currentFlight.getFlightId());
        passengersTableView.getItems().clear();
        passengersTableView.getItems().addAll(bookings);
    }

    private void calculateStatistics() {
        List<Booking> bookings = bookingDAO.findByFlightId(currentFlight.getFlightId());
        
        double totalRevenue = bookings.stream()
            .mapToDouble(Booking::getTotalPrice)
            .sum();
        
        int bookedSeats = bookings.size();
        int totalSeats = currentFlight.getTotalSeats();
        double occupancyRate = totalSeats > 0 ? (double) bookedSeats / totalSeats * 100 : 0;
        
        double averagePrice = bookedSeats > 0 ? totalRevenue / bookedSeats : 0;
        
        totalRevenueLabel.setText(String.format("%.2f", totalRevenue));
        occupancyRateLabel.setText(String.format("%.1f%%", occupancyRate));
        averagePriceLabel.setText(String.format("%.2f", averagePrice));
    }

    private void checkUserPermissions() {
        if (SessionManager.getCurrentUser() != null) {
            UserRole userRole = SessionManager.getCurrentUser().getRole();
            isAdminUser = (userRole == UserRole.ADMIN || userRole == UserRole.AIRLINE_MANAGEMENT);
            System.out.println("FlightDetailsController: User role: " + userRole + ", isAdmin: " + isAdminUser);
        } else {
            isAdminUser = false;
            System.out.println("FlightDetailsController: No user logged in, defaulting to read-only mode");
        }
    }

    private void setupUserPermissions() {
        if (!isAdminUser) {
            // Make all fields read-only for non-admin users
            setAllFieldsReadOnly();
            hideAdminButtons();
            System.out.println("FlightDetailsController: Set to read-only mode for non-admin user");
        } else {
            System.out.println("FlightDetailsController: Admin mode enabled - editing available");
        }
    }

    private void setAllFieldsReadOnly() {
        // Make all input fields non-editable
        flightNumberField.setEditable(false);
        aircraftTypeField.setEditable(false);
        departureAirportComboBox.setDisable(true);
        arrivalAirportComboBox.setDisable(true);
        departureDatePicker.setDisable(true);
        departureTimeField.setEditable(false);
        arrivalTimeField.setEditable(false);
        statusComboBox.setDisable(true);
        basePriceField.setEditable(false);
        economyPriceField.setEditable(false);
        businessPriceField.setEditable(false);
        totalCapacityField.setEditable(false);
        availableSeatsField.setEditable(false);
        bookedSeatsField.setEditable(false);
    }

    private void hideAdminButtons() {
        // Hide admin-only buttons for non-admin users
        editButton.setVisible(false);
        saveButton.setVisible(false);
        cancelEditButton.setVisible(false);
        cancelFlightButton.setVisible(false);
        duplicateFlightButton.setVisible(false);
        exportPassengersButton.setVisible(false);
        checkInAllButton.setVisible(false);
        
        // Hide passenger list from non-admin users (privacy protection)
        passengersTableView.setVisible(false);
        passengersTableView.setManaged(false); // Remove from layout calculations
        
        // Keep only back and refresh buttons visible
        backButton.setVisible(true);
        refreshButton.setVisible(true);
    }

    @FXML
        private void handleBack() {
        System.out.println("FlightDetailsController: Back button clicked");
        if (SessionManager.getCurrentUser() != null && SessionManager.getCurrentUser().getRole() != null) {
            UserRole userRole = SessionManager.getCurrentUser().getRole();
            NavigationManager.getInstance().showFlightInformation();
            }
        else {
            // If user role is unclear, go to login for security
            NavigationManager.getInstance().navigateTo(NavigationManager.LOGIN_SCREEN);
        }
    }

    @FXML
    private void handleEdit() {
        if (!isAdminUser) {
            showAlert("Access Denied", "You do not have permission to edit flight details.", Alert.AlertType.WARNING);
            return;
        }
        
        System.out.println("FlightDetailsController: Edit button clicked");
        isEditMode = true;
        setEditMode(true);
    }

    @FXML
    private void handleSave() {
        if (!isAdminUser) {
            showAlert("Access Denied", "You do not have permission to save flight details.", Alert.AlertType.WARNING);
            return;
        }
        
        System.out.println("FlightDetailsController: Save button clicked");
        try {
            saveFlightChanges();
            isEditMode = false;
            setEditMode(false);
            showAlert("Success", "Flight details updated successfully!", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            System.err.println("Error saving flight: " + e.getMessage());
            showAlert("Error", "Failed to save flight details: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancelEdit() {
        System.out.println("FlightDetailsController: Cancel edit button clicked");
        isEditMode = false;
        setEditMode(false);
        populateFlightFields(); // Restore original values
    }

    @FXML
    private void handleCancelFlight() {
        if (!isAdminUser) {
            showAlert("Access Denied", "You do not have permission to cancel flights.", Alert.AlertType.WARNING);
            return;
        }
        
        System.out.println("FlightDetailsController: Cancel flight button clicked");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Flight");
        alert.setHeaderText("Cancel Flight Confirmation");
        alert.setContentText("Are you sure you want to cancel this flight? This action cannot be undone.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                currentFlight.setStatus(FlightStatus.CANCELLED);
                flightDAO.update(currentFlight);
                statusComboBox.setValue("Cancelled");
                showAlert("Success", "Flight has been cancelled.", Alert.AlertType.INFORMATION);
            }
        });
    }

    @FXML
    private void handleRefresh() {
        System.out.println("FlightDetailsController: Refresh button clicked");
        loadFlightData();
    }

    @FXML
    private void handleDuplicateFlight() {
        System.out.println("FlightDetailsController: Duplicate flight button clicked");
        // Implementation for duplicating flight
        showAlert("Info", "Duplicate flight feature coming soon!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleExportPassengers() {
        System.out.println("FlightDetailsController: Export passengers button clicked");
        // Implementation for exporting passenger list
        showAlert("Export Feature", "Export functionality will be implemented in future version.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleCheckInAll() {
        System.out.println("FlightDetailsController: Check-in all button clicked");
        // Implementation for checking in all passengers
        showAlert("Info", "Check-in all feature coming soon!", Alert.AlertType.INFORMATION);
    }

    private void setEditMode(boolean editMode) {
        // Only allow edit mode for admin users
        if (!isAdminUser) {
            editMode = false;
        }
        
        // Enable/disable fields based on edit mode
        aircraftTypeField.setEditable(editMode);
        departureAirportComboBox.setDisable(!editMode);
        arrivalAirportComboBox.setDisable(!editMode);
        departureDatePicker.setDisable(!editMode);
        departureTimeField.setEditable(editMode);
        arrivalTimeField.setEditable(editMode);
        statusComboBox.setDisable(!editMode);
        basePriceField.setEditable(editMode);
        economyPriceField.setEditable(editMode);  // Keep for UI, even though not persisted
        businessPriceField.setEditable(editMode); // Keep for UI, even though not persisted
        totalCapacityField.setEditable(editMode);
        flightNotesArea.setEditable(editMode);

        // Show/hide buttons only for admin users
        if (isAdminUser) {
            editButton.setVisible(!editMode);
            saveButton.setVisible(editMode);
            cancelEditButton.setVisible(editMode);
        }
        
        // Update isEditMode field
        isEditMode = editMode;
    }

    private void saveFlightChanges() {
        try {
            // Update flight object with form values
            currentFlight.setAircraft(aircraftTypeField.getText());
            currentFlight.setDepartureAirport(departureAirportComboBox.getValue());
            currentFlight.setArrivalAirport(arrivalAirportComboBox.getValue());
            
            // Parse and validate date/time fields
            LocalDate date = departureDatePicker.getValue();
            if (date == null) {
                throw new IllegalArgumentException("Departure date is required");
            }
            
            LocalTime depTime = LocalTime.parse(departureTimeField.getText(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime arrTime = LocalTime.parse(arrivalTimeField.getText(), DateTimeFormatter.ofPattern("HH:mm"));
            
            currentFlight.setDepartureTime(date.atTime(depTime));
            currentFlight.setArrivalTime(date.atTime(arrTime));
            
            // Convert string status to FlightStatus enum
            String statusString = statusComboBox.getValue();
            FlightStatus status = null;
            
            // Map display names to enum values
            switch (statusString) {
                case "Scheduled":
                    status = FlightStatus.SCHEDULED;
                    break;
                case "Boarding":
                    status = FlightStatus.BOARDING;
                    break;
                case "Departed":
                    status = FlightStatus.DEPARTED;
                    break;
                case "Arrived":
                    status = FlightStatus.ARRIVED;
                    break;
                case "Delayed":
                    status = FlightStatus.DELAYED;
                    break;
                case "Cancelled":
                    status = FlightStatus.CANCELLED;
                    break;
                case "On Time":
                    status = FlightStatus.ON_TIME;
                    break;
                default:
                    status = FlightStatus.SCHEDULED; // Default fallback
                    break;
            }
            currentFlight.setStatus(status);
            
            // Parse and validate price and capacity fields
            double basePrice = Double.parseDouble(basePriceField.getText());
            if (basePrice < 0) {
                throw new IllegalArgumentException("Base price cannot be negative");
            }
            currentFlight.setBasePrice(basePrice);
            
            // Parse and save economy price
            double economyPrice = Double.parseDouble(economyPriceField.getText());
            if (economyPrice < 0) {
                throw new IllegalArgumentException("Economy price cannot be negative");
            }
            currentFlight.setEconomyPrice(economyPrice);
            
            // Parse and save business price
            double businessPrice = Double.parseDouble(businessPriceField.getText());
            if (businessPrice < 0) {
                throw new IllegalArgumentException("Business price cannot be negative");
            }
            currentFlight.setBusinessPrice(businessPrice);
            
            int totalSeats = Integer.parseInt(totalCapacityField.getText());
            if (totalSeats <= 0) {
                throw new IllegalArgumentException("Total seats must be greater than 0");
            }
            currentFlight.setTotalSeats(totalSeats);
            
            // Save flight notes
            String notes = flightNotesArea.getText();
            currentFlight.setNotes(notes);

            // Debug: Print status before saving
            System.out.println("DEBUG: Status before saving: " + currentFlight.getStatus() + " (" + currentFlight.getStatus().getDisplayName() + ")");
            
            // Save to database
            FlightService flightService = new FlightService();
            boolean updateSuccess = flightService.updateFlight(currentFlight);
            if (!updateSuccess) {
                throw new RuntimeException("Failed to save flight changes to database");
            }
            
            // Debug: Verify what was saved
            System.out.println("DEBUG: Update success: " + updateSuccess);
            
            // Reload the flight data from database to ensure we have the latest persisted state
            Optional<Flight> updatedFlight = flightService.getFlightDetails(currentFlight.getFlightNumber());
            if (updatedFlight.isPresent()) {
                Flight reloadedFlight = updatedFlight.get();
                System.out.println("DEBUG: Status after reloading from DB: " + reloadedFlight.getStatus() + " (" + reloadedFlight.getStatus().getDisplayName() + ")");
                currentFlight = reloadedFlight;
            }
            
            NavigationManager.getInstance().setSharedData("selectedFlight", currentFlight);
            System.out.println("FlightDetailsController: Successfully saved flight changes for flight " + currentFlight.getFlightNumber());
            
            // Notify other views that flight data has been updated
            NavigationManager.getInstance().setSharedData("flightDataUpdated", true);
            
            // Refresh the UI fields to show the saved changes
            populateFlightFields();
            
            // Refresh statistics
            calculateStatistics();
            
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in price or capacity fields");
        } catch (Exception e) {
            System.err.println("Error saving flight changes: " + e.getMessage());
            throw e;
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
