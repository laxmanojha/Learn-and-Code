package frontend.newsaggregation.console.menu;

import frontend.newsaggregation.model.Category;
import frontend.newsaggregation.model.NewsArticle;
import frontend.newsaggregation.model.NotificationPreference;
import frontend.newsaggregation.model.User;
import frontend.newsaggregation.service.NotificationService;
import frontend.newsaggregation.util.AppState;
import frontend.newsaggregation.util.InputUtil;

import java.util.Arrays;
import java.util.List;

public class NotificationMenu {

    private static final NotificationService notificationService = new NotificationService();

    public static void startNotificationMenu(User user) {
        while (true) {
        	if (AppState.shouldExitToHome()) {
                return;
            }
            System.out.println("\nN O T I F I C A T I O N S");
            System.out.println("1. View Notifications");
            System.out.println("2. Configure Notifications");
            System.out.println("3. Back");
            System.out.println("4. Logout");

            String choice = InputUtil.readLine("Enter your choice: ");

            switch (choice) {
                case "1":
                    viewNotifications();
                    break;
                case "2":
                    configureNotifications();
                    break;
                case "3":
                    return;
                case "4":
                    System.out.println("Logging out...");
                    AppState.setExitToHome(true);
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void viewNotifications() {
        List<NewsArticle> notifications = notificationService.fetchNotifications();
        if (notifications.isEmpty()) {
            System.out.println("No notifications found.");
            return;
        }

        int page = 0;
        int pageSize = 5;
        while (true) {
            if (AppState.shouldExitToHome()) {
                return;
            }

            int start = page * pageSize;
            int end = Math.min(start + pageSize, notifications.size());

            System.out.println("\nN O T I F I C A T I O N S  (Page " + (page + 1) + "):");
            for (int index = start; index < end; index++) {
                NewsArticle article = notifications.get(index);
                System.out.println((index + 1) + ". " + article.getTitle());
                System.out.println("   ➤ Source: " + article.getSource());
                System.out.println("   ➤ Categories: " + article.getCategories());
                System.out.println("   ➤ Published At: " + article.getPublishedAt());
                System.out.println("   ➤ URL: " + article.getUrl());
                System.out.println("-------------------------------------------------");
            }

            System.out.println("Options: n (Next), p (Previous), b (Back), l (Logout)");
            String option = InputUtil.readLine("Choose: ");

            switch (option.toLowerCase()) {
                case "n":
                    if (end >= notifications.size()) {
                        System.out.println("No more pages.");
                    } else {
                        page++;
                    }
                    break;
                case "p":
                    if (page == 0) {
                        System.out.println("Already at first page.");
                    } else {
                        page--;
                    }
                    break;
                case "b":
                    return;
                case "l":
                    AppState.setExitToHome(true);
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void configureNotifications() {
        while (true) {
            if (AppState.shouldExitToHome()) {
                return;
            }

            List<Category> categories = notificationService.fetchCategories();
            List<NotificationPreference> categoryPreferences = notificationService.fetchCategoryPreferences();
            NotificationPreference keywordPreferences = notificationService.fetchKeywordsPreferences();

            System.out.println("\nC O N F I G U R E - N O T I F I C A T I O N S");

            int index = 1;
            for (Category cat : categories) {
                List<String> catKeywords = categoryPreferences.stream()
                        .filter(p -> p.getCategoryId() != null && p.getCategoryId() == cat.getId() && p.isEnabled())
                        .flatMap(p -> p.getKeywords() != null ? p.getKeywords().stream() : java.util.stream.Stream.empty())
                        .toList();

                boolean isEnabled = !catKeywords.isEmpty();

                String keywordDisplay = isEnabled ? " [" + String.join(", ", catKeywords) + "]" : "";
                System.out.println(index++ + ". " + cat.getName() + keywordDisplay + " - " + (isEnabled ? "Enabled" : "Disabled"));
            }

            // Handle keyword (non-category) preferences
            boolean isKeywordEnabled = keywordPreferences != null && keywordPreferences.isEnabled();
            List<String> keywordOnlyPrefs = keywordPreferences != null && keywordPreferences.getKeywords() != null
                    ? keywordPreferences.getKeywords()
                    : List.of();
            String keywordDisplay = isKeywordEnabled && !keywordOnlyPrefs.isEmpty()
                    ? " [" + String.join(", ", keywordOnlyPrefs) + "]"
                    : "";

            System.out.println(index + ". Keywords" + keywordDisplay + " - " + (isKeywordEnabled ? "Enabled" : "Disabled"));
            int keywordOption = index;
            index++;
            System.out.println(index + ". Back");
            int backOption = index;
            index++;
            System.out.println(index + ". Logout");

            String choice = InputUtil.readLine("Enter your choice: ");
            if (choice.equals(String.valueOf(backOption))) {
                return;
            } else if (choice.equals(String.valueOf(index))) {
                System.out.println("Logging out...");
                AppState.setExitToHome(true);
                return;
            } else {
                try {
                    int option = Integer.parseInt(choice);
                    if (option == keywordOption) {
                        handleKeywordOption(isKeywordEnabled);
                    } else if (option > 0 && option <= categories.size()) {
                        handleCategoryOption(categories.get(option - 1), categoryPreferences);
                    } else {
                        System.out.println("Invalid option.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input.");
                }
            }
        }
    }

    private static void handleCategoryOption(Category category, List<NotificationPreference> preferences) {
        boolean isEnabled = preferences.stream()
                .anyMatch(p -> p.getCategoryId() != null && p.getCategoryId() == category.getId() && p.isEnabled());

        String action = InputUtil.readLine("Do you want to " + (isEnabled ? "Disable" : "Enable") + " this category? (y/n): ");
        if (action.equalsIgnoreCase("y")) {
            if (isEnabled) {
                notificationService.disableCategory(category.getId());
            } else {
                String keywordsInput = InputUtil.readLine("Enter keywords (comma-separated): ");
                List<String> keywords = Arrays.asList(keywordsInput.split(","));
                notificationService.enableCategory(category.getId(), keywords);
            }
        }
    }

    private static void handleKeywordOption(boolean isEnabled) {
        String action = InputUtil.readLine("Do you want to " + (isEnabled ? "Disable" : "Enable") + " keyword notifications? (y/n): ");
        if (action.equalsIgnoreCase("y")) {
            if (isEnabled) {
                notificationService.disableKeyword();
            } else {
                String keywordsInput = InputUtil.readLine("Enter keywords (comma-separated): ");
                List<String> keywords = Arrays.asList(keywordsInput.split(","));
                notificationService.enableKeyword(keywords);
            }
        }
    }
}
