package backend.newsaggregation.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

import com.google.gson.Gson;

import backend.newsaggregation.model.NewsArticle;
import backend.newsaggregation.model.NotificationPref;
import backend.newsaggregation.model.NotificationPreference;
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
        	List<NewsArticle> newsArticles = notificationService.getConsoleNotifications(userId);
        	resp.getWriter().write(gson.toJson(newsArticles));
        } else if (path.equals("/preferences")) {
            List<NotificationPreference> prefs = notificationService.getAllPreferences(userId);
            resp.getWriter().write(gson.toJson(prefs));
        } else if (path.equals("/preferences/category")) {
            // GET /api/notifications/category → filter only catgory prefs
            List<NotificationPreference> keywords = notificationService.getCategoryPreferences(userId);
            resp.getWriter().write(gson.toJson(keywords));
        } else if (path.equals("/preferences/keywords")) {
        	// GET /api/notifications/keywords → filter only keyword prefs
        	List<NotificationPreference> keywords = notificationService.getKeywordPreferences(userId);
        	resp.getWriter().write(gson.toJson(keywords));
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(errorJson("Endpoint not found"));
        }
    }

//    @Override
//    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        String path = req.getPathInfo();
//        int userId = getUserIdFromSession(req);
//        if (userId == -1) {
//        	resp.getWriter().write(errorJson("User not found"));
//        	return;
//        }
//        resp.setContentType("application/json");
//
//        if ("/config/category".equals(path)) {
//            Map<String, Object> body = parseJsonBody(req);
//            int categoryId = Integer.parseInt((String) body.get("categoryId"));
//            boolean enabled = Boolean.parseBoolean(body.get("enabled").toString());
//
//            boolean updated = notificationService.updateCategoryConfig(userId, categoryId, enabled);
//            writeSuccess(resp, updated, "Preference updated");
//        } else if ("/config/keywords".equals(path)) {
//    		Map<String, Object> body = parseJsonBody(req);
//    		List<String> keywords = (List<String>) body.get("keywords");
//    		boolean enabled = Boolean.parseBoolean(body.get("enabled").toString());
//    		
//    		boolean updated = notificationService.updateCategoryConfig(userId, categoryId, enabled);
//    		writeSuccess(resp, updated, "Preference updated");
//    	} else {
//	        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
//	        resp.getWriter().write(errorJson("PUT endpoint not found"));
//        }
//    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        int userId = getUserIdFromSession(req);
        if (userId == -1) {
        	resp.getWriter().write(errorJson("User not found"));
        	return;
        }
        resp.setContentType("application/json");

        if ("/config/category".equals(path)) {
            Map<String, Object> body = parseJsonBody(req);
            int categoryId = Integer.parseInt((String) body.get("categoryId"));
            @SuppressWarnings("unchecked")
			List<String> keywords = (List<String>) body.get("keywords");

            boolean updated = notificationService.updateCategoryConfig(userId, categoryId, keywords);
            writeSuccess(resp, updated, "Preference updated");
        } else if ("config/keywords".equals(path)) {
            Map<String, Object> body = parseJsonBody(req);

            @SuppressWarnings("unchecked")
			List<String> keywords = (List<String>) body.get("keywords");

            boolean success = notificationService.addKeywords(userId, keywords);
            writeSuccess(resp, success, "Keyword(s) added");
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(errorJson("POST endpoint not found"));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	String path = req.getPathInfo();
        int userId = getUserIdFromSession(req);
        if (userId == -1) {
        	resp.getWriter().write(errorJson("User not found"));
        	return;
        }
        resp.setContentType("application/json");

        if ("/config/category".equals(path)) {
            Map<String, Object> body = parseJsonBody(req);
            int categoryId = Integer.parseInt((String) body.get("categoryId"));

            boolean updated = notificationService.updateCategoryConfig(userId, categoryId);
            writeSuccess(resp, updated, "Preference updated");
        } else if ("config/keywords".equals(path)) {

            boolean success = notificationService.removeKeywords(userId);
            writeSuccess(resp, success, "Keyword(s) removed");
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
            userId = user.getId();
            System.out.println("User id: " + userId);
        }
        return userId;
    }
}
