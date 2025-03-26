package com.itt.ecommerce.controller;

import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonObject;
import com.itt.ecommerce.dto.UserDto;
import com.itt.ecommerce.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/user/login")
public class Login extends HttpServlet{
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        UserDto user = new UserDto(username, password); 
        
        String result = UserService.authenticateUser(user);
        int loginSuccess = Integer.parseInt(result.split(":")[0]);
        String message = result.split(":")[1];
        
        JsonObject jsonResponse = new JsonObject();
        if (loginSuccess == 1) {
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
}