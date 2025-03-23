package com.itt.ecommerce.service;

import java.io.IOException;
import java.util.List;

import com.itt.ecommerce.dto.CategoryDto;

public class OrderService {
	
	public static void orderProduct(String username) throws IOException, InterruptedException {
		System.out.println("\nTaking order for " + username + "...");
		List<CategoryDto> allCategories = CategoryService.getAllCategories();
		showCategories(allCategories);
	}
	
	private static void showCategories(List<CategoryDto> allCategories) {
		for (CategoryDto category: allCategories) {
			System.out.println(category.getCategory_id() + " " + category.getCategory_name());
		}
	}
	
	public static void viewOrderHistory(String username) {
		System.out.println("\nFetching order history for " + username + "...");
	}
}