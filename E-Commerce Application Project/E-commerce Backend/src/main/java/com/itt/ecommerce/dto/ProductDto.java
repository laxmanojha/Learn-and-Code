package com.itt.ecommerce.dto;

public class ProductDto {
	private int product_id;
	private String product_name;
	private int category_id;
	private float price;
	private int stock_quantity;
	
	public ProductDto(int product_id, String product_name, int category_id, float price, int stock_quantity) {
		super();
		this.product_id = product_id;
		this.product_name = product_name;
		this.category_id = category_id;
		this.price = price;
		this.stock_quantity = stock_quantity;
	}
	
	public int getProduct_id() {
		return product_id;
	}
	public String getProduct_name() {
		return product_name;
	}
	public int getCategory_id() {
		return category_id;
	}
	public float getPrice() {
		return price;
	}
	public int getStock_quantity() {
		return stock_quantity;
	}
}
