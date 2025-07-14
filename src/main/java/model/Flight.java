package model;

import java.time.LocalDateTime;

/**
 * Represents a flight with departure/arrival details and pricing.
 */
public class Flight {
    private String flightId;
    private String flightNumber;
    private String airline;
    private String departureAirport;
    private String arrivalAirport;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private double basePrice;
    private double economyPrice;
    private double businessPrice;
    private int totalSeats;
    private int availableSeats;
    private FlightStatus status;
    private String aircraft;
    private double duration; // Duration in hours
    private String notes; // Flight notes for admin use

    public Flight() {
        this.status = FlightStatus.SCHEDULED;
    }

    public Flight(String flightId, String flightNumber, String airline,
                  String departureAirport, String arrivalAirport,
                  LocalDateTime departureTime, LocalDateTime arrivalTime,
                  double basePrice, int totalSeats) {
        this();
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.airline = airline;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.basePrice = basePrice;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
        calculateDuration();
    }

    /**
     * Calculate flight duration in hours
     */
    private void calculateDuration() {
        if (departureTime != null && arrivalTime != null) {
            long minutes = java.time.Duration.between(departureTime, arrivalTime).toMinutes();
            this.duration = minutes / 60.0;
        }
    }

    /**
     * Book seats on this flight
     * @param seatCount Number of seats to book
     * @return true if booking successful
     */
    public boolean bookSeats(int seatCount) {
        if (seatCount <= 0 || seatCount > availableSeats) {
            return false;
        }
        availableSeats -= seatCount;
        return true;
    }

    /**
     * Cancel seat bookings
     * @param seatCount Number of seats to cancel
     */
    public void cancelSeats(int seatCount) {
        if (seatCount > 0) {
            availableSeats = Math.min(totalSeats, availableSeats + seatCount);
        }
    }

    /**
     * Check if flight has available seats
     * @param requiredSeats Number of seats needed
     * @return true if seats available
     */
    public boolean hasAvailableSeats(int requiredSeats) {
        return availableSeats >= requiredSeats;
    }

    /**
     * Get formatted duration string
     * @return Duration in "Xh Ym" format
     */
    public String getFormattedDuration() {
        int hours = (int) duration;
        int minutes = (int) ((duration - hours) * 60);
        return hours + "h " + minutes + "m";
    }

    // Getters and Setters
    public String getFlightId() { return flightId; }
    public void setFlightId(String flightId) { this.flightId = flightId; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public String getAirline() { return airline; }
    public void setAirline(String airline) { this.airline = airline; }

    public String getDepartureAirport() { return departureAirport; }
    public void setDepartureAirport(String departureAirport) { this.departureAirport = departureAirport; }

    public String getArrivalAirport() { return arrivalAirport; }
    public void setArrivalAirport(String arrivalAirport) { this.arrivalAirport = arrivalAirport; }

    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { 
        this.departureTime = departureTime;
        calculateDuration();
    }

    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { 
        this.arrivalTime = arrivalTime;
        calculateDuration();
    }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }

    public double getEconomyPrice() { return economyPrice; }
    public void setEconomyPrice(double economyPrice) { this.economyPrice = economyPrice; }

    public double getBusinessPrice() { return businessPrice; }
    public void setBusinessPrice(double businessPrice) { this.businessPrice = businessPrice; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    public FlightStatus getStatus() { return status; }
    public void setStatus(FlightStatus status) { this.status = status; }

    public String getAircraft() { return aircraft; }
    public void setAircraft(String aircraft) { this.aircraft = aircraft; }

    public double getDuration() { return duration; }
    public void setDuration(double duration) { this.duration = duration; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "Flight{" +
                "flightNumber='" + flightNumber + '\'' +
                ", airline='" + airline + '\'' +
                ", route='" + departureAirport + " -> " + arrivalAirport + '\'' +
                ", price=" + basePrice +
                ", availableSeats=" + availableSeats +
                ", status=" + status +
                '}';
    }
}

 