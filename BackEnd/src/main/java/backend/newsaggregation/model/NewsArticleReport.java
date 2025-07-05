package backend.newsaggregation.model;

import java.util.Date;

public class NewsArticleReport {
    private int id;
    private int userId;
    private int newsId;
    private String reason;
    private Date reportedAt;
    
    public NewsArticleReport() {}

    public NewsArticleReport(int id, int userId, int newsId, String reason, Date reportedAt) {
        this.id = id;
        this.userId = userId;
        this.newsId = newsId;
        this.reason = reason;
        this.reportedAt = reportedAt;
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
}
