package backend.newsaggregation.model;

public class NewsStats {
    private int newsId;
    private int userId;
    private boolean isLiked;

    public NewsStats() {}

    public NewsStats(int newsId, int userId, boolean isLiked) {
        this.newsId = newsId;
        this.userId = userId;
        this.isLiked = isLiked;
    }

	public int getNewsId() {
		return newsId;
	}

	public int getUserId() {
		return userId;
	}

	public boolean isLiked() {
		return isLiked;
	}

	public void setNewsId(int newsId) {
		this.newsId = newsId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setLiked(boolean isLiked) {
		this.isLiked = isLiked;
	}
}
