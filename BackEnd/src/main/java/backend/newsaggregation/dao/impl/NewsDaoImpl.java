package backend.newsaggregation.dao.impl;

import java.sql.Connection;
import java.sql.Statement;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backend.newsaggregation.dao.interfaces.NewsDao;
import backend.newsaggregation.model.NewsArticle;
import backend.newsaggregation.model.NewsArticleCategoryInfo;
import backend.newsaggregation.util.DatabaseConfig;

public class NewsDaoImpl implements NewsDao {

    private static NewsDaoImpl instance;
    private static Connection conn;
	private static final Logger logger = LoggerFactory.getLogger(NewsDaoImpl.class);

    private NewsDaoImpl() {}

    public static NewsDaoImpl getInstance() {
        if (instance == null) {
            instance = new NewsDaoImpl();
            conn = DatabaseConfig.getConnection();
        }
        return instance;
    }

    private NewsArticle mapRowToArticle(ResultSet rs) throws SQLException {
        return new NewsArticle(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("description"),
            rs.getString("source"),
            rs.getString("url"),
            rs.getInt("category_id"),
            rs.getTimestamp("published_at")
        );
    }
    
    private String baseQuery(String condition) {
        return """
            SELECT na.*, nac.category_id AS category_id
            FROM news_article na
            LEFT JOIN news_article_category nac ON na.id = nac.news_id
            LEFT JOIN news_category nc ON nac.category_id = nc.id
            WHERE na.is_hidden = FALSE
              AND NOT EXISTS (
                  SELECT 1
                  FROM news_article_category nac2
                  JOIN news_category nc2 ON nac2.category_id = nc2.id
                  WHERE nac2.news_id = na.id
                    AND nc2.is_hidden = TRUE
              )
            """ + (condition == null || condition.isBlank() ? "" : " AND " + condition);
    }
    
