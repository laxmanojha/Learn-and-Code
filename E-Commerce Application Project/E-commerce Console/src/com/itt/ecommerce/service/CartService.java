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
    private static final Scanner sc = new Scanner(System.in);
	
	public static void addToCart(String username, int categoryId, int productId, int quantity) throws IOException, InterruptedException {
	    String url = BASE_URL + "/cart";
	    String formData = "username=" + username + "&productId=" + productId + "&quantity=" + quantity;

	    HttpResponse<String> response = HttpUtil.sendPostRequest(url, formData);
	    HttpUtil.processResponse(response, "Add to Cart");
	}
	
	public static void showCategories(List<CategoryDto> allCategories) {
        allCategories.sort(Comparator.comparingInt(CategoryDto::getCategory_id));

        System.out.println("+------------+--------------------------+");
        System.out.printf("| %-10s | %-24s |\n","CategoryID", "Category Name");
        System.out.println("+------------+--------------------------+");

        for (CategoryDto category : allCategories) {
            System.out.printf("| %-10d | %-24s |\n", category.getCategory_id(), category.getCategory_name());
        }

        System.out.println("+------------+--------------------------+");
	}
	
	public static void showProducts(List<ProductDto> allProducts) {
	    allProducts.sort(Comparator.comparingInt(ProductDto::getProduct_id));

	    System.out.println("+------------+--------------------------+--------------+----------------+");
	    System.out.println("| ProductID  | Product Name             | Price        | Stock Quantity |");
	    System.out.println("+------------+--------------------------+--------------+----------------+");

	    for (ProductDto product : allProducts) {
	        System.out.printf("| %-10d | %-24s | %-12.2f | %-14d |\n", 
	            product.getProduct_id(), 
	            product.getProduct_name(), 
	            product.getPrice(), 
	            product.getStock_quantity()
	        );
	    }

	    System.out.println("+------------+--------------------------+--------------+----------------+");
	}
	
	public static void addToCart(String username) throws IOException, InterruptedException {

	    while (true) {
	        List<CategoryDto> allCategories = CategoryService.getAllCategories();
	        showCategoriesWithBackOption(allCategories);

	        int userChoiceCategoryId = getUserChoiceCategoryIdWithBack();
	        if (userChoiceCategoryId == -1) return;

	        while (true) {
	            List<ProductDto> allProducts = CategoryService.getAllProductsOfCategory(userChoiceCategoryId);
	            showProductsWithBackOption(allProducts);

	            int selectedProductId = getUserChoiceProductIdWithBack(allProducts);
	            if (selectedProductId == -1) break;

	            int productQuantity = getProductQuantityWithBack(selectedProductId, allProducts);
	            if (productQuantity == -1) continue;

	            addToCart(username, userChoiceCategoryId, selectedProductId, productQuantity);
	            return;
	        }
	    }
	}

	private static void showCategoriesWithBackOption(List<CategoryDto> categories) {
	    showCategories(categories);
	    System.out.println("0: Go Back");
	}

	private static void showProductsWithBackOption(List<ProductDto> products) {
	    showProducts(products);
	    System.out.println("0: Go Back");
	}

	private static int getUserChoiceCategoryIdWithBack() {
	    Scanner scanner = new Scanner(System.in);
	    int categoryId;
	    
	    while (true) {
	        System.out.print("Enter category ID (or 0 to go back): ");
	        String input = scanner.nextLine().trim();

	        try {
	            categoryId = Integer.parseInt(input);

	            if (categoryId == 0) return -1;
	            if (categoryId >= 1 && categoryId <= 20) return categoryId;

	            System.out.println("Error: Category ID must be between 1 and 20.");
	        } catch (NumberFormatException e) {
	            System.out.println("Error: Invalid input. Enter a valid integer.");
	        }
	    }
	}

	private static int getUserChoiceProductIdWithBack(List<ProductDto> allProducts) {
	    Scanner scanner = new Scanner(System.in);

	    while (true) {
	        System.out.print("Enter product ID (or 0 to go back): ");
	        String input = scanner.nextLine().trim();

	        try {
	            int productId = Integer.parseInt(input);
	            if (productId == 0) return -1;

	            boolean exists = allProducts.stream().anyMatch(p -> p.getProduct_id() == productId);
	            if (exists) return productId;

	            System.out.println("Error: Product ID must be from the displayed list.");
	        } catch (NumberFormatException e) {
	            System.out.println("Error: Invalid input. Enter a valid integer.");
	        }
	    }
	}

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
	            if (quantity == 0) return -1;

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
	    System.out.println("+------------+----------------------+------------+------------+-----------------+");
	    System.out.printf("| %-10s | %-20s | %-10s | %-10s | %-15s |\n",
	            "Product ID", "Product Name", "Price", "Quantity", "Total Amount");
	    System.out.println("+------------+----------------------+------------+------------+-----------------+");
	    for (CartItemDto item : allCartItems) {
	        System.out.printf("| %-10d | %-20s | %-10.2f | %-10d | %-15.2f |\n",
	                item.getProductId(), item.getProductName(),
	                item.getProductPrice(), item.getQuantity(), (item.getProductPrice() * item.getQuantity()));
	    }
	    System.out.println("+------------+----------------------+------------+------------+-----------------+");
	}
	
	public static void editCart(String username) throws IOException, InterruptedException {
	    int choice;
	    do {
	    	viewCart(username);
	        System.out.println("\n===== Edit Cart Menu =====");
	        System.out.println("1: Edit product quantity");
	        System.out.println("2: Delete a specific product from the cart");
	        System.out.println("3: Delete all products from the cart");
	        System.out.println("4: Go back to the main menu");
	        System.out.print("Enter your choice: ");

	        choice = getValidIntegerInput();

	        switch (choice) {
	            case 1 -> editProductQuantity(username);
	            case 2 -> removeSpecificProduct(username);
	            case 3 -> removeAllProducts(username);
	            case 4 -> System.out.println("Returning to the main menu...");
	            default -> System.out.println("Invalid choice. Please try again.");
	        }
	    } while (choice != 4);
	}

	private static void editProductQuantity(String username) throws IOException, InterruptedException {
	    System.out.print("Enter the Product ID (or 0 to cancel): ");
	    int productId = getValidIntegerInput();
	    
	    if (productId == 0) {
	        System.out.println("Operation canceled. Returning to the previous menu...");
	        return;
	    }

	    System.out.print("Enter the new quantity (0 to remove product): ");
	    int quantity = getValidIntegerInput();

	    if (quantity == 0) {
	        System.out.println("Removing product from cart...");
	        CartService.removeProduct(username, productId);
	    } else {
	        System.out.println("Updating cart...");
	        CartService.updateCart(username, productId, quantity);
	    }
	}

	private static void removeSpecificProduct(String username) throws IOException, InterruptedException {
	    System.out.print("Enter the Product ID to remove (or 0 to cancel): ");
	    int productId = getValidIntegerInput();

	    if (productId == 0) {
	        System.out.println("Operation canceled.");
	        return;
	    }

	    CartService.removeProduct(username, productId);
	}

	private static void removeAllProducts(String username) throws IOException, InterruptedException {
	    System.out.println("Are you sure you want to delete all products from the cart? (yes/no)");
	    String confirm = sc.next();

	    if (confirm.equalsIgnoreCase("yes")) {
	        CartService.removeAllProduct(username);
	    } else {
	        System.out.println("Operation canceled.");
	    }
	}

	private static int getValidIntegerInput() {
	    while (true) {
	        try {
	            String input = sc.nextLine().trim();
	            return Integer.parseInt(input);
	        } catch (NumberFormatException e) {
	            System.out.print("Invalid input. Please enter a valid number: ");
	        }
	    }
	}
	
	public static void updateCart(String username, int productId, int quantity) throws IOException, InterruptedException {
	    String url = BASE_URL + "/cart/update";
	    String requestBody = "{\"username\": \"" + username + "\", \"productId\": " + productId + ", \"quantity\": " + quantity + "}";

	    HttpResponse<String> response = HttpUtil.sendPutRequest(url, requestBody);
	    HttpUtil.processResponse(response, "Cart update");
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