package backend.newsaggregation.dao.interfaces;

import backend.newsaggregation.model.User;

public interface UserDao {
    User getUserByUsername(String username);
    boolean saveUser(User user);
}
