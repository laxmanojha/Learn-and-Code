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
    	article.setContent(rs.getString("description"));
    	article.setSource(rs.getString("source"));
    	article.setUrl(rs.getString("url"));
    	article.setCategoryId(Integer.parseInt(rs.getString("category")));
    	article.setDatePublished(rs.getDate("published_at"));
    	return article;
    }

    private NewsArticle extractArticleWithReactionCount(ResultSet rs) throws SQLException {
        NewsArticle article = new NewsArticle();
        article.setId(rs.getInt("id"));
        article.setTitle(rs.getString("title"));
        article.setContent(rs.getString("description"));
        article.setSource(rs.getString("source"));
        article.setUrl(rs.getString("url"));
        article.setCategoryId(Integer.parseInt(rs.getString("category")));
        article.setDatePublished(rs.getDate("published_at"));
        article.setLikes(Integer.parseInt(rs.getString("like_count")));
        article.setDisLikes(Integer.parseInt(rs.getString("dislike_count")));
        return article;
    }

    @Override
    public List<NewsArticle> searchArticles(String keyword) {
        String sql = "SELECT * FROM news_article WHERE title LIKE ? OR description LIKE ?";
        return search(sql, keyword, null, null, null);
    }

    @Override
    public List<NewsArticle> searchArticles(String keyword, LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT * FROM news_article 
            WHERE (title LIKE ? OR description LIKE ?)
              AND published_at BETWEEN ? AND ?
        """;
        return search(sql, keyword, startDate, endDate, null);
    }

    @Override
    public List<NewsArticle> searchArticlesSorted(String keyword, String sortBy) {
        String sortColumn = "like_count";
        if ("dislikes".equalsIgnoreCase(sortBy)) {
            sortColumn = "dislike_count";
        }

        String sql = """
            SELECT na.*, 
                   COUNT(CASE WHEN nar.reaction_type = 'like' THEN 1 END) AS like_count,
                   COUNT(CASE WHEN nar.reaction_type = 'dislike' THEN 1 END) AS dislike_count
            FROM news_article na
            LEFT JOIN news_article_reaction nar ON na.id = nar.news_id
            WHERE na.title LIKE ? OR na.description LIKE ?
            GROUP BY na.id
            ORDER BY ? DESC""";

        return search(sql, keyword, null, null, sortColumn);
    }

    @Override
    public List<NewsArticle> searchArticles(String keyword, LocalDate startDate, LocalDate endDate, String sortBy) {
        String sortColumn = "like_count"; // default
        if ("dislikes".equalsIgnoreCase(sortBy)) {
            sortColumn = "dislike_count";
        }

        String sql = """
            SELECT na.*, nac.category_id AS category,
                   COUNT(CASE WHEN nar.reaction_type = 'like' THEN 1 END) AS like_count,
                   COUNT(CASE WHEN nar.reaction_type = 'dislike' THEN 1 END) AS dislike_count
            FROM news_article na
            LEFT JOIN news_article_reaction nar ON na.id = nar.news_id
            JOIN news_article_category nac ON na.id = nac.news_id
            WHERE (na.title LIKE ? OR na.description LIKE ?)
              AND na.published_at BETWEEN ? AND ?
            GROUP BY na.id, nac.category_id
            ORDER BY ? DESC""";

        return search(sql, keyword, startDate, endDate, sortColumn);
    }


    private List<NewsArticle> search(String sql, String keyword, LocalDate start, LocalDate end, String sortColumn) {
        List<NewsArticle> articles = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");

            if (start != null && end != null) {
                stmt.setDate(3, Date.valueOf(start));
                stmt.setDate(4, Date.valueOf(end));
            }
            
            if (sortColumn != null) {
            	stmt.setString(3, sortColumn);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
            	if (sortColumn == null)
            		articles.add(extractArticle(rs));
            	else
            		articles.add(extractArticleWithReactionCount(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return articles;
    }
}
