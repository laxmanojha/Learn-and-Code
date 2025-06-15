package backend.newsaggregation.service;
import backend.newsaggregation.model.NotificationPref;
import backend.newsaggregation.dao.interfaces.NotificationDao;

import java.util.List;

public class NotificationService {

    private static NotificationService instance;
    private final NotificationDao notificationDao;

    private NotificationService() {
		this(NotificationDao.getInstance());
	}
	
	private NotificationService(NotificationDao notificationDao) {
        this.notificationDao = notificationDao;
    }

    public static NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    public List<NotificationPref> getAllPreferences(int userId) {
        return notificationDao.getPreferencesByUser(userId);
    }

    public boolean updateCategoryConfig(int userId, String category, boolean enabled) {
        return notificationDao.updateCategoryPreference(userId, category, enabled);
    }

    public boolean addKeyword(int userId, String keyword) {
        return notificationDao.addKeywordPreference(userId, keyword);
    }

    public boolean removeKeyword(int userId, String keyword) {
        return notificationDao.removeKeywordPreference(userId, keyword);
    }

    public List<NotificationPref> getKeywordPreferences(int userId) {
        return notificationDao.getPreferencesByUser(userId).stream()
                .filter(p -> p.getKeywords() != null)
                .toList();
    }

    public boolean updateKeywordStatus(int userId, String keyword, boolean enabled) {
        return notificationDao.updateKeywordPreference(userId, keyword, enabled);
    }
}
