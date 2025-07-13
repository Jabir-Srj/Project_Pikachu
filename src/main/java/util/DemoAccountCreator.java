package util;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Utility application to create demo accounts for the airline system
 */
public class DemoAccountCreator extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        // Create demo accounts
        DataManager dataManager = new DataManager();
        dataManager.createDemoAccounts();
        
        System.out.println("\n=== DEMO ACCOUNTS CREATED ===");
        System.out.println("Admin Account:");
        System.out.println("  Username: admin");
        System.out.println("  Password: 123456");
        System.out.println("  Email: admin@pikachu-airlines.com");
        System.out.println();
        System.out.println("Customer Account:");
        System.out.println("  Username: customer");
        System.out.println("  Password: 123456");
        System.out.println("  Email: customer@demo.com");
        System.out.println();
        System.out.println("Additional Accounts:");
        System.out.println("  support.admin / 123456 (Admin)");
        System.out.println("  john.doe / 123456 (Customer)");
        System.out.println("  jane.smith / 123456 (Customer)");
        System.out.println("===============================\n");
        
        // Close the application after creating accounts
        primaryStage.close();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
