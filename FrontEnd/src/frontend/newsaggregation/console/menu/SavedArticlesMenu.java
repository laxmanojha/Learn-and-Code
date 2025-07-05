package frontend.newsaggregation.console.menu;

import frontend.newsaggregation.model.NewsArticle;
import frontend.newsaggregation.model.User;
import frontend.newsaggregation.service.ArticleActionService;
import frontend.newsaggregation.service.AuthService;
import frontend.newsaggregation.service.SavedArticlesService;
import frontend.newsaggregation.util.AppState;
import frontend.newsaggregation.util.DateUtil;
import frontend.newsaggregation.util.InputUtil;

import java.util.List;

public class SavedArticlesMenu {

    private static final SavedArticlesService savedService = new SavedArticlesService();
    private static final ArticleActionService articleService = new ArticleActionService();

    public static void show(User user) {
        while (true) {
        	if (AppState.shouldExitToHome()) {
                return;
            }
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
                    if (AuthService.getInstance().logout()) {
                        AppState.setExitToHome(true);
                        return;
                    }
                    break;
                case "3":
                    int articleId = InputUtil.readInt("Enter Article ID to delete: ");
                    System.out.println("ArticleID: " + articleId);
                    boolean deleted = savedService.deleteSavedArticle(articleId);
                    if (deleted) {
                        System.out.println("Article deleted successfully.");
                    } else {
                        System.out.println("Failed to delete the article.");
                    }
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
