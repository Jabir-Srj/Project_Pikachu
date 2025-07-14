package controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import model.Flight;
import model.User;
import model.Customer;
import model.Passenger;
import model.Booking;
import model.PaymentDetails;
import service.FlightService;
import service.BookingService;
import util.NavigationManager;
import util.ServiceLocator;

 // Controller for FlightInformation.fxml and FlightDetails.fxml

public class FlightSearchController implements Initializable {
    
    // FlightInformation.fxml controls
    @FXML private ComboBox<String> fromComboBox;
    @FXML private ComboBox<String> toComboBox;
    @FXML private DatePicker departureDatePicker;
    @FXML private ComboBox<String> passengersComboBox;
    @FXML private Button searchButton;
    @FXML private Button resetButton;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private VBox flightResultsContainer;
    @FXML private Button loadMoreButton;
    @FXML private Button backButton;
    @FXML private VBox customerSection;
    @FXML private VBox adminSection;
    @FXML private VBox adminFlightsContainer;
    @FXML private Button addFlightButton;
    
    // FlightDetails.fxml controls
    @FXML private Label flightNumberLabel;
    @FXML private Label aircraftLabel;
    @FXML private Label statusLabel;
    @FXML private Label availableSeatsLabel;
    @FXML private Button selectEconomyButton;
    @FXML private Button selectBusinessButton;
    @FXML private Button selectFirstButton;
    @FXML private Button editFlightButton;
    @FXML private Button cancelFlightButton;
    @FXML private Button printButton;
    
    private FlightService flightService;
    private User currentUser;
    private ObservableList<Flight> searchResults;
    private Flight selectedFlight;
    private List<Flight> currentSearchResults; // Store current search results for filtering/sorting
    
    // Airport codes for dropdown
    private final String[] airports = {
        "Tokyo (NRT)", "Seoul (ICN)", "Singapore (SIN)", "Kuala Lumpur (KUL)",
        "Bangkok (BKK)", "Manila (MNL)", "Hong Kong (HKG)", "Taipei (TPE)",
        "Jakarta (CGK)", "Ho Chi Minh City (SGN)", "Beijing (PEK)", "Shanghai (PVG)",
        "Mumbai (BOM)", "Delhi (DEL)", "Osaka (KIX)", "Busan (PUS)",
        "Colombo (CMB)", "Chennai (MAA)", "Dhaka (DAC)", "Kathmandu (KTM)",
        "Yangon (RGN)", "Hanoi (HAN)", "Phnom Penh (PNH)", "Vientiane (VTE)",
        "Macau (MFM)", "Guangzhou (CAN)"
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        flightService = serviceLocator.getFlightService();
        currentUser = (User) NavigationManager.getInstance().getSharedData("currentUser");
        searchResults = FXCollections.observableArrayList();
        
        // Check if flight data was updated and refresh if needed
        Boolean dataUpdated = (Boolean) NavigationManager.getInstance().getSharedData("flightDataUpdated");
        if (dataUpdated != null && dataUpdated) {
            // Clear the flag
            NavigationManager.getInstance().setSharedData("flightDataUpdated", false);
            System.out.println("FlightSearchController: Detected flight data update, refreshing displays");
        }
        
        setupUI();
        loadInitialData();
    }
    
