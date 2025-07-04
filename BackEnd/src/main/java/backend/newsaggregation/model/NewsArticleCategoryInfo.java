package backend.newsaggregation.model;

import java.util.Date;

public class NewsArticleCategoryInfo {
    private int categoryId;
    private String categoryType;
    private int newsId;
    private Date createdAt;
    
    public NewsArticleCategoryInfo () {}

    public NewsArticleCategoryInfo(int categoryId, String categoryType, int newsId, Date createdAt) {
        this.categoryId = categoryId;
        this.categoryType = categoryType;
        this.newsId = newsId;
        this.createdAt = createdAt;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public int getNewsId() {
        return newsId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public void setNewsId(int newsId) {
        this.newsId = newsId;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
