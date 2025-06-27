package frontend.newsaggregation.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import frontend.newsaggregation.model.User;

public class HttpUtil {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static String sessionCookie = null;
    
    public static HttpResponse<String> loginAndSetSessionCookie(String loginUrl, String payloadJson) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(loginUrl))
                .POST(HttpRequest.BodyPublishers.ofString(payloadJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Optional<String> cookie = response.headers().firstValue("Set-Cookie");

        cookie.ifPresentOrElse(
                value -> System.out.println("Session Cookie: " + value),
                () -> System.out.println("No session cookie received.")
        );

        sessionCookie = cookie.orElse(null);
        
        return response;
    }


    public static HttpResponse<String> sendPostRequest(String url, String formData) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(BodyPublishers.ofString(formData, StandardCharsets.UTF_8))
                .header("Content-Type", "application/json");
        
        if (sessionCookie != null) {
        	builder.header("Cookie", sessionCookie);
        }
        
        HttpRequest request = builder.build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> sendGetRequest(String url) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json");

        if (sessionCookie != null) {
            builder.header("Cookie", sessionCookie);
        }

        HttpRequest request = builder.build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> sendDeleteRequest(String url) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .header("Content-Type", "application/json");

        if (sessionCookie != null) {
            builder.header("Cookie", sessionCookie);
        }

        HttpRequest request = builder.build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
    
    public static HttpResponse<String> sendPutRequest(String url, String requestBody) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/json");

        if (sessionCookie != null) {
            builder.header("Cookie", sessionCookie);
        }

        HttpRequest request = builder.build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static boolean processResponse(HttpResponse<String> response, String action) {
        String responseBody = response.body();

        try {
        	System.out.println(responseBody);
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
        Gson gson = new Gson();

        try {
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            
            if (jsonResponse.get("success").getAsBoolean()) {
                JsonObject userJson = jsonResponse.getAsJsonObject("userInfo");

                User user = gson.fromJson(userJson, User.class);
                
                user.toString();
            } else {
                System.out.println("Error: " + jsonResponse.get("message").getAsString());
            }
        } catch (Exception e) {
            System.err.println("Failed to parse JSON response: " + e.getMessage());
        }
    }

}
