package com.itt.ecommerce.service;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.itt.ecommerce.dto.CartDto;
import com.itt.ecommerce.dto.CartItemDto;
import com.itt.ecommerce.dto.OrderHistoryDto;
import com.itt.ecommerce.util.HttpUtil;

public class OrderService {
	private static final String BASE_URL = "http://localhost:8080/E-Commerce-Application";
    private static final Gson gson = new Gson();
	
	public static void checkOut(String username) throws IOException, InterruptedException {
	    List<CartItemDto> allCartItems = CartService.getCartItems(username);
	    if (!allCartItems.isEmpty()) {
		    CartDto cartDto = new CartDto();
		    cartDto.setCartItems(allCartItems);
	
		    ObjectMapper objectMapper = new ObjectMapper();
		    String jsonPayload = objectMapper.writeValueAsString(cartDto);
	
		    String url = BASE_URL + "/order";
	
		    HttpUtil.processResponse(HttpUtil.sendPostRequest(url, jsonPayload), "Order checkout");
	    }
	}
	
	public static void viewOrderHistory(String username) throws IOException, InterruptedException {
		System.out.println("\nFetching order history for " + username + "...");
        String url = BASE_URL + "/order/order-history" + "?username=" + username;
        HttpResponse<String> response = HttpUtil.sendGetRequest(url);
        
        if (response.statusCode() == 200) {
        	List<OrderHistoryDto> orderHistory = parseOrderHistoryResponse(response.body());
        	displayOrderHistory(orderHistory);
        } else {
            System.err.println("Failed to fetch order history: " + response.body());
        }
    }
    
    private static List<OrderHistoryDto> parseOrderHistoryResponse(String jsonResponse) {
        try {
            JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);

            if (jsonObject.get("success").getAsBoolean()) {
                JsonArray orderHistoryArray = jsonObject.getAsJsonArray("orderHistory");
                return gson.fromJson(orderHistoryArray, new TypeToken<List<OrderHistoryDto>>() {}.getType());
            } else {
                System.err.println("Error: " + jsonObject.get("message").getAsString());
            }
        } catch (Exception e) {
            System.err.println("Failed to parse order history response: " + e.getMessage());
        }
        return List.of();
    }
    
    private static void displayOrderHistory(List<OrderHistoryDto> orderHistory) {
        if (orderHistory == null || orderHistory.isEmpty()) {
            System.out.println("No order history found.");
            return;
        }

        System.out.printf("%-20s %-20s %-10s %-10s %-15s %-20s%n", 
                          "Product Category", "Product Name", "Price", "Qty", "Total Price", "Order Date");
        System.out.println("----------------------------------------------------------------------------------------------");


        for (OrderHistoryDto order : orderHistory) {
            System.out.printf("%-20s %-20s %-10.2f %-10d %-15.2f %-20s%n",
                              order.getProductCategory(),
                              order.getProductName(),
                              order.getProductPrice(),
                              order.getProductQuantity(),
                              order.getTotalProductsPrice(),
                              order.getOrderDate());
        }
    }

}