package backend.newsaggregation.controller;

import backend.newsaggregation.service.CategoryService;
import jakarta.servlet.http.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class AdminCategoryVisibilityServletTest {

    @Mock private CategoryService categoryService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;

    private ByteArrayOutputStream responseOutput;
    private PrintWriter writer;
    private AdminCategoryVisibilityServlet servlet;

    @BeforeEach
    public void setup() throws IOException {
        MockitoAnnotations.openMocks(this);
        responseOutput = new ByteArrayOutputStream();
        writer = new PrintWriter(responseOutput, true);
        when(response.getWriter()).thenReturn(writer);

        servlet = new AdminCategoryVisibilityServlet(categoryService);
    }

    private String getResponseBody() {
        writer.flush();
        return responseOutput.toString();
    }

    @Test
    public void testDoPut_HideCategory_Success() throws IOException {
        when(request.getPathInfo()).thenReturn("/5/hide");
        when(categoryService.hideCategory(5)).thenReturn(true);

        servlet.doPut(request, response);
        String body = getResponseBody();

        assertTrue(body.contains("\"success\":true"));
        assertTrue(body.contains("Category visibility updated"));
    }

    @Test
    public void testDoPut_UnhideCategory_Success() throws IOException {
        when(request.getPathInfo()).thenReturn("/10/unhide");
        when(categoryService.unhideCategory(10)).thenReturn(true);

        servlet.doPut(request, response);
        String body = getResponseBody();

        assertTrue(body.contains("\"success\":true"));
        assertTrue(body.contains("Category visibility updated"));
    }

    @Test
    public void testDoPut_InvalidPath_Null() throws IOException {
        when(request.getPathInfo()).thenReturn(null);

        servlet.doPut(request, response);
        String body = getResponseBody();

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(body.contains("Invalid path"));
    }

    @Test
    public void testDoPut_InvalidPath_TooShort() throws IOException {
        when(request.getPathInfo()).thenReturn("/hide");

        servlet.doPut(request, response);
        String body = getResponseBody();

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(body.contains("Invalid path"));
    }

    @Test
    public void testDoPut_InvalidCategoryIdFormat() throws IOException {
        when(request.getPathInfo()).thenReturn("/abc/hide");

        servlet.doPut(request, response);
        String body = getResponseBody();

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(body.contains("Invalid category ID"));
    }

    @Test
    public void testDoPut_UnknownAction() throws IOException {
        when(request.getPathInfo()).thenReturn("/5/archive");

        servlet.doPut(request, response);
        String body = getResponseBody();

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(body.contains("Unknown action"));
    }

    @Test
    public void testDoPut_HideCategory_Failure() throws IOException {
        when(request.getPathInfo()).thenReturn("/5/hide");
        when(categoryService.hideCategory(5)).thenReturn(false);

        servlet.doPut(request, response);
        String body = getResponseBody();

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        assertTrue(body.contains("Failed to update category visibility"));
    }

    @Test
    public void testDoPut_ServiceException() throws IOException {
        when(request.getPathInfo()).thenReturn("/5/hide");
        when(categoryService.hideCategory(5)).thenThrow(new RuntimeException("db error"));

        servlet.doPut(request, response);
        String body = getResponseBody();

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        assertTrue(body.contains("Server error"));
    }
}
