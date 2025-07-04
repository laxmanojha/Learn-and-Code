package frontend.newsaggregation.model;

import java.util.Date;
import java.util.List;

public class NotificationPreference {

    private int id;
    private int userId;
    private String keyword;
    private boolean isEnabled;
    private Date createdAt;
    private List<String> keywords;
    private Integer categoryId;
    private String categoryType;

    public NotificationPreference() {}

    public NotificationPreference(int id, int userId, String keyword, boolean isEnabled, Date createdAt) {
        this.id = id;
        this.userId = userId;
        this.keyword = keyword;
        this.isEnabled = isEnabled;
        this.createdAt = createdAt;
        this.categoryId = null;
        this.categoryType = null;
    }

    public NotificationPreference(int id, int userId, int categoryId, String keyword,
                                  boolean isEnabled, Date createdAt, String categoryType) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.keyword = keyword;
        this.isEnabled = isEnabled;
        this.createdAt = createdAt;
        this.categoryType = categoryType;
    }

    // ✅ Getters and setters...

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
    
    public List<String> getKeywords() {
    	return keywords;
    }
    
    public void setKeywords(List<String> keywords) {
    	this.keywords = keywords;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }
}
