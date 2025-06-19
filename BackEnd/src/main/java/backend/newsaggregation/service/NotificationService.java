package backend.newsaggregation.service;
import backend.newsaggregation.model.NotificationPref;
import backend.newsaggregation.dao.interfaces.NotificationCategoryPrefDao;
import backend.newsaggregation.dao.interfaces.NotificationKeywordPrefDao;

import java.util.List;

public class NotificationService {

    private static NotificationService instance;
    private final NotificationCategoryPrefDao notificationCategoryPrefDao;
    private final NotificationKeywordPrefDao notificationKeywordPrefDao;

    private NotificationService() {
		this(NotificationCategoryPrefDao.getInstance(), NotificationKeywordPrefDao.getInstance());
	}
	
	private NotificationService(NotificationCategoryPrefDao notificationCategoryPrefDao, NotificationKeywordPrefDao notificationKeywordPrefDao) {
        this.notificationCategoryPrefDao = notificationCategoryPrefDao;
        this.notificationKeywordPrefDao = notificationKeywordPrefDao;
    }

    public static NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    public List<NotificationPref> getAllPreferences(int userId) {
        return notificationCategoryPrefDao.getPreferencesByUser(userId);
    }

    public boolean updateCategoryConfig(int userId, int categoryId, boolean enabled) {
        return notificationCategoryPrefDao.updateCategoryPreference(userId, categoryId, enabled);
    }

    public boolean addKeyword(int userId, String keyword) {
    	List<String> keywords = null;
        return notificationKeywordPrefDao.addKeywordPreference(userId, keywords);
    }

    public boolean removeKeyword(int userId, String keyword) {
        return notificationKeywordPrefDao.removeKeywordPreference(userId, keyword);
    }

    public List<NotificationPref> getKeywordPreferences(int userId) {
        return notificationCategoryPrefDao.getPreferencesByUser(userId).stream()
                .filter(p -> p.getKeywords() != null)
                .toList();
    }

    public boolean updateKeywordStatus(int userId, String keyword, boolean enabled) {
    	List<String> keywords = null;
        return notificationKeywordPrefDao.updateKeywordPreference(userId, keywords, enabled);
    }
}
