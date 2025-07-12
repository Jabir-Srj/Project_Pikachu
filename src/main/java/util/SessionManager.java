package util;

import model.User;

/**
 * Session manager to handle current user session
 */
public class SessionManager {
    private static User currentUser;
    
    public static void setCurrentUser(User user) {
        currentUser = user;
    }
    
    public static User getCurrentUser() {
        return currentUser;
    }
    
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public static void logout() {
        currentUser = null;
    }
    
    public static String getCurrentUserId() {
        return currentUser != null ? currentUser.getUserId() : null;
    }
    
    public static String getCurrentUserName() {
        return currentUser != null ? currentUser.getFirstName() + " " + currentUser.getLastName() : null;
    }
}
