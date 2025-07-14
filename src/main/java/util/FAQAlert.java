package util;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import model.User;

/**
 * Utility class for showing FAQ information in an alert dialog
 */
public class FAQAlert {
    
    private static final String[] FAQ_CATEGORIES = {
        "📋 Booking & Reservations",
        "✈️ Flight Information", 
        "💳 Payment & Billing",
        "🎧 Customer Support",
        "👤 Account Management"
    };
    
    private static final String[][] FAQ_DATA = {
        // Booking FAQs
        {
            "Q: How do I book a flight?",
            "A: To book a flight, go to the 'Search Flights' section on your dashboard, enter your departure and destination cities, select your travel date, choose the number of passengers, and click 'Search'. Browse the available flights and click 'Select Flight' on your preferred option to proceed with booking.",
            
            "Q: Can I cancel or modify my booking?",
            "A: Yes, you can view and manage your bookings by going to 'My Bookings' on your dashboard. From there, you can view booking details, request modifications, or cancel your reservation. Please note that cancellation and modification policies may apply depending on your ticket type.",
            
            "Q: How far in advance can I book a flight?",
            "A: You can book flights up to 330 days in advance. We recommend booking early to secure the best prices and seat availability, especially during peak travel seasons.",
            
            "Q: What information do I need to complete a booking?",
            "A: You'll need passenger details (full name as on ID, date of birth, contact information), payment information, and any special requirements such as meal preferences or accessibility needs."
        },
        // Flight FAQs
        {
            "Q: How do I check flight status?",
            "A: You can check your flight status in the 'My Bookings' section or by searching for flights in the 'Search Flights' section. Flight status updates include on-time, delayed, or cancelled information.",
            
            "Q: What's included in the ticket price?",
            "A: The base ticket price includes your seat, carry-on baggage, and basic in-flight service. Additional services like meals, extra baggage, and seat selection may be available for an additional fee.",
            
            "Q: What are the different class options?",
            "A: We offer Economy, Business, and First Class options. Economy provides comfortable seating and basic amenities, Business Class offers enhanced comfort and premium services, while First Class provides luxury amenities and personalized service.",
            
            "Q: How early should I arrive at the airport?",
            "A: For domestic flights, arrive at least 2 hours before departure. For international flights, arrive at least 3 hours before departure to allow time for check-in, security screening, and any additional procedures."
        },
        // Payment FAQs
        {
            "Q: What payment methods do you accept?",
            "A: We accept major credit cards (Visa, MasterCard, American Express), debit cards, and digital wallets. Payment is processed securely through our encrypted payment system.",
            
            "Q: When will my payment be charged?",
            "A: Your payment is charged immediately upon completion of your booking. You'll receive a confirmation email with your booking details and receipt.",
            
            "Q: Can I get a refund for my ticket?",
            "A: Refund eligibility depends on your ticket type and the timing of your cancellation. You can request a refund through the 'My Bookings' section. Refundable tickets allow full refunds, while non-refundable tickets may have restrictions.",
            
            "Q: How long do refunds take to process?",
            "A: Refunds typically take 5-7 business days to appear on your original payment method. The exact timing may vary depending on your bank or payment provider."
        },
        // Support FAQs
        {
            "Q: How can I contact customer support?",
            "A: You can contact support by submitting a ticket through the 'Support Tickets' section on your dashboard, using our AI Assistant for immediate help, or viewing your submitted tickets in 'View My Tickets'.",
            
            "Q: What information should I include in a support ticket?",
            "A: Include your booking reference number, a clear description of your issue, any relevant dates, and your contact information. The more details you provide, the faster we can assist you.",
            
            "Q: How quickly will I receive a response to my support ticket?",
            "A: We aim to respond to all support tickets within 24 hours. Urgent matters are prioritized and may receive faster responses. You can track the status of your tickets in the 'View My Tickets' section.",
            
            "Q: What can the AI Assistant help me with?",
            "A: Our AI Assistant can help with booking information, flight status queries, general questions about policies, and guide you through common procedures. For complex issues, it can direct you to submit a support ticket."
        },
        // Account FAQs
        {
            "Q: How do I update my account information?",
            "A: Click on the 'Profile' button in the top right corner of your dashboard to access your account settings. You can update your personal information, contact details, and preferences from there.",
            
            "Q: How do I change my password?",
            "A: Go to your Profile settings and look for the password change option. You'll need to enter your current password and choose a new secure password. Make sure to use a strong password with a mix of letters, numbers, and symbols.",
            
            "Q: What if I forget my login credentials?",
            "A: Use the 'Forgot Password' link on the login page to reset your password. If you've forgotten your username, contact customer support with your account details for assistance.",
            
            "Q: Can I delete my account?",
            "A: Yes, you can request account deletion by contacting customer support. Please note that this action is permanent and will remove all your booking history and account data."
        }
    };
    
    /**
     * Show FAQ dialog
     */
    public static void show(User currentUser) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Frequently Asked Questions");
        dialog.setHeaderText("Pikachu Airlines - Customer Support");
        
