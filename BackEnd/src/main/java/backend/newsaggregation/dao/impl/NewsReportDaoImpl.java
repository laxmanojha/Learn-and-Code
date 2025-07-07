package backend.newsaggregation.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backend.newsaggregation.dao.interfaces.NewsReportDao;
import backend.newsaggregation.model.NewsArticleReport;
import backend.newsaggregation.util.DatabaseConfig;

public class NewsReportDaoImpl implements NewsReportDao{

	private static NewsReportDaoImpl instance;
    private static Connection conn;
	private static final Logger logger = LoggerFactory.getLogger(NewsReportDaoImpl.class);

    private NewsReportDaoImpl() {}

    public static NewsReportDaoImpl getInstance() {
        if (instance == null) {
            instance = new NewsReportDaoImpl();
            conn = DatabaseConfig.getConnection();
        }
        return instance;
    }

    @Override
    public boolean reportArticle(int userId, int newsId, String comment) {
        String sql = "INSERT INTO news_article_report (user_id, news_id, reason) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, newsId);
            stmt.setString(3, comment);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            logger.error(e.getStackTrace().toString());
            return false;
        }
    }
    
    @Override
    public List<NewsArticleReport> getReportedArticles() {
        List<NewsArticleReport> reports = new ArrayList<>();
        String sql = "SELECT * FROM news_article_report";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    NewsArticleReport report = new NewsArticleReport(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("news_id"),
                        rs.getString("reason"),
                        rs.getTimestamp("reported_at")
                    );
                    reports.add(report);
                }
            }

        } catch (SQLException e) {
            logger.error(e.getStackTrace().toString());
        }

        return reports;
    }
}

