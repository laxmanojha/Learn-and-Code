package backend.newsaggregation.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import backend.newsaggregation.model.HiddenKeyword;
import backend.newsaggregation.service.AdminKeywordService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/admin/keyword/*")
public class AdminKeywordBlockServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static AdminKeywordService adminKeywordService = AdminKeywordService.getInstance();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        try (BufferedReader reader = request.getReader();
             PrintWriter out = response.getWriter()) {

            JsonObject body = JsonParser.parseReader(reader).getAsJsonObject();
            String keyword = body.has("keyword") ? body.get("keyword").getAsString().trim() : null;

            if (keyword == null || keyword.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(errorJson("Keyword is required."));
                return;
            }

            if (adminKeywordService.addKeyword(keyword)) {
                out.write(successJson("Keyword added."));
            } else {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                out.write(errorJson("Failed to add keyword. Might already exist."));
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo(); // expected /{id}
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        if (pathInfo == null || pathInfo.split("/").length < 2) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(errorJson("Keyword ID missing"));
            return;
        }

        int keywordId;
        try {
            keywordId = Integer.parseInt(pathInfo.split("/")[1]);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(errorJson("Invalid ID"));
            return;
        }

        if (adminKeywordService.deleteKeyword(keywordId)) {
            out.write(successJson("Keyword deleted."));
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write(errorJson("Keyword deletion failed."));
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        try (PrintWriter out = response.getWriter()) {
            List<HiddenKeyword> keywords = adminKeywordService.getAllKeywords();

            out.write(gson.toJson(keywords));
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
