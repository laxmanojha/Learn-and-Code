package com.itt.tubmlr.util;

import java.util.Scanner;

public class UserInputHandler {

    public String getBlogName(Scanner scanner) {
        System.out.print("Enter the Tumblr blog name: ");
        return scanner.nextLine().trim();
    }

    public int[] getPostRange(Scanner scanner) {
        System.out.print("Enter the post range (start-end): ");
        String[] parts = scanner.nextLine().trim().split("-");

        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid range format. Please use 'start-end' format.");
        }

        int start = Integer.parseInt(parts[0]);
        int end = Integer.parseInt(parts[1]);

        if (start < 0 || end <= start) {
            throw new IllegalArgumentException("Invalid range values. Ensure start < end and values are positive.");
        }

        return new int[]{start, end};
    }
}
