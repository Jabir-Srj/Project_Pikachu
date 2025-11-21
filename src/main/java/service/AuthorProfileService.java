package service;

import dao.AuthorProfileDAO;
import model.AuthorProfile;

import java.util.List;

/**
 * Service class for AuthorProfile business logic
 */
public class AuthorProfileService {
    private final AuthorProfileDAO profileDAO;
    
    public AuthorProfileService() {
        this.profileDAO = new AuthorProfileDAO();
    }
    
    /**
     * Get all author profiles
     */
    public List<AuthorProfile> getAllProfiles() {
        return profileDAO.getAllProfiles();
    }
    
    /**
     * Get profile by user ID
     */
    public AuthorProfile getProfileByUserId(String userId) {
        return profileDAO.getProfileByUserId(userId);
    }
    
    /**
     * Create or update author profile
     */
    public void saveProfile(AuthorProfile profile) {
        AuthorProfile existing = profileDAO.getProfileByUserId(profile.getUserId());
        if (existing != null) {
            profileDAO.updateProfile(profile);
        } else {
            profileDAO.addProfile(profile);
        }
    }
    
    /**
     * Delete author profile
     */
    public void deleteProfile(String userId) {
        profileDAO.deleteProfile(userId);
    }
}
