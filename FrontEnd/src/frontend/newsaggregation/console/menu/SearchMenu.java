package frontend.newsaggregation.console.menu;

import frontend.newsaggregation.model.NewsArticle;
import frontend.newsaggregation.model.User;
import frontend.newsaggregation.service.AuthService;
import frontend.newsaggregation.service.SearchService;
import frontend.newsaggregation.service.SavedArticlesService;
import frontend.newsaggregation.util.DateUtil;
import frontend.newsaggregation.util.InputUtil;

import java.util.List;

public class SearchMenu {

    private static final SearchService searchService = new SearchService();
    private static final SavedArticlesService savedService = new SavedArticlesService();

    public static void show(User user) {
        String query = InputUtil.readLine("Enter search keyword: ");
        if (query.isEmpty()) {
            System.out.println("Search keyword cannot be empty.");
            return;
        }

        // Date filter
        String applyDate = InputUtil.readLine("Apply date range filter? (Y/N): ");
        String startDate = null;
        String endDate = null;
        if (applyDate.equalsIgnoreCase("Y")) {
            startDate = InputUtil.readLine("Enter start date (yyyy-MM-dd): ");
            endDate = InputUtil.readLine("Enter end date (yyyy-MM-dd): ");
        }

        // Sorting
        String sort = null;
        String sortChoice = InputUtil.readLine("Sort by 1. Likes 2. Dislikes 3. No Sorting: ");
        switch (sortChoice) {
            case "1":
                sort = "likes";
                break;
            case "2":
                sort = "dislikes";
                break;
            case "3":
                sort = null;
                break;
            default:
                System.out.println("Invalid sort option. Skipping sort.");
        }

        // Fetch results
        List<NewsArticle> articles = searchService.searchArticles(query, startDate, endDate, sort);

        System.out.println("\nWelcome to the News Application, " + user.getUsername() + "!");
        System.out.println("Date: " + DateUtil.getCurrentDate() + " Time: " + DateUtil.getCurrentTime());
        System.out.println("S E A R C H");
        System.out.println("Results for \"" + query + "\"");

        if (articles.isEmpty()) {
            System.out.println("No articles found for your query.");
        } else {
            articles.forEach(article -> {
                System.out.println(article);
                System.out.println("-------------------------------------------------");
            });
        }

        while (true) {
            System.out.println("\n1. Back");
            System.out.println("2. Logout");
            System.out.println("3. Save Article");

            String choice = InputUtil.readLine("Enter your choice: ");

            switch (choice) {
                case "1":
                    return;
                case "2":
                    if (AuthService.getInstance().logout()) {
                        System.out.println("Logged out successfully.");
                        System.exit(0);
                    }
                    break;
                case "3":
                    int articleId = InputUtil.readInt("Enter Article ID to save: ");
                    boolean saved = savedService.saveArticle(articleId);
                    if (saved) {
                        System.out.println("✔️ Article saved successfully.");
                    } else {
                        System.out.println("❌ Failed to save the article.");
                    }
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
