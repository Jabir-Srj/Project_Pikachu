package model;

import java.time.LocalDateTime;

/**
 * Represents a reply to a ticket
 */
public class TicketReply {
    private String responderId;
    private String responderName;
    private String message;
    private LocalDateTime timestamp;

    public TicketReply() {}

    public TicketReply(String message, String repliedBy, LocalDateTime repliedAt) {
        this.message = message;
        this.responderId = repliedBy;
        this.timestamp = repliedAt;
    }

    // Getters and Setters
    public String getResponderId() { return responderId; }
    public void setResponderId(String responderId) { this.responderId = responderId; }

    public String getResponderName() { return responderName; }
    public void setResponderName(String responderName) { this.responderName = responderName; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    // Legacy getter for compatibility
    public String getRepliedBy() { return responderId; }
    public void setRepliedBy(String repliedBy) { this.responderId = repliedBy; }

    public LocalDateTime getRepliedAt() { return timestamp; }
    public void setRepliedAt(LocalDateTime repliedAt) { this.timestamp = repliedAt; }
}
