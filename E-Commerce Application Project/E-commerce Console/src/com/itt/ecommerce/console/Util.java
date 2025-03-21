//package com.itt.ecommerce.console;
//
//import java.io.IOException;
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.net.http.HttpRequest.BodyPublishers;
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import com.google.gson.reflect.TypeToken;
//import com.itt.ecommerce.dto.CategoryDto;
//
//public class Util {
//	private static final String BASE_URL = "http://localhost:8080/E-Commerce-Application";
//    private static final HttpClient client = HttpClient.newHttpClient();
//
//    public static boolean loginUser(String username, String password) throws IOException, InterruptedException {
//        String url = BASE_URL + "/user/login";
//        String formData = "username=" + username + "&password=" + password;
//
//        HttpResponse<String> response = sendPostRequest(url, formData);
//        return processResponse(response, "Login");
//    }
//
//    public static boolean registerUser(String name, String username, String password) throws IOException, InterruptedException {
//        String url = BASE_URL + "/user/register";
//        String formData = "fullname=" + name + "&username=" + username + "&password=" + password;
//
//        HttpResponse<String> response = sendPostRequest(url, formData);
//        return processResponse(response, "Registration");
//    }
//
//    private static HttpResponse<String> sendPostRequest(String url, String formData) throws IOException, InterruptedException {
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(url))
//                .header("Content-Type", "application/x-www-form-urlencoded")
//                .POST(BodyPublishers.ofString(formData, StandardCharsets.UTF_8))
//                .build();
//
//        return client.send(request, HttpResponse.BodyHandlers.ofString());
//    }
//
//    private static boolean processResponse(HttpResponse<String> response, String action) {
//        int statusCode = response.statusCode();
//        String responseBody = response.body();
//
//        System.out.println("Response Code: " + statusCode);
//        System.out.println("Response Body: " + responseBody);
//
//        try {
//            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
//            boolean success = jsonResponse.get("success").getAsBoolean();
//            String message = jsonResponse.get("message").getAsString();
//
//            if (success) {
//                System.out.println(action + " Successful: " + message);
//                return true;
//            } else {
//                System.out.println(action + " Failed: " + message);
//            }
//        } catch (Exception e) {
//            System.err.println("Failed to parse JSON response: " + e.getMessage());
//        }
//        
//        return false;
//    }
//	
//	public static void viewUserDetails(String username) {
//        System.out.println("\nFetching user details for " + username + "...");
//    }
//
//	private static final Gson gson = new Gson();
//
//    public static List<CategoryDto> getAllCategories() throws IOException, InterruptedException {
//        String url = BASE_URL + "/api/category";
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(url))
//                .GET()
//                .header("Accept", "application/json")
//                .build();
//
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        int statusCode = response.statusCode();
//        String responseBody = response.body();
//        
//        System.out.println("Response Code: " + statusCode);
//        System.out.println("Response Body: " + responseBody);
//
//        if (statusCode == 200) {
//            return parseCategoryResponse(responseBody);
//        } else {
//            System.err.println("Failed to fetch categories: " + responseBody);
//            return List.of();
//        }
//    }
//
//    private static List<CategoryDto> parseCategoryResponse(String jsonResponse) {
//        try {
//            JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
//
//            if (jsonObject.get("success").getAsBoolean()) {
//                JsonArray categoriesArray = jsonObject.getAsJsonArray("categories");
//
//                return gson.fromJson(categoriesArray, new TypeToken<List<CategoryDto>>() {}.getType());
//            } else {
//                System.err.println("Error: " + jsonObject.get("message").getAsString());
//            }
//        } catch (Exception e) {
//            System.err.println("Failed to parse category response: " + e.getMessage());
//        }
//        return List.of();
//    }
//
//    public static void viewCart(String username) {
//        System.out.println("\nFetching cart for " + username + "...");
//    }
//
//    public static void viewOrderHistory(String username) {
//        System.out.println("\nFetching order history for " + username + "...");
//    }
//}
