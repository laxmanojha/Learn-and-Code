package com.itt.ecommerce.service;

import com.itt.ecommerce.dao.UserDao;
import com.itt.ecommerce.dto.UserDto;

public class UserService {
	public static String authenticateUser(UserDto user) {
		UserDto userData = UserDao.getUser(user);
		if (userData == null)
				return "User authentication failed";
		else
			return "User authenticated successfully " + "\n" + "Welcome " + userData.getName();
	}
	
	public static String registerUser(UserDto user) {
		boolean result = UserDao.addUser(user);
		if (result)
			return "User profile created successfully";
		else
			return "User profile creation failed";
	}
}
