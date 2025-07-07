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

    private final NewsService newsService;
    private final SavedArticleService savedArticleService;
    private final SearchNewsService searchNewsService;
    private final CategoryService categoryService;
    private final PersonalizedNewsService personalizedNewsService;
    private final Gson gson = new Gson();
    
    public NewsServlet() {
        this(NewsService.getInstance(), SavedArticleService.getInstance(), SearchNewsService.getInstance(), CategoryService.getInstance(), PersonalizedNewsService.getInstance());
    }

    public NewsServlet(NewsService newsService, SavedArticleService savedArticleService, SearchNewsService searchNewsService, CategoryService categoryService, PersonalizedNewsService personalizedNewsService) {
        this.newsService = newsService;
        this.savedArticleService = savedArticleService;
        this.searchNewsService = searchNewsService;
        this.categoryService = categoryService;
        this.personalizedNewsService = personalizedNewsService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String path = request.getPathInfo();
        boolean isPersonalized = isPersonalizedRequest(request);
        User user = getSessionUser(request);
        int userId = user != null ? user.getId() : -1;

        if (isPersonalized && user == null) {
            respondError(response, HttpServletResponse.SC_UNAUTHORIZED, "Login required for personalized results.");
            return;
        }

        try (PrintWriter out = response.getWriter()) {
            if (path == null || path.equals("/today")) {
                handleTodayNews(out, isPersonalized, userId);
            } else if (path.startsWith("/date-range")) {
                handleDateRangeNews(request, out, isPersonalized, userId);
            } else if (path.startsWith("/search")) {
                handleSearchRequest(request, response, out, userId, isPersonalized);
            } else if (path.startsWith("/saved")) {
                handleSavedArticles(out, response, userId, user);
            } else if (path.startsWith("/category")) {
                List<Category> results = categoryService.getAllCategory();
                out.write(gson.toJson(results));
            } else {
                respondError(response, HttpServletResponse.SC_NOT_FOUND, "Invalid endpoint");
            }
        } catch (Exception e) {
            respondError(response, HttpServletResponse.SC_BAD_REQUEST, "Error: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String path = request.getPathInfo();
        try (PrintWriter out = response.getWriter()) {
            if (path.matches("/\\d+/save")) {
                handleSaveArticle(request, response, out, path);
            } else if (path.equals("/category")) {
                handleAddCategory(request, response, out);
            } else {
                respondError(response, HttpServletResponse.SC_NOT_FOUND, "Invalid save request");
            }
        } catch (Exception e) {
            respondError(response, HttpServletResponse.SC_BAD_REQUEST, "Error: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            String path = request.getPathInfo();
            if (path != null && path.startsWith("/saved/")) {
                handleDeleteSavedArticle(request, response, out, path);
            } else {
                respondError(response, HttpServletResponse.SC_NOT_FOUND, "Invalid delete request");
            }
        }
    }

    private void handleSaveArticle(HttpServletRequest request, HttpServletResponse response, PrintWriter out, String path) throws IOException {
        int articleId = Integer.parseInt(path.split("/")[1]);
        User user = getSessionUser(request);

        if (user == null) {
            respondError(response, HttpServletResponse.SC_UNAUTHORIZED, "Login required");
            return;
        }

        boolean saved = savedArticleService.saveArticle(user.getId(), articleId);
        respondWithMessage(out, saved, saved ? "Article saved." : "Could not save article or already saved.");
    }

    private void handleAddCategory(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws Exception {
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
        Category category = objectMapper.readValue(jsonBuffer.toString(), Category.class);

        boolean saved = categoryService.addCategory(category.getName());
        respondWithMessage(out, saved, saved ? "Category saved." : "Could not save category.");
    }

    private void handleDeleteSavedArticle(HttpServletRequest request, HttpServletResponse response, PrintWriter out, String path) throws IOException {
        User user = getSessionUser(request);
        if (user == null) {
            respondError(response, HttpServletResponse.SC_UNAUTHORIZED, "Login required");
            return;
        }

        int articleId = Integer.parseInt(path.substring("/saved/".length()));
        boolean deleted = savedArticleService.deleteSavedArticle(user.getId(), articleId);
        respondWithMessage(out, deleted, deleted ? "Article deleted." : "Delete failed.");
    }

    private void respondWithMessage(PrintWriter out, boolean success, String message) {
        JsonObject json = new JsonObject();
        json.addProperty("success", success);
        json.addProperty("message", message);
        out.write(json.toString());
    }

    private void respondError(HttpServletResponse response, int statusCode, String msg) throws IOException {
        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            JsonObject json = new JsonObject();
            json.addProperty("success", false);
            json.addProperty("message", msg);
            out.write(json.toString());
        }
    }

    private boolean isAdmin(HttpServletRequest request) {
        User user = getSessionUser(request);
        return user != null && user.getRoleId() == 1;
    }

    private void respondForbidden(HttpServletResponse response) throws IOException {
        respondError(response, HttpServletResponse.SC_FORBIDDEN, "Access denied: Admins only");
    }

    private void handleTodayNews(PrintWriter out, boolean personalized, int userId) {
        List<NewsArticle> articles = newsService.getTodayHeadlines();
        if (personalized) {
            articles = personalizedNewsService.getPersonalizedArticles(userId, articles);
        }
        out.write(gson.toJson(articles));
    }

    private void handleDateRangeNews(HttpServletRequest request, PrintWriter out, boolean personalized, int userId) {
        String start = request.getParameter("start");
        String end = request.getParameter("end");
        String type = request.getParameter("type");

        Date startDate = Date.valueOf(start);
        Date endDate = Date.valueOf(end);

        List<NewsArticle> articles = (type == null || type.equalsIgnoreCase("all")) ?
                newsService.getHeadlinesByDateRange(startDate, endDate) :
                newsService.getHeadlinesByDateRangeAndCategory(startDate, endDate, type);

        if (personalized) {
            articles = personalizedNewsService.getPersonalizedArticles(userId, articles);
        }

        out.write(gson.toJson(articles));
    }

    private void handleSearchRequest(HttpServletRequest request, HttpServletResponse response, PrintWriter out, int userId, boolean personalized) throws IOException {
        String query = request.getParameter("query");
        String start = request.getParameter("start");
        String end = request.getParameter("end");
        String sort = request.getParameter("sort");

        if (query == null || query.trim().isEmpty()) {
            respondError(response, HttpServletResponse.SC_BAD_REQUEST, "Query parameter is required");
            return;
        }

        List<NewsArticle> results = searchNewsService.searchArticles(userId, query, start, end, sort);
        if (personalized) {
            results = personalizedNewsService.getPersonalizedArticles(userId, results);
        }

        out.write(gson.toJson(results));
    }

    private void handleSavedArticles(PrintWriter out, HttpServletResponse response, int userId, User user) throws IOException {
        if (user == null) {
            respondError(response, HttpServletResponse.SC_UNAUTHORIZED, "Login required");
            return;
        }
        List<NewsArticle> articles = savedArticleService.getSavedArticlesByUser(userId);
        out.write(gson.toJson(articles));
    }

    private boolean isPersonalizedRequest(HttpServletRequest request) {
        String personalized = request.getParameter("personalized");
        return personalized != null && personalized.trim().equalsIgnoreCase("true");
    }

    private User getSessionUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object userObj = session.getAttribute("user");
            if (userObj instanceof User user) {
                return user;
            }
        }
        return null;
    }
}