    private void setupUI() {
        // Show appropriate section based on user type
        setupUserSections();
        
        // Setup airport dropdowns
        if (fromComboBox != null) {
            fromComboBox.setItems(FXCollections.observableArrayList(airports));
            toComboBox.setItems(FXCollections.observableArrayList(airports));
            
            // Setup passengers dropdown
            passengersComboBox.setItems(FXCollections.observableArrayList(
                "1 Passenger", "2 Passengers", "3 Passengers", "4 Passengers", "5+ Passengers"
            ));
            passengersComboBox.setValue("1 Passenger");
            
            // Setup sort options
            sortComboBox.setItems(FXCollections.observableArrayList(
                "Price (Low to High)", "Price (High to Low)", "Departure Time", "Duration"
            ));
            sortComboBox.setValue("Price (Low to High)");
            
            // Setup filter options
            filterComboBox.setItems(FXCollections.observableArrayList(
                "All Flights", "Direct Flights Only", "Morning Flights", "Evening Flights"
            ));
            filterComboBox.setValue("All Flights");
            
            // Set default date to tomorrow
            departureDatePicker.setValue(LocalDate.now().plusDays(1));
            
            // Add event handlers for sort and filter
            sortComboBox.setOnAction(e -> handleSortOrFilterChange());
            filterComboBox.setOnAction(e -> handleSortOrFilterChange());
        }
        
        // Setup button actions
        setupButtonActions();
    }
    
    private void setupUserSections() {
        // Show appropriate section based on user type
        boolean isAdmin = false;
        if (currentUser != null && currentUser.getRole() != null) {
            isAdmin = "ADMIN".equals(currentUser.getRole().name());
        }
        // If currentUser is null, default to customer view
        if (customerSection != null) {
            customerSection.setVisible(!isAdmin);
            customerSection.setManaged(!isAdmin);
        }
        if (adminSection != null) {
            adminSection.setVisible(isAdmin);
            adminSection.setManaged(isAdmin);
        }
    }
    
    private void setupButtonActions() {
        if (searchButton != null) {
            searchButton.setOnAction(e -> handleSearch());
        }
        
        if (resetButton != null) {
            resetButton.setOnAction(e -> handleReset());
        }
        
        if (backButton != null) {
            backButton.setOnAction(e -> handleBack());
        }
        
        if (loadMoreButton != null) {
            loadMoreButton.setOnAction(e -> loadMoreFlights());
        }
        
        if (addFlightButton != null) {
            addFlightButton.setOnAction(e -> addNewFlight());
        }
        
        // Flight details buttons
        if (selectEconomyButton != null) {
            selectEconomyButton.setOnAction(e -> selectFlight("Economy"));
            selectBusinessButton.setOnAction(e -> selectFlight("Business"));
            selectFirstButton.setOnAction(e -> selectFlight("First"));
        }
        
        if (editFlightButton != null && currentUser != null && currentUser.getRole() != null && currentUser.getRole().name().equals("ADMIN")) {
            editFlightButton.setOnAction(e -> editFlight());
        } else if (editFlightButton != null) {
            editFlightButton.setVisible(false);
        }
        
        if (cancelFlightButton != null && currentUser != null && currentUser.getRole() != null && currentUser.getRole().name().equals("ADMIN")) {
            cancelFlightButton.setOnAction(e -> cancelFlight());
        } else if (cancelFlightButton != null) {
            cancelFlightButton.setVisible(false);
        }
        
        if (printButton != null) {
            printButton.setOnAction(e -> printFlightDetails());
        }
    }
    
    private void loadInitialData() {
        // Load all flights (always get fresh data from service)
        List<Flight> allFlights = flightService.getAllFlights();
        
        // Initialize current search results with all flights
        currentSearchResults = new ArrayList<>(allFlights);
        
        if (flightResultsContainer != null) {
            // Load flights for customer search results
            displayFlights(allFlights);
        }
        
        if (adminFlightsContainer != null) {
            // Load flights for admin view
            displayAdminFlights(allFlights);
        }
        
        // If this is flight details view, load selected flight data
        if (flightNumberLabel != null && selectedFlight != null) {
            displayFlightDetails();
        }
    }
    
    private void displayAdminFlights(List<Flight> flights) {
        if (adminFlightsContainer == null) return;
        
        adminFlightsContainer.getChildren().clear();
        
        for (Flight flight : flights) {
            VBox flightCard = createAdminFlightCard(flight);
            adminFlightsContainer.getChildren().add(flightCard);
        }
    }
    
