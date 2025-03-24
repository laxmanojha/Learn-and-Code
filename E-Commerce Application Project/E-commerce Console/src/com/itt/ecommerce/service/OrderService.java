package com.itt.ecommerce.service;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import com.itt.ecommerce.dto.CategoryDto;
import com.itt.ecommerce.dto.ProductDto;

public class OrderService {
	private static final String BASE_URL = "http://localhost:8080/E-Commerce-Application";
	
	public static void orderProduct(String username) throws IOException, InterruptedException {
		System.out.println("\nTaking order for " + username + "...");
		List<CategoryDto> allCategories = CategoryService.getAllCategories();
		showCategories(allCategories);
		
		int userChoiceCategoryId = getUserChoiceCategoryId();
		List<ProductDto> allProducts = CategoryService.getAllProductsOfCategory(userChoiceCategoryId);
		showProducts(allProducts);
	}
	
	private static void showCategories(List<CategoryDto> allCategories) {
        allCategories.sort(Comparator.comparingInt(CategoryDto::getCategory_id));

        System.out.println("+------------+--------------------------+");
        System.out.println("| CategoryID | Category Name            |");
        System.out.println("+------------+--------------------------+");

        for (CategoryDto category : allCategories) {
            System.out.printf("| %-10d | %-20s |\n", category.getCategory_id(), category.getCategory_name());
        }

        System.out.println("+------------+--------------------------+");
	}
	
	private static void showProducts(List<ProductDto> allProducts) {
		allProducts.sort(Comparator.comparingInt(ProductDto::getProduct_id));

        System.out.println("+------------+--------------------------+");
        System.out.println("| ProductID | Product Name              |");
        System.out.println("+------------+--------------------------+");

        for (ProductDto product : allProducts) {
            System.out.printf("| %-10d | %-20s |\n", product.getProduct_id(), product.getProduct_name());
        }

        System.out.println("+------------+----------------------+");
	}
	
	private static int getUserChoiceCategoryId() {
	    Scanner scanner = new Scanner(System.in);
	    int categoryId = -1;
	    
	    while (true) {
	        System.out.print("Please enter category ID (1-20) to continue shopping: ");
	        String input = scanner.nextLine().trim();

	        try {
	            categoryId = Integer.parseInt(input);

	            if (categoryId >= 1 && categoryId <= 20) { 
	                break;
	            } else {
	                System.out.println("Error: Category ID must be between 1 and 20. Please try again.");
	            }
	        } catch (NumberFormatException e) {
	            System.out.println("Error: Invalid input. Please enter a valid integer between 1 and 20.");
	        }
	    }

	    return categoryId;
	}
	
	public static void viewOrderHistory(String username) {
		System.out.println("\nFetching order history for " + username + "...");
	}
}