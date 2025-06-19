package backend.newsaggregation.service;

import backend.newsaggregation.dao.interfaces.SavedArticleDao;
import backend.newsaggregation.model.NewsArticle;

import java.util.List;

public class SavedArticleService {

    private static SavedArticleService instance;
    private final SavedArticleDao savedArticleDao;

    private SavedArticleService() {
		this(SavedArticleDao.getInstance());
	}
	
	private SavedArticleService(SavedArticleDao savedArticleDao) {
        this.savedArticleDao = savedArticleDao;
    }

    public static SavedArticleService getInstance() {
        if (instance == null) {
            instance = new SavedArticleService();
        }
        return instance;
    }

    public boolean saveArticle(int userId, int articleId) {
        if (isArticleSaved(userId, articleId)) {
            return false; // Already saved
        }
        return savedArticleDao.saveArticle(userId, articleId);
    }

    public boolean deleteSavedArticle(int userId, int articleId) {
        return savedArticleDao.deleteSavedArticle(userId, articleId);
    }

    public boolean isArticleSaved(int userId, int articleId) {
        return savedArticleDao.isArticleSavedByUser(userId, articleId);
    }

    public List<NewsArticle> getSavedArticlesByUser(int userId) {
        return savedArticleDao.getSavedArticlesByUser(userId);
    }
}

