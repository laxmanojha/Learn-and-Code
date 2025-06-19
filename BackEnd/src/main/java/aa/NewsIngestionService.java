package aa;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.gson.*;

import backend.newsaggregation.dao.interfaces.NewsDao;
import backend.newsaggregation.util.CategoryInferenceUtil;

public class NewsIngestionService {

    private final NewsDao newsDao = NewsDao.getInstance();

    private final List<ExternalApiSource> sources = List.of(
        new NewsApiOrgFetcher(),
        new TheNewsApiFetcher()
    );

    public void syncAllSources() {
        for (ExternalApiSource source : sources) {
            try {
                List<NewsArticle> articles = source.fetchArticles();
                for (NewsArticle article : articles) {
                    newsDao.insertOrUpdateArticle(article);
                }
                updateServerStatus(source.getServerId(), true);
            } catch (Exception e) {
                updateServerStatus(source.getServerId(), false);
                e.printStackTrace();
            }
        }
    }

    private void updateServerStatus(int serverId, boolean success) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "UPDATE external_server SET last_accessed = ?, server_status_id = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                stmt.setInt(2, success ? 1 : 2); // 1 = active, 2 = inactive
                stmt.setInt(3, serverId);
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}