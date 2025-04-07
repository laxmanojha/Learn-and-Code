package com.itt.ecommerce.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.itt.ecommerce.dto.CartItemDto;
import com.itt.ecommerce.dto.OrderHistoryDto;
import com.itt.ecommerce.util.DatabaseConfig;

public class OrderDao {
	public static int getOrderIdByUserId(int userId) {
        String query = "SELECT * FROM orders o WHERE user_id = ? ORDER BY order_id DESC;";

        try (Connection con = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("order_id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
	
	public static boolean addOrderDetails(int userId, float totalPrice) {
		String query = "INSERT INTO orders(user_id, total_price) VALUES(?, ?);";

		try (Connection con = DatabaseConfig.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(query)) {

			ps.setInt(1, userId);
			ps.setFloat(2, totalPrice);

			int rowsInserted = ps.executeUpdate();
            
            return rowsInserted > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean addOrderItemDetails(int orderId, List<CartItemDto> cartItems) {

		try (Connection con = DatabaseConfig.getInstance().getConnection()) {

            con.setAutoCommit(false);

            String sql = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?);";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                for (CartItemDto cartItem : cartItems) {
                    ps.setInt(1, orderId);
                    ps.setInt(2, cartItem.getProductId());
                    ps.setInt(3, cartItem.getQuantity());
                    ps.setFloat(4, cartItem.getProductPrice());
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
	
	public static List<OrderHistoryDto> getOrderHistory(int userId) {
        String query = "SELECT *, oi.quantity*oi.price as quantities_total_price FROM orders o " + 
        				"INNER JOIN order_items oi ON o.order_id = oi.order_id " +
        				"INNER JOIN products p ON p.product_id = oi.product_id " +
        				"INNER JOIN categories c ON p.category_id = c.category_id " +
        				"WHERE o.user_id = ?;";

        try (Connection con = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
        	
        	ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
            	List<OrderHistoryDto> allProductHistory = new ArrayList<OrderHistoryDto>();
            	SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.ENGLISH);

            	while (rs.next()) {
            	    Timestamp timestamp = rs.getTimestamp("o.order_date");
            	    String formattedDate = timestamp != null ? dateFormat.format(timestamp) : "N/A";

            	    allProductHistory.add(new OrderHistoryDto(
            	        rs.getString("c.category_name"),
            	        rs.getString("p.product_name"),
            	        rs.getFloat("p.price"),
            	        rs.getInt("oi.quantity"),
            	        rs.getFloat("quantities_total_price"),
            	        formattedDate
            	    ));
            	}
                return allProductHistory;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
	}
}
