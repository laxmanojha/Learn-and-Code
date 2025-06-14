package backend.newsaggregation.dao.interfaces;

import java.util.List;

import backend.newsaggregation.model.NewsArticle;

public interface SavedArticleDao {
    boolean saveArticle(int userId, int newsId);
    boolean deleteSavedArticle(int userId, int newsId);
    List<NewsArticle> getSavedArticlesByUser(int userId);
    boolean isArticleSavedByUser(int userId, int newsId);
}
