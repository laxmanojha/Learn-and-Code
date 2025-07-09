package frontend.newsaggregation.console.menu;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import frontend.newsaggregation.constant.StaticConfiguration;
import frontend.newsaggregation.model.Category;
import frontend.newsaggregation.service.AdminCategoryService;
import frontend.newsaggregation.util.HttpUtil;
import frontend.newsaggregation.util.InputUtil;

public class CategoryMenu {

	private static final AdminCategoryService adminCategoryService = AdminCategoryService.getInstance();

	public static void handleAddingNewCategory() {
		List<Category> categories = fetchCategories();
		displayCategoryMenu(categories);
        String categoryName = InputUtil.readLine("Enter new news category name: ");

        if (categoryName.trim().isEmpty()) {
            System.out.println("Category name cannot be empty.");
        } else {
        	adminCategoryService.addCategory(categoryName.trim());
        }
    }
	
	private static List<Category> fetchCategories() {
        String url = StaticConfiguration.getBaseUrl() + "/news/category";
        try {
            HttpResponse<String> response = HttpUtil.sendGetRequest(url);
            if (response.statusCode() == 200) {
                Type listType = new TypeToken<List<Category>>() {}.getType();
                return new Gson().fromJson(response.body(), listType);
            } else {
                System.out.println("Failed to fetch categories.");
            }
        } catch (Exception e) {
            System.out.println("Error fetching categories: " + e.getMessage());
        }
        return new ArrayList<>();
    }

	private static void displayCategoryMenu(List<Category> categories) {
	    System.out.println("\nList of Categories:");
	    System.out.printf("%-5s %-30s%n", "No.", "Category Name");
	    System.out.println("--------------------------------------------------");

	    for (int i = 0; i < categories.size(); i++) {
	        String name = capitalize(categories.get(i).getName());
	        System.out.printf("%-5d %-30s%n", i + 1, name);
	    }
	}
    
    private static String capitalize(String word) {
        if (word == null || word.isEmpty()) return word;
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }
}
