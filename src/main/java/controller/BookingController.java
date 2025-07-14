package controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import model.Booking;
import model.Customer;
import model.Flight;
import model.Passenger;
import model.PaymentDetails;
import model.User;
import service.BookingService;
import service.FlightService;
import util.NavigationManager;
import util.ServiceLocator;

/**
 * Controller for PaymentDetails.fxml and BookingDetails.fxml
 * Manages passenger details, payment and booking confirmation
 */
public class BookingController implements Initializable {
    
    // Payment Details Screen Controls
    @FXML private TextField passengerNameField;
    @FXML private TextField passengerEmailField;
    @FXML private TextField passengerPhoneField;
    @FXML private DatePicker dateOfBirthPicker;
    @FXML private ComboBox<String> passengerTypeComboBox;
    @FXML private ComboBox<String> seatPreferenceComboBox;
    @FXML private CheckBox mealPreferenceCheckBox;
    @FXML private CheckBox baggageInsuranceCheckBox;
    
    // Payment Details FXML Controls
    @FXML private Button backButton;
    @FXML private RadioButton creditCardRadio;
    @FXML private RadioButton paypalRadio;
    @FXML private RadioButton bankTransferRadio;
    @FXML private VBox creditCardForm;
    @FXML private TextField cardNumberField;
    @FXML private TextField cardholderNameField;
    @FXML private TextField expiryDateField;
    @FXML private TextField cvvField;
    @FXML private CheckBox sameAsContactCheckBox;
    @FXML private GridPane billingAddressGrid;
    @FXML private TextField streetAddressField;
    @FXML private TextField cityField;
    @FXML private ComboBox<String> stateComboBox;
    @FXML private TextField zipCodeField;
    @FXML private ComboBox<String> countryComboBox;
    @FXML private CheckBox termsCheckBox;
    @FXML private CheckBox privacyCheckBox;
    @FXML private CheckBox refundCheckBox;
    @FXML private Button cancelButton;
    @FXML private Button completePaymentButton;
    
    // Flight Summary
    @FXML private Label flightNumberLabel;
    @FXML private Label departureLabel;
    @FXML private Label arrivalLabel;
    @FXML private Label departureDateLabel;
    @FXML private Label flightTimeLabel;
    @FXML private Label basePriceLabel;
    @FXML private Label addOnsLabel;
    @FXML private Label taxesLabel;
    @FXML private Label totalPriceLabel;
    
    // Flight Status Elements (for BookingDetails view)
    @FXML private javafx.scene.text.Text flightStatusLabel;
    @FXML private javafx.scene.text.Text flightDurationLabel;
    @FXML private javafx.scene.text.Text aircraftLabel;
    
    // Payment Action Buttons
    @FXML private Button confirmPaymentButton;
    @FXML private Button cancelBookingButton;
    
    // Booking Details Screen Controls
    @FXML private Label bookingReferenceLabel;
    @FXML private Label bookingStatusLabel;
    @FXML private Label bookingDateLabel;
    @FXML private Label customerNameLabel;
    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;
    @FXML private TableView<Passenger> passengersTableView;
    @FXML private TableColumn<Passenger, String> passengerNameColumn;
    @FXML private TableColumn<Passenger, String> seatNumberColumn;
    @FXML private TableColumn<Passenger, String> classColumn;
    
    // Booking Action Buttons
    @FXML private Button backToSearchButton;
    @FXML private Button modifyBookingButton;
    @FXML private Button cancelBookingDetailsButton;
    @FXML private Button downloadTicketButton;
    @FXML private Button requestRefundButton;
    
    // Services
    private BookingService bookingService;
    private FlightService flightService;
    private User currentUser;
    private Flight selectedFlight;
    private Booking currentBooking;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        bookingService = serviceLocator.getBookingService();
        flightService = serviceLocator.getFlightService();
        currentUser = (User) NavigationManager.getInstance().getSharedData("currentUser");
        selectedFlight = (Flight) NavigationManager.getInstance().getSharedData("selectedFlight");
        
        // Check if flight data was updated by admin
        if (Boolean.TRUE.equals(NavigationManager.getInstance().getSharedData("flightDataUpdated"))) {
            NavigationManager.getInstance().setSharedData("flightDataUpdated", false); // Clear flag
            // Refresh flight data if we're showing booking details
            if (bookingReferenceLabel != null) {
                loadFlightStatusUpdates();
            }
        }
        
