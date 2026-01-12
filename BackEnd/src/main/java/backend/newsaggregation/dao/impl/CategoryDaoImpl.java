package backend.newsaggregation.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backend.newsaggregation.dao.interfaces.CategoryDao;
import backend.newsaggregation.model.Category;
import backend.newsaggregation.util.DatabaseConfig;

public class CategoryDaoImpl implements CategoryDao{
	
	private static CategoryDaoImpl instance;
	Connection conn = DatabaseConfig.getConnection();
	private static final Logger logger = LoggerFactory.getLogger(CategoryDaoImpl.class);

    private CategoryDaoImpl() {}

    public static CategoryDaoImpl getInstance() {
        if (instance == null) {
            instance = new CategoryDaoImpl();
        }
        return instance;
    }

    public boolean addCategory(String name) {
        String sql = "INSERT INTO news_category (category_type) VALUES (?)";
        try {
        	Connection conn = DatabaseConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(1, name);
            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            logger.error(e.getStackTrace().toString());
        }
        return false;
    }
    
    @Override
    public List<Category> getAllCategory() {
    	String sql = "select * from news_category";
    	List<Category> categories = new ArrayList<>();
    	try {
    		PreparedStatement ps = conn.prepareStatement(sql);
    		ResultSet rs = ps.executeQuery();
    		while (rs.next()) {
    			categories.add(new Category(rs.getInt("id"), rs.getString("category_type"), rs.getBoolean("is_hidden")));
    		}
    		
    	} catch (SQLException e) {
            logger.error(e.getStackTrace().toString());
    	}
    	return categories;
    }
    
    @Override
    public boolean hideCategory(int categoryId) {
        String sql = "UPDATE news_category SET is_hidden = TRUE WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error(e.getStackTrace().toString());
            return false;
        }
    }

    @Override
    public boolean unhideCategory(int categoryId) {
        String sql = "UPDATE news_category SET is_hidden = FALSE WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error(e.getStackTrace().toString());
            return false;
        }
    }
    
    @Override
    public Set<String> getCategoryTypesForNews(Set<Integer> newsIds) {
        Set<String> categoryTypes = new HashSet<>();

        if (newsIds == null || newsIds.isEmpty()) {
            return categoryTypes;
        }

        String placeholders = newsIds.stream().map(id -> "?").collect(Collectors.joining(", "));
        String sql = """
            SELECT DISTINCT nc.category_type
            FROM news_article_category nac
            JOIN news_category nc ON nac.category_id = nc.id
            WHERE nac.news_id IN (""" + placeholders + ")";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int index = 1;
            for (int id : newsIds) {
                stmt.setInt(index++, id);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                categoryTypes.add(rs.getString("category_type"));
            }

        } catch (SQLException e) {
            logger.error(e.getStackTrace().toString());
        }

        return categoryTypes;
    }
}
