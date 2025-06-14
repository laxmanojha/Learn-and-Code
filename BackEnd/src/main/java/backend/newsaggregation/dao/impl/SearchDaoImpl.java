package backend.newsaggregation.dao.impl;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import backend.newsaggregation.dao.interfaces.SearchDao;
import backend.newsaggregation.model.NewsArticle;
import backend.newsaggregation.util.DatabaseConfig;

public class SearchDaoImpl implements SearchDao {

    private static SearchDaoImpl instance;

    private SearchDaoImpl() {}

    public static SearchDaoImpl getInstance() {
        if (instance == null) {
            instance = new SearchDaoImpl();
        }
        return instance;
    }

    private NewsArticle extractArticle(ResultSet rs) throws SQLException {
        NewsArticle article = new NewsArticle();
        article.setId(rs.getInt("id"));
        article.setTitle(rs.getString("title"));
        article.setContent(rs.getString("content"));
        article.setSource(rs.getString("source"));
        article.setUrl(rs.getString("url"));
        article.setCategoryId(rs.getString("category"));
        article.setDatePublished(rs.getDate("published_date").toLocalDate());
        article.setLikes(rs.getInt("likes"));
        article.setDislikes(rs.getInt("dislikes"));
        return article;
    }

    @Override
    public List<NewsArticle> searchArticles(String keyword) {
        String sql = "SELECT * FROM news_articles WHERE title LIKE ? OR content LIKE ?";
        return search(sql, keyword, null, null, null);
    }

    @Override
    public List<NewsArticle> searchArticles(String keyword, LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT * FROM news_articles 
            WHERE (title LIKE ? OR content LIKE ?)
              AND published_date BETWEEN ? AND ?
        """;
        return search(sql, keyword, startDate, endDate, null);
    }

    @Override
    public List<NewsArticle> searchArticlesSorted(String keyword, String sortBy) {
        String column = "likes";
        if ("dislikes".equalsIgnoreCase(sortBy)) {
            column = "dislikes";
        }
        String sql = """
            SELECT * FROM news_articles 
            WHERE title LIKE ? OR content LIKE ?
            ORDER BY """ + column + " DESC";

        return search(sql, keyword, null, null, null);
    }

    private List<NewsArticle> search(String sql, String keyword, LocalDate start, LocalDate end, String sortBy) {
        List<NewsArticle> articles = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");

            if (start != null && end != null) {
                stmt.setDate(3, Date.valueOf(start));
                stmt.setDate(4, Date.valueOf(end));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                articles.add(extractArticle(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return articles;
    }
}
