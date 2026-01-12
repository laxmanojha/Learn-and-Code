package backend.newsaggregation.ingestion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import backend.newsaggregation.model.NewsArticle;
import backend.newsaggregation.service.ExternalServerService;

public class TheNewsApi implements ExternalNewsApi{
	
	private static final String NEWS_API_KEY = ExternalServerService.getInstance().getApiKeyByServerName("TheNewsApi");
    private static final String NEWS_URL = "https://api.thenewsapi.com/v1/news/top?api_token=" + NEWS_API_KEY;
	private static final Logger logger = LoggerFactory.getLogger(TheNewsApi.class);
	
	@Override
	public List<NewsArticle> parseExternalApiData() {
		String jsonResponse = fetchNewsApiData();
        List<NewsArticle> dataList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");

        try {
            JsonNode root = mapper.readTree(jsonResponse);
            JsonNode dataArray = root.path("data");

            int idCounter = 1;
            for (JsonNode item : dataArray) {
                NewsArticle data = new NewsArticle();

                data.setId(idCounter++);
                data.setTitle(item.path("title").asText(null));
                data.setDescription(item.path("description").asText(null));
                data.setSnippet(item.path("snippet").asText(null));
                data.setUrl(item.path("url").asText(null));
                data.setImageUrl(item.path("image_url").asText(null));
                data.setSource(item.path("source").asText(null));
                
                List<String> categories = new ArrayList<>();
                JsonNode categoryArray = item.path("categories");
                if (categoryArray.isArray()) {
                    for (JsonNode cat : categoryArray) {
                        categories.add(cat.asText());
                    }
                    if (categories.isEmpty()) {
                    	categories.add("general");
                    }
                }
                data.setCategories(categories);

                // Parse published date
                String publishedAtStr = item.path("published_at").asText(null);
                if (publishedAtStr != null) {
                    Date publishedAt = dateFormat.parse(publishedAtStr);
                    data.setPublishedAt(publishedAt);
                }

                dataList.add(data);
            }

        } catch (Exception e) {
            logger.error(e.getStackTrace().toString());
        }

        return dataList;
    }
	
	public String fetchNewsApiData() {
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(NEWS_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.setRequestProperty("Accept", "application/json");

            int status = connection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }
            } else {
                logger.error("GET request failed. Response Code: " + status);
            }

            connection.disconnect();
        } catch (Exception e) {
            logger.error(e.getStackTrace().toString());
        }

        return response.toString();
    }

}
