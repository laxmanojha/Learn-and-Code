package frontend.newsaggregation.console.dashboard;

import java.time.LocalDateTime;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;
import java.util.List;

import frontend.newsaggregation.model.Category;
import frontend.newsaggregation.model.ExternalServer;
import frontend.newsaggregation.model.HiddenKeyword;
import frontend.newsaggregation.model.NewsArticleReport;
import frontend.newsaggregation.model.User;
import frontend.newsaggregation.service.AdminCategoryService;
import frontend.newsaggregation.service.AdminKeywordService;
import frontend.newsaggregation.service.AdminNewsService;
import frontend.newsaggregation.service.AuthService;
import frontend.newsaggregation.service.CategoryService;
import frontend.newsaggregation.service.ExternalServerService;
import frontend.newsaggregation.util.AppState;
import frontend.newsaggregation.util.InputUtil;

public class AdminDashboard {
	
	private static final AuthService authService = AuthService.getInstance();
	private static final ExternalServerService serverService = ExternalServerService.getInstance();
	private static final AdminCategoryService adminCategoryService = AdminCategoryService.getInstance();
	private static final AdminNewsService adminNewsService = AdminNewsService.getInstance();
	private static final AdminKeywordService adminKeywordService = AdminKeywordService.getInstance();

    public static void startAdminMenu(User user) {
        while (true) {
        	if (AppState.shouldExitToHome()) {
                AppState.reset();
                return;
            }
        	showWelcomeMessage(user);
            System.out.println("1. View the list of external servers and status");
            System.out.println("2. View the external server’s details");
            System.out.println("3. Update/Edit the external server’s details");
            System.out.println("4. Add new News Category");
            System.out.println("5. Manage Category Visibility (Hide/Unhide)");
            System.out.println("6. Manage Reported Articles");
            System.out.println("7. Manage Keyword Filters");
            System.out.println("8. Logout");

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
                    manageReportedArticles();
                    break;
                    
                case "7":
                    manageKeywordFilters();
                    break;

                case "8":
                	authService.logout();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void showWelcomeMessage(User user) {
    	DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mma");

        LocalDateTime now = LocalDateTime.now();
        String date = now.format(dateFormatter);
        String time = now.format(timeFormatter);

        System.out.println("\nWelcome to the News Application, " + user.getUsername() + "!");
        System.out.println("Date: " + date);
        System.out.println("Time: " + time);
    }
    
    private static void handleExternalServerWithStatus() {
    	List<ExternalServer> servers = serverService.getAllServers();

        if (servers.isEmpty()) {
            System.out.println("No external servers found.");
        } else {
            System.out.println("\nList of external servers:");
            System.out.printf("%-5s %-20s %-10s %-20s%n", "No.", "Server Name", "Status", "Last Accessed");
            System.out.println("---------------------------------------------------------------");

            int count = 1;
            for (ExternalServer server : servers) {
                String status = serverService.formatStatus(server.getServerStatus());
                String lastAccessed = serverService.formatDate(server.getLastAccessed());
                System.out.printf("%-5d %-20s %-10s %-20s%n", count++, server.getServerName(), status, lastAccessed);
            }
        }
    }
    
    private static void handleExternalServerDetails() {
    	List<ExternalServer> serverDetails = serverService.getServerDetails();

        if (serverDetails.isEmpty()) {
            System.out.println("No external server details found.");
        } else {
            System.out.println("\nList of external server details:");
            System.out.printf("%-5s %-20s %-50s%n", "No.", "Server Name", "API Key");
            System.out.println("--------------------------------------------------------------------------------");

            int count = 1;
            for (ExternalServer server : serverDetails) {
                System.out.printf("%-5d %-20s %-50s%n", count++, server.getServerName(), server.getApiKey());
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

        updateService.updateApiKey(serverId, updatedKey);
    }
    
    private static void handleAddingNewCategory() {
    	CategoryService categoryService = new CategoryService();
        String categoryName = InputUtil.readLine("Enter new news category name: ");

        if (categoryName.trim().isEmpty()) {
            System.out.println("Category name cannot be empty.");
        } else {
            categoryService.addCategory(categoryName.trim());
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

            if (selected.getIsHidden()) {
                adminCategoryService.unhideCategory(catId);
            } else {
                adminCategoryService.hideCategory(catId);
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
    
    private static void manageReportedArticles() {
        List<NewsArticleReport> reports = adminNewsService.getReportedArticles();

        if (reports.isEmpty()) {
            System.out.println("No reported articles found.");
            return;
        }

        System.out.println("\nReported Articles:");
        showReportedNewsDetails(reports);

        while (true) {
            System.out.println("Options:");
            System.out.println("1. Hide Article by ID");
            System.out.println("2. Unhide Article by ID");
            System.out.println("3. Back");

            String choice = InputUtil.readLine("Enter your choice: ");
            switch (choice) {
                case "1":
                    int hideId = InputUtil.readInt("Enter News Article ID to hide: ");
                    adminNewsService.hideNews(hideId);
                    break;
                case "2":
                    int unhideId = InputUtil.readInt("Enter News Article ID to unhide: ");
                    adminNewsService.unhideNews(unhideId);
                    break;
                case "3":
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }
    
    private static void showReportedNewsDetails(List<NewsArticleReport> reports) {

    	SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");

    	System.out.println("\nReported Articles:");
    	System.out.printf("%-5s %-10s %-60s %-20s %-12s %-15s %-10s%n", 
    	    "No.", "News ID", "Title", "Reason", "Reported At", "Report Count", "Hidden");
    	System.out.println("---------------------------------------------------------------------------------------------------------------------------");

    	Map<Integer, List<NewsArticleReport>> groupedByNews = reports.stream()
    	        .collect(Collectors.groupingBy(NewsArticleReport::getNewsId));

    	int count = 1;
    	for (Map.Entry<Integer, List<NewsArticleReport>> entry : groupedByNews.entrySet()) {
    	    int newsId = entry.getKey();
    	    List<NewsArticleReport> reportList = entry.getValue();

    	    NewsArticleReport latestReport = reportList.stream()
    	            .max(Comparator.comparing(NewsArticleReport::getReportedAt))
    	            .orElse(null);

    	    if (latestReport != null) {
    	        System.out.printf("%-5d %-10d %-60s %-20s %-12s %-15d %-10s%n",
    	                count++,
    	                newsId,
    	                truncate(latestReport.getNewsArticle(), 58),
    	                truncate(latestReport.getReason(), 18),
    	                sdf.format(latestReport.getReportedAt()),
    	                reportList.size(),
    	                latestReport.getIsHidden() == 1 ? "Yes" : "No"
    	        );
    	    }
    	}
    }
    
    private static String truncate(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }
    
    private static void manageKeywordFilters() {
        while (true) {
            List<HiddenKeyword> keywords = adminKeywordService.getHiddenKeywords();

            System.out.println("\n--- Hidden Keywords ---");
            if (keywords.isEmpty()) {
                System.out.println("No hidden keywords.");
            } else {
                for (HiddenKeyword k : keywords) {
                    System.out.println(k);
                }
            }

            System.out.println("\nOptions:");
            System.out.println("1. Add Keyword");
            System.out.println("2. Delete Keyword by ID");
            System.out.println("3. Back");

            String choice = InputUtil.readLine("Enter your choice: ");
            switch (choice) {
                case "1":
                    String keyword = InputUtil.readLine("Enter keyword to add: ").trim();
                    if (!keyword.isEmpty()) {
                    	adminKeywordService.addKeyword(keyword);
                    } else {
                        System.out.println("Keyword cannot be empty.");
                    }
                    break;
                case "2":
                    int id = InputUtil.readInt("Enter Keyword ID to delete: ");
                    adminKeywordService.deleteKeyword(id);
                    break;
                case "3":
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}
