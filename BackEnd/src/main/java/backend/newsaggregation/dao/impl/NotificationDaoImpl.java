package backend.newsaggregation.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
    public List<NotificationPreference> getPreferencesByUser(int userId) {
        List<NotificationPreference> prefs = new ArrayList<>();

        String sql = "SELECT * FROM notification_preferences WHERE user_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                NotificationPreference pref = new NotificationPreference();
                pref.setUserId(rs.getInt("user_id"));
                pref.setCategory(rs.getString("category"));
                pref.setKeyword(rs.getString("keyword"));
                pref.setEnabled(rs.getBoolean("enabled"));
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
            INSERT INTO notification_preferences (user_id, category, enabled)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE enabled = ?
        """;

        try (Connection conn = DBConnectionUtil.getConnection();
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
            UPDATE notification_preferences SET enabled = ?
            WHERE user_id = ? AND keyword = ?
        """;

        try (Connection conn = DBConnectionUtil.getConnection();
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
            INSERT INTO notification_preferences (user_id, keyword, enabled)
            VALUES (?, ?, true)
        """;

        try (Connection conn = DBConnectionUtil.getConnection();
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
            DELETE FROM notification_preferences WHERE user_id = ? AND keyword = ?
        """;

        try (Connection conn = DBConnectionUtil.getConnection();
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
