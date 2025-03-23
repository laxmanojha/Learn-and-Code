package com.itt.ecommerce.service;

import java.util.List;

import com.itt.ecommerce.dao.CartDao;
import com.itt.ecommerce.dao.UserDao;
import com.itt.ecommerce.dto.CartItemDto;

public class CartService {
	public static List<CartItemDto> fetchAllCartItems(String username) {
		int user_id = UserDao.getUserIDByUsername(username);
		int cart_id = CartDao.getCartIDByUserID(user_id);
		return CartDao.getAllCartItems(cart_id);
	}
}
