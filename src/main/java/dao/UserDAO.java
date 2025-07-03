package dao;

import model.*;
import util.DataManager;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

/**
 * Data Access Object for User entities.
 * Handles all database operations for users.
 */
public class UserDAO {
    private DataManager dataManager;

    public UserDAO() {
        this.dataManager = new DataManager();
    }

    /**
     * Save a new user
     * @param user User to save
     * @return true if save successful
     */
    public boolean save(User user) {
        try {
            List<User> users = dataManager.loadUsers();
            users.add(user);
            return dataManager.saveUsers(users);
        } catch (Exception e) {
            System.err.println("Error saving user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update an existing user
     * @param user User to update
     * @return true if update successful
     */
    public boolean update(User user) {
        try {
            List<User> users = dataManager.loadUsers();
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getUserId().equals(user.getUserId())) {
                    users.set(i, user);
                    return dataManager.saveUsers(users);
                }
            }
            return false; // User not found
        } catch (Exception e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Find user by ID
     * @param userId User ID
     * @return User if found
     */
    public Optional<User> findById(String userId) {
        try {
            List<User> users = dataManager.loadUsers();
            return users.stream()
                       .filter(user -> user.getUserId().equals(userId))
                       .findFirst();
        } catch (Exception e) {
            System.err.println("Error finding user by ID: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Find user by username
     * @param username Username
     * @return User if found
     */
    public Optional<User> findByUsername(String username) {
        try {
            List<User> users = dataManager.loadUsers();
            return users.stream()
                       .filter(user -> user.getUsername().equals(username))
                       .findFirst();
        } catch (Exception e) {
            System.err.println("Error finding user by username: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Find user by email
     * @param email Email address
     * @return User if found
     */
    public Optional<User> findByEmail(String email) {
        try {
            List<User> users = dataManager.loadUsers();
            return users.stream()
                       .filter(user -> user.getEmail().equals(email))
                       .findFirst();
        } catch (Exception e) {
            System.err.println("Error finding user by email: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Check if username exists
     * @param username Username to check
     * @return true if exists
     */
    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    /**
     * Check if email exists
     * @param email Email to check
     * @return true if exists
     */
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    /**
     * Get all customers
     * @return List of customers
     */
    public List<Customer> findAllCustomers() {
        try {
            List<User> users = dataManager.loadUsers();
            List<Customer> customers = new ArrayList<>();
            
            for (User user : users) {
                if (user instanceof Customer) {
                    customers.add((Customer) user);
                }
            }
            
            return customers;
        } catch (Exception e) {
            System.err.println("Error finding all customers: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get all admins
     * @return List of admins
     */
    public List<Admin> findAllAdmins() {
        try {
            List<User> users = dataManager.loadUsers();
            List<Admin> admins = new ArrayList<>();
            
            for (User user : users) {
                if (user instanceof Admin) {
                    admins.add((Admin) user);
                }
            }
            
            return admins;
        } catch (Exception e) {
            System.err.println("Error finding all admins: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Delete user by ID
     * @param userId User ID
     * @return true if deletion successful
     */
    public boolean deleteById(String userId) {
        try {
            List<User> users = dataManager.loadUsers();
            boolean removed = users.removeIf(user -> user.getUserId().equals(userId));
            
            if (removed) {
                return dataManager.saveUsers(users);
            }
            
            return false; // User not found
        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all users
     * @return List of all users
     */
    public List<User> findAll() {
        try {
            return dataManager.loadUsers();
        } catch (Exception e) {
            System.err.println("Error finding all users: " + e.getMessage());
            return new ArrayList<>();
        }
    }
} 