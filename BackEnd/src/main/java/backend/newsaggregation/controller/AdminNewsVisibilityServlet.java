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
    private final NewsReportService newsReportService;

    public AdminNewsVisibilityServlet() {
        this(NewsReportService.getInstance());
    }

    public AdminNewsVisibilityServlet(NewsReportService newsReportService) {
        this.newsReportService = newsReportService;
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String pathInfo = request.getPathInfo();
            if (!isValidPath(pathInfo)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(errorJson("Invalid path"));
                return;
            }

            String[] parts = pathInfo.split("/");
            int newsId = parseNewsId(parts[1], response, out);
            if (newsId == -1) return;

            String action = parts[2];
            handleVisibilityUpdate(newsId, action, response, out);
        }
    }

    private boolean isValidPath(String pathInfo) {
        return pathInfo != null && pathInfo.split("/").length >= 3;
    }

    private int parseNewsId(String newsIdStr, HttpServletResponse response, PrintWriter out) throws IOException {
        try {
            return Integer.parseInt(newsIdStr);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(errorJson("Invalid news ID"));
            return -1;
        }
    }

    private void handleVisibilityUpdate(int newsId, String action, HttpServletResponse response, PrintWriter out) throws IOException {
        boolean success;
        try {
            switch (action.toLowerCase()) {
                case "hide" -> success = newsReportService.hideArticle(newsId);
                case "unhide" -> success = newsReportService.unHideArticle(newsId);
                default -> {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.write(errorJson("Unknown action"));
                    return;
                }
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
