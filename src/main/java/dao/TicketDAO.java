package dao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import model.Ticket;
import model.TicketStatus;
import util.DataManager;

/**
 * Data Access Object for Ticket operations
 */
public class TicketDAO {
    private DataManager dataManager;

    public TicketDAO() {
        this.dataManager = new DataManager();
    }

    /**
     * Save a new ticket
     * @param ticket Ticket to save
     * @return true if save successful
     */
    public boolean save(Ticket ticket) {
        try {
            List<Ticket> tickets = dataManager.loadTickets();
            tickets.add(ticket);
            return dataManager.saveTickets(tickets);
        } catch (Exception e) {
            System.err.println("Error saving ticket: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update an existing ticket
     * @param ticket Ticket to update
     * @return true if update successful
     */
    public boolean update(Ticket ticket) {
        try {
            List<Ticket> tickets = dataManager.loadTickets();
            for (int i = 0; i < tickets.size(); i++) {
                if (tickets.get(i).getTicketId().equals(ticket.getTicketId())) {
                    tickets.set(i, ticket);
                    return dataManager.saveTickets(tickets);
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error updating ticket: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete a ticket
     * @param ticketId Ticket ID to delete
     * @return true if delete successful
     */
    public boolean delete(String ticketId) {
        try {
            List<Ticket> tickets = dataManager.loadTickets();
            tickets.removeIf(ticket -> ticket.getTicketId().equals(ticketId));
            return dataManager.saveTickets(tickets);
        } catch (Exception e) {
            System.err.println("Error deleting ticket: " + e.getMessage());
            return false;
        }
    }

    /**
     * Find ticket by ID
     * @param ticketId Ticket ID
     * @return Ticket if found
     */
    public Optional<Ticket> findById(String ticketId) {
        try {
            List<Ticket> tickets = dataManager.loadTickets();
            return tickets.stream()
                .filter(ticket -> ticket.getTicketId().equals(ticketId))
                .findFirst();
        } catch (Exception e) {
            System.err.println("Error finding ticket by ID: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Find tickets by customer ID
     * @param customerId Customer ID
     * @return List of customer tickets
     */
    public List<Ticket> findByCustomerId(String customerId) {
        try {
            List<Ticket> tickets = dataManager.loadTickets();
            System.out.println("TicketDAO: Total tickets loaded: " + tickets.size());
            System.out.println("TicketDAO: Searching for customer ID: '" + customerId + "'");
            
            // Debug: Check first few tickets to see what customerId values are actually in memory
            for (int i = 0; i < Math.min(5, tickets.size()); i++) {
                Ticket ticket = tickets.get(i);
                System.out.println("TicketDAO: Sample ticket " + i + " - ID: " + ticket.getTicketId() + 
                                 ", customerId: '" + ticket.getCustomerId() + "'");
            }
            
            List<Ticket> matchingTickets = tickets.stream()
                .filter(ticket -> {
                    String ticketCustomerId = ticket.getCustomerId();
                    boolean matches = ticketCustomerId != null && ticketCustomerId.equals(customerId);
                    System.out.println("TicketDAO: Ticket " + ticket.getTicketId() + " has customerId: '" + 
                                     ticketCustomerId + "' - matches: " + matches);
                    return matches;
                })
                .collect(Collectors.toList());
                
            System.out.println("TicketDAO: Found " + matchingTickets.size() + " matching tickets");
            return matchingTickets;
        } catch (Exception e) {
            System.err.println("Error finding tickets by customer ID: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Find tickets by status
     * @param status Ticket status
     * @return List of tickets with specified status
     */
    public List<Ticket> findByStatus(TicketStatus status) {
        try {
            List<Ticket> tickets = dataManager.loadTickets();
            return tickets.stream()
                .filter(ticket -> ticket.getStatus() == status)
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error finding tickets by status: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Find all tickets
     * @return List of all tickets
     */
    public List<Ticket> findAll() {
        try {
            return dataManager.loadTickets();
        } catch (Exception e) {
            System.err.println("Error loading all tickets: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Search tickets by subject or description
     * @param searchTerm Search term
     * @return List of matching tickets
     */
    public List<Ticket> searchTickets(String searchTerm) {
        try {
            List<Ticket> tickets = dataManager.loadTickets();
            String lowerSearchTerm = searchTerm.toLowerCase();
            return tickets.stream()
                .filter(ticket -> 
                    ticket.getSubject().toLowerCase().contains(lowerSearchTerm) ||
                    ticket.getDescription().toLowerCase().contains(lowerSearchTerm))
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error searching tickets: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Find tickets assigned to a specific agent
     * @param agentId Agent ID
     * @return List of assigned tickets
     */
    public List<Ticket> findByAssignedAgent(String agentId) {
        try {
            List<Ticket> tickets = dataManager.loadTickets();
            return tickets.stream()
                .filter(ticket -> ticket.getAssignedTo() != null && 
                        ticket.getAssignedTo().equals(agentId))
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error finding tickets by assigned agent: " + e.getMessage());
            return List.of();
        }
    }
} 