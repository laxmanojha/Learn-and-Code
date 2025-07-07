package backend.newsaggregation.controller;

import backend.newsaggregation.model.NotificationPreference;
import backend.newsaggregation.model.User;
import backend.newsaggregation.service.NotificationService;
import com.google.gson.Gson;
import jakarta.servlet.http.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.io.*;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NotificationServletTest {

    @InjectMocks
    private NotificationServlet servlet;

    @Mock
    private NotificationService notificationService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    private ByteArrayOutputStream responseStream;
    private PrintWriter writer;
    private final Gson gson = new Gson();

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        responseStream = new ByteArrayOutputStream();
        writer = new PrintWriter(responseStream, true);

        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);

        servlet = new NotificationServlet(notificationService);
    }

    private void simulateUserSession(int userId) {
        when(session.getAttribute("user")).thenReturn(new User("user", "pass", "email", 2) {{ setId(userId); }});
    }

    @Test
    public void testGetConsoleNotifications() throws IOException {
        simulateUserSession(5);
        when(request.getPathInfo()).thenReturn("/");
        when(notificationService.getConsoleNotifications(5)).thenReturn(List.of());

        servlet.doGet(request, response);
        writer.flush();

        assertTrue(responseStream.toString().contains("[]"));
    }

    @Test
    public void testGetAllPreferences() throws IOException {
        simulateUserSession(1);
        when(request.getPathInfo()).thenReturn("/preferences");
        when(notificationService.getAllPreferences(1)).thenReturn(List.of());

        servlet.doGet(request, response);
        writer.flush();

        assertTrue(responseStream.toString().contains("[]"));
    }

    @Test
    public void testGetCategoryPreferences() throws IOException {
        simulateUserSession(1);
        when(request.getPathInfo()).thenReturn("/preferences/category");
        when(notificationService.getCategoryPreferences(1)).thenReturn(List.of());

        servlet.doGet(request, response);
        writer.flush();
        assertTrue(responseStream.toString().contains("[]"));
    }

    @Test
    public void testGetKeywordPreferences() throws IOException {
        simulateUserSession(1);
        when(request.getPathInfo()).thenReturn("/preferences/keywords");
        when(notificationService.getKeywordPreferences(1)).thenReturn(new NotificationPreference());

        servlet.doGet(request, response);
        writer.flush();
        assertTrue(responseStream.toString().contains("isEnabled"));
    }

    @Test
    public void testPostCategoryConfigUpdate() throws IOException {
        simulateUserSession(1);
        when(request.getPathInfo()).thenReturn("/config/category");
        String json = gson.toJson(Map.of("categoryId", "10", "keywords", List.of("tech", "ai")));
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
        when(notificationService.updateCategoryConfig(eq(1), eq(10), anyList())).thenReturn(true);

        servlet.doPost(request, response);
        writer.flush();
        assertTrue(responseStream.toString().contains("success"));
    }

    @Test
    public void testPostAddKeywords() throws IOException {
        simulateUserSession(1);
        when(request.getPathInfo()).thenReturn("/config/keywords");
        String json = gson.toJson(Map.of("keywords", List.of("health", "finance")));
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
        when(notificationService.addKeywords(1, List.of("health", "finance"))).thenReturn(true);

        servlet.doPost(request, response);
        writer.flush();
        assertTrue(responseStream.toString().contains("Keyword(s) added"));
    }

    @Test
    public void testDeleteCategoryConfig() throws IOException {
        simulateUserSession(1);
        when(request.getPathInfo()).thenReturn("/config/category");
        String json = gson.toJson(Map.of("categoryId", "5"));
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
        when(notificationService.updateCategoryConfig(1, 5)).thenReturn(true);

        servlet.doDelete(request, response);
        writer.flush();
        assertTrue(responseStream.toString().contains("Preference updated"));
    }

    @Test
    public void testDeleteRemoveKeywords() throws IOException {
        simulateUserSession(1);
        when(request.getPathInfo()).thenReturn("/config/keywords");
        when(notificationService.removeKeywords(1)).thenReturn(true);

        servlet.doDelete(request, response);
        writer.flush();
        assertTrue(responseStream.toString().contains("Keyword(s) removed"));
    }

    @Test
    public void testGetWithoutUserInSession() throws IOException {
        when(session.getAttribute("user")).thenReturn(null);
        servlet.doGet(request, response);
        writer.flush();
        assertTrue(responseStream.toString().contains("User not found"));
    }

    @Test
    public void testInvalidGetEndpoint() throws IOException {
        simulateUserSession(1);
        when(request.getPathInfo()).thenReturn("/invalid");
        servlet.doGet(request, response);
        writer.flush();
        assertTrue(responseStream.toString().contains("Endpoint not found"));
    }

    @Test
    public void testInvalidPostEndpoint() throws IOException {
        simulateUserSession(1);
        when(request.getPathInfo()).thenReturn("/invalid");
        servlet.doPost(request, response);
        writer.flush();
        assertTrue(responseStream.toString().contains("POST endpoint not found"));
    }

    @Test
    public void testInvalidDeleteEndpoint() throws IOException {
        simulateUserSession(1);
        when(request.getPathInfo()).thenReturn("/invalid");
        servlet.doDelete(request, response);
        writer.flush();
        assertTrue(responseStream.toString().contains("DELETE endpoint not found"));
    }
}
