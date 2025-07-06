package ai;

import java.util.concurrent.CompletableFuture;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import javafx.scene.control.TextArea;

/**
 * Main AI service for Pikachu Airlines customer support
 * Implements OpenAI integration using LangChain4j
 */
public class AirlineAIService {
    
    private AirlineAssistant assistant;
    private boolean isInitialized = false;
    private static AirlineAIService instance;
    
    // System prompt for the AI assistant
    private static final String SYSTEM_PROMPT = 
        "You are a helpful customer service representative for Pikachu Airlines. " +
        "You should provide accurate, friendly, and professional assistance to customers. " +
        "You have access to airline policies, procedures, and troubleshooting information. " +
        "Key information about Pikachu Airlines:\n" +
        "- Customer service phone: 1-800-PIKACHU (1-800-745-2248)\n" +
        "- Email: support@pikachuairlines.com\n" +
        "- Booking changes allowed up to 2 hours before departure\n" +
        "- Free cancellation within 24 hours of booking\n" +
        "- Baggage: 1 carry-on (7kg) and 1 checked bag (23kg) included in Economy\n" +
        "- Online check-in opens 24 hours before departure\n" +
        "- Frequent flyer program: Pika Miles\n" +
        "- Serves 150+ destinations across Asia-Pacific, Europe, North America, and Middle East\n" +
        "If you don't know something specific, direct customers to contact customer service. " +
        "Always maintain a positive and helpful tone. Keep responses concise but informative.";
    
    private AirlineAIService() {
        // Private constructor for singleton
    }
    
    /**
     * Gets the singleton instance of the AI service
     */
    public static AirlineAIService getInstance() {
        if (instance == null) {
            instance = new AirlineAIService();
        }
        return instance;
    }
    
    /**
     * Initializes the AI service with OpenAI integration
     */
    public CompletableFuture<Boolean> initialize() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("Initializing Pikachu Airlines AI Service...");
                
                // Create streaming chat model with OpenAI
                StreamingChatModel streamingModel = createStreamingChatModel();
                
                // Build the AI assistant
                assistant = AiServices.builder(AirlineAssistant.class)
                    .streamingChatModel(streamingModel)
                    .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                    .systemMessageProvider(chatMemoryId -> SYSTEM_PROMPT)
                    .build();
                
                isInitialized = true;
                System.out.println("AI Service initialized successfully!");
                return true;
                
            } catch (Exception e) {
                System.err.println("Failed to initialize AI Service: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        });
    }
    
    /**
     * Creates the streaming chat model with OpenAI
     */
    private StreamingChatModel createStreamingChatModel() {
        try {
            return OpenAiStreamingChatModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName(OpenAiChatModelName.GPT_4_O_MINI)
                .temperature(0.7)
                .maxTokens(500)
                .build();
        } catch (Exception e) {
            System.err.println("Failed to create streaming model with primary key, trying backup...");
            try {
                return OpenAiStreamingChatModel.builder()
                    .apiKey(ApiKeys.OPENAI_API_KEY_BACKUP_1)
                    .modelName(OpenAiChatModelName.GPT_4_O_MINI)
                    .temperature(0.7)
                    .maxTokens(500)
                    .build();
            } catch (Exception e2) {
                System.err.println("Failed to create streaming model with backup key 1, trying backup 2...");
                return OpenAiStreamingChatModel.builder()
                    .apiKey(ApiKeys.OPENAI_API_KEY_BACKUP_2)
                    .modelName(OpenAiChatModelName.GPT_4_O_MINI)
                    .temperature(0.7)
                    .maxTokens(500)
                    .build();
            }
        }
    }
    
    /**
     * Sends a message to the AI assistant and handles streaming response
     */
    public void sendMessage(String message, TextArea chatArea, Runnable onComplete) {
        if (!isInitialized) {
            chatArea.appendText("❌ AI Service is not initialized. Please wait and try again.\n\n");
            return;
        }
        
        if (assistant == null) {
            chatArea.appendText("❌ AI Assistant is not available. Please contact customer service.\n\n");
            return;
        }
        
        try {
            // Add user message context
            String contextualMessage = "Customer question: " + message + 
                "\nPlease provide a helpful response as a Pikachu Airlines customer service representative.";
            
            // Create response handler
            AirlineStreamingResponseHandler responseHandler = 
                new AirlineStreamingResponseHandler(chatArea, onComplete);
            
            // Send message and handle streaming response
            assistant.chat(contextualMessage)
                .onPartialResponse(responseHandler::onNext)
                .onCompleteResponse(responseHandler::onComplete)
                .onError(responseHandler::onError)
                .start();
                
        } catch (Exception e) {
            chatArea.appendText("❌ Error sending message: " + e.getMessage() + "\n\n");
            System.err.println("Error in sendMessage: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Gets a simple response (non-streaming) for testing
     */
    public String getSimpleResponse(String message) {
        if (!isInitialized || assistant == null) {
            return "AI Service is not available. Please contact customer service at 1-800-PIKACHU.";
        }
        
        try {
            String contextualMessage = "Customer question: " + message + 
                "\nPlease provide a helpful response as a Pikachu Airlines customer service representative.";
            return assistant.answer(contextualMessage);
        } catch (Exception e) {
            System.err.println("Error getting simple response: " + e.getMessage());
            return "Sorry, I encountered an error. Please contact customer service at 1-800-PIKACHU.";
        }
    }
    
    /**
     * Checks if the AI service is ready for use
     */
    public boolean isReady() {
        return isInitialized && assistant != null;
    }
    
    /**
     * Gets initialization status
     */
    public boolean isInitialized() {
        return isInitialized;
    }
} 