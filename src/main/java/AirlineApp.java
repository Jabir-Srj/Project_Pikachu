import ai.AirlineAIService;
import javafx.application.Application;
import javafx.stage.Stage;
import service.BookingService;
import service.FlightService;
import service.TicketService;
import service.UserService;
import util.DataManager;
import util.NavigationManager;
import util.ServiceLocator;

/**
 * Pikachu Airlines - Premium Customer Service Application
 * This is the main application class that launches the JavaFX application
 * and manages the application lifecycle. All UI is defined in FXML files.
 */
public class AirlineApp extends Application {
    
    private UserService userService;
    private FlightService flightService;
    private BookingService bookingService;
    private TicketService ticketService;
    private DataManager dataManager;
    private AirlineAIService aiService;
    
    @Override
    public void start(Stage primaryStage) {
        // Initialize services
        initializeServices();
        
        // Set up the primary stage
        primaryStage.setTitle("Pikachu Airlines - Premium Travel Experience");
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(800);
        primaryStage.setWidth(1400);
        primaryStage.setHeight(900);
        primaryStage.centerOnScreen();
        
        // Initialize navigation manager
        NavigationManager navigationManager = NavigationManager.getInstance();
        navigationManager.setPrimaryStage(primaryStage);
        
        // Start with login screen
        navigationManager.navigateTo(NavigationManager.LOGIN_SCREEN);
    }
    
    /**
     * Initialize all application services
     */
    private void initializeServices() {
        // Initialize data manager
        dataManager = new DataManager();
        
        // Initialize services
        userService = new UserService();
        flightService = new FlightService();
        bookingService = new BookingService();
        ticketService = new TicketService();
        
        // Register services with ServiceLocator
        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        serviceLocator.setUserService(userService);
        serviceLocator.setFlightService(flightService);
        serviceLocator.setBookingService(bookingService);
        serviceLocator.setTicketService(ticketService);
        serviceLocator.setDataManager(dataManager);
        
        // Load sample data
        dataManager.loadSampleData();
        
        // Initialize AI service
        aiService = AirlineAIService.getInstance();
        serviceLocator.setAiService(aiService);
        aiService.initialize().thenAccept(success -> {
            if (success) {
                System.out.println("AI Service initialized successfully!");
            } else {
                System.err.println("AI Service initialization failed!");
            }
        });
    }
    

    
    @Override
    public void stop() {
        // Clean up resources when application closes
        if (aiService != null) {
            // Any cleanup needed for AI service
        }
        System.out.println("Application shutting down...");
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}