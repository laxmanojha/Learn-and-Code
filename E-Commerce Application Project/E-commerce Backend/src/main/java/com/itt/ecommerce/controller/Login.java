package com.itt.ecommerce.controller;

import java.io.IOException;
import java.io.PrintWriter;

import com.itt.ecommerce.dto.UserDto;
import com.itt.ecommerce.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class Login extends HttpServlet{
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();

        String email = request.getParameter("username");
        String password = request.getParameter("password");
        UserDto user = new UserDto(email, password); 
        
        String result = UserService.authenticateUser(user);
        out.write(result);
	}
}