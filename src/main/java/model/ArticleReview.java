package model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Review feedback for article submissions
 */
public class ArticleReview {
    private String id;
    private String articleId;
    private String reviewerId; // Admin who reviewed
    private String reviewerName;
    private ReviewDecision decision;
    private String comments;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime reviewedAt;
    
    public ArticleReview() {
        this.id = UUID.randomUUID().toString();
        this.reviewedAt = LocalDateTime.now();
    }
    
    public ArticleReview(String articleId, String reviewerId, String reviewerName) {
        this();
        this.articleId = articleId;
        this.reviewerId = reviewerId;
        this.reviewerName = reviewerName;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getArticleId() {
        return articleId;
    }
    
    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }
    
    public String getReviewerId() {
        return reviewerId;
    }
    
    public void setReviewerId(String reviewerId) {
        this.reviewerId = reviewerId;
    }
    
    public String getReviewerName() {
        return reviewerName;
    }
    
    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }
    
    public ReviewDecision getDecision() {
        return decision;
    }
    
    public void setDecision(ReviewDecision decision) {
        this.decision = decision;
    }
    
    public String getComments() {
        return comments;
    }
    
    public void setComments(String comments) {
        this.comments = comments;
    }
    
    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }
    
    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
}
