package com.itt.ecommerce.service;

import com.itt.ecommerce.dao.UserDao;
import com.itt.ecommerce.dto.UserDto;

public class UserService {
	public static boolean authenticateUser(UserDto user) {
		UserDto userData = UserDao.getUserBasedOnUserCredentials(user);
		return userData != null;
	}
	
	public static boolean registerUser(UserDto user) {
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
