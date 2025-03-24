package com.itt.ecommerce.service;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import com.itt.ecommerce.dto.CategoryDto;
import com.itt.ecommerce.dto.ProductDto;
import com.itt.ecommerce.util.HttpUtil;

public class OrderService {
	private static final String BASE_URL = "http://localhost:8080/E-Commerce-Application";
	
	public static void orderProduct(String username) throws IOException, InterruptedException {
		HttpResponse<String> response = null;
		do {
			System.out.println("\nTaking order for " + username + "...");
			List<CategoryDto> allCategories = CategoryService.getAllCategories();
			showCategories(allCategories);
			
			int userChoiceCategoryId = getUserChoiceCategoryId();
			List<ProductDto> allProducts = CategoryService.getAllProductsOfCategory(userChoiceCategoryId);
			showProducts(allProducts);
			
			int selectedProductId = getUserChoiceProductId(allProducts);
			int productQuantity = getProductQuantity(selectedProductId, allProducts);
			
			String url = BASE_URL + "/cart";
	        String formData = "username=" + username + "&productId=" + selectedProductId + "&quantity=" + productQuantity;
	
	        response = HttpUtil.sendPostRequest(url, formData);
		} while(!HttpUtil.processResponse(response, "Add to cart"));
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

        System.out.println("+------------+--------------------------+");
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
	
	private static int getUserChoiceProductId(List<ProductDto> allProducts) {
	    Scanner scanner = new Scanner(System.in);
	    
	    while (true) {
	        System.out.print("Please enter product ID (shown above) to continue shopping: ");
	        String input = scanner.nextLine().trim();

	        try {
	            final int enteredProductId = Integer.parseInt(input);

	            boolean exists = allProducts.stream().anyMatch(p -> p.getProduct_id() == enteredProductId);

	            if (exists) { 
	                return enteredProductId;
	            } else {
	                System.out.println("Error: Product ID must be from the displayed list. Please try again.");
	            }
	        } catch (NumberFormatException e) {
	            System.out.println("Error: Invalid input. Please enter a valid integer ID.");
	        }
	    }
	}
	
	private static int getProductQuantity(int selectedProductId, List<ProductDto> allProducts) {
	    Scanner scanner = new Scanner(System.in);

	    ProductDto selectedProduct = allProducts.stream()
	            .filter(p -> p.getProduct_id() == selectedProductId)
	            .findFirst()
	            .orElse(null);

	    if (selectedProduct == null) {
	        System.out.println("Error: Selected product not found.");
	        return -1;
	    }

	    int stockQuantity = selectedProduct.getStock_quantity();
	    int quantity = 0;

	    while (true) {
	        System.out.print("Enter quantity (Available stock: " + stockQuantity + "): ");
	        String input = scanner.nextLine().trim();

	        try {
	            quantity = Integer.parseInt(input);

	            if (quantity > 0 && quantity <= stockQuantity) {
	                break;
	            } else {
	                System.out.println("Error: Quantity must be between 1 and " + stockQuantity + ". Try again.");
	            }
	        } catch (NumberFormatException e) {
	            System.out.println("Error: Invalid input. Please enter a valid integer quantity.");
	        }
	    }

	    return quantity;
	}

	
	public static void viewOrderHistory(String username) {
		System.out.println("\nFetching order history for " + username + "...");
	}
}