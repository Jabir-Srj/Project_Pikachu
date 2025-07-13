package controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.User;
import util.NavigationManager;

/**
 * Controller for AIChatbot.fxml
 * Manages AI chatbot interactions and customer support
 */
public class ChatbotController implements Initializable {
    
    // Header Controls
    @FXML private Button backButton;
    @FXML private Button clearChatButton;
    @FXML private Button logoutButton;
    
    // Chat Area
    @FXML private ScrollPane chatScrollPane;
    @FXML private VBox chatContainer;
    
    // Quick Action Buttons
    @FXML private Button flightStatusButton;
    @FXML private Button bookingHelpButton;
    @FXML private Button baggageInfoButton;
    @FXML private Button checkInButton;
    
    // Message Input
    @FXML private TextArea messageTextArea;
    @FXML private Button sendButton;
    @FXML private Button voiceButton;
    @FXML private ProgressIndicator typingIndicator;
    
    // Suggested Responses
    @FXML private VBox suggestedResponsesContainer;
    @FXML private Button suggestion1Button;
    @FXML private Button suggestion2Button;
    @FXML private Button suggestion3Button;
    @FXML private Button suggestion4Button;
    
    // Services and Data
    private Object aiService;
    private User currentUser;
    private boolean isAIInitialized = false;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentUser = (User) NavigationManager.getInstance().getSharedData("currentUser");
        
        // Try to initialize AI service if available
        try {
            Class<?> aiServiceClass = Class.forName("ai.AirlineAIService");
            aiService = aiServiceClass.getMethod("getInstance").invoke(null);
        } catch (Exception e) {
            System.out.println("AI Service not available: " + e.getMessage());
            aiService = null;
        }
        
        setupEventHandlers();
        setupKeyboardShortcuts();
        initializeAIService();
        
