package frontend.newsaggregation.console.menu;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import frontend.newsaggregation.constant.StaticConfiguration;
import frontend.newsaggregation.model.Category;
import frontend.newsaggregation.model.NewsArticle;
import frontend.newsaggregation.model.User;
import frontend.newsaggregation.service.ArticleActionService;
import frontend.newsaggregation.service.AuthService;
import frontend.newsaggregation.service.HeadlineService;
import frontend.newsaggregation.util.AppState;
import frontend.newsaggregation.util.HttpUtil;
import frontend.newsaggregation.util.InputUtil;

public class HeadlineMenu {

    private static final HeadlineService headlineService = new HeadlineService();
    private static final ArticleActionService articleService = new ArticleActionService();
    private static final AuthService authService = new AuthService();

    public static void startHeadlineMenu(User user) {
        while (true) {
        	if (AppState.shouldExitToHome()) {
                return;
            }
            System.out.println("\nHeadlines Menu:");
            System.out.println("1. Today");
            System.out.println("2. Date Range");
            System.out.println("3. Back");
            System.out.println("4. Logout");

            String choice = InputUtil.readLine("Enter your choice: ");

            switch (choice) {
                case "1":
                    showTodayHeadlines(user);
                    break;
                case "2":
                    showDateRangeHeadlines(user);
                    break;
                case "3":
                	return;
                case "4":
                    if (authService.logout()) {
                        AppState.setExitToHome(true);
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
        boolean personalizedPreference = InputUtil.readYesNo("Make it personalized");
        List<NewsArticle> articles = headlineService.fetchTodayHeadlines(personalizedPreference);
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
        boolean personalizedPreference = InputUtil.readYesNo("Make it personalized");

        List<NewsArticle> articles = headlineService.fetchHeadlinesByDateRange(startDate, endDate, category, personalizedPreference);
        if (articles.isEmpty()) {
            System.out.println("No articles found for the selected date range.");
            return;
        }
        handleArticleActions(articles, user);
    }

    private static String selectCategory() {
        String url = StaticConfiguration.getBaseUrl() + "/news/category";

        try {
            HttpResponse<String> response = HttpUtil.sendGetRequest(url);
            if (response.statusCode() != 200) {
                System.out.println("Failed to fetch categories. Using default 'general'.");
                return "general";
            }

            // Parse JSON array to List<Category>
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Category>>() {}.getType();
            List<Category> categories = gson.fromJson(response.body(), listType);

            // Show menu
            System.out.println("\nSelect Category:");
            System.out.println("0. All");

            for (int index = 0; index < categories.size(); index++) {
                System.out.printf("%d. %s%n", index + 1, capitalize(categories.get(index).getName()));
            }

            String choice = InputUtil.readLine("Enter your choice: ");
            int index = Integer.parseInt(choice);

            if (index == 0) {
                return "general";
            }

            if (index > 0 && index <= categories.size()) {
                return categories.get(index - 1).getName();
            } else {
                System.out.println("Invalid choice. Using default 'general'.");
                return "general";
            }

        } catch (Exception e) {
            System.out.println("Error fetching categories: " + e.getMessage());
            return "general";
        }
    }

    private static String capitalize(String word) {
        if (word == null || word.isEmpty()) return word;
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }

    private static void handleArticleActions(List<NewsArticle> articles, User user) {
    	int page = 0;
        int pageSize = 5;
        while (true) {
            if (AppState.shouldExitToHome()) {
                return;
            }

            int start = page * pageSize;
            int end = Math.min(start + pageSize, articles.size());
            System.out.println("\n----- HEADLINES -----  (Page " + (page + 1) + "):");
            for (int index = start; index < end; index++) {
                NewsArticle article = articles.get(index);
                System.out.println(article);
                System.out.println("-----------------------------------------");
            }

            System.out.println("Options: n (Next), p (Previous)");
            System.out.println("1. Back");
            System.out.println("2. Logout");
            System.out.println("3. Save Article");
            System.out.println("4. Like/Dislike Article");
            System.out.println("5. Report Article");

            String action = InputUtil.readLine("Enter your choice: ");
            
            switch (action) {
	            case "n":
	            case "N":
                    if (end >= articles.size()) {
                        System.out.println("No more pages.");
                    } else {
                        page++;
                    }
                    break;
                case "p":
                case "P":
                    if (page == 0) {
                        System.out.println("Already at first page.");
                    } else {
                        page--;
                    }
                    break;
            	case "1":
                    return;
                case "2":
                    if (authService.logout()) {
                        System.out.println("Logged out successfully.");
                        AppState.setExitToHome(true);
                        return;
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
                    String comment = InputUtil.readLine("Comment(press enter to skip):");
                    articleService.reportArticle(reportId, comment);
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
