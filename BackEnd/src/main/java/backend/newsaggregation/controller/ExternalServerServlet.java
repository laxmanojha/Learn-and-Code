package backend.newsaggregation.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import backend.newsaggregation.model.User;
import backend.newsaggregation.service.ExternalServerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/servers/*")
public class ExternalServerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ExternalServerService serverService;
    private final Gson gson = new Gson();

    public ExternalServerServlet() {
        this(ExternalServerService.getInstance());
    }

    public ExternalServerServlet(ExternalServerService serverService) {
        this.serverService = serverService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAdmin(request)) {
            respondForbidden(response);
            return;
        }

        prepareJsonResponse(response);

        String path = request.getPathInfo();
        switch (path == null ? "" : path) {
            case "":
            case "/":
                respondWithJson(response, HttpServletResponse.SC_OK,
                        gson.toJson(serverService.getAllServersBasicDetails()));
                break;
            case "/details":
                respondWithJson(response, HttpServletResponse.SC_OK,
                        gson.toJson(serverService.getAllServersWithApiKeys()));
                break;
            default:
                respondWithError(response, HttpServletResponse.SC_NOT_FOUND, "Invalid endpoint");
                break;
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAdmin(request)) {
            respondForbidden(response);
            return;
        }

        prepareJsonResponse(response);

        String path = request.getPathInfo();

        if (path == null || path.equals("/")) {
            respondWithError(response, HttpServletResponse.SC_BAD_REQUEST, "Missing server ID in URL");
            return;
        }

        String[] parts = path.split("/");
        if (parts.length != 2) {
            respondWithError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid server ID format");
            return;
        }

        try (BufferedReader reader = request.getReader()) {
            int serverId = Integer.parseInt(parts[1]);

            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            String newApiKey = json.get("apiKey").getAsString();

            boolean updated = serverService.updateApiKey(serverId, newApiKey);

            if (updated) {
                respondWithMessage(response, HttpServletResponse.SC_OK, "API key updated successfully");
            } else {
                respondWithError(response, HttpServletResponse.SC_NOT_FOUND, "Server not found or update failed");
            }
        } catch (NumberFormatException e) {
            respondWithError(response, HttpServletResponse.SC_BAD_REQUEST, "Server ID must be a number");
        }
    }

    private boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute("user");
        return userObj instanceof User && ((User) userObj).getRoleId() == 1;
    }

    private void prepareJsonResponse(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    private void respondForbidden(HttpServletResponse response) throws IOException {
        respondWithError(response, HttpServletResponse.SC_FORBIDDEN, "Access denied: Admins only");
    }

    private void respondWithMessage(HttpServletResponse response, int statusCode, String message) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("success", true);
        json.addProperty("message", message);
        respondWithJson(response, statusCode, json.toString());
    }

    private void respondWithError(HttpServletResponse response, int statusCode, String message) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("success", false);
        json.addProperty("message", message);
        respondWithJson(response, statusCode, json.toString());
    }

    private void respondWithJson(HttpServletResponse response, int statusCode, String jsonData) throws IOException {
        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.write(jsonData);
            out.flush();
        }
    }
}
