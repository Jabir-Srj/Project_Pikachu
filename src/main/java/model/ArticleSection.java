package model;

/**
 * Represents a section within an article (e.g., Introduction, Methodology, Results)
 */
public class ArticleSection {
    private String title;
    private String content;
    private int order;
    
    public ArticleSection() {}
    
    public ArticleSection(String title, String content, int order) {
        this.title = title;
        this.content = content;
        this.order = order;
    }
    
    // Getters and Setters
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public int getOrder() {
        return order;
    }
    
    public void setOrder(int order) {
        this.order = order;
    }
}
