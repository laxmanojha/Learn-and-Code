package backend.newsaggregation.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import backend.newsaggregation.dao.interfaces.CategoryDao;
import backend.newsaggregation.model.Category;
import backend.newsaggregation.util.DatabaseConfig;

public class CategoryDaoImpl implements CategoryDao{
	private static CategoryDaoImpl instance;
	Connection conn = DatabaseConfig.getConnection();

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
            e.printStackTrace();
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
    			categories.add(new Category(rs.getInt("id"), rs.getString("category_type")));
    		}
    		
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    	return categories;
    }
}
