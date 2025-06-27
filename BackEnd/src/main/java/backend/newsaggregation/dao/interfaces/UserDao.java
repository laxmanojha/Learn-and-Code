package backend.newsaggregation.dao.interfaces;

import java.sql.Timestamp;

import backend.newsaggregation.dao.impl.UserDaoImpl;
import backend.newsaggregation.model.User;

public interface UserDao {
	
	static UserDao getInstance() {
        return UserDaoImpl.getInstance();
    }
	
    User getUserByUsername(String username);
    User getUserByEmail(String email);
    boolean saveUser(User user);
    boolean saveNotificationViewedTime(int userId, Timestamp time);
    Timestamp getNotificationViewedTime(int userId);
}
