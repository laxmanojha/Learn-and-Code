package frontend.newsaggregation.console.menu;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import frontend.newsaggregation.model.Category;
import frontend.newsaggregation.model.HiddenKeyword;
import frontend.newsaggregation.model.NewsArticleReport;
import frontend.newsaggregation.service.AdminCategoryService;
import frontend.newsaggregation.service.AdminKeywordService;
import frontend.newsaggregation.service.AdminNewsService;
import frontend.newsaggregation.util.InputUtil;

public class ReportMenu {

	private static final AdminCategoryService adminCategoryService = AdminCategoryService.getInstance();
	private static final AdminNewsService adminNewsService = AdminNewsService.getInstance();
	private static final AdminKeywordService adminKeywordService = AdminKeywordService.getInstance();

	public static void handleCategoryVisibility() {
        List<Category> categories = adminCategoryService.fetchAllCategories();

        if (categories.isEmpty()) {
            System.out.println("No categories found.");
            return;
        } else {
        	showCategory(categories);
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
	
	private static void showCategory(List<Category> categories) {
		System.out.println("\nCategory Visibility:");
		System.out.printf("%-5s %-30s %-10s%n", "ID", "Category Name", "Visibility");
		System.out.println("-----------------------------------------------------------");

		for (Category cat : categories) {
		    String name = capitalize(cat.getName());
		    String visibility = cat.getIsHidden() ? "Hidden" : "Visible";
		    System.out.printf("%-5d %-30s %-10s%n", cat.getId(), name, visibility);
		}
	}
	
	private static String capitalize(String word) {
	    if (word == null || word.isEmpty()) return word;
	    return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
	}
    
    public static void manageReportedArticles() {
        List<NewsArticleReport> reports = adminNewsService.getReportedArticles();

        if (reports.isEmpty()) {
            System.out.println("No reported articles found.");
            return;
        }

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
    
    public static void showReportedNewsDetails(List<NewsArticleReport> reports) {
    	SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");

    	System.out.println("\nReported Articles:");
    	System.out.printf("%-5s %-10s %-60s %-20s %-22s %-15s %-10s%n", 
    	    "No.", "News ID", "Title", "Reason", "Reported At", "Report Count", "Hidden");
    	System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------");

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
    	        System.out.printf("%-5d %-10d %-60s %-20s %-22s %-15d %-10s%n",
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
    
    public static String truncate(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }
    
    public static void manageKeywordFilters() {
        while (true) {
            List<HiddenKeyword> keywords = adminKeywordService.getHiddenKeywords();

            if (keywords.isEmpty()) {
                System.out.println("No hidden keywords.");
            } else {
            	showHiddenKeywords(keywords);
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
    
    private static void showHiddenKeywords(List<HiddenKeyword> keywords) {
    	System.out.println("\nList of Hidden Keywords:");
    	System.out.printf("%-5s %-20s %-25s%n", "ID", "Keyword", "Created At");
    	System.out.println("-------------------------------------------------------");

    	SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");

    	for (HiddenKeyword k : keywords) {
    	    System.out.printf(
    	        "%-5d %-20s %-25s%n",
    	        k.getId(),
    	        k.getKeyword(),
    	        sdf.format(k.getCreatedAt())
    	    );
    	}
    }
}
