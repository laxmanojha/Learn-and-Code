package com.itt.ecommerce.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.itt.ecommerce.dto.UserDto;
import com.itt.ecommerce.util.DatabaseConfig;

public class UserDao {
    public static boolean addUser(UserDto user) {
        String query = "INSERT INTO users (full_name, username, password) VALUES (?, ?, ?)";

        try (Connection con = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getUserName());
            ps.setString(3, user.getPassword());

            int rowsInserted = ps.executeUpdate();
            
            return rowsInserted > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static UserDto getUserBasedOnUserCredentials(UserDto user) {
        String query = "SELECT * FROM users WHERE username = ?";

        try (Connection con = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, user.getUserName());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UserDto(rs.getInt("user_id"), rs.getString("full_name"), rs.getString("username"), rs.getString("password"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static UserDto getUserBasedOnUsername(String userName) {
        String query = "SELECT * FROM users WHERE username = ?;";

        try (Connection con = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, userName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UserDto(rs.getInt("user_id"), rs.getString("full_name"), rs.getString("username"), rs.getString("password"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static int getUserIDByUsername(String userName) {
        String query = "SELECT * FROM users WHERE username = ?;";

        try (Connection con = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, userName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}