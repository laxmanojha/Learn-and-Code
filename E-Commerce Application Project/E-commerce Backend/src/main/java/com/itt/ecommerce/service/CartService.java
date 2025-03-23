package com.itt.ecommerce.service;

import java.util.List;

import com.itt.ecommerce.dao.CartDao;
import com.itt.ecommerce.dao.ProductDao;
import com.itt.ecommerce.dao.UserDao;
import com.itt.ecommerce.dto.CartItemDto;
import com.itt.ecommerce.dto.ProductDto;

public class CartService {
	public static List<CartItemDto> fetchAllCartItems(String username) {
		int userId = UserDao.getUserIDByUsername(username);
		int cartId = CartDao.getCartIDByUserID(userId);
		return CartDao.getAllCartItems(cartId);
	}
	
	public static String addToCart(String username, int productId, int quantity) {
		ProductDto product = ProductDao.getProductById(productId);
		String message = null;
		
		if (product == null)
			return "0:Entered product id is not valid";
		
		if (product.getStock_quantity() < quantity) {
			message = "0:Quantity can not be more than available stock quantity";
		}
		
		int cartId = getCartId(username);
		if (cartId == -1) {
			message = "0:Unable to create cart for " + username; 
		}
		
		boolean itemAddedToCart = CartDao.addItemToCart(cartId, productId, quantity);
		if (itemAddedToCart)
			message = "1:" + product.getProduct_name() + " has been added to your cart successfully.";
		else
			message = "0:Failed in adding " + product.getProduct_name() + " to cart.";
		
		return message;
	}
	
	private static int getCartId(String username) {
		int userId = UserDao.getUserIDByUsername(username);
		int cartId = CartDao.getCartIDByUserID(userId);
		
		if (cartId == -1) {
			if (CartDao.addUserIdToCart(userId)) {
				return CartDao.getCartIDByUserID(userId);
			}
		}
		return cartId;
	}
}
