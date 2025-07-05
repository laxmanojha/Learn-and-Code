package frontend.newsaggregation.console.dashboard;

import java.util.List;

import frontend.newsaggregation.model.Category;
import frontend.newsaggregation.model.ExternalServer;
import frontend.newsaggregation.model.User;
import frontend.newsaggregation.service.AdminCategoryService;
import frontend.newsaggregation.service.AuthService;
import frontend.newsaggregation.service.CategoryService;
import frontend.newsaggregation.service.ExternalServerService;
import frontend.newsaggregation.util.AppState;
import frontend.newsaggregation.util.InputUtil;

public class AdminDashboard {
	
	private static final AuthService authService = AuthService.getInstance();
	private static final ExternalServerService serverService = ExternalServerService.getInstance();
	private static final AdminCategoryService adminCategoryService = AdminCategoryService.getInstance();

    public static void startAdminMenu(User user) {
        while (true) {
        	if (AppState.shouldExitToHome()) {
                AppState.reset();
                return;
            }
            System.out.println("\nWelcome " + user.getUsername() + "! Please choose an option:");
            System.out.println("1. View the list of external servers and status");
            System.out.println("2. View the external server’s details");
            System.out.println("3. Update/Edit the external server’s details");
            System.out.println("4. Add new News Category");
            System.out.println("5. Manage Category Visibility (Hide/Unhide)");
            System.out.println("6. Logout");

            String choice = InputUtil.readLine("Enter your choice: ");

            switch (choice) {
                case "1":
                    handleExternalServerWithStatus();
                    break;
                case "2":
                    handleExternalServerDetails();
                    break;

                case "3":
                    updateExternalServerDetails();
                    break;

                case "4":
                    handleAddingNewCategory();
                    break;

                case "5":
                    handleCategoryVisibility();
                    break;

                case "6":
                	handleLogout();
                    AppState.setExitToHome(true);
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    private static void handleExternalServerWithStatus() {
    	List<ExternalServer> servers = serverService.getAllServers();

        if (servers.isEmpty()) {
            System.out.println("No external servers found.");
        } else {
            System.out.println("\nList of external servers:");
            int count = 1;
            for (ExternalServer server : servers) {
                String status = serverService.formatStatus(server.getServerStatus());
                String lastAccessed = serverService.formatDate(server.getLastAccessed());
                System.out.println(count++ + ". " + server.getServerName() + " - " + status + " - last accessed: " + lastAccessed);
            }
        }
    }
    
    private static void handleExternalServerDetails() {
    	List<ExternalServer> serverDetails = serverService.getServerDetails();

        if (serverDetails.isEmpty()) {
            System.out.println("No external server details found.");
        } else {
            System.out.println("\nList of external server details:");
            int count = 1;
            for (ExternalServer server : serverDetails) {
                System.out.println(count++ + ". " + server.getServerName() + " - " + server.getApiKey());
            }
        }
    }
    
    private static void updateExternalServerDetails() {
    	ExternalServerService updateService = new ExternalServerService();

        String idInput = InputUtil.readLine("Enter the external server ID: ");
        int serverId;
        try {
            serverId = Integer.parseInt(idInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid server ID.");
            return;
        }

        String updatedKey = InputUtil.readLine("Enter the updated API key: ");

        boolean updated = updateService.updateApiKey(serverId, updatedKey);
        if (updated) {
            System.out.println("External server API key updated successfully.");
        } else {
            System.out.println("Failed to update the external server.");
        }
    }
    
    private static void handleAddingNewCategory() {
    	CategoryService categoryService = new CategoryService();
        String categoryName = InputUtil.readLine("Enter new news category name: ");

        if (categoryName.trim().isEmpty()) {
            System.out.println("Category name cannot be empty.");
        } else {
            boolean added = categoryService.addCategory(categoryName.trim());
            if (added) {
                System.out.println("Category added successfully.");
            } else {
                System.out.println("Failed to add category.");
            }
        }
    }
    
    private static void handleCategoryVisibility() {
        List<Category> categories = adminCategoryService.fetchAllCategories();

        if (categories.isEmpty()) {
            System.out.println("No categories found.");
            return;
        }

        System.out.println("\nCategory Visibility:");
        for (Category cat : categories) {
            System.out.printf("ID: %d | %s [%s]%n",
                cat.getId(),
                cat.getName(),
                cat.getIsHidden() ? "Hidden" : "Visible"
            );
        }

        String input = InputUtil.readLine("\nEnter category ID to toggle visibility or 'back' to return: ");
        if (input.equalsIgnoreCase("back")) {
            return;
        }

        try {
            int catId = Integer.parseInt(input);
            Category selected = categories.stream()
                    .filter(c -> c.getId() == catId)
                    .findFirst()
                    .orElse(null);

            if (selected == null) {
                System.out.println("Invalid category ID.");
                return;
            }

            boolean success;
            if (selected.getIsHidden()) {
                success = adminCategoryService.unhideCategory(catId);
            } else {
                success = adminCategoryService.hideCategory(catId);
            }

            if (success) {
                System.out.println("Category visibility updated successfully.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
    
    private static void handleLogout() {
    	boolean loggedOut = authService.logout();
    	if (loggedOut) {
    	    System.out.println("Logged out successfully.");
    	} else {
    	    System.out.println("Logout failed.");
    	}
    	return;
    }
}
