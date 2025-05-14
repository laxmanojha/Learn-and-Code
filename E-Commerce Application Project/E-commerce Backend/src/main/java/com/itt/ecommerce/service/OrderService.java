package com.itt.ecommerce.service;

import java.util.List;

import com.itt.ecommerce.dao.CartDao;
import com.itt.ecommerce.dao.OrderDao;
import com.itt.ecommerce.dao.ProductDao;
import com.itt.ecommerce.dao.UserDao;
import com.itt.ecommerce.dto.CartItemDto;
import com.itt.ecommerce.dto.OrderHistoryDto;

public class OrderService {
	
	private final OrderDao orderDao;
    private final ProductDao productDao;
    private final CartDao cartDao;
    private final UserDao userDao;
    
    public OrderService() {
    	this(new OrderDao(), new ProductDao(), new CartDao(), new UserDao());
    }

    public OrderService(OrderDao orderDao, ProductDao productDao, CartDao cartDao, UserDao userDao) {
        this.orderDao = orderDao;
        this.productDao = productDao;
        this.cartDao = cartDao;
        this.userDao = userDao;
    }
	
	public String completeOrder(List<CartItemDto> cartItems) {
		if (cartItems == null || cartItems.isEmpty())
			return "0:No cart items received to place an order";
		
		int userId = getUserId(cartItems.get(0));
		float totalPrice = getTotalPrice(cartItems);
		int orderId = -1;
		boolean allItemsOrdered = false;
		String message = null;
		
		boolean orderCreated = orderDao.addOrderDetails(userId, totalPrice);
		
		if (orderCreated)
			orderId = orderDao.getOrderIdByUserId(userId);
		else
			message = "0:Order creation failed.";
		
		if (orderId != -1)
			allItemsOrdered = orderDao.addOrderItemDetails(orderId, cartItems);
		
		if (allItemsOrdered) {
			boolean stockQuantityUpdated = productDao.updateProductStockQuantity(cartItems);
			int cartId = cartDao.getCartIDByUserID(userId);
			boolean itemsDeletedFromCart = cartDao.removeAllItemsFromCart(cartId);
			
			if (stockQuantityUpdated && itemsDeletedFromCart)
				message = "1:Item order completion successful.";
			else
				message = "0:Stock quantity is not get updated.";
		}
		return message;
	}

	private int getUserId(CartItemDto cartItem) {
	    int cartId = cartItem.getCartId();
	    return cartDao.getUserIDByCartID(cartId);
	}
	
	private float getTotalPrice(List<CartItemDto> cartItems) {
	    float totalPrice = 0;
	    for (CartItemDto cartItem : cartItems) {
	        totalPrice += cartItem.getProductPrice() * cartItem.getQuantity();
	    }
	    return totalPrice;
	}

	public List<OrderHistoryDto> getOrderHistory(String username) {
	    int userId = userDao.getUserIDByUsername(username);
	    return orderDao.getOrderHistory(userId);
	}
}
