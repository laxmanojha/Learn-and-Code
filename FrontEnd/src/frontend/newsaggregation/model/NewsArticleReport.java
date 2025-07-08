package frontend.newsaggregation.model;

import java.util.Date;

public class NewsArticleReport {
    private int id;
    private int userId;
    private int newsId;
    private String reason;
    private Date reportedAt;
    private String newsArticle;
    private int isHidden;
    
    public NewsArticleReport() {}

    public NewsArticleReport(int id, int userId, int newsId, String reason, Date reportedAt) {
        this.id = id;
        this.userId = userId;
        this.newsId = newsId;
        this.reason = reason;
        this.reportedAt = reportedAt;
    }
    
    public NewsArticleReport(int id, int userId, int newsId, String reason, Date reportedAt, String newsArticle, int isHidden) {
    	this.id = id;
    	this.userId = userId;
    	this.newsId = newsId;
    	this.reason = reason;
    	this.reportedAt = reportedAt;
    	this.newsArticle = newsArticle;
    	this.isHidden = isHidden;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getNewsId() { return newsId; }
    public void setNewsId(int newsId) { this.newsId = newsId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Date getReportedAt() { return reportedAt; }
    public void setReportedAt(Date reportedAt) { this.reportedAt = reportedAt; }

	public String getNewsArticle() { return newsArticle; }
	public void setNewsArticle(String newsArticle) { this.newsArticle = newsArticle; }
	
	public int getIsHidden() { return isHidden; }
	public void setIsHidden(int isHidden) { this.isHidden = isHidden; }

	@Override
	public String toString() {
		return "NewsArticleReport [id=" + id + ", userId=" + userId + ", newsId=" + newsId + ", reason=" + reason
				+ ", reportedAt=" + reportedAt + ", newsArticle=" + newsArticle + ", isHidden=" + isHidden + "]";
	}
}
