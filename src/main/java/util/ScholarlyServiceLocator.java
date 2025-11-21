package util;

import service.*;
import dao.*;

/**
 * Service Locator for scholarly publishing services
 * This provides centralized access to all application services
 */
public class ScholarlyServiceLocator {
    private static ScholarlyServiceLocator instance;
    
    private ArticleService articleService;
    private CommentService commentService;
    private AuthorProfileService authorProfileService;
    private UserService userService;
    private FlightService flightService;
    private BookingService bookingService;
    private TicketService ticketService;
    private util.DataManager dataManager;
    
    private ScholarlyServiceLocator() {
        // Initialize scholarly services
        this.articleService = new ArticleService();
        this.commentService = new CommentService();
        this.authorProfileService = new AuthorProfileService();
    }
    
    public static ScholarlyServiceLocator getInstance() {
        if (instance == null) {
            instance = new ScholarlyServiceLocator();
        }
        return instance;
    }
    
    // Scholarly Services
    public ArticleService getArticleService() {
        return articleService;
    }
    
    public void setArticleService(ArticleService articleService) {
        this.articleService = articleService;
    }
    
    public CommentService getCommentService() {
        return commentService;
    }
    
    public void setCommentService(CommentService commentService) {
        this.commentService = commentService;
    }
    
    public AuthorProfileService getAuthorProfileService() {
        return authorProfileService;
    }
    
    public void setAuthorProfileService(AuthorProfileService authorProfileService) {
        this.authorProfileService = authorProfileService;
    }
    
    // Legacy services (maintained for backward compatibility)
    public UserService getUserService() {
        return userService;
    }
    
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    
    public FlightService getFlightService() {
        return flightService;
    }
    
    public void setFlightService(FlightService flightService) {
        this.flightService = flightService;
    }
    
    public BookingService getBookingService() {
        return bookingService;
    }
    
    public void setBookingService(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    
    public TicketService getTicketService() {
        return ticketService;
    }
    
    public void setTicketService(TicketService ticketService) {
        this.ticketService = ticketService;
    }
    
    public util.DataManager getDataManager() {
        return dataManager;
    }
    
    public void setDataManager(util.DataManager dataManager) {
        this.dataManager = dataManager;
    }
}
