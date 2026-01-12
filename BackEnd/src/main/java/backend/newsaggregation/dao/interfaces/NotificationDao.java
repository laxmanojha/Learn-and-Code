package backend.newsaggregation.dao.interfaces;

import java.sql.Timestamp;
import java.util.List;

import backend.newsaggregation.dao.impl.NotificationDaoImpl;
import backend.newsaggregation.model.NewsArticle;

public interface NotificationDao {

	public static NotificationDaoImpl getInstance() {
        return NotificationDaoImpl.getInstance();
    }
	
	public List<NewsArticle> getNewsForConsoleNotification(int userId, Timestamp from, Timestamp to);
}
