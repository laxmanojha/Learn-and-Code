package backend.newsaggregation.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

import backend.newsaggregation.model.User;
import backend.newsaggregation.service.NewsReactionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@WebServlet("/api/news-reaction/*")
public class NewsReactionServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final NewsReactionService service = NewsReactionService.getInstance();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        prepareJsonResponse(response);

        int articleId = extractArticleId(request, response);
        if (articleId == -1) return;

        int userId = extractUserId(request, response);
        if (userId == -1) return;

        Map<String, String> body = parseRequestBody(request, response);
        if (body == null) return;

        String reaction = body.get("reaction");
        if (reaction == null || reaction.isBlank()) {
            respondWithError(response, HttpServletResponse.SC_BAD_REQUEST, "Missing reaction field");
            return;
        }

        try {
            boolean success = service.reactToArticle(userId, articleId, reaction.trim());
            if (success) {
                respondWithMessage(response, HttpServletResponse.SC_OK, "Reaction recorded.");
            } else {
                respondWithError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to record reaction.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            respondWithError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error.");
        }
    }

    private void prepareJsonResponse(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    private int extractArticleId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            respondWithError(response, HttpServletResponse.SC_BAD_REQUEST, "Article ID is missing in URL");
            return -1;
        }

        String[] parts = pathInfo.split("/");
        if (parts.length < 2) {
            respondWithError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid URL format");
            return -1;
        }

        try {
            return Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            respondWithError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid Article ID");
            return -1;
        }
    }

    private int extractUserId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            respondWithError(response, HttpServletResponse.SC_UNAUTHORIZED, "Login required");
            return -1;
        }

        Object userObj = session.getAttribute("user");
        if (userObj instanceof User user && user.getId() > 0) {
            return user.getId();
        }

        respondWithError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid user");
        return -1;
    }

    private Map<String, String> parseRequestBody(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            return mapper.readValue(request.getReader(), new TypeReference<>() {});
        } catch (Exception e) {
            respondWithError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON body");
            return null;
        }
    }

    private void respondWithMessage(HttpServletResponse response, int status, String message) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("success", true);
        json.addProperty("message", message);
        sendResponse(response, status, json);
    }

    private void respondWithError(HttpServletResponse response, int status, String message) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("success", false);
        json.addProperty("message", message);
        sendResponse(response, status, json);
    }

    private void sendResponse(HttpServletResponse response, int status, JsonObject json) throws IOException {
        response.setStatus(status);
        try (PrintWriter out = response.getWriter()) {
            out.write(json.toString());
            out.flush();
        }
    }
}
