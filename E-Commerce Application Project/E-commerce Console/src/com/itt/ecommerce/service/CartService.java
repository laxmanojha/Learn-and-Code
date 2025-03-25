package com.itt.ecommerce.service;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.itt.ecommerce.dto.CartItemDto;
import com.itt.ecommerce.dto.CategoryDto;
import com.itt.ecommerce.dto.ProductDto;
import com.itt.ecommerce.util.HttpUtil;

public class CartService {
	private static final String BASE_URL = "http://localhost:8080/E-Commerce-Application";
	private static final Gson gson = new Gson();
	
	public static void addToCart(String username, int categoryId, int productId, int quantity) throws IOException, InterruptedException {
	    String url = BASE_URL + "/cart";
	    String formData = "username=" + username + "&productId=" + productId + "&quantity=" + quantity;

	    HttpResponse<String> response = HttpUtil.sendPostRequest(url, formData);
	    HttpUtil.processResponse(response, "Add to Cart");
	}
	
	public static void showCategories(List<CategoryDto> allCategories) {
        allCategories.sort(Comparator.comparingInt(CategoryDto::getCategory_id));

        System.out.println("+------------+--------------------------+");
        System.out.println("| CategoryID | Category Name            |");
        System.out.println("+------------+--------------------------+");

        for (CategoryDto category : allCategories) {
            System.out.printf("| %-10d | %-20s |\n", category.getCategory_id(), category.getCategory_name());
        }

        System.out.println("+------------+--------------------------+");
	}
	
	public static void showProducts(List<ProductDto> allProducts) {
		allProducts.sort(Comparator.comparingInt(ProductDto::getProduct_id));

        System.out.println("+------------+--------------------------+");
        System.out.println("| ProductID | Product Name              |");
        System.out.println("+------------+--------------------------+");

        for (ProductDto product : allProducts) {
            System.out.printf("| %-10d | %-20s |\n", product.getProduct_id(), product.getProduct_name());
        }

        System.out.println("+------------+--------------------------+");
	}
	
	public static void takeCartInputAndAdd(String username) throws IOException, InterruptedException {
	    System.out.println("\nTaking order for " + username + "...");

	    while (true) {
	        // Step 1: Show all categories and get user selection
	        List<CategoryDto> allCategories = CategoryService.getAllCategories();
	        showCategoriesWithBackOption(allCategories);

	        int userChoiceCategoryId = getUserChoiceCategoryIdWithBack();
	        if (userChoiceCategoryId == -1) return; // Go back to main menu

	        while (true) {
	            // Step 2: Show all products from selected category and get user selection
	            List<ProductDto> allProducts = CategoryService.getAllProductsOfCategory(userChoiceCategoryId);
	            showProductsWithBackOption(allProducts);

	            int selectedProductId = getUserChoiceProductIdWithBack(allProducts);
	            if (selectedProductId == -1) break; // Go back to category selection

	            // Step 3: Get quantity for the selected product
	            int productQuantity = getProductQuantityWithBack(selectedProductId, allProducts);
	            if (productQuantity == -1) continue; // Go back to product selection

	            // Step 4: Call the API function with selected values
	            addToCart(username, userChoiceCategoryId, selectedProductId, productQuantity);
	            return; // Successfully added to cart, return to main menu
	        }
	    }
	}

	// Show categories with a "Go Back" option
	private static void showCategoriesWithBackOption(List<CategoryDto> categories) {
	    showCategories(categories);
	    System.out.println("0: Go Back");
	}

	// Show products with a "Go Back" option
	private static void showProductsWithBackOption(List<ProductDto> products) {
	    showProducts(products);
	    System.out.println("0: Go Back");
	}

	// Get user input for category with "Go Back"
	private static int getUserChoiceCategoryIdWithBack() {
	    Scanner scanner = new Scanner(System.in);
	    int categoryId;
	    
	    while (true) {
	        System.out.print("Enter category ID (or 0 to go back): ");
	        String input = scanner.nextLine().trim();

	        try {
	            categoryId = Integer.parseInt(input);

	            if (categoryId == 0) return -1; // Go back
	            if (categoryId >= 1 && categoryId <= 20) return categoryId;

	            System.out.println("Error: Category ID must be between 1 and 20.");
	        } catch (NumberFormatException e) {
	            System.out.println("Error: Invalid input. Enter a valid integer.");
	        }
	    }
	}

