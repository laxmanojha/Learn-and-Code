package backend.newsaggregation.service;

import backend.newsaggregation.dao.interfaces.UserDao;
import backend.newsaggregation.model.User;
import backend.newsaggregation.util.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserDao userDaoMock;
    private Util utilMock;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userDaoMock = mock(UserDao.class);
        utilMock = mock(Util.class);
        userService = new UserService(userDaoMock, utilMock);
    }

    @Test
    public void testIsValidEmail_validEmail_shouldReturnTrue() {
        assertTrue(userService.isValidEmail("user@example.com"));
    }

    @Test
    public void testIsValidEmail_invalidEmail_shouldReturnFalse() {
        assertFalse(userService.isValidEmail("user@@example.com"));
    }

    @Test
    public void testIsValidEmail_nullEmail_shouldReturnFalse() {
        assertFalse(userService.isValidEmail(null));
    }

    @Test
    public void testAuthenticateUser_success() {
        User inputUser = new User();
        inputUser.setUsername("john");
        inputUser.setPassword("password123");

        User storedUser = new User();
        storedUser.setUsername("john");
        storedUser.setPassword("hashed123");

        when(userDaoMock.getUserByUsername("john")).thenReturn(storedUser);
        when(utilMock.verifyPassword("password123", "hashed123")).thenReturn(true);

        String result = userService.authenticateUser(inputUser);
        assertEquals("1:User Authentication Successful.", result);
    }

    @Test
    public void testAuthenticateUser_invalidPassword() {
        User inputUser = new User();
        inputUser.setUsername("john");
        inputUser.setPassword("wrongpass");

        User storedUser = new User();
        storedUser.setUsername("john");
        storedUser.setPassword("correcthash");

        when(userDaoMock.getUserByUsername("john")).thenReturn(storedUser);
        when(utilMock.verifyPassword("wrongpass", "correcthash")).thenReturn(false);

        String result = userService.authenticateUser(inputUser);
        assertEquals("0:User Authentication Failed.", result);
    }

    @Test
    public void testAuthenticateUser_userNotExists() {
        User inputUser = new User();
        inputUser.setUsername("doesnotexist");

        when(userDaoMock.getUserByUsername("doesnotexist")).thenReturn(null);

        String result = userService.authenticateUser(inputUser);
        assertEquals("0:User does not exists with username -> doesnotexist", result);
    }

    @Test
    public void testRegisterUser_successfulRegistration() {
        User newUser = new User();
        newUser.setUsername("alice");
        newUser.setEmail("alice@example.com");
        newUser.setPassword("plainpass");

        when(userDaoMock.getUserByUsername("alice")).thenReturn(null);
        when(userDaoMock.getUserByEmail("alice@example.com")).thenReturn(null);
        when(utilMock.hashPassword("plainpass")).thenReturn("hashedpass");
        when(userDaoMock.saveUser(any(User.class))).thenReturn(true);

        String result = userService.registerUser(newUser);
        assertEquals("1:User registration successful.", result);
    }

    @Test
    public void testRegisterUser_invalidInput_shouldReturnError() {
        User invalidUser = new User();
        String result = userService.registerUser(invalidUser);
        assertEquals("0:Invalid input. Username, email, and password must not be empty.", result);
    }

    @Test
    public void testRegisterUser_invalidEmailFormat() {
        User user = new User();
        user.setUsername("bob");
        user.setEmail("invalidemail");
        user.setPassword("123456");

        String result = userService.registerUser(user);
        assertEquals("0:Invalid email format.", result);
    }

    @Test
    public void testRegisterUser_existingUsername() {
        User user = new User();
        user.setUsername("bob");
        user.setEmail("bob@example.com");
        user.setPassword("123456");

        when(userDaoMock.getUserByUsername("bob")).thenReturn(new User());

        String result = userService.registerUser(user);
        assertEquals("0:Username already exists.", result);
    }

    @Test
    public void testRegisterUser_existingEmail() {
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("bob@example.com");
        user.setPassword("123456");

        when(userDaoMock.getUserByUsername("newuser")).thenReturn(null);
        when(userDaoMock.getUserByEmail("bob@example.com")).thenReturn(new User());

        String result = userService.registerUser(user);
        assertEquals("0:Email already exists.", result);
    }

    @Test
    public void testRegisterUser_insertFails() {
        User user = new User();
        user.setUsername("userx");
        user.setEmail("userx@example.com");
        user.setPassword("pass");

        when(userDaoMock.getUserByUsername("userx")).thenReturn(null);
        when(userDaoMock.getUserByEmail("userx@example.com")).thenReturn(null);
        when(utilMock.hashPassword("pass")).thenReturn("hashed");
        when(userDaoMock.saveUser(any(User.class))).thenReturn(false);

        String result = userService.registerUser(user);
        assertEquals("0:User registration failed.", result);
    }

    @Test
    public void testGetUserByUsername_userExists() {
        User user = new User();
        when(userDaoMock.getUserByUsername("john")).thenReturn(user);
        assertNotNull(userService.getUserByUsername("john"));
    }

    @Test
    public void testGetUserByUsername_userNotFound() {
        when(userDaoMock.getUserByUsername("unknown")).thenReturn(null);
        assertNull(userService.getUserByUsername("unknown"));
    }

    @Test
    public void testGetUserByEmail_userExists() {
        User user = new User();
        when(userDaoMock.getUserByEmail("a@b.com")).thenReturn(user);
        assertNotNull(userService.getUserByEmail("a@b.com"));
    }

    @Test
    public void testGetUserByEmail_userNotFound() {
        when(userDaoMock.getUserByEmail("x@y.com")).thenReturn(null);
        assertNull(userService.getUserByEmail("x@y.com"));
    }

    @Test
    public void testGetAllUserIdsWithEmail_shouldReturnMap() {
        Map<Integer, String> mockMap = Map.of(1, "a@b.com", 2, "c@d.com");
        when(userDaoMock.getAllUserIdsWithEmail()).thenReturn(mockMap);
        assertEquals(mockMap, userService.getAllUserIdsWithEmail());
    }
}
