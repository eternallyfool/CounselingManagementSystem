/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.core;

public abstract class User {

    public static final String STATUS_ACTIVE = "Active";
    public static final String STATUS_INACTIVE = "Inactive";

    private String userId;
    private String username;
    private String password;
    private String role;
    private String fullName;
    private String status;

    public User(String userId, String username, String password, String role, String fullName) {
        this(userId, username, password, role, fullName, STATUS_ACTIVE);
    }

    public User(String userId, String username, String password, String role, String fullName, String status) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isActive() {
        return STATUS_ACTIVE.equalsIgnoreCase(status);
    }

    public boolean checkPassword(String enteredPassword) {
        return password != null && password.equals(enteredPassword);
    }

    public String toDataString() {
        return userId + "|" + username + "|" + password + "|" + role + "|" + fullName + "|" + status;
    }

    public String getDisplayName() {
        return fullName + " (" + role + ")";
    }

    public abstract String getDashboardTitle();
}