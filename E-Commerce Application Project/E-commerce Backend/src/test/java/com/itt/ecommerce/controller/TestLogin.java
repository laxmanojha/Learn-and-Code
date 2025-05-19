package com.itt.ecommerce.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.itt.ecommerce.dto.UserDto;
import com.itt.ecommerce.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestLogin {

    @InjectMocks
    private Login login;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    public void testLoginSuccess() throws Exception {
        when(request.getParameter("username")).thenReturn("john@example.com");
        when(request.getParameter("password")).thenReturn("password123");

        when(userService.authenticateUser(any(UserDto.class)))
                .thenReturn("1:Login successful");

        login.service(request, response);

        printWriter.flush();
        JsonObject json = JsonParser.parseString(stringWriter.toString()).getAsJsonObject();

        assertTrue(json.get("success").getAsBoolean());
        assertEquals("Login successful", json.get("message").getAsString());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testLoginFailure() throws Exception {
        when(request.getParameter("username")).thenReturn("john@example.com");
        when(request.getParameter("password")).thenReturn("wrongpassword");

        when(userService.authenticateUser(any(UserDto.class)))
                .thenReturn("0:Invalid username or password");

        login.service(request, response);

        printWriter.flush();
        JsonObject json = JsonParser.parseString(stringWriter.toString()).getAsJsonObject();

        assertFalse(json.get("success").getAsBoolean());
        assertEquals("Invalid username or password", json.get("message").getAsString());
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}
