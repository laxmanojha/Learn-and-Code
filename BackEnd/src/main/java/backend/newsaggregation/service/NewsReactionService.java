package backend.newsaggregation.service;

import backend.newsaggregation.dao.interfaces.CategoryDao;
import backend.newsaggregation.dao.interfaces.NewsReactionDao;

public class NewsReactionService {
	
    private NewsReactionDao reactionDao;
    private static NewsReactionService instance;
	
	private NewsReactionService() {
		this(NewsReactionDao.getInstance());
	}
	
	private NewsReactionService(NewsReactionDao reactionDao) {
        this.reactionDao = reactionDao;
    }

    public static NewsReactionService getInstance() {
        if (instance == null) {
            instance = new NewsReactionService();
        }
        return instance;
    }

    public boolean reactToArticle(int userId, int newsId, String reactionType) {
        if (!reactionType.equalsIgnoreCase("like") && !reactionType.equalsIgnoreCase("dislike")) {
            throw new IllegalArgumentException("Invalid reaction type. Only 'like' or 'dislike' allowed.");
        }
        return reactionDao.reactToArticle(userId, newsId, reactionType.toLowerCase());
    }
}
