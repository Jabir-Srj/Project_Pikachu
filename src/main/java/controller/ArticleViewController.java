package controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import model.*;
import service.ArticleService;
import service.CommentService;
import util.ScholarlyServiceLocator;
import util.NavigationManager;
import util.SessionManager;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for displaying a scholarly article with comments
 */
public class ArticleViewController {
    
    @FXML private ScrollPane articleScrollPane;
    @FXML private VBox articleContainer;
    @FXML private Label titleLabel;
    @FXML private Label authorLabel;
    @FXML private Label metadataLabel;
    @FXML private Label doiLabel;
    @FXML private TextArea abstractArea;
    @FXML private VBox sectionsContainer;
    @FXML private VBox referencesContainer;
    @FXML private VBox commentsContainer;
    @FXML private TextArea commentInput;
    @FXML private Button submitCommentButton;
    @FXML private Button backButton;
    @FXML private Button exportCitationButton;
    
    private ArticleService articleService;
    private CommentService commentService;
    private Article currentArticle;
    private User currentUser;
    
    @FXML
    public void initialize() {
        ScholarlyServiceLocator locator = ScholarlyServiceLocator.getInstance();
        this.articleService = locator.getArticleService();
        this.commentService = locator.getCommentService();
        this.currentUser = SessionManager.getCurrentUser();
        
        // Setup UI based on login status
        setupUI();
    }
    
    /**
     * Setup UI elements based on user login status
     */
    private void setupUI() {
        if (currentUser == null) {
            // Not logged in - hide comment input
            if (commentInput != null) commentInput.setVisible(false);
            if (submitCommentButton != null) submitCommentButton.setVisible(false);
        } else {
            // Logged in - enable commenting
            if (submitCommentButton != null) {
                submitCommentButton.setOnAction(e -> handleSubmitComment());
            }
        }
        
        if (backButton != null) {
            backButton.setOnAction(e -> handleBack());
        }
        
        if (exportCitationButton != null) {
            exportCitationButton.setOnAction(e -> handleExportCitation());
        }
    }
    
    /**
     * Load and display an article
     */
    public void loadArticle(String articleId) {
        currentArticle = articleService.getArticleById(articleId);
        if (currentArticle != null) {
            displayArticle();
            loadComments();
        }
    }
    
    /**
     * Display article content
     */
    private void displayArticle() {
        // Title
        if (titleLabel != null) {
            titleLabel.setText(currentArticle.getTitle());
            titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        }
        
        // Author
        if (authorLabel != null) {
            authorLabel.setText("By: " + getAuthorName(currentArticle.getAuthorId()));
            authorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        }
        
        // Metadata
        if (metadataLabel != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            String publishedDate = currentArticle.getPublishedAt() != null ? 
                currentArticle.getPublishedAt().format(formatter) : "Draft";
            String metadata = String.format("Published: %s | Version: %d | Category: %s",
                publishedDate, currentArticle.getVersion(), currentArticle.getCategory());
            metadataLabel.setText(metadata);
        }
        
        // DOI
        if (doiLabel != null && currentArticle.getDoi() != null) {
            doiLabel.setText("DOI: " + currentArticle.getDoi());
            doiLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #0066cc;");
        }
        
        // Abstract
        if (abstractArea != null) {
            abstractArea.setText(currentArticle.getAbstractText());
            abstractArea.setEditable(false);
            abstractArea.setWrapText(true);
        }
        
        // Sections
        if (sectionsContainer != null) {
            sectionsContainer.getChildren().clear();
            for (ArticleSection section : currentArticle.getSections()) {
                VBox sectionBox = createSectionBox(section);
                sectionsContainer.getChildren().add(sectionBox);
            }
        }
        
        // References
        if (referencesContainer != null) {
            referencesContainer.getChildren().clear();
            Label refHeader = new Label("References");
            refHeader.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 10 0 10 0;");
            referencesContainer.getChildren().add(refHeader);
            
            int refNum = 1;
            for (Reference ref : currentArticle.getReferences()) {
                Label refLabel = new Label(refNum + ". " + ref.toAPA());
                refLabel.setWrapText(true);
                refLabel.setStyle("-fx-padding: 5 0 5 20;");
                referencesContainer.getChildren().add(refLabel);
                refNum++;
            }
        }
    }
    
