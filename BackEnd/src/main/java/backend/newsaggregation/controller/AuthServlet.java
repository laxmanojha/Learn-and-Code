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
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null) {
            sendJsonResponse(response, false, "Missing credentials", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        User loginUser = new User(username, password);
        String result = userService.authenticateUser(loginUser);
        String[] resultParts = result.split(":", 2);
        int loginSuccess = Integer.parseInt(resultParts[0]);
        String message = resultParts[1];

        if (loginSuccess == 1) {
            User fullUser = userService.getUserByUsername(username);

            request.getSession().setAttribute("user", fullUser);

            sendJsonResponse(response, true, message, HttpServletResponse.SC_OK);
        } else {
            sendJsonResponse(response, false, message, HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void handleSignup(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");

        if (username == null || password == null || email == null) {
            sendJsonResponse(response, false, "Missing signup information", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        User user = new User(username, email, password, 2); //Default roleId = 2 i.e. registering as user
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
}
