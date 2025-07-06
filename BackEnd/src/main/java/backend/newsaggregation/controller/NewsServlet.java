package backend.newsaggregation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import backend.newsaggregation.model.Category;
import backend.newsaggregation.model.NewsArticle;
import backend.newsaggregation.model.User;
import backend.newsaggregation.service.CategoryService;
import backend.newsaggregation.service.NewsService;
import backend.newsaggregation.service.PersonalizedNewsService;
import backend.newsaggregation.service.SavedArticleService;
import backend.newsaggregation.service.SearchNewsService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.List;

@WebServlet("/api/news/*")
public class NewsServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final NewsService newsService = NewsService.getInstance();
    private final SavedArticleService savedArticleService = SavedArticleService.getInstance();
    private final SearchNewsService searchNewsService = SearchNewsService.getInstance();
    private final CategoryService categoryService = CategoryService.getInstance();
    private final PersonalizedNewsService personalizedNewsService = PersonalizedNewsService.getInstance();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String path = request.getPathInfo();

        boolean isPersonalized = isPersonalizedRequest(request);
        HttpSession session = request.getSession(false);
        User user = null;
        int userId = -1;

        if (session != null && session.getAttribute("user") != null) {
            user = (User) session.getAttribute("user");
            userId = user.getId();
        }

        if (isPersonalized && user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write(errorJson("Login required for personalized results."));
            return;
        }

        try {
            if (path == null || path.equals("/today")) {
                List<NewsArticle> articles = newsService.getTodayHeadlines();
                if (isPersonalized) {
                    articles = personalizedNewsService.getPersonalizedArticles(userId, articles);
                }
                out.write(gson.toJson(articles));

            } else if (path.startsWith("/date-range")) {
                String start = request.getParameter("start");
                String end = request.getParameter("end");
                String type = request.getParameter("type");

                Date startDate = Date.valueOf(start);
                Date endDate = Date.valueOf(end);

                List<NewsArticle> articles = (type == null || type.equalsIgnoreCase("all")) ?
                        newsService.getHeadlinesByDateRange(startDate, endDate) :
                        newsService.getHeadlinesByDateRangeAndCategory(startDate, endDate, type);

                if (isPersonalized) {
                    articles = personalizedNewsService.getPersonalizedArticles(userId, articles);
                }

                out.write(gson.toJson(articles));

            } else if (path.startsWith("/search")) {
                String query = request.getParameter("query");
                String start = request.getParameter("start");
                String end = request.getParameter("end");
                String sort = request.getParameter("sort");

                if (query == null || query.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.write(errorJson("Query parameter is required"));
                    return;
                }

                if (user == null) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.write(errorJson("Login required"));
                    return;
                }

                List<NewsArticle> results = searchNewsService.searchArticles(userId, query, start, end, sort);
                if (isPersonalized) {
                    results = personalizedNewsService.getPersonalizedArticles(userId, results);
                }

                out.write(gson.toJson(results));

            } else if (path.startsWith("/saved")) {
                if (user == null) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.write(errorJson("Login required"));
                    return;
                }

                List<NewsArticle> articles = savedArticleService.getSavedArticlesByUser(userId);
                out.write(gson.toJson(articles));

            } else if (path.startsWith("/category")) {
                List<Category> results = categoryService.getAllCategory();
                out.write(gson.toJson(results));

            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write(errorJson("Invalid endpoint"));
            }

        } catch (Exception e) {
            e.printStackTrace();
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
                if (session == null || session.getAttribute("user") == null) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.write(errorJson("Login required"));
                    return;
                }

                int userId = ((User) session.getAttribute("user")).getId();
                boolean saved = savedArticleService.saveArticle(userId, articleId);

                JsonObject result = new JsonObject();
                result.addProperty("success", saved);
                result.addProperty("message", saved ? "Article saved." : "Could not save article or Article is already saved.");
                out.write(result.toString());

            } else if (parts.length == 2 && parts[1].equals("category")) {
                if (!isAdmin(request)) {
                    respondForbidden(response);
                    return;
                }

                BufferedReader reader = request.getReader();
                StringBuilder jsonBuffer = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonBuffer.append(line);
                }

                ObjectMapper objectMapper = new ObjectMapper();
                String categoryName = null;
                if (!jsonBuffer.isEmpty()) {
                    Category category = objectMapper.readValue(jsonBuffer.toString(), Category.class);
                    categoryName = category.getName();
                }

                HttpSession session = request.getSession(false);
                User user = (User) session.getAttribute("user");
                if (session == null || user == null) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.write(errorJson("Login required"));
                    return;
                }

                boolean saved = categoryService.addCategory(categoryName);

                JsonObject result = new JsonObject();
                result.addProperty("success", saved);
                result.addProperty("message", saved ? "Category saved." : "Could not save category.");
                out.write(result.toString());

            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write(errorJson("Invalid save request"));
            }

        } catch (Exception e) {
            e.printStackTrace();
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
                if (session == null || session.getAttribute("user") == null) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.write(errorJson("Login required"));
                    return;
                }

                int userId = ((User) session.getAttribute("user")).getId();
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
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(errorJson("Error: " + e.getMessage()));
        }
    }

    private boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute("user");
        if (userObj instanceof User user) {
            return user.getRoleId() == 1;
        }
        return false;
    }

    private void respondForbidden(HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        try (PrintWriter out = response.getWriter()) {
            out.write(errorJson("Access denied: Admins only"));
        }
    }

    private String errorJson(String msg) {
        JsonObject json = new JsonObject();
        json.addProperty("success", false);
        json.addProperty("message", msg);
        return json.toString();
    }

    private boolean isPersonalizedRequest(HttpServletRequest request) {
        String personalized = request.getParameter("personalized");
        return personalized != null && personalized.trim().equalsIgnoreCase("true");
    }
}
