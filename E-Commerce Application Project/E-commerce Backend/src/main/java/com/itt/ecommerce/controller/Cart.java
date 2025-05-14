package com.itt.ecommerce.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.itt.ecommerce.dto.CartItemDto;
import com.itt.ecommerce.service.CartService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/cart/*")
public class Cart extends HttpServlet {

    private static final Gson gson = new Gson();
    private CartService cartService = new CartService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String username = request.getParameter("username");
        if (username == null || username.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"success\": false, \"message\": \"Username is required\"}");
            out.flush();
            return;
        }

        List<CartItemDto> allCartItems = cartService.fetchAllCartItems(username);
        JsonObject jsonResponse = new JsonObject();

        if (allCartItems != null && !allCartItems.isEmpty()) {
            jsonResponse.addProperty("success", true);
            JsonArray cartItemsJsonArray = gson.toJsonTree(allCartItems).getAsJsonArray();
            jsonResponse.add("cartItems", cartItemsJsonArray);
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Cart is empty for this user.");
            response.setStatus(HttpServletResponse.SC_OK);
        }

        out.write(jsonResponse.toString());
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String username = request.getParameter("username");
        int productId;
        int quantity;

        try {
            productId = Integer.parseInt(request.getParameter("productId"));
            quantity = Integer.parseInt(request.getParameter("quantity"));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"success\": false, \"message\": \"Invalid productId or quantity\"}");
            out.flush();
            return;
        }

        String result = cartService.addToCart(username, productId, quantity);
        int success = Integer.parseInt(result.split(":")[0]);
        String message = result.split(":")[1];

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("success", success == 1);
        jsonResponse.addProperty("message", message);
        response.setStatus(success == 1 ? HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST);

        out.write(jsonResponse.toString());
        out.flush();
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        BufferedReader reader = request.getReader();
        StringBuilder requestBody = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }

        JsonObject json = JsonParser.parseString(requestBody.toString()).getAsJsonObject();
        String username = json.get("username").getAsString();
        int productId = json.get("productId").getAsInt();
        int quantity = json.get("quantity").getAsInt();

        String result = cartService.updateCart(username, productId, quantity);
        int success = Integer.parseInt(result.split(":")[0]);
        String message = result.split(":")[1];

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("success", success == 1);
        jsonResponse.addProperty("message", message);
        response.setStatus(success == 1 ? HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST);

        out.write(jsonResponse.toString());
        out.flush();
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();

        String username = request.getParameter("username");

        if (username == null || username.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"success\": false, \"message\": \"Username is required\"}");
            out.flush();
            return;
        }

        JsonObject jsonResponse = new JsonObject();

        if (pathInfo == null || pathInfo.equals("/")) {
            String result = cartService.removeAllProduct(username);
            int success = Integer.parseInt(result.split(":")[0]);
            String message = result.split(":")[1];

            jsonResponse.addProperty("success", success == 1);
            jsonResponse.addProperty("message", message);
            response.setStatus(success == 1 ? HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST);
        } else {
            try {
                int productId = Integer.parseInt(pathInfo.substring(1));
                String result = cartService.removeProduct(username, productId);
                int success = Integer.parseInt(result.split(":")[0]);
                String message = result.split(":")[1];

                jsonResponse.addProperty("success", success == 1);
                jsonResponse.addProperty("message", message);
                response.setStatus(success == 1 ? HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST);
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Invalid productId format: " + pathInfo);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Invalid productId format");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }

        out.write(jsonResponse.toString());
        out.flush();
    }

}
