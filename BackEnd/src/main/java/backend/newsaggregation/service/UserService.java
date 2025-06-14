package backend.newsaggregation.service;

import backend.newsaggregation.dao.impl.UserDaoImpl;
import backend.newsaggregation.model.User;
import backend.newsaggregation.util.Util;

public class UserService {
	
	private UserDaoImpl userDao;
	private Util util;
	
	public UserService() {
		this(UserDaoImpl.getInstance(), new Util());
	}
	
	public UserService(UserDaoImpl userDao, Util util) {
        this.userDao = userDao;
        this.util = util;
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
