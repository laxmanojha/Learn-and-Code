package frontend.newsaggregation.console;

import frontend.newsaggregation.model.User;
import frontend.newsaggregation.service.AuthService;
import frontend.newsaggregation.util.InputUtil;

import java.util.regex.Pattern;

public class Main {

    private static final AuthService authService = AuthService.getInstance();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\nWelcome to the News Aggregator application. Please choose the options below.");
            System.out.println("1. Login");
            System.out.println("2. Sign up");
            System.out.println("3. Exit");

            String choice = InputUtil.readLine("Enter your choice: ");

            switch (choice) {
                case "1":
                    handleLogin();
                    break;
                case "2":
                    handleSignup();
                    break;
                case "3":
                	handleLogout();
                    System.out.println("Thank you for using News Aggregator. Goodbye!");
                    InputUtil.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void handleLogin() {
        String username = InputUtil.readLine("Enter username: ");
        String password = InputUtil.readLine("Enter password: ");

        User loggedInUser = authService.login(username, password);
        if (loggedInUser != null) {
            System.out.println("Welcome " + loggedInUser.getUsername() + "!");
            System.out.println(loggedInUser.getRoleType());
            if ("admin".equalsIgnoreCase(loggedInUser.getRoleType())) {
                AdminDashboard.startAdminMenu(loggedInUser);
            } else {
                UserDashboard.startUserMenu(loggedInUser);
            }
        } else {
            System.out.println("Login failed. Please check credentials.");
        }
    }


    private static void handleSignup() {
        String username = InputUtil.readLine("Enter username: ");
        String email = InputUtil.readLine("Enter email: ");
        String password = InputUtil.readLine("Enter password: ");

        if (!isValidUsername(username)) {
            System.out.println("Invalid username. Only alphanumeric and underscores allowed.");
            return;
        }

        if (!isValidEmail(email)) {
            System.out.println("Invalid email format.");
            return;
        }

        if (!isValidPassword(password)) {
            System.out.println("Password must be at least 6 characters with at least 1 number.");
            return;
        }

        User user = new User(username, email, password);
        boolean success = authService.signup(user);

        if (success) {
            System.out.println("Sign up successful. You can now login.");
        } else {
            System.out.println("Sign up failed. Try with different credentials.");
        }
    }
    
    private static void handleLogout() {
    	if (authService.logout()) {
    	    System.out.println("Logged out successfully.");
    	} else {
    	    System.out.println("Logout failed.");
    	}
    	return;
    }

    private static boolean isValidUsername(String username) {
        return Pattern.matches("^[a-zA-Z0-9_]{3,}$", username);
    }

    private static boolean isValidEmail(String email) {
        return Pattern.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$", email);
    }

    private static boolean isValidPassword(String password) {
        return password.length() >= 6 && password.matches(".*\\d.*");
    }
}
