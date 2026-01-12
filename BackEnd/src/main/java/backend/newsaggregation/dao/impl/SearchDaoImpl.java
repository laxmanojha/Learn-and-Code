package backend.newsaggregation.dao.impl;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backend.newsaggregation.dao.interfaces.SearchDao;
import backend.newsaggregation.model.NewsArticle;
import backend.newsaggregation.util.DatabaseConfig;

public class SearchDaoImpl implements SearchDao {

    private static SearchDaoImpl instance;
    private static Connection conn;
	private static final Logger logger = LoggerFactory.getLogger(SearchDaoImpl.class);

    private SearchDaoImpl() {}

    public static SearchDaoImpl getInstance() {
        if (instance == null) {
            instance = new SearchDaoImpl();
            conn = DatabaseConfig.getConnection();
        }
        return instance;
    }

    private NewsArticle extractArticleWithReactionCount(ResultSet rs) throws SQLException {
        NewsArticle article = new NewsArticle();
        article.setId(rs.getInt("id"));
        article.setTitle(rs.getString("title"));
        article.setSnippet(rs.getString("description"));
        article.setSource(rs.getString("source"));
        article.setUrl(rs.getString("url"));
        article.setPublishedAt(rs.getDate("published_at"));
        article.setCategories(new ArrayList<>());
        article.setLikeCount(rs.getInt("like_count"));
        article.setDislikeCount(rs.getInt("dislike_count"));
        return article;
    }

    private static final String BASE_SELECT = """
        SELECT na.*, 
               COUNT(CASE WHEN nar.reaction_type = 'like' THEN 1 END) AS like_count,
               COUNT(CASE WHEN nar.reaction_type = 'dislike' THEN 1 END) AS dislike_count
        FROM news_article na
        LEFT JOIN news_article_reaction nar ON na.id = nar.news_id
        WHERE na.is_hidden = FALSE
          AND NOT EXISTS (
              SELECT 1
              FROM news_article_category nac
              JOIN news_category nc ON nac.category_id = nc.id
              WHERE nac.news_id = na.id
                AND nc.is_hidden = TRUE
          )
          AND (na.title LIKE ? OR na.description LIKE ?)
        """;

    @Override
    public List<NewsArticle> searchArticles(String keyword) {
        String sql = BASE_SELECT + " GROUP BY na.id";
        return search(sql, keyword, null, null);
    }

    @Override
    public List<NewsArticle> searchArticles(String keyword, LocalDate startDate, LocalDate endDate) {
        String sql = BASE_SELECT + " AND na.created_at >= ? AND na.created_at <= ? GROUP BY na.id";
        return search(sql, keyword, startDate, endDate);
    }

    @Override
    public List<NewsArticle> searchArticlesSorted(String keyword, String sortBy) {
        String sortColumn = "like_count";
        if ("dislikes".equalsIgnoreCase(sortBy)) {
            sortColumn = "dislike_count";
        }

        String sql = BASE_SELECT + " GROUP BY na.id ORDER BY " + sortColumn + " DESC";
        return search(sql, keyword, null, null);
    }

    @Override
    public List<NewsArticle> searchArticles(String keyword, LocalDate startDate, LocalDate endDate, String sortBy) {
        String sortColumn = "like_count";
        if ("dislikes".equalsIgnoreCase(sortBy)) {
            sortColumn = "dislike_count";
        }

        String sql = BASE_SELECT + " AND na.created_at >= ? AND na.created_at <= ? GROUP BY na.id ORDER BY " + sortColumn + " DESC";
        return search(sql, keyword, startDate, endDate);
    }

    private List<NewsArticle> search(String sql, String keyword, LocalDate start, LocalDate end) {
        List<NewsArticle> articles = new ArrayList<>();

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);

            int idx = 1;
            stmt.setString(idx++, "%" + keyword + "%");
            stmt.setString(idx++, "%" + keyword + "%");

            if (start != null && end != null) {
                stmt.setDate(idx++, Date.valueOf(start));
                stmt.setDate(idx++, Date.valueOf(end));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                articles.add(extractArticleWithReactionCount(rs));
            }

        } catch (SQLException e) {
            logger.error(e.getStackTrace().toString());
        }

        return articles;
    }
}
