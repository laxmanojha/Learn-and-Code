package backend.newsaggregation.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import backend.newsaggregation.dao.interfaces.NewsReportDao;
import backend.newsaggregation.util.DatabaseConfig;

public class NewsReportDaoImpl implements NewsReportDao{

	private static NewsReportDaoImpl instance;
    private static Connection conn;

    private NewsReportDaoImpl() {}

    public static NewsReportDaoImpl getInstance() {
        if (instance == null) {
            instance = new NewsReportDaoImpl();
            conn = DatabaseConfig.getConnection();
        }
        return instance;
    }

    @Override
    public boolean reportArticle(int userId, int newsId) {
        String sql = "INSERT INTO news_article_report (user_id, news_id) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, newsId);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

