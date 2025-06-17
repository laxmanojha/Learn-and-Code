package backend.newsaggregation.controller;

import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import backend.newsaggregation.service.SavedArticleService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/api/articles/*")
public class ArticleSaveServlet extends HttpServlet {
    private final SavedArticleService savedArticleService = SavedArticleService.getInstance();
    private final Gson gson = new Gson();

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo(); // expected: /{id}/save
        PrintWriter out = response.getWriter();

        try {
            String[] parts = pathInfo.split("/");
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

    private String errorJson(String msg) {
        JsonObject json = new JsonObject();
        json.addProperty("success", false);
        json.addProperty("message", msg);
        return json.toString();
    }
}
