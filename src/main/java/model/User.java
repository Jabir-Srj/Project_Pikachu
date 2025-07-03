package model;

import java.time.LocalDateTime;

/**
 * Abstract base class for all users in the airline ticketing system.
 * Provides common attributes and methods shared by Customer, Admin, and
 * AirlineManagement.
 */
public abstract class User {
    private String userId;
    private String username;
    private String password; // Will be hashed for security
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private boolean isActive;

    public User() {
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    public User(String userId, String username, String password, String email,
            String firstName, String lastName, String phoneNumber, UserRole role) {
        this();
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    // Abstract method to be implemented by subclasses
    public abstract void updateProfile();

    /**
     * Authenticate user with provided password
     * 
     * @param password The password to verify
     * @return true if authentication successful
     */
    public boolean authenticate(String password) {
        // In a real implementation, this would hash the input and compare
        return this.password.equals(password);
    }

    /**
     * Update last login timestamp
     */
    public void recordLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role=" + role +
                '}';
    }
}