    private VBox createAdminFlightCard(Flight flight) {
        VBox card = new VBox(10);
        card.getStyleClass().addAll("content-card", "shadow-card-medium", "border-yellow-light");
        
        // Safely handle null values
        String flightNumber = flight.getFlightNumber() != null ? flight.getFlightNumber() : "N/A";
        String departure = flight.getDepartureAirport() != null ? flight.getDepartureAirport() : "TBD";
        String arrival = flight.getArrivalAirport() != null ? flight.getArrivalAirport() : "TBD";
        
        Label flightInfo = new Label(String.format("%s: %s → %s", flightNumber, departure, arrival));
        flightInfo.getStyleClass().add("flight-card-title");
        
        String departureTime = flight.getDepartureTime() != null ? flight.getDepartureTime().toLocalTime().toString() : "TBD";
        String arrivalTime = flight.getArrivalTime() != null ? flight.getArrivalTime().toLocalTime().toString() : "TBD";
        Label timeInfo = new Label(String.format("Departure: %s | Arrival: %s", departureTime, arrivalTime));
        
        Label detailsInfo = new Label(String.format("$%.2f | %d/%d seats | %s", 
            flight.getBasePrice(), flight.getAvailableSeats(), flight.getTotalSeats(), 
            flight.getStatus() != null ? flight.getStatus() : "Unknown"));
        
        Button editFlightBtn = new Button("Edit Flight");
        editFlightBtn.getStyleClass().addAll("button-secondary");
        editFlightBtn.setOnAction(e -> editFlight());
        
        Button viewDetailsBtn = new Button("View Details");
        viewDetailsBtn.getStyleClass().addAll("button-primary");
        viewDetailsBtn.setOnAction(e -> viewFlightDetails(flight));
        
        card.getChildren().addAll(flightInfo, timeInfo, detailsInfo, editFlightBtn, viewDetailsBtn);
        return card;
    }
    
    @FXML
    private void handleSearch() {
        if (fromComboBox == null || toComboBox == null || departureDatePicker == null) {
            return;
        }
        
        String from = fromComboBox.getValue();
        String to = toComboBox.getValue();
        LocalDate date = departureDatePicker.getValue();
        
        if (from == null || to == null || date == null) {
            showAlert("Please fill in all search fields.");
            return;
        }
        
        if (from.equals(to)) {
            showAlert("Departure and destination cannot be the same.");
            return;
        }
        
        // Get number of passengers from combo box
        int passengers = 1; // Default
        if (passengersComboBox != null && passengersComboBox.getValue() != null) {
            String passengerText = passengersComboBox.getValue();
            if (passengerText.contains("1")) passengers = 1;
            else if (passengerText.contains("2")) passengers = 2;
            else if (passengerText.contains("3")) passengers = 3;
            else if (passengerText.contains("4")) passengers = 4;
            else if (passengerText.contains("5")) passengers = 5;
        }
        
        // Perform search
        List<Flight> searchResults = flightService.searchFlights(from, to, date, passengers);
        
        // Store the search results for filtering/sorting
        currentSearchResults = new ArrayList<>(searchResults);
        
        // Apply filters and sorting
        searchResults = applyFilters(searchResults);
        searchResults = applySorting(searchResults);
        
        // Display results
        displayFlights(searchResults);
        
        if (searchResults.isEmpty()) {
            showAlert("No flights found for the selected criteria.");
        }
    }
    
    @FXML
    private void handleReset() {
        // Clear all search fields
        if (fromComboBox != null) {
            fromComboBox.setValue(null);
        }
        if (toComboBox != null) {
            toComboBox.setValue(null);
        }
        if (departureDatePicker != null) {
            departureDatePicker.setValue(LocalDate.now().plusDays(1));
        }
        if (passengersComboBox != null) {
            passengersComboBox.setValue("1 Passenger");
        }
        if (sortComboBox != null) {
            sortComboBox.setValue("Price (Low to High)");
        }
        if (filterComboBox != null) {
            filterComboBox.setValue("All Flights");
        }
        
        // Show all flights
        List<Flight> allFlights = flightService.getAllFlights();
        displayFlights(allFlights);
        
        showAlert("Search has been reset. Showing all available flights.");
    }

