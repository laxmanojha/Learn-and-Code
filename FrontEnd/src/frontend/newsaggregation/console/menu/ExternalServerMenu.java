package frontend.newsaggregation.console.menu;

import java.util.List;

import frontend.newsaggregation.model.ExternalServer;
import frontend.newsaggregation.service.ExternalServerService;
import frontend.newsaggregation.util.InputUtil;

public class ExternalServerMenu {

	private static final ExternalServerService serverService = ExternalServerService.getInstance();

	public static void handleExternalServerWithStatus() {
    	List<ExternalServer> servers = serverService.getAllServers();

        if (servers.isEmpty()) {
            System.out.println("No external servers found.");
        } else {
            System.out.println("\nList of external servers:");
            System.out.printf("%-5s %-20s %-10s %-20s%n", "No.", "Server Name", "Status", "Last Accessed");
            System.out.println("---------------------------------------------------------------");

            int count = 1;
            for (ExternalServer server : servers) {
                String status = serverService.formatStatus(server.getServerStatus());
                String lastAccessed = serverService.formatDate(server.getLastAccessed());
                System.out.printf("%-5d %-20s %-10s %-20s%n", count++, server.getServerName(), status, lastAccessed);
            }
        }
    }
    
    public static void handleExternalServerDetails() {
    	List<ExternalServer> serverDetails = serverService.getServerDetails();

        if (serverDetails.isEmpty()) {
            System.out.println("No external server details found.");
        } else {
            System.out.println("\nList of external server details:");
            System.out.printf("%-5s %-20s %-50s%n", "No.", "Server Name", "API Key");
            System.out.println("--------------------------------------------------------------------------------");

            int count = 1;
            for (ExternalServer server : serverDetails) {
                System.out.printf("%-5d %-20s %-50s%n", count++, server.getServerName(), server.getApiKey());
            }

        }
    }
    
    public static void updateExternalServerDetails() {
    	ExternalServerService updateService = new ExternalServerService();

        String idInput = InputUtil.readLine("Enter the external server ID or 'back' to return: ");
        if (idInput.equalsIgnoreCase("back")) {
            return;
        }
        int serverId;
        try {
            serverId = Integer.parseInt(idInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid server ID.");
            return;
        }

        String updatedKey = InputUtil.readLine("Enter the updated API key: ");

        updateService.updateApiKey(serverId, updatedKey);
    }
}
