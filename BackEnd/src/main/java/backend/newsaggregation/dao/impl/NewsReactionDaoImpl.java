package backend.newsaggregation.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import backend.newsaggregation.dao.interfaces.NewsReactionDao;
import backend.newsaggregation.util.DatabaseConfig;

public class NewsReactionDaoImpl implements NewsReactionDao{

	private static NewsReactionDaoImpl instance;
    private static Connection conn;

    private NewsReactionDaoImpl() {}

    public static NewsReactionDaoImpl getInstance() {
        if (instance == null) {
            instance = new NewsReactionDaoImpl();
            conn = DatabaseConfig.getConnection();
        }
        return instance;
    }

    @Override
    public boolean reactToArticle(int userId, int newsId, String reactionType) {
        String sql = "INSERT INTO news_article_reaction (user_id, news_id, reaction_type) " +
                     "VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE reaction_type = ?, reacted_at = CURRENT_TIMESTAMP";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, newsId);
            stmt.setString(3, reactionType);
            stmt.setString(4, reactionType);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
