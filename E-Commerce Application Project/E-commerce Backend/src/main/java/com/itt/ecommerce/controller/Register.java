package com.itt.ecommerce.controller;

import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonObject;
import com.itt.ecommerce.dto.UserDto;
import com.itt.ecommerce.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class Register extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String name = request.getParameter("fullname");
        String email = request.getParameter("username");
        String password = request.getParameter("password");

        UserDto user = new UserDto(name, email, password); 
        boolean isRegistered = UserService.registerUser(user);

        JsonObject jsonResponse = new JsonObject();
        if (isRegistered) {
            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("message", "User registered successfully.");
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Registration failed. User may already exist.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        out.write(jsonResponse.toString());
        out.flush();
    }
}
