package com.itt.ecommerce.dto;

public class UserDto {
    private int id;
    private String fullName;
    private String userName;
    private String password;

    public UserDto(String userName) {
        this.userName = userName;
    }
    
    public UserDto(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public UserDto(String fullName, String userName, String password) {
        this.fullName = fullName;
        this.userName = userName;
        this.password = password;
    }

    public UserDto(int id, String fullName, String userName, String password) {
        this.id = id;
        this.fullName = fullName;
        this.userName = userName;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
    
    public void setPassword(String hashedPassword) {
        this.password = hashedPassword;
    }

    @Override
    public String toString() {
        return "UserDto [id=" + id + ", fullName=" + fullName + ", userName=" + userName + ", password=" + password + "]";
    }
}
