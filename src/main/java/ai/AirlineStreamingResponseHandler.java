package ai;

import dev.langchain4j.model.chat.response.ChatResponse;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 * Handles streaming responses from the AI assistant for real-time chat experience
 */
public class AirlineStreamingResponseHandler {
    
    private final TextArea chatArea;
    private final Runnable onComplete;
    private final StringBuilder currentResponse;
    
    public AirlineStreamingResponseHandler(TextArea chatArea, Runnable onComplete) {
        this.chatArea = chatArea;
        this.onComplete = onComplete;
        this.currentResponse = new StringBuilder();
    }
    
    /**
     * Handles individual tokens as they arrive from the AI model
     */
    public void onNext(String token) {
        Platform.runLater(() -> {
            currentResponse.append(token);
            // Update the UI with the new token
            String currentText = chatArea.getText();
            if (currentText.endsWith("🤖 Pikachu Airlines AI: ")) {
                chatArea.setText(currentText + token);
            } else {
                chatArea.setText(currentText + token);
            }
            
            // Auto-scroll to bottom
            chatArea.setScrollTop(Double.MAX_VALUE);
        });
    }
    
    /**
     * Handles completion of the AI response
     */
    public void onComplete(ChatResponse response) {
        Platform.runLater(() -> {
            // Add a newline for the next message
            chatArea.appendText("\n\n");
            
            // Log the completion
            System.out.println("AI Response completed: " + currentResponse.toString());
            
            // Run completion callback
            if (onComplete != null) {
                onComplete.run();
            }
        });
    }
    
    /**
     * Handles errors during AI response generation
     */
    public void onError(Throwable error) {
        Platform.runLater(() -> {
            String errorMessage = "\n❌ Sorry, I encountered an error while processing your request. Please try again or contact our customer service at 1-800-PIKACHU.\n\n";
            chatArea.appendText(errorMessage);
            
            // Log the error
            System.err.println("AI Response error: " + error.getMessage());
            error.printStackTrace();
            
            // Run completion callback even on error
            if (onComplete != null) {
                onComplete.run();
            }
        });
    }
    
    /**
     * Gets the current accumulated response
     */
    public String getCurrentResponse() {
        return currentResponse.toString();
    }
} 