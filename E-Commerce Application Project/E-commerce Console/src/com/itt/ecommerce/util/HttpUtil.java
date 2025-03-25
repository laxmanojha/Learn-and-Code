package com.itt.ecommerce.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.itt.ecommerce.dto.UserDto;

public class HttpUtil {
    private static final HttpClient client = HttpClient.newHttpClient();

    public static HttpResponse<String> sendPostRequest(String url, String formData) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(BodyPublishers.ofString(formData, StandardCharsets.UTF_8))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> sendGetRequest(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> sendDeleteRequest(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .header("Content-Type", "application/json")
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static boolean processResponse(HttpResponse<String> response, String action) {
        int statusCode = response.statusCode();
        String responseBody = response.body();

        System.out.println("Response Code: " + statusCode);
        System.out.println("Response Body: " + responseBody);

        try {
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
            boolean success = jsonResponse.get("success").getAsBoolean();
            String message = jsonResponse.get("message").getAsString();

            if (success) {
                System.out.println(action + " Successful: " + message);
                return true;
            } else {
                System.out.println(action + " Failed: " + message);
            }
        } catch (Exception e) {
            System.err.println("Failed to parse JSON response: " + e.getMessage());
        }
        
        return false;
    }
    
    public static void processUserDetailsResponse(HttpResponse<String> response) {
        String responseBody = response.body();
        int statusCode = response.statusCode();
        
        System.out.println("Response Code: " + statusCode);
        System.out.println("Response Body: " + responseBody);

        Gson gson = new Gson();

        try {
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            
            if (jsonResponse.get("success").getAsBoolean()) {
                JsonObject userJson = jsonResponse.getAsJsonObject("userInfo");

                UserDto user = gson.fromJson(userJson, UserDto.class);
                
                System.out.println("\n--- User Details ---");
                System.out.println("User ID: " + user.getId());
                System.out.println("Full Name: " + user.getFullName());
                System.out.println("Username: " + user.getUserName());
            } else {
                System.out.println("Error: " + jsonResponse.get("message").getAsString());
            }
        } catch (Exception e) {
            System.err.println("Failed to parse JSON response: " + e.getMessage());
        }
    }

}
