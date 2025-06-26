package frontend.newsaggregation.console;

import java.util.List;

import frontend.newsaggregation.model.ExternalServer;
import frontend.newsaggregation.model.User;
import frontend.newsaggregation.service.AuthService;
import frontend.newsaggregation.service.ExternalServerService;
import frontend.newsaggregation.util.InputUtil;

public class AdminDashboard {
	
	private static final AuthService authService = AuthService.getInstance();
	private static final ExternalServerService serverService = ExternalServerService.getInstance();

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
                    List<ExternalServer> servers = serverService.getAllServers();

                    if (servers.isEmpty()) {
                        System.out.println("No external servers found.");
                    } else {
                        System.out.println("\nList of external servers:");
                        int count = 1;
                        for (ExternalServer server : servers) {
                            String status = serverService.formatStatus(server.getServerStatus());
                            String lastAccessed = serverService.formatDate(server.getLastAccessed());
                            System.out.println(count++ + ". " + server.getServerName() + " - " + status + " - last accessed: " + lastAccessed);
                        }
                    }
                    break;
                case "2":
                    List<ExternalServer> serverDetails = serverService.getServerDetails();

                    if (serverDetails.isEmpty()) {
                        System.out.println("No external server details found.");
                    } else {
                        System.out.println("\nList of external server details:");
                        int count = 1;
                        for (ExternalServer server : serverDetails) {
                            System.out.println(count++ + ". " + server.getServerName() + " - " + server.getApiKey());
                        }
                    }
                    break;

                case "3":
                    ExternalServerService updateService = new ExternalServerService();

                    String idInput = InputUtil.readLine("Enter the external server ID: ");
                    int serverId;
                    try {
                        serverId = Integer.parseInt(idInput);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid server ID.");
                        break;
                    }

                    String updatedKey = InputUtil.readLine("Enter the updated API key: ");

                    boolean updated = updateService.updateApiKey(serverId, updatedKey);
                    if (updated) {
                        System.out.println("External server API key updated successfully.");
                    } else {
                        System.out.println("Failed to update the external server.");
                    }
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