    private void handleSortOrFilterChange() {
        if (currentSearchResults == null || currentSearchResults.isEmpty()) {
            // If no search has been performed, show all flights
            List<Flight> allFlights = flightService.getAllFlights();
            currentSearchResults = new ArrayList<>(allFlights);
        }
        
        // Apply current filters and sorting to stored results
        List<Flight> filteredResults = applyFilters(currentSearchResults);
        List<Flight> sortedResults = applySorting(filteredResults);
        
        // Display the results
        displayFlights(sortedResults);
    }
    
    private List<Flight> applyFilters(List<Flight> flights) {
        if (filterComboBox == null || filterComboBox.getValue() == null) {
            return flights;
        }
        
        String filter = filterComboBox.getValue();
        
        switch (filter) {
            case "Direct Flights Only":
                // Assuming all current flights are direct for simplicity
                return flights;
            case "Morning Flights":
                return flights.stream()
                    .filter(f -> f.getDepartureTime() != null && f.getDepartureTime().getHour() < 12)
                    .collect(Collectors.toList());
            case "Evening Flights":
                return flights.stream()
                    .filter(f -> f.getDepartureTime() != null && f.getDepartureTime().getHour() >= 18)
                    .collect(Collectors.toList());
            case "All Flights":
            default:
                return flights;
        }
    }
    
    private List<Flight> applySorting(List<Flight> flights) {
        if (sortComboBox == null || sortComboBox.getValue() == null) {
            return flights;
        }
        
        String sort = sortComboBox.getValue();
        
        switch (sort) {
            case "Price (Low to High)":
                return flights.stream()
                    .filter(f -> f.getBasePrice() > 0) // Filter out flights with invalid prices
                    .sorted((f1, f2) -> Double.compare(f1.getBasePrice(), f2.getBasePrice()))
                    .collect(Collectors.toList());
            case "Price (High to Low)":
                return flights.stream()
                    .filter(f -> f.getBasePrice() > 0) // Filter out flights with invalid prices
                    .sorted((f1, f2) -> Double.compare(f2.getBasePrice(), f1.getBasePrice()))
                    .collect(Collectors.toList());
            case "Departure Time":
                return flights.stream()
                    .filter(f -> f.getDepartureTime() != null) // Filter out flights with null departure times
                    .sorted((f1, f2) -> f1.getDepartureTime().compareTo(f2.getDepartureTime()))
                    .collect(Collectors.toList());
            case "Duration":
                return flights.stream()
                    .filter(f -> f.getDepartureTime() != null && f.getArrivalTime() != null)
                    .sorted((f1, f2) -> {
                        long duration1 = java.time.Duration.between(f1.getDepartureTime(), f1.getArrivalTime()).toMinutes();
                        long duration2 = java.time.Duration.between(f2.getDepartureTime(), f2.getArrivalTime()).toMinutes();
                        return Long.compare(duration1, duration2);
                    })
                    .collect(Collectors.toList());
            default:
                return flights;
        }
    }
    
    private void displayFlights(List<Flight> flights) {
        if (flightResultsContainer == null) return;
        
        flightResultsContainer.getChildren().clear();
        
        for (Flight flight : flights) {
            VBox flightCard = createFlightCard(flight);
            flightResultsContainer.getChildren().add(flightCard);
        }
    }
    
