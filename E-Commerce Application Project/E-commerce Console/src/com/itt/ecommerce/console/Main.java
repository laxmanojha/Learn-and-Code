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
                case 2 -> CartService.takeCartInputAndAdd(username);
                case 3 -> CartService.viewCart(username);
                case 4 -> editCart(username);
                case 5 -> OrderService.checkOut(username);
                case 6 -> OrderService.viewOrderHistory(username);
                case 7 -> System.out.println("Logging out...");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 7);
    }

    private static void showUserDashboard() {
        System.out.println("\n===== User Dashboard =====");
        System.out.println("1. View Details");
        System.out.println("2. View Products");
        System.out.println("3. View Cart");
        System.out.println("4. Edit Cart");
        System.out.println("5. Check Out");
        System.out.println("6. Order History");
        System.out.println("7. Logout");
        System.out.print("Enter your choice: ");
    }
    
    private static void editCart(String username) throws IOException, InterruptedException {
        int choice;
        do {
            System.out.println("\nEdit Cart Menu:");
            System.out.println("1: Delete a specific product from the cart");
            System.out.println("2: Delete all products from the cart");
            System.out.println("3: Go back to the main menu");
            System.out.print("Enter your choice: ");

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter the Product ID to remove: ");
                    int productId = sc.nextInt();
                    CartService.removeProduct(username, productId);
                }
                case 2 -> {
                    System.out.println("Are you sure you want to delete all products from the cart? (yes/no)");
                    String confirm = sc.next();
                    if (confirm.equalsIgnoreCase("yes")) {
                        CartService.removeAllProduct(username);
                    } else {
                        System.out.println("Operation canceled.");
                    }
                }
                case 3 -> System.out.println("Returning to main menu...");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 3);
    }
}
