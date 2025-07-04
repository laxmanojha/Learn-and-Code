package backend.newsaggregation.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import backend.newsaggregation.model.NewsArticle;
import backend.newsaggregation.util.DatabaseConfig;

public class NotificationDaoImpl {
	
	private static NotificationDaoImpl instance;
    private static Connection connection = DatabaseConfig.getConnection();

    private NotificationDaoImpl() {}

    public static NotificationDaoImpl getInstance() {
        if (instance == null) {
            instance = new NotificationDaoImpl();
        }
        return instance;
    }

	public List<NewsArticle> getNewsForConsoleNotification(int userId, Timestamp from, Timestamp to){
		List<NewsArticle> result = new ArrayList<>();
		try {
			boolean hasCatPrefs = hasAnyEnabledCategories(userId);
			boolean hasGlobalKeywords = hasAnyEnabledGlobalKeywords(userId);
			
			if (!hasCatPrefs && !hasGlobalKeywords) {
				return getAllNewsBetween(from, to);
			}
			if (hasCatPrefs) result.addAll(getNotificationByCategoryPreference(userId, from, to));
			if (hasGlobalKeywords) result.addAll(getNotificationByGlobalKeywords(userId, from, to));
			
		}  catch (SQLException e) {
            e.printStackTrace();
        }
		
        return result;
    }

    private boolean hasAnyEnabledCategories(int userId) throws SQLException {
        String sql = "SELECT 1 FROM notification_category_pref WHERE user_id = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    private boolean hasAnyEnabledGlobalKeywords(int userId) throws SQLException {
        String sql = "SELECT 1 FROM notification_keyword_pref WHERE user_id = ? AND is_enabled = true LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    private List<NewsArticle> getAllNewsBetween(Timestamp from, Timestamp to) throws SQLException {
        List<NewsArticle> list = new ArrayList<>();
        String sql = "SELECT * FROM news_article WHERE created_at > ? AND created_at <= ? ORDER BY created_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, from);
            stmt.setTimestamp(2, to);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
            	NewsArticle article = new NewsArticle();
            	article.setId(rs.getInt("id"));
            	article.setTitle(rs.getString("title"));
            	article.setDescription(rs.getString("description"));
            	article.setUrl(rs.getString("url"));
            	article.setSource(rs.getString("source"));
            	article.setPublishedAt(rs.getTimestamp("created_at"));
                list.add(article);
            }
        }
        return list;
    }

    private List<NewsArticle> getNotificationByCategoryPreference(int userId, Timestamp from, Timestamp to) throws SQLException {
        List<NewsArticle> result = new ArrayList<>();
        String sql = """
            SELECT DISTINCT n.*
            FROM news_article n
            JOIN news_article_category nc ON n.id = nc.news_id
            JOIN notification_category_pref uck ON nc.category_id = uck.category_id
            WHERE uck.user_id = ?
              AND n.created_at > ? AND n.created_at <= ?
              AND (
                n.title LIKE CONCAT('%', uck.keyword, '%')
                OR n.description LIKE CONCAT('%', uck.keyword, '%')
              )
            ORDER BY n.created_at DESC
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setTimestamp(2, from);
            stmt.setTimestamp(3, to);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
            	NewsArticle article = new NewsArticle();
            	article.setId(rs.getInt("id"));
            	article.setTitle(rs.getString("title"));
            	article.setDescription(rs.getString("description"));
            	article.setUrl(rs.getString("url"));
            	article.setSource(rs.getString("source"));
            	article.setPublishedAt(rs.getTimestamp("created_at"));
                result.add(article);
            }
        }
        return result;
    }

    private List<NewsArticle> getNotificationByGlobalKeywords(int userId, Timestamp from, Timestamp to) throws SQLException {
        List<NewsArticle> matchedNews = new ArrayList<>();

        String keywordSql = "SELECT keyword FROM notification_keyword_pref WHERE user_id = ? AND is_enabled = true";
        List<String> keywords = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(keywordSql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    keywords.add(rs.getString("keyword"));
                }
            }
        }

        if (keywords.isEmpty()) return matchedNews;

        StringBuilder sql = new StringBuilder("SELECT * FROM news_article WHERE created_at > ? AND created_at <= ? AND (");
        for (int i = 0; i < keywords.size(); i++) {
            if (i > 0) sql.append(" OR ");
            sql.append("LOWER(title) LIKE ? OR LOWER(description) LIKE ?");
        }
        sql.append(") ORDER BY created_at DESC");

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            stmt.setTimestamp(1, from);
            stmt.setTimestamp(2, to);
            int index = 3;
            for (String keyword : keywords) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                stmt.setString(index++, pattern);
                stmt.setString(index++, pattern);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                	NewsArticle article = new NewsArticle();
                	article.setId(rs.getInt("id"));
                	article.setTitle(rs.getString("title"));
                	article.setDescription(rs.getString("description"));
                	article.setUrl(rs.getString("url"));
                	article.setSource(rs.getString("source"));
                	article.setPublishedAt(rs.getTimestamp("created_at"));
                    matchedNews.add(article);
                }
            }
        }

        return matchedNews;
    }
}