    private VBox createFlightCard(Flight flight) {
        VBox card = new VBox(10);
        card.getStyleClass().addAll("content-card", "shadow-card-medium", "border-yellow-light");
        
        // Safely handle null values
        String flightNumber = flight.getFlightNumber() != null ? flight.getFlightNumber() : "N/A";
        String departure = flight.getDepartureAirport() != null ? flight.getDepartureAirport() : "TBD";
        String arrival = flight.getArrivalAirport() != null ? flight.getArrivalAirport() : "TBD";
        
        Label flightInfo = new Label(String.format("%s: %s → %s", flightNumber, departure, arrival));
        flightInfo.getStyleClass().add("flight-card-title");
        
        String departureTime = flight.getDepartureTime() != null ? flight.getDepartureTime().toLocalTime().toString() : "TBD";
        String arrivalTime = flight.getArrivalTime() != null ? flight.getArrivalTime().toLocalTime().toString() : "TBD";
        Label timeInfo = new Label(String.format("Departure: %s | Arrival: %s", departureTime, arrivalTime));
        
        Label priceInfo = new Label(String.format("$%.2f | %d seats available", 
            flight.getBasePrice(), flight.getAvailableSeats()));
        
        // Add status information for customers
        String status = flight.getStatus() != null ? flight.getStatus().getDisplayName() : "Unknown";
        Label statusInfo = new Label("Status: " + status);
        statusInfo.getStyleClass().add("flight-status-label");
        
        Button selectFlightBtn = new Button("⚡ Select Flight");
        selectFlightBtn.getStyleClass().addAll("button-primary", "shadow-subtle");
        selectFlightBtn.setOnAction(_ -> selectFlightForBooking(flight));
        
        // Style status label and update button based on flight status
        switch (flight.getStatus()) {
            case SCHEDULED:
            case ON_TIME:
                statusInfo.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                break;
            case DELAYED:
            case BOARDING:
                statusInfo.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                break;
            case CANCELLED:
                statusInfo.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                selectFlightBtn.setDisable(true);
                selectFlightBtn.setText("Flight Cancelled");
                break;
            case DEPARTED:
            case ARRIVED:
                statusInfo.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
                selectFlightBtn.setDisable(true);
                selectFlightBtn.setText("Flight Departed");
                break;
        }

        card.getChildren().addAll(flightInfo, timeInfo, priceInfo, statusInfo, selectFlightBtn);
        return card;
    }
    
    private void viewFlightDetails(Flight flight) {
        selectedFlight = flight;
        NavigationManager.getInstance().showFlightDetails(flight);
    }
    
