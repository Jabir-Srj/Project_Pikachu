package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import model.Article;
import model.ArticleStatus;
import model.User;
import model.UserRole;
import service.ArticleService;
import util.ScholarlyServiceLocator;
import util.NavigationManager;
import util.SessionManager;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for browsing published articles
 */
public class ArticleBrowseController {
    
    @FXML private TableView<Article> articlesTable;
    @FXML private TableColumn<Article, String> titleColumn;
    @FXML private TableColumn<Article, String> authorColumn;
    @FXML private TableColumn<Article, String> categoryColumn;
    @FXML private TableColumn<Article, String> publishedColumn;
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button viewArticleButton;
    @FXML private Button newArticleButton;
    @FXML private Button myArticlesButton;
    @FXML private Button dashboardButton;
    @FXML private Label welcomeLabel;
    
    private ArticleService articleService;
    private User currentUser;
    
    @FXML
    public void initialize() {
        ScholarlyServiceLocator locator = ScholarlyServiceLocator.getInstance();
        this.articleService = locator.getArticleService();
        this.currentUser = SessionManager.getCurrentUser();
        
        setupTable();
        setupButtons();
        loadArticles();
        updateWelcomeLabel();
    }
    
    /**
     * Setup table columns
     */
    private void setupTable() {
        if (titleColumn != null) {
            titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        }
        
        if (authorColumn != null) {
            authorColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty("Author " + cellData.getValue().getAuthorId().substring(0, 8))
            );
        }
        
        if (categoryColumn != null) {
            categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        }
        
        if (publishedColumn != null) {
            publishedColumn.setCellValueFactory(cellData -> {
                Article article = cellData.getValue();
                if (article.getPublishedAt() != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
                    return new SimpleStringProperty(article.getPublishedAt().format(formatter));
                }
                return new SimpleStringProperty("N/A");
            });
        }
        
        // Double-click to view article
        if (articlesTable != null) {
            articlesTable.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    handleViewArticle();
                }
            });
        }
    }
    
    /**
     * Setup buttons
     */
    private void setupButtons() {
        if (searchButton != null) {
            searchButton.setOnAction(e -> handleSearch());
        }
        
        if (viewArticleButton != null) {
            viewArticleButton.setOnAction(e -> handleViewArticle());
        }
        
        if (newArticleButton != null) {
            newArticleButton.setOnAction(e -> handleNewArticle());
            // Only show for publishers
            newArticleButton.setVisible(currentUser != null && 
                (currentUser.getRole() == UserRole.PUBLISHER || 
                 currentUser.getRole() == UserRole.AIRLINE_MANAGEMENT ||
                 currentUser.getRole() == UserRole.ADMIN));
        }
        
        if (myArticlesButton != null) {
            myArticlesButton.setOnAction(e -> handleMyArticles());
            // Only show for publishers
            myArticlesButton.setVisible(currentUser != null && 
                (currentUser.getRole() == UserRole.PUBLISHER || 
                 currentUser.getRole() == UserRole.AIRLINE_MANAGEMENT ||
                 currentUser.getRole() == UserRole.ADMIN));
        }
        
        if (dashboardButton != null) {
            dashboardButton.setOnAction(e -> handleDashboard());
            // Only show for admins
            dashboardButton.setVisible(currentUser != null && 
                currentUser.getRole() == UserRole.ADMIN);
        }
    }
    
    /**
     * Load published articles
     */
    private void loadArticles() {
        if (articlesTable != null) {
            List<Article> articles = articleService.getPublishedArticles();
            articlesTable.getItems().clear();
            articlesTable.getItems().addAll(articles);
        }
    }
    
    /**
     * Update welcome label
     */
    private void updateWelcomeLabel() {
        if (welcomeLabel != null) {
            if (currentUser != null) {
                welcomeLabel.setText("Welcome, " + currentUser.getUsername() + 
                    " (" + currentUser.getRole().getDisplayName() + ")");
            } else {
                welcomeLabel.setText("Browse Scholarly Articles (Login to comment and contribute)");
            }
        }
    }
    
    /**
     * Handle search
     */
    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadArticles();
            return;
        }
        
        List<Article> results = articleService.searchArticles(keyword);
        articlesTable.getItems().clear();
        articlesTable.getItems().addAll(results);
    }
    
    /**
     * Handle view article
     */
    @FXML
    private void handleViewArticle() {
        Article selected = articlesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Navigate to article view (would need to implement)
            showAlert("View Article", "Article: " + selected.getTitle() + "\nDOI: " + selected.getDoi());
        }
    }
    
    /**
     * Handle new article creation
     */
    @FXML
    private void handleNewArticle() {
        if (currentUser == null) {
            showAlert("Login Required", "Please login as a publisher to create articles");
            return;
        }
        
        // Navigate to article editor (would need to implement)
        showAlert("New Article", "Article editor will open here");
    }
    
    /**
     * Handle my articles
     */
    @FXML
    private void handleMyArticles() {
        if (currentUser == null) return;
        
        List<Article> myArticles = articleService.getArticlesByAuthor(currentUser.getUserId());
        articlesTable.getItems().clear();
        articlesTable.getItems().addAll(myArticles);
    }
    
    /**
     * Handle dashboard
     */
    @FXML
    private void handleDashboard() {
        if (currentUser != null && currentUser.getRole() == UserRole.ADMIN) {
            // Navigate to admin dashboard (would need to implement)
            showAlert("Admin Dashboard", "Admin review dashboard will open here");
        }
    }
    
    /**
     * Show alert dialog
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
