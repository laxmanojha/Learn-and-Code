package frontend.newsaggregation.service;

import com.google.gson.JsonObject;
import frontend.newsaggregation.util.HttpUtil;

import java.io.IOException;
import java.net.http.HttpResponse;

public class CategoryService {

    private static final String CATEGORY_URL = "http://localhost:8080/backend/api/news/category";

    public boolean addCategory(String categoryName) {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("name", categoryName);

            HttpResponse<String> response = HttpUtil.sendPostRequest(CATEGORY_URL, json.toString());
            return HttpUtil.processResponse(response, "Add Category");
        } catch (IOException | InterruptedException e) {
            System.err.println("Error while adding category: " + e.getMessage());
            return false;
        }
    }
}
