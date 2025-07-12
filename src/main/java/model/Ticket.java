package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer support ticket.
 */
public class Ticket {
    private String ticketId;
    private String customerId;
    private String customerName;
    private String customerEmail;
    private String subject;
    private String description;
    private String category;
    private TicketStatus status;
    private TicketPriority priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String assignedTo;
    private List<TicketReply> replies;
    private LocalDateTime submissionDate;
    private String closedBy;
    private LocalDateTime closeDate;

    public Ticket() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = TicketStatus.OPEN;
        this.priority = TicketPriority.MEDIUM;
        this.replies = new ArrayList<>();
    }

    public Ticket(String ticketId, String customerId, String subject, String description) {
        this();
        this.ticketId = ticketId;
        this.customerId = customerId;
        this.subject = subject;
        this.description = description;
    }

    /**
     * Add a reply to this ticket
     * @param reply The reply message
     * @param repliedBy User ID who replied
     */
    public void addReply(String reply, String repliedBy) {
        TicketReply ticketReply = new TicketReply(reply, repliedBy, LocalDateTime.now());
        replies.add(ticketReply);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Close the ticket
     */
    public void closeTicket() {
        this.status = TicketStatus.CLOSED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Escalate the ticket
     */
    public void escalateTicket() {
        this.priority = TicketPriority.HIGH;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { 
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public TicketPriority getPriority() { return priority; }
    public void setPriority(TicketPriority priority) { this.priority = priority; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

    public List<TicketReply> getReplies() { return replies; }
    public void setReplies(List<TicketReply> replies) { this.replies = replies; }

    public LocalDateTime getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(LocalDateTime submissionDate) { this.submissionDate = submissionDate; }

    public String getClosedBy() { return closedBy; }
    public void setClosedBy(String closedBy) { this.closedBy = closedBy; }

    public LocalDateTime getCloseDate() { return closeDate; }
    public void setCloseDate(LocalDateTime closeDate) { this.closeDate = closeDate; }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId='" + ticketId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", subject='" + subject + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", replies=" + replies.size() +
                '}';
    }
}

 