package model;

/**
 * Author/Publisher profile with research credentials
 */
public class AuthorProfile {
    private String userId; // References User id
    private String fullName;
    private String affiliation; // University, Research Institute
    private String researchInterests;
    private String bio;
    private String orcid; // ORCID identifier
    private String website;
    private String email;
    
    public AuthorProfile() {}
    
    public AuthorProfile(String userId, String fullName) {
        this.userId = userId;
        this.fullName = fullName;
    }
    
    // Getters and Setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getAffiliation() {
        return affiliation;
    }
    
    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }
    
    public String getResearchInterests() {
        return researchInterests;
    }
    
    public void setResearchInterests(String researchInterests) {
        this.researchInterests = researchInterests;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public String getOrcid() {
        return orcid;
    }
    
    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }
    
    public String getWebsite() {
        return website;
    }
    
    public void setWebsite(String website) {
        this.website = website;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
}
