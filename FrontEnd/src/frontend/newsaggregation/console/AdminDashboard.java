package frontend.newsaggregation.console;

import frontend.newsaggregation.model.User;
import frontend.newsaggregation.service.AuthService;
import frontend.newsaggregation.util.InputUtil;

public class AdminDashboard {
	
	private static final AuthService authService = AuthService.getInstance();

    public static void startAdminMenu(User user) {
        while (true) {
            System.out.println("\nWelcome " + user.getUsername() + "! Please choose an option:");
            System.out.println("1. View the list of external servers and status");
            System.out.println("2. View the external server’s details");
            System.out.println("3. Update/Edit the external server’s details");
            System.out.println("4. Add new News Category");
            System.out.println("5. Logout");

            String choice = InputUtil.readLine("Enter your choice: ");

            switch (choice) {
                case "1":
                    System.out.println("Viewing list of external servers and status...");
                    break;
                case "2":
                    System.out.println("Viewing external server’s details...");
                    break;
                case "3":
                    System.out.println("Updating external server’s details...");
                    break;
                case "4":
                    System.out.println("Adding new News Category...");
                    break;
                case "5":
                	handleLogout();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
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
