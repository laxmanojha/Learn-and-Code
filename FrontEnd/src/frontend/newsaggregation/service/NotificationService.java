package frontend.newsaggregation.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import frontend.newsaggregation.constant.StaticConfiguration;
import frontend.newsaggregation.model.Category;
import frontend.newsaggregation.model.NotificationPreference;
import frontend.newsaggregation.util.HttpUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class NotificationService {

    private final Gson gson = new Gson();

    public List<String> fetchNotifications() {
        String url = StaticConfiguration.getBaseUrl() + "/notifications";
        try {
            HttpResponse<String> response = HttpUtil.sendGetRequest(url);
            if (response.statusCode() == 200) {
                Type listType = new TypeToken<List<String>>() {}.getType();
                return gson.fromJson(response.body(), listType);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to fetch notifications: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public List<Category> fetchCategories() {
        String url = StaticConfiguration.getBaseUrl() + "/news/category";
        try {
            HttpResponse<String> response = HttpUtil.sendGetRequest(url);
            if (response.statusCode() == 200) {
                Type listType = new TypeToken<List<Category>>() {}.getType();
                return gson.fromJson(response.body(), listType);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to fetch categories: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public List<NotificationPreference> fetchPreferences() {
        String url = StaticConfiguration.getBaseUrl() + "/notifications/preferences";
        try {
            HttpResponse<String> response = HttpUtil.sendGetRequest(url);
            if (response.statusCode() == 200) {
                Type listType = new TypeToken<List<NotificationPreference>>() {}.getType();
                return gson.fromJson(response.body(), listType);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to fetch preferences: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public boolean enableCategory(int categoryId, List<String> keywords) {
        String url = StaticConfiguration.getBaseUrl() + "/notifications/config/category";
        String body = gson.toJson(new CategoryRequest(categoryId, keywords));
        try {
            HttpResponse<String> response = HttpUtil.sendPostRequest(url, body);
            return HttpUtil.processResponse(response, "Enable Category");
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to enable category: " + e.getMessage());
            return false;
        }
    }

    public boolean disableCategory(int categoryId) {
        String url = StaticConfiguration.getBaseUrl() + "/notifications/config/category";
        String body = "{\"categoryId\": \"" + categoryId + "\"}";
        try {
            HttpResponse<String> response = HttpUtil.sendDeleteRequest(url, body);
            return HttpUtil.processResponse(response, "Disable Category");
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to disable category: " + e.getMessage());
            return false;
        }
    }

    public boolean enableKeyword(List<String> keywords) {
        String url = StaticConfiguration.getBaseUrl() + "/notifications/config/keywords";
        String body = gson.toJson(new KeywordRequest(keywords));
        try {
            HttpResponse<String> response = HttpUtil.sendPostRequest(url, body);
            return HttpUtil.processResponse(response, "Enable Keyword");
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to enable keyword: " + e.getMessage());
            return false;
        }
    }

    public boolean disableKeyword() {
        String url = StaticConfiguration.getBaseUrl() + "/notifications/config/keywords";
        try {
            HttpResponse<String> response = HttpUtil.sendDeleteRequest(url, "{}");
            return HttpUtil.processResponse(response, "Disable Keyword");
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to disable keyword: " + e.getMessage());
            return false;
        }
    }

    // Helper classes for request bodies
    class CategoryRequest {
        int categoryId;
        List<String> keywords;

        public CategoryRequest(int categoryId, List<String> keywords) {
            this.categoryId = categoryId;
            this.keywords = keywords;
        }
    }

    class KeywordRequest {
        List<String> keywords;

        public KeywordRequest(List<String> keywords) {
            this.keywords = keywords;
        }
    }
}
