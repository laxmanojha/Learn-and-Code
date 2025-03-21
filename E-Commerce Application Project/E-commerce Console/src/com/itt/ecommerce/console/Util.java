package com.itt.ecommerce.console;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Util {
	public static boolean loginUser(String username, String password) throws IOException, InterruptedException {
        String url = "http://localhost:8080/E-Commerce-Application/user/login";

        // Creating form data (application/x-www-form-urlencoded)
        String formData = "username=" + username + "&password=" + password;

        // Building HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(BodyPublishers.ofString(formData, StandardCharsets.UTF_8))
                .build();

        // Sending request
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();
        System.out.println("Response Code: " + statusCode);

        // Reading response body
        String responseBody = response.body();
        System.out.println("Response Body: " + responseBody);

        // Parsing JSON response
        try {
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
            boolean success = jsonResponse.get("success").getAsBoolean();
            String message = jsonResponse.get("message").getAsString();

            if (success) {
            	System.out.println("Login Success: " + message);
            	return true;
            }
            
        } catch (Exception e) {
            System.err.println("Failed to parse JSON response: " + e.getMessage());
        }
        
        System.out.println("Login Failed!");
        return false;
    }
	
	public static void registerUser(String name, String username, String password) throws IOException, InterruptedException {
        String url = "http://localhost:8080/E-Commerce-Application/user/register";

        // Creating form data (application/x-www-form-urlencoded)
        String formData = "fullname=" + name + "&username=" + username + "&password=" + password;

        // Building HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(BodyPublishers.ofString(formData, StandardCharsets.UTF_8))
                .build();

        // Sending request
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();
        System.out.println("Response Code: " + statusCode);

        // Reading response body
        String responseBody = response.body();
        System.out.println("Response Body: " + responseBody);

        // Parsing JSON response
        try {
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
            boolean success = jsonResponse.get("success").getAsBoolean();
            String message = jsonResponse.get("message").getAsString();

            if (success) {
                System.out.println("Registration Successful: " + message);
            } else {
                System.out.println("Registration Failed: " + message);
            }
        } catch (Exception e) {
            System.err.println("Failed to parse JSON response: " + e.getMessage());
        }
    }
	
	public static void viewUserDetails(String username) {
        System.out.println("\nFetching user details for " + username + "...");
        // Implement API call to fetch user details from the backend
    }

    public static void orderProduct(String username) {
        System.out.println("\nPlacing an order for " + username + "...");
        // Implement API call to order a product
    }

    public static void viewCart(String username) {
        System.out.println("\nFetching cart for " + username + "...");
        // Implement API call to view user's cart
    }

    public static void viewOrderHistory(String username) {
        System.out.println("\nFetching order history for " + username + "...");
        // Implement API call to fetch order history
    }
}