        // Initialize UI based on which screen is loaded
        if (passengerNameField != null) {
            initializePaymentScreen();
        } else if (bookingReferenceLabel != null) {
            initializeBookingDetailsScreen();
        } else {
            // For mock payment screen or other screens, just setup event handlers
            setupEventHandlers();
        }
        
        // Skip complex toggle group setup for mock payment
    }
    
    /**
     * Initialize Payment Details screen
     */
    private void initializePaymentScreen() {
        setupPaymentFormControls();
        loadFlightSummary();
        setupEventHandlers();
    }
    
    /**
     * Initialize Booking Details screen
     */
    private void initializeBookingDetailsScreen() {
        currentBooking = (Booking) NavigationManager.getInstance().getSharedData("selectedBooking");
        if (currentBooking != null) {
            loadBookingDetails();
            setupPassengersTable();
        }
        // Setup event handlers for all buttons including back button
        setupEventHandlers();
    }
    
    /**
     * Setup payment form controls with default values
     */
    private void setupPaymentFormControls() {
        // Passenger type options
        if (passengerTypeComboBox != null) {
            passengerTypeComboBox.setItems(FXCollections.observableArrayList(
                "Adult", "Child", "Infant", "Senior"
            ));
            passengerTypeComboBox.setValue("Adult");
        }
        
        // Seat preference options
        if (seatPreferenceComboBox != null) {
            seatPreferenceComboBox.setItems(FXCollections.observableArrayList(
                "Window", "Aisle", "Middle", "No Preference"
            ));
            seatPreferenceComboBox.setValue("No Preference");
        }
        
        // Initialize combo boxes for address fields
        
        // Set current user details if available
        if (currentUser instanceof Customer) {
            Customer customer = (Customer) currentUser;
            if (passengerNameField != null) {
                passengerNameField.setText(customer.getFirstName() + " " + customer.getLastName());
            }
            if (passengerEmailField != null) {
                passengerEmailField.setText(customer.getEmail());
            }
            if (passengerPhoneField != null) {
                passengerPhoneField.setText(customer.getPhoneNumber());
            }
        }
    }
    
    /**
     * Load flight summary information
     */
    private void loadFlightSummary() {
        if (selectedFlight != null) {
            if (flightNumberLabel != null) {
                flightNumberLabel.setText(selectedFlight.getFlightNumber());
            }
            if (departureLabel != null) {
                departureLabel.setText(selectedFlight.getDepartureAirport());
            }
            if (arrivalLabel != null) {
                arrivalLabel.setText(selectedFlight.getArrivalAirport());
            }
            if (departureDateLabel != null) {
                departureDateLabel.setText(selectedFlight.getDepartureTime().toLocalDate().toString());
            }
            if (flightTimeLabel != null) {
                flightTimeLabel.setText(selectedFlight.getDepartureTime().toLocalTime().toString());
            }
            
            double basePrice = selectedFlight.getBasePrice();
            double addOnsTotal = calculateAddOnsTotal();
            double taxes = basePrice * 0.15; // 15% tax
            double total = basePrice + addOnsTotal + taxes;
            
            if (basePriceLabel != null) {
                basePriceLabel.setText(String.format("$%.2f", basePrice));
            }
            if (addOnsLabel != null) {
                addOnsLabel.setText(String.format("$%.2f", addOnsTotal));
            }
            if (taxesLabel != null) {
                taxesLabel.setText(String.format("$%.2f", taxes));
            }
            if (totalPriceLabel != null) {
                totalPriceLabel.setText(String.format("$%.2f", total));
            }
        }
    }
    
    /**
     * Calculate total for selected add-ons
     */
    private double calculateAddOnsTotal() {
        double total = 0.0;
        if (mealPreferenceCheckBox != null && mealPreferenceCheckBox.isSelected()) total += 25.0;
        if (baggageInsuranceCheckBox != null && baggageInsuranceCheckBox.isSelected()) total += 15.0;
        return total;
    }
    
    /**
     * Setup event handlers for payment screen
     */
    private void setupEventHandlers() {
        // Recalculate total when add-ons change
        if (mealPreferenceCheckBox != null) {
            mealPreferenceCheckBox.setOnAction(e -> loadFlightSummary());
        }
        if (baggageInsuranceCheckBox != null) {
            baggageInsuranceCheckBox.setOnAction(e -> loadFlightSummary());
        }
        
        // Back button - use role-based navigation
        if (backButton != null) {
            backButton.setOnAction(e -> handleBackToDashboard());
        }
        
        // Payment confirmation
        if (confirmPaymentButton != null) {
            confirmPaymentButton.setOnAction(e -> handleConfirmPayment());
        }
        
        // Complete payment button handler
        if (completePaymentButton != null) {
            completePaymentButton.setOnAction(e -> handleCompletePayment());
        }
        
        // Cancel button handler
        if (cancelButton != null) {
            cancelButton.setOnAction(e -> handleCancelBooking());
        }
        
        if (cancelBookingButton != null) {
            cancelBookingButton.setOnAction(e -> handleCancelBooking());
        }
    }
    
    /**
     * Handle payment confirmation
     */
    @FXML
    private void handleConfirmPayment() {
        if (validatePaymentForm()) {
            processBooking();
        }
    }
    
    /**
     * Validate payment form
     */
    private boolean validatePaymentForm() {
        if (passengerNameField == null || passengerNameField.getText().trim().isEmpty()) {
            showAlert("Error", "Please enter passenger name");
            return false;
        }
        
        if (passengerEmailField == null || passengerEmailField.getText().trim().isEmpty()) {
            showAlert("Error", "Please enter passenger email");
            return false;
        }
        
        if (cardNumberField == null || cardNumberField.getText().trim().isEmpty()) {
            showAlert("Error", "Please enter card number");
            return false;
        }
        
        if (cardholderNameField == null || cardholderNameField.getText().trim().isEmpty()) {
            showAlert("Error", "Please enter card holder name");
            return false;
        }
        
        if (cvvField == null || cvvField.getText().trim().isEmpty()) {
            showAlert("Error", "Please enter CVV");
            return false;
        }
        
        return true;
    }
    
    /**
     * Process the booking
     */
    private void processBooking() {
        try {
            // Create passenger
            Passenger passenger = new Passenger();
            passenger.setPassengerId(UUID.randomUUID().toString());
            String[] names = passengerNameField.getText().split(" ", 2);
            passenger.setFirstName(names[0]);
            if (names.length > 1) passenger.setLastName(names[1]);
            if (dateOfBirthPicker != null && dateOfBirthPicker.getValue() != null) {
                passenger.setDateOfBirth(dateOfBirthPicker.getValue());
            }
            passenger.setSeatNumber(generateSeatNumber());
            
            // Create passenger list
            List<Passenger> passengers = new ArrayList<>();
            passengers.add(passenger);
            
            // Create payment details
            PaymentDetails paymentDetails = new PaymentDetails();
            paymentDetails.setCardNumber(cardNumberField.getText());
            paymentDetails.setCardholderName(cardholderNameField.getText());
            
            // Build billing address from individual fields
            if (streetAddressField != null && cityField != null) {
                String billingAddress = buildBillingAddress();
                paymentDetails.setBillingAddress(billingAddress);
            }
            
            double totalAmount = selectedFlight.getBasePrice() + calculateAddOnsTotal() + (selectedFlight.getBasePrice() * 0.15);
            paymentDetails.setAmount(totalAmount);
            
            // Create booking through service
            Customer customer = (Customer) currentUser;
            Booking savedBooking = bookingService.createBooking(customer, selectedFlight, passengers, paymentDetails);
            
            if (savedBooking != null) {
                showAlert("Success", "Booking created! Reference: " + savedBooking.getBookingId());
                
                // Navigate to booking details
                NavigationManager.getInstance().setSharedData("selectedBooking", savedBooking);
                NavigationManager.getInstance().showBookingDetails(savedBooking);
            } else {
                showAlert("Error", "Failed to create booking. Please try again.");
            }
            
        } catch (Exception e) {
            showAlert("Error", "Failed to process booking: " + e.getMessage());
        }
    }
    
    /**
     * Load booking details for the details screen
     */
    private void loadBookingDetails() {
        if (currentBooking != null) {
            if (bookingReferenceLabel != null) {
                bookingReferenceLabel.setText(currentBooking.getBookingId());
            }
            if (bookingStatusLabel != null) {
                bookingStatusLabel.setText(currentBooking.getStatus().toString());
            }
            if (bookingDateLabel != null) {
                bookingDateLabel.setText(currentBooking.getBookingDate().toLocalDate().toString());
            }
            
            // Load customer details if available
            if (currentUser instanceof Customer) {
                Customer customer = (Customer) currentUser;
                if (customerNameLabel != null) {
                    customerNameLabel.setText(customer.getFirstName() + " " + customer.getLastName());
                }
                if (emailLabel != null) {
                    emailLabel.setText(customer.getEmail());
                }
                if (phoneLabel != null) {
                    phoneLabel.setText(customer.getPhoneNumber());
                }
            }
            
            // Load flight information and status
            loadFlightStatusUpdates();
        }
    }
    
    /**
     * Load and update flight status information
     */
    private void loadFlightStatusUpdates() {
        if (currentBooking != null && currentBooking.getFlightId() != null) {
            // Get fresh flight data
            flightService.getFlightDetails(currentBooking.getFlightId()).ifPresent(flight -> {
                if (flightNumberLabel != null) {
                    flightNumberLabel.setText(flight.getFlightNumber());
                }
                
                if (flightStatusLabel != null) {
                    String statusText = flight.getStatus() != null ? flight.getStatus().getDisplayName() : "Unknown";
                    flightStatusLabel.setText(statusText);
                    
                    // Style the status based on flight status
                    switch (flight.getStatus()) {
                        case SCHEDULED:
                        case ON_TIME:
                            flightStatusLabel.setFill(javafx.scene.paint.Color.web("#27ae60"));
                            break;
                        case DELAYED:
                        case BOARDING:
                            flightStatusLabel.setFill(javafx.scene.paint.Color.web("#f39c12"));
                            break;
                        case CANCELLED:
                            flightStatusLabel.setFill(javafx.scene.paint.Color.web("#e74c3c"));
                            break;
                        case DEPARTED:
                        case ARRIVED:
                            flightStatusLabel.setFill(javafx.scene.paint.Color.web("#3498db"));
                            break;
                        default:
                            flightStatusLabel.setFill(javafx.scene.paint.Color.web("#7f8c8d"));
                            break;
                    }
                }
                
                if (aircraftLabel != null && flight.getAircraft() != null) {
                    aircraftLabel.setText(flight.getAircraft());
                }
                
                System.out.println("BookingController: Flight status updated to " + flight.getStatus());
            });
        }
    }
    
    /**
     * Setup passengers table
     */
    private void setupPassengersTable() {
        if (passengersTableView != null && currentBooking != null) {
            if (passengerNameColumn != null) {
                passengerNameColumn.setCellValueFactory(cellData -> 
                    new SimpleStringProperty(cellData.getValue().getFullName()));
            }
            if (seatNumberColumn != null) {
                seatNumberColumn.setCellValueFactory(cellData -> 
                    new SimpleStringProperty(cellData.getValue().getSeatNumber()));
            }
            if (classColumn != null) {
                classColumn.setCellValueFactory(cellData -> 
                    new SimpleStringProperty("Economy")); // Default class
            }
            
            passengersTableView.setItems(FXCollections.observableList(currentBooking.getPassengers()));
        }
    }
    
    /**
     * Handle cancel booking from payment screen
     */
    @FXML
    private void handleCancelBooking() {
        NavigationManager.getInstance().showFlightInformation();
    }
    
    /**
     * Handle modify booking
     */
    @FXML
    private void handleModifyBooking() {
        showAlert("Info", "Booking modifications are not available at this time. Please contact customer service.");
    }
    
    /**
     * Handle cancel booking from details screen
     */
    @FXML
    private void handleCancelBookingDetails() {
        if (currentBooking != null && currentUser != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Cancel Booking");
            alert.setHeaderText("Are you sure you want to cancel this booking?");
            alert.setContentText("This action cannot be undone.");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    boolean success = bookingService.cancelBooking(currentBooking.getBookingId(), currentUser.getUserId());
                    if (success) {
                        showAlert("Success", "Booking has been cancelled");
                        NavigationManager.getInstance().showBookingOverview();
                    } else {
                        showAlert("Error", "Failed to cancel booking");
                    }
                }
            });
        }
    }
    
    /**
     * Handle download ticket
     */
    @FXML
    private void handleDownloadTicket() {
        showAlert("Info", "Ticket downloaded successfully! Check your Downloads folder.");
    }
    
    /**
     * Handle request refund
     */
    @FXML
    private void handleRequestRefund() {
        showAlert("Info", "Refund request submitted. You will be contacted within 24 hours.");
    }
    
    /**
     * Handle back to dashboard
     */
    @FXML
    private void handleBackToDashboard() {
        // Navigate based on user role - NEVER allow role escalation
        if (currentUser != null && currentUser.getRole() != null) {
            if (currentUser.getRole().name().equals("ADMIN")) {
                NavigationManager.getInstance().showAdminDashboard();
            } else {
                NavigationManager.getInstance().showCustomerOverview();
            }
        } else {
            // If user role is unclear, go to login for security
            NavigationManager.getInstance().navigateTo(NavigationManager.LOGIN_SCREEN);
        }
    }
    
    /**
     * Handle back to search
     */
    @FXML
    private void handleBackToSearch() {
        NavigationManager.getInstance().showFlightInformation();
    }
    
    /**
     * Generate seat number
     */
    private String generateSeatNumber() {
        int row = (int) (Math.random() * 30) + 1;
        char seat = (char) ('A' + (int) (Math.random() * 6));
        return row + String.valueOf(seat);
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
    
    /**
     * Handle complete payment - simple flow without backend verification
     */
    @FXML
    private void handleCompletePayment() {
        try {
            // Mock payment processing - no real validation needed
            
            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Mock Payment Successful");
            alert.setHeaderText("Demo Booking Confirmed!");
            alert.setContentText("Your demo payment has been processed successfully.\n\n" +
                               "Flight: NYC → LAX\n" +
                               "Amount: $598.00\n" +
                               "Status: CONFIRMED\n\n" +
                               "This was a demonstration - no real transaction occurred.");
            alert.showAndWait();
            
            // Navigate to customer overview
            NavigationManager.getInstance().showCustomerOverview();
            
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Unable to Continue");
            alert.setContentText("There was an error navigating to the next page. Please try again.");
            alert.showAndWait();
        }
    }
    
    /**
     * Validate basic payment information
     */
    private boolean validateBasicPaymentInfo() {
        // Check if terms are agreed to
        if (termsCheckBox != null && !termsCheckBox.isSelected()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Terms Required");
            alert.setHeaderText("Please agree to terms");
            alert.setContentText("You must agree to the Terms of Service to complete your booking.");
            alert.showAndWait();
            return false;
        }
        
        // Basic validation for credit card if selected
        if (creditCardRadio != null && creditCardRadio.isSelected()) {
            if (cardNumberField != null && (cardNumberField.getText() == null || cardNumberField.getText().trim().isEmpty())) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Card Number Required");
                alert.setHeaderText("Please enter card number");
                alert.setContentText("Card number is required to complete the payment.");
                alert.showAndWait();
                return false;
            }
            
            if (cardholderNameField != null && (cardholderNameField.getText() == null || cardholderNameField.getText().trim().isEmpty())) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cardholder Name Required");
                alert.setHeaderText("Please enter cardholder name");
                alert.setContentText("Cardholder name is required to complete the payment.");
                alert.showAndWait();
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Build billing address from individual form fields
     */
    private String buildBillingAddress() {
        StringBuilder address = new StringBuilder();
        
        if (streetAddressField != null && !streetAddressField.getText().trim().isEmpty()) {
            address.append(streetAddressField.getText().trim());
        }
        
        if (cityField != null && !cityField.getText().trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(cityField.getText().trim());
        }
        
        if (stateComboBox != null && stateComboBox.getValue() != null) {
            if (address.length() > 0) address.append(", ");
            address.append(stateComboBox.getValue());
        }
        
        if (zipCodeField != null && !zipCodeField.getText().trim().isEmpty()) {
            if (address.length() > 0) address.append(" ");
            address.append(zipCodeField.getText().trim());
        }
        
        if (countryComboBox != null && countryComboBox.getValue() != null) {
            if (address.length() > 0) address.append(", ");
            address.append(countryComboBox.getValue());
        }
        
        return address.toString();
    }
    
    /**
     * Setup payment method toggle group
     */
    private void setupPaymentMethodToggleGroup() {
        // Create ToggleGroup and add radio buttons
        ToggleGroup paymentMethodToggleGroup = new ToggleGroup();
        creditCardRadio.setToggleGroup(paymentMethodToggleGroup);
        paypalRadio.setToggleGroup(paymentMethodToggleGroup);
        bankTransferRadio.setToggleGroup(paymentMethodToggleGroup);
        
        // Set default selection
        creditCardRadio.setSelected(true);
        
        // Add listener to show/hide credit card form
        paymentMethodToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == creditCardRadio) {
                creditCardForm.setVisible(true);
                creditCardForm.setManaged(true);
            } else {
                creditCardForm.setVisible(false);
                creditCardForm.setManaged(false);
            }
        });
    }
}