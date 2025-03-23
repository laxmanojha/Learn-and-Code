package com.itt.ecommerce.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itt.ecommerce.dto.UserDto;
import com.itt.ecommerce.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet("/user")
public class UserInfo extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        String username = request.getParameter("username");

        UserDto userInfo = UserService.getUserInfo(username);
        JsonObject jsonResponse = new JsonObject();
        Gson gson = new Gson();

        if (userInfo != null) {
            jsonResponse.addProperty("success", true);
            
            JsonElement userJson = gson.toJsonTree(userInfo);
            jsonResponse.add("userInfo", userJson);

            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Error in fetching user details.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        out.write(gson.toJson(jsonResponse));
        out.flush();
    }

}