package util;

import ai.AirlineAIService;
import service.BookingService;
import service.FlightService;
import service.TicketService;
import service.UserService;

/**
 * ServiceLocator provides access to application services
 * This avoids the need for controllers to directly access AirlineApp
 */
public class ServiceLocator {
    private static ServiceLocator instance;
    
    private UserService userService;
    private FlightService flightService;
    private BookingService bookingService;
    private TicketService ticketService;
    private AirlineAIService aiService;
    private DataManager dataManager;
    
    private ServiceLocator() {
        // Private constructor for singleton
    }
    
    /**
     * Get the singleton instance
     */
    public static ServiceLocator getInstance() {
        if (instance == null) {
            instance = new ServiceLocator();
        }
        return instance;
    }
    
    // Setters for services (called from AirlineApp)
    
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    
    public void setFlightService(FlightService flightService) {
        this.flightService = flightService;
    }
    
    public void setBookingService(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    
    public void setTicketService(TicketService ticketService) {
        this.ticketService = ticketService;
    }
    
    public void setAiService(AirlineAIService aiService) {
        this.aiService = aiService;
    }
    
    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }
    
    // Getters for services
    
    public UserService getUserService() {
        return userService;
    }
    
    public FlightService getFlightService() {
        return flightService;
    }
    
    public BookingService getBookingService() {
        return bookingService;
    }
    
    public TicketService getTicketService() {
        return ticketService;
    }
    
    public AirlineAIService getAiService() {
        return aiService;
    }
    
    public DataManager getDataManager() {
        return dataManager;
    }
} 