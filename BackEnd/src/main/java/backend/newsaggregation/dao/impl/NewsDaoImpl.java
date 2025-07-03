package backend.newsaggregation.dao.impl;

import java.sql.Connection;
import java.sql.Statement;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import backend.newsaggregation.dao.interfaces.NewsDao;
import backend.newsaggregation.model.Category;
import backend.newsaggregation.model.NewsArticle;
import backend.newsaggregation.model.NewsArticleCategoryInfo;
import backend.newsaggregation.model.NewsArticle;
import backend.newsaggregation.util.DatabaseConfig;

public class NewsDaoImpl implements NewsDao {

    private static NewsDaoImpl instance;
    private static Connection conn;

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
            """ + condition;
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

        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
                e.printStackTrace();
                return false;
            }
        }
    }

    @Override
    public List<NewsArticle> getNewsByDate(Date date) {
        String sql = baseQuery("WHERE DATE(na.published_at) = ?");
        return getNewsList(sql, ps -> ps.setDate(1, new Date(date.getTime())));
    }

    @Override
    public List<NewsArticle> getNewsByDateAndCategory(Date date, String category) {
        String sql = baseQuery("WHERE DATE(na.published_at) = ? AND nc.category_type = ?");
        return getNewsList(sql, ps -> {
            ps.setDate(1, new Date(date.getTime()));
            ps.setString(2, category);
        });
    }

    @Override
    public List<NewsArticle> getNewsByDateRange(Date startDate, Date endDate) {
        String sql = baseQuery("WHERE na.published_at BETWEEN ? AND ?");
        return getNewsList(sql, ps -> {
            ps.setDate(1, new Date(startDate.getTime()));
            ps.setDate(2, new Date(endDate.getTime()));
        });
    }

    @Override
    public List<NewsArticle> getNewsByDateRangeAndCategory(Date startDate, Date endDate, String category) {
        String sql = baseQuery("WHERE na.published_at BETWEEN ? AND ? AND nc.category_type = ?");
        return getNewsList(sql, ps -> {
            ps.setDate(1, new Date(startDate.getTime()));
            ps.setDate(2, new Date(endDate.getTime()));
            ps.setString(3, category);
        });
    }

    @Override
    public NewsArticle getNewsById(int id) {
        String sql = baseQuery("WHERE na.id = ?");
        try {
             PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? mapRowToArticle(rs) : null;

        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }

        return result;
    }

    @FunctionalInterface
    interface PreparedStatementSetter {
        void set(PreparedStatement ps) throws SQLException;
    }
}
