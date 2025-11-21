import javafx.application.Application;
import javafx.stage.Stage;
import service.*;
import util.DataManager;
import util.NavigationManager;
import util.ServiceLocator;
import util.ScholarlyServiceLocator;

/**
 * Scholarly Publishing Platform - Main Application
 * A peer-reviewed, discoverable scholarly publishing platform
 * inspired by Stanford Encyclopedia of Philosophy
 */
public class ScholarlyApp extends Application {
    
    private UserService userService;
    private ArticleService articleService;
    private CommentService commentService;
    private AuthorProfileService authorProfileService;
    private DataManager dataManager;
    
    @Override
    public void start(Stage primaryStage) {
        // Initialize services
        initializeServices();
        
        // Set up the primary stage
        primaryStage.setTitle("پژوتێ - Scholarly Publishing Platform");
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(700);
        primaryStage.setWidth(1400);
        primaryStage.setHeight(850);
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
        // Initialize data manager (for backward compatibility)
        dataManager = new DataManager();
        
        // Initialize legacy services
        userService = new UserService();
        
        // Initialize scholarly services
        articleService = new ArticleService();
        commentService = new CommentService();
        authorProfileService = new AuthorProfileService();
        
        // Register with legacy service locator
        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        serviceLocator.setUserService(userService);
        serviceLocator.setDataManager(dataManager);
        
        // Register with scholarly service locator
        ScholarlyServiceLocator scholarlyLocator = ScholarlyServiceLocator.getInstance();
        scholarlyLocator.setArticleService(articleService);
        scholarlyLocator.setCommentService(commentService);
        scholarlyLocator.setAuthorProfileService(authorProfileService);
        scholarlyLocator.setUserService(userService);
        
        // Load sample data
        dataManager.loadSampleData();
        loadScholarlyData();
    }
    
    /**
     * Load sample scholarly data
     */
    private void loadScholarlyData() {
        // This will be populated with sample articles and profiles
        System.out.println("Scholarly publishing platform initialized");
    }
    
    @Override
    public void stop() {
        // Clean up resources when application closes
        System.out.println("Scholarly platform shutting down...");
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
