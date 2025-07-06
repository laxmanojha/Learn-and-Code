package backend.newsaggregation.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.cj.protocol.Resultset;

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
    
    @Override
    public List<Integer> getLikedNewsIds(int userId) {
    	String sql = "SELECT news_id FROM news_article_reaction WHERE user_id = ? AND reaction_type = 'like'";
    	List<Integer> newsIds = new ArrayList<>();
    	
    	try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    		stmt.setInt(1, userId);
    		ResultSet rs = stmt.executeQuery();
    		while (rs.next()) {
    			newsIds.add(rs.getInt("news_id"));
    		}
    		
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    	return newsIds;
    }
}
