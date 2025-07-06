import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Booking;
import model.BookingStatus;
import model.Customer;
import model.Flight;
import model.FlightStatus;
import model.User;
import service.BookingService;
import service.FlightService;
import service.TicketService;
import service.UserService;
import util.DataManager;
import ai.AirlineAIService;

/**
 * Pikachu Airlines - Premium Customer Service Application
 * Modern design matching Figma specifications with professional airline aesthetics
 * Enhanced with proper menu bar and scaling
 */
public class AirlineApp extends Application {
    
    // Modern Color Palette - Pikachu Airlines Yellow Theme (Matching Figma Design)
    private static final String PRIMARY_YELLOW = "#F4D03F";    // Main yellow color from Figma
    private static final String SECONDARY_YELLOW = "#F5C842";  // Slightly darker yellow for accents
    private static final String LIGHT_YELLOW = "#FEF9E7";      // Light yellow backgrounds
    private static final String SUCCESS_GREEN = "#10B981";      // Success messages
    private static final String WARNING_ORANGE = "#F59E0B";     // Warnings/pending
    private static final String DANGER_RED = "#EF4444";        // Errors/cancellations
    private static final String PURPLE_ACCENT = "#8B5CF6";     // Premium features
    private static final String CREAM_50 = "#FFFACD";         // Light cream backgrounds
    private static final String CREAM_100 = "#FEF5E7";        // Card backgrounds
    private static final String GRAY_200 = "#E5E7EB";         // Borders
    private static final String GRAY_400 = "#9CA3AF";         // Subtle text
    private static final String GRAY_600 = "#4B5563";         // Secondary text
    private static final String GRAY_800 = "#1F2937";         // Primary text
    private static final String GRAY_900 = "#111827";         // Headers
    private static final String WHITE = "#FFFFFF";            // Pure white
    
    private Stage primaryStage;
    private UserService userService;
    private FlightService flightService;
    private BookingService bookingService;
    private TicketService ticketService;
    private DataManager dataManager;
    private User currentUser;
    private AirlineAIService aiService;
    
    // Screen management
    private Scene loginScene;
    private Scene registrationScene;
    private Scene customerDashboardScene;
    private Scene adminDashboardScene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        // Initialize services
        this.dataManager = new DataManager();
        this.userService = new UserService();
        this.flightService = new FlightService();
        this.bookingService = new BookingService();
        this.ticketService = new TicketService();
        
        // Load sample data
        dataManager.loadSampleData();
        
        // Initialize AI service
        this.aiService = AirlineAIService.getInstance();
        aiService.initialize().thenAccept(success -> {
            if (success) {
                System.out.println("AI Service initialized successfully!");
            } else {
                System.err.println("AI Service initialization failed!");
            }
        });
        
        // Set up the primary stage with proper scaling and menu bar
        primaryStage.setTitle("Pikachu Airlines - Premium Travel Experience");
        primaryStage.setResizable(true);
        
        // Windowed mode with reasonable dimensions
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(800);
        primaryStage.setMaximized(false);
        primaryStage.setResizable(true);
        
        // Set preferred windowed size
        primaryStage.setWidth(1400);
        primaryStage.setHeight(900);
        
        // Center the window on screen
        primaryStage.centerOnScreen();
        
