package backend.newsaggregation.service;

import backend.newsaggregation.dao.interfaces.UserDao;
import backend.newsaggregation.model.User;
import backend.newsaggregation.util.Util;

public class UserService {
	
	private static UserService instance;
	private UserDao userDao;
	private Util util;
	
	private UserService() {
		this(UserDao.getInstance(), Util.getInstance());
	}
	
	private UserService(UserDao userDao, Util util) {
        this.userDao = userDao;
        this.util = util;
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }
	
	public String authenticateUser(User user) {
		String message = null;
		User userStoredData = userDao.getUserByUsername(user.getUsername());
		if (userStoredData == null)
			message = "0:User does not exists with username -> " + user.getUsername();
		else {			
			boolean passwordMatched = util.verifyPassword(user.getPassword(), userStoredData.getPassword());
			
			if (passwordMatched)
				message = "1:User Authentication Successful.";
			else
				message = "0:User Authentication Failed.";
		}
		
		return message;
	}
	
	public boolean registerUser(User user) {
		String hashedPassword = util.hashPassword(user.getPassword()) ;
		user.setPassword(hashedPassword);
		boolean result = userDao.saveUser(user);
		return result;
	}
	
	public User getUserInfo(String username) {
		User userInfo = userDao.getUserByUsername(username);
		if (userInfo != null)
			return userInfo;
		return null;
	}
}
