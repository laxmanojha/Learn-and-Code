package backend.newsaggregation.controller;

import backend.newsaggregation.model.NewsArticleReport;
import backend.newsaggregation.model.User;
import backend.newsaggregation.service.NewsReportService;
import jakarta.servlet.http.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NewsReportServletTest {

    @InjectMocks
    private NewsReportServlet servlet;

    @Mock
    private NewsReportService reportService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    private ByteArrayOutputStream outputStream;
    private PrintWriter writer;

    @BeforeEach
    public void setup() throws IOException {
        MockitoAnnotations.openMocks(this);
        servlet = new NewsReportServlet(reportService);

        outputStream = new ByteArrayOutputStream();
        writer = new PrintWriter(outputStream, true);

        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    public void testDoGet_shouldReturnAllReports() throws IOException {
        List<NewsArticleReport> mockReports = List.of(
                new NewsArticleReport(1, 101, 10, "spam", new Date()),
                new NewsArticleReport(2, 102, 10, "fake", new Date())
        );
        when(reportService.getNewsArticleReport()).thenReturn(mockReports);

        servlet.doGet(request, response);

        writer.flush();
        String json = outputStream.toString();
        assertTrue(json.contains("\"newsId\":10"));
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testDoPost_successfulReport() throws Exception {
        mockSessionUser(1);
        when(request.getPathInfo()).thenReturn("/123");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("{\"comment\":\"spam\"}")));
        when(reportService.reportArticle(eq(1), eq(123), eq("spam"))).thenReturn(true);

        servlet.doPost(request, response);

        writer.flush();
        String json = outputStream.toString();
        assertTrue(json.contains("Article reported successfully"));
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testDoPost_articleIdMissing() throws Exception {
        mockSessionUser(1);
        when(request.getPathInfo()).thenReturn("/");

        servlet.doPost(request, response);

        writer.flush();
        assertTrue(outputStream.toString().contains("Article ID is missing"));
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPost_invalidArticleIdFormat() throws Exception {
        mockSessionUser(1);
        when(request.getPathInfo()).thenReturn("/abc");

        servlet.doPost(request, response);

        writer.flush();
        assertTrue(outputStream.toString().contains("Invalid Article ID"));
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPost_userNotLoggedIn() throws Exception {
        when(request.getSession(false)).thenReturn(null);
        when(request.getPathInfo()).thenReturn("/123");

        servlet.doPost(request, response);

        writer.flush();
        assertTrue(outputStream.toString().contains("Login required"));
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testDoPost_invalidJsonBody() throws Exception {
        mockSessionUser(1);
        when(request.getPathInfo()).thenReturn("/123");
        when(request.getReader()).thenThrow(new IOException("Invalid"));

        servlet.doPost(request, response);

        writer.flush();
        assertTrue(outputStream.toString().contains("Invalid JSON body"));
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPost_nullRequestBody() throws Exception {
        mockSessionUser(1);
        when(request.getPathInfo()).thenReturn("/123");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("{}")));
        when(reportService.reportArticle(anyInt(), anyInt(), isNull())).thenReturn(true);

        servlet.doPost(request, response);

        writer.flush();
        assertTrue(outputStream.toString().contains("Article reported successfully"));
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testDoPost_serviceFailure() throws Exception {
        mockSessionUser(1);
        when(request.getPathInfo()).thenReturn("/123");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("{\"comment\":\"test\"}")));
        when(reportService.reportArticle(anyInt(), anyInt(), anyString())).thenReturn(false);

        servlet.doPost(request, response);

        writer.flush();
        assertTrue(outputStream.toString().contains("Failed to report article"));
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testDoPost_serviceThrowsException() throws Exception {
        mockSessionUser(1);
        when(request.getPathInfo()).thenReturn("/123");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("{\"comment\":\"bad\"}")));
        when(reportService.reportArticle(anyInt(), anyInt(), anyString())).thenThrow(new RuntimeException("DB error"));

        servlet.doPost(request, response);

        writer.flush();
        assertTrue(outputStream.toString().contains("Server error"));
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    private void mockSessionUser(int userId) {
        User user = new User();
        user.setId(userId);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
    }
}
