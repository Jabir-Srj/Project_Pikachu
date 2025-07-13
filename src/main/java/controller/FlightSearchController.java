package controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
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
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.Flight;
import model.User;
import service.FlightService;
import util.NavigationManager;
import util.ServiceLocator;

/**
 * Controller for FlightInformation.fxml and FlightDetails.fxml
 * Handles flight searches and selection
 */
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
        // Load all flights
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
    
    /**
     * Handle sort or filter changes independently
     */
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
        
        // Clear stored search results
        currentSearchResults = null;
        
        // Show all flights
        List<Flight> allFlights = flightService.getAllFlights();
        displayFlights(allFlights);
        
        showAlert("Search has been reset. Showing all available flights.");
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
        
        Button viewDetailsBtn = new Button("View Details");
        viewDetailsBtn.getStyleClass().addAll("button-secondary");
        viewDetailsBtn.setOnAction(e -> viewFlightDetails(flight));
        
        Button selectFlightBtn = new Button("⚡ Select Flight");
        selectFlightBtn.getStyleClass().addAll("button-primary", "shadow-subtle");
        selectFlightBtn.setOnAction(e -> selectFlightForBooking(flight));
        
        card.getChildren().addAll(flightInfo, timeInfo, priceInfo, viewDetailsBtn, selectFlightBtn);
        return card;
    }
    
    private void viewFlightDetails(Flight flight) {
        selectedFlight = flight;
        NavigationManager.getInstance().showFlightDetails(flight);
    }
    
    private void selectFlightForBooking(Flight flight) {
        selectedFlight = flight;
        
        // Navigate to booking overview instead of payment confirmation
        NavigationManager.getInstance().showBookingOverview();
    }
    
    private void displayFlightDetails() {
        if (selectedFlight == null) return;
        
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
        // Admin only - open flight edit dialog
        showAlert("Flight editing functionality will be implemented in future version.");
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
} 