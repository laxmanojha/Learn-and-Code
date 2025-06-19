package backend.newsaggregation.model;

import java.util.Date;

public class NotificationLog {
    private int id;
    private int userId;
    private int newsId;
    private Date sentAt;

    public NotificationLog() {}

    public NotificationLog(int id, int userId, int newsId, Date sentAt) {
        this.id = id;
        this.userId = userId;
        this.newsId = newsId;
        this.sentAt = sentAt;
    }

	public int getId() {
		return id;
	}

	public int getUserId() {
		return userId;
	}

	public int getNewsId() {
		return newsId;
	}

	public Date getSentAt() {
		return sentAt;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setNewsId(int newsId) {
		this.newsId = newsId;
	}

	public void setSentAt(Date sentAt) {
		this.sentAt = sentAt;
	}
}
