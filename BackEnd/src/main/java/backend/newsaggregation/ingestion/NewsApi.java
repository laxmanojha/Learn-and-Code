package backend.newsaggregation.ingestion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import backend.newsaggregation.model.NewsArticle;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class NewsApi implements ExternalNewsApi {

    private static final String NEWS_API_KEY = "af3ce09176fb4fd3be6fcfd1e000776c";
    private static final String HEADLINES_URL = "https://newsapi.org/v2/top-headlines?country=us&apiKey=" + NEWS_API_KEY;
    private static final String SOURCES_URL = "https://newsapi.org/v2/top-headlines/sources?apiKey=" + NEWS_API_KEY;

    public String fetchNewsApiData(String apiUrl) {
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response.toString();
    }

    @Override
    public List<NewsArticle> parseExternalApiData() {
        List<NewsArticle> result = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        try {
            // Fetch both APIs
            JsonNode sourcesRoot = fetchJson(SOURCES_URL);
            JsonNode articlesRoot = fetchJson(HEADLINES_URL);

            // Build source ID -> category map
            Map<String, List<String>> sourceCategoryMap = new HashMap<>();
            JsonNode sourcesArray = sourcesRoot.path("sources");

            for (JsonNode source : sourcesArray) {
                String id = source.path("id").asText(null);
                String category = source.path("category").asText(null);

                if (id != null && category != null) {
                    sourceCategoryMap.put(id, Collections.singletonList(category));
                }
            }

            // Extract articles
            JsonNode articles = articlesRoot.path("articles");
            int idCounter = 1;

            for (JsonNode articleNode : articles) {
                NewsArticle article = new NewsArticle();

                article.setId(idCounter++);
                article.setTitle(articleNode.path("title").asText(null));
                article.setDescription(articleNode.path("description").asText(null));
                article.setSnippet(articleNode.path("content").asText(null));
                article.setUrl(articleNode.path("url").asText(null));
                article.setImageUrl(articleNode.path("urlToImage").asText(null));
                article.setSource(articleNode.path("source").path("name").asText(null));

                String publishedStr = articleNode.path("publishedAt").asText(null);
                if (publishedStr != null) {
                    article.setPublishedAt(sdf.parse(publishedStr));
                }

                String sourceId = articleNode.path("source").path("id").asText(null);
                List<String> categories = sourceCategoryMap.getOrDefault(sourceId, List.of("general"));
                article.setCategories(categories);

                result.add(article);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private JsonNode fetchJson(String apiUrl) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        StringBuilder sb = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(sb.toString());
    }
}
