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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
    @FXML private CheckBox priorityBoardingCheckBox;
    
    // Payment Information
    @FXML private TextField cardNumberField;
    @FXML private TextField cardHolderField;
    @FXML private ComboBox<String> expiryMonthComboBox;
    @FXML private ComboBox<String> expiryYearComboBox;
    @FXML private TextField cvvField;
    @FXML private ComboBox<String> cardTypeComboBox;
    @FXML private TextField billingAddressField;
    
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
    @FXML private Button backButton;
    @FXML private Button modifyBookingButton;
    @FXML private Button cancelBookingDetailsButton;
    @FXML private Button downloadTicketButton;
    @FXML private Button requestRefundButton;
    @FXML private Button backToSearchButton;
    
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
        
        // Initialize UI based on which screen is loaded
        if (passengerNameField != null) {
            initializePaymentScreen();
        } else if (bookingReferenceLabel != null) {
            initializeBookingDetailsScreen();
        } else {
            // For overview screen or other screens, just setup event handlers
            setupEventHandlers();
        }
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
        
        // Card type options
        if (cardTypeComboBox != null) {
            cardTypeComboBox.setItems(FXCollections.observableArrayList(
                "Visa", "Mastercard", "American Express", "Discover"
            ));
        }
        
        // Expiry month/year
        setupExpiryDateComboBoxes();
        
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
     * Setup expiry date combo boxes
     */
    private void setupExpiryDateComboBoxes() {
        if (expiryMonthComboBox != null) {
            ObservableList<String> months = FXCollections.observableArrayList();
            for (int i = 1; i <= 12; i++) {
                months.add(String.format("%02d", i));
            }
            expiryMonthComboBox.setItems(months);
        }
        
        if (expiryYearComboBox != null) {
            ObservableList<String> years = FXCollections.observableArrayList();
            int currentYear = LocalDate.now().getYear();
            for (int i = currentYear; i <= currentYear + 10; i++) {
                years.add(String.valueOf(i));
            }
            expiryYearComboBox.setItems(years);
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
        if (priorityBoardingCheckBox != null && priorityBoardingCheckBox.isSelected()) total += 20.0;
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
        if (priorityBoardingCheckBox != null) {
            priorityBoardingCheckBox.setOnAction(e -> loadFlightSummary());
        }
        
        // Back button
        if (backButton != null) {
            backButton.setOnAction(e -> handleBackToDashboard());
        }
        
        // Payment confirmation
        if (confirmPaymentButton != null) {
            confirmPaymentButton.setOnAction(e -> handleConfirmPayment());
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
        
        if (cardHolderField == null || cardHolderField.getText().trim().isEmpty()) {
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
            paymentDetails.setCardholderName(cardHolderField.getText());
            if (billingAddressField != null) {
                paymentDetails.setBillingAddress(billingAddressField.getText());
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
} 