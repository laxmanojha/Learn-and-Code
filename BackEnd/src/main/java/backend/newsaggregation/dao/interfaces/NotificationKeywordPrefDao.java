package backend.newsaggregation.dao.interfaces;

import java.util.List;

import backend.newsaggregation.dao.impl.NotificationKeywordPrefDaoImpl;
import backend.newsaggregation.model.NotificationPref;
import backend.newsaggregation.model.NotificationPreference;

public interface NotificationKeywordPrefDao {
	
	static NotificationKeywordPrefDao getInstance() {
        return NotificationKeywordPrefDaoImpl.getInstance();
    }
	
    NotificationPreference getPreferencesByUser(int userId);
    boolean updateKeywordPreference(int userId, List<String> keywords, boolean enabled);
    boolean updateAllKeywordPreference(int userId, boolean enabled);
    boolean addKeywordPreference(int userId, List<String> keywords);
    boolean removeKeywordPreference(int userId, List<String> keywords);
    boolean removeAllKeywordPreference(int userId);
}