    @Override
    public int saveNews(NewsArticle item) {
        String insertQuery = "INSERT INTO news_article " +
                "(title, description, snippet, published_at, url, image_url, source) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement stmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, item.getTitle());
            stmt.setString(2, item.getDescription());
            stmt.setString(3, item.getSnippet());

            if (item.getPublishedAt() != null) {
                stmt.setTimestamp(4, new java.sql.Timestamp(item.getPublishedAt().getTime()));
            } else {
                stmt.setNull(4, java.sql.Types.TIMESTAMP);
            }

            stmt.setString(5, item.getUrl());
            stmt.setString(6, item.getImageUrl());
            stmt.setString(7, item.getSource());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            // Duplicate URL detected — fetch existing article ID
            System.out.println("Duplicate article, fetching existing ID...");
            return getNewsIdByUrl(item.getUrl());
        } catch (SQLException e) {
            logger.error(e.getStackTrace().toString());
        }
        return -1;
    }

    private int getNewsIdByUrl(String url) {
        String query = "SELECT id FROM news_article WHERE url = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, url);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            logger.error(e.getStackTrace().toString());
        }
        return -1;
    }
    
    @Override
    public int getLatestNewsArticleId() {
        String query = "SELECT id FROM news_article ORDER BY id DESC LIMIT 1";

        try {
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }

        } catch (SQLException e) {
            logger.error(e.getStackTrace().toString());
        }

        return -1;
    }
    
    public int getOrInsertCategoryId(String categoryType) {
        String selectQuery = "SELECT id FROM news_category WHERE category_type = ?";
        String insertQuery = "INSERT INTO news_category (category_type) VALUES (?)";

        try {

            try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
                selectStmt.setString(1, categoryType);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("id");
                    }
                }
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, categoryType);
                int affectedRows = insertStmt.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            return generatedKeys.getInt(1);
                        }
                    }
                }
            }

        } catch (SQLException e) {
            logger.error(e.getStackTrace().toString());
        }

        return -1;
    }
    
    @Override
    public boolean insertNewsCategoryMapping(int newsId, int categoryId) {
        String insertQuery = "INSERT INTO news_article_category (news_id, category_id) VALUES (?, ?)";

        try (
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            stmt.setInt(1, newsId);
            stmt.setInt(2, categoryId);

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { 
                System.out.println("Mapping already exists for news_id=" + newsId + " and category_id=" + categoryId);
                return true;
            } else {
                logger.error(e.getStackTrace().toString());
                return false;
            }
        }
    }

    @Override
    public List<NewsArticle> getNewsByDate(Date date) {
        String sql = baseQuery("DATE(na.published_at) = ?");
        return getNewsList(sql, ps -> ps.setDate(1, new Date(date.getTime())));
    }

    @Override
    public List<NewsArticle> getNewsByDateAndCategory(Date date, String category) {
        String sql = baseQuery("DATE(na.published_at) = ? AND nc.category_type = ?");
        return getNewsList(sql, ps -> {
            ps.setDate(1, new Date(date.getTime()));
            ps.setString(2, category);
        });
    }

    @Override
    public List<NewsArticle> getNewsByDateRange(Date startDate, Date endDate) {
        String sql = baseQuery("na.created_at >= ? AND na.created_at <= ?");
        return getNewsList(sql, ps -> {
            ps.setDate(1, new Date(startDate.getTime()));
            ps.setDate(2, new Date(endDate.getTime()));
        });
    }

    @Override
    public List<NewsArticle> getNewsByDateRangeAndCategory(Date startDate, Date endDate, String category) {
        String sql = baseQuery("na.created_at >= ? AND na.created_at <= ? AND nc.category_type = ?");
        return getNewsList(sql, ps -> {
            ps.setDate(1, new Date(startDate.getTime()));
            ps.setDate(2, new Date(endDate.getTime()));
            ps.setString(3, category);
        });
    }

    @Override
    public NewsArticle getNewsById(int id) {
        String sql = baseQuery("na.id = ?");
        try {
             PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? mapRowToArticle(rs) : null;

        } catch (SQLException e) {
            logger.error(e.getStackTrace().toString());
            return null;
        }
    }

    private List<NewsArticle> getNewsList(String sql, PreparedStatementSetter setter) {
        List<NewsArticle> articles = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);

            setter.set(ps);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                articles.add(mapRowToArticle(rs));
            }

        } catch (SQLException e) {
            logger.error(e.getStackTrace().toString());
        }
        return articles;
    }
    
    @Override
    public List<NewsArticleCategoryInfo> getAllCategory(int newsId) {
    	List<NewsArticleCategoryInfo> result = new ArrayList<>();
        
        String sql = """
            SELECT nc.id AS category_id, nc.category_type, nac.news_id, nac.created_at
            FROM news_article_category nac
            JOIN news_category nc ON nac.category_id = nc.id
            WHERE nac.news_id = ?
        """;

        try {
        	PreparedStatement ps = conn.prepareStatement(sql);
        	ps.setInt(1, newsId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                NewsArticleCategoryInfo info = new NewsArticleCategoryInfo(
                    rs.getInt("category_id"),
                    rs.getString("category_type"),
                    rs.getInt("news_id"),
                    rs.getTimestamp("created_at")
                );
                result.add(info);
            }

        } catch (SQLException e) {
            logger.error(e.getStackTrace().toString());
        }

        return result;
    }
    
    @Override
    public boolean hideArticle(int newsId) {
        String sql = "UPDATE news_article SET is_hidden = TRUE WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newsId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error(e.getStackTrace().toString());
            return false;
        }
    }

    @Override
    public boolean unhideArticle(int newsId) {
        String sql = "UPDATE news_article SET is_hidden = FALSE WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newsId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error(e.getStackTrace().toString());
            return false;
        }
    }

    @Override
    public int getReportCount(int newsId) {
        String sql = "SELECT COUNT(*) AS report_count FROM news_article_report WHERE news_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newsId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("report_count");
            }
        } catch (SQLException e) {
            logger.error(e.getStackTrace().toString());
        }
        return 0;
    }

    @FunctionalInterface
    interface PreparedStatementSetter {
        void set(PreparedStatement ps) throws SQLException;
    }
}
