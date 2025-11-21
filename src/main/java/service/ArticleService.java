package service;

import dao.ArticleDAO;
import model.Article;
import model.ArticleStatus;

import java.util.List;

/**
 * Service class for Article business logic
 */
public class ArticleService {
    private final ArticleDAO articleDAO;
    
    public ArticleService() {
        this.articleDAO = new ArticleDAO();
    }
    
    /**
     * Get all published articles for public viewing
     */
    public List<Article> getPublishedArticles() {
        return articleDAO.getPublishedArticles();
    }
    
    /**
     * Get all articles (admin view)
     */
    public List<Article> getAllArticles() {
        return articleDAO.getAllArticles();
    }
    
    /**
     * Get article by ID
     */
    public Article getArticleById(String id) {
        return articleDAO.getArticleById(id);
    }
    
    /**
     * Get articles by author (publisher view)
     */
    public List<Article> getArticlesByAuthor(String authorId) {
        return articleDAO.getArticlesByAuthor(authorId);
    }
    
    /**
     * Get articles by status
     */
    public List<Article> getArticlesByStatus(ArticleStatus status) {
        return articleDAO.getArticlesByStatus(status);
    }
    
    /**
     * Search published articles
     */
    public List<Article> searchArticles(String keyword) {
        return articleDAO.searchArticles(keyword);
    }
    
    /**
     * Create new article draft
     */
    public Article createArticle(String title, String authorId, String abstractText) {
        Article article = new Article(title, authorId, abstractText);
        article.generateDOI();
        articleDAO.addArticle(article);
        return article;
    }
    
    /**
     * Update article
     */
    public void updateArticle(Article article) {
        article.setStatus(ArticleStatus.DRAFT); // Reset to draft on edit
        articleDAO.updateArticle(article);
    }
    
    /**
     * Submit article for review
     */
    public void submitForReview(String articleId) {
        Article article = articleDAO.getArticleById(articleId);
        if (article != null && article.getStatus() == ArticleStatus.DRAFT) {
            article.setStatus(ArticleStatus.SUBMITTED);
            articleDAO.updateArticle(article);
        }
    }
    
    /**
     * Approve article for publication (admin action)
     */
    public void approveArticle(String articleId) {
        Article article = articleDAO.getArticleById(articleId);
        if (article != null) {
            article.setStatus(ArticleStatus.PUBLISHED);
            articleDAO.updateArticle(article);
        }
    }
    
    /**
     * Request revisions (admin action)
     */
    public void requestRevisions(String articleId) {
        Article article = articleDAO.getArticleById(articleId);
        if (article != null) {
            article.setStatus(ArticleStatus.REVISION_REQUESTED);
            articleDAO.updateArticle(article);
        }
    }
    
    /**
     * Delete article (admin action)
     */
    public void deleteArticle(String articleId) {
        articleDAO.deleteArticle(articleId);
    }
    
    /**
     * Get articles pending review
     */
    public List<Article> getPendingReviewArticles() {
        return articleDAO.getArticlesByStatus(ArticleStatus.SUBMITTED);
    }
}
