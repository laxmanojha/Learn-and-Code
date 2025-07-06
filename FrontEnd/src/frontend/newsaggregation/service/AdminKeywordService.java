package frontend.newsaggregation.service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import frontend.newsaggregation.model.HiddenKeyword;
import frontend.newsaggregation.util.CustomDateDeserializer;
import frontend.newsaggregation.util.HttpUtil;

public class AdminKeywordService {

    private static final String BASE_URL = "http://localhost:8080/News-Aggregation/api/admin/keyword";
    private final Gson gson = new GsonBuilder()
    	    .registerTypeAdapter(Date.class, new CustomDateDeserializer())
    	    .create();
    private static AdminKeywordService instance; 

    public static AdminKeywordService getInstance() {
        if (instance == null) {
            instance = new AdminKeywordService();
        }
        return instance;
    }

    public List<HiddenKeyword> getHiddenKeywords() {
        try {
            HttpResponse<String> response = HttpUtil.sendGetRequest(BASE_URL);
            if (response.statusCode() == 200) {
                Type listType = new TypeToken<List<HiddenKeyword>>() {}.getType();
                return gson.fromJson(response.body(), listType);
            } else {
                System.out.println("Failed to fetch hidden keywords. Response: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error while fetching keywords: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public boolean addKeyword(String keyword) {
        JsonObject body = new JsonObject();
        body.addProperty("keyword", keyword);

        try {
            HttpResponse<String> response = HttpUtil.sendPostRequest(BASE_URL, body.toString());
            return HttpUtil.processResponse(response, "Add Keyword");
        } catch (IOException | InterruptedException e) {
            System.err.println("Error adding keyword: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteKeyword(int keywordId) {
        try {
            HttpResponse<String> response = HttpUtil.sendDeleteRequest(BASE_URL + "/" + keywordId);
            return HttpUtil.processResponse(response, "Delete Keyword");
        } catch (IOException | InterruptedException e) {
            System.err.println("Error deleting keyword: " + e.getMessage());
            return false;
        }
    }
}
