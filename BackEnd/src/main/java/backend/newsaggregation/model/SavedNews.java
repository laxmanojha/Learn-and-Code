package backend.newsaggregation.model;

public class SavedNews {
    private int userId;
    private int newsId;

    public SavedNews() {}

    public SavedNews(int userId, int newsId) {
        this.userId = userId;
        this.newsId = newsId;
    }

	public int getUserId() {
		return userId;
	}

	public int getNewsId() {
		return newsId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setNewsId(int newsId) {
		this.newsId = newsId;
	}
}
