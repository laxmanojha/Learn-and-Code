package backend.newsaggregation.dao.interfaces;

import java.util.List;

import backend.newsaggregation.dao.impl.NotificationDaoImpl;
import backend.newsaggregation.model.NotificationPref;

public interface NotificationDao {
	
	static NotificationDao getInstance() {
        return NotificationDaoImpl.getInstance();
    }
	
    List<NotificationPref> getPreferencesByUser(int userId);
    boolean updateCategoryPreference(int userId, String category, boolean enabled);
    boolean updateKeywordPreference(int userId, String keyword, boolean enabled);
    boolean addKeywordPreference(int userId, String keyword);
    boolean removeKeywordPreference(int userId, String keyword);
}
