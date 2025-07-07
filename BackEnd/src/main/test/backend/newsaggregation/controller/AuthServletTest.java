package backend.newsaggregation.controller;

import backend.newsaggregation.dao.interfaces.UserDao;
import backend.newsaggregation.model.User;
import backend.newsaggregation.service.UserService;
import backend.newsaggregation.util.Util;

import jakarta.servlet.http.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.*;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AuthServletTest {

    @InjectMocks
    private AuthServlet authServlet;

    @Mock
    private UserDao userDao;

    @Mock
    private Util util;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    private PrintWriter printWriter;
    private StringWriter stringWriter;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);

        UserService userService = new UserService(userDao, util);
        Field userServiceField = AuthServlet.class.getDeclaredField("userService");
        userServiceField.setAccessible(true);
        userServiceField.set(authServlet, userService);

        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
        when(request.getSession()).thenReturn(session);

        authServlet.init();
    }

    @Test
    public void testLoginSuccess() throws Exception {
        String requestBody = "{\"username\":\"john\",\"password\":\"secret\"}";
        User storedUser = new User("john", "hashed-secret", "john@example.com", 2);

        when(request.getPathInfo()).thenReturn("/login");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(userDao.getUserByUsername("john")).thenReturn(storedUser);
        when(util.verifyPassword("secret", "hashed-secret")).thenReturn(true);

        authServlet.doPost(request, response);
        printWriter.flush();

        JsonObject json = JsonParser.parseString(stringWriter.toString()).getAsJsonObject();

        assertTrue(json.get("success").getAsBoolean());
        assertEquals("User Authentication Successful.", json.get("message").getAsString());
        assertEquals("john@example.com", json.get("user").getAsJsonObject().get("email").getAsString());
    }

    @Test
    public void testLoginFailure_WrongPassword() throws Exception {
        String requestBody = "{\"username\":\"john\",\"password\":\"wrong\"}";
        User storedUser = new User("john", "hashed-secret", "john@example.com", 2);

        when(request.getPathInfo()).thenReturn("/login");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(userDao.getUserByUsername("john")).thenReturn(storedUser);
        when(util.verifyPassword("wrong", "hashed-secret")).thenReturn(false);

        authServlet.doPost(request, response);
        printWriter.flush();

        JsonObject json = JsonParser.parseString(stringWriter.toString()).getAsJsonObject();

        assertFalse(json.get("success").getAsBoolean());
        assertTrue(json.get("message").getAsString().contains("Authentication Failed"));
    }

    @Test
    public void testLoginFailure_UserNotFound() throws Exception {
        String requestBody = "{\"username\":\"john\",\"password\":\"secret\"}";

        when(request.getPathInfo()).thenReturn("/login");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(userDao.getUserByUsername("john")).thenReturn(null);

        authServlet.doPost(request, response);
        printWriter.flush();

        JsonObject json = JsonParser.parseString(stringWriter.toString()).getAsJsonObject();

        assertFalse(json.get("success").getAsBoolean());
        assertTrue(json.get("message").getAsString().contains("does not exists"));
    }

    @Test
    public void testSignupSuccess() throws Exception {
        String requestBody = "{\"username\":\"jane\",\"password\":\"1234\",\"email\":\"jane@example.com\"}";

        when(request.getPathInfo()).thenReturn("/signup");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(userDao.getUserByUsername("jane")).thenReturn(null);
        when(userDao.getUserByEmail("jane@example.com")).thenReturn(null);
        when(util.hashPassword("1234")).thenReturn("hashed-1234");
        when(userDao.saveUser(any(User.class))).thenReturn(true);

        authServlet.doPost(request, response);
        printWriter.flush();

        JsonObject json = JsonParser.parseString(stringWriter.toString()).getAsJsonObject();

        assertTrue(json.get("success").getAsBoolean());
        assertTrue(json.get("message").getAsString().contains("registration successful"));
    }

    @Test
    public void testSignupFailure_EmailExists() throws Exception {
        String requestBody = "{\"username\":\"jane\",\"password\":\"1234\",\"email\":\"jane@example.com\"}";
        User existing = new User("jane2", "xxx", "jane@example.com", 2);

        when(request.getPathInfo()).thenReturn("/signup");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(userDao.getUserByUsername("jane")).thenReturn(null);
        when(userDao.getUserByEmail("jane@example.com")).thenReturn(existing);

        authServlet.doPost(request, response);
        printWriter.flush();

        JsonObject json = JsonParser.parseString(stringWriter.toString()).getAsJsonObject();

        assertFalse(json.get("success").getAsBoolean());
        assertTrue(json.get("message").getAsString().contains("Email already exists"));
    }

    @Test
    public void testLogoutSuccess() throws Exception {
        when(request.getPathInfo()).thenReturn("/logout");

        authServlet.doPost(request, response);
        printWriter.flush();

        verify(session).invalidate();
        JsonObject json = JsonParser.parseString(stringWriter.toString()).getAsJsonObject();

        assertTrue(json.get("success").getAsBoolean());
        assertTrue(json.get("message").getAsString().contains("Logged out"));
    }

    @Test
    public void testInvalidEndpoint() throws Exception {
        when(request.getPathInfo()).thenReturn("/invalid");

        authServlet.doPost(request, response);
        printWriter.flush();

        JsonObject json = JsonParser.parseString(stringWriter.toString()).getAsJsonObject();

        assertFalse(json.get("success").getAsBoolean());
        assertTrue(json.get("message").getAsString().contains("Invalid or unknown endpoint"));
    }
}
