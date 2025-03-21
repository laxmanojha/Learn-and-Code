package com.itt.ecommerce.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.itt.ecommerce.dto.CategoryDto;
import com.itt.ecommerce.util.HttpUtil;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;

public class CategoryService {
    private static final String BASE_URL = "http://localhost:8080/E-Commerce-Application";
    private static final Gson gson = new Gson();

    public static List<CategoryDto> getAllCategories() throws IOException, InterruptedException {
        String url = BASE_URL + "/category";
        HttpResponse<String> response = HttpUtil.sendGetRequest(url);

        if (response.statusCode() == 200) {
            return parseCategoryResponse(response.body());
        } else {
            System.err.println("Failed to fetch categories: " + response.body());
            return List.of();
        }
    }

    private static List<CategoryDto> parseCategoryResponse(String jsonResponse) {
        try {
            JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);

            if (jsonObject.get("success").getAsBoolean()) {
                JsonArray categoriesArray = jsonObject.getAsJsonArray("categories");
                return gson.fromJson(categoriesArray, new TypeToken<List<CategoryDto>>() {}.getType());
            } else {
                System.err.println("Error: " + jsonObject.get("message").getAsString());
            }
        } catch (Exception e) {
            System.err.println("Failed to parse category response: " + e.getMessage());
        }
        return List.of();
    }
}
