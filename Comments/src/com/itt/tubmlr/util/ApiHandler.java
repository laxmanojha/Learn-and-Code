package com.itt.tubmlr.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.itt.tubmlr.dto.ApiRequestParamsDto;

public class ApiHandler {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    public HttpResponse<String> sendGetRequest(ApiRequestParamsDto apiRequestParams) throws Exception {
        String urlString = "https://" + apiRequestParams.getBlogName() + ".tumblr.com/api/read/json?type=photo&num=" + apiRequestParams.getNum() + "&start=" + apiRequestParams.getStart();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .GET()
                .build();

        return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }
    
    public String parseResponse(HttpResponse<String> response) {
    	if (response.statusCode() != 200) {
    		throw new RuntimeException("HTTP error code: " + response.statusCode());
    	}
    	
    	String jsonResponse = cleanJsonResponse(response.body());
    	
    	return jsonResponse;
    }
    
    private String cleanJsonResponse(String response) {
    	return response.replaceFirst("var tumblr_api_read = ", "").trim();
    }
}
