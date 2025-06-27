package backend.newsaggregation.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Date;

import backend.newsaggregation.dao.interfaces.UserDao;
import backend.newsaggregation.model.User;
import backend.newsaggregation.util.DatabaseConfig;

public class UserDaoImpl implements UserDao {

    private static UserDaoImpl instance;
    private static Connection conn = DatabaseConfig.getConnection();

    private UserDaoImpl() {}

    public static UserDaoImpl getInstance() {
        if (instance == null) {
            instance = new UserDaoImpl();
        }
        return instance;
    }

    @Override
    public User getUserByUsername(String username) {
        User user = null;
        String sql = "SELECT * FROM user INNER JOIN user_role ON user.role_id = user_role.id WHERE username = ? ";
        try {
        	Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                user = new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("email"),
                    rs.getInt("role_id"),
                    rs.getString("type"),
                    rs.getDate("notification_viewed_at")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
    
    @Override
    public User getUserByEmail(String email) {
    	User user = null;
    	String sql = "SELECT * FROM user INNER JOIN user_role ON user.role_id = user_role.id WHERE email = ? ";
    	try {
    		Connection conn = DatabaseConfig.getConnection();
    		PreparedStatement stmt = conn.prepareStatement(sql);
    		stmt.setString(1, email);
    		ResultSet rs = stmt.executeQuery();
    		
    		if (rs.next()) {
    			user = new User(
    					rs.getInt("id"),
    					rs.getString("username"),
    					rs.getString("password"),
    					rs.getString("email"),
    					rs.getInt("role_id"),
    					rs.getString("type"),
    					rs.getDate("notification_viewed_at")
    					);
    		}
    		
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    	return user;
    }

    @Override
    public boolean saveUser(User user) {
        String sql = "INSERT INTO user (username, password, email, role_id) VALUES (?, ?, ?, ?)";
        try {
        	Connection conn = DatabaseConfig.getConnection();
        	PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setInt(4, user.getRoleId());

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public Timestamp getNotificationViewedTime(int userId) {
        String sql = "SELECT notification_viewed_at FROM user WHERE id = ?";
        
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getTimestamp("notification_viewed_at");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    @Override
    public boolean saveNotificationViewedTime(int userId, Timestamp time) {
        String sql = "UPDATE user SET notification_viewed_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, new java.sql.Timestamp(time.getTime()));
            stmt.setInt(2, userId);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
