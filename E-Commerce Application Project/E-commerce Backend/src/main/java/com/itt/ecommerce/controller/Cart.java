package com.itt.ecommerce.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itt.ecommerce.dto.CartItemDto;
import com.itt.ecommerce.dto.ProductDto;
import com.itt.ecommerce.dto.UserDto;
import com.itt.ecommerce.service.CartService;
import com.itt.ecommerce.service.ProductCategoryService;
import com.itt.ecommerce.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/cart/*")
public class Cart extends HttpServlet {
	
	private static final Gson gson = new Gson();
	
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
        	addProductToCart(request, response, out);
        } else {
        	String username = pathInfo.split("/")[1];

            fetchAllCartItems(username, response, out);
        }
    }
    
    private static void addProductToCart(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
    	String username = request.getParameter("username");
    	int productId = Integer.parseInt(request.getParameter("productId"));
    	int quantity = Integer.parseInt(request.getParameter("quantity"));
    	
    	String result = CartService.addToCart(username, productId, quantity);
    	int success = Integer.parseInt(result.split(":")[0]);
    	String message = result.split(":")[1];
    	
    	JsonObject jsonResponse = new JsonObject();

        if (success == 1) {
        	jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("message", message);
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", message);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        out.write(jsonResponse.toString());
        out.flush();
    }
    
    private static void fetchAllCartItems(String username, HttpServletResponse response, PrintWriter out) {
    	List<CartItemDto> allCartItems = CartService.fetchAllCartItems(username);
        JsonObject jsonResponse = new JsonObject();

        if (allCartItems != null && !allCartItems.isEmpty()) {
            jsonResponse.addProperty("success", true);
            JsonArray cartItemsJsonArray = gson.toJsonTree(allCartItems).getAsJsonArray();
            jsonResponse.add("cartItems", cartItemsJsonArray);
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Cart is empty for this user.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        out.write(jsonResponse.toString());
        out.flush();
    }
}