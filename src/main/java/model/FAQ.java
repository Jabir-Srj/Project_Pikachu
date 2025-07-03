package model;

import java.time.LocalDateTime;

/**
 * Represents a frequently asked question in the knowledge base.
 */
public class FAQ {
    private String faqId;
    private String question;
    private String answer;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private boolean isActive;
    private int viewCount;

    public FAQ() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
        this.viewCount = 0;
    }

    public FAQ(String faqId, String question, String answer, String category, String createdBy) {
        this();
        this.faqId = faqId;
        this.question = question;
        this.answer = answer;
        this.category = category;
        this.createdBy = createdBy;
    }

    /**
     * Update the FAQ content
     * @param question New question
     * @param answer New answer
     * @param updatedBy User who updated
     */
    public void updateContent(String question, String answer, String updatedBy) {
        this.question = question;
        this.answer = answer;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Increment view count when FAQ is viewed
     */
    public void incrementViewCount() {
        this.viewCount++;
    }

    /**
     * Deactivate the FAQ
     */
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Activate the FAQ
     */
    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getFaqId() { return faqId; }
    public void setFaqId(String faqId) { this.faqId = faqId; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

    @Override
    public String toString() {
        return "FAQ{" +
                "faqId='" + faqId + '\'' +
                ", question='" + question + '\'' +
                ", category='" + category + '\'' +
                ", isActive=" + isActive +
                ", viewCount=" + viewCount +
                '}';
    }
} 