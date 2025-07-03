package model;

import java.util.ArrayList;
import java.util.List;

/**
 * AirlineManagement class extending User for airline management personnel.
 */
public class AirlineManagement extends User {
    private String department;
    private List<String> authorizedActions;
    private ManagementLevel level;

    public AirlineManagement() {
        super();
        this.authorizedActions = new ArrayList<>();
        this.setRole(UserRole.AIRLINE_MANAGEMENT);
        this.level = ManagementLevel.SUPERVISOR;
    }

    public AirlineManagement(String userId, String username, String password, String email,
                           String firstName, String lastName, String phoneNumber, String department) {
        super(userId, username, password, email, firstName, lastName, phoneNumber, UserRole.AIRLINE_MANAGEMENT);
        this.authorizedActions = new ArrayList<>();
        this.department = department;
        this.level = ManagementLevel.SUPERVISOR;
        initializeDefaultActions();
    }

    /**
     * Initialize default actions for airline management
     */
    private void initializeDefaultActions() {
        authorizedActions.add("APPROVE_REFUND");
        authorizedActions.add("REJECT_REFUND");
        authorizedActions.add("EDIT_BOOKING");
        authorizedActions.add("CANCEL_BOOKING");
        authorizedActions.add("VIEW_BOOKING_DETAILS");
    }

    @Override
    public void updateProfile() {
        // Airline management specific profile update logic
        System.out.println("Updating airline management profile for: " + getFirstName() + " " + getLastName());
        // This would typically update management-specific information in the database
    }

    /**
     * Approve a refund request
     * @param refundRequest The refund request to approve
     * @return true if approval successful
     */
    public boolean approveRefund(RefundRequest refundRequest) {
        if (refundRequest == null || !canPerformAction("APPROVE_REFUND")) {
            return false;
        }

        refundRequest.setStatus(RefundStatus.APPROVED);
        refundRequest.setReviewedBy(getUserId());
        refundRequest.setReviewDate(java.time.LocalDateTime.now());
        
        System.out.println("Refund approved by " + getFirstName() + " for request: " + refundRequest.getRefundId());
        return true;
    }

    /**
     * Reject a refund request
     * @param refundRequest The refund request to reject
     * @param reason The reason for rejection
     * @return true if rejection successful
     */
    public boolean rejectRefund(RefundRequest refundRequest, String reason) {
        if (refundRequest == null || !canPerformAction("REJECT_REFUND")) {
            return false;
        }

        refundRequest.setStatus(RefundStatus.REJECTED);
        refundRequest.setReviewedBy(getUserId());
        refundRequest.setReviewDate(java.time.LocalDateTime.now());
        refundRequest.setRejectionReason(reason);
        
        System.out.println("Refund rejected by " + getFirstName() + " for request: " + refundRequest.getRefundId());
        return true;
    }

    /**
     * Edit booking information
     * @param booking The booking to edit
     * @param changes Description of changes made
     * @return true if edit successful
     */
    public boolean editBooking(Booking booking, String changes) {
        if (booking == null || !canPerformAction("EDIT_BOOKING")) {
            return false;
        }

        // Logic to edit booking details
        System.out.println("Booking " + booking.getBookingId() + " edited by " + getFirstName());
        System.out.println("Changes: " + changes);
        return true;
    }

    /**
     * Cancel a booking on behalf of customer
     * @param booking The booking to cancel
     * @param reason The reason for cancellation
     * @return true if cancellation successful
     */
    public boolean cancelBooking(Booking booking, String reason) {
        if (booking == null || !canPerformAction("CANCEL_BOOKING")) {
            return false;
        }

        booking.cancelBooking();
        System.out.println("Booking " + booking.getBookingId() + " cancelled by " + getFirstName());
        System.out.println("Reason: " + reason);
        return true;
    }

    /**
     * View detailed booking information
     * @param bookingId The booking ID to view
     * @return Booking object if found and authorized
     */
    public Booking viewBookingDetails(String bookingId) {
        if (!canPerformAction("VIEW_BOOKING_DETAILS")) {
            return null;
        }

        // Logic to retrieve booking details
        System.out.println("Viewing booking details for: " + bookingId);
        return null; // Would return actual booking object from database
    }

    /**
     * Check if user can perform a specific action
     * @param action The action to check
     * @return true if authorized
     */
    public boolean canPerformAction(String action) {
        return authorizedActions.contains(action) || level == ManagementLevel.DIRECTOR;
    }

    /**
     * Add an authorized action for this management user
     * @param action The action to authorize
     */
    public void addAuthorizedAction(String action) {
        if (action != null && !authorizedActions.contains(action)) {
            authorizedActions.add(action);
        }
    }

    /**
     * Remove an authorized action
     * @param action The action to remove
     */
    public void removeAuthorizedAction(String action) {
        authorizedActions.remove(action);
    }

    // Getters and Setters
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public List<String> getAuthorizedActions() { return new ArrayList<>(authorizedActions); }
    public void setAuthorizedActions(List<String> authorizedActions) { this.authorizedActions = authorizedActions; }

    public ManagementLevel getLevel() { return level; }
    public void setLevel(ManagementLevel level) { this.level = level; }

    @Override
    public String toString() {
        return "AirlineManagement{" +
                "userId='" + getUserId() + '\'' +
                ", name='" + getFirstName() + " " + getLastName() + '\'' +
                ", department='" + department + '\'' +
                ", level=" + level +
                ", authorizedActions=" + authorizedActions.size() +
                '}';
    }
}

/**
 * Enum for management levels
 */
enum ManagementLevel {
    SUPERVISOR("Supervisor"),
    MANAGER("Manager"),
    DIRECTOR("Director");

    private final String displayName;

    ManagementLevel(String displayName) {
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