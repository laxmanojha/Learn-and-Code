package backend.newsaggregation.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import backend.newsaggregation.dao.interfaces.NotificationCategoryPrefDao;
import backend.newsaggregation.model.NotificationPref;
import backend.newsaggregation.util.DatabaseConfig;

public class NotificationCategoryPrefDaoImpl implements NotificationCategoryPrefDao {

    private static NotificationCategoryPrefDaoImpl instance;

    private NotificationCategoryPrefDaoImpl() {}

    public static NotificationCategoryPrefDaoImpl getInstance() {
        if (instance == null) {
            instance = new NotificationCategoryPrefDaoImpl();
        }
        return instance;
    }

    @Override
    public List<NotificationPref> getPreferencesByUser(int userId) {
        List<NotificationPref> prefs = new ArrayList<>();

        String sql = "SELECT * FROM notification_category_pref WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                NotificationPref pref = new NotificationPref();
                pref.setUserId(rs.getInt("user_id"));
                pref.setCategoryId(rs.getInt("category_id"));
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
    public boolean updateCategoryPreference(int userId, int categoryId, boolean enabled) {
        String sql = """
            UPDATE notification_category_pref (user_id, category_id, is_enabled)
            SET is_enabled = ?
            WHERE user_id = ? AND category_id = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

        	stmt.setBoolean(1, enabled);
            stmt.setInt(2, userId);
            stmt.setInt(3, categoryId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
