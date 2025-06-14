package backend.newsaggregation.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        article.setCategoryId(Integer.parseInt(rs.getString("category")));
        article.setDatePublished(rs.getDate("published_date"));
//        article.setLikes(rs.getInt("likes"));
//        article.setDislikes(rs.getInt("dislikes"));
        return article;
    }

    @Override
    public List<NewsArticle> searchArticles(String keyword) {
        String sql = "SELECT * FROM news_articles WHERE title LIKE ? OR content LIKE ?";
        return search(sql, keyword, null, null);
    }

    @Override
    public List<NewsArticle> searchArticles(String keyword, LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT * FROM news_articles 
            WHERE (title LIKE ? OR content LIKE ?)
              AND published_date BETWEEN ? AND ?
        """;
        return search(sql, keyword, startDate, endDate);
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

        return search(sql, keyword, null, null);
    }
    
    @Override
    public List<NewsArticle> searchArticles(String keyword, LocalDate startDate, LocalDate endDate, String sortBy) {
        String sortColumn = "likes"; // default
        if ("dislikes".equalsIgnoreCase(sortBy)) {
            sortColumn = "dislikes";
        }

        String sql = """
            SELECT na.*, nac.news_category_id AS category 
            FROM news_article na
            JOIN news_article_stats nas ON na.id = nas.news_article_id
            JOIN news_article_category nac ON na.id = nac.news_article_id
            WHERE (na.title LIKE ? OR na.description LIKE ?)
              AND na.published_date BETWEEN ? AND ?
            ORDER BY nas.""" + sortColumn + " DESC";

        return search(sql, keyword, startDate, endDate);
    }

    private List<NewsArticle> search(String sql, String keyword, LocalDate start, LocalDate end) {
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
