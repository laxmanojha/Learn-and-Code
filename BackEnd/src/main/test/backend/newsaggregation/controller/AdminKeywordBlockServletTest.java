package backend.newsaggregation.controller;

import backend.newsaggregation.model.HiddenKeyword;
import backend.newsaggregation.service.AdminKeywordService;
import jakarta.servlet.http.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminKeywordBlockServletTest {

    @Mock private AdminKeywordService mockKeywordService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;

    private ByteArrayOutputStream outputStream;
    private PrintWriter writer;
    private AdminKeywordBlockServlet servlet;

    @BeforeEach
    public void setup() throws IOException {
        MockitoAnnotations.openMocks(this);
        outputStream = new ByteArrayOutputStream();
        writer = new PrintWriter(outputStream, true);
        when(response.getWriter()).thenReturn(writer);

        servlet = new AdminKeywordBlockServlet(mockKeywordService);
    }

    @Test
    public void testDoPost_ValidKeyword_Success() throws IOException {
        String keywordJson = "{\"keyword\": \"fake-news\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(keywordJson)));
        when(mockKeywordService.addKeyword("fake-news")).thenReturn(true);

        servlet.doPost(request, response);
        writer.flush();

        String json = outputStream.toString();
        assertTrue(json.contains("success"));
        assertTrue(json.contains("Keyword added"));
    }

    @Test
    public void testDoPost_MissingKeywordField() throws IOException {
        String keywordJson = "{}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(keywordJson)));

        servlet.doPost(request, response);
        writer.flush();

        String json = outputStream.toString();
        assertTrue(json.contains("Keyword is required"));
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPost_KeywordAlreadyExists() throws IOException {
        String keywordJson = "{\"keyword\": \"spam\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(keywordJson)));
        when(mockKeywordService.addKeyword("spam")).thenReturn(false);

        servlet.doPost(request, response);
        writer.flush();

        String json = outputStream.toString();
        assertTrue(json.contains("Failed to add keyword"));
        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
    }

    @Test
    public void testDoDelete_ValidId_Success() throws IOException {
        when(request.getPathInfo()).thenReturn("/5");
        when(mockKeywordService.deleteKeyword(5)).thenReturn(true);

        servlet.doDelete(request, response);
        writer.flush();

        String json = outputStream.toString();
        assertTrue(json.contains("Keyword deleted"));
    }

    @Test
    public void testDoDelete_InvalidIdFormat() throws IOException {
        when(request.getPathInfo()).thenReturn("/abc");

        servlet.doDelete(request, response);
        writer.flush();

        String json = outputStream.toString();
        assertTrue(json.contains("Invalid ID"));
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoDelete_MissingId() throws IOException {
        when(request.getPathInfo()).thenReturn(null);

        servlet.doDelete(request, response);
        writer.flush();

        String json = outputStream.toString();
        assertTrue(json.contains("Keyword ID missing"));
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoDelete_DeleteFails() throws IOException {
        when(request.getPathInfo()).thenReturn("/10");
        when(mockKeywordService.deleteKeyword(10)).thenReturn(false);

        servlet.doDelete(request, response);
        writer.flush();

        String json = outputStream.toString();
        assertTrue(json.contains("Keyword deletion failed"));
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testDoGet_ReturnsKeywords() throws IOException {
        List<HiddenKeyword> mockKeywords = List.of(
            new HiddenKeyword(1, "politics", null),
            new HiddenKeyword(2, "violence", null)
        );
        when(mockKeywordService.getAllKeywords()).thenReturn(mockKeywords);

        servlet.doGet(request, response);
        writer.flush();

        String json = outputStream.toString();
        assertTrue(json.contains("politics"));
        assertTrue(json.contains("violence"));
    }
}
