package backend.newsaggregation.model;

import java.sql.Timestamp;

public class Notification {
	private final int id;
    private final int userId;
    private final int newsId;
    private final Timestamp time;

    public Notification(int id, int userId, int newsId, Timestamp time) {
        this.id = id;
        this.userId = userId;
        this.newsId = newsId;
        this.time = time;
    }

    public Notification(int userId, int newsId, Timestamp time) {
        this(0, userId, newsId, time);
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

    public Timestamp getTime() {
        return time;
    }
}