	// Get user input for product with "Go Back"
	private static int getUserChoiceProductIdWithBack(List<ProductDto> allProducts) {
	    Scanner scanner = new Scanner(System.in);

	    while (true) {
	        System.out.print("Enter product ID (or 0 to go back): ");
	        String input = scanner.nextLine().trim();

	        try {
	            int productId = Integer.parseInt(input);
	            if (productId == 0) return -1; // Go back to category selection

	            boolean exists = allProducts.stream().anyMatch(p -> p.getProduct_id() == productId);
	            if (exists) return productId;

	            System.out.println("Error: Product ID must be from the displayed list.");
	        } catch (NumberFormatException e) {
	            System.out.println("Error: Invalid input. Enter a valid integer.");
	        }
	    }
	}

	// Get user input for quantity with "Go Back"
	private static int getProductQuantityWithBack(int selectedProductId, List<ProductDto> allProducts) {
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

	    while (true) {
	        System.out.print("Enter quantity (or 0 to go back, Available stock: " + stockQuantity + "): ");
	        String input = scanner.nextLine().trim();

	        try {
	            int quantity = Integer.parseInt(input);
	            if (quantity == 0) return -1; // Go back to product selection

	            if (quantity > 0 && quantity <= stockQuantity) return quantity;

	            System.out.println("Error: Quantity must be between 1 and " + stockQuantity + ".");
	        } catch (NumberFormatException e) {
	            System.out.println("Error: Invalid input. Enter a valid integer.");
	        }
	    }
	}
	
	public static void viewCart(String username) throws IOException, InterruptedException {
	  System.out.println("\nFetching cart for " + username + "...");
	  List<CartItemDto> allCartItems = getCartItems(username);
	  showCartItems(allCartItems);
	}
	
	public static List<CartItemDto> getCartItems(String username) throws IOException, InterruptedException {
		String url = BASE_URL + "/cart?username=" + username;
		HttpResponse<String> response = HttpUtil.sendGetRequest(url);
		return parseCartResponse(response.body());
	}
	
	private static List<CartItemDto> parseCartResponse(String jsonResponse) {
        try {
            JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);

            if (jsonObject.get("success").getAsBoolean()) {
                JsonArray carcartItemsArray = jsonObject.getAsJsonArray("cartItems");
                return gson.fromJson(carcartItemsArray, new TypeToken<List<CartItemDto>>() {}.getType());
            } else {
                System.out.println(jsonObject.get("message").getAsString());
            }
        } catch (Exception e) {
            System.err.println("Failed to parse category response: " + e.getMessage());
        }
        return List.of();
    }
	
	private static void showCartItems(List<CartItemDto> allCartItems) {
	    if (allCartItems == null || allCartItems.isEmpty()) {
	        System.out.println("Your cart is empty.");
	        return;
	    }

	    System.out.printf("| %-10s | %-10s | %-20s | %-10s | %-10s |\n",
	            "Item ID", "Product ID", "Product Name", "Quantity", "Price");

	    for (CartItemDto item : allCartItems) {
	        System.out.printf("| %-10d | %-10d | %-20s | %-10d | %-10.2f |\n",
	                item.getCartItemId(), item.getProductId(), item.getProductName(),
	                item.getQuantity(), item.getProductPrice());
	    }
	}
	
	public static void removeProduct(String username, int productId) throws IOException, InterruptedException {
		String url = BASE_URL + "/cart/" + productId + "?username=" + username;
		
		HttpResponse<String> response = HttpUtil.sendDeleteRequest(url);
		HttpUtil.processResponse(response, "Product removal");
	}
	
	public static void removeAllProduct(String username) throws IOException, InterruptedException {
		String url = BASE_URL + "/cart?username=" + username;
		
		HttpResponse<String> response = HttpUtil.sendDeleteRequest(url);
		HttpUtil.processResponse(response, "All Products removeal");
	}
}