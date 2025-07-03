package backend.newsaggregation.controller;


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

@WebServlet("/news/*/reaction")
public class NewsReactionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    	PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo(); // format: /{articleId}/reaction
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

//        int userId = (int) request.getSession().getAttribute("userId");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write(errorJson("Login required"));
            return;
        }

        Object userObj = request.getSession().getAttribute("user");
        int userId = -1;
        if (userObj instanceof backend.newsaggregation.model.User user) {
            userId = ((backend.newsaggregation.model.User) userObj).getId();
        }
        if (userId == 0) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not logged in");
            return;
        }

        // Parse JSON Body
        BufferedReader reader = request.getReader();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> body = mapper.readValue(reader, Map.class);

        String reaction = body.get("reaction");
        if (reaction == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing reaction field");
            return;
        }

        // Process
        try {
           NewsReactionService service = NewsReactionService.getInstance();

            boolean success = service.reactToArticle(userId, articleId, reaction);

            response.setContentType("application/json");
            if (success) {
                out.write("{\"status\": \"success\", \"message\": \"Reaction recorded.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.write("{\"status\": \"error\", \"message\": \"Failed to record reaction.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\": \"error\", \"message\": \"Server error.\"}");
        }
    }
    
    private String errorJson(String msg) {
        JsonObject json = new JsonObject();
        json.addProperty("success", false);
        json.addProperty("message", msg);
        return json.toString();
    }
}
