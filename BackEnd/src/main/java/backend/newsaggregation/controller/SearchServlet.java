package backend.newsaggregation.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import backend.newsaggregation.model.NewsArticle;
import backend.newsaggregation.service.NewsService;
import backend.newsaggregation.service.SearchNewsService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/search")
public class SearchServlet extends HttpServlet {
    private final SearchNewsService newsService = SearchNewsService.getInstance();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String query = request.getParameter("query");
        String start = request.getParameter("start");
        String end = request.getParameter("end");
        String sort = request.getParameter("sort");

        PrintWriter out = response.getWriter();

        if (query == null || query.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(errorJson("Query parameter is required"));
            return;
        }

        try {
            List<NewsArticle> results = newsService.searchArticles(query, start, end, sort);
            out.write(gson.toJson(results));
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