    /**
     * Create a section display box
     */
    private VBox createSectionBox(ArticleSection section) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15, 0, 15, 0));
        
        Label titleLabel = new Label(section.getTitle());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        TextArea contentArea = new TextArea(section.getContent());
        contentArea.setEditable(false);
        contentArea.setWrapText(true);
        contentArea.setPrefRowCount(10);
        
        box.getChildren().addAll(titleLabel, contentArea);
        return box;
    }
    
    /**
     * Load and display comments
     */
    private void loadComments() {
        if (commentsContainer != null && currentArticle != null) {
            commentsContainer.getChildren().clear();
            
            Label header = new Label("Scholarly Discussion");
            header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 20 0 10 0;");
            commentsContainer.getChildren().add(header);
            
            List<ArticleComment> comments = commentService.getArticleComments(currentArticle.getId());
            for (ArticleComment comment : comments) {
                if (!comment.isReply()) {
                    VBox commentBox = createCommentBox(comment);
                    commentsContainer.getChildren().add(commentBox);
                    
                    // Load replies
                    List<ArticleComment> replies = commentService.getReplies(comment.getId());
                    for (ArticleComment reply : replies) {
                        VBox replyBox = createCommentBox(reply);
                        replyBox.setStyle(replyBox.getStyle() + "-fx-padding: 10 0 10 40;");
                        commentsContainer.getChildren().add(replyBox);
                    }
                }
            }
        }
    }
    
    /**
     * Create a comment display box
     */
    private VBox createCommentBox(ArticleComment comment) {
        VBox box = new VBox(5);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 5;");
        
        Label userLabel = new Label(comment.getUserName());
        userLabel.setStyle("-fx-font-weight: bold;");
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        Label dateLabel = new Label(comment.getCreatedAt().format(formatter));
        dateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        
        TextArea contentArea = new TextArea(comment.getContent());
        contentArea.setEditable(false);
        contentArea.setWrapText(true);
        contentArea.setPrefRowCount(3);
        
        box.getChildren().addAll(userLabel, dateLabel, contentArea);
        return box;
    }
    
    /**
     * Handle comment submission
     */
    @FXML
    private void handleSubmitComment() {
        if (currentUser == null) {
            showAlert("Login Required", "Please login to comment");
            return;
        }
        
        String content = commentInput.getText().trim();
        if (content.isEmpty()) {
            showAlert("Empty Comment", "Please enter your comment");
            return;
        }
        
        commentService.addComment(
            currentArticle.getId(),
            currentUser.getUserId(),
            currentUser.getUsername(),
            content
        );
        
        commentInput.clear();
        loadComments();
        showAlert("Success", "Comment posted successfully");
    }
    
    /**
     * Handle export citation
     */
    @FXML
    private void handleExportCitation() {
        if (currentArticle == null) return;
        
        // Create a dialog with citation formats
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export Citation");
        alert.setHeaderText("Citation for: " + currentArticle.getTitle());
        
        String citation = String.format(
            "APA Format:\n%s. (%s). %s. DOI: %s\n\n",
            getAuthorName(currentArticle.getAuthorId()),
            currentArticle.getPublishedAt() != null ? 
                currentArticle.getPublishedAt().getYear() : "n.d.",
            currentArticle.getTitle(),
            currentArticle.getDoi()
        );
        
        alert.setContentText(citation);
        alert.showAndWait();
    }
    
    /**
     * Get author name from ID
     */
    private String getAuthorName(String authorId) {
        // Try to get from author profile, fallback to "Unknown Author"
        return "Author " + authorId.substring(0, Math.min(8, authorId.length()));
    }
    
    /**
     * Handle back navigation
     */
    @FXML
    private void handleBack() {
        // Navigate back to article browse
        NavigationManager.getInstance().navigateTo("ArticleBrowse.fxml");
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
