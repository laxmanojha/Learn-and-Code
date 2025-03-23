package com.itt.ecommerce.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.itt.ecommerce.dto.CartItemDto;

public class CartDao {
	public static int getCartIDByUserID(int userId) {
        String url = "jdbc:mysql://localhost:3306/ecommerce_application";
        String username = "root";
        String dbPassword = "Rj@1465887732";
        String query = "SELECT * FROM cart WHERE user_id = ?;";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection(url, username, dbPassword);
                 PreparedStatement ps = con.prepareStatement(query)) {

                ps.setInt(1, userId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("cart_id");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
	
	public static int getUserIDByCartID(int cartId) {
        String url = "jdbc:mysql://localhost:3306/ecommerce_application";
        String username = "root";
        String dbPassword = "Rj@1465887732";
        String query = "SELECT * FROM cart WHERE cart_id = ?;";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection(url, username, dbPassword);
                 PreparedStatement ps = con.prepareStatement(query)) {

                ps.setInt(1, cartId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("user_id");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

	public static boolean addUserIdToCart(int userId) {
		String url = "jdbc:mysql://localhost:3306/ecommerce_application";
        String username = "root";
        String dbPassword = "Rj@1465887732";
        String query = "INSERT INTO cart(user_id) VALUES(?);";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection(url, username, dbPassword);
                 PreparedStatement ps = con.prepareStatement(query)) {

                ps.setInt(1, userId);

                int rowsInserted = ps.executeUpdate();
                
                return rowsInserted > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
	}
	
	public static List<CartItemDto> getAllCartItems(int cartId) {
		String url = "jdbc:mysql://localhost:3306/ecommerce_application";
		String username = "root";
		String dbPassword = "Rj@1465887732";
		String query = "SELECT * FROM cart_items c INNER JOIN products p ON c.product_id = p.product_id WHERE c.cart_id = ?";

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			try (Connection con = DriverManager.getConnection(url, username, dbPassword);
					PreparedStatement ps = con.prepareStatement(query)) {

				ps.setInt(1, cartId);

				try (ResultSet rs = ps.executeQuery()) {
					List<CartItemDto> allCartItems = new ArrayList<CartItemDto>();
					while (rs.next()) {
						allCartItems.add(new CartItemDto(rs.getInt("c.cart_item_id"), rs.getInt("c.cart_id"), rs.getInt("p.product_id"), rs.getInt("c.quantity"), rs.getString("p.product_name"), rs.getFloat("p.price")));
					}
					return allCartItems;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean addItemToCart(int cartId, int productId, int quantity) {
		String url = "jdbc:mysql://localhost:3306/ecommerce_application";
		String username = "root";
		String dbPassword = "Rj@1465887732";
		String query = "INSERT INTO cart_items(cart_id, product_id, quantity) VALUES(?, ?, ?);";

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			try (Connection con = DriverManager.getConnection(url, username, dbPassword);
					PreparedStatement ps = con.prepareStatement(query)) {

				ps.setInt(1, cartId);
				ps.setInt(2, productId);
				ps.setInt(3, quantity);

				int rowsInserted = ps.executeUpdate();
                
                return rowsInserted > 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean removeItemsFromCart(int cartId) {
		String url = "jdbc:mysql://localhost:3306/ecommerce_application";
		String username = "root";
		String dbPassword = "Rj@1465887732";
		String query = "DELETE FROM cart WHERE cart_id = ?;";

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			try (Connection con = DriverManager.getConnection(url, username, dbPassword);
					PreparedStatement ps = con.prepareStatement(query)) {

				ps.setInt(1, cartId);

				int rowsAffected = ps.executeUpdate();
                
                return rowsAffected > 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
