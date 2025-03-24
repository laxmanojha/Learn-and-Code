package com.itt.ecommerce.service;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.itt.ecommerce.dto.CategoryDto;
import com.itt.ecommerce.util.HttpUtil;

public class CartService {
	private static final String BASE_URL = "http://localhost:8080/E-Commerce-Application";
	
	public static void viewCart(String username) {
	  System.out.println("\nFetching cart for " + username + "...");
	  String url = BASE_URL + "/cart/" + username;
	
	  HttpResponse<String> response = HttpUtil.sendGetRequest(url);
	  
	}
	
	private static List<CategoryDto> parseCartResponse(String jsonResponse) {
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
