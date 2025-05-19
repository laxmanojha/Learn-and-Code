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

public class TestRegister {

    @InjectMocks
    private Register registerServlet;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    public void testService_UserRegisteredSuccessfully_ReturnsSuccessJson() throws Exception {
        when(request.getParameter("fullname")).thenReturn("Alice Smith");
        when(request.getParameter("username")).thenReturn("alice@example.com");
        when(request.getParameter("password")).thenReturn("secure123");

        when(userService.registerUser(any(UserDto.class))).thenReturn(true);

        registerServlet.service(request, response);

        printWriter.flush();
        String jsonOutput = stringWriter.toString();
        JsonObject json = JsonParser.parseString(jsonOutput).getAsJsonObject();

        assertTrue(json.get("success").getAsBoolean());
        assertEquals("User registered successfully.", json.get("message").getAsString());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testService_RegistrationFailed_ReturnsFailureJson() throws Exception {
        when(request.getParameter("fullname")).thenReturn("Bob Doe");
        when(request.getParameter("username")).thenReturn("bob@example.com");
        when(request.getParameter("password")).thenReturn("pass123");

        when(userService.registerUser(any(UserDto.class))).thenReturn(false);

        registerServlet.service(request, response);

        printWriter.flush();
        String jsonOutput = stringWriter.toString();
        JsonObject json = JsonParser.parseString(jsonOutput).getAsJsonObject();

        assertFalse(json.get("success").getAsBoolean());
        assertEquals("Registration failed. User may already exist.", json.get("message").getAsString());
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}
