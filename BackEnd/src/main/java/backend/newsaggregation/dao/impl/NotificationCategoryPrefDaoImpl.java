package backend.newsaggregation.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import backend.newsaggregation.dao.interfaces.NotificationCategoryPrefDao;
import backend.newsaggregation.model.NotificationPreference;
import backend.newsaggregation.util.DatabaseConfig;

public class NotificationCategoryPrefDaoImpl implements NotificationCategoryPrefDao {

    private static NotificationCategoryPrefDaoImpl instance;
    Connection conn = DatabaseConfig.getConnection();

    private NotificationCategoryPrefDaoImpl() {}

    public static NotificationCategoryPrefDaoImpl getInstance() {
        if (instance == null) {
            instance = new NotificationCategoryPrefDaoImpl();
        }
        return instance;
    }

    @Override
    public List<NotificationPreference> getCategoryPreferencesByUser(int userId) {
        List<NotificationPreference> prefs = new ArrayList<>();

        String sql = "SELECT * FROM notification_category_pref ncp INNER JOIN news_category nc ON "
        		+ "ncp.category_id = nc.id WHERE user_id = ?";

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                NotificationPreference pref = new NotificationPreference();
                pref.setUserId(rs.getInt("user_id"));
                pref.setCategoryId(rs.getInt("category_id"));
                pref.setEnabled(rs.getBoolean("is_enabled"));
                pref.setCreatedAt(rs.getDate("created_at"));
                pref.setCategoryType(rs.getString("category_type"));
                pref.setKeyword(rs.getString("keyword"));
                prefs.add(pref);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return prefs;
    }
    
    @Override
    public boolean addCategoryPreference(int userId, int categoryId, List<String> keywords) {
        String sql = """
            INSERT INTO notification_category_pref (user_id, category_id, keyword, is_enabled)
            VALUES (?, ?, ?, true)
        """;

        try {
    		PreparedStatement stmt = conn.prepareStatement(sql);

            conn.setAutoCommit(false);

            for (String keyword : keywords) {
                stmt.setInt(1, userId);
                stmt.setInt(2, categoryId);
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
    public boolean updateCategoryPreference(int userId, int categoryId, boolean enabled) {
        String sql = """
            UPDATE notification_category_pref
            SET is_enabled = ?
            WHERE user_id = ? AND category_id = ?
        """;

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);

        	stmt.setBoolean(1, enabled);
            stmt.setInt(2, userId);
            stmt.setInt(3, categoryId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    
    @Override
    public boolean removeCategoryPreference(int userId, int categoryId) {
    	String sql = """
            DELETE FROM notification_category_pref WHERE user_id = ? and category_id = ?
        """;
    	
    	try {
    		PreparedStatement stmt = conn.prepareStatement(sql);
    		
    		stmt.setInt(1, userId);
    		stmt.setInt(2, categoryId);
    		
    		return stmt.executeUpdate() > 0;
    		
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    	
    	return false;
    }
}
