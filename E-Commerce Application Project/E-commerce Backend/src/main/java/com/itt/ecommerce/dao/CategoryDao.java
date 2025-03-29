package com.itt.ecommerce.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.itt.ecommerce.dto.CategoryDto;

public class CategoryDao {
	public static ArrayList<CategoryDto> getAllCategories() {
		String url = "jdbc:mysql://localhost:3306/ecommerce_application";
        String username = "root";
        String dbPassword = "Rj@1465887732";
        String query = "SELECT * FROM categories";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection(url, username, dbPassword);
                 PreparedStatement ps = con.prepareStatement(query)) {

                try (ResultSet rs = ps.executeQuery()) {
                	ArrayList<CategoryDto> allCategories = new ArrayList<CategoryDto>();
                    while (rs.next()) {
                        allCategories.add(new CategoryDto(rs.getInt("category_id"), rs.getString("category_name")));
                    }
                    return allCategories;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
	}
}
