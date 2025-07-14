# Pikachu Airlines - AI Coding Instructions

## Project Overview
Pikachu Airlines is a comprehensive JavaFX desktop application for airline customer service management. This is a service-oriented architecture application built with Java 23+ featuring a modern UI, custom JSON persistence layer, and AI-powered customer support capabilities.

## Architecture & Design Patterns

### Core Architecture
- **Pattern**: Model-View-Controller (MVC) with Service Layer Architecture
- **Technology Stack**: JavaFX 23.0.2, Java 23+, Maven, LangChain4j
- **Data Persistence**: Custom JSON-based file system (no external database)
- **Dependency Injection**: Custom ServiceLocator pattern
- **Navigation**: Centralized NavigationManager for screen transitions

### Key Architectural Components

#### 1. ServiceLocator Pattern (`util/ServiceLocator.java`)
```java
// Singleton service registry - always use getInstance()
ServiceLocator serviceLocator = ServiceLocator.getInstance();
UserService userService = serviceLocator.getUserService();
```
- **Purpose**: Central dependency injection registry
- **Usage**: Controllers retrieve services via ServiceLocator.getInstance()
- **Services**: UserService, FlightService, BookingService, TicketService, DataManager

#### 2. NavigationManager (`util/NavigationManager.java`)
```java
// Centralized navigation with shared data
NavigationManager.getInstance().navigateTo("CustomerDashboard.fxml");
NavigationManager.getInstance().setSharedData("currentUser", user);
User currentUser = (User) NavigationManager.getInstance().getSharedData("currentUser");
```
- **Purpose**: Manages all FXML screen transitions and inter-screen data sharing
- **Usage**: Controllers must use NavigationManager for all navigation
- **Data Sharing**: Use setSharedData()/getSharedData() for passing data between screens

#### 3. Custom JSON Persistence (`util/DataManager.java`)
```java
// Custom JSON persistence - NO external database
List<Flight> flights = dataManager.loadFlights();
boolean success = dataManager.saveFlights(flights);
```
- **Critical**: Uses custom JSON parsing (1500+ lines) - NOT Jackson/Gson
- **Files**: flights.json, bookings.json, users.json, tickets.json
- **Parsing**: Custom extractJsonValue() methods for field extraction
- **Thread Safety**: File-based operations with error handling

### Service Layer Architecture

#### Service Classes Pattern
All services follow this initialization pattern:
```java
public class XxxService {
    private XxxDAO dao;
    private OtherService otherService; // Dependencies
    
    public XxxService() {
        this.dao = new XxxDAO();
        this.otherService = new OtherService(); // Direct instantiation
    }
}
```

#### DAO Layer Pattern
All DAOs follow this structure:
```java
public class XxxDAO {
    private DataManager dataManager;
    
    public XxxDAO() {
        this.dataManager = new DataManager();
    }
    
    // Standard CRUD operations
    public boolean save(Xxx entity) { /* Add to list, save file */ }
    public boolean update(Xxx entity) { /* Find by ID, replace, save */ }
    public boolean delete(String id) { /* Remove from list, save */ }
    public Optional<Xxx> findById(String id) { /* Stream filter */ }
    public List<Xxx> findAll() { /* Load from file */ }
}
```

## Controller Conventions

### Standard Controller Structure
```java
public class XxxController implements Initializable {
    // FXML Controls
    @FXML private Button backButton;
    @FXML private TableView<Entity> tableView;
    
    // Services (retrieved via ServiceLocator)
    private UserService userService;
    private FlightService flightService;
    
    // Navigation
    private NavigationManager navigationManager;
    
    // Current user context
    private User currentUser;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Get current user from NavigationManager
        currentUser = (User) NavigationManager.getInstance().getSharedData("currentUser");
        
        // 2. Initialize services via ServiceLocator
        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        userService = serviceLocator.getUserService();
        flightService = serviceLocator.getFlightService();
        
        // 3. Initialize NavigationManager
        navigationManager = NavigationManager.getInstance();
        
        // 4. Setup UI components
        setupEventHandlers();
        loadData();
        setupTableColumns(); // For TableView controllers
    }
    
    private void setupEventHandlers() {
        if (backButton != null) {
            backButton.setOnAction(_ -> handleBackToDashboard());
        }
    }
    
    @FXML
    private void handleBackToDashboard() {
        navigationManager.navigateTo("CustomerDashboard.fxml");
    }
}
```

### Role-Based Access Control
```java
// Check user roles for UI/feature access
if (currentUser instanceof Admin || currentUser instanceof AirlineManagement) {
    // Admin-only features
    adminPanel.setVisible(true);
} else {
    // Customer features only
    adminPanel.setVisible(false);
}
```

## AI Integration (LangChain4j)

### AI Service Pattern (`ai/AirlineAIService.java`)
```java
// Singleton AI service with graceful degradation
AirlineAIService aiService = AirlineAIService.getInstance();
if (aiService.isReady()) {
    aiService.sendMessage(message, responseArea, onCompleteCallback);
} else {
    // Fallback to customer service contact
    showErrorMessage("AI Assistant is unavailable. Contact 1-800-PIKACHU.");
}
```

