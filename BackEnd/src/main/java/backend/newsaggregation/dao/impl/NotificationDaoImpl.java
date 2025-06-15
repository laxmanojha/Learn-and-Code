package backend.newsaggregation.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import backend.newsaggregation.dao.interfaces.NotificationDao;
import backend.newsaggregation.model.NotificationPref;
import backend.newsaggregation.util.DatabaseConfig;

public class NotificationDaoImpl implements NotificationDao {

    private static NotificationDaoImpl instance;

    private NotificationDaoImpl() {}

    public static NotificationDaoImpl getInstance() {
        if (instance == null) {
            instance = new NotificationDaoImpl();
        }
        return instance;
    }

    @Override
    public List<NotificationPref> getPreferencesByUser(int userId) {
        List<NotificationPref> prefs = new ArrayList<>();

        String sql = "SELECT * FROM notification_pref WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                NotificationPref pref = new NotificationPref();
                pref.setUserId(rs.getInt("user_id"));
                pref.setKeywords(rs.getString("keywords"));
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
    public boolean updateCategoryPreference(int userId, String category, boolean enabled) {
        String sql = """
            INSERT INTO notification_pref (user_id, keyword, enabled)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE enabled = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, category);
            stmt.setBoolean(3, enabled);
            stmt.setBoolean(4, enabled);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean updateKeywordPreference(int userId, String keyword, boolean enabled) {
        String sql = """
            UPDATE notification_pref SET enabled = ?
            WHERE user_id = ? AND keyword = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, enabled);
            stmt.setInt(2, userId);
            stmt.setString(3, keyword);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean addKeywordPreference(int userId, String keyword) {
        String sql = """
            INSERT INTO notification_pref (user_id, keyword, enabled)
            VALUES (?, ?, true)
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, keyword);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                // already exists
                return true;
            }
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean removeKeywordPreference(int userId, String keyword) {
        String sql = """
            DELETE FROM notification_pref WHERE user_id = ? AND keyword = ?
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
}
