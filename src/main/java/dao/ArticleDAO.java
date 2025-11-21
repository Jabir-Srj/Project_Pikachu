package dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.Article;
import model.ArticleStatus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Access Object for Article persistence
 */
public class ArticleDAO {
    private static final String ARTICLES_FILE = "src/main/resources/data/articles.json";
    private final ObjectMapper objectMapper;
    private List<Article> articles;
    
    public ArticleDAO() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.articles = new ArrayList<>();
        loadArticles();
    }
    
    /**
     * Load articles from JSON file
     */
    private void loadArticles() {
        try {
            File file = new File(ARTICLES_FILE);
            if (file.exists()) {
                articles = objectMapper.readValue(file, new TypeReference<List<Article>>() {});
            }
        } catch (IOException e) {
            System.err.println("Error loading articles: " + e.getMessage());
            articles = new ArrayList<>();
        }
    }
    
    /**
     * Save articles to JSON file
     */
    public void saveArticles() {
        try {
            File file = new File(ARTICLES_FILE);
            file.getParentFile().mkdirs();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, articles);
        } catch (IOException e) {
            System.err.println("Error saving articles: " + e.getMessage());
        }
    }
    
    /**
     * Get all articles
     */
    public List<Article> getAllArticles() {
        return new ArrayList<>(articles);
    }
    
    /**
     * Get published articles only
     */
    public List<Article> getPublishedArticles() {
        return articles.stream()
                .filter(a -> a.getStatus() == ArticleStatus.PUBLISHED)
                .collect(Collectors.toList());
    }
    
    /**
     * Get article by ID
     */
    public Article getArticleById(String id) {
        return articles.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Get articles by author
     */
    public List<Article> getArticlesByAuthor(String authorId) {
        return articles.stream()
                .filter(a -> a.getAuthorId().equals(authorId))
                .collect(Collectors.toList());
    }
    
    /**
     * Get articles by status
     */
    public List<Article> getArticlesByStatus(ArticleStatus status) {
        return articles.stream()
                .filter(a -> a.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    /**
     * Search articles by keyword in title, abstract, or keywords
     */
    public List<Article> searchArticles(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return articles.stream()
                .filter(a -> a.getStatus() == ArticleStatus.PUBLISHED)
                .filter(a -> 
                    a.getTitle().toLowerCase().contains(lowerKeyword) ||
                    a.getAbstractText().toLowerCase().contains(lowerKeyword) ||
                    a.getKeywords().stream().anyMatch(k -> k.toLowerCase().contains(lowerKeyword)))
                .collect(Collectors.toList());
    }
    
    /**
     * Add new article
     */
    public void addArticle(Article article) {
        articles.add(article);
        saveArticles();
    }
    
    /**
     * Update existing article
     */
    public void updateArticle(Article article) {
        for (int i = 0; i < articles.size(); i++) {
            if (articles.get(i).getId().equals(article.getId())) {
                articles.set(i, article);
                saveArticles();
                return;
            }
        }
    }
    
    /**
     * Delete article
     */
    public void deleteArticle(String id) {
        articles.removeIf(a -> a.getId().equals(id));
        saveArticles();
    }
}
