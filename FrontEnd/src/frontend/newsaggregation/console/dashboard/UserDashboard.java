package frontend.newsaggregation.console.dashboard;

import frontend.newsaggregation.console.menu.HeadlineMenu;
import frontend.newsaggregation.console.menu.NotificationMenu;
import frontend.newsaggregation.console.menu.SavedArticlesMenu;
import frontend.newsaggregation.console.menu.SearchMenu;
import frontend.newsaggregation.model.User;
import frontend.newsaggregation.service.AuthService;
import frontend.newsaggregation.util.InputUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserDashboard {

	private static final AuthService authService = AuthService.getInstance();

    public static void startUserMenu(User user) {
        showWelcomeMessage(user);
        
        while (true) {
            System.out.println("\nPlease choose the options below:");
            System.out.println("1. Headlines");
            System.out.println("2. Saved Articles");
            System.out.println("3. Search");
            System.out.println("4. Notifications");
            System.out.println("5. Logout");

            String choice = InputUtil.readLine("Enter your choice: ");

            switch (choice) {
                case "1":
                    HeadlineMenu.startHeadlineMenu(user);
                    break;
                case "2":
                    SavedArticlesMenu.show(user);
                    break;
                case "3":
                    SearchMenu.show(user);
                    break;
                case "4":
                    NotificationMenu.startNotificationMenu(user);
                    break;
                case "5":
                    handleLogout();
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

        System.out.println("\nWelcome to the News Application, " + user.getUsername() + "! Date: " + date);
        System.out.println("Time: " + time);
    }
    

    private static void handleLogout() {
    	boolean loggedOut = authService.logout();
    	if (loggedOut) {
    	    System.out.println("Logged out successfully.");
    	} else {
    	    System.out.println("Logout failed.");
    	}
    	return;
    }
}
