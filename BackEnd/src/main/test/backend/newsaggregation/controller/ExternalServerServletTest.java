package backend.newsaggregation.controller;

import backend.newsaggregation.model.User;
import backend.newsaggregation.service.ExternalServerService;
import jakarta.servlet.http.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExternalServerServletTest {

    private ExternalServerServlet servlet;

    @Mock
    private ExternalServerService serverService;

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

        servlet = new ExternalServerServlet(serverService);

        responseOutput = new ByteArrayOutputStream();
        writer = new PrintWriter(responseOutput, true);

        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void testGetAllServersBasicDetails_AsAdmin() throws Exception {
        User admin = new User("admin", "pass", "admin@email.com", 1);
        when(session.getAttribute("user")).thenReturn(admin);
        when(request.getPathInfo()).thenReturn("/");
        when(serverService.getAllServersBasicDetails()).thenReturn(List.of());

        servlet.doGet(request, response);

        writer.flush();
        String output = responseOutput.toString();
        assertTrue(output.contains("[]"));
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testGetAllServersWithApiKeys_AsAdmin() throws Exception {
        User admin = new User("admin", "pass", "admin@email.com", 1);
        when(session.getAttribute("user")).thenReturn(admin);
        when(request.getPathInfo()).thenReturn("/details");
        when(serverService.getAllServersWithApiKeys()).thenReturn(List.of());

        servlet.doGet(request, response);

        writer.flush();
        String output = responseOutput.toString();
        System.out.println("testGetAllServersWithApiKeys_AsAdmin" + output);
        assertTrue(output.contains("[]"));
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testGet_InvalidEndpoint() throws Exception {
        when(session.getAttribute("user")).thenReturn(new User("admin", "pass", "admin@email.com", 1));
        when(request.getPathInfo()).thenReturn("/bad");

        servlet.doGet(request, response);
        writer.flush();

        assertTrue(responseOutput.toString().contains("Invalid endpoint"));
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    public void testGet_NonAdminUserBlocked() throws Exception {
        when(session.getAttribute("user")).thenReturn(new User("user", "pass", "user@email.com", 2));

        servlet.doGet(request, response);
        writer.flush();

        assertTrue(responseOutput.toString().contains("Access denied"));
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    public void testPut_UpdateApiKey_Success() throws Exception {
        when(session.getAttribute("user")).thenReturn(new User("admin", "pass", "admin@email.com", 1));
        when(request.getPathInfo()).thenReturn("/42");
        when(serverService.updateApiKey(42, "new-key")).thenReturn(true);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("{\"apiKey\":\"new-key\"}")));

        servlet.doPut(request, response);
        writer.flush();

        String output = responseOutput.toString();
        System.out.println("testPut_UpdateApiKey_Success: " + output);
        assertTrue(output.contains("updated successfully"));
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testPut_InvalidServerIdFormat() throws Exception {
        when(session.getAttribute("user")).thenReturn(new User("admin", "pass", "admin@email.com", 1));
        when(request.getPathInfo()).thenReturn("/abc");

        servlet.doPut(request, response);
        writer.flush();

        assertTrue(responseOutput.toString().contains("must be a number"));
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testPut_MissingServerId() throws Exception {
        when(session.getAttribute("user")).thenReturn(new User("admin", "pass", "admin@email.com", 1));
        when(request.getPathInfo()).thenReturn("/");

        servlet.doPut(request, response);
        writer.flush();

        assertTrue(responseOutput.toString().contains("Missing server ID"));
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testPut_NonAdminBlocked() throws Exception {
        when(session.getAttribute("user")).thenReturn(new User("user", "pass", "user@email.com", 2));

        servlet.doPut(request, response);
        writer.flush();

        assertTrue(responseOutput.toString().contains("Access denied"));
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    public void testPut_UpdateFails_NotFound() throws Exception {
        when(session.getAttribute("user")).thenReturn(new User("admin", "pass", "admin@email.com", 1));
        when(request.getPathInfo()).thenReturn("/999");
        when(serverService.updateApiKey(999, "bad-key")).thenReturn(false);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("{\"apiKey\":\"bad-key\"}")));

        servlet.doPut(request, response);
        writer.flush();

        assertTrue(responseOutput.toString().contains("update failed"));
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
}
