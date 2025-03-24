package com.itt.ecommerce.service;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itt.ecommerce.dto.CartDto;
import com.itt.ecommerce.dto.CartItemDto;
import com.itt.ecommerce.util.HttpUtil;

public class OrderService {
	private static final String BASE_URL = "http://localhost:8080/E-Commerce-Application";
	
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
	
	public static void viewOrderHistory(String username) {
		System.out.println("\nFetching order history for " + username + "...");
	}
}