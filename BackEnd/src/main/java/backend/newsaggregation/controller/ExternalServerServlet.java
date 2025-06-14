package backend.newsaggregation.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import backend.newsaggregation.model.ExternalServer;
import backend.newsaggregation.service.ExternalServerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/servers/*")
public class ExternalServerServlet extends HttpServlet {

    private final ExternalServerService serverService = ExternalServerService.getInstance();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isAdmin(request)) {
            respondForbidden(response);
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();
        try (PrintWriter out = response.getWriter()) {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<ExternalServer> servers = serverService.getAllServersBasicDetails();
                out.write(gson.toJson(servers));
                response.setStatus(HttpServletResponse.SC_OK);
            } else if (pathInfo.equals("/details")) {
                List<ExternalServer> apiKeys = serverService.getAllServersWithApiKeys();
                out.write(gson.toJson(apiKeys));
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write(errorJson("Invalid endpoint"));
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isAdmin(request)) {
            respondForbidden(response);
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();
        try (BufferedReader reader = request.getReader(); PrintWriter out = response.getWriter()) {
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(errorJson("Missing server ID in URL"));
                return;
            }

            String[] parts = pathInfo.split("/");
            if (parts.length != 2) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(errorJson("Invalid server ID format"));
                return;
            }

            int serverId = Integer.parseInt(parts[1]);
            JsonObject requestBody = JsonParser.parseReader(reader).getAsJsonObject();
            String newApiKey = requestBody.get("apiKey").getAsString();

            boolean updated = serverService.updateApiKey(serverId, newApiKey);

            if (updated) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.write(successJson("API key updated successfully"));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write(errorJson("Server not found or update failed"));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(errorJson("Server ID must be a number"));
        }
    }


    private boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute("user");
        if (userObj instanceof backend.newsaggregation.model.User user) {
            return user.getRoleId() == 1;
        }
        return false;
    }

    private void respondForbidden(HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        try (PrintWriter out = response.getWriter()) {
            out.write(errorJson("Access denied: Admins only"));
        }
    }

    private String successJson(String message) {
        JsonObject json = new JsonObject();
        json.addProperty("success", true);
        json.addProperty("message", message);
        return json.toString();
    }

    private String errorJson(String message) {
        JsonObject json = new JsonObject();
        json.addProperty("success", false);
        json.addProperty("message", message);
        return json.toString();
    }
}

