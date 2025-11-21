package model;

/**
 * Represents a scholarly reference/citation
 */
public class Reference {
    private String authors;
    private String title;
    private String source; // Journal, conference, book, etc.
    private String year;
    private String doi;
    private String url;
    private String pages;
    
    public Reference() {}
    
    public Reference(String authors, String title, String source, String year) {
        this.authors = authors;
        this.title = title;
        this.source = source;
        this.year = year;
    }
    
    // Getters and Setters
    public String getAuthors() {
        return authors;
    }
    
    public void setAuthors(String authors) {
        this.authors = authors;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getYear() {
        return year;
    }
    
    public void setYear(String year) {
        this.year = year;
    }
    
    public String getDoi() {
        return doi;
    }
    
    public void setDoi(String doi) {
        this.doi = doi;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getPages() {
        return pages;
    }
    
    public void setPages(String pages) {
        this.pages = pages;
    }
    
    /**
     * Format reference in APA style
     */
    public String toAPA() {
        StringBuilder sb = new StringBuilder();
        if (authors != null) sb.append(authors).append(". ");
        if (year != null) sb.append("(").append(year).append("). ");
        if (title != null) sb.append(title).append(". ");
        if (source != null) sb.append(source);
        if (pages != null) sb.append(", ").append(pages);
        sb.append(".");
        if (doi != null) sb.append(" https://doi.org/").append(doi);
        return sb.toString();
    }
    
    /**
     * Format reference in BibTeX
     */
    public String toBibTeX() {
        StringBuilder sb = new StringBuilder("@article{ref,\n");
        if (authors != null) sb.append("  author = {").append(authors).append("},\n");
        if (title != null) sb.append("  title = {").append(title).append("},\n");
        if (source != null) sb.append("  journal = {").append(source).append("},\n");
        if (year != null) sb.append("  year = {").append(year).append("},\n");
        if (pages != null) sb.append("  pages = {").append(pages).append("},\n");
        if (doi != null) sb.append("  doi = {").append(doi).append("},\n");
        sb.append("}");
        return sb.toString();
    }
}
