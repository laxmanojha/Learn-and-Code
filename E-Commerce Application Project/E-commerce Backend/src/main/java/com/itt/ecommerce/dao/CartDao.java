package com.itt.ecommerce.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.itt.ecommerce.dto.CartItemDto;
import com.itt.ecommerce.util.DatabaseConfig;

public class CartDao {
	public static int getCartIDByUserID(int userId) {
        String query = "SELECT * FROM cart WHERE user_id = ?;";

        try (Connection con = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cart_id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
	
	public static int getUserIDByCartID(int cartId) {
        String query = "SELECT * FROM cart WHERE cart_id = ?;";

        try (Connection con = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, cartId);

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

	public static boolean addUserIdToCart(int userId) {
        String query = "INSERT INTO cart(user_id) VALUES(?);";

        try (Connection con = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, userId);

            int rowsInserted = ps.executeUpdate();
            
            return rowsInserted > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
	}
	
	public static List<CartItemDto> getAllCartItems(int cartId) {
		String query = "SELECT * FROM cart_items c INNER JOIN products p ON c.product_id = p.product_id WHERE c.cart_id = ?";

		try (Connection con = DatabaseConfig.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(query)) {

			ps.setInt(1, cartId);

			try (ResultSet rs = ps.executeQuery()) {
				List<CartItemDto> allCartItems = new ArrayList<CartItemDto>();
				while (rs.next()) {
					allCartItems.add(new CartItemDto(rs.getInt("c.cart_item_id"), rs.getInt("c.cart_id"), rs.getInt("p.product_id"), rs.getInt("c.quantity"), rs.getString("p.product_name"), rs.getFloat("p.price")));
				}
				return allCartItems;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean addItemToCart(int cartId, int productId, int quantity) {
		String query = "INSERT INTO cart_items(cart_id, product_id, quantity) VALUES(?, ?, ?);";

		try (Connection con = DatabaseConfig.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(query)) {

			ps.setInt(1, cartId);
			ps.setInt(2, productId);
			ps.setInt(3, quantity);

			int rowsInserted = ps.executeUpdate();
            
            return rowsInserted > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean removeItemFromCart(int cartId, int productId) {
		String query = "DELETE FROM cart_items WHERE cart_id = ? AND product_id = ?;";

		try (Connection con = DatabaseConfig.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(query)) {

			ps.setInt(1, cartId);
			ps.setInt(2, productId);

			int rowsAffected = ps.executeUpdate();
            
            return rowsAffected > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean updateItemInCart(int quantity, int cartId, int productId) {
		String query = "UPDATE cart_items SET quantity = ? WHERE cart_id = ? AND product_id = ?;";

		try (Connection con = DatabaseConfig.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(query)) {

			ps.setInt(1, quantity);
			ps.setInt(2, cartId);
			ps.setInt(3, productId);

			int rowsAffected = ps.executeUpdate();
            
            return rowsAffected > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean removeAllItemsFromCart(int cartId) {
		String query = "DELETE FROM cart WHERE cart_id = ?;";

		try (Connection con = DatabaseConfig.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(query)) {

			ps.setInt(1, cartId);

			int rowsAffected = ps.executeUpdate();
            
            return rowsAffected > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
