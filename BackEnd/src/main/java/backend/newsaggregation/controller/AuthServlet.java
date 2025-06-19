package backend.newsaggregation.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import backend.newsaggregation.model.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import backend.newsaggregation.service.UserService;

@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {

    private final UserService userService = UserService.getInstance();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            sendJsonResponse(response, false, "Invalid endpoint", HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        switch (pathInfo) {
            case "/login":
                handleLogin(request, response);
                break;
            case "/signup":
                handleSignup(request, response);
                break;
            case "/logout":
                request.getSession().invalidate();
                sendJsonResponse(response, true, "Logged out successfully.", HttpServletResponse.SC_OK);
                break;
            default:
                sendJsonResponse(response, false, "Endpoint not found", HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder jsonBuffer = new StringBuilder();
    	String username = null;
    	String password = null;
    	String line;
    	
    	try (BufferedReader reader = request.getReader()) {
    		while ((line = reader.readLine()) != null) {
    			jsonBuffer.append(line);
    		}
    	}
    	
    	ObjectMapper objectMapper = new ObjectMapper();
    	if (!jsonBuffer.isEmpty()) {
    		User user = objectMapper.readValue(jsonBuffer.toString(), User.class);
    		username = user.getUsername();
    		password = user.getPassword();
    	}

        if (username == null || password == null) {
            sendJsonResponse(response, false, "Missing credentials", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        User loginUser = new User(username, password);
        System.out.println("Reached in Auth Servlet.");
        System.out.println("username " + username + "password " + password);
        String result = userService.authenticateUser(loginUser);
        String[] resultParts = result.split(":", 2);
        int loginSuccess = Integer.parseInt(resultParts[0]);
        String message = resultParts[1];

        if (loginSuccess == 1) {
            User fullUser = userService.getUserByUsername(username);

            request.getSession().setAttribute("user", fullUser);
            
            System.out.println("session object: " + request.getSession().getAttribute("user"));

            sendJsonResponse(response, true, message, HttpServletResponse.SC_OK, fullUser);
        } else {
            sendJsonResponse(response, false, message, HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void handleSignup(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	StringBuilder jsonBuffer = new StringBuilder();
    	String username = null;
    	String password = null;
    	String email = null;
    	String line;
    	
    	try (BufferedReader reader = request.getReader()) {
    		while ((line = reader.readLine()) != null) {
    			jsonBuffer.append(line);
    		}
    	}
    	
    	ObjectMapper objectMapper = new ObjectMapper();
    	if (!jsonBuffer.isEmpty()) {
    		User user = objectMapper.readValue(jsonBuffer.toString(), User.class);
    		username = user.getUsername();
    		password = user.getPassword();
    		email = user.getEmail();
    	}
    	
    	System.out.println(username +" " + password + " " + email);

        if (username == null || password == null || email == null) {
            sendJsonResponse(response, false, "Missing signup information", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        User user = new User(username, password, email, 2); //Default roleId = 2 i.e. registering as user
        String result = userService.registerUser(user);
        String[] resultParts = result.split(":", 2);
        int isRegistered = Integer.parseInt(resultParts[0]);
        String message = resultParts[1];

        if (isRegistered == 1) {
            sendJsonResponse(response, true, message, HttpServletResponse.SC_OK);
        } else {
            sendJsonResponse(response, false, message, HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void sendJsonResponse(HttpServletResponse response, boolean success, String message, int statusCode) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("success", success);
        json.addProperty("message", message);

        response.setStatus(statusCode);

        try (PrintWriter out = response.getWriter()) {
            out.write(json.toString());
            out.flush();
        }
    }
    
    private void sendJsonResponse(HttpServletResponse response, boolean success, String message, int statusCode, User user) throws IOException {
    	JsonObject json = new JsonObject();
    	Gson gson = new Gson();
    	json.addProperty("success", success);
    	json.addProperty("message", message);
    	JsonElement userJson = gson.toJsonTree(user);
        json.add("user", userJson);
    	
    	response.setStatus(statusCode);
    	
    	try (PrintWriter out = response.getWriter()) {
    		out.write(json.toString());
    		out.flush();
    	}
    }
}
