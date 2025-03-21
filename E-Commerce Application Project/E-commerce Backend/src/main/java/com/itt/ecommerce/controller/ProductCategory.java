package com.itt.ecommerce.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.itt.ecommerce.dto.CategoryDto;
import com.itt.ecommerce.service.CategoryService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ProductCategory extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        List<CategoryDto> allCategories = CategoryService.getAllCategories();
        JsonObject jsonResponse = new JsonObject();

        if (allCategories != null && !allCategories.isEmpty()) {
            jsonResponse.addProperty("success", true);

            Gson gson = new Gson();
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
}