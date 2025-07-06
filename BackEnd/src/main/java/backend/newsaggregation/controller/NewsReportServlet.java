package backend.newsaggregation.controller;

import backend.newsaggregation.model.NewsArticleReport;
import backend.newsaggregation.service.NewsReportService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.*;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@WebServlet("/api/news-report/*")
public class NewsReportServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private final NewsReportService newsReportService = NewsReportService.getInstance();
    private final Gson gson = new Gson();
	
	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        List<NewsArticleReport> newsArticleReports = newsReportService.getNewsArticleReport();
        resp.getWriter().write(gson.toJson(newsArticleReports));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    	PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo(); // format: /{articleId}
        NewsReportService service = NewsReportService.getInstance();
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

        Object userObj = request.getSession().getAttribute("user");
        int userId = -1;
        if (userObj instanceof backend.newsaggregation.model.User user) {
            userId = ((backend.newsaggregation.model.User) userObj).getId();
        }
        
        if (userId == 0) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not logged in");
            return;
        }
        
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> body;
        try {
            body = mapper.readValue(request.getReader(), new TypeReference<>() {});
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON body");
            return;
        }

        String comment = body.get("comment");

        try {

            boolean success = service.reportArticle(userId, articleId, comment);

            response.setContentType("application/json");
            if (success) {
                out.write(successJson("Article reported successfully."));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.write(errorJson("Failed to report article."));
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write(errorJson("Server error."));
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
