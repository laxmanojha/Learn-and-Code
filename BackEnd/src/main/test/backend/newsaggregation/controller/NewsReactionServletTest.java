package backend.newsaggregation.controller;

import backend.newsaggregation.model.User;
import backend.newsaggregation.service.NewsReactionService;
import jakarta.servlet.http.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NewsReactionServletTest {

    @InjectMocks
    private NewsReactionServlet servlet;

    @Mock
    private NewsReactionService reactionService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    private ByteArrayOutputStream responseOutput;
    private PrintWriter writer;

    @BeforeEach
    public void setup() throws IOException {
        MockitoAnnotations.openMocks(this);

        responseOutput = new ByteArrayOutputStream();
        writer = new PrintWriter(responseOutput, true);

        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);

        servlet = new NewsReactionServlet(reactionService);
    }

    @Test
    public void testReactionSuccess() throws Exception {
        User user = new User("john", "pass", "john@email.com", 2);
        user.setId(100);

        when(request.getPathInfo()).thenReturn("/42");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(reactionService.reactToArticle(100, 42, "like")).thenReturn(true);

        StringReader body = new StringReader("{\"reaction\":\"like\"}");
        when(request.getReader()).thenReturn(new BufferedReader(body));

        servlet.doPost(request, response);

        writer.flush();
        String output = responseOutput.toString();
        assertTrue(output.contains("\"success\":true"));
        assertTrue(output.contains("Reaction recorded"));
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testMissingReactionField() throws Exception {
        setupValidSession("/42");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("{}")));

        servlet.doPost(request, response);
        writer.flush();
        assertTrue(responseOutput.toString().contains("Missing reaction field"));
    }

    @Test
    public void testInvalidArticleId() throws Exception {
        when(request.getPathInfo()).thenReturn("/abc");

        servlet.doPost(request, response);
        writer.flush();
        assertTrue(responseOutput.toString().contains("Invalid Article ID"));
    }

    @Test
    public void testMissingSession() throws Exception {
        when(request.getPathInfo()).thenReturn("/5");
        when(request.getSession(false)).thenReturn(null);

        servlet.doPost(request, response);
        writer.flush();
        assertTrue(responseOutput.toString().contains("Login required"));
    }

    @Test
    public void testReactionServiceFailure() throws Exception {
        User user = new User("john", "pass", "john@email.com", 2);
        user.setId(1);

        when(request.getPathInfo()).thenReturn("/99");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(reactionService.reactToArticle(1, 99, "love")).thenReturn(false);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("{\"reaction\":\"love\"}")));

        servlet.doPost(request, response);
        writer.flush();

        assertTrue(responseOutput.toString().contains("Failed to record reaction"));
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    private void setupValidSession(String pathInfo) throws IOException {
        User user = new User("john", "pass", "john@email.com", 2);
        user.setId(10);

        when(request.getPathInfo()).thenReturn(pathInfo);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
    }
}
