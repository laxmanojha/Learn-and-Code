package frontend.newsaggregation.service;

import frontend.newsaggregation.constant.StaticConfiguration;
import frontend.newsaggregation.model.User;
import frontend.newsaggregation.util.HttpUtil;
import java.io.IOException;
import java.net.http.HttpResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AuthService {

	private static AuthService instance; 

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public User login(String username, String password) {
        JsonObject requestJson = new JsonObject();
        requestJson.addProperty("username", username);
        requestJson.addProperty("password", password);

        try {
            HttpResponse<String> response = HttpUtil.loginAndSetSessionCookie(StaticConfiguration.getBaseUrl() + "auth/login", requestJson.toString());
            String body = response.body();

            JsonObject jsonResponse = JsonParser.parseString(body).getAsJsonObject();
            boolean success = jsonResponse.get("success").getAsBoolean();

            if (success) {
                JsonObject userInfo = jsonResponse.getAsJsonObject("user");
                User user = new Gson().fromJson(userInfo, User.class);
                System.out.println("Login Successful: " + jsonResponse.get("message").getAsString());
                System.out.println(user.toString());
                return user;
            } else {
                System.out.println("Login Failed: " + jsonResponse.get("message").getAsString());
            }
        } catch (Exception e) {
            System.err.println("Login failed due to an exception: " + e.getMessage());
        }
        return null;
    }

    public boolean signup(User user) {
        try {
            Gson gson = new Gson();
            String userJson = gson.toJson(user);

            HttpResponse<String> response = HttpUtil.sendPostRequest(StaticConfiguration.getBaseUrl() + "auth/signup", userJson);
            return HttpUtil.processResponse(response, "Sign Up");
        } catch (IOException | InterruptedException e) {
            System.err.println("Sign Up failed due to an exception: " + e.getMessage());
            return false;
        }
    }
    
    public boolean logout() {
	    try {
	        HttpResponse<String> response = HttpUtil.sendPostRequest(StaticConfiguration.getBaseUrl() + "auth/logout", "{}");
	        return HttpUtil.processResponse(response, "Logout");
	    } catch (IOException | InterruptedException e) {
	        System.err.println("Logout failed due to an exception: " + e.getMessage());
	        return false;
	    }
	}
}
