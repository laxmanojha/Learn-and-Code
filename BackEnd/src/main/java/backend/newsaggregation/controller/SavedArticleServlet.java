//package backend.newsaggregation.controller;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.List;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonObject;
//
//import backend.newsaggregation.model.NewsArticle;
//import backend.newsaggregation.service.SavedArticleService;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//
//@WebServlet("/api/saved-articles/*")
//public class SavedArticleServlet extends HttpServlet {
//    private final SavedArticleService savedArticleService = SavedArticleService.getInstance();
//    private final Gson gson = new Gson();
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//
//        HttpSession session = request.getSession(false);
//        PrintWriter out = response.getWriter();
//
//        if (session == null || session.getAttribute("user") == null) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            out.write(errorJson("Login required"));
//            return;
//        }
//
//        Object userObj = session.getAttribute("user");
//        int userId = -1;
//        if (userObj instanceof backend.newsaggregation.model.User user) {
//            userId = ((backend.newsaggregation.model.User) userObj).getId();
//        }
//        List<NewsArticle> articles = savedArticleService.getSavedArticlesByUser(userId);
//        out.write(gson.toJson(articles));
//    }
//
//    @Override
//    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//
//        HttpSession session = request.getSession(false);
//        PrintWriter out = response.getWriter();
//
//        if (session == null || session.getAttribute("userId") == null) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            out.write(errorJson("Login required"));
//            return;
//        }
//
//        String pathInfo = request.getPathInfo(); // expected: /{id}
//        if (pathInfo == null || pathInfo.length() <= 1) {
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            out.write(errorJson("Missing article ID"));
//            return;
//        }
//
//        int userId = (int) session.getAttribute("userId");
//        int articleId = Integer.parseInt(pathInfo.substring(1));
//
//        boolean deleted = savedArticleService.deleteSavedArticle(userId, articleId);
//        JsonObject result = new JsonObject();
//        result.addProperty("success", deleted);
//        result.addProperty("message", deleted ? "Article deleted." : "Delete failed.");
//        out.write(result.toString());
//    }
//
//    private String errorJson(String msg) {
//        JsonObject json = new JsonObject();
//        json.addProperty("success", false);
//        json.addProperty("message", msg);
//        return json.toString();
//    }
//}
