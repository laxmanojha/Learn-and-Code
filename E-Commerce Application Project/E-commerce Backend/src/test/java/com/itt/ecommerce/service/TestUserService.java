package com.itt.ecommerce.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.itt.ecommerce.dao.UserDao;
import com.itt.ecommerce.dto.UserDto;
import com.itt.ecommerce.util.Util;

public class TestUserService {
	
	@Test
    public void testAuthenticateUser_UserDoesNotExist() {
        UserDto inputUser = new UserDto("unknown_user", "password123");
        UserDao mockDao = Mockito.mock(UserDao.class);
        Util mockUtil = Mockito.mock(Util.class);

        Mockito.when(mockDao.getUserBasedOnUserCredentials(inputUser)).thenReturn(null);

        UserService userService = new UserService(mockDao, mockUtil);
        String result = userService.authenticateUser(inputUser);

        Assertions.assertEquals("0:User does not exists with username -> unknown_user", result);
    }

    @Test
    public void testAuthenticateUser_PasswordMatched() {
        UserDto inputUser = new UserDto("laxman", "password123");
        UserDto storedUser = new UserDto("laxman", "$2a$10$abcdefg");

        UserDao mockDao = Mockito.mock(UserDao.class);
        Util mockUtil = Mockito.mock(Util.class);

        Mockito.when(mockDao.getUserBasedOnUserCredentials(inputUser)).thenReturn(storedUser);
        Mockito.when(mockUtil.verifyPassword("password123", "$2a$10$abcdefg")).thenReturn(true);

        UserService userService = new UserService(mockDao, mockUtil);
        String result = userService.authenticateUser(inputUser);

        Assertions.assertEquals("1:User Authentication Successful.", result);
    }

    @Test
    public void testAuthenticateUser_PasswordMismatch() {
        UserDto inputUser = new UserDto("laxman", "wrongpassword");
        UserDto storedUser = new UserDto("laxman", "$2a$10$abcdefg");

        UserDao mockDao = Mockito.mock(UserDao.class);
        Util mockUtil = Mockito.mock(Util.class);

        Mockito.when(mockDao.getUserBasedOnUserCredentials(inputUser)).thenReturn(storedUser);
        Mockito.when(mockUtil.verifyPassword("wrongpassword", "$2a$10$abcdefg")).thenReturn(false);

        UserService userService = new UserService(mockDao, mockUtil);
        String result = userService.authenticateUser(inputUser);

        Assertions.assertEquals("0:User Authentication Failed.", result);
    }

    @Test
    public void testAuthenticateUser_NullPasswordInInput() {
        UserDto inputUser = new UserDto("laxman", null);
        UserDto storedUser = new UserDto("laxman", "$2a$10$abcdefg");

        UserDao mockDao = Mockito.mock(UserDao.class);
        Util mockUtil = Mockito.mock(Util.class);

        Mockito.when(mockDao.getUserBasedOnUserCredentials(inputUser)).thenReturn(storedUser);
        Mockito.when(mockUtil.verifyPassword(null, "$2a$10$abcdefg")).thenReturn(false);

        UserService userService = new UserService(mockDao, mockUtil);
        String result = userService.authenticateUser(inputUser);

        Assertions.assertEquals("0:User Authentication Failed.", result);
    }

    @Test
    public void testAuthenticateUser_NullPasswordInStoredUser() {
        UserDto inputUser = new UserDto("laxman", "abc123");
        UserDto storedUser = new UserDto("laxman", null);

        UserDao mockDao = Mockito.mock(UserDao.class);
        Util mockUtil = Mockito.mock(Util.class);

        Mockito.when(mockDao.getUserBasedOnUserCredentials(inputUser)).thenReturn(storedUser);
        Mockito.when(mockUtil.verifyPassword("abc123", null)).thenReturn(false);

        UserService userService = new UserService(mockDao, mockUtil);
        String result = userService.authenticateUser(inputUser);

        Assertions.assertEquals("0:User Authentication Failed.", result);
    }
	
	@Test
	public void testRegisterUser_Positive() {
	    UserDto user = new UserDto("laxman", "password123");
	    UserDao mockDao = Mockito.mock(UserDao.class);
        Util util = Mockito.mock(Util.class);
	    Mockito.when(mockDao.addUser(Mockito.any(UserDto.class))).thenReturn(true);

	    UserService userService = new UserService(mockDao, util);

	    boolean result = userService.registerUser(user);

	    Assertions.assertTrue(result);
	    Assertions.assertNotEquals("password123", user.getPassword());
	}

	@Test
	public void testRegisterUser_Negative() {
	    UserDto user = new UserDto("john", "password123");
	    UserDao mockDao = Mockito.mock(UserDao.class);
        Util util = Mockito.mock(Util.class);
	    Mockito.when(mockDao.addUser(Mockito.any(UserDto.class))).thenReturn(false);

	    UserService userService = new UserService(mockDao, util);

	    boolean result = userService.registerUser(user);

	    Assertions.assertFalse(result);
	    Assertions.assertNotEquals("password123", user.getPassword());
	}

    @Test
    public void testGetUserInfo_Positive() {
        String username = "laxman";
        UserDto mockUser = new UserDto(username);

        UserDao mockDao = Mockito.mock(UserDao.class);
        Util util = Mockito.mock(Util.class);
        Mockito.when(mockDao.getUserBasedOnUsername(username)).thenReturn(mockUser);

        UserService userService = new UserService(mockDao, util);
        UserDto result = userService.getUserInfo(username);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(username, result.getUserName());
    }

    @Test
    public void testGetUserInfo_Negative() {
        String username = "john_doe";

        UserDao mockDao = Mockito.mock(UserDao.class);
        Util util = Mockito.mock(Util.class);
        Mockito.when(mockDao.getUserBasedOnUsername(username)).thenReturn(null);

        UserService userService = new UserService(mockDao, util);
        UserDto result = userService.getUserInfo(username);

        Assertions.assertNull(result);
    }
}

