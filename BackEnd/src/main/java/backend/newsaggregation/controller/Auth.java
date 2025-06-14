package backend.newsaggregation.controller;

import java.io.IOException;
import java.io.PrintWriter;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import backend.newsaggregation.model.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import backend.newsaggregation.service.UserService;

@WebServlet("/api/auth/*")
public class Auth extends HttpServlet {

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
            default:
                sendJsonResponse(response, false, "Endpoint not found", HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null) {
            sendJsonResponse(response, false, "Missing credentials", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        User user = new User(username, password);
        String result = userService.authenticateUser(user);
        String[] resultParts = result.split(":", 2);
        int loginSuccess = Integer.parseInt(resultParts[0]);
        String message = resultParts[1];

        boolean success = loginSuccess == 1;
        int statusCode = success ? HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST;
        sendJsonResponse(response, success, message, statusCode);
    }

    private void handleSignup(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");

        if (username == null || password == null || email == null) {
            sendJsonResponse(response, false, "Missing signup information", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        User user = new User(username, email, password, 2); // Default roleId = 2 i.e. registering as user
        boolean isRegistered = userService.registerUser(user);

        if (isRegistered) {
            sendJsonResponse(response, true, "User registered successfully.", HttpServletResponse.SC_OK);
        } else {
            sendJsonResponse(response, false, "Registration failed. User may already exist.", HttpServletResponse.SC_BAD_REQUEST);
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
}
