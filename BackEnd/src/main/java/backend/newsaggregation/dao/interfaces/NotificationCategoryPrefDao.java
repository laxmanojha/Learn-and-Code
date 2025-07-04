package backend.newsaggregation.dao.interfaces;

import java.util.List;

import backend.newsaggregation.dao.impl.NotificationCategoryPrefDaoImpl;
import backend.newsaggregation.model.NotificationPref;
import backend.newsaggregation.model.NotificationPreference;

public interface NotificationCategoryPrefDao {
	
	static NotificationCategoryPrefDao getInstance() {
        return NotificationCategoryPrefDaoImpl.getInstance();
    }
	
    List<NotificationPreference> getCategoryPreferencesByUser(int userId);
    boolean updateCategoryPreference(int userId, int categoryId, boolean enabled);
    boolean addCategoryPreference(int userId, int categoryId, List<String> keywords);
    boolean removeCategoryPreference(int userId, int categoryId);
}
