package com.itt.tubmlr.dto;

public class BlogInfoDto {

    private final String title;
    private final String description;
    private final String name;
    private final int totalPosts;

    public BlogInfoDto(String title, String description, String name, int totalPosts) {
        this.title = title;
        this.description = description;
        this.name = name;
        this.totalPosts = totalPosts;
    }

    public void displayInfo() {
        System.out.println("\nTitle: " + title);
        System.out.println("Name: " + name);
        System.out.println("Description: " + description);
        System.out.println("Total Posts: " + totalPosts + "\n");
    }
}
