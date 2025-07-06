package backend.newsaggregation.controller;

import java.io.IOException;
import java.io.PrintWriter;
import com.google.gson.JsonObject;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import backend.newsaggregation.service.NewsReportService;

@WebServlet("/api/admin/news/*")
public class AdminNewsVisibilityServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static NewsReportService newsReportService = NewsReportService.getInstance();

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo(); // Expected format: /{newsId}/hide or /{newsId}/unhide
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        if (pathInfo == null || pathInfo.split("/").length < 3) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(errorJson("Invalid path"));
            return;
        }

        String[] parts = pathInfo.split("/");
        int newsId;
        try {
            newsId = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(errorJson("Invalid news ID"));
            return;
        }

        String action = parts[2]; // "hide" or "unhide"
        boolean success = false;

        try {

            if ("hide".equalsIgnoreCase(action)) {
                success = newsReportService.hideArticle(newsId);
            } else if ("unhide".equalsIgnoreCase(action)) {
            	success = newsReportService.unHideArticle(newsId);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(errorJson("Unknown action"));
                return;
            }

            if (success) {
                out.write(successJson("Action successful."));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.write(errorJson("Failed to update article visibility."));
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write(errorJson("Server error."));
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
