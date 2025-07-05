package frontend.newsaggregation.model;

import java.util.Date;

public class NewsArticleReport {
    private int id;
    private int userId;
    private int newsId;
    private String reason;
    private Date reportedAt;

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getNewsId() { return newsId; }
    public String getReason() { return reason; }
    public Date getReportedAt() { return reportedAt; }

    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setNewsId(int newsId) { this.newsId = newsId; }
    public void setReason(String reason) { this.reason = reason; }
    public void setReportedAt(Date reportedAt) { this.reportedAt = reportedAt; }

    @Override
    public String toString() {
        return "Report ID: " + id + ", News ID: " + newsId + ", User ID: " + userId +
               ", Reason: " + reason + ", Reported At: " + reportedAt;
    }
}
