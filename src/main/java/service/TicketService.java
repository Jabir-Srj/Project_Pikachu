package service;

import model.Ticket;
import model.Customer;
import model.TicketStatus;
import model.TicketPriority;
import dao.TicketDAO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for ticket-related operations including submission, tracking, and management.
 */
public class TicketService {
    private TicketDAO ticketDAO;

    public TicketService() {
        this.ticketDAO = new TicketDAO();
    }

    /**
     * Submit a new support ticket
     * @param customer Customer submitting the ticket
     * @param subject Subject of the ticket
     * @param description Detailed description of the issue
     * @param category Category of the ticket
     * @param priority Priority level
     * @return Created ticket if successful
     */
    public Ticket submitTicket(Customer customer, String subject, String description, String category, TicketPriority priority) {
        try {
            if (!validateTicketData(subject, description)) {
                return null;
            }

            Ticket ticket = new Ticket();
            ticket.setTicketId(generateTicketId());
            ticket.setCustomerId(customer.getUserId());
            ticket.setCustomerName(customer.getFirstName() + " " + customer.getLastName());
            ticket.setCustomerEmail(customer.getEmail());
            ticket.setSubject(subject);
            ticket.setDescription(description);
            ticket.setCategory(category != null ? category : "General");
            ticket.setPriority(priority != null ? priority : TicketPriority.MEDIUM);
            ticket.setStatus(TicketStatus.OPEN);
            ticket.setSubmissionDate(LocalDateTime.now());

            if (ticketDAO.save(ticket)) {
                return ticket;
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error submitting ticket: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get customer tickets
     * @param customerId Customer ID
     * @return List of customer tickets
     */
    public List<Ticket> getCustomerTickets(String customerId) {
        return ticketDAO.findByCustomerId(customerId);
    }

    /**
     * Get ticket by ID
     * @param ticketId Ticket ID
     * @return Ticket if found
     */
    public Optional<Ticket> getTicketById(String ticketId) {
        return ticketDAO.findById(ticketId);
    }

    /**
     * Get all open tickets (for admin use)
     * @return List of open tickets
     */
    public List<Ticket> getOpenTickets() {
        return ticketDAO.findByStatus(TicketStatus.OPEN);
    }

    /**
     * Reply to a ticket (admin function)
     * @param ticketId Ticket ID
     * @param adminId Admin ID
     * @param replyMessage Reply message
     * @return true if reply successful
     */
    public boolean replyToTicket(String ticketId, String adminId, String replyMessage) {
        try {
            Optional<Ticket> ticketOpt = ticketDAO.findById(ticketId);
            if (ticketOpt.isEmpty()) {
                return false;
            }

            Ticket ticket = ticketOpt.get();
            ticket.addReply(adminId, replyMessage);
            ticket.setStatus(TicketStatus.IN_PROGRESS);
            
            return ticketDAO.update(ticket);
        } catch (Exception e) {
            System.err.println("Error replying to ticket: " + e.getMessage());
            return false;
        }
    }

    /**
     * Close a ticket
     * @param ticketId Ticket ID
     * @param adminId Admin ID
     * @return true if closure successful
     */
    public boolean closeTicket(String ticketId, String adminId) {
        try {
            Optional<Ticket> ticketOpt = ticketDAO.findById(ticketId);
            if (ticketOpt.isEmpty()) {
                return false;
            }

            Ticket ticket = ticketOpt.get();
            ticket.setStatus(TicketStatus.CLOSED);
            ticket.setClosedBy(adminId);
            ticket.setCloseDate(LocalDateTime.now());
            
            return ticketDAO.update(ticket);
        } catch (Exception e) {
            System.err.println("Error closing ticket: " + e.getMessage());
            return false;
        }
    }

    /**
     * Escalate ticket to agent
     * @param ticketId Ticket ID
     * @return true if escalation successful
     */
    public boolean escalateToAgent(String ticketId) {
        try {
            Optional<Ticket> ticketOpt = ticketDAO.findById(ticketId);
            if (ticketOpt.isEmpty()) {
                return false;
            }

            Ticket ticket = ticketOpt.get();
            ticket.setPriority(TicketPriority.HIGH);
            ticket.setStatus(TicketStatus.ESCALATED);
            
            return ticketDAO.update(ticket);
        } catch (Exception e) {
            System.err.println("Error escalating ticket: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all tickets (for admin dashboard)
     * @return List of all tickets
     */
    public List<Ticket> getAllTickets() {
        return ticketDAO.findAll();
    }

    /**
     * Search tickets by subject or description
     * @param searchTerm Search term
     * @return List of matching tickets
     */
    public List<Ticket> searchTickets(String searchTerm) {
        return ticketDAO.searchTickets(searchTerm);
    }

    /**
     * Validate ticket data
     * @param subject Subject
     * @param description Description
     * @return true if valid
     */
    private boolean validateTicketData(String subject, String description) {
        if (subject == null || subject.trim().isEmpty()) {
            return false;
        }
        if (description == null || description.trim().isEmpty()) {
            return false;
        }
        return subject.length() <= 200 && description.length() <= 2000;
    }

    /**
     * Generate unique ticket ID
     * @return Generated ticket ID
     */
    private String generateTicketId() {
        return "TKT_" + System.currentTimeMillis();
    }
} 