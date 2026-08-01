/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.io;

import cms.core.*;
import cms.util.IdGenerator;
import java.util.ArrayList;
import java.util.List;

public class UserFileRepository {

    private final String filePath = "data/users.txt";

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        List<String> lines = FileManager.readLines(filePath);

        for (String line : lines) {
            String[] parts = line.split("\\|", -1);

            if (parts.length >= 5) {
                String userId = parts[0];
                String username = parts[1];
                String password = parts[2];
                String role = parts[3];
                String fullName = parts[4];
                String status = parts.length >= 6 ? parts[5] : User.STATUS_ACTIVE;

                switch (role) {
                    case "Admin":
                        users.add(new Admin(userId, username, password, fullName, status));
                        break;

                    case "Counselor":
                        users.add(new Counselor(userId, username, password, fullName, status));
                        break;

                    case "Receptionist":
                        users.add(new Receptionist(userId, username, password, fullName, status));
                        break;

                    case "Student":
                        users.add(new Student(userId, username, password, fullName, status));
                        break;
                }
            }
        }

        return users;
    }

    public User findById(String userId) {
        for (User user : getAllUsers()) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }

        return null;
    }

    public User findByUsername(String username) {
        for (User user : getAllUsers()) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }

        return null;
    }

    public List<User> findByRole(String role) {
        List<User> result = new ArrayList<>();

        for (User user : getAllUsers()) {
            if (user.getRole().equals(role)) {
                result.add(user);
            }
        }

        return result;
    }

    public void addUser(User user) {
        FileManager.appendLine(filePath, user.toDataString());
    }

    public boolean updateUser(User updatedUser) {
        List<User> users = getAllUsers();
        List<String> lines = new ArrayList<>();
        boolean updated = false;

        for (User user : users) {
            if (user.getUserId().equals(updatedUser.getUserId())) {
                lines.add(updatedUser.toDataString());
                updated = true;
            } else {
                lines.add(user.toDataString());
            }
        }

        if (updated) {
            FileManager.writeLines(filePath, lines);
        }

        return updated;
    }

    public String getNextUserId() {
        List<String> ids = new ArrayList<>();

        for (User user : getAllUsers()) {
            ids.add(user.getUserId());
        }

        return IdGenerator.generateNextId("U", ids);
    }
    public boolean deleteUser(String userId) {
    List<User> users = getAllUsers();
    List<String> lines = new ArrayList<>();
    boolean deleted = false;

    for (User user : users) {
        if (user.getUserId().equals(userId)) {
            deleted = true; // skip this user (effectively deleting)
        } else {
            lines.add(user.toDataString());
        }
    }

    if (deleted) {
        FileManager.writeLines(filePath, lines);
    }

    return deleted;
}

}
