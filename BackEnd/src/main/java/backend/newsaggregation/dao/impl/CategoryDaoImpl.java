package backend.newsaggregation.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import backend.newsaggregation.dao.interfaces.CategoryDao;
import backend.newsaggregation.util.DatabaseConfig;

public class CategoryDaoImpl implements CategoryDao{
	private static CategoryDaoImpl instance;

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
}
