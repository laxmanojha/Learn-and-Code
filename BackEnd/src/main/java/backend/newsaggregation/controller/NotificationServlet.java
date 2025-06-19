package backend.newsaggregation.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

import com.google.gson.Gson;

import backend.newsaggregation.model.NotificationPref;
import backend.newsaggregation.service.NotificationService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/api/notifications/*")
public class NotificationServlet extends HttpServlet {

    private NotificationService notificationService = NotificationService.getInstance();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        int userId = getUserIdFromSession(req);
        if (userId == -1) {
        	resp.getWriter().write(errorJson("User not found"));
        	return;
        }
        resp.setContentType("application/json");

        if (path == null || path.equals("/")) {
            // GET /api/notifications → get all preferences
            List<NotificationPref> prefs = notificationService.getAllPreferences(userId);
            resp.getWriter().write(gson.toJson(prefs));
        } else if (path.equals("/config")) {
            // GET /api/notifications/config → same as above or separate logic
            List<NotificationPref> prefs = notificationService.getAllPreferences(userId);
            resp.getWriter().write(gson.toJson(prefs));
        } else if (path.equals("/keywords")) {
            // GET /api/notifications/keywords → filter only keyword prefs
            List<NotificationPref> keywords = notificationService.getKeywordPreferences(userId);
            resp.getWriter().write(gson.toJson(keywords));
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(errorJson("Endpoint not found"));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        int userId = getUserIdFromSession(req);
        if (userId == -1) {
        	resp.getWriter().write(errorJson("User not found"));
        	return;
        }
        resp.setContentType("application/json");

        if ("/config".equals(path)) {
            Map<String, Object> body = parseJsonBody(req);
            int categoryId = Integer.parseInt((String) body.get("categoryId"));
            boolean enabled = Boolean.parseBoolean(body.get("enabled").toString());

            boolean updated = notificationService.updateCategoryConfig(userId, categoryId, enabled);
            writeSuccess(resp, updated, "Preference updated");
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(errorJson("PUT endpoint not found"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        int userId = getUserIdFromSession(req);
        if (userId == -1) {
        	resp.getWriter().write(errorJson("User not found"));
        	return;
        }
        resp.setContentType("application/json");

        if ("/keywords".equals(path)) {
            Map<String, Object> body = parseJsonBody(req);
            String keyword = (String) body.get("keyword");

            boolean success = notificationService.addKeyword(userId, keyword);
            writeSuccess(resp, success, "Keyword added");
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(errorJson("POST endpoint not found"));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        int userId = getUserIdFromSession(req);
        resp.setContentType("application/json");

        if ("/keywords".equals(path)) {
            Map<String, Object> body = parseJsonBody(req);
            String keyword = (String) body.get("keyword");

            boolean removed = notificationService.removeKeyword(userId, keyword);
            writeSuccess(resp, removed, "Keyword removed");
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(errorJson("DELETE endpoint not found"));
        }
    }

    private Map<String, Object> parseJsonBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return gson.fromJson(sb.toString(), Map.class);
    }

    private void writeSuccess(HttpServletResponse resp, boolean success, String message) throws IOException {
        if (success) {
            resp.getWriter().write("{\"success\": true, \"message\": \"" + message + "\"}");
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"success\": false, \"message\": \"" + message + "\"}");
        }
    }

    private String errorJson(String msg) {
        return "{\"error\": \"" + msg + "\"}";
    }

    private int getUserIdFromSession(HttpServletRequest req) {
    	Object userObj = req.getSession().getAttribute("user");
    	int userId = -1;
        if (userObj instanceof backend.newsaggregation.model.User user) {
            userId = user.getRoleId();
            System.out.println("User id: " + userId);
        }
        return userId;
    }
}
