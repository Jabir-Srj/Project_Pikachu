package model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Comment on an article for scholarly discussion
 */
public class ArticleComment {
    private String id;
    private String articleId;
    private String userId;
    private String userName; // Cached for display
    private String content;
    private String parentCommentId; // For threaded comments
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    private boolean flagged;
    private boolean hidden; // Hidden by moderator
    
    public ArticleComment() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.flagged = false;
        this.hidden = false;
    }
    
    public ArticleComment(String articleId, String userId, String userName, String content) {
        this();
        this.articleId = articleId;
        this.userId = userId;
        this.userName = userName;
        this.content = content;
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
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getParentCommentId() {
        return parentCommentId;
    }
    
    public void setParentCommentId(String parentCommentId) {
        this.parentCommentId = parentCommentId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isFlagged() {
        return flagged;
    }
    
    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }
    
    public boolean isHidden() {
        return hidden;
    }
    
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
    
    /**
     * Check if this is a reply to another comment
     */
    public boolean isReply() {
        return parentCommentId != null && !parentCommentId.isEmpty();
    }
}
