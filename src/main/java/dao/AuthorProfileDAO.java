package dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.AuthorProfile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for AuthorProfile persistence
 */
public class AuthorProfileDAO {
    private static final String PROFILES_FILE = "src/main/resources/data/author_profiles.json";
    private final ObjectMapper objectMapper;
    private List<AuthorProfile> profiles;
    
    public AuthorProfileDAO() {
        this.objectMapper = new ObjectMapper();
        this.profiles = new ArrayList<>();
        loadProfiles();
    }
    
    private void loadProfiles() {
        try {
            File file = new File(PROFILES_FILE);
            if (file.exists()) {
                profiles = objectMapper.readValue(file, new TypeReference<List<AuthorProfile>>() {});
            }
        } catch (IOException e) {
            System.err.println("Error loading author profiles: " + e.getMessage());
            profiles = new ArrayList<>();
        }
    }
    
    public void saveProfiles() {
        try {
            File file = new File(PROFILES_FILE);
            file.getParentFile().mkdirs();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, profiles);
        } catch (IOException e) {
            System.err.println("Error saving author profiles: " + e.getMessage());
        }
    }
    
    public List<AuthorProfile> getAllProfiles() {
        return new ArrayList<>(profiles);
    }
    
    public AuthorProfile getProfileByUserId(String userId) {
        return profiles.stream()
                .filter(p -> p.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }
    
    public void addProfile(AuthorProfile profile) {
        profiles.add(profile);
        saveProfiles();
    }
    
    public void updateProfile(AuthorProfile profile) {
        for (int i = 0; i < profiles.size(); i++) {
            if (profiles.get(i).getUserId().equals(profile.getUserId())) {
                profiles.set(i, profile);
                saveProfiles();
                return;
            }
        }
    }
    
    public void deleteProfile(String userId) {
        profiles.removeIf(p -> p.getUserId().equals(userId));
        saveProfiles();
    }
}
