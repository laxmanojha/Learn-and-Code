package com.itt.ecommerce.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itt.ecommerce.dto.CartDto;
import com.itt.ecommerce.dto.CartItemDto;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/order")
public class Order  extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Read JSON from request body
        StringBuilder jsonBuffer = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }
        }

        // Convert JSON to CartDto object
        ObjectMapper objectMapper = new ObjectMapper();
        CartDto cartDto = objectMapper.readValue(jsonBuffer.toString(), CartDto.class);
        List<CartItemDto> cartItems = cartDto.getCartItems();

        // Process each cart item (Save to DB)
        try (Connection con = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO cart_items (cart_id, product_id, quantity, product_name, product_price) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                for (CartItemDto item : cartItems) {
                    ps.setInt(1, item.getCartId());
                    ps.setInt(2, item.getProductId());
                    ps.setInt(3, item.getQuantity());
                    ps.setString(4, item.getProductName());
                    ps.setFloat(5, item.getProductPrice());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error saving cart items.");
            return;
        }

        // Send success response
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("Cart items added successfully.");
    }
}
