package service;

import dao.CommentDAO;
import model.ArticleComment;

import java.util.List;

/**
 * Service class for ArticleComment business logic
 */
public class CommentService {
    private final CommentDAO commentDAO;
    
    public CommentService() {
        this.commentDAO = new CommentDAO();
    }
    
    /**
     * Get all comments for an article (not hidden)
     */
    public List<ArticleComment> getArticleComments(String articleId) {
        return commentDAO.getCommentsByArticle(articleId);
    }
    
    /**
     * Get replies to a comment
     */
    public List<ArticleComment> getReplies(String parentCommentId) {
        return commentDAO.getReplies(parentCommentId);
    }
    
    /**
     * Add a new comment
     */
    public ArticleComment addComment(String articleId, String userId, String userName, String content) {
        ArticleComment comment = new ArticleComment(articleId, userId, userName, content);
        commentDAO.addComment(comment);
        return comment;
    }
    
    /**
     * Add a reply to a comment
     */
    public ArticleComment addReply(String articleId, String userId, String userName, 
                                   String content, String parentCommentId) {
        ArticleComment comment = new ArticleComment(articleId, userId, userName, content);
        comment.setParentCommentId(parentCommentId);
        commentDAO.addComment(comment);
        return comment;
    }
    
    /**
     * Flag a comment for moderation
     */
    public void flagComment(String commentId) {
        ArticleComment comment = commentDAO.getCommentById(commentId);
        if (comment != null) {
            comment.setFlagged(true);
            commentDAO.updateComment(comment);
        }
    }
    
    /**
     * Hide a comment (admin action)
     */
    public void hideComment(String commentId) {
        ArticleComment comment = commentDAO.getCommentById(commentId);
        if (comment != null) {
            comment.setHidden(true);
            commentDAO.updateComment(comment);
        }
    }
    
    /**
     * Unhide a comment (admin action)
     */
    public void unhideComment(String commentId) {
        ArticleComment comment = commentDAO.getCommentById(commentId);
        if (comment != null) {
            comment.setHidden(false);
            commentDAO.updateComment(comment);
        }
    }
    
    /**
     * Delete a comment
     */
    public void deleteComment(String commentId) {
        commentDAO.deleteComment(commentId);
    }
}
