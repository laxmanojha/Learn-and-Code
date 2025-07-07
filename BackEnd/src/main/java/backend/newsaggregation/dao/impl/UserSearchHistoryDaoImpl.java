package backend.newsaggregation.dao.impl;

import backend.newsaggregation.dao.interfaces.UserSearchHistoryDao;
import backend.newsaggregation.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserSearchHistoryDaoImpl implements UserSearchHistoryDao {

    private static UserSearchHistoryDaoImpl instance;
    private static Connection conn;
	private static final Logger logger = LoggerFactory.getLogger(UserSearchHistoryDaoImpl.class);

    private UserSearchHistoryDaoImpl() {
        conn = DatabaseConfig.getConnection();
    }

    public static UserSearchHistoryDaoImpl getInstance() {
        if (instance == null) {
            instance = new UserSearchHistoryDaoImpl();
        }
        return instance;
    }

    @Override
    public void logSearch(int userId, String keyword) {
        String sql = """
            INSERT INTO user_search_history (user_id, keyword)
            VALUES (?, ?)
            ON DUPLICATE KEY UPDATE searched_at = CURRENT_TIMESTAMP
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, keyword);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getStackTrace().toString());
        }
    }

    @Override
    public List<String> getRecentSearchKeywords(int userId, int limit) {
        String sql = """
            SELECT keyword FROM user_search_history 
            WHERE user_id = ? 
            ORDER BY searched_at DESC 
            LIMIT ?
        """;

        List<String> keywords = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                keywords.add(rs.getString("keyword"));
            }
        } catch (SQLException e) {
            logger.error(e.getStackTrace().toString());
        }

        return keywords;
    }
}
