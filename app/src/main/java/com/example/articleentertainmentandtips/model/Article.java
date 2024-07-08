package com.example.articleentertainmentandtips.model;

public class Article {
    private String UID;
    private String ArticleCategory;
    private String ArticleContent;
    private String ArticleImage;
    private String ArticleTitle;
    private String CreatedAt;

    // Diperlukan untuk Firebase
    public Article() {}

    public Article(String UID, String ArticleCategory, String ArticleContent, String ArticleImage, String ArticleTitle, String CreatedAt) {
        this.UID = UID;
        this.ArticleCategory = ArticleCategory;
        this.ArticleContent = ArticleContent;
        this.ArticleImage = ArticleImage;
        this.ArticleTitle = ArticleTitle;
        this.CreatedAt = CreatedAt;
    }

    // Getter and Setter methods
    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
        System.out.println("Setting UID: " + UID); // Debug log
    }

    public String getArticleCategory() {
        return ArticleCategory;
    }

    public void setArticleCategory(String articleCategory) {
        this.ArticleCategory = articleCategory;
    }

    public String getArticleContent() {
        return ArticleContent;
    }

    public void setArticleContent(String articleContent) {
        this.ArticleContent = articleContent;
    }

    public String getArticleImage() {
        return ArticleImage;
    }

    public void setArticleImage(String articleImage) {
        this.ArticleImage = articleImage;
    }

    public String getArticleTitle() {
        return ArticleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.ArticleTitle = articleTitle;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        this.CreatedAt = createdAt;
    }
}