        // Auto-scroll to bottom of chat
        chatScrollPane.vvalueProperty().bind(chatContainer.heightProperty());
    }
    
    /**
     * Setup event handlers for UI controls
     */
    private void setupEventHandlers() {
        // Header buttons
        if (backButton != null) {
            backButton.setOnAction(_ -> handleBackToDashboard());
        }
        if (clearChatButton != null) {
            clearChatButton.setOnAction(_ -> handleClearChat());
        }
        if (logoutButton != null) {
            logoutButton.setOnAction(_ -> handleLogout());
        }
        
        // Quick action buttons
        if (flightStatusButton != null) {
            flightStatusButton.setOnAction(_ -> handleQuickAction("What is my flight status?"));
        }
        if (bookingHelpButton != null) {
            bookingHelpButton.setOnAction(_ -> handleQuickAction("I need help with my booking"));
        }
        if (baggageInfoButton != null) {
            baggageInfoButton.setOnAction(_ -> handleQuickAction("What are your baggage policies?"));
        }
        if (checkInButton != null) {
            checkInButton.setOnAction(_ -> handleQuickAction("How do I check in for my flight?"));
        }
        
        // Message input
        if (sendButton != null) {
            sendButton.setOnAction(_ -> handleSendMessage());
        }
        if (voiceButton != null) {
            voiceButton.setOnAction(_ -> handleVoiceInput());
        }
        
        // Suggested responses
        if (suggestion1Button != null) {
            suggestion1Button.setOnAction(_ -> handleQuickAction("What are your baggage allowances?"));
        }
        if (suggestion2Button != null) {
            suggestion2Button.setOnAction(_ -> handleQuickAction("How do I check in online?"));
        }
        if (suggestion3Button != null) {
            suggestion3Button.setOnAction(_ -> handleQuickAction("Can I change my flight?"));
        }
        if (suggestion4Button != null) {
            suggestion4Button.setOnAction(_ -> handleQuickAction("Flight delay compensation?"));
        }
    }
    
    /**
     * Setup keyboard shortcuts
     */
    private void setupKeyboardShortcuts() {
        if (messageTextArea != null) {
            messageTextArea.setOnKeyPressed(event -> {
                if (event.getCode().toString().equals("ENTER")) {
                    if (event.isControlDown()) {
                        // Ctrl+Enter for new line
                        messageTextArea.appendText("\n");
                    } else {
                        // Enter to send message
                        event.consume();
                        handleSendMessage();
                    }
                }
            });
        }
    }
    
    /**
     * Initialize AI service
     */
    private void initializeAIService() {
        showTypingIndicator(true);
        addSystemMessage("🤖 Initializing AI Assistant...");
        
        if (aiService == null) {
            Platform.runLater(() -> {
                showTypingIndicator(false);
                isAIInitialized = false;
                addSystemMessage("❌ AI Assistant is currently unavailable. Please contact customer service at 1-800-PIKACHU.");
            });
            return;
        }
        
        try {
            Object result = aiService.getClass().getMethod("initialize").invoke(aiService);
            if (result instanceof java.util.concurrent.CompletableFuture<?>) {
                @SuppressWarnings("unchecked")
                java.util.concurrent.CompletableFuture<Boolean> initFuture = 
                    (java.util.concurrent.CompletableFuture<Boolean>) result;
                
                initFuture.thenAccept(success -> {
                    Platform.runLater(() -> {
                        showTypingIndicator(false);
                        isAIInitialized = success;
                        
                        if (success) {
                            String welcomeMessage = "✅ AI Assistant is ready! How can I help you today?";
                            if (currentUser != null && currentUser.getUsername() != null) {
                                welcomeMessage = "✅ Hello " + currentUser.getUsername() + "! AI Assistant is ready! How can I help you today?";
                            }
                            addSystemMessage(welcomeMessage);
                        } else {
                            addSystemMessage("❌ AI Assistant is currently unavailable. Please contact customer service at 1-800-PIKACHU.");
                        }
                    });
                });
            }
        } catch (Exception e) {
            Platform.runLater(() -> {
                showTypingIndicator(false);
                isAIInitialized = false;
                addSystemMessage("❌ AI Assistant is currently unavailable. Please contact customer service at 1-800-PIKACHU.");
            });
        }
    }
    
    /**
     * Handle sending a message
     */
    @FXML
    private void handleSendMessage() {
        String message = messageTextArea.getText().trim();
        if (message.isEmpty()) {
            return;
        }
        
        // Clear input and add user message
        messageTextArea.clear();
        addUserMessage(message);
        
        // Check if AI is available
        if (!isAIInitialized) {
            addSystemMessage("❌ AI Assistant is not available. Please contact customer service at 1-800-PIKACHU.");
            return;
        }
        
        // Show typing indicator and get AI response
        showTypingIndicator(true);
        
        // Create a TextArea for AI response streaming
        TextArea responseArea = new TextArea();
        responseArea.setEditable(false);
        responseArea.setWrapText(true);
        responseArea.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        
        HBox aiMessageBox = createAIMessageBox(responseArea);
        chatContainer.getChildren().add(aiMessageBox);
        
        // Send message to AI service
        if (aiService != null) {
            try {
                aiService.getClass().getMethod("sendMessage", String.class, TextArea.class, Runnable.class)
                    .invoke(aiService, message, responseArea, (Runnable) () -> {
                        Platform.runLater(() -> {
                            showTypingIndicator(false);
                            // Auto-scroll to bottom
                            Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
                        });
                    });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showTypingIndicator(false);
                    addSystemMessage("❌ AI Service is currently unavailable. Please contact customer service at 1-800-PIKACHU.");
                });
            }
        } else {
            Platform.runLater(() -> {
                showTypingIndicator(false);
                addSystemMessage("❌ AI Service is currently unavailable. Please contact customer service at 1-800-PIKACHU.");
            });
        }
    }
    
    /**
     * Handle quick action buttons
     */
    private void handleQuickAction(String message) {
        messageTextArea.setText(message);
        handleSendMessage();
    }
    
    /**
     * Handle voice input (placeholder)
     */
    @FXML
    private void handleVoiceInput() {
        showAlert("Info", "Voice input feature coming soon! For now, please type your message.");
    }
    
    /**
     * Handle clear chat
     */
    @FXML
    private void handleClearChat() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear Chat");
        alert.setHeaderText("Clear chat history?");
        alert.setContentText("This will remove all messages from this session.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Keep only the welcome message (first child)
                if (chatContainer.getChildren().size() > 1) {
                    chatContainer.getChildren().removeIf(node -> 
                        chatContainer.getChildren().indexOf(node) > 1);
                }
                addSystemMessage("💬 Chat cleared. How can I help you?");
            }
        });
    }
    
    /**
     * Handle back to dashboard
     */
    @FXML
    private void handleBackToDashboard() {
        NavigationManager.getInstance().showCustomerOverview();
    }
    
    /**
     * Handle logout
     */
    @FXML
    private void handleLogout() {
        NavigationManager.getInstance().clearSharedData();
        NavigationManager.getInstance().navigateTo(NavigationManager.LOGIN_SCREEN);
    }
    
    /**
     * Add user message to chat
     */
    private void addUserMessage(String message) {
        VBox messageBox = new VBox(10);
        messageBox.setStyle("-fx-background-color: #F4D03F; -fx-background-radius: 15; -fx-padding: 15; " +
                           "-fx-alignment: center-right; -fx-max-width: 500;");
        
        // Message header
        HBox header = new HBox(10);
        header.setStyle("-fx-alignment: center-left;");
        
        Text userIcon = new Text("👤");
        userIcon.setStyle("-fx-font-size: 16px;");
        
        Text userName = new Text("You");
        userName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-fill: black;");
        
        Text timestamp = new Text(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        timestamp.setStyle("-fx-font-size: 12px; -fx-fill: #333333;");
        
        header.getChildren().addAll(userIcon, userName, timestamp);
        
        // Message content
        Text messageText = new Text(message);
        messageText.setStyle("-fx-font-size: 16px; -fx-fill: black; -fx-wrap-text: true;");
        messageText.setWrappingWidth(450);
        
        messageBox.getChildren().addAll(header, messageText);
        
        // Container to align right
        HBox container = new HBox();
        container.setStyle("-fx-alignment: center-right; -fx-padding: 10 20;");
        container.getChildren().add(messageBox);
        
        chatContainer.getChildren().add(container);
        
        // Auto-scroll to bottom
        Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
    }
    
    /**
     * Add system/AI message to chat
     */
    private void addSystemMessage(String message) {
        VBox messageBox = new VBox(10);
        messageBox.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 15; -fx-padding: 15; " +
                           "-fx-alignment: center-left; -fx-max-width: 500;");
        
        // Message header
        HBox header = new HBox(10);
        header.setStyle("-fx-alignment: center-left;");
        
        Text aiIcon = new Text("🤖");
        aiIcon.setStyle("-fx-font-size: 16px;");
        
        Text aiName = new Text("Pikachu AI");
        aiName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-fill: #2c3e50;");
        
        Text timestamp = new Text(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        timestamp.setStyle("-fx-font-size: 12px; -fx-fill: #7f8c8d;");
        
        header.getChildren().addAll(aiIcon, aiName, timestamp);
        
        // Message content
        Text messageText = new Text(message);
        messageText.setStyle("-fx-font-size: 16px; -fx-fill: #2c3e50; -fx-wrap-text: true;");
        messageText.setWrappingWidth(450);
        
        messageBox.getChildren().addAll(header, messageText);
        
        // Container to align left
        HBox container = new HBox();
        container.setStyle("-fx-alignment: center-left; -fx-padding: 10 20;");
        container.getChildren().add(messageBox);
        
        chatContainer.getChildren().add(container);
        
        // Auto-scroll to bottom
        Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
    }
    
    /**
     * Create AI message box for streaming response
     */
    private HBox createAIMessageBox(TextArea responseArea) {
        VBox messageBox = new VBox(10);
        messageBox.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 15; -fx-padding: 15; " +
                           "-fx-alignment: center-left; -fx-max-width: 500;");
        
        // Message header
        HBox header = new HBox(10);
        header.setStyle("-fx-alignment: center-left;");
        
        Text aiIcon = new Text("🤖");
        aiIcon.setStyle("-fx-font-size: 16px;");
        
        Text aiName = new Text("Pikachu AI");
        aiName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-fill: #2c3e50;");
        
        Text timestamp = new Text(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        timestamp.setStyle("-fx-font-size: 12px; -fx-fill: #7f8c8d;");
        
        header.getChildren().addAll(aiIcon, aiName, timestamp);
        
        // Style the response area
        responseArea.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; " +
                             "-fx-font-size: 16px; -fx-text-fill: #2c3e50;");
        responseArea.setPrefRowCount(1);
        responseArea.setMaxWidth(450);
        
        messageBox.getChildren().addAll(header, responseArea);
        
        // Container to align left
        HBox container = new HBox();
        container.setStyle("-fx-alignment: center-left; -fx-padding: 10 20;");
        container.getChildren().add(messageBox);
        
        return container;
    }
    
    /**
     * Show/hide typing indicator
     */
    private void showTypingIndicator(boolean show) {
        if (typingIndicator != null) {
            typingIndicator.setVisible(show);
            typingIndicator.setManaged(show);
        }
        
        if (sendButton != null) {
            sendButton.setDisable(show);
        }
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