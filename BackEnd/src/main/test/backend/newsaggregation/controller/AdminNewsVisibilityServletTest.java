package backend.newsaggregation.controller;

import backend.newsaggregation.service.NewsReportService;
import jakarta.servlet.http.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminNewsVisibilityServletTest {

    @InjectMocks
    private AdminNewsVisibilityServlet servlet;

    @Mock
    private NewsReportService mockNewsReportService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private ByteArrayOutputStream responseOutput;
    private PrintWriter writer;

    @BeforeEach
    public void setup() throws IOException {
        MockitoAnnotations.openMocks(this);
        responseOutput = new ByteArrayOutputStream();
        writer = new PrintWriter(responseOutput, true);

        when(response.getWriter()).thenReturn(writer);

        servlet = new AdminNewsVisibilityServlet(mockNewsReportService);
    }

    @Test
    public void testHideArticle_Success() throws IOException {
        when(request.getPathInfo()).thenReturn("/42/hide");
        when(mockNewsReportService.hideArticle(42)).thenReturn(true);

        servlet.doPut(request, response);
        writer.flush();

        String output = responseOutput.toString();
        assertTrue(output.contains("success"));
        assertTrue(output.contains("Action successful."));
        verify(mockNewsReportService).hideArticle(42);
        verify(response, never()).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testUnhideArticle_Success() throws IOException {
        when(request.getPathInfo()).thenReturn("/7/unhide");
        when(mockNewsReportService.unHideArticle(7)).thenReturn(true);

        servlet.doPut(request, response);
        writer.flush();

        String output = responseOutput.toString();
        assertTrue(output.contains("success"));
        assertTrue(output.contains("Action successful."));
        verify(mockNewsReportService).unHideArticle(7);
    }

    @Test
    public void testInvalidPath() throws IOException {
        when(request.getPathInfo()).thenReturn("/invalidOnlyOneSegment");

        servlet.doPut(request, response);
        writer.flush();

        String output = responseOutput.toString();
        assertTrue(output.contains("Invalid path"));
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockNewsReportService, never()).hideArticle(anyInt());
    }

    @Test
    public void testInvalidNewsId() throws IOException {
        when(request.getPathInfo()).thenReturn("/abc/hide");

        servlet.doPut(request, response);
        writer.flush();

        String output = responseOutput.toString();
        assertTrue(output.contains("Invalid news ID"));
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockNewsReportService, never()).hideArticle(anyInt());
    }

    @Test
    public void testUnknownAction() throws IOException {
        when(request.getPathInfo()).thenReturn("/10/delete");

        servlet.doPut(request, response);
        writer.flush();

        String output = responseOutput.toString();
        assertTrue(output.contains("Unknown action"));
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockNewsReportService, never()).hideArticle(anyInt());
    }

    @Test
    public void testHideArticle_Failure() throws IOException {
        when(request.getPathInfo()).thenReturn("/11/hide");
        when(mockNewsReportService.hideArticle(11)).thenReturn(false);

        servlet.doPut(request, response);
        writer.flush();

        String output = responseOutput.toString();
        assertTrue(output.contains("Failed to update article visibility"));
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testServiceThrowsException() throws IOException {
        when(request.getPathInfo()).thenReturn("/15/hide");
        when(mockNewsReportService.hideArticle(15)).thenThrow(new RuntimeException("Simulated error"));

        servlet.doPut(request, response);
        writer.flush();

        String output = responseOutput.toString();
        assertTrue(output.contains("Server error"));
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}
