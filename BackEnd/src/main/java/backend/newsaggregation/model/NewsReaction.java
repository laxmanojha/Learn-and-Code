package backend.newsaggregation.model;

import java.util.Date;

public class NewsReaction {
    private int id;
    private int userId;
    private int newsId;
    private String reactionType;  // "like" or "dislike"
    private Date reactedAt;

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

    public int getNewsId() {
        return newsId;
    }
    public void setNewsId(int newsId) {
        this.newsId = newsId;
    }

    public String getReactionType() {
        return reactionType;
    }
    public void setReactionType(String reactionType) {
        this.reactionType = reactionType;
    }

    public Date getReactedAt() {
        return reactedAt;
    }
    public void setReactedAt(Date reactedAt) {
        this.reactedAt = reactedAt;
    }
}
