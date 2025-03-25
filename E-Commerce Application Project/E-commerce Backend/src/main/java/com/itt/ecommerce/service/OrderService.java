package com.itt.ecommerce.service;

import java.util.List;

import com.itt.ecommerce.dao.CartDao;
import com.itt.ecommerce.dao.OrderDao;
import com.itt.ecommerce.dao.ProductDao;
import com.itt.ecommerce.dao.UserDao;
import com.itt.ecommerce.dto.CartItemDto;
import com.itt.ecommerce.dto.OrderHistoryDto;

public class OrderService {
	
	public static String completeOrder(List<CartItemDto> cartItems) {
		if (cartItems == null)
			return "0:No cart items received to place an order";
		
		int userId = getUserId(cartItems.get(0));
		float totalPrice = getTotalPrice(cartItems);
		int orderId = -1;
		boolean allItemsOrdered = false;
		String message = null;
		
		boolean orderCreated = OrderDao.addOrderDetails(userId, totalPrice);
		
		if (orderCreated)
			orderId = OrderDao.getOrderIdByUserId(userId);
		else
			message = "0:Order creation failed.";
		
		if (orderId != -1)
			allItemsOrdered = OrderDao.addOrderItemDetails(orderId, cartItems);
		
		if (allItemsOrdered) {
			boolean stockQuantityUpdated = ProductDao.updateProductStockQuantity(cartItems);
			int cartId = CartDao.getCartIDByUserID(userId);
			boolean itemsDeletedFromCart = CartDao.removeAllItemsFromCart(cartId);
			
			if (stockQuantityUpdated && itemsDeletedFromCart)
				message = "1:Item order completion successful.";
			else
				message = "0:Stock quantity is not get updated.";
		}
		return message;
	}

	private static int getUserId(CartItemDto cartItem) {
	    int cartId = cartItem.getCartId();
	    return CartDao.getUserIDByCartID(cartId);
	}
	
	private static float getTotalPrice(List<CartItemDto> cartItems) {
	    float totalPrice = 0;
	    for (CartItemDto cartItem : cartItems) {
	        totalPrice += cartItem.getProductPrice() * cartItem.getQuantity();
	    }
	    return totalPrice;
	}

	public static List<OrderHistoryDto> getOrderHistory(String username) {
	    int userId = UserDao.getUserIDByUsername(username);
	    return OrderDao.getOrderHistory(userId);
	}
}
