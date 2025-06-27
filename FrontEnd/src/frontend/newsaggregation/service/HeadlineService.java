package frontend.newsaggregation.service;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

import frontend.newsaggregation.constant.StaticConfiguration;
import frontend.newsaggregation.model.NewsArticle;
import frontend.newsaggregation.util.HttpUtil;

public class HeadlineService {
    private static final String BASE_URL = StaticConfiguration.getBaseUrl();
    private static final Gson gson = new Gson();

    public List<NewsArticle> fetchTodayHeadlines() {
        try {
            HttpResponse<String> response = HttpUtil.sendGetRequest(BASE_URL + "/news/today");

            if (response.statusCode() == 200) {
                NewsArticle[] articles = gson.fromJson(response.body(), NewsArticle[].class);
                return Arrays.asList(articles);
            } else {
                System.out.println("Failed to fetch today's headlines. Status code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error fetching today's headlines: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public List<NewsArticle> fetchHeadlinesByDateRange(String startDate, String endDate, String category) {
        try {
            String url = BASE_URL + "/news/date-range?start=" + startDate + "&end=" + endDate + "&type=" + category;
            HttpResponse<String> response = HttpUtil.sendGetRequest(url);

            if (response.statusCode() == 200) {
                NewsArticle[] articles = gson.fromJson(response.body(), NewsArticle[].class);
                return Arrays.asList(articles);
            } else {
                System.out.println("Failed to fetch headlines for date range. Status code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error fetching headlines for date range: " + e.getMessage());
        }
        return new ArrayList<>();
    }
}
