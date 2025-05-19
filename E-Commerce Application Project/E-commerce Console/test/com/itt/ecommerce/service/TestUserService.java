package com.itt.ecommerce.service;

import com.itt.ecommerce.dto.UserDto;
import com.itt.ecommerce.util.HttpUtil;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import java.io.IOException;
import java.net.http.HttpResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestUserService {

    @Test
    public void testLoginUser_success() throws IOException, InterruptedException {
        try (MockedStatic<HttpUtil> mockedHttpUtil = mockStatic(HttpUtil.class)) {
            HttpResponse<String> mockResponse = mock(HttpResponse.class);

            mockedHttpUtil.when(() -> HttpUtil.sendPostRequest(contains("/user/login"), anyString()))
                          .thenReturn(mockResponse);
            mockedHttpUtil.when(() -> HttpUtil.processResponse(mockResponse, "Login"))
                          .thenReturn(true);

            boolean result = UserService.loginUser("john_doe", "pass123");

            assertTrue(result);
            mockedHttpUtil.verify(() -> HttpUtil.sendPostRequest(contains("/user/login"), anyString()));
            mockedHttpUtil.verify(() -> HttpUtil.processResponse(mockResponse, "Login"));
        }
    }

    @Test
    public void testLoginUser_failure() throws IOException, InterruptedException {
        try (MockedStatic<HttpUtil> mockedHttpUtil = mockStatic(HttpUtil.class)) {
            HttpResponse<String> mockResponse = mock(HttpResponse.class);

            mockedHttpUtil.when(() -> HttpUtil.sendPostRequest(contains("/user/login"), anyString()))
                          .thenReturn(mockResponse);
            mockedHttpUtil.when(() -> HttpUtil.processResponse(mockResponse, "Login"))
                          .thenReturn(false);

            boolean result = UserService.loginUser("john_doe", "wrongpass");

            assertFalse(result);
        }
    }

    @Test
    public void testRegisterUser_success() throws IOException, InterruptedException {
        UserDto user = new UserDto("John Doe", "john_doe", "pass123");

        try (MockedStatic<HttpUtil> mockedHttpUtil = mockStatic(HttpUtil.class)) {
            HttpResponse<String> mockResponse = mock(HttpResponse.class);

            mockedHttpUtil.when(() -> HttpUtil.sendPostRequest(contains("/user/register"), anyString()))
                          .thenReturn(mockResponse);
            mockedHttpUtil.when(() -> HttpUtil.processResponse(mockResponse, "Registration"))
                          .thenReturn(true);

            boolean result = UserService.registerUser(user);

            assertTrue(result);
        }
    }

    @Test
    public void testRegisterUser_failure() throws IOException, InterruptedException {
        UserDto user = new UserDto("John Doe", "john_doe", "pass123");

        try (MockedStatic<HttpUtil> mockedHttpUtil = mockStatic(HttpUtil.class)) {
            HttpResponse<String> mockResponse = mock(HttpResponse.class);

            mockedHttpUtil.when(() -> HttpUtil.sendPostRequest(contains("/user/register"), anyString()))
                          .thenReturn(mockResponse);
            mockedHttpUtil.when(() -> HttpUtil.processResponse(mockResponse, "Registration"))
                          .thenReturn(false);

            boolean result = UserService.registerUser(user);

            assertFalse(result);
        }
    }

    @Test
    public void testViewUserDetails() throws IOException, InterruptedException {
        try (MockedStatic<HttpUtil> mockedHttpUtil = mockStatic(HttpUtil.class)) {
            HttpResponse<String> mockResponse = mock(HttpResponse.class);

            mockedHttpUtil.when(() -> HttpUtil.sendPostRequest(contains("/user"), anyString()))
                          .thenReturn(mockResponse);
            mockedHttpUtil.when(() -> HttpUtil.processUserDetailsResponse(mockResponse))
                          .thenAnswer(invocation -> null);

            assertDoesNotThrow(() -> UserService.viewUserDetails("john_doe"));

            mockedHttpUtil.verify(() -> HttpUtil.sendPostRequest(contains("/user"), anyString()));
            mockedHttpUtil.verify(() -> HttpUtil.processUserDetailsResponse(mockResponse));
        }
    }
}
