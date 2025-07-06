package ai;

import dev.langchain4j.service.TokenStream;

/**
 * AI Service interface for Pikachu Airlines Customer Support
 * 
 * This interface defines the contract for AI-powered customer support functionality.
 * LangChain4j will provide an implementation using proxy and reflection,
 * integrating RAG (Retrieval Augmented Generation) capabilities to provide
 * accurate airline-specific responses based on the knowledge base.
 */
public interface AirlineAssistant {
    
    /**
     * Provides a complete response to customer queries
     * @param query The customer's question or request
     * @return A complete response string
     */
    String answer(String query);
    
    /**
     * Provides streaming responses for real-time chat experience
     * @param message The customer's message
     * @return A TokenStream for real-time response delivery
     */
    TokenStream chat(String message);
} 