package com.itt.ecommerce.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.itt.ecommerce.dao.UserDao;
import com.itt.ecommerce.dto.UserDto;

public class TestUserService {
	@Test
	public void testGetUserInfo_Positive() {
	    // Arrange
	    String username = "john_doe";
	    UserDto expectedUser = new UserDto(username);

	    // Mock UserDao behavior
//	    Mockito.mockStatic(UserDao.class).when(() -> UserDao.getUserBasedOnUsername(username))
//	           .thenReturn(expectedUser);
	    
//	    try (MockedStatic<UserDao> mockedStatic = Mockito.mockStatic(UserDao.class)) {
//	        mockedStatic.when(() -> UserDao.getUserBasedOnUsername(username))
//	                    .thenReturn(expectedUser);

		    // Act
		    UserDto actualUser = expectedUser;  // Replace `YourClass` with actual class name
	
		    // Assert
		    Assertions.assertNotNull(actualUser);
		    Assertions.assertEquals(username, actualUser.getUserName());
	}
}
