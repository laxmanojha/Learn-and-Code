package com.itt.ecommerce.console;

import java.io.IOException;
import java.util.Scanner;
import com.itt.ecommerce.service.CartService;
import com.itt.ecommerce.service.OrderService;
import com.itt.ecommerce.service.UserService;

public class Main {
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            runApplication();
        } catch (IOException | InterruptedException e) {
            System.err.println("An error occurred: " + e.getMessage());
        } finally {
            sc.close();
        }
    }

    private static void runApplication() throws IOException, InterruptedException {
        int choice;
        do {
            showMainMenu();
            choice = getUserChoice();

            switch (choice) {
                case 1 -> handleLogin();
                case 2 -> handleRegistration();
                case 3 -> System.out.println("Exiting the application...");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 3);
    }

    private static void showMainMenu() {
        System.out.println("\n===== E-Commerce Console App =====");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");
    }

    private static int getUserChoice() {
        try {
            return Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return -1;
        }
    }

    private static void handleLogin() throws IOException, InterruptedException {
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();

        boolean loginSuccess = UserService.loginUser(username, password);
        if (loginSuccess) {
            handleUserDashboard(username);
        }
    }

    private static void handleRegistration() throws IOException, InterruptedException {
        System.out.print("Enter name: ");
        String name = sc.nextLine();
        System.out.print("Enter username: ");
        String regUserName = sc.nextLine();
        System.out.print("Enter password: ");
        String regPassword = sc.nextLine();

        boolean registrationSuccess = UserService.registerUser(name, regUserName, regPassword);
        if (registrationSuccess) {
            System.out.println("Regitration successful! Congratulations " + name);
        }
    }

    private static void handleUserDashboard(String username) throws IOException, InterruptedException {
        int choice;
        do {
            showUserDashboard();
            choice = getUserChoice();

            switch (choice) {
                case 1 -> UserService.viewUserDetails(username);
                case 2 -> CartService.addToCart(username);
                case 3 -> CartService.viewCart(username);
                case 4 -> OrderService.checkOut(username);
                case 5 -> OrderService.viewOrderHistory(username);
                case 6 -> System.out.println("Logging out...");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 6);
    }

    private static void showUserDashboard() {
        System.out.println("\n===== User Dashboard =====");
        System.out.println("1. View Details");
        System.out.println("2. View Products");
        System.out.println("3. View Cart");
        System.out.println("4. Check Out");
        System.out.println("5. Order History");
        System.out.println("6. Logout");
        System.out.print("Enter your choice: ");
    }
}
