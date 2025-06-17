package backend.newsaggregation.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import backend.newsaggregation.dao.interfaces.NotificationCategoryPrefDao;
import backend.newsaggregation.dao.interfaces.NotificationKeywordPrefDao;
import backend.newsaggregation.model.NotificationPref;
import backend.newsaggregation.util.DatabaseConfig;

public class NotificationKeywordPrefDaoImpl implements NotificationKeywordPrefDao {

    private static NotificationKeywordPrefDaoImpl instance;

    private NotificationKeywordPrefDaoImpl() {}

    public static NotificationKeywordPrefDaoImpl getInstance() {
        if (instance == null) {
            instance = new NotificationKeywordPrefDaoImpl();
        }
        return instance;
    }

    @Override
    public List<NotificationPref> getPreferencesByUser(int userId) {
        List<NotificationPref> prefs = new ArrayList<>();

        String sql = "SELECT * FROM notification_keyword_pref WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                NotificationPref pref = new NotificationPref();
                pref.setUserId(rs.getInt("user_id"));
                pref.setKeywords(rs.getString("keyword"));
                pref.setEnabled(rs.getBoolean("is_enabled"));
                pref.setCreated_at(rs.getDate("created_at"));
                prefs.add(pref);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return prefs;
    }

    @Override
    public boolean updateKeywordPreference(int userId, List<String> keywords, boolean enabled) {
        String sql = """
            UPDATE notification_keyword_pref SET enabled = ?
            WHERE user_id = ? AND keyword = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (String keyword : keywords) {
                stmt.setBoolean(1, enabled);
                stmt.setInt(2, userId);
                stmt.setString(3, keyword);
                stmt.addBatch();
            }

            int[] results = stmt.executeBatch();

            for (int result : results) {
                if (result == Statement.EXECUTE_FAILED) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            conn.setAutoCommit(true);
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try (Connection rollbackConn = DatabaseConfig.getConnection()) {
                if (rollbackConn != null && !rollbackConn.getAutoCommit()) {
                    rollbackConn.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }

        return false;
    }

    
    @Override
    public boolean updateAllKeywordPreference(int userId, boolean enabled) {
    	String sql = """
            UPDATE notification_keyword_pref SET is_enabled = ?
            WHERE user_id = ?
        """;
    	
    	try (Connection conn = DatabaseConfig.getConnection();
    			PreparedStatement stmt = conn.prepareStatement(sql)) {
    		
    		stmt.setBoolean(1, enabled);
    		stmt.setInt(2, userId);
    		
    		return stmt.executeUpdate() > 0;
    		
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    	
    	return false;
    }

    @Override
    public boolean addKeywordPreference(int userId, List<String> keywords) {
        String sql = """
            INSERT INTO notification_pref (user_id, keyword, is_enabled)
            VALUES (?, ?, true)
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (String keyword : keywords) {
                stmt.setInt(1, userId);
                stmt.setString(2, keyword);
                stmt.addBatch();
            }

            int[] results = stmt.executeBatch();

            for (int result : results) {
                if (result == Statement.EXECUTE_FAILED) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            conn.setAutoCommit(true);
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try (Connection conn = DatabaseConfig.getConnection()) {
                if (conn != null && !conn.getAutoCommit()) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }

        return false;
    }

    @Override
    public boolean removeKeywordPreference(int userId, String keyword) {
        String sql = """
            DELETE FROM notification_keyword_pref WHERE user_id = ? AND keyword = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, keyword);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    
    @Override
    public boolean removeAllKeywordPreference(int userId) {
    	String sql = """
            DELETE FROM notification_keyword_pref WHERE user_id = ?
        """;
    	
    	try (Connection conn = DatabaseConfig.getConnection();
    			PreparedStatement stmt = conn.prepareStatement(sql)) {
    		
    		stmt.setInt(1, userId);
    		return stmt.executeUpdate() > 0;
    		
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    	
    	return false;
    }
}
