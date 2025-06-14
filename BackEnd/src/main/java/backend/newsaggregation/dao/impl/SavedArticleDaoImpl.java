package backend.newsaggregation.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SavedArticleDaoImpl implements SavedArticleDao {

    private static SavedArticleDaoImpl instance;

    private SavedArticleDaoImpl() {}

    public static SavedArticleDaoImpl getInstance() {
        if (instance == null) {
            instance = new SavedArticleDaoImpl();
        }
        return instance;
    }

    @Override
    public boolean saveArticle(int userId, int newsId) {
        String sql = "INSERT INTO saved_articles (user_id, news_id, saved_at) VALUES (?, ?, NOW())";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, newsId);
            return stmt.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException e) {
            // duplicate entry, ignore or handle as per use case
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean deleteSavedArticle(int userId, int newsId) {
        String sql = "DELETE FROM saved_articles WHERE user_id = ? AND news_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, newsId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public List<News> getSavedArticlesByUser(int userId) {
        List<News> articles = new ArrayList<>();
        String sql = "SELECT n.* FROM news n JOIN saved_articles s ON n.id = s.news_id WHERE s.user_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                News news = new News();
                news.setId(rs.getInt("id"));
                news.setTitle(rs.getString("title"));
                news.setDescription(rs.getString("description"));
                news.setUrl(rs.getString("url"));
                news.setSource(rs.getString("source"));
                news.setCategory(rs.getString("category"));
                news.setPublishedAt(rs.getTimestamp("published_at"));
                news.setLikes(rs.getInt("likes"));
                news.setDislikes(rs.getInt("dislikes"));
                articles.add(news);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return articles;
    }

    @Override
    public boolean isArticleSavedByUser(int userId, int newsId) {
        String sql = "SELECT 1 FROM saved_articles WHERE user_id = ? AND news_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, newsId);

            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
