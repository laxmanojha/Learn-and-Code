package backend.newsaggregation.controller;

import java.io.IOException;
import java.io.PrintWriter;
import com.google.gson.JsonObject;

import backend.newsaggregation.service.CategoryService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/admin/category/*")
public class AdminCategoryVisibilityServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static CategoryService categoryService = CategoryService.getInstance();

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo(); // Expected format: /{categoryId}/hide or /{categoryId}/unhide
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        if (pathInfo == null || pathInfo.split("/").length < 3) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(errorJson("Invalid path"));
            return;
        }

        String[] parts = pathInfo.split("/");
        int categoryId;
        try {
            categoryId = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(errorJson("Invalid category ID"));
            return;
        }

        String action = parts[2]; // "hide" or "unhide"
        boolean success = false;

        try {

            if ("hide".equalsIgnoreCase(action)) {
                success = categoryService.hideCategory(categoryId);
            } else if ("unhide".equalsIgnoreCase(action)) {
                success = categoryService.unhideCategory(categoryId);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(errorJson("Unknown action"));
                return;
            }

            if (success) {
                out.write(successJson("Category visibility updated."));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.write(errorJson("Failed to update category visibility."));
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
