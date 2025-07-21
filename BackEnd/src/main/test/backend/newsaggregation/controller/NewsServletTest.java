package backend.newsaggregation.controller;

import backend.newsaggregation.model.*;
import backend.newsaggregation.service.*;
import jakarta.servlet.http.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.*;
import java.sql.Date;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NewsServletTest {

    @InjectMocks
    private NewsServlet servlet;

    @Mock private NewsService newsService;
    @Mock private SavedArticleService savedArticleService;
    @Mock private SearchNewsService searchNewsService;
    @Mock private CategoryService categoryService;
    @Mock private PersonalizedNewsService personalizedNewsService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;

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
    public void testGetTodayNews_NotPersonalized() throws IOException {
        when(request.getPathInfo()).thenReturn("/today");
        when(request.getParameter("personalized")).thenReturn("false");
        when(newsService.getTodayHeadlines()).thenReturn(List.of());

        servlet.doGet(request, response);
        assertTrue(getResponseOutput().contains("[]"));
    }

    @Test
    public void testGetTodayNews_Personalized_Authenticated() throws IOException {
        when(request.getPathInfo()).thenReturn("/today");
        when(request.getParameter("personalized")).thenReturn("true");
        when(request.getSession(false)).thenReturn(session);
        User user = new User("u", "p", "e", 2);
        user.setId(42);
        when(session.getAttribute("user")).thenReturn(user);
        when(newsService.getTodayHeadlines()).thenReturn(List.of(new NewsArticle()));
        when(personalizedNewsService.getPersonalizedArticles(eq(42), anyList())).thenReturn(List.of(new NewsArticle()));

        servlet.doGet(request, response);
        assertTrue(getResponseOutput().contains("["));
    }

    @Test
    public void testGetTodayNews_Personalized_Unauthenticated() throws IOException {
        when(request.getPathInfo()).thenReturn("/today");
        when(request.getParameter("personalized")).thenReturn("true");
        when(request.getSession(false)).thenReturn(null);

        servlet.doGet(request, response);
        assertTrue(getResponseOutput().contains("Login required"));
    }

    @Test
    public void testGetDateRangeNews() throws IOException {
        when(request.getPathInfo()).thenReturn("/date-range");
        when(request.getParameter("start")).thenReturn("2024-01-01");
        when(request.getParameter("end")).thenReturn("2024-01-05");
        when(request.getParameter("type")).thenReturn("all");
        when(newsService.getHeadlinesByDateRange(any(), any())).thenReturn(List.of(new NewsArticle()));

        servlet.doGet(request, response);
        assertTrue(getResponseOutput().contains("["));
    }

    @Test
    public void testGetSearchMissingQuery() throws IOException {
        when(request.getPathInfo()).thenReturn("/search");
        when(request.getParameter("query")).thenReturn(null);

        servlet.doGet(request, response);
        assertTrue(getResponseOutput().contains("Query parameter is required"));
    }

    @Test
    public void testGetSearchWithValidQuery() throws IOException {
        when(request.getPathInfo()).thenReturn("/search");
        when(request.getParameter("query")).thenReturn("AI");
        when(searchNewsService.searchArticles(anyInt(), any(), any(), any(), any()))
                .thenReturn(List.of(new NewsArticle()));

        servlet.doGet(request, response);
        assertTrue(getResponseOutput().contains("["));
    }

    @Test
    public void testGetCategoryList() throws IOException {
        when(request.getPathInfo()).thenReturn("/category");
        when(categoryService.getAllCategory()).thenReturn(List.of(new Category(0, "Tech")));

        servlet.doGet(request, response);
        assertTrue(getResponseOutput().contains("Tech"));
    }

    @Test
    public void testGetSavedArticles_Unauthenticated() throws IOException {
        when(request.getPathInfo()).thenReturn("/saved");
        when(request.getSession(false)).thenReturn(null);

        servlet.doGet(request, response);
        assertTrue(getResponseOutput().contains("Login required"));
    }

    @Test
    public void testGetSavedArticles_Authenticated() throws IOException {
        when(request.getPathInfo()).thenReturn("/saved");
        User user = new User("name", "", "email", 2);
        user.setId(3);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(savedArticleService.getSavedArticlesByUser(3)).thenReturn(List.of());

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
    public void testPostSaveArticle_Authenticated() throws IOException {
        when(request.getPathInfo()).thenReturn("/1/save");
        User user = new User("x", "", "x", 2);
        user.setId(1);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(savedArticleService.saveArticle(1, 1)).thenReturn(true);

        servlet.doPost(request, response);
        assertTrue(getResponseOutput().contains("Article saved."));
    }

    @Test
    public void testPostCategory_Unauthorized() throws IOException {
        when(request.getPathInfo()).thenReturn("/category");
        User user = new User("notadmin", "", "e", 2); // roleId != 1
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);

        servlet.doPost(request, response);
        assertTrue(getResponseOutput().contains("Access denied"));
    }

    @Test
    public void testDeleteSavedArticle_Authenticated() throws IOException {
        when(request.getPathInfo()).thenReturn("/saved/5");
        User user = new User("john", "", "john@example.com", 2);
        user.setId(1);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(savedArticleService.deleteSavedArticle(1, 5)).thenReturn(true);

        servlet.doDelete(request, response);
        assertTrue(getResponseOutput().contains("Article deleted."));
    }

    @Test
    public void testDeleteSavedArticle_Unauthenticated() throws IOException {
        when(request.getPathInfo()).thenReturn("/saved/5");
        when(request.getSession(false)).thenReturn(null);

        servlet.doDelete(request, response);
        assertTrue(getResponseOutput().contains("Login required"));
    }

    @Test
    public void testDeleteInvalidPath() throws IOException {
        when(request.getPathInfo()).thenReturn("/invalid-path");

        servlet.doDelete(request, response);
        assertTrue(getResponseOutput().contains("Invalid delete request"));
    }

    @Test
    public void testInvalidGetEndpoint() throws IOException {
        when(request.getPathInfo()).thenReturn("/unknown");

        servlet.doGet(request, response);
        assertTrue(getResponseOutput().contains("Invalid endpoint"));
    }

    @Test
    public void testInvalidPostEndpoint() throws IOException {
        when(request.getPathInfo()).thenReturn("/bad/post");
        servlet.doPost(request, response);
        assertTrue(getResponseOutput().contains("Invalid save request"));
    }
}
