package backend.newsaggregation.controller;

import backend.newsaggregation.model.User;
import backend.newsaggregation.service.UserService;
import jakarta.servlet.http.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthServletTest {

    @InjectMocks
    private AuthServlet servlet;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    private ByteArrayOutputStream responseOutput;
    private PrintWriter writer;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        responseOutput = new ByteArrayOutputStream();
        writer = new PrintWriter(responseOutput, true);

        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);

        servlet = new AuthServlet(userService);
        servlet.init();
    }

    @Test
    public void testLoginSuccess() throws Exception {
        String body = "{\"username\":\"john\",\"password\":\"secret\"}";
        User fullUser = new User("john", "hashed", "john@example.com", 2);

        when(request.getPathInfo()).thenReturn("/login");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(body)));
        when(userService.authenticateUser(any())).thenReturn("1:Login Successful");
        when(userService.getUserByUsername("john")).thenReturn(fullUser);

        servlet.doPost(request, response);
        writer.flush();
        String output = responseOutput.toString();

        assertTrue(output.contains("\"success\":true"));
        assertTrue(output.contains("john@example.com"));
    }

    @Test
    public void testLoginFail_WrongCredentials() throws Exception {
        String body = "{\"username\":\"john\",\"password\":\"wrong\"}";

        when(request.getPathInfo()).thenReturn("/login");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(body)));
        when(userService.authenticateUser(any())).thenReturn("0:Login Failed");

        servlet.doPost(request, response);
        writer.flush();
        String output = responseOutput.toString();

        assertTrue(output.contains("\"success\":false"));
        assertTrue(output.contains("Login Failed"));
    }

    @Test
    public void testLoginMissingCredentials() throws Exception {
        String body = "{\"username\":null,\"password\":null}";

        when(request.getPathInfo()).thenReturn("/login");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(body)));

        servlet.doPost(request, response);
        writer.flush();
        assertTrue(responseOutput.toString().contains("Missing credentials"));
    }

    @Test
    public void testSignupSuccess() throws Exception {
        String body = "{\"username\":\"alice\",\"password\":\"pass\",\"email\":\"alice@email.com\"}";

        when(request.getPathInfo()).thenReturn("/signup");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(body)));
        when(userService.registerUser(any())).thenReturn("1:User registration successful.");

        servlet.doPost(request, response);
        writer.flush();
        String output = responseOutput.toString();

        assertTrue(output.contains("\"success\":true"));
        assertTrue(output.contains("registration successful"));
    }

    @Test
    public void testSignupFailure_ExistingUser() throws Exception {
        String body = "{\"username\":\"bob\",\"password\":\"pass\",\"email\":\"bob@email.com\"}";

        when(request.getPathInfo()).thenReturn("/signup");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(body)));
        when(userService.registerUser(any())).thenReturn("0:Email already exists.");

        servlet.doPost(request, response);
        writer.flush();
        String output = responseOutput.toString();

        assertTrue(output.contains("\"success\":false"));
        assertTrue(output.contains("Email already exists"));
    }

    @Test
    public void testSignupMissingInfo() throws Exception {
        String body = "{\"username\":\"\",\"password\":null,\"email\":null}";

        when(request.getPathInfo()).thenReturn("/signup");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(body)));

        servlet.doPost(request, response);
        writer.flush();

        assertTrue(responseOutput.toString().contains("Missing signup information"));
    }

    @Test
    public void testLogout() throws Exception {
        when(request.getPathInfo()).thenReturn("/logout");

        servlet.doPost(request, response);
        writer.flush();

        verify(session).invalidate();
        assertTrue(responseOutput.toString().contains("Logged out successfully"));
    }

    @Test
    public void testInvalidPath() throws Exception {
        when(request.getPathInfo()).thenReturn("/unknown");

        servlet.doPost(request, response);
        writer.flush();

        assertTrue(responseOutput.toString().contains("Invalid or unknown endpoint"));
    }
}
