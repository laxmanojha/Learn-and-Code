package frontend.newsaggregation.console.dashboard;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import frontend.newsaggregation.console.menu.CategoryMenu;
import frontend.newsaggregation.console.menu.ExternalServerMenu;
import frontend.newsaggregation.console.menu.ReportMenu;
import frontend.newsaggregation.model.User;
import frontend.newsaggregation.service.AuthService;
import frontend.newsaggregation.util.AppState;
import frontend.newsaggregation.util.InputUtil;

public class AdminDashboard {
	
	private static final AuthService authService = AuthService.getInstance();

    public static void startAdminMenu(User user) {
    	showWelcomeMessage(user);
    	boolean continueLoop = true;
        while (continueLoop) {
        	if (AppState.shouldExitToHome()) {
                AppState.reset();
                return;
            }
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
                    ExternalServerMenu.handleExternalServerWithStatus();
                    break;
                case "2":
                	ExternalServerMenu.handleExternalServerDetails();
                    break;

                case "3":
                	ExternalServerMenu.updateExternalServerDetails();
                    break;

                case "4":
                    CategoryMenu.handleAddingNewCategory();
                    break;

                case "5":
                    ReportMenu.handleCategoryVisibility();
                    break;

                case "6":
                	ReportMenu.manageReportedArticles();
                    break;
                    
                case "7":
                	ReportMenu.manageKeywordFilters();
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
}
    