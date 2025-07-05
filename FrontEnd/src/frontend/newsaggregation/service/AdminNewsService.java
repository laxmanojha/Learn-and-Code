package frontend.newsaggregation.service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import frontend.newsaggregation.model.NewsArticleReport;
import frontend.newsaggregation.util.HttpUtil;

public class AdminNewsService {

    private static final Gson gson = new Gson();
    private static final String BASE_URL = "http://localhost:8080/News-Aggregation/api";
    private static AdminNewsService instance; 

    public static AdminNewsService getInstance() {
        if (instance == null) {
            instance = new AdminNewsService();
        }
        return instance;
    }

    public List<NewsArticleReport> getReportedArticles() {
        try {
            HttpResponse<String> response = HttpUtil.sendGetRequest(BASE_URL + "/news-report/");
            if (response.statusCode() == 200) {
                Type listType = new TypeToken<List<NewsArticleReport>>() {}.getType();
                return gson.fromJson(response.body(), listType);
            } else {
                System.out.println("Failed to fetch reported articles. Response: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error while fetching reported articles: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public boolean hideNews(int newsId) {
        try {
            HttpResponse<String> response = HttpUtil.sendPutRequest(BASE_URL + "/admin/news/" + newsId + "/hide", "");
            return HttpUtil.processResponse(response, "Hide News Article");
        } catch (IOException | InterruptedException e) {
            System.err.println("Error hiding article: " + e.getMessage());
            return false;
        }
    }

    public boolean unhideNews(int newsId) {
        try {
            HttpResponse<String> response = HttpUtil.sendPutRequest(BASE_URL + "/admin/news/" + newsId + "/unhide", "");
            return HttpUtil.processResponse(response, "Unhide News Article");
        } catch (IOException | InterruptedException e) {
            System.err.println("Error unhiding article: " + e.getMessage());
            return false;
        }
    }
}
