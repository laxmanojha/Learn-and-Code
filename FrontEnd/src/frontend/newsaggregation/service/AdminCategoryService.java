package frontend.newsaggregation.service;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;

import frontend.newsaggregation.constant.StaticConfiguration;
import frontend.newsaggregation.model.Category;
import frontend.newsaggregation.util.HttpUtil;

public class AdminCategoryService {

    private static final String BASE_URL = StaticConfiguration.getBaseUrl() + "admin/category";
    private static final String CATEGORY_LIST_URL = StaticConfiguration.getBaseUrl() + "news/category";
    private static final Gson gson = new Gson();
    private static AdminCategoryService instance; 

    public static AdminCategoryService getInstance() {
        if (instance == null) {
            instance = new AdminCategoryService();
        }
        return instance;
    }

    public List<Category> fetchAllCategories() {
        try {
            HttpResponse<String> response = HttpUtil.sendGetRequest(CATEGORY_LIST_URL);
            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), new TypeToken<List<Category>>() {}.getType());
            } else {
                System.out.println("Failed to fetch categories: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error fetching categories: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public boolean hideCategory(int categoryId) {
        String url = BASE_URL + "/" + categoryId + "/hide";
        try {
            HttpResponse<String> response = HttpUtil.sendPutRequest(url, "{}");
            return HttpUtil.processResponse(response, "Hide Category");
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to hide category: " + e.getMessage());
            return false;
        }
    }

    public boolean unhideCategory(int categoryId) {
        String url = BASE_URL + "/" + categoryId + "/unhide";
        try {
            HttpResponse<String> response = HttpUtil.sendPutRequest(url, "{}");
            return HttpUtil.processResponse(response, "Unhide Category");
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to unhide category: " + e.getMessage());
            return false;
        }
    }
}
