package backend.newsaggregation.controller;

import java.io.IOException;
import java.io.PrintWriter;
import com.google.gson.JsonObject;
import backend.newsaggregation.service.CategoryService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/admin/category/*")
public class AdminCategoryVisibilityServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final CategoryService categoryService = CategoryService.getInstance();

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String[] pathParts = getPathParts(request.getPathInfo());
        try (PrintWriter out = response.getWriter()) {
            if (!isValidPath(pathParts)) {
                respondWithError(response, out, HttpServletResponse.SC_BAD_REQUEST, "Invalid path.");
                return;
            }

            int categoryId = parseCategoryId(pathParts[1], response, out);
            if (categoryId == -1) return;

            String action = pathParts[2];
            handleCategoryVisibilityUpdate(categoryId, action, response, out);
        }
    }

    private String[] getPathParts(String pathInfo) {
        return pathInfo == null ? new String[0] : pathInfo.split("/");
    }

    private boolean isValidPath(String[] pathParts) {
        return pathParts.length == 3 && !pathParts[1].isEmpty();
    }

    private int parseCategoryId(String part, HttpServletResponse response, PrintWriter out) throws IOException {
        try {
            return Integer.parseInt(part);
        } catch (NumberFormatException e) {
            respondWithError(response, out, HttpServletResponse.SC_BAD_REQUEST, "Invalid category ID.");
            return -1;
        }
    }

    private void handleCategoryVisibilityUpdate(int categoryId, String action, HttpServletResponse response, PrintWriter out) throws IOException {
        boolean success;
        try {
            if ("hide".equalsIgnoreCase(action)) {
                success = categoryService.hideCategory(categoryId);
            } else if ("unhide".equalsIgnoreCase(action)) {
                success = categoryService.unhideCategory(categoryId);
            } else {
                respondWithError(response, out, HttpServletResponse.SC_BAD_REQUEST, "Unknown action.");
                return;
            }

            if (success) {
                respondWithSuccess(out, "Category visibility updated.");
            } else {
                respondWithError(response, out, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update category visibility.");
            }
        } catch (Exception e) {
            respondWithError(response, out, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error.");
        }
    }

    private void respondWithSuccess(PrintWriter out, String message) {
        JsonObject json = new JsonObject();
        json.addProperty("success", true);
        json.addProperty("message", message);
        out.write(json.toString());
    }

    private void respondWithError(HttpServletResponse response, PrintWriter out, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        JsonObject json = new JsonObject();
        json.addProperty("success", false);
        json.addProperty("message", message);
        out.write(json.toString());
    }
}
