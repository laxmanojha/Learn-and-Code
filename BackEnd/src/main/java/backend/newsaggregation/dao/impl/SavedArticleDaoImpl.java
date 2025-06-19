package backend.newsaggregation.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import backend.newsaggregation.dao.interfaces.SavedArticleDao;
import backend.newsaggregation.model.NewsArticle;
import backend.newsaggregation.util.DatabaseConfig;

public class SavedArticleDaoImpl implements SavedArticleDao {

    private static SavedArticleDaoImpl instance;

    private SavedArticleDaoImpl() {}

    public static synchronized SavedArticleDaoImpl getInstance() {
        if (instance == null) {
            instance = new SavedArticleDaoImpl();
        }
        return instance;
    }

    @Override
    public boolean saveArticle(int userId, int newsId) {
        String sql = "INSERT INTO saved_news(news_id, user_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, newsId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteSavedArticle(int userId, int newsId) {
        String sql = "DELETE FROM saved_news WHERE user_id = ? AND news_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, newsId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isArticleSavedByUser(int userId, int newsId) {
        String sql = "SELECT 1 FROM saved_news WHERE user_id = ? AND news_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, newsId);
            return ps.executeQuery().next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<NewsArticle> getSavedArticlesByUser(int userId) {
        List<NewsArticle> savedArticles = new ArrayList<>();

        String sql = """
            SELECT na.*, nac.category_id AS category_id
            FROM saved_news sn
            JOIN news_article na ON sn.news_id = na.id
            LEFT JOIN news_article_category nac ON na.id = nac.news_id
            WHERE sn.user_id = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                savedArticles.add(new NewsArticle(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("source"),
                    rs.getString("url"),
                    rs.getInt("category_id"),
                    rs.getTimestamp("published_at")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return savedArticles;
    }
}

