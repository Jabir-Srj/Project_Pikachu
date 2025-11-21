package model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Article represents a scholarly publication with structured content,
 * references, and metadata for academic publishing.
 */
public class Article {
    private String id;
    private String title;
    private String authorId; // Reference to User (publisher/author)
    private String abstractText;
    private List<ArticleSection> sections;
    private List<Reference> references;
    private String doi; // Digital Object Identifier or permanent link
    private ArticleStatus status;
    private int version;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime publishedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastUpdatedAt;
    
    private List<String> keywords;
    private List<String> disciplines;
    private String category; // Main research field
    
    // Constructors
    public Article() {
        this.id = UUID.randomUUID().toString();
        this.sections = new ArrayList<>();
        this.references = new ArrayList<>();
        this.keywords = new ArrayList<>();
        this.disciplines = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.lastUpdatedAt = LocalDateTime.now();
        this.status = ArticleStatus.DRAFT;
        this.version = 1;
    }
    
    public Article(String title, String authorId, String abstractText) {
        this();
        this.title = title;
        this.authorId = authorId;
        this.abstractText = abstractText;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getAuthorId() {
        return authorId;
    }
    
    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }
    
    public String getAbstractText() {
        return abstractText;
    }
    
    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }
    
    public List<ArticleSection> getSections() {
        return sections;
    }
    
    public void setSections(List<ArticleSection> sections) {
        this.sections = sections;
    }
    
    public List<Reference> getReferences() {
        return references;
    }
    
    public void setReferences(List<Reference> references) {
        this.references = references;
    }
    
    public String getDoi() {
        return doi;
    }
    
    public void setDoi(String doi) {
        this.doi = doi;
    }
    
    public ArticleStatus getStatus() {
        return status;
    }
    
    public void setStatus(ArticleStatus status) {
        this.status = status;
        if (status == ArticleStatus.PUBLISHED && this.publishedAt == null) {
            this.publishedAt = LocalDateTime.now();
        }
        this.lastUpdatedAt = LocalDateTime.now();
    }
    
    public int getVersion() {
        return version;
    }
    
    public void setVersion(int version) {
        this.version = version;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }
    
    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }
    
    public LocalDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }
    
    public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
    
    public List<String> getKeywords() {
        return keywords;
    }
    
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public List<String> getDisciplines() {
        return disciplines;
    }
    
    public void setDisciplines(List<String> disciplines) {
        this.disciplines = disciplines;
    }
    
    /**
     * Generate a permanent DOI-style identifier for this article
     */
    public void generateDOI() {
        if (this.doi == null || this.doi.isEmpty()) {
            this.doi = "10.scholarly/" + this.id;
        }
    }
    
    /**
     * Increment version and update timestamp
     */
    public void incrementVersion() {
        this.version++;
        this.lastUpdatedAt = LocalDateTime.now();
    }
}
