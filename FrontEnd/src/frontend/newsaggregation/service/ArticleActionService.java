package frontend.newsaggregation.service;

import java.io.IOException;
import java.net.http.HttpResponse;

import frontend.newsaggregation.constant.StaticConfiguration;
import frontend.newsaggregation.util.HttpUtil;

public class ArticleActionService {
    private static final String BASE_URL = StaticConfiguration.getBaseUrl();

    public boolean saveArticle(int articleId) {
        try {
            HttpResponse<String> response = HttpUtil.sendPostRequest(BASE_URL + "/news/" + articleId + "/save", "{}");
            return HttpUtil.processResponse(response, "Save Article");
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to save article: " + e.getMessage());
            return false;
        }
    }

    public boolean reactToArticle(int articleId, String reaction) {
        try {
            String json = "{ \"reaction\": \"" + reaction + "\" }";
            HttpResponse<String> response = HttpUtil.sendPostRequest(BASE_URL + "/news/" + articleId + "/reaction", json);
            return HttpUtil.processResponse(response, "React to Article");
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to react to article: " + e.getMessage());
            return false;
        }
    }

    public boolean reportArticle(int articleId) {
        try {
            HttpResponse<String> response = HttpUtil.sendPostRequest(BASE_URL + "/news/" + articleId + "/report", "{}");
            return HttpUtil.processResponse(response, "Report Article");
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to report article: " + e.getMessage());
            return false;
        }
    }
}
