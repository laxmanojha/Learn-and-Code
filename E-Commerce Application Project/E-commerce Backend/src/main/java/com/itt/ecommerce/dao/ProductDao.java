package com.itt.ecommerce.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.itt.ecommerce.dto.CartItemDto;
import com.itt.ecommerce.dto.ProductDto;
import com.itt.ecommerce.util.DatabaseConfig;

public class ProductDao {
	public static List<ProductDto> getAllProductsByCategoryId(int category_id) {
		String query = "SELECT * FROM products WHERE category_id = ?";

		try (Connection con = DatabaseConfig.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(query)) {

			ps.setInt(1, category_id);

			try (ResultSet rs = ps.executeQuery()) {
				List<ProductDto> allProducts = new ArrayList<ProductDto>();
				while (rs.next()) {
					allProducts.add(new ProductDto(rs.getInt("product_id"), rs.getString("product_name"), rs.getInt("category_id"), rs.getFloat("price"), rs.getInt("stock_quantity")));
				}
				return allProducts;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ProductDto getProductById(int productId) {
		String query = "SELECT * FROM products WHERE product_id = ?";

		try (Connection con = DatabaseConfig.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(query)) {

			ps.setInt(1, productId);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return (new ProductDto(rs.getInt("product_id"), rs.getString("product_name"), rs.getInt("category_id"), rs.getFloat("price"), rs.getInt("stock_quantity")));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean updateProductStockQuantity(List<CartItemDto> cartItems) {

        try (Connection con = DatabaseConfig.getInstance().getConnection()) {

            con.setAutoCommit(false);

            String sql = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id = ?;";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                for (CartItemDto cartItem : cartItems) {
                    ps.setInt(1, cartItem.getQuantity());
                    ps.setInt(2, cartItem.getProductId());
                    ps.addBatch();
                }

                int[] results = ps.executeBatch();

                for (int result : results) {
                    if (result <= 0) { 
                        con.rollback();
                        return false;
                    }
                }

                con.commit();
                return true;

            } catch (Exception e) {
                con.rollback();
                e.printStackTrace();
                return false;
            } finally {
                con.setAutoCommit(true);
            }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return false;
	}
}
