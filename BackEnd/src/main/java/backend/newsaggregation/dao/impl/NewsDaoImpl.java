package backend.newsaggregation.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import backend.newsaggregation.dao.interfaces.NewsDao;
import backend.newsaggregation.model.NewsArticle;
import backend.newsaggregation.util.DatabaseConfig;

import java.util.*;

public class NewsDaoImpl implements NewsDao {

    private static NewsDaoImpl instance;

    private NewsDaoImpl() {}

    public static synchronized NewsDaoImpl getInstance() {
        if (instance == null) {
            instance = new NewsDaoImpl();
        }
        return instance;
    }

    private NewsArticle mapRowToArticle(ResultSet rs) throws SQLException {
        return new NewsArticle(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("description"),
            rs.getString("source"),
            rs.getString("url"),
            rs.getInt("category_id"),
            rs.getTimestamp("published_date")
        );
    }

    private String baseQuery(String condition) {
        return """
            SELECT na.*, nac.news_category_id AS category_id
            FROM news_article na
            LEFT JOIN news_article_category nac ON na.id = nac.news_article_id
            LEFT JOIN news_category nc ON nac.news_category_id = nc.id
            """ + condition;
    }

    @Override
    public List<NewsArticle> getNewsByDate(Date date) {
        String sql = baseQuery("WHERE DATE(na.published_date) = ?");
        return getNewsList(sql, ps -> ps.setDate(1, new java.sql.Date(date.getTime())));
    }

    @Override
    public List<NewsArticle> getNewsByDateAndCategory(Date date, String category) {
        String sql = baseQuery("WHERE DATE(na.published_date) = ? AND nc.category_type = ?");
        return getNewsList(sql, ps -> {
            ps.setDate(1, new java.sql.Date(date.getTime()));
            ps.setString(2, category);
        });
    }

    @Override
    public List<NewsArticle> getNewsByDateRange(Date startDate, Date endDate) {
        String sql = baseQuery("WHERE na.published_date BETWEEN ? AND ?");
        return getNewsList(sql, ps -> {
            ps.setDate(1, new java.sql.Date(startDate.getTime()));
            ps.setDate(2, new java.sql.Date(endDate.getTime()));
        });
    }

    @Override
    public List<NewsArticle> getNewsByDateRangeAndCategory(Date startDate, Date endDate, String category) {
        String sql = baseQuery("WHERE na.published_date BETWEEN ? AND ? AND nc.category_type = ?");
        return getNewsList(sql, ps -> {
            ps.setDate(1, new java.sql.Date(startDate.getTime()));
            ps.setDate(2, new java.sql.Date(endDate.getTime()));
            ps.setString(3, category);
        });
    }

    @Override
    public NewsArticle getNewsById(int id) {
        String sql = baseQuery("WHERE na.id = ?");
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? mapRowToArticle(rs) : null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<NewsArticle> getNewsList(String sql, PreparedStatementSetter setter) {
        List<NewsArticle> articles = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setter.set(ps);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                articles.add(mapRowToArticle(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return articles;
    }

    @FunctionalInterface
    interface PreparedStatementSetter {
        void set(PreparedStatement ps) throws SQLException;
    }
}

