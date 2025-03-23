package com.itt.ecommerce.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.itt.ecommerce.dto.CartDto;
import com.itt.ecommerce.dto.CartItemDto;
import com.itt.ecommerce.service.OrderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/order")
public class Order  extends HttpServlet {

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
        	completeOrder(out, request, response);
        } else {
        	String username = pathInfo.split("/")[1];

        }
    }
    
    private static void completeOrder(PrintWriter out, HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
    	StringBuilder jsonBuffer = new StringBuilder();
    	List<CartItemDto> cartItems = null;
    	String line;
    	
    	try (BufferedReader reader = request.getReader()) {
    		while ((line = reader.readLine()) != null) {
    			jsonBuffer.append(line);
    		}
    	}
    	
    	ObjectMapper objectMapper = new ObjectMapper();
    	if (!jsonBuffer.isEmpty()) {
    		CartDto cartDto = objectMapper.readValue(jsonBuffer.toString(), CartDto.class);
    		cartItems = cartDto.getCartItems();
    	}
    	
    	String result = OrderService.makeAnOrder(cartItems);
    	int success = Integer.parseInt(result.split(":")[0]);
    	String message = result.split(":")[1];
    	
    	JsonObject jsonResponse = new JsonObject();
    	
    	if (success == 1) {
    		jsonResponse.addProperty("success", false);
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
}
