package backend.newsaggregation.controller;

import java.io.IOException;
import java.io.PrintWriter;

import backend.newsaggregation.service.CategoryService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/admin/category")
public class CategoryServlet extends HttpServlet {
	private final CategoryService categoryService = CategoryService.getInstance();

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();

        String name = req.getParameter("name");
        if (name == null || name.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("Category name is required.");
            return;
        }

        try {
            categoryService.addCategory(name);
            out.println("Category '" + name + "' added successfully.");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("Error adding category: " + e.getMessage());
        }
    }
}
