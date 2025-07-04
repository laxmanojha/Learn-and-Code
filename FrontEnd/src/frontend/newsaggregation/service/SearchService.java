package frontend.newsaggregation.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import frontend.newsaggregation.constant.StaticConfiguration;
import frontend.newsaggregation.model.NewsArticle;
import frontend.newsaggregation.util.HttpUtil;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SearchService {
    private final Gson gson = new Gson();

    public List<NewsArticle> searchArticles(String query, String startDate, String endDate, String sort) {
        try {
            StringBuilder urlBuilder = new StringBuilder(StaticConfiguration.getBaseUrl() + "/news/search?query=")
                    .append(URLEncoder.encode(query, StandardCharsets.UTF_8));

            if (startDate != null && endDate != null) {
                urlBuilder.append("&start=").append(startDate)
                          .append("&end=").append(endDate);
            }

            if (sort != null) {
                urlBuilder.append("&sort=").append(sort);
            }

            HttpResponse<String> response = HttpUtil.sendGetRequest(urlBuilder.toString());

            if (response.statusCode() == 200) {
                Type listType = new TypeToken<ArrayList<NewsArticle>>() {}.getType();
                return gson.fromJson(response.body(), listType);
            } else {
                System.out.println("Failed to fetch search results. Status code: " + response.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Exception while fetching search results: " + e.getMessage());
        }

        return new ArrayList<>();
    }
}
