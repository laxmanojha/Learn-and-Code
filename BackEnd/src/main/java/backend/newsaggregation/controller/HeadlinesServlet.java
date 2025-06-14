package backend.newsaggregation.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import backend.newsaggregation.model.NewsArticle;
import backend.newsaggregation.service.NewsService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/headlines/*")
public class HeadlinesServlet extends HttpServlet {
    private final NewsService newsService = NewsService.getInstance();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String path = request.getPathInfo();
        PrintWriter out = response.getWriter();

        try {
            if ("/today".equals(path)) {
                List<NewsArticle> articles = newsService.getTodayHeadlines();
                out.write(gson.toJson(articles));
            } else if ("/date-range".equals(path)) {
                String start = request.getParameter("start");
                String end = request.getParameter("end");
                String type = request.getParameter("type");

                Date startDate = Date.valueOf(start);
                Date endDate = Date.valueOf(end);

                List<NewsArticle> articles =
                    (type == null || type.equals("all")) ?
                    newsService.getHeadlinesByDateRange(startDate, endDate) :
                    newsService.getHeadlinesByDateRangeAndCategory(startDate, endDate, type);

                out.write(gson.toJson(articles));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write(errorJson("Invalid endpoint"));
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
