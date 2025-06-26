package backend.newsaggregation.service;

import backend.newsaggregation.dao.interfaces.NewsDao;
import backend.newsaggregation.dao.interfaces.SavedArticleDao;
import backend.newsaggregation.model.NewsArticle;
import backend.newsaggregation.model.NewsArticleCategoryInfo;

import java.util.List;

public class SavedArticleService {

    private static SavedArticleService instance;
    private final SavedArticleDao savedArticleDao;
    private final NewsDao newsDao;

    private SavedArticleService() {
		this(SavedArticleDao.getInstance(), NewsDao.getInstance());
	}
	
	private SavedArticleService(SavedArticleDao savedArticleDao, NewsDao newsDao) {
        this.savedArticleDao = savedArticleDao;
        this.newsDao = newsDao;
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
        List<NewsArticle> articles = savedArticleDao.getSavedArticlesByUser(userId);
        return mapCategoriesToNews(articles);
    }
    
    private List<NewsArticle> mapCategoriesToNews(List<NewsArticle> newsArticles) {
    	List<NewsArticleCategoryInfo> articleCategoryInfos = newsDao.getAllCategory();
    	for (NewsArticle newsArticle: newsArticles) {
    		for (NewsArticleCategoryInfo articleCategoryInfo: articleCategoryInfos) {
    			if (newsArticle.getId() == articleCategoryInfo.getNewsId()) {
    				newsArticle.getCategories().add(articleCategoryInfo.getCategoryType());
    			}
    		}
    	}
    	
    	return newsArticles;
    }
}

