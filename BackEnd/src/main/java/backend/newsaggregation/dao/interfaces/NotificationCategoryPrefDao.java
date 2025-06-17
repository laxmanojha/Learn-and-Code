package backend.newsaggregation.dao.interfaces;

import java.util.List;

import backend.newsaggregation.dao.impl.NotificationCategoryPrefDaoImpl;
import backend.newsaggregation.model.NotificationPref;

public interface NotificationCategoryPrefDao {
	
	static NotificationCategoryPrefDao getInstance() {
        return NotificationCategoryPrefDaoImpl.getInstance();
    }
	
    List<NotificationPref> getPreferencesByUser(int userId);
    boolean updateCategoryPreference(int userId, int categoryId, boolean enabled);
}
