package com.itt.ecommerce.service;

import com.itt.ecommerce.dao.UserDao;
import com.itt.ecommerce.dto.UserDto;
import com.itt.ecommerce.util.Util;

public class UserService {
	
	private UserDao userDao;
	private Util util;
	
	public UserService() {
		this(new UserDao(), new Util());
	}
	
	public UserService(UserDao userDao, Util util) {
        this.userDao = userDao;
        this.util = util;
    }
	
	public String authenticateUser(UserDto user) {
		String message = null;
		UserDto userStoredData = userDao.getUserBasedOnUserCredentials(user);
		if (userStoredData == null)
			message = "0:User does not exists with username -> " + user.getUserName();
		else {			
			boolean passwordMatched = util.verifyPassword(user.getPassword(), userStoredData.getPassword());
			
			if (passwordMatched)
				message = "1:User Authentication Successful.";
			else
				message = "0:User Authentication Failed.";
		}
		
		return message;
	}
	
	public boolean registerUser(UserDto user) {
		String hashedPassword = util.hashPassword(user.getPassword()) ;
		user.setPassword(hashedPassword);
		boolean result = userDao.addUser(user);
		return result;
	}
	
	public UserDto getUserInfo(String username) {
		UserDto userInfo = userDao.getUserBasedOnUsername(username);
		if (userInfo != null)
			return userInfo;
		return null;
	}
}