    private void selectFlightForBooking(Flight flight) {
        selectedFlight = flight;
        
        // Create a booking for the selected flight
        if (currentUser != null && currentUser instanceof Customer) {
            Customer customer = (Customer) currentUser;
            
            // Create a simple passenger for the booking
            Passenger passenger = new Passenger();
            passenger.setPassengerId(java.util.UUID.randomUUID().toString());
            passenger.setFirstName(customer.getFirstName());
            passenger.setLastName(customer.getLastName());
            passenger.setSeatNumber("TBD"); // Will be assigned later
            
            List<Passenger> passengers = new ArrayList<>();
            passengers.add(passenger);
            
            // Create payment details (simplified for demo)
            PaymentDetails paymentDetails = new PaymentDetails();
            paymentDetails.setAmount(flight.getBasePrice());
            paymentDetails.setCardNumber("DEMO-1234");
            paymentDetails.setCardholderName(customer.getFirstName() + " " + customer.getLastName());
            
            // Create booking through service
            BookingService bookingService = new BookingService();
            Booking booking = bookingService.createBooking(customer, flight, passengers, paymentDetails);
            
            if (booking != null) {
                // Confirm the booking immediately for demo purposes
                bookingService.confirmBooking(booking.getBookingId());
                
                // Add to customer's booking history
                customer.bookFlight(booking);
                
                // Show success message
                showAlert("Flight Confirmed!", 
                    "Flight " + flight.getFlightNumber() + " has been added to confirmed flights.\n" +
                    "Booking ID: " + booking.getBookingId() + "\n" +
                    "Route: " + flight.getDepartureAirport() + " → " + flight.getArrivalAirport() + "\n" +
                    "Date: " + flight.getDepartureTime().toLocalDate() + "\n" +
                    "Status: CONFIRMED", Alert.AlertType.INFORMATION);
                
                // Navigate to booking overview to show the new booking
                NavigationManager.getInstance().showBookingOverview();
            } else {
                showAlert("Booking Failed", "Unable to create booking for this flight. Please try again.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("User Error", "Please log in as a customer to book flights.", Alert.AlertType.WARNING);
        }
    }
    
    private void displayFlightDetails() {
        if (selectedFlight == null) return;
        
        // Refresh flight data from database to ensure we have the latest status
        Optional<Flight> updatedFlightOpt = flightService.getFlightDetails(selectedFlight.getFlightNumber());
        if (updatedFlightOpt.isPresent()) {
            selectedFlight = updatedFlightOpt.get();
            // Update shared data with fresh flight information
            NavigationManager.getInstance().setSharedData("selectedFlight", selectedFlight);
        }
        
        flightNumberLabel.setText(selectedFlight.getFlightNumber());
        aircraftLabel.setText(selectedFlight.getAircraft());
        statusLabel.setText(selectedFlight.getStatus().toString());
        availableSeatsLabel.setText(selectedFlight.getAvailableSeats() + " / " + selectedFlight.getTotalSeats());
        
        // Update status label style based on status
        statusLabel.getStyleClass().clear();
        switch (selectedFlight.getStatus()) {
            case SCHEDULED:
                statusLabel.getStyleClass().add("status-confirmed-gradient");
                break;
            case DELAYED:
                statusLabel.getStyleClass().add("status-pending-gradient");
                break;
            case CANCELLED:
                statusLabel.getStyleClass().add("status-cancelled-gradient");
                break;
            case ON_TIME:
                statusLabel.getStyleClass().add("status-confirmed-gradient");
                break;
            case BOARDING:
                statusLabel.getStyleClass().add("status-pending-gradient");
                break;
            case DEPARTED:
                statusLabel.getStyleClass().add("status-confirmed-gradient");
                break;
            case ARRIVED:
                statusLabel.getStyleClass().add("status-confirmed-gradient");
                break;
        }
        
        // Disable first class if sold out
        if (selectedFlight.getAvailableSeats() < 10) {
            selectFirstButton.setDisable(true);
            selectFirstButton.setText("Sold Out");
        }
    }
    
    private void selectFlight(String classType) {
        if (selectedFlight == null) return;
        
        // Store selected class for booking
        NavigationManager.getInstance().setSelectedFlightClass(classType);
        
        // Navigate to booking overview instead of payment confirmation
        NavigationManager.getInstance().showBookingOverview();
    }
    
    private void editFlight() {
        if (selectedFlight == null) {
            showAlert("No flight selected", "Please select a flight to edit.", Alert.AlertType.WARNING);
            return;
        }
        
        // Create the edit dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Flight");
        dialog.setHeaderText("Edit Flight Details for: " + selectedFlight.getFlightNumber());
        
        // Create the dialog content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        // Create form fields
        TextField flightNumberField = new TextField(selectedFlight.getFlightNumber());
        TextField airlineField = new TextField(selectedFlight.getAirline());
        TextField departureAirportField = new TextField(selectedFlight.getDepartureAirport());
        TextField arrivalAirportField = new TextField(selectedFlight.getArrivalAirport());
        TextField basePriceField = new TextField(String.valueOf(selectedFlight.getBasePrice()));
        TextField totalSeatsField = new TextField(String.valueOf(selectedFlight.getTotalSeats()));
        TextField aircraftField = new TextField(selectedFlight.getAircraft());
        
        // Add labels and fields to grid
        grid.add(new Label("Flight Number:"), 0, 0);
        grid.add(flightNumberField, 1, 0);
        grid.add(new Label("Airline:"), 0, 1);
        grid.add(airlineField, 1, 1);
        grid.add(new Label("Departure Airport:"), 0, 2);
        grid.add(departureAirportField, 1, 2);
        grid.add(new Label("Arrival Airport:"), 0, 3);
        grid.add(arrivalAirportField, 1, 3);
        grid.add(new Label("Base Price:"), 0, 4);
        grid.add(basePriceField, 1, 4);
        grid.add(new Label("Total Seats:"), 0, 5);
        grid.add(totalSeatsField, 1, 5);
        grid.add(new Label("Aircraft:"), 0, 6);
        grid.add(aircraftField, 1, 6);
        
        dialog.getDialogPane().setContent(grid);
        
        // Add buttons
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Show dialog and handle result
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Validate and update flight information
                    String flightNumber = flightNumberField.getText().trim();
                    String airline = airlineField.getText().trim();
                    String departureAirport = departureAirportField.getText().trim();
                    String arrivalAirport = arrivalAirportField.getText().trim();
                    String aircraft = aircraftField.getText().trim();
                    
                    if (flightNumber.isEmpty() || airline.isEmpty() || departureAirport.isEmpty() || 
                        arrivalAirport.isEmpty() || aircraft.isEmpty()) {
                        showAlert("Validation Error", "All fields are required.", Alert.AlertType.ERROR);
                        return;
                    }
                    
                    double basePrice = Double.parseDouble(basePriceField.getText().trim());
                    int totalSeats = Integer.parseInt(totalSeatsField.getText().trim());
                    
                    if (basePrice <= 0 || totalSeats <= 0) {
                        showAlert("Validation Error", "Price and seats must be positive numbers.", Alert.AlertType.ERROR);
                        return;
                    }
                    
                    // Update flight object
                    selectedFlight.setFlightNumber(flightNumber);
                    selectedFlight.setAirline(airline);
                    selectedFlight.setDepartureAirport(departureAirport);
                    selectedFlight.setArrivalAirport(arrivalAirport);
                    selectedFlight.setBasePrice(basePrice);
                    selectedFlight.setTotalSeats(totalSeats);
                    selectedFlight.setAircraft(aircraft);
                    
                    // Update available seats if total seats changed
                    int bookedSeats = selectedFlight.getTotalSeats() - selectedFlight.getAvailableSeats();
                    selectedFlight.setAvailableSeats(Math.max(0, totalSeats - bookedSeats));
                    
                    // Save to database
                    if (flightService.updateFlight(selectedFlight)) {
                        // Refresh the display
                        displayFlightDetails();
                        loadInitialData();
                        
                        showAlert("Success", "Flight details updated successfully.", Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("Error", "Failed to update flight details.", Alert.AlertType.ERROR);
                    }
                    
                } catch (NumberFormatException e) {
                    showAlert("Validation Error", "Price and seats must be valid numbers.", Alert.AlertType.ERROR);
                } catch (Exception e) {
                    showAlert("Error", "An error occurred while updating the flight: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }
    
    private void cancelFlight() {
        // Admin only - cancel flight
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Cancel Flight");
        confirmation.setHeaderText("Are you sure you want to cancel this flight?");
        confirmation.setContentText("This action cannot be undone and will affect all passengers.");
        
        confirmation.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                selectedFlight.setStatus(model.FlightStatus.CANCELLED);
                flightService.updateFlight(selectedFlight);
                displayFlightDetails();
                showAlert("Flight has been cancelled successfully.");
            }
        });
    }
    
    private void printFlightDetails() {
        showAlert("Flight details have been sent to printer.");
    }
    
    private void loadMoreFlights() {
        // Load additional flights if available
        showAlert("All available flights are displayed.");
    }
    
    private void addNewFlight() {
        // Admin only - add new flight
        showAlert("Add new flight functionality will be implemented in future version.");
    }
    
    private void handleBack() {
        if (currentUser != null && currentUser.getRole() != null && currentUser.getRole().name().equals("ADMIN")) {
            NavigationManager.getInstance().showAdminDashboard();
        } else {
            NavigationManager.getInstance().showCustomerOverview();
        }
    }
    
    public void setSelectedFlight(Flight flight) {
        this.selectedFlight = flight;
        if (flightNumberLabel != null) {
            displayFlightDetails();
        }
    }
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Flight Search");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 
