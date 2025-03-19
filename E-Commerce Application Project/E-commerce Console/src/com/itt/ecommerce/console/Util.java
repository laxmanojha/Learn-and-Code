package com.itt.ecommerce.console;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.charset.StandardCharsets;

public class Util {
	public static void loginUser(String username, String password) throws IOException, InterruptedException {
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

        // Printing response
        System.out.println("Response Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());
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

        // Printing response
        System.out.println("Response Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());
    }
}
