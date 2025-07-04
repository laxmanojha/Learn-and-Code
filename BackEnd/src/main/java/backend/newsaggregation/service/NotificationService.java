package backend.newsaggregation.service;
import backend.newsaggregation.model.Category;
import backend.newsaggregation.model.NewsArticle;
import backend.newsaggregation.model.NewsArticleCategoryInfo;
import backend.newsaggregation.model.NotificationPref;
import backend.newsaggregation.model.NotificationPreference;
import backend.newsaggregation.dao.impl.CategoryDaoImpl;
import backend.newsaggregation.dao.impl.NotificationDaoImpl;
import backend.newsaggregation.dao.interfaces.CategoryDao;
import backend.newsaggregation.dao.interfaces.NewsDao;
import backend.newsaggregation.dao.interfaces.NotificationCategoryPrefDao;
import backend.newsaggregation.dao.interfaces.NotificationDao;
import backend.newsaggregation.dao.interfaces.NotificationKeywordPrefDao;
import backend.newsaggregation.dao.interfaces.UserDao;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NotificationService {

    private static NotificationService instance;
    private final UserDao userDao;
    private final CategoryDao categoryDao;
    private final NotificationCategoryPrefDao notificationCategoryPrefDao;
    private final NotificationKeywordPrefDao notificationKeywordPrefDao;
    private final NotificationDao notificationDao;
    private final NewsDao newsDao;

    private NotificationService() {
		this(UserDao.getInstance(), CategoryDao.getInstance(), NotificationCategoryPrefDao.getInstance(), NotificationKeywordPrefDao.getInstance(), NotificationDao.getInstance(), NewsDao.getInstance());
	}
	
	private NotificationService(UserDao userDao, CategoryDao categoryDao, NotificationCategoryPrefDao notificationCategoryPrefDao, NotificationKeywordPrefDao notificationKeywordPrefDao, NotificationDao notificationDao, NewsDao newsDao) {
        this.userDao = userDao;
		this.categoryDao = categoryDao;
		this.notificationCategoryPrefDao = notificationCategoryPrefDao;
        this.notificationKeywordPrefDao = notificationKeywordPrefDao;
        this.notificationDao = notificationDao;
        this.newsDao = newsDao;
    }

    public static NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }
    
    public List<NewsArticle> getConsoleNotifications(int userId){
    	Timestamp lastViewed = userDao.getNotificationViewedTime(userId);
        Timestamp now = Timestamp.from(Instant.now());
        List<NewsArticle> newsArticles = notificationDao.getNewsForConsoleNotification(userId, lastViewed, now);
        userDao.saveNotificationViewedTime(userId, now);
        for (NewsArticle newsArticle: newsArticles) {
        	newsArticle = mapCategoriesToNews(newsArticle);
        }
        return filterUniqueById(newsArticles);
    }

    public List<NotificationPreference> getAllPreferences(int userId) {
        List<NotificationPreference> categoryPreference = notificationCategoryPrefDao.getCategoryPreferencesByUser(userId);
        if (categoryPreference.isEmpty()) {
        	List<Category> categories = categoryDao.getAllCategory();
        	categoryPreference = createListOfCategoryPreference(categories, categoryPreference);
        }
        NotificationPreference keywordPreference = notificationKeywordPrefDao.getPreferencesByUser(userId);
        categoryPreference.add(keywordPreference);
        return categoryPreference;
    }
    
    private List<NotificationPreference> createListOfCategoryPreference(List<Category> categories, List<NotificationPreference> categoryPreference) {
    	
    	for (Category category : categories) {
    		NotificationPreference notificationPreference = new NotificationPreference();
    		notificationPreference.setCategoryId(category.getId());
    		notificationPreference.setCategoryType(category.getName());
    		notificationPreference.setEnabled(false);
    		notificationPreference.setKeywords(null);
    		categoryPreference.add(notificationPreference);
    	}
    	return categoryPreference;
    }

    public boolean updateCategoryConfig(int userId, int categoryId, List<String> keywords) {
    	return notificationCategoryPrefDao.addCategoryPreference(userId, categoryId, keywords);
    }
    
    public boolean updateCategoryConfig(int userId, int categoryId) {
    	return notificationCategoryPrefDao.removeCategoryPreference(userId, categoryId);
    }

    public boolean addKeywords(int userId, List<String> keywords) {
        return notificationKeywordPrefDao.addKeywordPreference(userId, keywords);
    }
    
    public boolean removeKeywords(int userId) {
    	return notificationKeywordPrefDao.removeAllKeywordPreference(userId);
    }

    public boolean removeKeywords(int userId, List<String> keywords) {
        return notificationKeywordPrefDao.removeKeywordPreference(userId, keywords);
    }
    
    public List<NotificationPreference> getCategoryPreferences(int userId) {
    	return notificationCategoryPrefDao.getCategoryPreferencesByUser(userId);
    }

    public NotificationPreference getKeywordPreferences(int userId) {
        return notificationKeywordPrefDao.getPreferencesByUser(userId);
    }

    public boolean updateKeywordStatus(int userId, String keyword, boolean enabled) {
    	List<String> keywords = null;
        return notificationKeywordPrefDao.updateKeywordPreference(userId, keywords, enabled);
    }
    
    private NewsArticle mapCategoriesToNews(NewsArticle newsArticle) {
    	List<NewsArticleCategoryInfo> articleCategoryInfos = newsDao.getAllCategory(newsArticle.getId());
		for (NewsArticleCategoryInfo articleCategoryInfo: articleCategoryInfos) {
			if (newsArticle.getId() == articleCategoryInfo.getNewsId()) {
				newsArticle.getCategories().add(articleCategoryInfo.getCategoryType());
			}
		}
    	
    	return newsArticle;
    }
    
    private static List<NewsArticle> filterUniqueById(List<NewsArticle> articles) {
        Map<Integer, NewsArticle> uniqueMap = new LinkedHashMap<>();
        for (NewsArticle article : articles) {
            uniqueMap.putIfAbsent(article.getId(), article);
        }
        return new ArrayList<>(uniqueMap.values());
    }
}
