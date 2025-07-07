package backend.newsaggregation.controller;

import backend.newsaggregation.model.User;
import backend.newsaggregation.service.*;
import jakarta.servlet.http.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.io.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NewsServletTest {

    @InjectMocks
    private NewsServlet servlet;

    @Mock
    private NewsService newsService;

    @Mock
    private SavedArticleService savedArticleService;

    @Mock
    private SearchNewsService searchNewsService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private PersonalizedNewsService personalizedNewsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    private ByteArrayOutputStream responseStream;
    private PrintWriter writer;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        responseStream = new ByteArrayOutputStream();
        writer = new PrintWriter(responseStream, true);
        when(response.getWriter()).thenReturn(writer);

        servlet = new NewsServlet(newsService, savedArticleService, searchNewsService, categoryService, personalizedNewsService);
    }

    private String getResponseOutput() {
        writer.flush();
        return responseStream.toString();
    }

    @Test
    public void testGetTodayNews() throws IOException {
        when(request.getPathInfo()).thenReturn("/today");
        when(request.getParameter("personalized")).thenReturn("false");

        when(newsService.getTodayHeadlines()).thenReturn(List.of());

        servlet.doGet(request, response);
        assertTrue(getResponseOutput().contains("[]"));
    }

    @Test
    public void testGetTodayNewsPersonalized_Unauthenticated() throws IOException {
        when(request.getPathInfo()).thenReturn("/today");
        when(request.getParameter("personalized")).thenReturn("true");
        when(request.getSession(false)).thenReturn(null);

        servlet.doGet(request, response);
        assertTrue(getResponseOutput().contains("Login required"));
    }

    @Test
    public void testGetCategory() throws IOException {
        when(request.getPathInfo()).thenReturn("/category");
        when(categoryService.getAllCategory()).thenReturn(List.of());

        servlet.doGet(request, response);
        assertTrue(getResponseOutput().contains("[]"));
    }

    @Test
    public void testPostSaveArticle_Unauthenticated() throws IOException {
        when(request.getPathInfo()).thenReturn("/1/save");
        when(request.getSession(false)).thenReturn(null);

        servlet.doPost(request, response);
        assertTrue(getResponseOutput().contains("Login required"));
    }

    @Test
    public void testDeleteSavedArticle() throws IOException {
        when(request.getPathInfo()).thenReturn("/saved/1");
        User user = new User("john", "", "john@example.com", 2);
        user.setId(1);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(savedArticleService.deleteSavedArticle(1, 1)).thenReturn(true);

        servlet.doDelete(request, response);
        assertTrue(getResponseOutput().contains("Article deleted."));
    }

    @Test
    public void testSearchWithoutQuery() throws IOException {
        when(request.getPathInfo()).thenReturn("/search");
        when(request.getParameter("query")).thenReturn(null);

        servlet.doGet(request, response);
        assertTrue(getResponseOutput().contains("Query parameter is required"));
    }

    @Test
    public void testGetInvalidEndpoint() throws IOException {
        when(request.getPathInfo()).thenReturn("/invalid-endpoint");

        servlet.doGet(request, response);
        assertTrue(getResponseOutput().contains("Invalid endpoint"));
    }

    @Test
    public void testPostCategory_Unauthorized() throws IOException {
        when(request.getPathInfo()).thenReturn("/category");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(new User("user", "", "user@email.com", 2));

        servlet.doPost(request, response);
        assertTrue(getResponseOutput().contains("Access denied"));
    }

    @Test
    public void testDeleteInvalidPath() throws IOException {
        when(request.getPathInfo()).thenReturn("/delete-nothing");

        servlet.doDelete(request, response);
        assertTrue(getResponseOutput().contains("Invalid delete request"));
    }
}