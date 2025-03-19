package com.itt.ecommerce.service;

import com.itt.ecommerce.dao.UserDao;
import com.itt.ecommerce.dto.UserDto;

public class UserService {
	public static boolean authenticateUser(UserDto user) {
		UserDto userData = UserDao.getUser(user);
		return userData != null;
	}
	
	public static boolean registerUser(UserDto user) {
		boolean result = UserDao.addUser(user);
		return result;
	}
}
