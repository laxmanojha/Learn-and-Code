package com.itt.ecommerce.service;

import com.itt.ecommerce.util.HttpUtil;
import java.io.IOException;
import java.net.http.HttpResponse;

public class UserService {
    private static final String BASE_URL = "http://localhost:8080/E-Commerce-Application";

    public static boolean loginUser(String username, String password) throws IOException, InterruptedException {
        String url = BASE_URL + "/user/login";
        String formData = "username=" + username + "&password=" + password;

        HttpResponse<String> response = HttpUtil.sendPostRequest(url, formData);
        return HttpUtil.processResponse(response, "Login");
    }

    public static boolean registerUser(String name, String username, String password) throws IOException, InterruptedException {
        String url = BASE_URL + "/user/register";
        String formData = "fullname=" + name + "&username=" + username + "&password=" + password;

        HttpResponse<String> response = HttpUtil.sendPostRequest(url, formData);
        return HttpUtil.processResponse(response, "Registration");
    }

    public static void viewUserDetails(String username) throws IOException, InterruptedException {
        System.out.println("\nFetching user details for " + username + "...");
        String url = BASE_URL + "/user";
        String formData = "username=" + username;
        
        HttpResponse<String> response = HttpUtil.sendPostRequest(url, formData);
        HttpUtil.processUserDetailsResponse(response);
    }
}
