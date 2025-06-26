package backend.newsaggregation.model;

import java.sql.Date;

public class NotificationPref {
    private int userId;
    private int categoryId;
    private String keywords;

	private boolean isEnabled;
    private Date created_at;
    
    public NotificationPref() {}

	public NotificationPref(int userId, String keywords, boolean isEnabled, Date created_at) {
		super();
		this.userId = userId;
		this.keywords = keywords;
		this.isEnabled = isEnabled;
		this.created_at = created_at;
	}

	public int getUserId() {
		return userId;
	}

	public String getKeywords() {
		return keywords;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public int getCategoryId() {
		return categoryId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
    
	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
}
