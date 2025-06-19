package aa;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

class TheNewsApiFetcher implements ExternalApiSource {
    private static final String API_URL = "https://api.thenewsapi.com/v1/news/top?api_token=H26dF12qYfehuW1gVF4Pu5qbTljKLUuvSJYWMaeX&locale=us";
    private static final int SERVER_ID = 2;

    @Override
    public List<NewsArticle> fetchArticles() throws Exception {
        String response = getApiResponse(API_URL);
        JsonObject json = JsonParser.parseString(response).getAsJsonObject();
        JsonArray data = json.getAsJsonArray("data");

        List<NewsArticle> result = new ArrayList<>();
        for (JsonElement element : data) {
            JsonObject obj = element.getAsJsonObject();
            NewsArticle article = new NewsArticle();
            article.setTitle(obj.get("title").getAsString());
            article.setContent(obj.get("description").getAsString());
            article.setSource(obj.get("source").getAsString());
            article.setUrl(obj.get("url").getAsString());
            article.setDatePublished(Date.valueOf(obj.get("published_at").getAsString().substring(0, 10)));
            article.setCategoryIds(CategoryInferenceUtil.inferCategories(
                obj.get("title").getAsString() + " " + obj.get("description").getAsString()));
            result.add(article);
        }
        return result;
    }

    private String getApiResponse(String url) throws Exception {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        return in.lines().collect(Collectors.joining());
    }

    @Override
    public int getServerId() {
        return SERVER_ID;
    }
}
