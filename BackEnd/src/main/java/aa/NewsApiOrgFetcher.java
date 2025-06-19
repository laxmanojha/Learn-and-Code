package aa;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

class NewsApiOrgFetcher implements ExternalApiSource {
    private static final String API_URL = "https://newsapi.org/v2/top-headlines?country=us&category=business&apiKey=af3ce09176fb4fd3be6fcfd1e000776c";
    private static final int SERVER_ID = 1;

    @Override
    public List<NewsArticle> fetchArticles() throws Exception {
        String response = getApiResponse(API_URL);
        JsonObject json = JsonParser.parseString(response).getAsJsonObject();
        JsonArray articles = json.getAsJsonArray("articles");

        List<NewsArticle> result = new ArrayList<>();
        for (JsonElement element : articles) {
            JsonObject obj = element.getAsJsonObject();
            NewsArticle article = new NewsArticle();
            article.setTitle(obj.get("title").getAsString());
            article.setSnippet(obj.get("description").getAsString());
            article.setSource(obj.get("source").getAsJsonObject().get("name").getAsString());
            article.setUrl(obj.get("url").getAsString());
            article.setPublishedAt(Date.valueOf(obj.get("publishedAt").getAsString().substring(0, 10)));
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

