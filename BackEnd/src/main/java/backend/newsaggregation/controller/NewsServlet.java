package backend.newsaggregation.controller;

import com.google.gson.Gson;

import backend.newsaggregation.service.NewsService;
import backend.newsaggregation.service.SavedArticleService;
import backend.newsaggregation.service.SearchNewsService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.List;

import com.google.gson.JsonObject;

import backend.newsaggregation.model.NewsArticle;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/api/news/*")
public class NewsServlet extends HttpServlet {
    private final NewsService newsService = NewsService.getInstance();
    private final SavedArticleService savedArticleService = SavedArticleService.getInstance();
    private final SearchNewsService searchNewsService = SearchNewsService.getInstance();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String path = request.getPathInfo();
        PrintWriter out = response.getWriter();

        try {
            if (path == null || path.equals("/today")) {
                // GET /api/news/today
                List<NewsArticle> articles = newsService.getTodayHeadlines();
                out.write(gson.toJson(articles));

            } else if (path.equals("/date-range")) {
                // GET /api/news/date-range?start=...&end=...&type=...
                String start = request.getParameter("start");
                String end = request.getParameter("end");
                String type = request.getParameter("type");

                Date startDate = Date.valueOf(start);
                Date endDate = Date.valueOf(end);

                List<NewsArticle> articles = (type == null || type.equals("all")) ?
                        newsService.getHeadlinesByDateRange(startDate, endDate) :
                        newsService.getHeadlinesByDateRangeAndCategory(startDate, endDate, type);

                out.write(gson.toJson(articles));

            } else if (path.equals("/saved")) {
                // GET /api/news/saved
                HttpSession session = request.getSession(false);
                if (session == null || session.getAttribute("userId") == null) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.write(errorJson("Login required"));
                    return;
                }

                int userId = (int) session.getAttribute("userId");
                List<NewsArticle> articles = savedArticleService.getSavedArticlesByUser(userId);
                out.write(gson.toJson(articles));

            } else if (path.equals("/search")) {
                // GET /api/news/search?query=...&start=...&end=...&sort=...
                String query = request.getParameter("query");
                String start = request.getParameter("start");
                String end = request.getParameter("end");
                String sort = request.getParameter("sort");

                if (query == null || query.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.write(errorJson("Query parameter is required"));
                    return;
                }

                List<NewsArticle> results = searchNewsService.searchArticles(query, start, end, sort);
                out.write(gson.toJson(results));

            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write(errorJson("Invalid endpoint"));
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(errorJson("Error: " + e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        String path = request.getPathInfo(); // e.g. /{id}/save

        try {
            String[] parts = path.split("/");
            if (parts.length == 3 && parts[2].equals("save")) {
                int articleId = Integer.parseInt(parts[1]);

                HttpSession session = request.getSession(false);
                if (session == null || session.getAttribute("userId") == null) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.write(errorJson("Login required"));
                    return;
                }

                int userId = (int) session.getAttribute("userId");
                boolean saved = savedArticleService.saveArticle(userId, articleId);

                JsonObject result = new JsonObject();
                result.addProperty("success", saved);
                result.addProperty("message", saved ? "Article saved." : "Could not save article.");
                out.write(result.toString());
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write(errorJson("Invalid save request"));
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(errorJson("Error: " + e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        String path = request.getPathInfo(); // expected: /saved/{id}

        try {
            if (path != null && path.startsWith("/saved/")) {
                HttpSession session = request.getSession(false);
                if (session == null || session.getAttribute("userId") == null) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.write(errorJson("Login required"));
                    return;
                }

                int userId = (int) session.getAttribute("userId");
                int articleId = Integer.parseInt(path.substring("/saved/".length()));

                boolean deleted = savedArticleService.deleteSavedArticle(userId, articleId);
                JsonObject result = new JsonObject();
                result.addProperty("success", deleted);
                result.addProperty("message", deleted ? "Article deleted." : "Delete failed.");
                out.write(result.toString());
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write(errorJson("Invalid delete request"));
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(errorJson("Error: " + e.getMessage()));
        }
    }

    private String errorJson(String msg) {
        JsonObject json = new JsonObject();
        json.addProperty("success", false);
        json.addProperty("message", msg);
        return json.toString();
    }
}