### AI Controller Integration
```java
// Dynamic AI service loading in controllers
try {
    Class<?> aiServiceClass = Class.forName("ai.AirlineAIService");
    aiService = aiServiceClass.getMethod("getInstance").invoke(null);
} catch (Exception e) {
    System.out.println("AI Service not available: " + e.getMessage());
    aiService = null;
}
```

## Data Models & Relationships

### User Hierarchy
```java
User (abstract)
├── Customer (bookings, tickets)
├── Admin (user management, system oversight)
└── AirlineManagement (flight operations, advanced booking management)
```

### Core Entities
- **Flight**: Routes, scheduling, capacity management
- **Booking**: Customer reservations with passengers and payment
- **Ticket**: Customer support tickets with status tracking
- **User**: Authentication and role-based access

### Entity Relationships
- Customer → Many Bookings → Many Passengers
- Flight → Many Bookings (capacity management)
- Customer → Many Tickets (support history)

## File Organization

### Key Directories
```
src/main/java/
├── AirlineApp.java              # Main application entry point
├── controller/                  # FXML controllers (MVC View layer)
├── model/                       # Data entities and enums
├── service/                     # Business logic layer
├── dao/                         # Data access objects
├── util/                        # ServiceLocator, NavigationManager, DataManager
└── ai/                          # LangChain4j AI integration (optional)

src/main/resources/
└── fxml/                        # JavaFX FXML files
```

## Development Guidelines

### Adding New Features

#### 1. New Entity
```bash
# Create model class
model/NewEntity.java

# Create DAO with standard CRUD
dao/NewEntityDAO.java

# Create service layer
service/NewEntityService.java

# Register in ServiceLocator
util/ServiceLocator.java (add getter/setter)

# Update DataManager for persistence
util/DataManager.java (add load/save methods)
```

#### 2. New Screen
```bash
# Create FXML file
src/main/resources/fxml/NewScreen.fxml

# Create controller following standard pattern
controller/NewScreenController.java

# Add navigation calls to existing controllers
navigationManager.navigateTo("NewScreen.fxml");
```

### Code Quality Standards

#### Error Handling
```java
// Always wrap DAO operations in try-catch
try {
    List<Entity> entities = dataManager.loadEntities();
    // Process entities
} catch (Exception e) {
    System.err.println("Error loading entities: " + e.getMessage());
    return new ArrayList<>(); // Return empty list, not null
}
```

#### Logging Convention
```java
// Use System.out for debug info, System.err for errors
System.out.println("ServiceName: Operation successful for ID: " + id);
System.err.println("ServiceName: Error in operation: " + e.getMessage());
```

#### Thread Safety (JavaFX)
```java
// Always use Platform.runLater for UI updates from background threads
Platform.runLater(() -> {
    statusLabel.setText("Operation completed");
    progressIndicator.setVisible(false);
});
```

### Testing Patterns

#### Service Testing
```java
// Services can be tested independently
@Test
public void testBookingCreation() {
    BookingService bookingService = new BookingService();
    Booking booking = bookingService.createBooking(customer, flight, passengers, payment);
    assertNotNull(booking);
    assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
}
```

#### Mock Data
- Use DataManager.loadSampleData() for development
- Sample data includes flights, users, bookings for testing

## Common Patterns & Anti-Patterns

### ✅ Do This
```java
// Use ServiceLocator for dependency injection
ServiceLocator serviceLocator = ServiceLocator.getInstance();
UserService userService = serviceLocator.getUserService();

// Use NavigationManager for all screen transitions
NavigationManager.getInstance().navigateTo("TargetScreen.fxml");

// Check for null/empty before operations
if (customerId != null && !customerId.trim().isEmpty()) {
    List<Booking> bookings = bookingService.getCustomerBookings(customerId);
}

// Graceful degradation for optional features (AI)
if (aiService != null && aiService.isReady()) {
    // Use AI features
} else {
    // Fallback behavior
}
```

### ❌ Avoid This
```java
// Don't create services directly in controllers
BookingService bookingService = new BookingService(); // Wrong!

// Don't use direct FXML loading for navigation
FXMLLoader.load(getClass().getResource("Screen.fxml")); // Wrong!

// Don't return null from collections
return null; // Return empty list instead

// Don't ignore exceptions in DAO operations
dataManager.saveData(data); // Add try-catch!
```

## Performance Considerations

### Data Loading
- JSON files loaded entirely into memory
- Use caching in controllers for repeated data access
- Implement pagination for large datasets in TableViews

### UI Responsiveness
- Use background tasks for file I/O operations
- Show progress indicators for long-running operations
- Implement proper loading states

## Security Notes

### Authentication
- Password comparison uses simple string equality (development only)
- Role-based access implemented in controllers
- Session management via NavigationManager shared data

### Data Privacy
- Customer data filtered based on user roles
- Admin features hidden from customer users
- Booking/ticket access restricted to owners

## Build & Deployment

### Maven Configuration
- Java 23 target compilation
- JavaFX 23.0.2 dependencies
- LangChain4j optional dependencies (graceful degradation)

### Running Application
```bash
# Development mode
mvn clean javafx:run

# Package application
mvn clean package
```

This application follows enterprise patterns while maintaining simplicity through custom implementations. Always test role-based access, verify JSON persistence operations, and ensure proper error handling in all new code.
