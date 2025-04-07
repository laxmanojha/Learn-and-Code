package com.itt.ecommerce.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.itt.ecommerce.dto.CategoryDto;
import com.itt.ecommerce.util.DatabaseConfig;

public class CategoryDao {
	public static ArrayList<CategoryDto> getAllCategories() {
        String query = "SELECT * FROM categories";

        try (Connection con = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            try (ResultSet rs = ps.executeQuery()) {
            	ArrayList<CategoryDto> allCategories = new ArrayList<CategoryDto>();
                while (rs.next()) {
                    allCategories.add(new CategoryDto(rs.getInt("category_id"), rs.getString("category_name")));
                }
                return allCategories;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
	}
}
