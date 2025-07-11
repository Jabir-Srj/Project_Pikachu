package service;

import java.util.List;
import java.util.Optional;

import dao.UserDAO;
import model.Customer;
import model.User;

/**
 * Service class for user-related operations including registration, login, and profile management.
 */
public class UserService {
    private UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Authenticate user with username and password (used by AirlineApp)
     * @param username Username
     * @param password Password
     * @return User object if authentication successful, null otherwise
     */
    public User authenticate(String username, String password) {
        return loginUser(username, password);
    }

    /**
     * Register a new user
     * @param user The user to register
     * @return true if registration successful
     */
    public boolean registerUser(User user) {
        try {
            // Validate user data
            if (!validateUserData(user)) {
                return false;
            }

            // Check if username or email already exists
            if (userDAO.existsByUsername(user.getUsername()) || 
                userDAO.existsByEmail(user.getEmail())) {
                return false;
            }

            // Hash password (simplified - in real implementation use proper hashing)
            user.setPassword(hashPassword(user.getPassword()));

            // Generate user ID
            user.setUserId(generateUserId());

            // Save user
            return userDAO.save(user);
        } catch (Exception e) {
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Authenticate user login
     * @param username Username or email
     * @param password Password
     * @return User object if login successful, null otherwise
     */
    public User loginUser(String username, String password) {
        try {
            // Find user by username or email
            Optional<User> userOpt = userDAO.findByUsername(username);
            if (userOpt.isEmpty()) {
                userOpt = userDAO.findByEmail(username);
            }

            if (userOpt.isEmpty()) {
                return null;
            }

            User user = userOpt.get();

            // Verify password
            if (verifyPassword(password, user.getPassword())) {
                user.recordLogin();
                userDAO.update(user);
                return user;
            }

            return null;
        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            return null;
        }
    }

    /**
     * Update user profile
     * @param user The user with updated information
     * @return true if update successful
     */
    public boolean updateUserProfile(User user) {
        try {
            if (user == null || user.getUserId() == null) {
                return false;
            }

            // Validate updated data
            if (!validateUserData(user)) {
                return false;
            }

            // Update the user
            user.updateProfile();
            return userDAO.update(user);
        } catch (Exception e) {
            System.err.println("Error updating user profile: " + e.getMessage());
            return false;
        }
    }

    /**
     * Change user password
     * @param userId User ID
     * @param oldPassword Current password
     * @param newPassword New password
     * @return true if password changed successfully
     */
    public boolean changePassword(String userId, String oldPassword, String newPassword) {
        try {
            Optional<User> userOpt = userDAO.findById(userId);
            if (userOpt.isEmpty()) {
                return false;
            }

            User user = userOpt.get();

            // Verify old password
            if (!verifyPassword(oldPassword, user.getPassword())) {
                return false;
            }

            // Set new password
            user.setPassword(hashPassword(newPassword));
            return userDAO.update(user);
        } catch (Exception e) {
            System.err.println("Error changing password: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all customers (for admin use)
     * @return List of customers
     */
    public List<Customer> getAllCustomers() {
        return userDAO.findAllCustomers();
    }

    /**
     * Find user by ID
     * @param userId User ID
     * @return User if found
     */
    public Optional<User> findUserById(String userId) {
        return userDAO.findById(userId);
    }

    /**
     * Deactivate user account
     * @param userId User ID
     * @return true if deactivation successful
     */
    public boolean deactivateUser(String userId) {
        try {
            Optional<User> userOpt = userDAO.findById(userId);
            if (userOpt.isEmpty()) {
                return false;
            }

            User user = userOpt.get();
            user.setActive(false);
            return userDAO.update(user);
        } catch (Exception e) {
            System.err.println("Error deactivating user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Validate user data
     * @param user User to validate
     * @return true if valid
     */
    private boolean validateUserData(User user) {
        if (user == null) return false;
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) return false;
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) return false;
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) return false;
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) return false;
        if (user.getPassword() == null || user.getPassword().length() < 6) return false;

        // Validate email format (simple validation)
        return user.getEmail().contains("@") && user.getEmail().contains(".");
    }

    /**
     * Hash password (simplified - in real implementation use BCrypt or similar)
     * @param password Plain text password
     * @return Hashed password
     */
    private String hashPassword(String password) {
        // Simplified hashing - in real implementation use proper password hashing
        return "HASH_" + password.hashCode();
    }

    /**
     * Verify password against hash
     * @param password Plain text password
     * @param hashedPassword Hashed password
     * @return true if passwords match
     */
    private boolean verifyPassword(String password, String hashedPassword) {
        return hashPassword(password).equals(hashedPassword);
    }

    /**
     * Generate unique user ID
     * @return Generated user ID
     */
    private String generateUserId() {
        return "USER_" + System.currentTimeMillis();
    }
} 