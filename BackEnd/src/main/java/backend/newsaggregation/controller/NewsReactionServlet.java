package backend.newsaggregation.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

import backend.newsaggregation.service.NewsReactionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.*;
import java.util.Map;

@WebServlet("/api/news-reaction/*")
public class NewsReactionServlet extends HttpServlet {
	NewsReactionService service = NewsReactionService.getInstance();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo(); // format: /{articleId}
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Article ID is missing in URL");
            return;
        }

        String[] splits = pathInfo.split("/");
        if (splits.length < 2) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL format");
            return;
        }

        int articleId;
        try {
            articleId = Integer.parseInt(splits[1]);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Article ID");
            return;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write(errorJson("Login required"));
            return;
        }

        int userId = -1;
        Object userObj = session.getAttribute("user");
        if (userObj instanceof backend.newsaggregation.model.User user) {
            userId = user.getId();
        }

        if (userId <= 0) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid user");
            return;
        }

        // Parse JSON Body
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> body;
        try {
            body = mapper.readValue(request.getReader(), new TypeReference<>() {});
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON body");
            return;
        }

        String reaction = body.get("reaction");
        if (reaction == null || reaction.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing reaction field");
            return;
        }

        try {
            boolean success = service.reactToArticle(userId, articleId, reaction.trim());

            if (success) {
                out.write(successJson("Reaction recorded."));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.write(errorJson("Failed to record reaction."));
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write(errorJson("Server error."));
        } finally {
            out.close();
        }
    }

    private String successJson(String msg) {
        JsonObject json = new JsonObject();
        json.addProperty("success", true);
        json.addProperty("message", msg);
        return json.toString();
    }

    private String errorJson(String msg) {
        JsonObject json = new JsonObject();
        json.addProperty("success", false);
        json.addProperty("message", msg);
        return json.toString();
    }
}

