package com.itt.ecommerce.service;

import java.util.List;
import com.itt.ecommerce.dao.CartDao;
import com.itt.ecommerce.dao.ProductDao;
import com.itt.ecommerce.dao.UserDao;
import com.itt.ecommerce.dto.CartItemDto;
import com.itt.ecommerce.dto.ProductDto;

public class CartService {
	
    private final ProductDao productDao;
    private final CartDao cartDao;
    private final UserDao userDao;
    
    public CartService() {
    	this(new ProductDao(), new CartDao(), new UserDao());
    }

    public CartService(ProductDao productDao, CartDao cartDao, UserDao userDao) {
        this.productDao = productDao;
        this.cartDao = cartDao;
        this.userDao = userDao;
    }
	
	public List<CartItemDto> fetchAllCartItems(String username) {
		int userId = userDao.getUserIDByUsername(username);
		int cartId = cartDao.getCartIDByUserID(userId);
		return cartDao.getAllCartItems(cartId);
	}
	
	public String addToCart(String username, int productId, int quantity) {
		ProductDto product = productDao.getProductById(productId);
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
		
		boolean itemAddedToCart = cartDao.addItemToCart(cartId, productId, quantity);
		if (itemAddedToCart)
			message = "1:" + product.getProduct_name() + " has been added to your cart successfully.";
		else
			message = "0:Failed in adding " + product.getProduct_name() + " to cart.";
		
		return message;
	}
	
	public String removeProduct(String username, int productId) {
		String message = null;
		int cartId = getCartId(username);
		boolean result = cartDao.removeItemFromCart(cartId, productId);
		
		if(result) {
			message = "1:Product with ID " + productId + " successfully removed.";
		} else {
			message = "0:Failed in removing Product with ID " + productId;
		}
		return message;
	}
	
	public String removeAllProduct(String username) {
		String message = null;
		int cartId = getCartId(username);
		boolean result = cartDao.removeAllItemsFromCart(cartId);
		
		if(result) {
			message = "1:All products are successfully removed.";
		} else {
			message = "0:Failed in removing all products from cart.";
		}
		return message;
	}
	
	public String updateCart(String username, int productId, int quantity) {
		String message = null;
		int cartId = getCartId(username);
		boolean result = cartDao.updateItemInCart(quantity, cartId, productId);
		
		if(result) {
			message = "1:Product quanitty successfully updated.";
		} else {
			message = "0:Failed in updating product quantity.";
		}
		return message;
	}
	
	private int getCartId(String username) {
		int userId = userDao.getUserIDByUsername(username);
		int cartId = cartDao.getCartIDByUserID(userId);
		
		if (cartId == -1) {
			if (cartDao.addUserIdToCart(userId)) {
				return cartDao.getCartIDByUserID(userId);
			}
		}
		return cartId;
	}
	
}
