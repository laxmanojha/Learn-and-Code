package com.itt.ecommerce.dto;

public class CartItemDto {
	private int cartItemId;
	private int cartId;
	private int productId;
	private int quantity;
	private String productName;
	private float productPrice;
	
	public CartItemDto() {}
	
	public CartItemDto(int cartItemId, int cartId, int productId, int quantity, String productName, float productPrice) {
		super();
		this.cartItemId = cartItemId;
		this.cartId = cartId;
		this.productId = productId;
		this.quantity = quantity;
		this.productName = productName;
		this.productPrice = productPrice;
	}

	public int getCartItemId() {
		return cartItemId;
	}

	public int getCartId() {
		return cartId;
	}

	public int getProductId() {
		return productId;
	}

	public int getQuantity() {
		return quantity;
	}

	public String getProductName() {
		return productName;
	}

	public float getProductPrice() {
		return productPrice;
	}
}
