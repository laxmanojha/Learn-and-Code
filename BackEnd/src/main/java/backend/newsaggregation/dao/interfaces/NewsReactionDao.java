package backend.newsaggregation.dao.interfaces;

import java.util.List;

import backend.newsaggregation.dao.impl.NewsReactionDaoImpl;

public interface NewsReactionDao {

	public static NewsReactionDao getInstance() {
        return NewsReactionDaoImpl.getInstance();
    }

	public boolean reactToArticle(int userId, int newsId, String reactionType);
	public List<Integer> getLikedNewsIds(int userId);
}
