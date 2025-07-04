package frontend.newsaggregation.console.menu;

import frontend.newsaggregation.model.NewsArticle;
import frontend.newsaggregation.model.User;
import frontend.newsaggregation.service.AuthService;
import frontend.newsaggregation.service.SavedArticlesService;
import frontend.newsaggregation.util.DateUtil;
import frontend.newsaggregation.util.InputUtil;

import java.util.List;

public class SavedArticlesMenu {

    private static final SavedArticlesService savedService = new SavedArticlesService();

    public static void show(User user) {
        while (true) {
            List<NewsArticle> savedArticles = savedService.fetchSavedArticles();

            System.out.println("\nWelcome to the News Application, " + user.getUsername() + "!");
            System.out.println("Date: " + DateUtil.getCurrentDate() + " Time: " + DateUtil.getCurrentTime());
            System.out.println("S A V E D");

            if (savedArticles.isEmpty()) {
                System.out.println("No saved articles found.");
            } else {
                savedArticles.forEach(article -> {
                    System.out.println(article);
                    System.out.println("-------------------------------------------------");
                });
            }

            System.out.println("\n1. Back");
            System.out.println("2. Logout");
            System.out.println("3. Delete Article");

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
                    int articleId = InputUtil.readInt("Enter Article ID to delete: ");
                    boolean deleted = savedService.deleteSavedArticle(articleId);
                    if (deleted) {
                        System.out.println("Article deleted successfully.");
                    } else {
                        System.out.println("Failed to delete the article.");
                    }
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