        // Create custom dialog pane
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add("/css/application.css");
        dialogPane.getStyleClass().add("content-card");
        
        // Set up the main content
        VBox mainContent = createMainContent(currentUser, dialog);
        
        // Wrap in scroll pane for better handling
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("faq-scroll-pane");
        scrollPane.setPrefSize(800, 600);
        
        dialogPane.setContent(scrollPane);
        
        // Add close button
        ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButtonType);
        
        // Style the close button
        Button closeButton = (Button) dialog.getDialogPane().lookupButton(closeButtonType);
        if (closeButton != null) {
            closeButton.getStyleClass().add("button-primary");
        }
        
        // Make dialog resizable
        dialog.setResizable(true);
        
        // Show dialog
        dialog.showAndWait();
    }
    
    /**
     * Create the main content for the FAQ dialog
     */
    private static VBox createMainContent(User currentUser, Dialog<?> dialog) {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.getStyleClass().add("background-gray-light");
        
        // Header section
        VBox headerSection = createHeaderSection();
        mainContent.getChildren().add(headerSection);
        
        // FAQ sections
        for (int i = 0; i < FAQ_CATEGORIES.length; i++) {
            VBox faqSection = createFAQSection(FAQ_CATEGORIES[i], FAQ_DATA[i]);
            mainContent.getChildren().add(faqSection);
        }
        
        // Help section
        VBox helpSection = createHelpSection(currentUser, dialog);
        mainContent.getChildren().add(helpSection);
        
        return mainContent;
    }
    
    /**
     * Create header section
     */
    private static VBox createHeaderSection() {
        VBox header = new VBox(15);
        header.getStyleClass().add("content-card");
        header.setPadding(new Insets(20));
        
        Text title = new Text("🤔 Frequently Asked Questions");
        title.getStyleClass().add("section-title");
        title.setFont(Font.font("System Bold", 24));
        
        Text subtitle = new Text("Find quick answers to common questions about Pikachu Airlines services.");
        subtitle.getStyleClass().add("text-muted");
        
        header.getChildren().addAll(title, subtitle);
        return header;
    }
    
    /**
     * Create individual FAQ section
     */
    private static VBox createFAQSection(String categoryTitle, String[] faqs) {
        VBox section = new VBox(15);
        section.getStyleClass().add("content-card");
        section.setPadding(new Insets(20));
        
        Text title = new Text(categoryTitle);
        title.getStyleClass().add("section-title");
        title.setFont(Font.font("System Bold", 20));
        
        section.getChildren().add(title);
        
        // Add FAQ items
        for (int i = 0; i < faqs.length; i += 2) {
            if (i + 1 < faqs.length) {
                VBox faqItem = createFAQItem(faqs[i], faqs[i + 1]);
                section.getChildren().add(faqItem);
            }
        }
        
        return section;
    }
    
    /**
     * Create individual FAQ item
     */
    private static VBox createFAQItem(String question, String answer) {
        VBox faqItem = new VBox(10);
        faqItem.getStyleClass().add("faq-item");
        
        Text questionText = new Text(question);
        questionText.getStyleClass().add("faq-question");
        questionText.setFont(Font.font("System Bold", 16));
        
        Text answerText = new Text(answer);
        answerText.getStyleClass().add("faq-answer");
        answerText.setWrappingWidth(700); // Allow text wrapping
        
        faqItem.getChildren().addAll(questionText, answerText);
        return faqItem;
    }
    
    /**
     * Create help section
     */
    private static VBox createHelpSection(User currentUser, Dialog<?> dialog) {
        VBox helpSection = new VBox(15);
        helpSection.getStyleClass().addAll("content-card", "help-section");
        helpSection.setPadding(new Insets(20));
        
        Text title = new Text("🤝 Still Need Help?");
        title.getStyleClass().add("section-title");
        title.setFont(Font.font("System Bold", 20));
        
        Text description = new Text("Can't find what you're looking for? We're here to help!");
        description.getStyleClass().add("help-description");
        
        HBox buttonBox = new HBox(15);
        
        Button submitTicketBtn = new Button("📝 Submit Ticket");
        submitTicketBtn.getStyleClass().add("button-primary");
        submitTicketBtn.setOnAction(_ -> {
            NavigationManager.getInstance().showTicketSubmission();
            dialog.close();
        });
        
        Button aiChatBtn = new Button("🤖 AI Assistant");
        aiChatBtn.getStyleClass().add("button-secondary");
        aiChatBtn.setOnAction(_ -> {
            NavigationManager.getInstance().showAIChatbot();
            dialog.close();
        });
        
        Button viewTicketsBtn = new Button("📋 My Tickets");
        viewTicketsBtn.getStyleClass().add("button-accent");
        viewTicketsBtn.setOnAction(_ -> {
            NavigationManager.getInstance().showTicketManagement();
            dialog.close();
        });
        
        buttonBox.getChildren().addAll(submitTicketBtn, aiChatBtn, viewTicketsBtn);
        
        helpSection.getChildren().addAll(title, description, buttonBox);
        return helpSection;
    }
    
}
