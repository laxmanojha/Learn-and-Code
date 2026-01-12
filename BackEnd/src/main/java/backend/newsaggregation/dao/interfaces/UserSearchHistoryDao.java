package backend.newsaggregation.dao.interfaces;

import java.util.List;

import backend.newsaggregation.dao.impl.UserSearchHistoryDaoImpl;

public interface UserSearchHistoryDao {
	
	static UserSearchHistoryDao getInstance() {
		return UserSearchHistoryDaoImpl.getInstance();
	}
	
    void logSearch(int userId, String keyword);
    List<String> getRecentSearchKeywords(int userId, int limit);
}
