package com.itt.ecommerce.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.itt.ecommerce.dto.CategoryDto;
import com.itt.ecommerce.dto.ProductDto;
import com.itt.ecommerce.service.ProductCategoryService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/category/*")
public class ProductCategory extends HttpServlet {

    private static final Gson gson = new Gson();
    private ProductCategoryService productCategoryService = new ProductCategoryService();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            fetchAllCategories(response, out);
        } else {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 3 && pathParts[2].equals("products")) {
                try {
                    int categoryId = Integer.parseInt(pathParts[1]);
                    fetchProductsByCategory(categoryId, response, out);
                } catch (NumberFormatException e) {
                    sendErrorResponse(response, out, "Invalid category ID format.");
                }
            } else {
                sendErrorResponse(response, out, "Invalid URL format.");
            }
        }
    }

    private void fetchAllCategories(HttpServletResponse response, PrintWriter out) throws IOException {
        List<CategoryDto> allCategories = productCategoryService.getAllCategories();
        JsonObject jsonResponse = new JsonObject();

        if (allCategories != null && !allCategories.isEmpty()) {
            jsonResponse.addProperty("success", true);
            JsonArray categoryJsonArray = gson.toJsonTree(allCategories).getAsJsonArray();
            jsonResponse.add("categories", categoryJsonArray);
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Error in fetching all categories.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        out.write(jsonResponse.toString());
        out.flush();
    }

    private void fetchProductsByCategory(int categoryId, HttpServletResponse response, PrintWriter out) throws IOException {
        List<ProductDto> products = productCategoryService.getProductsByCategory(categoryId);
        JsonObject jsonResponse = new JsonObject();

        if (products != null && !products.isEmpty()) {
            jsonResponse.addProperty("success", true);
            JsonArray productJsonArray = gson.toJsonTree(products).getAsJsonArray();
            jsonResponse.add("products", productJsonArray);
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "No products found for this category.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        out.write(jsonResponse.toString());
        out.flush();
    }

    private void sendErrorResponse(HttpServletResponse response, PrintWriter out, String message) throws IOException {
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("success", false);
        jsonResponse.addProperty("message", message);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        out.write(jsonResponse.toString());
        out.flush();
    }
}
