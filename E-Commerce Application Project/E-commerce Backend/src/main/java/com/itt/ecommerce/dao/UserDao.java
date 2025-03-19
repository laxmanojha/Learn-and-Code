package com.itt.ecommerce.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.itt.ecommerce.dto.UserDto;

public class UserDao {
    public static boolean addUser(UserDto user) {
        String url = "jdbc:mysql://localhost:3306/employee";
        String username = "root";
        String password = "Rj@1465887732";
        String query = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection con = DriverManager.getConnection(url, username, password);
                 PreparedStatement ps = con.prepareStatement(query)) {

                ps.setString(1, user.getName());
                ps.setString(2, user.getEmail());
                ps.setString(3, user.getPassword());

                int rowsInserted = ps.executeUpdate();
                
                return rowsInserted > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static UserDto getUser(UserDto user) {
        String url = "jdbc:mysql://localhost:3306/employee";
        String username = "root";
        String dbPassword = "Rj@1465887732";
        String query = "SELECT * FROM users WHERE email = ? AND password = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection(url, username, dbPassword);
                 PreparedStatement ps = con.prepareStatement(query)) {

                ps.setString(1, user.getEmail());
                ps.setString(2, user.getPassword());

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new UserDto(rs.getInt("user_id"), rs.getString("username"), rs.getString("email"), rs.getString("password"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}