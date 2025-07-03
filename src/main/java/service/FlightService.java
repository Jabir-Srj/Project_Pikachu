package service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import dao.FlightDAO;
import model.Flight;
import model.FlightStatus;

/**
 * Service class for flight-related operations including search, management, and booking.
 */
public class FlightService {
    private FlightDAO flightDAO;

    public FlightService() {
        this.flightDAO = new FlightDAO();
    }

    /**
     * Search flights by origin, destination, and date
     * @param origin Origin airport code
     * @param destination Destination airport code
     * @param departureDate Departure date
     * @param passengers Number of passengers
     * @return List of available flights
     */
    public List<Flight> searchFlights(String origin, String destination, LocalDate departureDate, int passengers) {
        try {
            return flightDAO.findFlights(origin, destination, departureDate)
                .stream()
                .filter(flight -> flight.getAvailableSeats() >= passengers)
                .filter(flight -> flight.getStatus() == FlightStatus.SCHEDULED || flight.getStatus() == FlightStatus.BOARDING)
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error searching flights: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Get flight details by flight number
     * @param flightNumber Flight number
     * @return Flight details if found
     */
    public Optional<Flight> getFlightDetails(String flightNumber) {
        return flightDAO.findByFlightNumber(flightNumber);
    }

    /**
     * Get all flights (for admin/management use)
     * @return List of all flights
     */
    public List<Flight> getAllFlights() {
        return flightDAO.findAll();
    }

    /**
     * Update flight status
     * @param flightNumber Flight number
     * @param status New flight status
     * @return true if update successful
     */
    public boolean updateFlightStatus(String flightNumber, FlightStatus status) {
        try {
            Optional<Flight> flightOpt = flightDAO.findByFlightNumber(flightNumber);
            if (flightOpt.isEmpty()) {
                return false;
            }

            Flight flight = flightOpt.get();
            flight.setStatus(status);
            return flightDAO.update(flight);
        } catch (Exception e) {
            System.err.println("Error updating flight status: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check seat availability
     * @param flightNumber Flight number
     * @param requestedSeats Number of seats requested
     * @return true if seats available
     */
    public boolean checkSeatAvailability(String flightNumber, int requestedSeats) {
        Optional<Flight> flightOpt = flightDAO.findByFlightNumber(flightNumber);
        return flightOpt.map(flight -> flight.getAvailableSeats() >= requestedSeats).orElse(false);
    }

    /**
     * Reserve seats on a flight
     * @param flightNumber Flight number
     * @param numberOfSeats Number of seats to reserve
     * @return true if reservation successful
     */
    public boolean reserveSeats(String flightNumber, int numberOfSeats) {
        try {
            Optional<Flight> flightOpt = flightDAO.findByFlightNumber(flightNumber);
            if (flightOpt.isEmpty()) {
                return false;
            }

            Flight flight = flightOpt.get();
            if (flight.getAvailableSeats() < numberOfSeats) {
                return false;
            }

            flight.setAvailableSeats(flight.getAvailableSeats() - numberOfSeats);
            return flightDAO.update(flight);
        } catch (Exception e) {
            System.err.println("Error reserving seats: " + e.getMessage());
            return false;
        }
    }

    /**
     * Release seats on a flight (for cancellations)
     * @param flightNumber Flight number
     * @param numberOfSeats Number of seats to release
     * @return true if release successful
     */
    public boolean releaseSeats(String flightNumber, int numberOfSeats) {
        try {
            Optional<Flight> flightOpt = flightDAO.findByFlightNumber(flightNumber);
            if (flightOpt.isEmpty()) {
                return false;
            }

            Flight flight = flightOpt.get();
            flight.setAvailableSeats(flight.getAvailableSeats() + numberOfSeats);
            return flightDAO.update(flight);
        } catch (Exception e) {
            System.err.println("Error releasing seats: " + e.getMessage());
            return false;
        }
    }

    /**
     * Create new flight (for airline management)
     * @param flight Flight to create
     * @return true if creation successful
     */
    public boolean createFlight(Flight flight) {
        try {
            if (!validateFlightData(flight)) {
                return false;
            }

            // Check if flight number already exists
            if (flightDAO.findByFlightNumber(flight.getFlightNumber()).isPresent()) {
                return false;
            }

            flight.setFlightId(generateFlightId());
            return flightDAO.save(flight);
        } catch (Exception e) {
            System.err.println("Error creating flight: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update flight details
     * @param flight Flight with updated information
     * @return true if update successful
     */
    public boolean updateFlight(Flight flight) {
        try {
            if (flight == null || flight.getFlightId() == null) {
                return false;
            }

            return flightDAO.update(flight);
        } catch (Exception e) {
            System.err.println("Error updating flight: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete flight
     * @param flightId Flight ID
     * @return true if deletion successful
     */
    public boolean deleteFlight(String flightId) {
        return flightDAO.delete(flightId);
    }

    /**
     * Get flights by date range
     * @param startDate Start date
     * @param endDate End date
     * @return List of flights in date range
     */
    public List<Flight> getFlightsByDateRange(LocalDate startDate, LocalDate endDate) {
        return flightDAO.findByDateRange(startDate, endDate);
    }

    /**
     * Validate flight data
     * @param flight Flight to validate
     * @return true if valid
     */
    private boolean validateFlightData(Flight flight) {
        if (flight == null) return false;
        if (flight.getFlightNumber() == null || flight.getFlightNumber().trim().isEmpty()) return false;
        if (flight.getDepartureAirport() == null || flight.getDepartureAirport().trim().isEmpty()) return false;
        if (flight.getArrivalAirport() == null || flight.getArrivalAirport().trim().isEmpty()) return false;
        if (flight.getDepartureTime() == null) return false;
        if (flight.getArrivalTime() == null) return false;
        if (flight.getDepartureTime().isAfter(flight.getArrivalTime())) return false;
        if (flight.getTotalSeats() <= 0) return false;
        if (flight.getBasePrice() <= 0) return false;

        return true;
    }

    /**
     * Generate unique flight ID
     * @return Generated flight ID
     */
    private String generateFlightId() {
        return "FLT_" + System.currentTimeMillis();
    }
} 