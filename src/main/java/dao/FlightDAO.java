package dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import model.Flight;
import util.DataManager;

/**
 * Data Access Object for Flight operations
 */
public class FlightDAO {
    private DataManager dataManager;

    public FlightDAO() {
        this.dataManager = new DataManager();
    }

    /**
     * Save a new flight
     * @param flight Flight to save
     * @return true if save successful
     */
    public boolean save(Flight flight) {
        try {
            List<Flight> flights = dataManager.loadFlights();
            flights.add(flight);
            return dataManager.saveFlights(flights);
        } catch (Exception e) {
            System.err.println("Error saving flight: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update an existing flight
     * @param flight Flight to update
     * @return true if update successful
     */
    public boolean update(Flight flight) {
        try {
            List<Flight> flights = dataManager.loadFlights();
            for (int i = 0; i < flights.size(); i++) {
                if (flights.get(i).getFlightId().equals(flight.getFlightId())) {
                    flights.set(i, flight);
                    return dataManager.saveFlights(flights);
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error updating flight: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete a flight
     * @param flightId Flight ID to delete
     * @return true if delete successful
     */
    public boolean delete(String flightId) {
        try {
            List<Flight> flights = dataManager.loadFlights();
            flights.removeIf(flight -> flight.getFlightId().equals(flightId));
            return dataManager.saveFlights(flights);
        } catch (Exception e) {
            System.err.println("Error deleting flight: " + e.getMessage());
            return false;
        }
    }

    /**
     * Find flight by ID
     * @param flightId Flight ID
     * @return Flight if found
     */
    public Optional<Flight> findById(String flightId) {
        try {
            List<Flight> flights = dataManager.loadFlights();
            return flights.stream()
                .filter(flight -> flight.getFlightId().equals(flightId))
                .findFirst();
        } catch (Exception e) {
            System.err.println("Error finding flight by ID: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Find flight by flight number
     * @param flightNumber Flight number
     * @return Flight if found
     */
    public Optional<Flight> findByFlightNumber(String flightNumber) {
        try {
            List<Flight> flights = dataManager.loadFlights();
            return flights.stream()
                .filter(flight -> flight.getFlightNumber().equals(flightNumber))
                .findFirst();
        } catch (Exception e) {
            System.err.println("Error finding flight by number: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Find flights by origin, destination and date
     * @param origin Origin airport code
     * @param destination Destination airport code
     * @param departureDate Departure date
     * @return List of matching flights
     */
    public List<Flight> findFlights(String origin, String destination, LocalDate departureDate) {
        try {
            List<Flight> flights = dataManager.loadFlights();
            return flights.stream()
                .filter(flight -> flight.getDepartureAirport().equalsIgnoreCase(origin))
                .filter(flight -> flight.getArrivalAirport().equalsIgnoreCase(destination))
                .filter(flight -> flight.getDepartureTime().toLocalDate().equals(departureDate))
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error finding flights: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Find all flights
     * @return List of all flights
     */
    public List<Flight> findAll() {
        try {
            return dataManager.loadFlights();
        } catch (Exception e) {
            System.err.println("Error loading all flights: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Find flights by date range
     * @param startDate Start date
     * @param endDate End date
     * @return List of flights in date range
     */
    public List<Flight> findByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            List<Flight> flights = dataManager.loadFlights();
            return flights.stream()
                .filter(flight -> {
                    LocalDate flightDate = flight.getDepartureTime().toLocalDate();
                    return !flightDate.isBefore(startDate) && !flightDate.isAfter(endDate);
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error finding flights by date range: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Find flights by origin
     * @param origin Origin airport code
     * @return List of flights from origin
     */
    public List<Flight> findByOrigin(String origin) {
        try {
            List<Flight> flights = dataManager.loadFlights();
            return flights.stream()
                .filter(flight -> flight.getDepartureAirport().equalsIgnoreCase(origin))
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error finding flights by origin: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Find flights by destination
     * @param destination Destination airport code
     * @return List of flights to destination
     */
    public List<Flight> findByDestination(String destination) {
        try {
            List<Flight> flights = dataManager.loadFlights();
            return flights.stream()
                .filter(flight -> flight.getArrivalAirport().equalsIgnoreCase(destination))
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error finding flights by destination: " + e.getMessage());
            return List.of();
        }
    }
} 