package frontend.newsaggregation.console.menu;

import java.util.List;
import frontend.newsaggregation.model.NewsArticle;
import frontend.newsaggregation.model.User;
import frontend.newsaggregation.service.ArticleActionService;
import frontend.newsaggregation.service.AuthService;
import frontend.newsaggregation.service.HeadlineService;
import frontend.newsaggregation.util.InputUtil;

public class HeadlineMenu {

    private static final HeadlineService headlineService = new HeadlineService();
    private static final ArticleActionService articleService = new ArticleActionService();
    private static final AuthService authService = new AuthService();

    public static void startHeadlineMenu(User user) {
        while (true) {
            System.out.println("\nHeadlines Menu:");
            System.out.println("1. Today");
            System.out.println("2. Date Range");
            System.out.println("3. Logout");

            String choice = InputUtil.readLine("Enter your choice: ");

            switch (choice) {
                case "1":
                    showTodayHeadlines(user);
                    break;
                case "2":
                    showDateRangeHeadlines(user);
                    break;
                case "3":
                    if (authService.logout()) {
                        System.out.println("Logged out successfully.");
                        return;
                    } else {
                        System.out.println("Logout failed.");
                    }
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void showTodayHeadlines(User user) {
        List<NewsArticle> articles = headlineService.fetchTodayHeadlines();
        if (articles.isEmpty()) {
            System.out.println("No articles found for today.");
            return;
        }
        handleArticleActions(articles, user);
    }

    private static void showDateRangeHeadlines(User user) {
        String startDate = InputUtil.readLine("Enter start date (YYYY-MM-DD): ");
        String endDate = InputUtil.readLine("Enter end date (YYYY-MM-DD): ");
        String category = selectCategory();

        List<NewsArticle> articles = headlineService.fetchHeadlinesByDateRange(startDate, endDate, category);
        if (articles.isEmpty()) {
            System.out.println("No articles found for the selected date range.");
            return;
        }
        handleArticleActions(articles, user);
    }

    private static String selectCategory() {
        System.out.println("\nSelect Category:");
        System.out.println("1. All");
        System.out.println("2. Business");
        System.out.println("3. Entertainment");
        System.out.println("4. Sports");
        System.out.println("5. Technology");

        String choice = InputUtil.readLine("Enter your choice: ");
        switch (choice) {
            case "1":
                return "general";
            case "2":
                return "business";
            case "3":
                return "entertainment";
            case "4":
                return "sports";
            case "5":
                return "technology";
            default:
                System.out.println("Invalid choice, defaulting to 'general'.");
                return "general";
        }
    }

    private static void handleArticleActions(List<NewsArticle> articles, User user) {
        while (true) {
            System.out.println("\n----- HEADLINES -----");
            for (NewsArticle article : articles) {
                System.out.println(article);
                System.out.println("-----------------------------------------");
            }

            System.out.println("\nActions:");
            System.out.println("1. Back");
            System.out.println("2. Logout");
            System.out.println("3. Save Article");
            System.out.println("4. Like/Dislike Article");
            System.out.println("5. Report Article");

            String action = InputUtil.readLine("Enter your choice: ");

            switch (action) {
                case "1":
                    return;
                case "2":
                    if (authService.logout()) {
                        System.out.println("Logged out successfully.");
                        System.exit(0);
                    } else {
                        System.out.println("Logout failed.");
                    }
                    break;
                case "3":
                    int saveId = InputUtil.readInt("Enter Article ID to save: ");
                    articleService.saveArticle(saveId);
                    break;
                case "4":
                    int reactId = InputUtil.readInt("Enter Article ID to react: ");
                    String reaction = InputUtil.readLine("Enter reaction (like/dislike): ").toLowerCase();
                    if (reaction.equals("like") || reaction.equals("dislike")) {
                        articleService.reactToArticle(reactId, reaction);
                    } else {
                        System.out.println("Invalid reaction. Use 'like' or 'dislike'.");
                    }
                    break;
                case "5":
                    int reportId = InputUtil.readInt("Enter Article ID to report: ");
                    articleService.reportArticle(reportId);
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
