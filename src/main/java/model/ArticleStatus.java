package model;

/**
 * Status of an article in the publishing workflow
 */
public enum ArticleStatus {
    DRAFT,           // Author is still working on it
    SUBMITTED,       // Submitted for review
    UNDER_REVIEW,    // Being reviewed by admin/editor
    REVISION_REQUESTED, // Changes requested
    APPROVED,        // Approved for publication
    PUBLISHED,       // Live and publicly accessible
    ARCHIVED         // Archived/deprecated version
}
