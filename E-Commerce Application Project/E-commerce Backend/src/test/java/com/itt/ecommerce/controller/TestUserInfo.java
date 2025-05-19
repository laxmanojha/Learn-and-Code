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
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestUserInfo {

    @InjectMocks
    private UserInfo userInfo;

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
    public void testService_UserFound_ReturnsSuccessJson() throws Exception {
        UserDto mockUser = new UserDto(1, "John Doe", "john", "9999999999");
        when(request.getParameter("username")).thenReturn("john");
        when(userService.getUserInfo("john")).thenReturn(mockUser);

        userInfo.service(request, response);

        printWriter.flush();
        String responseOutput = stringWriter.toString();
        JsonObject json = JsonParser.parseString(responseOutput).getAsJsonObject();

        assertTrue(json.get("success").getAsBoolean());
        assertTrue(json.has("userInfo"));
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testService_UserNotFound_ReturnsErrorJson() throws Exception {
        when(request.getParameter("username")).thenReturn("unknown");
        when(userService.getUserInfo("unknown")).thenReturn(null);

        userInfo.service(request, response);

        printWriter.flush();
        String responseOutput = stringWriter.toString();
        JsonObject json = JsonParser.parseString(responseOutput).getAsJsonObject();

        assertFalse(json.get("success").getAsBoolean());
        assertEquals("Error in fetching user details.", json.get("message").getAsString());
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}
