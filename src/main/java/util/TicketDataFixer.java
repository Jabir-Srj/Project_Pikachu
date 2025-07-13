package util;

import java.time.LocalDateTime;
import java.util.List;
import model.Ticket;
import model.TicketStatus;
import model.TicketPriority;

/**
 * Utility class to fix existing ticket data issues
 */
public class TicketDataFixer {
    
    /**
     * Fix existing tickets with empty IDs
     */
    public static void fixEmptyTicketIds() {
        try {
            DataManager dataManager = new DataManager();
            List<Ticket> tickets = dataManager.loadTickets();
            boolean hasChanges = false;
            
            System.out.println("TicketDataFixer: Found " + tickets.size() + " tickets");
            
            for (int i = 0; i < tickets.size(); i++) {
                Ticket ticket = tickets.get(i);
                if (ticket.getTicketId() == null || ticket.getTicketId().trim().isEmpty()) {
                    String newId = "TKT_" + System.currentTimeMillis() + "_" + i;
                    ticket.setTicketId(newId);
                    hasChanges = true;
                    System.out.println("TicketDataFixer: Fixed ticket " + i + " with new ID: " + newId);
                }
            }
            
            if (hasChanges) {
                boolean success = dataManager.saveTickets(tickets);
                if (success) {
                    System.out.println("TicketDataFixer: Successfully saved fixed tickets");
                } else {
                    System.err.println("TicketDataFixer: Failed to save fixed tickets");
                }
            } else {
                System.out.println("TicketDataFixer: No tickets needed fixing");
            }
            
        } catch (Exception e) {
            System.err.println("TicketDataFixer: Error fixing tickets: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Main method to run the fix manually
     */
    public static void main(String[] args) {
        System.out.println("TicketDataFixer: Starting ticket data fix...");
        fixEmptyTicketIds();
        System.out.println("TicketDataFixer: Finished ticket data fix.");
    }
} 