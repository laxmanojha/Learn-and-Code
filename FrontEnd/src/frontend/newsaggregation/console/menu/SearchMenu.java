package frontend.newsaggregation.console.menu;

import frontend.newsaggregation.model.NewsArticle;
import frontend.newsaggregation.model.User;
import frontend.newsaggregation.service.ArticleActionService;
import frontend.newsaggregation.service.AuthService;
import frontend.newsaggregation.service.SearchService;
import frontend.newsaggregation.service.SavedArticlesService;
import frontend.newsaggregation.util.AppState;
import frontend.newsaggregation.util.DateUtil;
import frontend.newsaggregation.util.InputUtil;

import java.util.List;

public class SearchMenu {

    private static final SearchService searchService = new SearchService();
    private static final ArticleActionService articleService = new ArticleActionService();
    private static final AuthService authService = new AuthService();

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
        String sortChoice = InputUtil.readLine("Sort by \n1. Likes \n2. Dislikes \n3. No Sorting\nEnter choice: ");
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

        boolean personalizedPreference = InputUtil.readYesNo("Make it personalized");

        // Fetch results
        List<NewsArticle> articles = searchService.searchArticles(query, startDate, endDate, sort, personalizedPreference);
        
        if ("likes".equalsIgnoreCase(sort)) {
            articles.sort((a, b) -> Integer.compare(b.getLikeCount(), a.getLikeCount())); // Descending by likes
        } else if ("dislikes".equalsIgnoreCase(sort)) {
            articles.sort((a, b) -> Integer.compare(b.getDislikeCount(), a.getDislikeCount())); // Descending by dislikes
        }
        
        if (articles.isEmpty()) {
            System.out.println("No articles found for your query-> " + query);
            return;
        }

        handleArticleActions(articles, user, query);
    }
    
    private static void handleArticleActions(List<NewsArticle> articles, User user, String query) {
    	int page = 0;
        int pageSize = 5;
        while (true) {
            if (AppState.shouldExitToHome()) {
                return;
            }

            int start = page * pageSize;
            int end = Math.min(start + pageSize, articles.size());
            System.out.println("\nWelcome to the News Application, " + user.getUsername() + "!");
            System.out.println("Date: " + DateUtil.getCurrentDate() + " Time: " + DateUtil.getCurrentTime());
            System.out.println("\n----- SEARCH -----  (Page " + (page + 1) + "):");
            System.out.println("Results for \"" + query + "\"");
            for (int index = start; index < end; index++) {
                NewsArticle article = articles.get(index);
                System.out.println(article.displayWithReaction());
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
