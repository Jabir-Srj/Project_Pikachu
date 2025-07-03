package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Admin class extending User for system administrators.
 */
public class Admin extends User {
    private List<String> managedSections;
    private String department;
    private AdminLevel level;

    public Admin() {
        super();
        this.managedSections = new ArrayList<>();
        this.setRole(UserRole.ADMIN);
        this.level = AdminLevel.JUNIOR;
    }

    public Admin(String userId, String username, String password, String email,
                 String firstName, String lastName, String phoneNumber, String department) {
        super(userId, username, password, email, firstName, lastName, phoneNumber, UserRole.ADMIN);
        this.managedSections = new ArrayList<>();
        this.department = department;
        this.level = AdminLevel.JUNIOR;
    }

    @Override
    public void updateProfile() {
        // Admin-specific profile update logic
        System.out.println("Updating admin profile for: " + getFirstName() + " " + getLastName());
        // This would typically update admin-specific information in the database
    }

    /**
     * Manage flight details (add, update, remove)
     * @param action The action to perform
     * @param flight The flight to manage
     * @return true if action successful
     */
    public boolean manageFlightDetails(String action, Flight flight) {
        if (flight == null) return false;
        
        switch (action.toLowerCase()) {
            case "add":
                // Logic to add new flight
                System.out.println("Adding flight: " + flight.getFlightNumber());
                return true;
            case "update":
                // Logic to update existing flight
                System.out.println("Updating flight: " + flight.getFlightNumber());
                return true;
            case "remove":
                // Logic to remove flight
                System.out.println("Removing flight: " + flight.getFlightNumber());
                return true;
            default:
                return false;
        }
    }

    /**
     * Reply to customer support ticket
     * @param ticket The ticket to reply to
     * @param reply The reply message
     * @return true if reply sent successfully
     */
    public boolean replyToTicket(Ticket ticket, String reply) {
        if (ticket == null || reply == null || reply.trim().isEmpty()) {
            return false;
        }
        
        ticket.addReply(reply, getUserId());
        System.out.println("Admin " + getFirstName() + " replied to ticket " + ticket.getTicketId());
        return true;
    }

    /**
     * Close a support ticket
     * @param ticket The ticket to close
     * @return true if ticket closed successfully
     */
    public boolean closeTicket(Ticket ticket) {
        if (ticket == null) return false;
        
        ticket.setStatus(TicketStatus.CLOSED);
        System.out.println("Admin " + getFirstName() + " closed ticket " + ticket.getTicketId());
        return true;
    }

    /**
     * Update knowledge base (FAQs)
     * @param action The action (add, update, remove)
     * @param faq The FAQ item
     * @return true if action successful
     */
    public boolean updateKnowledgeBase(String action, FAQ faq) {
        if (faq == null) return false;
        
        switch (action.toLowerCase()) {
            case "add":
                System.out.println("Adding FAQ: " + faq.getQuestion());
                return true;
            case "update":
                System.out.println("Updating FAQ: " + faq.getQuestion());
                return true;
            case "remove":
                System.out.println("Removing FAQ: " + faq.getQuestion());
                return true;
            default:
                return false;
        }
    }

    /**
     * View and manage customer details
     * @param customerId The customer ID to manage
     * @return Customer object if found
     */
    public Customer manageCustomerDetails(String customerId) {
        // Logic to retrieve and manage customer details
        System.out.println("Managing customer details for ID: " + customerId);
        return null; // Would return actual customer object from database
    }

    /**
     * Add a managed section to this admin
     * @param section The section to manage
     */
    public void addManagedSection(String section) {
        if (section != null && !managedSections.contains(section)) {
            managedSections.add(section);
        }
    }

    /**
     * Check if admin can manage a specific section
     * @param section The section to check
     * @return true if admin can manage this section
     */
    public boolean canManageSection(String section) {
        return managedSections.contains(section) || level == AdminLevel.SENIOR;
    }

    // Getters and Setters
    public List<String> getManagedSections() { return new ArrayList<>(managedSections); }
    public void setManagedSections(List<String> managedSections) { this.managedSections = managedSections; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public AdminLevel getLevel() { return level; }
    public void setLevel(AdminLevel level) { this.level = level; }

    @Override
    public String toString() {
        return "Admin{" +
                "userId='" + getUserId() + '\'' +
                ", name='" + getFirstName() + " " + getLastName() + '\'' +
                ", department='" + department + '\'' +
                ", level=" + level +
                ", managedSections=" + managedSections.size() +
                '}';
    }
}

/**
 * Enum for admin levels
 */
enum AdminLevel {
    JUNIOR("Junior Admin"),
    SENIOR("Senior Admin"),
    SUPER("Super Admin");

    private final String displayName;

    AdminLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
} 