        // Show login screen
        showLoginScreen();
    }
    
    /**
     * Create Application Menu Bar
     */
    private MenuBar createApplicationMenuBar() {
        MenuBar menuBar = new MenuBar();
        
        // Application Menu
        Menu appMenu = new Menu("Application");
        MenuItem aboutItem = new MenuItem("About Pikachu Airlines");
        aboutItem.setOnAction(e -> showAboutDialog());
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> primaryStage.close());
        appMenu.getItems().addAll(aboutItem, new SeparatorMenuItem(), exitItem);
        
        // View Menu
        Menu viewMenu = new Menu("View");
        MenuItem loginItem = new MenuItem("Login Screen");
        loginItem.setOnAction(e -> showLoginScreen());
        MenuItem registerItem = new MenuItem("Register");
        registerItem.setOnAction(e -> showRegistrationScreen());
        viewMenu.getItems().addAll(loginItem, registerItem);
        
        // Help Menu
        Menu helpMenu = new Menu("Help");
        MenuItem supportItem = new MenuItem("Customer Support");
        supportItem.setOnAction(e -> showSupportInfo());
        MenuItem faqItem = new MenuItem("FAQ");
        faqItem.setOnAction(e -> showFAQDialog());
        helpMenu.getItems().addAll(supportItem, faqItem);
        
        menuBar.getMenus().addAll(appMenu, viewMenu, helpMenu);
        
        // Style the menu bar
        menuBar.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-border-color: " + GRAY_200 + ";" +
            "-fx-border-width: 0 0 1 0;"
        );
        
        return menuBar;
    }
    
    /**
     * Show About Dialog
     */
    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Pikachu Airlines");
        alert.setHeaderText("Pikachu Airlines Management System");
        alert.setContentText("Version 1.0\n\nA comprehensive airline management system with modern UI design.\n\nFeatures:\n- Flight booking and management\n- Customer support ticketing\n- Admin dashboard\n- Real-time flight information");
        alert.showAndWait();
    }
    
    /**
     * Show Support Info
     */
    private void showSupportInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Customer Support");
        alert.setHeaderText("Need Help?");
        alert.setContentText("Contact our 24/7 customer support:\n\nPhone: +60 3-1234-5678\nEmail: support@pikachuairlines.com\nLive Chat: Available in the application");
        alert.showAndWait();
    }
    
    /**
     * Show FAQ Dialog
     */
    private void showFAQDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Frequently Asked Questions");
        alert.setHeaderText("Quick Help");
        alert.setContentText("Common Questions:\n\n1. How do I book a flight?\n   - Login and use the flight search feature\n\n2. How do I cancel my booking?\n   - Go to 'My Bookings' and select cancel\n\n3. How do I contact support?\n   - Use the support chat or ticket system\n\n4. Test Credentials:\n   - Admin: admin / 123456\n   - Customer: customer / 123456");
        alert.showAndWait();
    }
    
    /**
     * Modern Login Screen - Premium Airline Design (Enhanced with menu bar)
     */
    private void showLoginScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + LIGHT_YELLOW + ";");
        
        // Add Application Menu Bar
        MenuBar menuBar = createApplicationMenuBar();
        root.setTop(menuBar);
        
        // Main Content Container with improved scaling
        StackPane mainContainer = new StackPane();
        mainContainer.setPadding(new Insets(40, 50, 40, 50));
        
        // Responsive Login Card Container
        VBox loginCard = new VBox(30);
        loginCard.setAlignment(Pos.CENTER);
        loginCard.setPadding(new Insets(60, 80, 60, 80));
        loginCard.setMaxWidth(550);
        loginCard.setMinWidth(400);
        loginCard.prefWidthProperty().bind(mainContainer.widthProperty().multiply(0.4));
        loginCard.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-background-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 30, 0, 0, 10);" +
            "-fx-border-radius: 20;"
        );
        
        // Header Section
        VBox headerSection = new VBox(20);
        headerSection.setAlignment(Pos.CENTER);
        
        // Logo Section with better scaling
        HBox logoSection = new HBox(15);
        logoSection.setAlignment(Pos.CENTER);
        
        Label logoIcon = new Label("✈️");
        logoIcon.setStyle("-fx-font-size: 36px;");
        
        Label logoText = new Label("Pikachu Airlines");
        logoText.setStyle(
            "-fx-font-size: 32px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + PRIMARY_YELLOW + ";"
        );
        
        logoSection.getChildren().addAll(logoIcon, logoText);
        
        Label welcomeText = new Label("Welcome Back!");
        welcomeText.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-text-fill: " + GRAY_600 + ";"
        );
        
        headerSection.getChildren().addAll(logoSection, welcomeText);
        
        // Enhanced form section with better scaling
        VBox formSection = new VBox(25);
        formSection.setAlignment(Pos.CENTER);
        
        // Email Field with enhanced styling
        VBox emailContainer = new VBox(10);
        Label emailLabel = new Label("Email Address");
        emailLabel.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: 600;" +
            "-fx-text-fill: " + GRAY_800 + ";"
        );
        
        TextField emailField = createEnhancedTextField("Enter your email address");
        emailContainer.getChildren().addAll(emailLabel, emailField);
        
        // Password Field with enhanced styling
        VBox passwordContainer = new VBox(10);
        Label passwordLabel = new Label("Password");
        passwordLabel.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: 600;" +
            "-fx-text-fill: " + GRAY_800 + ";"
        );
        
        PasswordField passwordField = createEnhancedPasswordField("Enter your password");
        passwordContainer.getChildren().addAll(passwordLabel, passwordField);
        
        // Enhanced Sign In Button
        Button signInButton = createEnhancedPrimaryButton("Sign In", 450);
        signInButton.setOnAction(e -> handleLogin(emailField.getText(), passwordField.getText()));
        passwordField.setOnAction(e -> signInButton.fire());
        
        // Registration link section
        HBox signUpRow = new HBox(8);
        signUpRow.setAlignment(Pos.CENTER);
        
        Label noAccountText = new Label("Don't have an account?");
        noAccountText.setStyle(
            "-fx-font-size: 15px;" +
            "-fx-text-fill: " + GRAY_600 + ";"
        );
        
        Label signUpLink = new Label("Sign Up");
        signUpLink.setStyle(
            "-fx-font-size: 15px;" +
            "-fx-text-fill: " + PRIMARY_YELLOW + ";" +
            "-fx-cursor: hand;" +
            "-fx-font-weight: 600;" +
            "-fx-underline: true;"
        );
        signUpLink.setOnMouseClicked(e -> showRegistrationScreen());
        
        signUpRow.getChildren().addAll(noAccountText, signUpLink);
        
        formSection.getChildren().addAll(emailContainer, passwordContainer, signInButton, signUpRow);
        
        // Enhanced help section with default credentials
        VBox helpSection = new VBox(12);
        helpSection.setAlignment(Pos.CENTER);
        helpSection.setPadding(new Insets(25, 0, 0, 0));
        helpSection.setStyle(
            "-fx-border-color: " + GRAY_200 + ";" +
            "-fx-border-width: 1 0 0 0;" +
            "-fx-background-color: " + CREAM_50 + ";" +
            "-fx-background-radius: 0 0 20 20;"
        );
        
        Label helpTitle = new Label("Test Credentials");
        helpTitle.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + GRAY_800 + ";"
        );
        
        Label adminCreds = new Label("Admin: username = 'admin', password = '123456'");
        adminCreds.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: " + GRAY_600 + ";"
        );
        
        Label customerCreds = new Label("Customer: username = 'customer', password = '123456'");
        customerCreds.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: " + GRAY_600 + ";"
        );
        
        helpSection.getChildren().addAll(helpTitle, adminCreds, customerCreds);
        
        loginCard.getChildren().addAll(headerSection, formSection, helpSection);
        mainContainer.getChildren().add(loginCard);
        
        root.setCenter(mainContainer);
        
        // Windowed scene with appropriate scaling
        loginScene = new Scene(root, 1400, 900);
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }
    
    /**
     * Create Enhanced Text Field with Responsive Design
     */
    private TextField createEnhancedTextField(String promptText) {
        TextField field = new TextField();
        field.setPromptText(promptText);
        field.setMaxWidth(450);
        field.setMinWidth(300);
        field.setPrefHeight(55);
        field.setStyle(
            "-fx-font-size: 15px;" +
            "-fx-padding: 18 20;" +
            "-fx-background-color: " + PRIMARY_YELLOW + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-width: 2;" +
            "-fx-border-color: transparent;" +
            "-fx-border-radius: 10;" +
            "-fx-text-fill: " + GRAY_900 + ";" +
            "-fx-prompt-text-fill: " + GRAY_600 + ";"
        );
        
        // Enhanced focus effects
        field.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                field.setStyle(
                    "-fx-font-size: 15px;" +
                    "-fx-padding: 18 20;" +
                    "-fx-background-color: " + PRIMARY_YELLOW + ";" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-color: " + SECONDARY_YELLOW + ";" +
                    "-fx-border-radius: 10;" +
                    "-fx-text-fill: " + GRAY_900 + ";" +
                    "-fx-prompt-text-fill: " + GRAY_600 + ";"
                );
            } else {
                field.setStyle(
                    "-fx-font-size: 15px;" +
                    "-fx-padding: 18 20;" +
                    "-fx-background-color: " + PRIMARY_YELLOW + ";" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-color: transparent;" +
                    "-fx-border-radius: 10;" +
                    "-fx-text-fill: " + GRAY_900 + ";" +
                    "-fx-prompt-text-fill: " + GRAY_600 + ";"
                );
            }
        });
        
        return field;
    }
    
    /**
     * Create Enhanced Password Field
     */
    private PasswordField createEnhancedPasswordField(String promptText) {
        PasswordField field = new PasswordField();
        field.setPromptText(promptText);
        field.setPrefWidth(450);
        field.setPrefHeight(55);
        field.setStyle(
            "-fx-font-size: 15px;" +
            "-fx-padding: 18 20;" +
            "-fx-background-color: " + PRIMARY_YELLOW + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-width: 2;" +
            "-fx-border-color: transparent;" +
            "-fx-border-radius: 10;" +
            "-fx-text-fill: " + GRAY_900 + ";" +
            "-fx-prompt-text-fill: " + GRAY_600 + ";"
        );
        
        // Enhanced focus effects
        field.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                field.setStyle(
                    "-fx-font-size: 15px;" +
                    "-fx-padding: 18 20;" +
                    "-fx-background-color: " + PRIMARY_YELLOW + ";" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-color: " + SECONDARY_YELLOW + ";" +
                    "-fx-border-radius: 10;" +
                    "-fx-text-fill: " + GRAY_900 + ";" +
                    "-fx-prompt-text-fill: " + GRAY_600 + ";"
                );
            } else {
                field.setStyle(
                    "-fx-font-size: 15px;" +
                    "-fx-padding: 18 20;" +
                    "-fx-background-color: " + PRIMARY_YELLOW + ";" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-color: transparent;" +
                    "-fx-border-radius: 10;" +
                    "-fx-text-fill: " + GRAY_900 + ";" +
                    "-fx-prompt-text-fill: " + GRAY_600 + ";"
                );
            }
        });
        
        return field;
    }
    
    /**
     * Create Enhanced Primary Button
     */
    private Button createEnhancedPrimaryButton(String text, double width) {
        Button button = new Button(text);
        button.setPrefWidth(width);
        button.setPrefHeight(55);
        button.setStyle(
            "-fx-background-color: " + SECONDARY_YELLOW + ";" +
            "-fx-text-fill: " + GRAY_900 + ";" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-border-width: 0;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
        );
        
        // Enhanced hover effects
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: " + PRIMARY_YELLOW + ";" +
            "-fx-text-fill: " + GRAY_900 + ";" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-border-width: 0;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);"
        ));
        
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: " + SECONDARY_YELLOW + ";" +
            "-fx-text-fill: " + GRAY_900 + ";" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-border-width: 0;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
        ));
        
        return button;
    }
    
    /**
     * Enhanced Registration Screen - Better scaling and menu bar
     */
    private void showRegistrationScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + LIGHT_YELLOW + ";");
        
        // Add Application Menu Bar
        MenuBar menuBar = createApplicationMenuBar();
        root.setTop(menuBar);
        
        // Main Container with improved scaling
        StackPane mainContainer = new StackPane();
        mainContainer.setPadding(new Insets(30, 50, 30, 50));
        
        // Responsive Registration Card
        VBox registrationCard = new VBox(25);
        registrationCard.setAlignment(Pos.CENTER);
        registrationCard.setPadding(new Insets(50, 60, 50, 60));
        registrationCard.setMaxWidth(750);
        registrationCard.setMinWidth(500);
        registrationCard.prefWidthProperty().bind(mainContainer.widthProperty().multiply(0.6));
        registrationCard.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-background-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 30, 0, 0, 10);"
        );
        
        // Header with better styling
        VBox headerSection = new VBox(15);
        headerSection.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("Create Account");
        titleLabel.setStyle(
            "-fx-font-size: 32px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + GRAY_900 + ";"
        );
        
        Label subtitleLabel = new Label("Join Pikachu Airlines today");
        subtitleLabel.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-text-fill: " + GRAY_600 + ";"
        );
        
        headerSection.getChildren().addAll(titleLabel, subtitleLabel);
        
        // Enhanced Registration Form
        VBox formSection = new VBox(20);
        formSection.setAlignment(Pos.CENTER);
        
        // Name fields with better layout
        HBox nameRow = new HBox(15);
        nameRow.setAlignment(Pos.CENTER);
        TextField firstNameField = createEnhancedTextField("First Name");
        firstNameField.setPrefWidth(300);
        TextField lastNameField = createEnhancedTextField("Last Name");
        lastNameField.setPrefWidth(300);
        nameRow.getChildren().addAll(firstNameField, lastNameField);
        
        // Enhanced nationality dropdown
        ComboBox<String> nationalityCombo = createEnhancedComboBox("Nationality");
        nationalityCombo.getItems().addAll("Malaysian", "Singaporean", "Indonesian", "Thai", "Other");
        
        // DOB and Gender row with better styling
        HBox dobGenderRow = new HBox(15);
        dobGenderRow.setAlignment(Pos.CENTER);
        DatePicker dobPicker = createEnhancedDatePicker("Date of Birth");
        dobPicker.setPrefWidth(300);
        ComboBox<String> genderCombo = createEnhancedComboBox("Gender");
        genderCombo.setPrefWidth(300);
        genderCombo.getItems().addAll("Male", "Female", "Other");
        dobGenderRow.getChildren().addAll(dobPicker, genderCombo);
        
        // Contact fields with enhanced styling
        TextField emailField = createEnhancedTextField("Email Address");
        TextField phoneField = createEnhancedTextField("Phone Number");
        
        // Password fields with enhanced styling
        PasswordField passwordField = createEnhancedPasswordField("Password");
        PasswordField confirmPasswordField = createEnhancedPasswordField("Confirm Password");
        
        // Action buttons row (Back + Sign Up)
        HBox buttonRow = new HBox(20);
        buttonRow.setAlignment(Pos.CENTER);
        
        // Back to Login button
        Button backButton = createSecondaryButton("Back to Login", 250);
        backButton.setOnAction(e -> showLoginScreen());
        
        // Sign Up Button with validation
        Button signUpButton = createEnhancedPrimaryButton("Create Account", 250);
        signUpButton.setOnAction(e -> {
            // Comprehensive validation
            if (firstNameField.getText().trim().isEmpty() || 
                lastNameField.getText().trim().isEmpty() || 
                emailField.getText().trim().isEmpty() || 
                phoneField.getText().trim().isEmpty() ||
                passwordField.getText().trim().isEmpty() ||
                confirmPasswordField.getText().trim().isEmpty() ||
                nationalityCombo.getValue() == null ||
                dobPicker.getValue() == null ||
                genderCombo.getValue() == null) {
                
                showModernAlert("Error", "Please fill in all fields before creating an account.", "error");
                return;
            }
            
            if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                showModernAlert("Error", "Passwords do not match. Please try again.", "error");
                return;
            }
            
            if (passwordField.getText().length() < 6) {
                showModernAlert("Error", "Password must be at least 6 characters long.", "error");
                return;
            }
            
            if (!emailField.getText().contains("@") || !emailField.getText().contains(".")) {
                showModernAlert("Error", "Please enter a valid email address.", "error");
                return;
            }
            
            // Create new customer with all fields
            Customer newCustomer = new Customer();
            newCustomer.setFirstName(firstNameField.getText().trim());
            newCustomer.setLastName(lastNameField.getText().trim());
            newCustomer.setEmail(emailField.getText().trim());
            newCustomer.setPhoneNumber(phoneField.getText().trim());
            newCustomer.setUsername(emailField.getText().trim()); // Use email as username
            newCustomer.setPassword(passwordField.getText());
            
            System.out.println("Attempting to register user: " + newCustomer.getUsername());
            
            if (userService.registerUser(newCustomer)) {
                System.out.println("Registration successful for: " + newCustomer.getUsername());
                showModernAlert("Success", 
                    "Account created successfully!\n\n" +
                    "Username: " + newCustomer.getEmail() + "\n" +
                    "Password: " + passwordField.getText() + "\n\n" +
                    "You can now log in with these credentials.", "success");
                showLoginScreen();
            } else {
                System.out.println("Registration failed for: " + newCustomer.getUsername());
                showModernAlert("Error", "Registration failed. Email may already be in use.", "error");
            }
        });
        
        buttonRow.getChildren().addAll(backButton, signUpButton);
        
        formSection.getChildren().addAll(nameRow, nationalityCombo, dobGenderRow, emailField, phoneField, passwordField, confirmPasswordField, buttonRow);
        
        registrationCard.getChildren().addAll(headerSection, formSection);
        mainContainer.getChildren().add(registrationCard);
        
        root.setCenter(mainContainer);
        
        // Windowed scene with appropriate scaling
        registrationScene = new Scene(root, 1400, 900);
        primaryStage.setScene(registrationScene);
    }
    
    /**
     * Create Enhanced ComboBox
     */
    private ComboBox<String> createEnhancedComboBox(String promptText) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPromptText(promptText);
        comboBox.setPrefWidth(450);
        comboBox.setPrefHeight(55);
        comboBox.setStyle(
            "-fx-background-color: " + PRIMARY_YELLOW + ";" +
            "-fx-font-size: 15px;" +
            "-fx-padding: 18 20;" +
            "-fx-text-fill: " + GRAY_900 + ";" +
            "-fx-prompt-text-fill: " + GRAY_600 + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-width: 2;" +
            "-fx-border-color: transparent;" +
            "-fx-border-radius: 10;"
        );
        return comboBox;
    }
    
    /**
     * Create Enhanced DatePicker
     */
    private DatePicker createEnhancedDatePicker(String promptText) {
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText(promptText);
        datePicker.setPrefHeight(55);
        datePicker.setStyle(
            "-fx-background-color: " + PRIMARY_YELLOW + ";" +
            "-fx-font-size: 15px;" +
            "-fx-padding: 18 20;" +
            "-fx-text-fill: " + GRAY_900 + ";" +
            "-fx-prompt-text-fill: " + GRAY_600 + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-width: 2;" +
            "-fx-border-color: transparent;" +
            "-fx-border-radius: 10;"
        );
        return datePicker;
    }
    
    /**
     * Create Yellow-themed Text Field (matching Figma design)
     */
    private TextField createYellowTextField(String promptText) {
        return createYellowTextField(promptText, 400);
    }
    
    private TextField createYellowTextField(String promptText, double width) {
        TextField field = new TextField();
        field.setPromptText(promptText);
        field.setPrefWidth(width);
        field.setPrefHeight(50);
        field.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-padding: 15 18;" +
            "-fx-background-color: " + PRIMARY_YELLOW + ";" +
            "-fx-background-radius: 8;" +
            "-fx-border-width: 0;" +
            "-fx-text-fill: " + GRAY_900 + ";" +
            "-fx-prompt-text-fill: " + GRAY_600 + ";"
        );
        
        return field;
    }
    
    /**
     * Create Yellow-themed Password Field (matching Figma design)
     */
    private PasswordField createYellowPasswordField(String promptText) {
        return createYellowPasswordField(promptText, 400);
    }
    
    private PasswordField createYellowPasswordField(String promptText, double width) {
        PasswordField field = new PasswordField();
        field.setPromptText(promptText);
        field.setPrefWidth(width);
        field.setPrefHeight(50);
        field.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-padding: 15 18;" +
            "-fx-background-color: " + PRIMARY_YELLOW + ";" +
            "-fx-background-radius: 8;" +
            "-fx-border-width: 0;" +
            "-fx-text-fill: " + GRAY_900 + ";" +
            "-fx-prompt-text-fill: " + GRAY_600 + ";"
        );
        
        return field;
    }
    
    /**
     * Create Yellow Primary Button (matching Figma design)
     */
    private Button createYellowPrimaryButton(String text, double width) {
        Button button = new Button(text);
        button.setPrefWidth(width);
        button.setPrefHeight(50);
        button.setStyle(
            "-fx-background-color: " + SECONDARY_YELLOW + ";" +
            "-fx-text-fill: " + GRAY_900 + ";" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-border-width: 0;"
        );
        
        // Hover effects
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: " + PRIMARY_YELLOW + ";" +
            "-fx-text-fill: " + GRAY_900 + ";" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-border-width: 0;"
        ));
        
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: " + SECONDARY_YELLOW + ";" +
            "-fx-text-fill: " + GRAY_900 + ";" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-border-width: 0;"
        ));
        
        return button;
    }
    
    /**
     * Create Top Navigation Bar
     */
    private HBox createTopNavigationBar() {
        HBox topNav = new HBox();
        topNav.setPadding(new Insets(20, 30, 20, 30));
        topNav.setAlignment(Pos.CENTER_LEFT);
        topNav.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95); -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        
        // Logo Section
        HBox logoSection = new HBox(12);
        logoSection.setAlignment(Pos.CENTER_LEFT);
        
        Label logoIcon = new Label("✈️");
        logoIcon.setStyle("-fx-font-size: 24px;");
        
        Label logoText = new Label("Pikachu Airlines");
        logoText.setStyle(
            "-fx-font-size: 22px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + PRIMARY_YELLOW + ";"
        );
        
        logoSection.getChildren().addAll(logoIcon, logoText);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Navigation Links
        HBox navLinks = new HBox(30);
        navLinks.setAlignment(Pos.CENTER_RIGHT);
        
        Label homeLink = createNavLink("Home");
        Label aboutLink = createNavLink("About");
        Label servicesLink = createNavLink("Services");
        Label contactLink = createNavLink("Contact");
        
        navLinks.getChildren().addAll(homeLink, aboutLink, servicesLink, contactLink);
        
        topNav.getChildren().addAll(logoSection, spacer, navLinks);
        return topNav;
    }
    
    private Label createNavLink(String text) {
        Label link = new Label(text);
        link.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: " + GRAY_600 + ";" +
            "-fx-cursor: hand;" +
            "-fx-font-weight: 500;"
        );
        
        link.setOnMouseEntered(e -> link.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: " + PRIMARY_YELLOW + ";" +
            "-fx-cursor: hand;" +
            "-fx-font-weight: 500;"
        ));
        
        link.setOnMouseExited(e -> link.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: " + GRAY_600 + ";" +
            "-fx-cursor: hand;" +
            "-fx-font-weight: 500;"
        ));
        
        return link;
    }
    
    /**
     * Create Modern Text Field
     */
    private TextField createModernTextField(String promptText) {
        return createModernTextField(promptText, 400);
    }
    
    private TextField createModernTextField(String promptText, double width) {
        TextField field = new TextField();
        field.setPromptText(promptText);
        field.setPrefWidth(width);
        field.setPrefHeight(50);
        field.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-padding: 15 18;" +
            "-fx-background-color: " + WHITE + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + GRAY_200 + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 12;" +
            "-fx-text-fill: " + GRAY_800 + ";"
        );
        
        // Focus effects
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle(
                    "-fx-font-size: 14px;" +
                    "-fx-padding: 15 18;" +
                    "-fx-background-color: " + WHITE + ";" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-color: " + SECONDARY_YELLOW + ";" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 12;" +
                    "-fx-text-fill: " + GRAY_800 + ";" +
                    "-fx-effect: dropshadow(" + SECONDARY_YELLOW + "44, 8, 0, 0, 0);"
                );
            } else {
                field.setStyle(
                    "-fx-font-size: 14px;" +
                    "-fx-padding: 15 18;" +
                    "-fx-background-color: " + WHITE + ";" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-color: " + GRAY_200 + ";" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 12;" +
                    "-fx-text-fill: " + GRAY_800 + ";"
                );
            }
        });
        
        return field;
    }
    
    /**
     * Create Modern Password Field
     */
    private PasswordField createModernPasswordField(String promptText) {
        return createModernPasswordField(promptText, 400);
    }
    
    private PasswordField createModernPasswordField(String promptText, double width) {
        PasswordField field = new PasswordField();
        field.setPromptText(promptText);
        field.setPrefWidth(width);
        field.setPrefHeight(50);
        field.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-padding: 15 18;" +
            "-fx-background-color: " + WHITE + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + GRAY_200 + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 12;" +
            "-fx-text-fill: " + GRAY_800 + ";"
        );
        
        // Focus effects with yellow theme
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle(
                    "-fx-font-size: 14px;" +
                    "-fx-padding: 15 18;" +
                    "-fx-background-color: " + WHITE + ";" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-color: " + SECONDARY_YELLOW + ";" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 12;" +
                    "-fx-text-fill: " + GRAY_800 + ";" +
                    "-fx-effect: dropshadow(gaussian, " + SECONDARY_YELLOW + "44, 8, 0, 0, 0);"
                );
            } else {
                field.setStyle(
                    "-fx-font-size: 14px;" +
                    "-fx-padding: 15 18;" +
                    "-fx-background-color: " + WHITE + ";" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-color: " + GRAY_200 + ";" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 12;" +
                    "-fx-text-fill: " + GRAY_800 + ";"
                );
            }
        });
        
        return field;
    }
    
    /**
     * Create Primary Button (Updated for yellow theme)
     */
    private Button createPrimaryButton(String text, double width) {
        Button button = new Button(text);
        button.setPrefWidth(width);
        button.setPrefHeight(50);
        button.setStyle(
            "-fx-background-color: " + PRIMARY_YELLOW + ";" +
            "-fx-text-fill: " + GRAY_900 + ";" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 12;" +
            "-fx-cursor: hand;" +
            "-fx-border-width: 0;" +
            "-fx-effect: dropshadow(gaussian, rgba(244,208,63,0.3), 15, 0, 0, 5);"
        );
        
        // Hover effects
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: " + SECONDARY_YELLOW + ";" +
            "-fx-text-fill: " + GRAY_900 + ";" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 12;" +
            "-fx-cursor: hand;" +
            "-fx-border-width: 0;" +
            "-fx-effect: dropshadow(gaussian, rgba(245,200,66,0.4), 20, 0, 0, 8);" +
            "-fx-scale-y: 1.02;"
        ));
        
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: " + PRIMARY_YELLOW + ";" +
            "-fx-text-fill: " + GRAY_900 + ";" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 12;" +
            "-fx-cursor: hand;" +
            "-fx-border-width: 0;" +
            "-fx-effect: dropshadow(gaussian, rgba(244,208,63,0.3), 15, 0, 0, 5);"
        ));
        
        return button;
    }
    
    /**
     * Create Secondary Button (Updated for yellow theme)
     */
    private Button createSecondaryButton(String text, double width) {
        Button button = new Button(text);
        button.setPrefWidth(width);
        button.setPrefHeight(50);
        button.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-text-fill: " + GRAY_600 + ";" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: 600;" +
            "-fx-background-radius: 12;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: " + GRAY_200 + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 12;"
        );
        
        // Hover effects
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: " + CREAM_50 + ";" +
            "-fx-text-fill: " + GRAY_800 + ";" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: 600;" +
            "-fx-background-radius: 12;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: " + GRAY_400 + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 12;"
        ));
        
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-text-fill: " + GRAY_600 + ";" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: 600;" +
            "-fx-background-radius: 12;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: " + GRAY_200 + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 12;"
        ));
        
        return button;
    }
    
    /**
     * Create Modern Alert Dialog
     */
    private void showModernAlert(String title, String message, String type) {
        Alert alert;
        switch (type.toLowerCase()) {
            case "error":
                alert = new Alert(Alert.AlertType.ERROR);
                break;
            case "success":
                alert = new Alert(Alert.AlertType.INFORMATION);
                break;
            case "warning":
                alert = new Alert(Alert.AlertType.WARNING);
                break;
            default:
                alert = new Alert(Alert.AlertType.INFORMATION);
        }
        
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Style the alert to match our design
        alert.getDialogPane().setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-font-family: 'Segoe UI';" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;"
        );
        
        alert.showAndWait();
    }
    
    /**
     * Handle user login with enhanced feedback
     */
    private void handleLogin(String username, String password) {
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            showModernAlert("Error", "Please enter both username and password.", "error");
            return;
        }
        
        System.out.println("Login attempt - Username: " + username);
        
        User user = userService.authenticate(username, password);
        if (user != null) {
            currentUser = user;
            System.out.println("Login successful for user: " + user.getUsername() + " (" + user.getRole() + ")");
            showModernAlert("Success", "Welcome back, " + user.getFirstName() + "!", "success");
            showDashboard(user);
        } else {
            System.out.println("Login failed for username: " + username);
            showModernAlert("Login Failed", 
                "Invalid username or password.\n\n" +
                "Tip: Try the default credentials shown below the login form,\n" +
                "or create a new account and log in with those credentials.", "error");
        }
    }
    
    /**
     * Handle user registration
     */
    private void handleRegistration(String firstName, String lastName, String email, String phone,
                                   String username, String password, String confirmPassword, boolean termsAccepted) {
        // Validation
        if (firstName.trim().isEmpty() || lastName.trim().isEmpty() || email.trim().isEmpty() ||
            phone.trim().isEmpty() || username.trim().isEmpty() || password.trim().isEmpty()) {
            showModernAlert("Error", "Please fill in all fields.", "error");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showModernAlert("Error", "Passwords do not match.", "error");
            return;
        }
        
        if (!termsAccepted) {
            showModernAlert("Error", "Please accept the Terms and Conditions.", "error");
            return;
        }
        
        // Create new customer
        Customer newCustomer = new Customer();
        newCustomer.setFirstName(firstName);
        newCustomer.setLastName(lastName);
        newCustomer.setEmail(email);
        newCustomer.setPhoneNumber(phone);
        newCustomer.setUsername(username);
        newCustomer.setPassword(password);
        
        if (userService.registerUser(newCustomer)) {
            showModernAlert("Success", "Account created successfully! You can now log in.", "success");
            showLoginScreen();
        } else {
            showModernAlert("Error", "Registration failed. Username or email may already exist.", "error");
        }
    }
    
    /**
     * Show appropriate dashboard based on user role
     */
    private void showDashboard(User user) {
        switch (user.getRole()) {
            case CUSTOMER:
                showCustomerDashboard();
                break;
            case ADMIN:
                showAdminDashboard();
                break;
            case AIRLINE_MANAGEMENT:
                showAirlineManagementDashboard();
                break;
            default:
                showModernAlert("Error", "Unknown user role: " + user.getRole(), "error");
        }
    }
    
    /**
     * Enhanced Customer Dashboard - Premium Design with Menu Bar
     */
    private void showCustomerDashboard() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + CREAM_50 + ";");
        
        // Enhanced top section with menu bar and header
        VBox topSection = new VBox();
        
        // Customer-specific Menu Bar
        MenuBar menuBar = createCustomerMenuBar();
        topSection.getChildren().add(menuBar);
        
        // Enhanced Header
        HBox header = createDashboardHeader("Customer Portal", currentUser.getFirstName() + " " + currentUser.getLastName());
        topSection.getChildren().add(header);
        
        root.setTop(topSection);
        
        // Responsive Navigation sidebar
        VBox sidebar = createModernCustomerSidebar(root);
        sidebar.prefWidthProperty().bind(root.widthProperty().multiply(0.2));
        sidebar.setMinWidth(250);
        sidebar.setMaxWidth(350);
        root.setLeft(sidebar);
        
        // Responsive Main content area
        StackPane contentArea = new StackPane();
        contentArea.setPadding(new Insets(20));
        contentArea.setStyle("-fx-background-color: " + CREAM_50 + ";");
        HBox.setHgrow(contentArea, Priority.ALWAYS);
        
        // Default content - Modern Flight Search with ScrollPane
        VBox defaultContent = createModernFlightSearchContent();
        ScrollPane scrollPane = new ScrollPane(defaultContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: " + CREAM_50 + "; -fx-background: " + CREAM_50 + ";");
        contentArea.getChildren().add(scrollPane);
        
        root.setCenter(contentArea);
        
        // Windowed scene with appropriate scaling
        customerDashboardScene = new Scene(root, 1400, 900);
        primaryStage.setScene(customerDashboardScene);
        primaryStage.setTitle("Pikachu Airlines - Customer Portal");
    }
    
    /**
     * Create Customer-specific Menu Bar
     */
    private MenuBar createCustomerMenuBar() {
        MenuBar menuBar = new MenuBar();
        
        // Account Menu
        Menu accountMenu = new Menu("Account");
        MenuItem profileItem = new MenuItem("My Profile");
        profileItem.setOnAction(e -> showModernAlert("Profile", "Profile management feature coming soon!", "info"));
        MenuItem logoutItem = new MenuItem("Logout");
        logoutItem.setOnAction(e -> {
            currentUser = null;
            showLoginScreen();
        });
        accountMenu.getItems().addAll(profileItem, new SeparatorMenuItem(), logoutItem);
        
        // Bookings Menu
        Menu bookingsMenu = new Menu("Bookings");
        MenuItem myBookingsItem = new MenuItem("My Bookings");
        myBookingsItem.setOnAction(e -> switchContent((BorderPane) customerDashboardScene.getRoot(), createBookingOverviewContent()));
        MenuItem newBookingItem = new MenuItem("Search Flights");
        newBookingItem.setOnAction(e -> switchContent((BorderPane) customerDashboardScene.getRoot(), createModernFlightSearchContent()));
        bookingsMenu.getItems().addAll(myBookingsItem, newBookingItem);
        
        // Support Menu
        Menu supportMenu = new Menu("Support");
        MenuItem chatItem = new MenuItem("Live Chat");
        chatItem.setOnAction(e -> switchContent((BorderPane) customerDashboardScene.getRoot(), createSupportChatContent()));
        MenuItem ticketItem = new MenuItem("Submit Ticket");
        ticketItem.setOnAction(e -> showTicketSubmissionScreen());
        MenuItem faqItem = new MenuItem("FAQ");
        faqItem.setOnAction(e -> showFAQDialog());
        supportMenu.getItems().addAll(chatItem, ticketItem, faqItem);
        
        menuBar.getMenus().addAll(accountMenu, bookingsMenu, supportMenu);
        
        // Style the menu bar
        menuBar.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-border-color: " + GRAY_200 + ";" +
            "-fx-border-width: 0 0 1 0;"
        );
        
        return menuBar;
    }

    /**
     * Create Modern Dashboard Header
     */
    private HBox createDashboardHeader(String title, String username) {
        HBox header = new HBox();
        header.setPadding(new Insets(20, 40, 20, 40));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 2);" +
            "-fx-border-color: " + GRAY_200 + ";" +
            "-fx-border-width: 0 0 1 0;"
        );
        
        // Logo and Title Section
        HBox logoTitleSection = new HBox(20);
        logoTitleSection.setAlignment(Pos.CENTER_LEFT);
        
        Label logoIcon = new Label("✈️");
        logoIcon.setStyle("-fx-font-size: 28px;");
        
        VBox titleSection = new VBox(2);
        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + GRAY_900 + ";"
        );
        
        Label subtitleLabel = new Label("Pikachu Airlines Premium Experience");
        subtitleLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: " + GRAY_600 + ";"
        );
        
        titleSection.getChildren().addAll(titleLabel, subtitleLabel);
        logoTitleSection.getChildren().addAll(logoIcon, titleSection);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // User Section
        HBox userSection = new HBox(20);
        userSection.setAlignment(Pos.CENTER_RIGHT);
        
        VBox userInfo = new VBox(2);
        userInfo.setAlignment(Pos.CENTER_RIGHT);
        
        Label welcomeLabel = new Label("Welcome back,");
        welcomeLabel.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: " + GRAY_600 + ";"
        );
        
        Label userLabel = new Label(username);
        userLabel.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: 600;" +
            "-fx-text-fill: " + GRAY_800 + ";"
        );
        
        userInfo.getChildren().addAll(welcomeLabel, userLabel);
        
        // User Avatar
        Label avatar = new Label("👤");
        avatar.setStyle(
            "-fx-font-size: 24px;" +
            "-fx-background-color: " + LIGHT_YELLOW + ";" +
            "-fx-background-radius: 50;" +
            "-fx-padding: 8;"
        );
        
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle(
            "-fx-background-color: " + DANGER_RED + ";" +
            "-fx-text-fill: " + WHITE + ";" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 8 16;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-border-width: 0;"
        );
        logoutButton.setOnAction(e -> showLoginScreen());
        
        // Add hover effect
        logoutButton.setOnMouseEntered(e -> logoutButton.setStyle(
            "-fx-background-color: #DC2626;" +
            "-fx-text-fill: " + WHITE + ";" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 8 16;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-border-width: 0;"
        ));
        
        logoutButton.setOnMouseExited(e -> logoutButton.setStyle(
            "-fx-background-color: " + DANGER_RED + ";" +
            "-fx-text-fill: " + WHITE + ";" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 8 16;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-border-width: 0;"
        ));
        
        userSection.getChildren().addAll(userInfo, avatar, logoutButton);
        header.getChildren().addAll(logoTitleSection, spacer, userSection);
        return header;
    }

    /**
     * Create Modern Customer Sidebar
     */
    private VBox createModernCustomerSidebar(BorderPane parentRoot) {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(30, 20, 30, 20));
        sidebar.setMinWidth(250);
        sidebar.setMaxWidth(350);
        sidebar.setStyle("-fx-background-color: " + WHITE + "; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 10, 0, 2, 0); -fx-border-color: " + GRAY_200 + "; -fx-border-width: 0 1 0 0;");

        // Profile section
        VBox profileSection = new VBox(10);
        profileSection.setPadding(new Insets(0, 0, 30, 0));
        profileSection.setAlignment(Pos.CENTER);
        profileSection.setStyle("-fx-border-color: " + GRAY_200 + "; -fx-border-width: 0 0 1 0;");

        Label profilePic = new Label("👤");
        profilePic.setStyle("-fx-font-size: 48px; -fx-background-color: " + LIGHT_YELLOW + "; -fx-background-radius: 50; -fx-padding: 12;");
        profilePic.setAlignment(Pos.CENTER);

        Label userName = new Label(currentUser.getFirstName() + " " + currentUser.getLastName());
        userName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + GRAY_900 + ";");

        Label userEmail = new Label(currentUser.getEmail());
        userEmail.setStyle("-fx-font-size: 12px; -fx-text-fill: " + GRAY_600 + ";");

        profileSection.getChildren().addAll(profilePic, userName, userEmail);

        // Navigation menu
        VBox menuSection = new VBox(12);
        menuSection.setPadding(new Insets(30, 0, 0, 0));

        Label menuTitle = new Label("Menu");
        menuTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + GRAY_400 + "; -fx-padding: 0 0 10 0;");

        // Menu buttons
        Button flightSearchBtn = createSidebarButton("✈️", "Flight Search", SECONDARY_YELLOW);
        Button bookingsBtn = createSidebarButton("📋", "My Bookings", SUCCESS_GREEN);
        Button flightInfoBtn = createSidebarButton("ℹ️", "Flight Information", PRIMARY_YELLOW);
        Button supportBtn = createSidebarButton("💬", "Support & Chat", PURPLE_ACCENT);
        Button ticketsBtn = createSidebarButton("🎫", "My Tickets", WARNING_ORANGE);
        Button profileBtn = createSidebarButton("⚙️", "Profile Settings", LIGHT_YELLOW);

        // Event handlers for navigation
        flightSearchBtn.setOnAction(e -> switchContent(parentRoot, createModernFlightSearchContent()));
        bookingsBtn.setOnAction(e -> switchContent(parentRoot, createBookingOverviewContent()));
        flightInfoBtn.setOnAction(e -> switchContent(parentRoot, createFlightInformationContent()));
        supportBtn.setOnAction(e -> switchContent(parentRoot, createSupportChatContent()));
        ticketsBtn.setOnAction(e -> switchContent(parentRoot, createTicketOverviewContent()));
        profileBtn.setOnAction(e -> switchContent(parentRoot, createCustomerDetailsContent()));

        menuSection.getChildren().addAll(menuTitle, flightSearchBtn, bookingsBtn, flightInfoBtn, 
                                        supportBtn, ticketsBtn, profileBtn);

        sidebar.getChildren().addAll(profileSection, menuSection);
        return sidebar;
    }

    /**
     * Modern Flight Search Content - Card-based, premium design
     */
    private VBox createModernFlightSearchContent() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: " + CREAM_50 + ";");
        VBox.setVgrow(content, Priority.ALWAYS);

        // Title section
        VBox titleSection = new VBox(8);
        Label title = new Label("Search Flights");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_YELLOW + ";");

        Label subtitle = new Label("Find and book your perfect flight");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: " + GRAY_600 + ";");

        titleSection.getChildren().addAll(title, subtitle);

        // Search form card
        VBox searchForm = new VBox(25);
        searchForm.setPadding(new Insets(35, 40, 35, 40));
        searchForm.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 18; -fx-effect: dropshadow(gaussian, rgba(59,130,246,0.08), 18, 0, 0, 6);");

        // First row - Trip type and passengers
        HBox firstRow = new HBox(24);

        VBox tripTypeBox = new VBox(5);
        Label tripTypeLabel = new Label("Trip Type");
        tripTypeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + GRAY_800 + ";");

        ComboBox<String> tripTypeCombo = new ComboBox<>();
        tripTypeCombo.getItems().addAll("Round Trip", "One Way", "Multi-city");
        tripTypeCombo.setValue("Round Trip");
        tripTypeCombo.setPrefWidth(150);
        tripTypeCombo.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: " + GRAY_200 + ";");

        tripTypeBox.getChildren().addAll(tripTypeLabel, tripTypeCombo);

        VBox passengersBox = new VBox(5);
        Label passengersLabel = new Label("Passengers");
        passengersLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + GRAY_800 + ";");

        ComboBox<String> passengersCombo = new ComboBox<>();
        passengersCombo.getItems().addAll("1 Adult", "2 Adults", "3 Adults", "4 Adults", "5+ Adults");
        passengersCombo.setValue("1 Adult");
        passengersCombo.setPrefWidth(150);
        passengersCombo.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: " + GRAY_200 + ";");

        passengersBox.getChildren().addAll(passengersLabel, passengersCombo);

        VBox classBox = new VBox(5);
        Label classLabel = new Label("Class");
        classLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + GRAY_800 + ";");

        ComboBox<String> classCombo = new ComboBox<>();
        classCombo.getItems().addAll("Economy", "Premium Economy", "Business", "First Class");
        classCombo.setValue("Economy");
        classCombo.setPrefWidth(150);
        classCombo.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: " + GRAY_200 + ";");

        classBox.getChildren().addAll(classLabel, classCombo);

        firstRow.getChildren().addAll(tripTypeBox, passengersBox, classBox);

        // Second row - Origin and destination
        HBox secondRow = new HBox(30);
        secondRow.setAlignment(Pos.CENTER);

        VBox fromBox = new VBox(8);
        Label fromLabel = new Label("FROM");
        fromLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + GRAY_400 + ";");

        ComboBox<String> fromCombo = new ComboBox<>();
        fromCombo.getItems().addAll(
            "Tokyo (NRT) - Japan", "Seoul (ICN) - South Korea", "Singapore (SIN) - Singapore",
            "Kuala Lumpur (KUL) - Malaysia", "Bangkok (BKK) - Thailand", "Manila (MNL) - Philippines", 
            "Hong Kong (HKG) - Hong Kong", "Taipei (TPE) - Taiwan", "Jakarta (CGK) - Indonesia",
            "Ho Chi Minh City (SGN) - Vietnam", "Beijing (PEK) - China", "Shanghai (PVG) - China",
            "Mumbai (BOM) - India", "Delhi (DEL) - India", "Osaka (KIX) - Japan", "Busan (PUS) - South Korea",
            "Colombo (CMB) - Sri Lanka", "Chennai (MAA) - India", "Dhaka (DAC) - Bangladesh",
            "Kathmandu (KTM) - Nepal", "Yangon (RGN) - Myanmar", "Hanoi (HAN) - Vietnam",
            "Phnom Penh (PNH) - Cambodia", "Vientiane (VTE) - Laos", "Macau (MFM) - Macau",
            "Guangzhou (CAN) - China"
        );
        fromCombo.setValue("Tokyo (NRT) - Japan");
        fromCombo.setPrefWidth(260);
        fromCombo.setStyle("-fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 12; -fx-border-color: " + GRAY_200 + "; -fx-border-width: 1; -fx-border-radius: 12; -fx-background-color: " + CREAM_50 + ";");

        fromCombo.setButtonCell(new javafx.scene.control.ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText("🛫 " + item);
                    setStyle("-fx-font-size: 14px; -fx-text-fill: " + GRAY_900 + "; -fx-font-weight: 500;");
                }
            }
        });

        fromBox.getChildren().addAll(fromLabel, fromCombo);

        VBox toBox = new VBox(8);
        Label toLabel = new Label("TO");
        toLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + GRAY_400 + ";");

        ComboBox<String> toCombo = new ComboBox<>();
        toCombo.getItems().addAll(
            "Tokyo (NRT) - Japan", "Seoul (ICN) - South Korea", "Singapore (SIN) - Singapore",
            "Kuala Lumpur (KUL) - Malaysia", "Bangkok (BKK) - Thailand", "Manila (MNL) - Philippines", 
            "Hong Kong (HKG) - Hong Kong", "Taipei (TPE) - Taiwan", "Jakarta (CGK) - Indonesia",
            "Ho Chi Minh City (SGN) - Vietnam", "Beijing (PEK) - China", "Shanghai (PVG) - China",
            "Mumbai (BOM) - India", "Delhi (DEL) - India", "Osaka (KIX) - Japan", "Busan (PUS) - South Korea",
            "Colombo (CMB) - Sri Lanka", "Chennai (MAA) - India", "Dhaka (DAC) - Bangladesh",
            "Kathmandu (KTM) - Nepal", "Yangon (RGN) - Myanmar", "Hanoi (HAN) - Vietnam",
            "Phnom Penh (PNH) - Cambodia", "Vientiane (VTE) - Laos", "Macau (MFM) - Macau",
            "Guangzhou (CAN) - China"
        );
        toCombo.setValue("Seoul (ICN) - South Korea");
        toCombo.setPrefWidth(260);
        toCombo.setStyle("-fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 12; -fx-border-color: " + GRAY_200 + "; -fx-border-width: 1; -fx-border-radius: 12; -fx-background-color: " + CREAM_50 + ";");

        toCombo.setButtonCell(new javafx.scene.control.ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText("🛬 " + item);
                    setStyle("-fx-font-size: 14px; -fx-text-fill: " + GRAY_900 + "; -fx-font-weight: 500;");
                }
            }
        });

        toBox.getChildren().addAll(toLabel, toCombo);

        // Swap button
        Button swapButton = new Button("⇄");
        swapButton.setStyle("-fx-background-color: " + PRIMARY_YELLOW + "; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 50; -fx-cursor: hand; -fx-border-width: 0; -fx-effect: dropshadow(gaussian, rgba(30,58,138,0.10), 8, 0, 0, 2);");
        swapButton.setPrefSize(45, 45);
        swapButton.setOnAction(e -> {
            String fromValue = fromCombo.getValue();
            String toValue = toCombo.getValue();
            fromCombo.setValue(toValue);
            toCombo.setValue(fromValue);
        });
        swapButton.setOnMouseEntered(e -> swapButton.setStyle("-fx-background-color: " + SECONDARY_YELLOW + "; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 50; -fx-cursor: hand; -fx-border-width: 0; -fx-effect: dropshadow(gaussian, rgba(59,130,246,0.18), 12, 0, 0, 3);"));
        swapButton.setOnMouseExited(e -> swapButton.setStyle("-fx-background-color: " + PRIMARY_YELLOW + "; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 50; -fx-cursor: hand; -fx-border-width: 0; -fx-effect: dropshadow(gaussian, rgba(30,58,138,0.10), 8, 0, 0, 2);"));

        secondRow.getChildren().addAll(fromBox, swapButton, toBox);

        // Third row - Dates
        HBox thirdRow = new HBox(24);

        VBox departureBox = new VBox(5);
        Label departureLabel = new Label("DEPARTURE");
        departureLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + GRAY_400 + ";");

        HBox departureContent = new HBox(10);
        departureContent.setAlignment(Pos.CENTER_LEFT);
        departureContent.setPadding(new Insets(15));
        departureContent.setStyle("-fx-background-color: " + CREAM_50 + "; -fx-background-radius: 12; -fx-border-color: " + GRAY_200 + "; -fx-border-width: 1; -fx-border-radius: 12;");
        departureContent.setPrefWidth(200);

        Label calIcon1 = new Label("📅");
        calIcon1.setStyle("-fx-font-size: 16px;");

        VBox departureDetails = new VBox(2);
        DatePicker departurePicker = new DatePicker(LocalDate.now().plusDays(1));
        departurePicker.setStyle("-fx-font-size: 14px; -fx-background-color: transparent; -fx-border-width: 0;");

        departureDetails.getChildren().add(departurePicker);
        departureContent.getChildren().addAll(calIcon1, departureDetails);
        departureBox.getChildren().addAll(departureLabel, departureContent);

        VBox returnBox = new VBox(5);
        Label returnLabel = new Label("RETURN");
        returnLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + GRAY_400 + ";");

        HBox returnContent = new HBox(10);
        returnContent.setAlignment(Pos.CENTER_LEFT);
        returnContent.setPadding(new Insets(15));
        returnContent.setStyle("-fx-background-color: " + CREAM_50 + "; -fx-background-radius: 12; -fx-border-color: " + GRAY_200 + "; -fx-border-width: 1; -fx-border-radius: 12;");
        returnContent.setPrefWidth(200);

        Label calIcon2 = new Label("📅");
        calIcon2.setStyle("-fx-font-size: 16px;");

        VBox returnDetails = new VBox(2);
        DatePicker returnPicker = new DatePicker(LocalDate.now().plusDays(8));
        returnPicker.setStyle("-fx-font-size: 14px; -fx-background-color: transparent; -fx-border-width: 0;");

        returnDetails.getChildren().add(returnPicker);
        returnContent.getChildren().addAll(calIcon2, returnDetails);
        returnBox.getChildren().addAll(returnLabel, returnContent);

        thirdRow.getChildren().addAll(departureBox, returnBox);

        // Search button
        Button searchFlightsBtn = createPrimaryButton("Search Flights", 250);
        HBox searchButtonBox = new HBox();
        searchButtonBox.setAlignment(Pos.CENTER);
        searchButtonBox.getChildren().add(searchFlightsBtn);

        // Search results area
        VBox resultsArea = new VBox(15);
        resultsArea.setPadding(new Insets(20, 0, 0, 0));

        Label resultsTitle = new Label("Available Flights");
        resultsTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_YELLOW + ";");
        resultsTitle.setVisible(false);

        VBox flightResults = new VBox(15);

        // Event handler for search
        searchFlightsBtn.setOnAction(e -> {
            String fromSelection = fromCombo.getValue();
            String toSelection = toCombo.getValue();
            String from = fromSelection.split(" - ")[0];
            String to = toSelection.split(" - ")[0];
            LocalDate depDate = departurePicker.getValue();
            int passengers = Integer.parseInt(passengersCombo.getValue().split(" ")[0]);

            List<Flight> flights = flightService.searchFlights(from, to, depDate, passengers);

            flightResults.getChildren().clear();

            if (flights.isEmpty()) {
                Label noFlights = new Label("No flights found for the selected criteria.");
                noFlights.setStyle("-fx-font-size: 16px; -fx-text-fill: " + GRAY_600 + ";");
                flightResults.getChildren().add(noFlights);
            } else {
                for (Flight flight : flights) {
                    VBox flightCard = createFlightResultCard(flight);
                    flightResults.getChildren().add(flightCard);
                }
            }

            resultsTitle.setVisible(true);
        });

        resultsArea.getChildren().addAll(resultsTitle, flightResults);

        searchForm.getChildren().addAll(firstRow, secondRow, thirdRow, searchButtonBox);
        content.getChildren().addAll(titleSection, searchForm, resultsArea);

        return content;
    }
    
    /**
     * Create flight result card - Based on Flight Details.png design
     */
    private VBox createFlightResultCard(Flight flight) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3); " +
                     "-fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-radius: 12;");
        
        // Flight header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label airlineLabel = new Label(flight.getAirline());
        airlineLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        Label flightNumLabel = new Label(flight.getFlightNumber());
        flightNumLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #718096; -fx-padding: 0 0 0 10;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label priceLabel = new Label("$" + String.format("%.0f", flight.getBasePrice()));
        priceLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #667eea;");
        
        header.getChildren().addAll(airlineLabel, flightNumLabel, spacer, priceLabel);
        
        // Flight details
        HBox details = new HBox(40);
        details.setAlignment(Pos.CENTER_LEFT);
        
        // Departure info
        VBox depInfo = new VBox(5);
        Label depTime = new Label(flight.getDepartureTime().toLocalTime().toString());
        depTime.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        Label depAirport = new Label(flight.getDepartureAirport());
        depAirport.setStyle("-fx-font-size: 14px; -fx-text-fill: #718096;");
        
        depInfo.getChildren().addAll(depTime, depAirport);
        
        // Flight duration and arrow
        VBox flightInfo = new VBox(5);
        flightInfo.setAlignment(Pos.CENTER);
        
        Label duration = new Label(flight.getFormattedDuration());
        duration.setStyle("-fx-font-size: 12px; -fx-text-fill: #718096;");
        
        Label arrow = new Label("✈️ ────────→");
        arrow.setStyle("-fx-font-size: 16px; -fx-text-fill: #A0AEC0;");
        
        Label direct = new Label("Direct");
        direct.setStyle("-fx-font-size: 12px; -fx-text-fill: #48BB78; -fx-font-weight: bold;");
        
        flightInfo.getChildren().addAll(duration, arrow, direct);
        
        // Arrival info
        VBox arrInfo = new VBox(5);
        Label arrTime = new Label(flight.getArrivalTime().toLocalTime().toString());
        arrTime.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        Label arrAirport = new Label(flight.getArrivalAirport());
        arrAirport.setStyle("-fx-font-size: 14px; -fx-text-fill: #718096;");
        
        arrInfo.getChildren().addAll(arrTime, arrAirport);
        
        details.getChildren().addAll(depInfo, flightInfo, arrInfo);
        
        // Footer with seats and book button
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_LEFT);
        
        Label seatsLabel = new Label(flight.getAvailableSeats() + " seats available");
        seatsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #718096;");
        
        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);
        
        Button viewDetailsBtn = new Button("View Details");
        viewDetailsBtn.setStyle("-fx-background-color: #E2E8F0; -fx-text-fill: #4A5568; -fx-font-size: 14px; " +
                               "-fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand; -fx-border-width: 0;");
        
        Button bookBtn = new Button("Book Now");
        bookBtn.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-size: 14px; " +
                        "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; " +
                        "-fx-cursor: hand; -fx-border-width: 0; -fx-margin: 0 0 0 10;");
        
        // Event handlers
        viewDetailsBtn.setOnAction(e -> showFlightDetailsScreen(flight));
        bookBtn.setOnAction(e -> showBookingScreen(flight));
        
        footer.getChildren().addAll(seatsLabel, footerSpacer, viewDetailsBtn, bookBtn);
        
        card.getChildren().addAll(header, details, footer);
        return card;
    }
    
    // Utility methods
    private TextField createStyledTextField(String prompt, double width) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefWidth(width);
        field.setStyle("-fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 8; " +
                      "-fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-radius: 8; " +
                      "-fx-background-color: white;");
        return field;
    }
    
    private PasswordField createStyledPasswordField(String prompt, double width) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setPrefWidth(width);
        field.setStyle("-fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 8; " +
                      "-fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-radius: 8; " +
                      "-fx-background-color: white;");
        return field;
    }
    
    private void addButtonHoverEffect(Button button, String normalColor, String hoverColor) {
        String normalStyle = button.getStyle();
        String hoverStyle = normalStyle.replace(normalColor, hoverColor);
        
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(normalStyle));
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Placeholder methods for dashboard components (to be implemented in next parts)
    private HBox createMainHeader(String title, String username) {
        HBox header = new HBox();
        header.setPadding(new Insets(15, 30, 15, 30));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label userLabel = new Label("Welcome, " + username);
        userLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #4A5568;");
        
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #E53E3E; -fx-text-fill: white; -fx-padding: 8 16; " +
                             "-fx-background-radius: 6; -fx-cursor: hand; -fx-border-width: 0;");
        logoutButton.setOnAction(e -> showLoginScreen());
        
        header.getChildren().addAll(titleLabel, spacer, userLabel, logoutButton);
        return header;
    }
    
    /**
     * Modern Admin Dashboard - Premium Design
     */
    private void showAdminDashboard() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + CREAM_50 + ";");
        
        // Enhanced top section with menu bar and header
        VBox topSection = new VBox();
        
        // Admin-specific Menu Bar
        MenuBar menuBar = createAdminMenuBar();
        topSection.getChildren().add(menuBar);
        
        // Enhanced Header
        HBox header = createDashboardHeader("Admin Dashboard", currentUser.getFirstName() + " " + currentUser.getLastName());
        topSection.getChildren().add(header);
        
        root.setTop(topSection);
        
        // Responsive Navigation sidebar
        VBox sidebar = createModernAdminSidebar(root);
        sidebar.prefWidthProperty().bind(root.widthProperty().multiply(0.2));
        sidebar.setMinWidth(250);
        sidebar.setMaxWidth(350);
        root.setLeft(sidebar);
        
        // Responsive Main content area
        StackPane contentArea = new StackPane();
        contentArea.setPadding(new Insets(20));
        contentArea.setStyle("-fx-background-color: " + CREAM_50 + ";");
        HBox.setHgrow(contentArea, Priority.ALWAYS);
        
        VBox dashboardContent = createModernAdminDashboardContent();
        contentArea.getChildren().add(dashboardContent);
        
        root.setCenter(contentArea);
        
        // Windowed scene with appropriate scaling
        adminDashboardScene = new Scene(root, 1400, 900);
        primaryStage.setScene(adminDashboardScene);
        primaryStage.setTitle("Pikachu Airlines - Admin Dashboard");
    }
    
    /**
     * Create Admin-specific Menu Bar
     */
    private MenuBar createAdminMenuBar() {
        MenuBar menuBar = new MenuBar();
        
        // Admin Menu
        Menu adminMenu = new Menu("Admin");
        MenuItem dashboardItem = new MenuItem("Dashboard");
        dashboardItem.setOnAction(e -> switchContent((BorderPane) adminDashboardScene.getRoot(), createModernAdminDashboardContent()));
        MenuItem logoutItem = new MenuItem("Logout");
        logoutItem.setOnAction(e -> {
            currentUser = null;
            showLoginScreen();
        });
        adminMenu.getItems().addAll(dashboardItem, new SeparatorMenuItem(), logoutItem);
        
        // Management Menu
        Menu managementMenu = new Menu("Management");
        MenuItem flightMgmtItem = new MenuItem("Flight Management");
        flightMgmtItem.setOnAction(e -> switchContent((BorderPane) adminDashboardScene.getRoot(), createFlightManagementContent()));
        MenuItem bookingMgmtItem = new MenuItem("Booking Management");
        bookingMgmtItem.setOnAction(e -> switchContent((BorderPane) adminDashboardScene.getRoot(), createAllBookingsContent()));
        MenuItem customerMgmtItem = new MenuItem("Customer Management");
        customerMgmtItem.setOnAction(e -> switchContent((BorderPane) adminDashboardScene.getRoot(), createCustomerManagementContent()));
        managementMenu.getItems().addAll(flightMgmtItem, bookingMgmtItem, customerMgmtItem);
        
        // Support Menu
        Menu supportMenu = new Menu("Support");
        MenuItem ticketMgmtItem = new MenuItem("Ticket Management");
        ticketMgmtItem.setOnAction(e -> switchContent((BorderPane) adminDashboardScene.getRoot(), createAdminTicketManagementContent()));
        MenuItem refundMgmtItem = new MenuItem("Refund Management");
        refundMgmtItem.setOnAction(e -> switchContent((BorderPane) adminDashboardScene.getRoot(), createRefundApprovalContent()));
        supportMenu.getItems().addAll(ticketMgmtItem, refundMgmtItem);
        
        // Reports Menu
        Menu reportsMenu = new Menu("Reports");
        MenuItem analyticsItem = new MenuItem("Analytics & Reports");
        analyticsItem.setOnAction(e -> switchContent((BorderPane) adminDashboardScene.getRoot(), createReportsContent()));
        reportsMenu.getItems().add(analyticsItem);
        
        menuBar.getMenus().addAll(adminMenu, managementMenu, supportMenu, reportsMenu);
        
        // Style the menu bar
        menuBar.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-border-color: " + GRAY_200 + ";" +
            "-fx-border-width: 0 0 1 0;"
        );
        
        return menuBar;
    }
    
    /**
     * Create Modern Admin Sidebar
     */
    private VBox createModernAdminSidebar(BorderPane parentRoot) {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(30, 20, 30, 20));
        sidebar.setMinWidth(250);
        sidebar.setMaxWidth(350);
        sidebar.setStyle("-fx-background-color: " + WHITE + "; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 10, 0, 2, 0); -fx-border-color: " + GRAY_200 + "; -fx-border-width: 0 1 0 0;");
        
        // Profile section
        VBox profileSection = new VBox(10);
        profileSection.setPadding(new Insets(0, 0, 30, 0));
        profileSection.setAlignment(Pos.CENTER);
        profileSection.setStyle("-fx-border-color: " + GRAY_200 + "; -fx-border-width: 0 0 1 0;");
        
        Label profilePic = new Label("👨‍💼");
        profilePic.setStyle("-fx-font-size: 48px; -fx-background-color: " + DANGER_RED + "22; -fx-background-radius: 50; -fx-padding: 12;");
        profilePic.setAlignment(Pos.CENTER);
        
        Label userName = new Label(currentUser.getFirstName() + " " + currentUser.getLastName());
        userName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + GRAY_900 + ";");
        
        Label userRole = new Label("Administrator");
        userRole.setStyle("-fx-font-size: 12px; -fx-text-fill: " + DANGER_RED + "; -fx-font-weight: bold; -fx-background-color: " + DANGER_RED + "22; -fx-padding: 4 8; -fx-background-radius: 12;");
        
        profileSection.getChildren().addAll(profilePic, userName, userRole);
        
        // Navigation menu
        VBox menuSection = new VBox(12);
        menuSection.setPadding(new Insets(30, 0, 0, 0));
        
        Label menuTitle = new Label("Admin Menu");
        menuTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + GRAY_400 + "; -fx-padding: 0 0 10 0;");
        
        // Menu buttons with modern colors
        Button overviewBtn = createSidebarButton("📊", "Dashboard", PRIMARY_YELLOW);
        Button flightsBtn = createSidebarButton("✈️", "Manage Flights", SECONDARY_YELLOW);
        Button bookingsBtn = createSidebarButton("📋", "All Bookings", SUCCESS_GREEN);
        Button customersBtn = createSidebarButton("👥", "Customers", PURPLE_ACCENT);
        Button ticketsBtn = createSidebarButton("🎫", "Support Tickets", WARNING_ORANGE);
        Button refundsBtn = createSidebarButton("💰", "Refund Approvals", DANGER_RED);
        Button reportsBtn = createSidebarButton("📈", "Reports", GRAY_600);
        
        // Event handlers for navigation
        overviewBtn.setOnAction(e -> switchContent(parentRoot, createModernAdminDashboardContent()));
        flightsBtn.setOnAction(e -> switchContent(parentRoot, createFlightManagementContent()));
        bookingsBtn.setOnAction(e -> switchContent(parentRoot, createAllBookingsContent()));
        customersBtn.setOnAction(e -> switchContent(parentRoot, createCustomerManagementContent()));
        ticketsBtn.setOnAction(e -> switchContent(parentRoot, createAdminTicketManagementContent()));
        refundsBtn.setOnAction(e -> switchContent(parentRoot, createRefundApprovalContent()));
        reportsBtn.setOnAction(e -> switchContent(parentRoot, createReportsContent()));
        
        menuSection.getChildren().addAll(menuTitle, overviewBtn, flightsBtn, bookingsBtn, 
                                        customersBtn, ticketsBtn, refundsBtn, reportsBtn);
        
        sidebar.getChildren().addAll(profileSection, menuSection);
        return sidebar;
    }
    
    /**
     * Create Modern Admin Dashboard Content
     */
    private VBox createModernAdminDashboardContent() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(20, 40, 20, 40));
        content.setStyle("-fx-background-color: " + CREAM_50 + ";");
        
        // Title section
        VBox titleSection = new VBox(8);
        Label title = new Label("Admin Dashboard");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_YELLOW + ";");
        
        Label subtitle = new Label("Monitor and manage airline operations");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: " + GRAY_600 + ";");
        
        titleSection.getChildren().addAll(title, subtitle);
        
        // Statistics cards with modern design
        HBox statsRow = new HBox(24);
        
        VBox totalFlights = createModernStatCard("✈️", "Total Flights", "125", SECONDARY_YELLOW);
        VBox totalBookings = createModernStatCard("📋", "Active Bookings", "89", SUCCESS_GREEN);
        VBox totalCustomers = createModernStatCard("👥", "Customers", "342", PURPLE_ACCENT);
        VBox pendingTickets = createModernStatCard("🎫", "Pending Tickets", "12", WARNING_ORANGE);
        
        statsRow.getChildren().addAll(totalFlights, totalBookings, totalCustomers, pendingTickets);
        
        // Recent activity section with modern styling
        VBox recentActivity = new VBox(20);
        recentActivity.setPadding(new Insets(30));
        recentActivity.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 18; -fx-effect: dropshadow(gaussian, rgba(59,130,246,0.08), 18, 0, 0, 6);");
        
        Label activityTitle = new Label("Recent Activity");
        activityTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + GRAY_900 + ";");
        
        VBox activityList = new VBox(12);
        activityList.getChildren().addAll(
            createModernActivityItem("New booking #BK2024001 created", "2 minutes ago", SUCCESS_GREEN),
            createModernActivityItem("Flight AA123 status updated to Delayed", "15 minutes ago", WARNING_ORANGE),
            createModernActivityItem("Support ticket #T001 submitted", "32 minutes ago", PURPLE_ACCENT),
            createModernActivityItem("Refund approved for booking #BK2024000", "1 hour ago", SECONDARY_YELLOW)
        );
        
        recentActivity.getChildren().addAll(activityTitle, activityList);
        
        content.getChildren().addAll(titleSection, statsRow, recentActivity);
        return content;
    }
    
    /**
     * Create Modern Stat Card
     */
    private VBox createModernStatCard(String icon, String title, String value, String color) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setPrefWidth(200);
        card.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-background-radius: 16;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 12, 0, 0, 4);" +
            "-fx-border-color: " + CREAM_100 + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 16;"
        );
        
        HBox iconTitleRow = new HBox(12);
        iconTitleRow.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle(
            "-fx-font-size: 24px;" +
            "-fx-background-color: " + color + "22;" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 8;"
        );
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + GRAY_600 + "; -fx-font-weight: 500;");
        
        iconTitleRow.getChildren().addAll(iconLabel, titleLabel);
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        card.getChildren().addAll(iconTitleRow, valueLabel);
        return card;
    }
    
    /**
     * Create Modern Activity Item
     */
    private HBox createModernActivityItem(String message, String time, String color) {
        HBox item = new HBox(15);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(12));
        item.setStyle("-fx-background-color: " + CREAM_50 + "; -fx-background-radius: 10;");
        
        Label dot = new Label("●");
        dot.setStyle("-fx-font-size: 12px; -fx-text-fill: " + color + ";");
        
        VBox messageBox = new VBox(2);
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + GRAY_900 + "; -fx-font-weight: 500;");
        
        Label timeLabel = new Label(time);
        timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + GRAY_400 + ";");
        
        messageBox.getChildren().addAll(messageLabel, timeLabel);
        
        item.getChildren().addAll(dot, messageBox);
        return item;
    }
    
    private VBox createStatCard(String icon, String title, String value, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(25));
        card.setPrefWidth(200);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 32px;");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #718096;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        card.getChildren().addAll(iconLabel, titleLabel, valueLabel);
        return card;
    }
    
    private HBox createActivityItem(String message, String time, String color) {
        HBox item = new HBox(15);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(10));
        item.setStyle("-fx-background-color: #F8F9FA; -fx-background-radius: 8;");
        
        Label dot = new Label("●");
        dot.setStyle("-fx-font-size: 12px; -fx-text-fill: " + color + ";");
        
        VBox messageBox = new VBox(2);
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2D3748;");
        
        Label timeLabel = new Label(time);
        timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #A0AEC0;");
        
        messageBox.getChildren().addAll(messageLabel, timeLabel);
        
        item.getChildren().addAll(dot, messageBox);
        return item;
    }
    
    private void showAirlineManagementDashboard() {
        // Placeholder - will implement in next part
        showAlert("Info", "Airline Management Dashboard - Implementation in progress");
    }
    
    /**
     * Modern Booking Overview Content
     */
    private VBox createBookingOverviewContent() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(20, 40, 20, 40));
        content.setStyle("-fx-background-color: " + CREAM_50 + ";");
        
        // Title section
        VBox titleSection = new VBox(8);
        Label title = new Label("My Bookings");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_YELLOW + ";");
        
        Label subtitle = new Label("Manage your flight reservations");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: " + GRAY_600 + ";");
        
        titleSection.getChildren().addAll(title, subtitle);
        
        // Get customer bookings
        if (currentUser instanceof Customer) {
            List<Booking> bookings = bookingService.getCustomerBookings(currentUser.getUserId());
            
            if (bookings.isEmpty()) {
                VBox emptyState = new VBox(20);
                emptyState.setAlignment(Pos.CENTER);
                emptyState.setPadding(new Insets(60));
                emptyState.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 18; -fx-effect: dropshadow(gaussian, rgba(59,130,246,0.08), 18, 0, 0, 6);");
                
                Label emptyIcon = new Label("✈️");
                emptyIcon.setStyle("-fx-font-size: 48px; -fx-opacity: 0.5;");
                
                Label noBookings = new Label("No bookings found");
                noBookings.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + GRAY_800 + ";");
                
                Label suggestion = new Label("Start by searching for flights to book your first trip!");
                suggestion.setStyle("-fx-font-size: 14px; -fx-text-fill: " + GRAY_600 + ";");
                
                Button searchFlightsBtn = createPrimaryButton("Search Flights", 200);
                searchFlightsBtn.setOnAction(e -> switchContent((BorderPane) content.getParent().getParent(), createModernFlightSearchContent()));
                
                emptyState.getChildren().addAll(emptyIcon, noBookings, suggestion, searchFlightsBtn);
                content.getChildren().addAll(titleSection, emptyState);
            } else {
                VBox bookingsContainer = new VBox(20);
                for (Booking booking : bookings) {
                    VBox bookingCard = createModernBookingCard(booking);
                    bookingsContainer.getChildren().add(bookingCard);
                }
                content.getChildren().addAll(titleSection, bookingsContainer);
            }
        }
        
        return content;
    }
    
    /**
     * Create Modern Booking Card
     */
    private VBox createModernBookingCard(Booking booking) {
        VBox card = new VBox(20);
        card.setPadding(new Insets(25));
        card.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-background-radius: 16;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 12, 0, 0, 4);" +
            "-fx-border-color: " + CREAM_100 + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 16;"
        );
        
        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        
        VBox bookingInfo = new VBox(5);
        Label bookingId = new Label("Booking #" + booking.getBookingId());
        bookingId.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + GRAY_900 + ";");
        
        Label bookingDate = new Label("Booked on " + booking.getBookingDate().toLocalDate().toString());
        bookingDate.setStyle("-fx-font-size: 12px; -fx-text-fill: " + GRAY_600 + ";");
        
        bookingInfo.getChildren().addAll(bookingId, bookingDate);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label status = new Label(booking.getStatus().toString());
        String statusColor = getModernStatusColor(booking.getStatus());
        status.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + statusColor + ";" +
            "-fx-background-color: " + statusColor + "22;" +
            "-fx-padding: 6 12;" +
            "-fx-background-radius: 12;"
        );
        
        header.getChildren().addAll(bookingInfo, spacer, status);
        
        // Booking details with modern styling
        HBox details = new HBox(30);
        details.setAlignment(Pos.CENTER_LEFT);
        
        VBox passengersInfo = new VBox(3);
        Label passengersLabel = new Label("Passengers");
        passengersLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + GRAY_400 + "; -fx-font-weight: 600;");
        Label passengersValue = new Label(String.valueOf(booking.getPassengers().size()));
        passengersValue.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + GRAY_800 + ";");
        passengersInfo.getChildren().addAll(passengersLabel, passengersValue);
        
        VBox priceInfo = new VBox(3);
        Label priceLabel = new Label("Total Price");
        priceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + GRAY_400 + "; -fx-font-weight: 600;");
        Label priceValue = new Label("$" + String.format("%.2f", booking.getTotalPrice()));
        priceValue.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_YELLOW + ";");
        priceInfo.getChildren().addAll(priceLabel, priceValue);
        
        details.getChildren().addAll(passengersInfo, priceInfo);
        
        // Action buttons with modern styling
        HBox actions = new HBox(12);
        
        Button viewBtn = new Button("View Details");
        viewBtn.setStyle(
            "-fx-background-color: " + SECONDARY_YELLOW + ";" +
            "-fx-text-fill: " + WHITE + ";" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 10 20;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-border-width: 0;"
        );
        viewBtn.setOnAction(e -> showBookingDetailsScreen(booking));
        
        Button cancelBtn = new Button("Cancel Booking");
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            cancelBtn.setStyle(
                "-fx-background-color: " + DANGER_RED + ";" +
                "-fx-text-fill: " + WHITE + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: 600;" +
                "-fx-padding: 10 20;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-border-width: 0;"
            );
            cancelBtn.setOnAction(e -> handleBookingCancellation(booking));
        } else {
            cancelBtn.setDisable(true);
            cancelBtn.setStyle(
                "-fx-background-color: " + GRAY_200 + ";" +
                "-fx-text-fill: " + GRAY_400 + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: 600;" +
                "-fx-padding: 10 20;" +
                "-fx-background-radius: 8;" +
                "-fx-border-width: 0;"
            );
        }
        
        actions.getChildren().addAll(viewBtn, cancelBtn);
        
        card.getChildren().addAll(header, details, actions);
        return card;
    }
    
    /**
     * Get modern status colors
     */
    private String getModernStatusColor(BookingStatus status) {
        switch (status) {
            case CONFIRMED: return SUCCESS_GREEN;
            case PENDING: return WARNING_ORANGE;
            case CANCELLED: return DANGER_RED;
            default: return GRAY_600;
        }
    }
    
    private String getStatusColor(BookingStatus status) {
        switch (status) {
            case CONFIRMED: return "#48BB78";
            case PENDING: return "#ED8936";
            case CANCELLED: return "#E53E3E";
            default: return "#4A5568";
        }
    }
    
    private VBox createFlightInformationContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        
        Label title = new Label("Flight Information");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        Label subtitle = new Label("Real-time flight status and information");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #718096;");
        
        // Search by flight number
        VBox searchSection = new VBox(15);
        searchSection.setPadding(new Insets(20));
        searchSection.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                              "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        
        Label searchLabel = new Label("Search Flight Status");
        searchLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        HBox searchBox = new HBox(15);
        TextField flightNumberField = new TextField();
        flightNumberField.setPromptText("Enter flight number (e.g., AA123)");
        flightNumberField.setPrefWidth(300);
        flightNumberField.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-radius: 8; " +
                                  "-fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-radius: 8;");
        
        Button searchFlightBtn = new Button("Search");
        searchFlightBtn.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-size: 14px; " +
                                "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; " +
                                "-fx-cursor: hand; -fx-border-width: 0;");
        
        searchBox.getChildren().addAll(flightNumberField, searchFlightBtn);
        searchSection.getChildren().addAll(searchLabel, searchBox);
        
        // Flight status results
        VBox resultsSection = new VBox(15);
        
        searchFlightBtn.setOnAction(e -> {
            String flightNum = flightNumberField.getText().trim();
            if (!flightNum.isEmpty()) {
                Optional<Flight> flight = flightService.getFlightDetails(flightNum);
                resultsSection.getChildren().clear();
                
                if (flight.isPresent()) {
                    VBox flightStatusCard = createFlightStatusCard(flight.get());
                    resultsSection.getChildren().add(flightStatusCard);
                } else {
                    Label notFound = new Label("Flight not found. Please check the flight number.");
                    notFound.setStyle("-fx-font-size: 16px; -fx-text-fill: #E53E3E; -fx-padding: 20;");
                    resultsSection.getChildren().add(notFound);
                }
            }
        });
        
        content.getChildren().addAll(title, subtitle, searchSection, resultsSection);
        return content;
    }
    
    private VBox createFlightStatusCard(Flight flight) {
        VBox card = new VBox(20);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3); " +
                     "-fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-radius: 12;");
        
        // Flight header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label flightInfo = new Label(flight.getAirline() + " " + flight.getFlightNumber());
        flightInfo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label status = new Label(flight.getStatus().toString());
        String statusColor = getFlightStatusColor(flight.getStatus());
        status.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + statusColor + "; " +
                       "-fx-background-color: " + statusColor + "22; -fx-padding: 8 15; -fx-background-radius: 20;");
        
        header.getChildren().addAll(flightInfo, spacer, status);
        
        // Route information
        HBox routeInfo = new HBox(50);
        routeInfo.setAlignment(Pos.CENTER);
        
        VBox depInfo = new VBox(5);
        depInfo.setAlignment(Pos.CENTER);
        Label depTime = new Label(flight.getDepartureTime().toLocalTime().toString());
        depTime.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        Label depAirport = new Label(flight.getDepartureAirport());
        depAirport.setStyle("-fx-font-size: 16px; -fx-text-fill: #718096;");
        Label depDate = new Label(flight.getDepartureTime().toLocalDate().toString());
        depDate.setStyle("-fx-font-size: 12px; -fx-text-fill: #A0AEC0;");
        depInfo.getChildren().addAll(depTime, depAirport, depDate);
        
        VBox routeDetails = new VBox(5);
        routeDetails.setAlignment(Pos.CENTER);
        Label duration = new Label(flight.getFormattedDuration());
        duration.setStyle("-fx-font-size: 14px; -fx-text-fill: #718096;");
        Label arrow = new Label("✈️ ──────→");
        arrow.setStyle("-fx-font-size: 20px; -fx-text-fill: #A0AEC0;");
        Label direct = new Label("Direct Flight");
        direct.setStyle("-fx-font-size: 12px; -fx-text-fill: #48BB78; -fx-font-weight: bold;");
        routeDetails.getChildren().addAll(duration, arrow, direct);
        
        VBox arrInfo = new VBox(5);
        arrInfo.setAlignment(Pos.CENTER);
        Label arrTime = new Label(flight.getArrivalTime().toLocalTime().toString());
        arrTime.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        Label arrAirport = new Label(flight.getArrivalAirport());
        arrAirport.setStyle("-fx-font-size: 16px; -fx-text-fill: #718096;");
        Label arrDate = new Label(flight.getArrivalTime().toLocalDate().toString());
        arrDate.setStyle("-fx-font-size: 12px; -fx-text-fill: #A0AEC0;");
        arrInfo.getChildren().addAll(arrTime, arrAirport, arrDate);
        
        routeInfo.getChildren().addAll(depInfo, routeDetails, arrInfo);
        
        // Additional details
        HBox additionalInfo = new HBox(40);
        additionalInfo.setAlignment(Pos.CENTER);
        
        VBox priceInfo = new VBox(3);
        priceInfo.setAlignment(Pos.CENTER);
        Label priceLabel = new Label("Starting From");
        priceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #A0AEC0;");
        Label price = new Label("$" + String.format("%.0f", flight.getBasePrice()));
        price.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #667eea;");
        priceInfo.getChildren().addAll(priceLabel, price);
        
        VBox seatsInfo = new VBox(3);
        seatsInfo.setAlignment(Pos.CENTER);
        Label seatsLabel = new Label("Available Seats");
        seatsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #A0AEC0;");
        Label seats = new Label(String.valueOf(flight.getAvailableSeats()));
        seats.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #48BB78;");
        seatsInfo.getChildren().addAll(seatsLabel, seats);
        
        additionalInfo.getChildren().addAll(priceInfo, seatsInfo);
        
        card.getChildren().addAll(header, routeInfo, additionalInfo);
        return card;
    }
    
    private String getFlightStatusColor(FlightStatus status) {
        switch (status) {
            case SCHEDULED: return "#4299E1";
            case BOARDING: return "#48BB78";
            case DEPARTED: return "#9F7AEA";
            case ARRIVED: return "#38B2AC";
            case DELAYED: return "#ED8936";
            case CANCELLED: return "#E53E3E";
            default: return "#4A5568";
        }
    }
    
    private VBox createSupportChatContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        
        Label title = new Label("Support & Chat");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        Label subtitle = new Label("Get help with AI assistance or submit a support ticket");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #718096;");
        
        // Quick help section
        VBox quickHelpSection = new VBox(15);
        quickHelpSection.setPadding(new Insets(20));
        quickHelpSection.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                                 "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        
        Label quickHelpTitle = new Label("Quick Help");
        quickHelpTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        HBox quickHelpButtons = new HBox(15);
        Button bookingHelpBtn = createQuickHelpButton("📋 Booking Issues", "#4299E1");
        Button flightHelpBtn = createQuickHelpButton("✈️ Flight Status", "#48BB78");
        Button refundHelpBtn = createQuickHelpButton("💰 Refunds", "#ED8936");
        Button generalHelpBtn = createQuickHelpButton("❓ General Help", "#9F7AEA");
        
        quickHelpButtons.getChildren().addAll(bookingHelpBtn, flightHelpBtn, refundHelpBtn, generalHelpBtn);
        quickHelpSection.getChildren().addAll(quickHelpTitle, quickHelpButtons);
        
        // Chat section
        VBox chatSection = new VBox(15);
        chatSection.setPadding(new Insets(20));
        chatSection.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        
        Label chatTitle = new Label("AI Assistant Chat");
        chatTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        // Chat messages area
        VBox chatMessages = new VBox(10);
        chatMessages.setPrefHeight(300);
        chatMessages.setStyle("-fx-background-color: #F7FAFC; -fx-background-radius: 8; " +
                             "-fx-padding: 15; -fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-radius: 8;");
        
        // Initial AI message
        VBox welcomeMessage = createChatMessage("AI Assistant", 
            "Hello! I'm here to help you with your travel needs. You can ask me about:\n" +
            "• Flight bookings and changes\n" +
            "• Check-in procedures\n" +
            "• Baggage policies\n" +
            "• Refund requests\n" +
            "\nHow can I assist you today?", true);
        chatMessages.getChildren().add(welcomeMessage);
        
        // Chat input
        HBox chatInput = new HBox(10);
        TextField messageField = new TextField();
        messageField.setPromptText("Type your message here...");
        messageField.setPrefHeight(40);
        messageField.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-radius: 20; " +
                             "-fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-radius: 20;");
        HBox.setHgrow(messageField, Priority.ALWAYS);
        
        Button sendBtn = new Button("Send");
        sendBtn.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-size: 14px; " +
                        "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 20; " +
                        "-fx-cursor: hand; -fx-border-width: 0;");
        
        chatInput.getChildren().addAll(messageField, sendBtn);
        
        // Enhanced Chat event handler with LangChain4j AI
        sendBtn.setOnAction(e -> {
            String message = messageField.getText().trim();
            if (!message.isEmpty()) {
                messageField.clear();
                
                // Disable send button during AI processing
                sendBtn.setDisable(true);
                sendBtn.setText("Processing...");
                
                // Check if AI service is ready
                if (!aiService.isReady()) {
                    // Fallback to basic response if AI not ready
                    VBox userMessage = createChatMessage("You", message, false);
                    chatMessages.getChildren().add(userMessage);
                    
                    String fallbackResponse = "I'm currently initializing my AI capabilities. Please try again in a moment, or contact our customer service at 1-800-PIKACHU for immediate assistance.";
                    VBox aiMessage = createChatMessage("AI Assistant", fallbackResponse, true);
                    chatMessages.getChildren().add(aiMessage);
                    
                    // Re-enable send button
                    sendBtn.setDisable(false);
                    sendBtn.setText("Send");
                    return;
                }
                
                // Add user message to chat
                VBox userMessage = createChatMessage("You", message, false);
                chatMessages.getChildren().add(userMessage);
                
                // Create a TextArea for streaming AI response
                javafx.scene.control.TextArea streamingArea = new javafx.scene.control.TextArea();
                streamingArea.setEditable(false);
                streamingArea.setWrapText(true);
                streamingArea.setPrefRowCount(3);
                streamingArea.setStyle(
                    "-fx-font-size: 14px;" +
                    "-fx-text-fill: #2D3748;" +
                    "-fx-background-color: #F0F4FF;" +
                    "-fx-border-width: 0;" +
                    "-fx-background-radius: 8;"
                );
                
                // Create streaming message container
                VBox streamingMessage = new VBox(5);
                streamingMessage.setPadding(new Insets(8, 12, 8, 12));
                streamingMessage.setAlignment(Pos.CENTER_LEFT);
                
                Label aiSenderLabel = new Label("🤖 Pikachu Airlines AI Assistant");
                aiSenderLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #667eea;");
                
                streamingMessage.getChildren().addAll(aiSenderLabel, streamingArea);
                chatMessages.getChildren().add(streamingMessage);
                
                // Send message to AI service with streaming response
                aiService.sendMessage(message, streamingArea, () -> {
                    // Re-enable send button when response is complete
                    javafx.application.Platform.runLater(() -> {
                        sendBtn.setDisable(false);
                        sendBtn.setText("Send");
                    });
                });
            }
        });
        
        messageField.setOnAction(e -> sendBtn.fire());
        
        chatSection.getChildren().addAll(chatTitle, chatMessages, chatInput);
        
        // Submit ticket section
        VBox ticketSection = new VBox(15);
        ticketSection.setPadding(new Insets(20));
        ticketSection.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                              "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        
        Label ticketTitle = new Label("Submit Support Ticket");
        ticketTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        Button submitTicketBtn = new Button("Create New Ticket");
        submitTicketBtn.setStyle("-fx-background-color: #48BB78; -fx-text-fill: white; -fx-font-size: 16px; " +
                                "-fx-font-weight: bold; -fx-padding: 12 24; -fx-background-radius: 8; " +
                                "-fx-cursor: hand; -fx-border-width: 0;");
        submitTicketBtn.setOnAction(e -> showTicketSubmissionScreen());
        
        ticketSection.getChildren().addAll(ticketTitle, submitTicketBtn);
        
        content.getChildren().addAll(title, subtitle, quickHelpSection, chatSection, ticketSection);
        return content;
    }
    
    private VBox createChatMessage(String sender, String message, boolean isAI) {
        VBox messageContainer = new VBox(5);
        messageContainer.setPadding(new Insets(8, 12, 8, 12));
        
        Label senderLabel = new Label(sender);
        senderLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + 
                           (isAI ? "#667eea" : "#48BB78") + ";");
        
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2D3748; -fx-padding: 8; " +
                             "-fx-background-color: " + (isAI ? "#F0F4FF" : "#F0FFF4") + "; " +
                             "-fx-background-radius: 8;");
        
        messageContainer.getChildren().addAll(senderLabel, messageLabel);
        
        if (!isAI) {
            messageContainer.setAlignment(Pos.CENTER_RIGHT);
            messageContainer.setMaxWidth(400);
        } else {
            messageContainer.setAlignment(Pos.CENTER_LEFT);
        }
        
        return messageContainer;
    }
    
    private String generateAIResponse(String userMessage) {
        // Simplified AI response generator
        String lowerMessage = userMessage.toLowerCase();
        
        if (lowerMessage.contains("booking") || lowerMessage.contains("book")) {
            return "I can help you with booking-related questions! You can:\n" +
                   "• View your current bookings in the 'My Bookings' section\n" +
                   "• Search for new flights using our flight search\n" +
                   "• Modify existing bookings (subject to airline policies)\n" +
                   "\nIs there a specific booking issue you need help with?";
        } else if (lowerMessage.contains("refund") || lowerMessage.contains("cancel")) {
            return "For refunds and cancellations:\n" +
                   "• You can cancel eligible bookings from your 'My Bookings' page\n" +
                   "• Refund processing typically takes 5-7 business days\n" +
                   "• Refund eligibility depends on your ticket type\n" +
                   "\nWould you like me to help you with a specific refund request?";
        } else if (lowerMessage.contains("flight") || lowerMessage.contains("status")) {
            return "For flight information:\n" +
                   "• Check real-time flight status in the 'Flight Information' section\n" +
                   "• Get updates on delays, gate changes, and boarding times\n" +
                   "• View detailed flight schedules and routes\n" +
                   "\nDo you need help finding information about a specific flight?";
        } else {
            return "Thank you for your question! I'm here to help with:\n" +
                   "• Flight bookings and modifications\n" +
                   "• Check-in and boarding passes\n" +
                   "• Baggage policies and fees\n" +
                   "• Refunds and cancellations\n" +
                   "\nIf you need more detailed assistance, I can connect you with a human agent. How can I help you today?";
        }
    }
    
    private Button createQuickHelpButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 14px; " +
                       "-fx-padding: 10 15; -fx-background-radius: 8; -fx-cursor: hand; -fx-border-width: 0;");
        button.setPrefWidth(150);
        return button;
    }
    
    private VBox createTicketOverviewContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        
        Label title = new Label("My Support Tickets");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        Label subtitle = new Label("Track your support requests");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #718096;");
        
        // Create new ticket button
        Button newTicketBtn = new Button("Create New Ticket");
        newTicketBtn.setStyle("-fx-background-color: #48BB78; -fx-text-fill: white; -fx-font-size: 16px; " +
                             "-fx-font-weight: bold; -fx-padding: 12 24; -fx-background-radius: 8; " +
                             "-fx-cursor: hand; -fx-border-width: 0;");
        newTicketBtn.setOnAction(e -> showTicketSubmissionScreen());
        
        // Get customer tickets (placeholder - would use TicketService)
        VBox ticketsContainer = new VBox(15);
        
        // Sample ticket cards (in real implementation, fetch from TicketService)
        Label noTickets = new Label("No support tickets found. Create a new ticket if you need assistance.");
        noTickets.setStyle("-fx-font-size: 16px; -fx-text-fill: #718096; -fx-padding: 40;");
        ticketsContainer.getChildren().add(noTickets);
        
        content.getChildren().addAll(title, subtitle, newTicketBtn, ticketsContainer);
        return content;
    }
    
    private VBox createCustomerDetailsContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        
        Label title = new Label("Profile Settings");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        Label subtitle = new Label("Manage your account information");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #718096;");
        
        // Profile form
        VBox profileForm = new VBox(20);
        profileForm.setPadding(new Insets(30));
        profileForm.setMaxWidth(600);
        profileForm.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        
        // Personal information section
        Label personalInfoLabel = new Label("Personal Information");
        personalInfoLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        HBox nameFields = new HBox(15);
        TextField firstNameField = createStyledTextField("First Name", 200);
        firstNameField.setText(currentUser.getFirstName());
        TextField lastNameField = createStyledTextField("Last Name", 200);
        lastNameField.setText(currentUser.getLastName());
        nameFields.getChildren().addAll(firstNameField, lastNameField);
        
        TextField emailField = createStyledTextField("Email Address", 415);
        emailField.setText(currentUser.getEmail());
        
        TextField phoneField = createStyledTextField("Phone Number", 415);
        phoneField.setText(currentUser.getPhoneNumber());
        
        // Save button
        Button saveBtn = new Button("Save Changes");
        saveBtn.setStyle("-fx-background-color: #48BB78; -fx-text-fill: white; -fx-font-size: 16px; " +
                        "-fx-font-weight: bold; -fx-padding: 12 24; -fx-background-radius: 8; " +
                        "-fx-cursor: hand; -fx-border-width: 0;");
        
        saveBtn.setOnAction(e -> {
            // Update user profile (in real implementation)
            currentUser.setFirstName(firstNameField.getText());
            currentUser.setLastName(lastNameField.getText());
            currentUser.setEmail(emailField.getText());
            currentUser.setPhoneNumber(phoneField.getText());
            
            showAlert("Success", "Profile updated successfully!");
        });
        
        profileForm.getChildren().addAll(personalInfoLabel, nameFields, emailField, phoneField, saveBtn);
        content.getChildren().addAll(title, subtitle, profileForm);
        return content;
    }
    
    // Methods for showing detailed screens
    private void showFlightDetailsScreen(Flight flight) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background: linear-gradient(to bottom, #F7FAFC, #EDF2F7);");
        
        // Header with back button
        HBox header = new HBox(20);
        header.setPadding(new Insets(20, 30, 20, 30));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        
        Button backBtn = new Button("← Back");
        backBtn.setStyle("-fx-background-color: #E2E8F0; -fx-text-fill: #4A5568; -fx-font-size: 14px; " +
                        "-fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand; -fx-border-width: 0;");
        backBtn.setOnAction(e -> showCustomerDashboard());
        
        Label title = new Label("Flight Details - " + flight.getFlightNumber());
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        header.getChildren().addAll(backBtn, title);
        root.setTop(header);
        
        // Main content
        VBox content = new VBox(30);
        content.setPadding(new Insets(40));
        content.setAlignment(Pos.CENTER);
        
        // Flight information card
        VBox flightCard = new VBox(25);
        flightCard.setPadding(new Insets(30));
        flightCard.setMaxWidth(800);
        flightCard.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                           "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 20, 0, 0, 5);");
        
        // Airline and flight header
        HBox flightHeader = new HBox();
        flightHeader.setAlignment(Pos.CENTER_LEFT);
        
        VBox airlineInfo = new VBox(5);
        Label airlineName = new Label(flight.getAirline());
        airlineName.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        Label flightNumber = new Label("Flight " + flight.getFlightNumber());
        flightNumber.setStyle("-fx-font-size: 16px; -fx-text-fill: #718096;");
        
        airlineInfo.getChildren().addAll(airlineName, flightNumber);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Flight status
        Label statusLabel = new Label(flight.getStatus().toString());
        String statusColor = getFlightStatusColor(flight.getStatus());
        statusLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + statusColor + "; " +
                            "-fx-background-color: " + statusColor + "22; -fx-padding: 10 20; -fx-background-radius: 25;");
        
        flightHeader.getChildren().addAll(airlineInfo, spacer, statusLabel);
        
        // Route details
        HBox routeDetails = new HBox(60);
        routeDetails.setAlignment(Pos.CENTER);
        routeDetails.setPadding(new Insets(20, 0, 20, 0));
        
        VBox departureDetails = new VBox(8);
        departureDetails.setAlignment(Pos.CENTER);
        
        Label depTime = new Label(flight.getDepartureTime().toLocalTime().toString());
        depTime.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        Label depAirport = new Label(flight.getDepartureAirport());
        depAirport.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #4A5568;");
        
        Label depDate = new Label(flight.getDepartureTime().toLocalDate().toString());
        depDate.setStyle("-fx-font-size: 14px; -fx-text-fill: #718096;");
        
        departureDetails.getChildren().addAll(depTime, depAirport, depDate);
        
        // Flight path
        VBox flightPath = new VBox(8);
        flightPath.setAlignment(Pos.CENTER);
        
        Label duration = new Label(flight.getFormattedDuration());
        duration.setStyle("-fx-font-size: 14px; -fx-text-fill: #718096;");
        
        Label arrow = new Label("✈️ ──────→");
        arrow.setStyle("-fx-font-size: 24px; -fx-text-fill: #A0AEC0;");
        
        Label directFlight = new Label("Direct Flight");
        directFlight.setStyle("-fx-font-size: 12px; -fx-text-fill: #48BB78; -fx-font-weight: bold;");
        
        flightPath.getChildren().addAll(duration, arrow, directFlight);
        
        VBox arrivalDetails = new VBox(8);
        arrivalDetails.setAlignment(Pos.CENTER);
        
        Label arrTime = new Label(flight.getArrivalTime().toLocalTime().toString());
        arrTime.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        Label arrAirport = new Label(flight.getArrivalAirport());
        arrAirport.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #4A5568;");
        
        Label arrDate = new Label(flight.getArrivalTime().toLocalDate().toString());
        arrDate.setStyle("-fx-font-size: 14px; -fx-text-fill: #718096;");
        
        arrivalDetails.getChildren().addAll(arrTime, arrAirport, arrDate);
        
        routeDetails.getChildren().addAll(departureDetails, flightPath, arrivalDetails);
        
        // Additional flight information
        HBox additionalInfo = new HBox(50);
        additionalInfo.setAlignment(Pos.CENTER);
        additionalInfo.setPadding(new Insets(20, 0, 0, 0));
        
        VBox priceDetails = new VBox(5);
        priceDetails.setAlignment(Pos.CENTER);
        Label priceHeader = new Label("Starting Price");
        priceHeader.setStyle("-fx-font-size: 14px; -fx-text-fill: #A0AEC0;");
        Label price = new Label("$" + String.format("%.0f", flight.getBasePrice()));
        price.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #667eea;");
        priceDetails.getChildren().addAll(priceHeader, price);
        
        VBox seatsDetails = new VBox(5);
        seatsDetails.setAlignment(Pos.CENTER);
        Label seatsHeader = new Label("Available Seats");
        seatsHeader.setStyle("-fx-font-size: 14px; -fx-text-fill: #A0AEC0;");
        Label seats = new Label(String.valueOf(flight.getAvailableSeats()));
        seats.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #48BB78;");
        seatsDetails.getChildren().addAll(seatsHeader, seats);
        
        additionalInfo.getChildren().addAll(priceDetails, seatsDetails);
        
        // Action buttons
        HBox actionButtons = new HBox(20);
        actionButtons.setAlignment(Pos.CENTER);
        actionButtons.setPadding(new Insets(30, 0, 0, 0));
        
        Button bookFlightBtn = new Button("Book This Flight");
        bookFlightBtn.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-size: 18px; " +
                              "-fx-font-weight: bold; -fx-padding: 15 40; -fx-background-radius: 10; " +
                              "-fx-cursor: hand; -fx-border-width: 0;");
        bookFlightBtn.setOnAction(e -> showBookingScreen(flight));
        
        actionButtons.getChildren().add(bookFlightBtn);
        
        flightCard.getChildren().addAll(flightHeader, routeDetails, additionalInfo, actionButtons);
        content.getChildren().add(flightCard);
        
        root.setCenter(content);
        
        Scene flightDetailsScene = new Scene(root, 1200, 800);
        primaryStage.setScene(flightDetailsScene);
        primaryStage.setTitle("Flight Details - " + flight.getFlightNumber());
    }
    
    private void showBookingScreen(Flight flight) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background: linear-gradient(to bottom, #F7FAFC, #EDF2F7);");
        
        // Header with back button
        HBox header = new HBox(20);
        header.setPadding(new Insets(20, 30, 20, 30));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        
        Button backBtn = new Button("← Back");
        backBtn.setStyle("-fx-background-color: #E2E8F0; -fx-text-fill: #4A5568; -fx-font-size: 14px; " +
                        "-fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand; -fx-border-width: 0;");
        backBtn.setOnAction(e -> showCustomerDashboard());
        
        Label title = new Label("Book Flight - " + flight.getFlightNumber());
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        header.getChildren().addAll(backBtn, title);
        root.setTop(header);
        
        // Main content
        HBox mainContent = new HBox(30);
        mainContent.setPadding(new Insets(30));
        
        // Left side - Booking form
        VBox bookingForm = new VBox(25);
        bookingForm.setPrefWidth(500);
        bookingForm.setPadding(new Insets(30));
        bookingForm.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);");
        
        Label formTitle = new Label("Passenger Information");
        formTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        // Passenger details
        VBox passengerSection = new VBox(15);
        
        Label passengerLabel = new Label("Primary Passenger");
        passengerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #4A5568;");
        
        HBox nameRow = new HBox(15);
        TextField firstNameField = createStyledTextField("First Name", 200);
        firstNameField.setText(currentUser.getFirstName());
        TextField lastNameField = createStyledTextField("Last Name", 200);
        lastNameField.setText(currentUser.getLastName());
        nameRow.getChildren().addAll(firstNameField, lastNameField);
        
        TextField emailField = createStyledTextField("Email Address", 415);
        emailField.setText(currentUser.getEmail());
        
        TextField phoneField = createStyledTextField("Phone Number", 415);
        phoneField.setText(currentUser.getPhoneNumber());
        
        DatePicker dobPicker = new DatePicker();
        dobPicker.setPromptText("Date of Birth");
        dobPicker.setPrefWidth(415);
        dobPicker.setStyle("-fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 8; " +
                          "-fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-radius: 8;");
        
        passengerSection.getChildren().addAll(passengerLabel, nameRow, emailField, phoneField, dobPicker);
        
        // Seat selection
        VBox seatSection = new VBox(15);
        
        Label seatLabel = new Label("Seat Preference");
        seatLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #4A5568;");
        
        ComboBox<String> seatCombo = new ComboBox<>();
        seatCombo.getItems().addAll("Window Seat", "Aisle Seat", "Middle Seat", "No Preference");
        seatCombo.setValue("No Preference");
        seatCombo.setPrefWidth(415);
        seatCombo.setStyle("-fx-font-size: 14px; -fx-padding: 8;");
        
        seatSection.getChildren().addAll(seatLabel, seatCombo);
        
        // Payment section
        VBox paymentSection = new VBox(15);
        
        Label paymentLabel = new Label("Payment Information");
        paymentLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #4A5568;");
        
        TextField cardNumberField = createStyledTextField("Card Number", 415);
        cardNumberField.setPromptText("1234 5678 9012 3456");
        
        HBox cardDetailsRow = new HBox(15);
        TextField expiryField = createStyledTextField("MM/YY", 120);
        TextField cvvField = createStyledTextField("CVV", 100);
        TextField cardNameField = createStyledTextField("Cardholder Name", 160);
        cardDetailsRow.getChildren().addAll(expiryField, cvvField, cardNameField);
        
        TextField billingAddressField = createStyledTextField("Billing Address", 415);
        
        paymentSection.getChildren().addAll(paymentLabel, cardNumberField, cardDetailsRow, billingAddressField);
        
        // Book button
        Button bookButton = new Button("Confirm Booking");
        bookButton.setStyle("-fx-background-color: #48BB78; -fx-text-fill: white; -fx-font-size: 18px; " +
                           "-fx-font-weight: bold; -fx-padding: 15 30; -fx-background-radius: 10; " +
                           "-fx-cursor: hand; -fx-border-width: 0;");
        bookButton.setPrefWidth(415);
        bookButton.setOnAction(e -> {
            // Comprehensive validation for all customer details
            StringBuilder validationErrors = new StringBuilder();
            
            // Validate passenger information
            if (firstNameField.getText().trim().isEmpty()) {
                validationErrors.append("• First Name is required\n");
            }
            if (lastNameField.getText().trim().isEmpty()) {
                validationErrors.append("• Last Name is required\n");
            }
            if (emailField.getText().trim().isEmpty()) {
                validationErrors.append("• Email Address is required\n");
            } else if (!emailField.getText().contains("@") || !emailField.getText().contains(".")) {
                validationErrors.append("• Please enter a valid email address\n");
            }
            if (phoneField.getText().trim().isEmpty()) {
                validationErrors.append("• Phone Number is required\n");
            }
            if (dobPicker.getValue() == null) {
                validationErrors.append("• Date of Birth is required\n");
            } else if (dobPicker.getValue().isAfter(LocalDate.now().minusYears(12))) {
                validationErrors.append("• Passenger must be at least 12 years old\n");
            }
            
            // Validate payment information
            if (cardNumberField.getText().trim().isEmpty()) {
                validationErrors.append("• Card Number is required\n");
            } else if (cardNumberField.getText().replaceAll("\\s", "").length() < 13) {
                validationErrors.append("• Please enter a valid card number\n");
            }
            if (expiryField.getText().trim().isEmpty()) {
                validationErrors.append("• Card Expiry Date is required\n");
            } else if (!expiryField.getText().matches("\\d{2}/\\d{2}")) {
                validationErrors.append("• Expiry date must be in MM/YY format\n");
            }
            if (cvvField.getText().trim().isEmpty()) {
                validationErrors.append("• CVV is required\n");
            } else if (cvvField.getText().length() < 3) {
                validationErrors.append("• CVV must be at least 3 digits\n");
            }
            if (cardNameField.getText().trim().isEmpty()) {
                validationErrors.append("• Cardholder Name is required\n");
            }
            if (billingAddressField.getText().trim().isEmpty()) {
                validationErrors.append("• Billing Address is required\n");
            }
            
            // If there are validation errors, show them
            if (validationErrors.length() > 0) {
                showAlert("Incomplete Information", 
                    "Please complete all required fields before proceeding:\n\n" + validationErrors.toString());
                return;
            }
            
            // All validation passed - create booking
            showAlert("Booking Confirmed", 
                "Your flight has been booked successfully!\n\n" +
                "Booking Reference: BK" + System.currentTimeMillis() + "\n" +
                "Flight: " + flight.getFlightNumber() + "\n" +
                "Passenger: " + firstNameField.getText() + " " + lastNameField.getText() + "\n" +
                "Email confirmation sent to: " + emailField.getText());
            showCustomerDashboard();
        });
        
        bookingForm.getChildren().addAll(formTitle, passengerSection, seatSection, paymentSection, bookButton);
        
        // Right side - Flight summary
        VBox flightSummary = new VBox(20);
        flightSummary.setPrefWidth(350);
        flightSummary.setPadding(new Insets(30));
        flightSummary.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                              "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);");
        
        Label summaryTitle = new Label("Flight Summary");
        summaryTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        // Flight details
        VBox flightDetails = new VBox(15);
        
        Label flightInfo = new Label(flight.getAirline() + " " + flight.getFlightNumber());
        flightInfo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        HBox routeInfo = new HBox(10);
        routeInfo.setAlignment(Pos.CENTER_LEFT);
        Label route = new Label(flight.getDepartureAirport() + " → " + flight.getArrivalAirport());
        route.setStyle("-fx-font-size: 16px; -fx-text-fill: #4A5568;");
        routeInfo.getChildren().add(route);
        
        HBox timeInfo = new HBox(20);
        timeInfo.setAlignment(Pos.CENTER_LEFT);
        
        VBox depInfo = new VBox(5);
        Label depLabel = new Label("Departure");
        depLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #A0AEC0;");
        Label depTime = new Label(flight.getDepartureTime().toLocalTime().toString());
        depTime.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        depInfo.getChildren().addAll(depLabel, depTime);
        
        VBox arrInfo = new VBox(5);
        Label arrLabel = new Label("Arrival");
        arrLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #A0AEC0;");
        Label arrTime = new Label(flight.getArrivalTime().toLocalTime().toString());
        arrTime.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        arrInfo.getChildren().addAll(arrLabel, arrTime);
        
        timeInfo.getChildren().addAll(depInfo, arrInfo);
        
        // Price breakdown
        VBox priceBreakdown = new VBox(10);
        priceBreakdown.setPadding(new Insets(20, 0, 0, 0));
        priceBreakdown.setStyle("-fx-border-color: #E2E8F0; -fx-border-width: 1 0 0 0;");
        
        Label priceTitle = new Label("Price Breakdown");
        priceTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        HBox basePrice = new HBox();
        basePrice.setAlignment(Pos.CENTER_LEFT);
        Label basePriceLabel = new Label("Base Fare");
        basePriceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #4A5568;");
        Region priceSpacer1 = new Region();
        HBox.setHgrow(priceSpacer1, Priority.ALWAYS);
        Label basePriceValue = new Label("$" + String.format("%.2f", flight.getBasePrice()));
        basePriceValue.setStyle("-fx-font-size: 14px; -fx-text-fill: #2D3748;");
        basePrice.getChildren().addAll(basePriceLabel, priceSpacer1, basePriceValue);
        
        HBox taxes = new HBox();
        taxes.setAlignment(Pos.CENTER_LEFT);
        Label taxesLabel = new Label("Taxes & Fees");
        taxesLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #4A5568;");
        Region priceSpacer2 = new Region();
        HBox.setHgrow(priceSpacer2, Priority.ALWAYS);
        Label taxesValue = new Label("$" + String.format("%.2f", flight.getBasePrice() * 0.15));
        taxesValue.setStyle("-fx-font-size: 14px; -fx-text-fill: #2D3748;");
        taxes.getChildren().addAll(taxesLabel, priceSpacer2, taxesValue);
        
        HBox total = new HBox();
        total.setAlignment(Pos.CENTER_LEFT);
        total.setPadding(new Insets(10, 0, 0, 0));
        total.setStyle("-fx-border-color: #E2E8F0; -fx-border-width: 1 0 0 0;");
        Label totalLabel = new Label("Total");
        totalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        Region priceSpacer3 = new Region();
        HBox.setHgrow(priceSpacer3, Priority.ALWAYS);
        Label totalValue = new Label("$" + String.format("%.2f", flight.getBasePrice() * 1.15));
        totalValue.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #667eea;");
        total.getChildren().addAll(totalLabel, priceSpacer3, totalValue);
        
        priceBreakdown.getChildren().addAll(priceTitle, basePrice, taxes, total);
        
        flightDetails.getChildren().addAll(flightInfo, routeInfo, timeInfo);
        flightSummary.getChildren().addAll(summaryTitle, flightDetails, priceBreakdown);
        
        mainContent.getChildren().addAll(bookingForm, flightSummary);
        root.setCenter(mainContent);
        
        Scene bookingScene = new Scene(root, 1200, 800);
        primaryStage.setScene(bookingScene);
        primaryStage.setTitle("Book Flight - " + flight.getFlightNumber());
    }
    
    private void showBookingDetailsScreen(Booking booking) {
        showAlert("Booking Details", "Detailed booking information screen - Implementation in progress");
    }
    
    private void handleBookingCancellation(Booking booking) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Cancel Booking");
        confirmAlert.setHeaderText("Are you sure you want to cancel this booking?");
        confirmAlert.setContentText("This action cannot be undone. Refund policies may apply.");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response.getButtonData() == javafx.scene.control.ButtonBar.ButtonData.OK_DONE) {
                boolean success = bookingService.cancelBooking(booking.getBookingId(), currentUser.getUserId());
                if (success) {
                    showAlert("Success", "Booking cancelled successfully. Refund will be processed according to our policies.");
                    // Refresh the bookings view
                    showCustomerDashboard();
                } else {
                    showAlert("Error", "Failed to cancel booking. Please contact customer support.");
                }
            }
        });
    }
    
    private void showTicketSubmissionScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background: linear-gradient(to bottom, #F7FAFC, #EDF2F7);");
        
        // Header with back button
        HBox header = new HBox(20);
        header.setPadding(new Insets(20, 30, 20, 30));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        
        Button backBtn = new Button("← Back");
        backBtn.setStyle("-fx-background-color: #E2E8F0; -fx-text-fill: #4A5568; -fx-font-size: 14px; " +
                        "-fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand; -fx-border-width: 0;");
        backBtn.setOnAction(e -> showCustomerDashboard());
        
        Label title = new Label("Submit Support Ticket");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        header.getChildren().addAll(backBtn, title);
        root.setTop(header);
        
        // Main content
        VBox content = new VBox(30);
        content.setPadding(new Insets(40));
        content.setAlignment(Pos.CENTER);
        
        // Ticket form
        VBox ticketForm = new VBox(25);
        ticketForm.setPadding(new Insets(40));
        ticketForm.setMaxWidth(600);
        ticketForm.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                           "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 20, 0, 0, 5);");
        
        Label formTitle = new Label("How can we help you?");
        formTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        Label formSubtitle = new Label("Fill out the form below and we'll get back to you as soon as possible");
        formSubtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #718096;");
        formSubtitle.setWrapText(true);
        
        // Contact information
        VBox contactSection = new VBox(15);
        
        Label contactLabel = new Label("Contact Information");
        contactLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #4A5568;");
        
        HBox nameRow = new HBox(15);
        TextField firstNameField = createStyledTextField("First Name", 200);
        firstNameField.setText(currentUser.getFirstName());
        TextField lastNameField = createStyledTextField("Last Name", 200);
        lastNameField.setText(currentUser.getLastName());
        nameRow.getChildren().addAll(firstNameField, lastNameField);
        
        TextField emailField = createStyledTextField("Email Address", 415);
        emailField.setText(currentUser.getEmail());
        
        TextField phoneField = createStyledTextField("Phone Number (Optional)", 415);
        phoneField.setText(currentUser.getPhoneNumber());
        
        contactSection.getChildren().addAll(contactLabel, nameRow, emailField, phoneField);
        
        // Ticket details
        VBox ticketDetailsSection = new VBox(15);
        
        Label ticketDetailsLabel = new Label("Ticket Details");
        ticketDetailsLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #4A5568;");
        
        // Category selection
        Label categoryLabel = new Label("Category");
        categoryLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #4A5568;");
        
        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll(
            "Booking Issues", 
            "Flight Changes", 
            "Refund Requests", 
            "Payment Problems", 
            "Special Assistance", 
            "Technical Support",
            "General Inquiry",
            "Complaint"
        );
        categoryCombo.setPromptText("Select a category");
        categoryCombo.setPrefWidth(415);
        categoryCombo.setStyle("-fx-font-size: 14px; -fx-padding: 12;");
        
        // Priority selection
        Label priorityLabel = new Label("Priority");
        priorityLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #4A5568;");
        
        ComboBox<String> priorityCombo = new ComboBox<>();
        priorityCombo.getItems().addAll("Low", "Medium", "High", "Urgent");
        priorityCombo.setValue("Medium");
        priorityCombo.setPrefWidth(415);
        priorityCombo.setStyle("-fx-font-size: 14px; -fx-padding: 12;");
        
        // Subject
        TextField subjectField = createStyledTextField("Subject", 415);
        subjectField.setPromptText("Brief description of your issue");
        
        // Message
        Label messageLabel = new Label("Message");
        messageLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #4A5568;");
        
        javafx.scene.control.TextArea messageArea = new javafx.scene.control.TextArea();
        messageArea.setPromptText("Please describe your issue in detail...");
        messageArea.setPrefRowCount(6);
        messageArea.setPrefWidth(415);
        messageArea.setStyle("-fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 8; " +
                            "-fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-radius: 8;");
        
        // Booking reference (optional)
        TextField bookingRefField = createStyledTextField("Booking Reference (Optional)", 415);
        bookingRefField.setPromptText("e.g., BK123456789");
        
        ticketDetailsSection.getChildren().addAll(
            ticketDetailsLabel, 
            categoryLabel, categoryCombo,
            priorityLabel, priorityCombo,
            subjectField, 
            messageLabel, messageArea, 
            bookingRefField
        );
        
        // Submit button
        Button submitButton = new Button("Submit Ticket");
        submitButton.setStyle("-fx-background-color: #48BB78; -fx-text-fill: white; -fx-font-size: 18px; " +
                             "-fx-font-weight: bold; -fx-padding: 15 40; -fx-background-radius: 10; " +
                             "-fx-cursor: hand; -fx-border-width: 0;");
        submitButton.setPrefWidth(415);
        
        submitButton.setOnAction(e -> {
            // Validate form
            if (categoryCombo.getValue() == null || subjectField.getText().trim().isEmpty() || 
                messageArea.getText().trim().isEmpty()) {
                showAlert("Error", "Please fill in all required fields.");
                return;
            }
            
            // Create ticket (simplified)
            String ticketId = "T" + System.currentTimeMillis();
            showAlert("Ticket Submitted", 
                "Your support ticket has been submitted successfully!\n\n" +
                "Ticket ID: " + ticketId + "\n" +
                "Priority: " + priorityCombo.getValue() + "\n" +
                "Category: " + categoryCombo.getValue() + "\n\n" +
                "We'll get back to you within 24 hours.");
            showCustomerDashboard();
        });
        
        ticketForm.getChildren().addAll(formTitle, formSubtitle, contactSection, ticketDetailsSection, submitButton);
        content.getChildren().add(ticketForm);
        
        root.setCenter(content);
        
        Scene ticketScene = new Scene(root, 1200, 800);
        primaryStage.setScene(ticketScene);
        primaryStage.setTitle("Submit Support Ticket");
    }
    
    // Admin Content Methods
    private VBox createFlightManagementContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        
        Label title = new Label("Flight Management");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        Label subtitle = new Label("Manage flight schedules and operations");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #718096;");
        
        // Add new flight button
        Button addFlightBtn = new Button("Add New Flight");
        addFlightBtn.setStyle("-fx-background-color: #48BB78; -fx-text-fill: white; -fx-font-size: 16px; " +
                             "-fx-font-weight: bold; -fx-padding: 12 24; -fx-background-radius: 8; " +
                             "-fx-cursor: hand; -fx-border-width: 0;");
        
        // Flights table placeholder
        VBox flightsTable = new VBox(15);
        flightsTable.setPadding(new Insets(20));
        flightsTable.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                             "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        
        Label tableTitle = new Label("Current Flights");
        tableTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        // Sample flight entries
        List<Flight> allFlights = flightService.getAllFlights();
        VBox flightEntries = new VBox(10);
        
        for (Flight flight : allFlights) {
            HBox flightRow = createFlightManagementRow(flight);
            flightEntries.getChildren().add(flightRow);
        }
        
        flightsTable.getChildren().addAll(tableTitle, flightEntries);
        content.getChildren().addAll(title, subtitle, addFlightBtn, flightsTable);
        return content;
    }
    
    private HBox createFlightManagementRow(Flight flight) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(15));
        row.setStyle("-fx-background-color: #F8F9FA; -fx-background-radius: 8; -fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-radius: 8;");
        
        Label flightInfo = new Label(flight.getFlightNumber() + " - " + flight.getAirline());
        flightInfo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        flightInfo.setPrefWidth(150);
        
        Label route = new Label(flight.getDepartureAirport() + " → " + flight.getArrivalAirport());
        route.setStyle("-fx-font-size: 14px; -fx-text-fill: #4A5568;");
        route.setPrefWidth(200);
        
        Label status = new Label(flight.getStatus().toString());
        String statusColor = getFlightStatusColor(flight.getStatus());
        status.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + statusColor + "; " +
                       "-fx-background-color: " + statusColor + "22; -fx-padding: 4 8; -fx-background-radius: 12;");
        status.setPrefWidth(100);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button editBtn = new Button("Edit");
        editBtn.setStyle("-fx-background-color: #4299E1; -fx-text-fill: white; -fx-padding: 6 12; " +
                        "-fx-background-radius: 6; -fx-cursor: hand; -fx-border-width: 0;");
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #E53E3E; -fx-text-fill: white; -fx-padding: 6 12; " +
                          "-fx-background-radius: 6; -fx-cursor: hand; -fx-border-width: 0;");
        
        row.getChildren().addAll(flightInfo, route, status, spacer, editBtn, deleteBtn);
        return row;
    }
    
    private VBox createAllBookingsContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        
        Label title = new Label("All Bookings");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        Label subtitle = new Label("Monitor and manage customer bookings");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #718096;");
        
        // Bookings table
        VBox bookingsTable = new VBox(15);
        bookingsTable.setPadding(new Insets(20));
        bookingsTable.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                              "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        
        Label tableTitle = new Label("Recent Bookings");
        tableTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        VBox bookingEntries = new VBox(10);
        List<Booking> allBookings = bookingService.getAllBookings();
        
        for (Booking booking : allBookings) {
            HBox bookingRow = createBookingManagementRow(booking);
            bookingEntries.getChildren().add(bookingRow);
        }
        
        bookingsTable.getChildren().addAll(tableTitle, bookingEntries);
        content.getChildren().addAll(title, subtitle, bookingsTable);
        return content;
    }
    
    private HBox createBookingManagementRow(Booking booking) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(15));
        row.setStyle("-fx-background-color: #F8F9FA; -fx-background-radius: 8; -fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-radius: 8;");
        
        Label bookingId = new Label("#" + booking.getBookingId());
        bookingId.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        bookingId.setPrefWidth(100);
        
        Label customer = new Label("Customer ID: " + booking.getCustomerId());
        customer.setStyle("-fx-font-size: 14px; -fx-text-fill: #4A5568;");
        customer.setPrefWidth(150);
        
        Label bookingDate = new Label(booking.getBookingDate().toLocalDate().toString());
        bookingDate.setStyle("-fx-font-size: 14px; -fx-text-fill: #718096;");
        bookingDate.setPrefWidth(120);
        
        Label status = new Label(booking.getStatus().toString());
        String statusColor = getStatusColor(booking.getStatus());
        status.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + statusColor + "; " +
                       "-fx-background-color: " + statusColor + "22; -fx-padding: 4 8; -fx-background-radius: 12;");
        status.setPrefWidth(100);
        
        Label totalPrice = new Label("$" + String.format("%.2f", booking.getTotalPrice()));
        totalPrice.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #667eea;");
        totalPrice.setPrefWidth(80);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button viewBtn = new Button("View");
        viewBtn.setStyle("-fx-background-color: #4299E1; -fx-text-fill: white; -fx-padding: 6 12; " +
                        "-fx-background-radius: 6; -fx-cursor: hand; -fx-border-width: 0;");
        
        row.getChildren().addAll(bookingId, customer, bookingDate, status, totalPrice, spacer, viewBtn);
        return row;
    }
    
    private VBox createCustomerManagementContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        
        Label title = new Label("Customer Management");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        Label subtitle = new Label("Manage customer accounts and information");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #718096;");
        
        // Customers table
        VBox customersTable = new VBox(15);
        customersTable.setPadding(new Insets(20));
        customersTable.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                               "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        
        Label tableTitle = new Label("Registered Customers");
        tableTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        VBox customerEntries = new VBox(10);
        List<Customer> allCustomers = userService.getAllCustomers();
        
        for (Customer customer : allCustomers) {
            HBox customerRow = createCustomerManagementRow(customer);
            customerEntries.getChildren().add(customerRow);
        }
        
        customersTable.getChildren().addAll(tableTitle, customerEntries);
        content.getChildren().addAll(title, subtitle, customersTable);
        return content;
    }
    
    private HBox createCustomerManagementRow(Customer customer) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(15));
        row.setStyle("-fx-background-color: #F8F9FA; -fx-background-radius: 8; -fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-radius: 8;");
        
        Label name = new Label(customer.getFirstName() + " " + customer.getLastName());
        name.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        name.setPrefWidth(200);
        
        Label email = new Label(customer.getEmail());
        email.setStyle("-fx-font-size: 14px; -fx-text-fill: #4A5568;");
        email.setPrefWidth(250);
        
        Label phone = new Label(customer.getPhoneNumber());
        phone.setStyle("-fx-font-size: 14px; -fx-text-fill: #718096;");
        phone.setPrefWidth(150);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button viewBtn = new Button("View Profile");
        viewBtn.setStyle("-fx-background-color: #4299E1; -fx-text-fill: white; -fx-padding: 6 12; " +
                        "-fx-background-radius: 6; -fx-cursor: hand; -fx-border-width: 0;");
        
        row.getChildren().addAll(name, email, phone, spacer, viewBtn);
        return row;
    }
    
    private VBox createAdminTicketManagementContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        
        Label title = new Label("Support Ticket Management");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        Label subtitle = new Label("Manage customer support requests");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #718096;");
        
        // Tickets table
        VBox ticketsTable = new VBox(15);
        ticketsTable.setPadding(new Insets(20));
        ticketsTable.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                             "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        
        Label tableTitle = new Label("Active Support Tickets");
        tableTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        // Sample tickets (in real implementation, fetch from TicketService)
        VBox ticketEntries = new VBox(10);
        
        // Add sample ticket rows
        HBox sampleTicket1 = createTicketManagementRow("T001", "High", "Booking Cancellation Issue", "John Doe", "2 hours ago");
        HBox sampleTicket2 = createTicketManagementRow("T002", "Medium", "Flight Information Request", "Jane Smith", "4 hours ago");
        HBox sampleTicket3 = createTicketManagementRow("T003", "Low", "General Inquiry", "Bob Wilson", "1 day ago");
        
        ticketEntries.getChildren().addAll(sampleTicket1, sampleTicket2, sampleTicket3);
        
        ticketsTable.getChildren().addAll(tableTitle, ticketEntries);
        content.getChildren().addAll(title, subtitle, ticketsTable);
        return content;
    }
    
    private HBox createTicketManagementRow(String ticketId, String priority, String subject, String customer, String timeAgo) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(15));
        row.setStyle("-fx-background-color: #F8F9FA; -fx-background-radius: 8; -fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-radius: 8;");
        
        Label ticket = new Label("#" + ticketId);
        ticket.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        ticket.setPrefWidth(80);
        
        Label priorityLabel = new Label(priority);
        String priorityColor = priority.equals("High") ? "#E53E3E" : priority.equals("Medium") ? "#ED8936" : "#48BB78";
        priorityLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + priorityColor + "; " +
                              "-fx-background-color: " + priorityColor + "22; -fx-padding: 4 8; -fx-background-radius: 12;");
        priorityLabel.setPrefWidth(80);
        
        Label subjectLabel = new Label(subject);
        subjectLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2D3748;");
        subjectLabel.setPrefWidth(250);
        
        Label customerLabel = new Label(customer);
        customerLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #4A5568;");
        customerLabel.setPrefWidth(150);
        
        Label timeLabel = new Label(timeAgo);
        timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #A0AEC0;");
        timeLabel.setPrefWidth(100);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button viewBtn = new Button("View");
        viewBtn.setStyle("-fx-background-color: #4299E1; -fx-text-fill: white; -fx-padding: 6 12; " +
                        "-fx-background-radius: 6; -fx-cursor: hand; -fx-border-width: 0;");
        
        Button replyBtn = new Button("Reply");
        replyBtn.setStyle("-fx-background-color: #48BB78; -fx-text-fill: white; -fx-padding: 6 12; " +
                         "-fx-background-radius: 6; -fx-cursor: hand; -fx-border-width: 0;");
        
        row.getChildren().addAll(ticket, priorityLabel, subjectLabel, customerLabel, timeLabel, spacer, viewBtn, replyBtn);
        return row;
    }
    
    private VBox createRefundApprovalContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        
        Label title = new Label("Refund Approvals");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        Label subtitle = new Label("Review and approve refund requests");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #718096;");
        
        // Refunds table
        VBox refundsTable = new VBox(15);
        refundsTable.setPadding(new Insets(20));
        refundsTable.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                             "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        
        Label tableTitle = new Label("Pending Refund Requests");
        tableTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        // Sample refund requests
        VBox refundEntries = new VBox(10);
        
        HBox sampleRefund1 = createRefundApprovalRow("R001", "BK2024001", "John Doe", "$450.00", "Cancelled flight", "2 hours ago");
        HBox sampleRefund2 = createRefundApprovalRow("R002", "BK2024002", "Jane Smith", "$320.00", "Personal emergency", "1 day ago");
        HBox sampleRefund3 = createRefundApprovalRow("R003", "BK2024003", "Bob Wilson", "$280.00", "Schedule change", "2 days ago");
        
        refundEntries.getChildren().addAll(sampleRefund1, sampleRefund2, sampleRefund3);
        
        refundsTable.getChildren().addAll(tableTitle, refundEntries);
        content.getChildren().addAll(title, subtitle, refundsTable);
        return content;
    }
    
    private HBox createRefundApprovalRow(String refundId, String bookingId, String customer, String amount, String reason, String timeAgo) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(15));
        row.setStyle("-fx-background-color: #F8F9FA; -fx-background-radius: 8; -fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-radius: 8;");
        
        Label refund = new Label("#" + refundId);
        refund.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        refund.setPrefWidth(80);
        
        Label booking = new Label(bookingId);
        booking.setStyle("-fx-font-size: 14px; -fx-text-fill: #4A5568;");
        booking.setPrefWidth(100);
        
        Label customerLabel = new Label(customer);
        customerLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2D3748;");
        customerLabel.setPrefWidth(150);
        
        Label amountLabel = new Label(amount);
        amountLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #667eea;");
        amountLabel.setPrefWidth(100);
        
        Label reasonLabel = new Label(reason);
        reasonLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #718096;");
        reasonLabel.setPrefWidth(150);
        
        Label timeLabel = new Label(timeAgo);
        timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #A0AEC0;");
        timeLabel.setPrefWidth(100);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button approveBtn = new Button("Approve");
        approveBtn.setStyle("-fx-background-color: #48BB78; -fx-text-fill: white; -fx-padding: 6 12; " +
                           "-fx-background-radius: 6; -fx-cursor: hand; -fx-border-width: 0;");
        
        Button rejectBtn = new Button("Reject");
        rejectBtn.setStyle("-fx-background-color: #E53E3E; -fx-text-fill: white; -fx-padding: 6 12; " +
                          "-fx-background-radius: 6; -fx-cursor: hand; -fx-border-width: 0;");
        
        row.getChildren().addAll(refund, booking, customerLabel, amountLabel, reasonLabel, timeLabel, spacer, approveBtn, rejectBtn);
        return row;
    }
    
    private VBox createReportsContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        
        Label title = new Label("Reports & Analytics");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        Label subtitle = new Label("Business intelligence and performance metrics");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #718096;");
        
        // Reports grid
        HBox reportsRow1 = new HBox(20);
        HBox reportsRow2 = new HBox(20);
        
        VBox bookingReport = createReportCard("📊", "Booking Analytics", "Monthly booking trends and revenue analysis", "#4299E1");
        VBox flightReport = createReportCard("✈️", "Flight Performance", "On-time performance and capacity utilization", "#48BB78");
        VBox customerReport = createReportCard("👥", "Customer Insights", "Customer demographics and satisfaction", "#9F7AEA");
        VBox revenueReport = createReportCard("💰", "Revenue Analysis", "Financial performance and profitability", "#ED8936");
        
        reportsRow1.getChildren().addAll(bookingReport, flightReport);
        reportsRow2.getChildren().addAll(customerReport, revenueReport);
        
        content.getChildren().addAll(title, subtitle, reportsRow1, reportsRow2);
        return content;
    }
    
    private VBox createReportCard(String icon, String title, String description, String color) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setPrefWidth(300);
        card.setPrefHeight(200);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3); " +
                     "-fx-cursor: hand;");
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 32px;");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        
        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #718096;");
        descLabel.setWrapText(true);
        
        Button generateBtn = new Button("Generate Report");
        generateBtn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 14px; " +
                            "-fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand; -fx-border-width: 0;");
        
        card.getChildren().addAll(iconLabel, titleLabel, descLabel, generateBtn);
        return card;
    }
    
    /**
     * Modern Sidebar Button
     */
    private Button createSidebarButton(String icon, String text, String accentColor) {
        Button button = new Button();
        HBox content = new HBox(12);
        content.setAlignment(Pos.CENTER_LEFT);
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 16px;");
        Label textLabel = new Label(text);
        textLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + GRAY_800 + ";");
        content.getChildren().addAll(iconLabel, textLabel);
        button.setGraphic(content);
        button.setPrefWidth(220);
        button.setPrefHeight(44);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-padding: 8 16; -fx-background-radius: 10; -fx-cursor: hand;");
        // Hover effects
        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: " + accentColor + "22; -fx-border-width: 0; -fx-padding: 8 16; -fx-background-radius: 10; -fx-cursor: hand;");
            textLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + accentColor + ";");
        });
        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-padding: 8 16; -fx-background-radius: 10; -fx-cursor: hand;");
            textLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + GRAY_800 + ";");
        });
        return button;
    }

    /**
     * Switches the main content area in the dashboard
     */
    private void switchContent(BorderPane root, VBox newContent) {
        // Wrap content in ScrollPane for better responsiveness
        ScrollPane scrollPane = new ScrollPane(newContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: " + CREAM_50 + "; -fx-background: " + CREAM_50 + ";");
        
        // Create responsive container
        StackPane contentContainer = new StackPane();
        contentContainer.setPadding(new Insets(20));
        contentContainer.setStyle("-fx-background-color: " + CREAM_50 + ";");
        HBox.setHgrow(contentContainer, Priority.ALWAYS);
        contentContainer.getChildren().add(scrollPane);
        
        root.setCenter(contentContainer);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}