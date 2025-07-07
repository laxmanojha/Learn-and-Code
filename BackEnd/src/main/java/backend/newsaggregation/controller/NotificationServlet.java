package backend.newsaggregation.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

import com.google.gson.Gson;

import backend.newsaggregation.model.NewsArticle;
import backend.newsaggregation.model.NotificationPreference;
import backend.newsaggregation.service.NotificationService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/notifications/*")
public class NotificationServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final NotificationService notificationService = NotificationService.getInstance();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        int userId = getUserIdFromSession(req);
        if (userId == -1) {
            respondError(resp, "User not found");
            return;
        }
        resp.setContentType("application/json");

        switch (path == null ? "/" : path) {
            case "/":
                handleConsoleNotifications(resp, userId);
                break;
            case "/preferences":
                handleAllPreferences(resp, userId);
                break;
            case "/preferences/category":
                handleCategoryPreferences(resp, userId);
                break;
            case "/preferences/keywords":
                handleKeywordPreferences(resp, userId);
                break;
            default:
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                respondError(resp, "Endpoint not found");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        int userId = getUserIdFromSession(req);
        if (userId == -1) {
            respondError(resp, "User not found");
            return;
        }
        resp.setContentType("application/json");

        switch (path) {
            case "/config/category":
                handleCategoryConfigUpdate(req, resp, userId);
                break;
            case "/config/keywords":
                handleAddKeywords(req, resp, userId);
                break;
            default:
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                respondError(resp, "POST endpoint not found");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        int userId = getUserIdFromSession(req);
        if (userId == -1) {
            respondError(resp, "User not found");
            return;
        }
        resp.setContentType("application/json");

        switch (path) {
            case "/config/category":
                handleCategoryConfigDelete(req, resp, userId);
                break;
            case "/config/keywords":
                handleRemoveKeywords(resp, userId);
                break;
            default:
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                respondError(resp, "DELETE endpoint not found");
        }
    }

    private void handleConsoleNotifications(HttpServletResponse resp, int userId) throws IOException {
        List<NewsArticle> newsArticles = notificationService.getConsoleNotifications(userId);
        resp.getWriter().write(gson.toJson(newsArticles));
    }

    private void handleAllPreferences(HttpServletResponse resp, int userId) throws IOException {
        List<NotificationPreference> prefs = notificationService.getAllPreferences(userId);
        resp.getWriter().write(gson.toJson(prefs));
    }

    private void handleCategoryPreferences(HttpServletResponse resp, int userId) throws IOException {
        List<NotificationPreference> prefs = notificationService.getCategoryPreferences(userId);
        resp.getWriter().write(gson.toJson(prefs));
    }

    private void handleKeywordPreferences(HttpServletResponse resp, int userId) throws IOException {
        NotificationPreference pref = notificationService.getKeywordPreferences(userId);
        resp.getWriter().write(gson.toJson(pref));
    }

    private void handleCategoryConfigUpdate(HttpServletRequest req, HttpServletResponse resp, int userId) throws IOException {
        Map<String, Object> body = parseJsonBody(req);
        int categoryId = Integer.parseInt((String) body.get("categoryId"));
        @SuppressWarnings("unchecked")
        List<String> keywords = (List<String>) body.get("keywords");

        boolean updated = notificationService.updateCategoryConfig(userId, categoryId, keywords);
        writeSuccess(resp, updated, "Preference updated");
    }

    private void handleAddKeywords(HttpServletRequest req, HttpServletResponse resp, int userId) throws IOException {
        Map<String, Object> body = parseJsonBody(req);
        @SuppressWarnings("unchecked")
        List<String> keywords = (List<String>) body.get("keywords");

        boolean success = notificationService.addKeywords(userId, keywords);
        writeSuccess(resp, success, "Keyword(s) added");
    }

    private void handleCategoryConfigDelete(HttpServletRequest req, HttpServletResponse resp, int userId) throws IOException {
        Map<String, Object> body = parseJsonBody(req);
        int categoryId = Integer.parseInt((String) body.get("categoryId"));

        boolean updated = notificationService.updateCategoryConfig(userId, categoryId);
        writeSuccess(resp, updated, "Preference updated");
    }

    private void handleRemoveKeywords(HttpServletResponse resp, int userId) throws IOException {
        boolean success = notificationService.removeKeywords(userId);
        writeSuccess(resp, success, "Keyword(s) removed");
    }

    @SuppressWarnings("unchecked")
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

    private void respondError(HttpServletResponse resp, String msg) throws IOException {
        resp.getWriter().write("{\"error\": \"" + msg + "\"}");
    }

    private int getUserIdFromSession(HttpServletRequest req) {
        Object userObj = req.getSession().getAttribute("user");
        if (userObj instanceof backend.newsaggregation.model.User user) {
            return user.getId();
        }
        return -1;
    }
}
