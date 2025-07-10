package frontend.newsaggregation.console.menu;

import frontend.newsaggregation.model.NewsArticle;
import frontend.newsaggregation.model.User;
import frontend.newsaggregation.service.ArticleActionService;
import frontend.newsaggregation.service.AuthService;
import frontend.newsaggregation.service.SavedArticlesService;
import frontend.newsaggregation.util.AppState;
import frontend.newsaggregation.util.InputUtil;

import java.util.List;

public class SavedArticlesMenu {

    private static final SavedArticlesService savedService = new SavedArticlesService();
    private static final ArticleActionService articleService = new ArticleActionService();

    public static void show(User user) {
        List<NewsArticle> savedArticles = savedService.fetchSavedArticles();

        if (savedArticles.isEmpty()) {
            System.out.println("No saved articles found.");
            return;
        }
        handleArticleActions(savedArticles, user);
    }
    
    private static void handleArticleActions(List<NewsArticle> articles, User user) {
    	int page = 0;
        int pageSize = 5;
        boolean continueLoop = true;
        while (continueLoop) {
            if (AppState.shouldExitToHome()) {
                return;
            }

            int start = page * pageSize;
            int end = Math.min(start + pageSize, articles.size());
            System.out.println("\n----- SAVED ARTICLES -----  (Page " + (page + 1) + "):");
            for (int index = start; index < end; index++) {
                NewsArticle article = articles.get(index);
                System.out.println(article);
                System.out.println("-----------------------------------------");
            }

            System.out.println("Options: n (Next), p (Previous)");
            System.out.println("\nActions:");
            System.out.println("1. Back");
            System.out.println("2. Logout");
            System.out.println("3. Delete Article");
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
                    	articles = savedService.fetchSavedArticles();
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
