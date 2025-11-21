package dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.ArticleComment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Access Object for ArticleComment persistence
 */
public class CommentDAO {
    private static final String COMMENTS_FILE = "src/main/resources/data/comments.json";
    private final ObjectMapper objectMapper;
    private List<ArticleComment> comments;
    
    public CommentDAO() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.comments = new ArrayList<>();
        loadComments();
    }
    
    private void loadComments() {
        try {
            File file = new File(COMMENTS_FILE);
            if (file.exists()) {
                comments = objectMapper.readValue(file, new TypeReference<List<ArticleComment>>() {});
            }
        } catch (IOException e) {
            System.err.println("Error loading comments: " + e.getMessage());
            comments = new ArrayList<>();
        }
    }
    
    public void saveComments() {
        try {
            File file = new File(COMMENTS_FILE);
            file.getParentFile().mkdirs();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, comments);
        } catch (IOException e) {
            System.err.println("Error saving comments: " + e.getMessage());
        }
    }
    
    public List<ArticleComment> getAllComments() {
        return new ArrayList<>(comments);
    }
    
    public List<ArticleComment> getCommentsByArticle(String articleId) {
        return comments.stream()
                .filter(c -> c.getArticleId().equals(articleId) && !c.isHidden())
                .collect(Collectors.toList());
    }
    
    public List<ArticleComment> getReplies(String parentCommentId) {
        return comments.stream()
                .filter(c -> parentCommentId.equals(c.getParentCommentId()) && !c.isHidden())
                .collect(Collectors.toList());
    }
    
    public ArticleComment getCommentById(String id) {
        return comments.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    public void addComment(ArticleComment comment) {
        comments.add(comment);
        saveComments();
    }
    
    public void updateComment(ArticleComment comment) {
        for (int i = 0; i < comments.size(); i++) {
            if (comments.get(i).getId().equals(comment.getId())) {
                comments.set(i, comment);
                saveComments();
                return;
            }
        }
    }
    
    public void deleteComment(String id) {
        comments.removeIf(c -> c.getId().equals(id));
        saveComments();
    }
}
