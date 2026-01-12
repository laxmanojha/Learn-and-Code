package backend.newsaggregation.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backend.newsaggregation.dao.interfaces.HiddenKeywordDao;
import backend.newsaggregation.model.HiddenKeyword;
import backend.newsaggregation.util.DatabaseConfig;

public class HiddenKeywordDaoImpl implements HiddenKeywordDao {
	private static HiddenKeywordDaoImpl instance;
    private static Connection conn;
	private static final Logger logger = LoggerFactory.getLogger(HiddenKeywordDaoImpl.class);

    private HiddenKeywordDaoImpl() {}

    public static HiddenKeywordDaoImpl getInstance() {
        if (instance == null) {
            instance = new HiddenKeywordDaoImpl();
            conn = DatabaseConfig.getConnection();
        }
        return instance;
    }

    @Override
    public boolean addKeyword(String keyword) {
        String sql = "INSERT INTO hidden_keywords (keyword) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, keyword);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) {
                System.err.println("Keyword already exists.");
            } else {
                logger.error(e.getStackTrace().toString());
            }
            return false;
        }
    }

    @Override
    public boolean deleteKeyword(int id) {
        String sql = "DELETE FROM hidden_keywords WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error(e.getStackTrace().toString());
            return false;
        }
    }

    @Override
    public List<HiddenKeyword> getAllKeywords() {
        String sql = "SELECT * FROM hidden_keywords";
        List<HiddenKeyword> keywords = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
            	HiddenKeyword hiddenKeyword = new HiddenKeyword();
            	hiddenKeyword.setId(rs.getInt("id"));
                hiddenKeyword.setKeyword(rs.getString("keyword"));
                hiddenKeyword.setCreatedAt(rs.getDate("created_at"));
                keywords.add(hiddenKeyword);
            }
        } catch (SQLException e) {
            logger.error(e.getStackTrace().toString());
        }
        return keywords;
    }
}
