package backend.newsaggregation.dao.interfaces;

import backend.newsaggregation.dao.impl.UserDaoImpl;
import backend.newsaggregation.model.User;

public interface UserDao {
	
	static UserDao getInstance() {
        return UserDaoImpl.getInstance();
    }
	
    User getUserByUsername(String username);
    boolean saveUser(User user);
}
