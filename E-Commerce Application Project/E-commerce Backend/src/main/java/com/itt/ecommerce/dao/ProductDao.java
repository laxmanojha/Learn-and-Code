package com.itt.ecommerce.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.itt.ecommerce.dto.ProductDto;

public class ProductDao {
	public static List<ProductDto> getAllProductsByCategoryId(int category_id) {
		String url = "jdbc:mysql://localhost:3306/ecommerce_application";
		String username = "root";
		String dbPassword = "Rj@1465887732";
		String query = "SELECT * FROM products WHERE category_id = ?";

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			try (Connection con = DriverManager.getConnection(url, username, dbPassword);
					PreparedStatement ps = con.prepareStatement(query)) {

				ps.setInt(1, category_id);

				try (ResultSet rs = ps.executeQuery()) {
					List<ProductDto> allProducts = new ArrayList<ProductDto>();
					while (rs.next()) {
						allProducts.add(new ProductDto(rs.getInt("product_id"), rs.getString("product_name"), rs.getInt("category_id"), rs.getFloat("price"), rs.getInt("stock_quantity")));
					}
					return allProducts;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ProductDto getProductById(int productId) {
		String url = "jdbc:mysql://localhost:3306/ecommerce_application";
		String username = "root";
		String dbPassword = "Rj@1465887732";
		String query = "SELECT * FROM products WHERE product_id = ?";

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			try (Connection con = DriverManager.getConnection(url, username, dbPassword);
					PreparedStatement ps = con.prepareStatement(query)) {

				ps.setInt(1, productId);

				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						return (new ProductDto(rs.getInt("product_id"), rs.getString("product_name"), rs.getInt("category_id"), rs.getFloat("price"), rs.getInt("stock_quantity")));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
