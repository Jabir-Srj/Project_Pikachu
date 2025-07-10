package controller;

import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.FAQ;

/**
 * Controller for FAQ operations
 * Provides FAQ data retrieval and management functionality
 */
public class FAQController {
    
    private ObservableList<FAQ> faqs;
    private static FAQController instance;
    
    private FAQController() {
        faqs = FXCollections.observableArrayList();
        initializeDefaultFAQs();
    }
    
    /**
     * Get singleton instance
     */
    public static FAQController getInstance() {
        if (instance == null) {
            instance = new FAQController();
        }
        return instance;
    }
    
    /**
     * Initialize default FAQs
     */
    private void initializeDefaultFAQs() {
        faqs.clear();
        
        // Booking FAQs
        faqs.add(new FAQ("FAQ001", 
            "How do I book a flight?", 
            "You can book a flight through our website, mobile app, or by calling our customer service at 1-800-PIKACHU. Simply select your departure and arrival cities, choose your travel dates, and follow the booking process.",
            "Booking",
            "System"));
            
        faqs.add(new FAQ("FAQ002",
            "Can I change my booking?",
            "Yes, you can change your booking up to 2 hours before departure. Changes may be subject to fare differences and change fees. You can modify your booking online through 'Manage My Booking' or contact customer service.",
            "Booking",
            "System"));
            
        faqs.add(new FAQ("FAQ003",
            "How do I cancel my booking?",
            "You can cancel your booking online through 'Manage My Booking' or by calling customer service. Free cancellation is available within 24 hours of booking. After 24 hours, cancellation fees may apply.",
            "Booking",
            "System"));
        
        // Check-in FAQs
        faqs.add(new FAQ("FAQ004",
            "How do I check in online?",
            "Online check-in opens 24 hours before departure. Visit our website or mobile app, go to 'Check-in', enter your booking reference and last name, select your seats, and print or download your boarding pass.",
            "Check-in",
            "System"));
            
        faqs.add(new FAQ("FAQ005",
            "When should I arrive at the airport?",
            "We recommend arriving 2 hours before domestic flights and 3 hours before international flights. During peak travel periods, consider arriving earlier.",
            "Check-in",
            "System"));
        
        // Baggage FAQs
        faqs.add(new FAQ("FAQ006",
            "What are your baggage allowances?",
            "Economy passengers get 1 carry-on bag (7kg, 55x40x23cm) and 1 checked bag (23kg) included. Business class includes 2 carry-on bags and 2 checked bags (32kg each). Additional baggage can be purchased.",
            "Baggage",
            "System"));
            
        faqs.add(new FAQ("FAQ007",
            "What items are prohibited in carry-on luggage?",
            "Prohibited items include liquids over 100ml, sharp objects, flammable materials, and lithium batteries in checked luggage. Please check our detailed prohibited items list on our website.",
            "Baggage",
            "System"));
        
        // Flight Status FAQs
        faqs.add(new FAQ("FAQ008",
            "How do I check my flight status?",
            "You can check your flight status on our website, mobile app, or by calling customer service. Enter your flight number or route to get real-time updates on departures, arrivals, and any delays.",
            "Flight Status",
            "System"));
            
        faqs.add(new FAQ("FAQ009",
            "What happens if my flight is delayed or cancelled?",
            "We'll notify you via SMS and email about any changes. For delays over 3 hours or cancellations, you may be entitled to compensation, rebooking, or refund. Contact customer service for assistance.",
            "Flight Status",
            "System"));
        
        // Payment FAQs
        faqs.add(new FAQ("FAQ010",
            "What payment methods do you accept?",
            "We accept major credit cards (Visa, Mastercard, American Express), debit cards, PayPal, and bank transfers. Some payment methods may have processing fees.",
            "Payment",
            "System"));
            
        faqs.add(new FAQ("FAQ011",
            "How do I request a refund?",
            "Refund requests can be made through 'Manage My Booking' or by contacting customer service. Refund eligibility depends on your ticket type and timing. Processing takes 7-14 business days.",
            "Payment",
            "System"));
        
        // Loyalty Program FAQs
        faqs.add(new FAQ("FAQ012",
            "How does the Pika Miles program work?",
            "Earn 1 Pika Mile for every $1 spent on flights. Miles can be redeemed for free flights, upgrades, and other rewards. Elite members get bonus earning rates and exclusive benefits.",
            "Loyalty Program",
            "System"));
    }
    
    /**
     * Get all FAQs
     */
    public ObservableList<FAQ> getAllFAQs() {
        return FXCollections.observableArrayList(faqs);
    }
    
    /**
     * Get active FAQs only
     */
    public ObservableList<FAQ> getActiveFAQs() {
        return faqs.filtered(FAQ::isActive);
    }
    
    /**
     * Get FAQs by category
     */
    public ObservableList<FAQ> getFAQsByCategory(String category) {
        return faqs.filtered(faq -> faq.isActive() && 
            (category == null || category.isEmpty() || faq.getCategory().equals(category)));
    }
    
    /**
     * Search FAQs by keyword
     */
    public ObservableList<FAQ> searchFAQs(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getActiveFAQs();
        }
        
        String searchTerm = keyword.toLowerCase();
        return faqs.filtered(faq -> faq.isActive() && 
            (faq.getQuestion().toLowerCase().contains(searchTerm) ||
             faq.getAnswer().toLowerCase().contains(searchTerm) ||
             faq.getCategory().toLowerCase().contains(searchTerm)));
    }
    
    /**
     * Get FAQ categories
     */
    public List<String> getCategories() {
        return faqs.stream()
            .filter(FAQ::isActive)
            .map(FAQ::getCategory)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }
    
    /**
     * Get FAQ by ID
     */
    public FAQ getFAQById(String faqId) {
        return faqs.stream()
            .filter(faq -> faq.getFaqId().equals(faqId))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Increment view count for FAQ
     */
    public void incrementViewCount(String faqId) {
        FAQ faq = getFAQById(faqId);
        if (faq != null) {
            faq.incrementViewCount();
        }
    }
    
    /**
     * Get most viewed FAQs
     */
    public List<FAQ> getMostViewedFAQs(int limit) {
        return faqs.stream()
            .filter(FAQ::isActive)
            .sorted((f1, f2) -> Integer.compare(f2.getViewCount(), f1.getViewCount()))
            .limit(limit)
            .collect(Collectors.toList());
    }
} 