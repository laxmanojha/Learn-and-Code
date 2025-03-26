package com.itt.ecommerce.service;

import com.itt.ecommerce.dao.UserDao;
import com.itt.ecommerce.dto.UserDto;
import com.itt.ecommerce.util.Util;

public class UserService {
	public static String authenticateUser(UserDto user) {
		String message = null;
		UserDto userStoredData = UserDao.getUserBasedOnUserCredentials(user);
		if (userStoredData == null)
			message = "0:User does exists with username -> " + user.getUserName();
		else {			
			boolean passwordMatched = Util.verifyPassword(user.getPassword(), userStoredData.getPassword());
			
			if (passwordMatched)
				message = "1:User Authentication Successful.";
			else
				message = "0:User Authentication Failed.";
		}
		
		return message;
	}
	
	public static boolean registerUser(UserDto user) {
		String hashedPassword = Util.hashPassword(user.getPassword()) ;
		user.setPassword(hashedPassword);
		boolean result = UserDao.addUser(user);
		return result;
	}
	
	public static UserDto getUserInfo(String username) {
		UserDto userInfo = UserDao.getUserBasedOnUsername(username);
		if (userInfo != null)
			return userInfo;
		return null;
	}
}
