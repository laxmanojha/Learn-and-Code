package backend.newsaggregation.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewsDaoImpl implements NewsDao {

    private static NewsDaoImpl instance;

    private NewsDaoImpl() {}

    public static NewsDaoImpl getInstance() {
        if (instance == null) {
            instance = new NewsDaoImpl();
        }
        return instance;
    }

    @Override
    public List<News> getNewsByDate(Date date) {
        String sql = "SELECT * FROM news WHERE DATE(published_at) = ?";
        return getNewsList(sql, new java.sql.Date(date.getTime()), null, null);
    }

    @Override
    public List<News> getNewsByDateAndCategory(Date date, String category) {
        String sql = "SELECT * FROM news WHERE DATE(published_at) = ? AND category = ?";
        return getNewsList(sql, new java.sql.Date(date.getTime()), category, null);
    }

    @Override
    public List<News> getNewsByDateRange(Date startDate, Date endDate) {
        String sql = "SELECT * FROM news WHERE DATE(published_at) BETWEEN ? AND ?";
        return getNewsList(sql, null, null, new java.sql.Date[]{new java.sql.Date(startDate.getTime()), new java.sql.Date(endDate.getTime())});
    }

    @Override
    public List<News> getNewsByDateRangeAndCategory(Date startDate, Date endDate, String category) {
        String sql = "SELECT * FROM news WHERE DATE(published_at) BETWEEN ? AND ? AND category = ?";
        return getNewsList(sql, null, category, new java.sql.Date[]{new java.sql.Date(startDate.getTime()), new java.sql.Date(endDate.getTime())});
    }

    @Override
    public News getNewsById(int id) {
        String sql = "SELECT * FROM news WHERE id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractNewsFromResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<News> getNewsList(String sql, java.sql.Date date, String category, java.sql.Date[] range) {
        List<News> newsList = new ArrayList<>();

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (range != null && range.length == 2) {
                stmt.setDate(1, range[0]);
                stmt.setDate(2, range[1]);
                if (category != null) stmt.setString(3, category);
            } else {
                stmt.setDate(1, date);
                if (category != null) stmt.setString(2, category);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                newsList.add(extractNewsFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return newsList;
    }

    private News extractNewsFromResultSet(ResultSet rs) throws SQLException {
        News news = new News();
        news.setId(rs.getInt("id"));
        news.setTitle(rs.getString("title"));
        news.setDescription(rs.getString("description"));
        news.setUrl(rs.getString("url"));
        news.setSource(rs.getString("source"));
        news.setCategory(rs.getString("category"));
        news.setPublishedAt(rs.getTimestamp("published_at"));
        news.setLikes(rs.getInt("likes"));
        news.setDislikes(rs.getInt("dislikes"));
        return news;
    }
}
