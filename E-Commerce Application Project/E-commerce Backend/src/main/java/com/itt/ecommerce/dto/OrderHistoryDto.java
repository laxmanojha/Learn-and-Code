package com.itt.ecommerce.dto;


public class OrderHistoryDto {
	private String productCategory;
	private String productName;
	private float productPrice;
	private int productQuantity;
	private float totalProductsPrice;
	private String orderDate;
	
	public OrderHistoryDto() {}
	
	public OrderHistoryDto(String productCategory, String productName, float productPrice, int productQuantity,
			float totalProductsPrice, String orderDate) {
		super();
		this.productCategory = productCategory;
		this.productName = productName;
		this.productPrice = productPrice;
		this.productQuantity = productQuantity;
		this.totalProductsPrice = totalProductsPrice;
		this.orderDate = orderDate;
	}
	
	public String getProductCategory() {
		return productCategory;
	}
	public String getProductName() {
		return productName;
	}
	public float getProductPrice() {
		return productPrice;
	}
	public int getProductQuantity() {
		return productQuantity;
	}
	public float getTotalProductsPrice() {
		return totalProductsPrice;
	}
	public String getOrderDate() {
		return orderDate;
	}
}
