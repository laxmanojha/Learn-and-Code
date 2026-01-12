package backend.newsaggregation.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import backend.newsaggregation.model.User;
import backend.newsaggregation.service.UserService;

@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(AuthServlet.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Gson gson = new Gson();
    private final UserService userService;
    private final Map<String, BiConsumer<HttpServletRequest, HttpServletResponse>> routeHandlers = new HashMap<>();

    public AuthServlet() {
        this(UserService.getInstance());
    }

    public AuthServlet(UserService userService) {
        this.userService = userService;
    }
    
    @Override
    public void init() {
        routeHandlers.put("/login", this::handleLogin);
        routeHandlers.put("/signup", this::handleSignup);
        routeHandlers.put("/logout", this::handleLogout);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || !routeHandlers.containsKey(pathInfo)) {
            sendJsonResponse(response, false, "Invalid or unknown endpoint", HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        routeHandlers.get(pathInfo).accept(request, response);
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) {
        try {
            User userRequest = parseRequestBody(request, User.class);

            if (userRequest.getUsername() == null || userRequest.getPassword() == null) {
                sendJsonResponse(response, false, "Missing credentials", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            String result = userService.authenticateUser(userRequest);
            String[] resultParts = result.split(":", 2);
            int successFlag = Integer.parseInt(resultParts[0]);
            String message = resultParts[1];

            if (successFlag == 1) {
                User fullUser = userService.getUserByUsername(userRequest.getUsername());
                request.getSession().setAttribute("user", fullUser);
                logger.info("Login successful for user: {}", fullUser.getUsername());
                sendJsonResponse(response, true, message, HttpServletResponse.SC_OK, fullUser);
            } else {
                logger.info("Login failed for user: {}", userRequest.getUsername());
                sendJsonResponse(response, false, message, HttpServletResponse.SC_BAD_REQUEST);
            }

        } catch (Exception e) {
            logger.error("Login failed: {}", e.getMessage());
            sendJsonResponse(response, false, "Invalid request", HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void handleSignup(HttpServletRequest request, HttpServletResponse response) {
        try {
            User userRequest = parseRequestBody(request, User.class);

            if (userRequest.getUsername() == null || userRequest.getPassword() == null || userRequest.getEmail() == null) {
                sendJsonResponse(response, false, "Missing signup information", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            userRequest.setRoleId(2); // Default user role

            String result = userService.registerUser(userRequest);
            String[] resultParts = result.split(":", 2);
            int successFlag = Integer.parseInt(resultParts[0]);
            String message = resultParts[1];

            if (successFlag == 1) {
                logger.info("User signup successful.");
                sendJsonResponse(response, true, message, HttpServletResponse.SC_OK);
            } else {
                logger.info("User signup failed.");
                sendJsonResponse(response, false, message, HttpServletResponse.SC_BAD_REQUEST);
            }

        } catch (Exception e) {
            logger.error("Signup failed: {}", e.getMessage());
            sendJsonResponse(response, false, "Invalid request", HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();
		logger.info("User logged out successfully.");
		sendJsonResponse(response, true, "Logged out successfully.", HttpServletResponse.SC_OK);
    }

    private <T> T parseRequestBody(HttpServletRequest request, Class<T> clazz) throws IOException {
        StringBuilder jsonBuffer = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }
        }
        return objectMapper.readValue(jsonBuffer.toString(), clazz);
    }

    private void sendJsonResponse(HttpServletResponse response, boolean success, String message, int statusCode) {
    	try {
    		JsonObject json = new JsonObject();
    		json.addProperty("success", success);
    		json.addProperty("message", message);
    		sendResponse(response, json, statusCode);
        } catch (IOException ioException) {
            logger.error("Error writing response: {}", ioException.getMessage());
        }
    }

    private void sendJsonResponse(HttpServletResponse response, boolean success, String message, int statusCode, User user) {
    	try {
	        JsonObject json = new JsonObject();
	        json.addProperty("success", success);
	        json.addProperty("message", message);
	        JsonElement userJson = gson.toJsonTree(user);
	        json.add("user", userJson);
	        sendResponse(response, json, statusCode);
    	} catch (IOException ioException) {
    		logger.error("Error writing response: {}", ioException.getMessage());
    	}
    }

    private void sendResponse(HttpServletResponse response, JsonObject json, int statusCode) throws IOException {
        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.write(json.toString());
            out.flush();
        }
    }
}
