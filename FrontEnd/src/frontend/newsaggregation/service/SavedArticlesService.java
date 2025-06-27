package frontend.newsaggregation.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import frontend.newsaggregation.constant.StaticConfiguration;
import frontend.newsaggregation.model.NewsArticle;
import frontend.newsaggregation.util.HttpUtil;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class SavedArticlesService {

    private final Gson gson = new Gson();

    public List<NewsArticle> fetchSavedArticles() {
        String url = StaticConfiguration.getBaseUrl() + "/news/saved";

        try {
            HttpResponse<String> response = HttpUtil.sendGetRequest(url);

            if (response.statusCode() == 200) {
                Type listType = new TypeToken<ArrayList<NewsArticle>>() {}.getType();
                return gson.fromJson(response.body(), listType);
            } else {
                System.out.println("Failed to fetch saved articles. Status code: " + response.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Exception while fetching saved articles: " + e.getMessage());
        }

        return new ArrayList<>();
    }

    public boolean deleteSavedArticle(int articleId) {
        String url = StaticConfiguration.getBaseUrl() + "/news/saved/" + articleId + "/delete";

        try {
            HttpResponse<String> response = HttpUtil.sendDeleteRequest(url);
            return HttpUtil.processResponse(response, "Delete Saved Article");
        } catch (IOException | InterruptedException e) {
            System.err.println("Exception while deleting saved article: " + e.getMessage());
            return false;
        }
    }

    public boolean saveArticle(int articleId) {
        String url = StaticConfiguration.getBaseUrl() + "/news/" + articleId + "/save";

        try {
            HttpResponse<String> response = HttpUtil.sendPostRequest(url, "{}");
            return HttpUtil.processResponse(response, "Save Article");
        } catch (IOException | InterruptedException e) {
            System.err.println("Exception while saving article: " + e.getMessage());
            return false;
        }
    }
}
