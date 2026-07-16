/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.io;

import cms.core.*;
import java.util.ArrayList;
import java.util.List;

public class UserFileRepository {

    private final String filePath = "data/users.txt";

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        List<String> lines = FileManager.readLines(filePath);

        for (String line : lines) {
            String[] parts = line.split("\\|");

            if (parts.length == 5) {
                String userId = parts[0];
                String username = parts[1];
                String password = parts[2];
                String role = parts[3];
                String fullName = parts[4];

                switch (role) {
                    case "Admin":
                        users.add(new Admin(userId, username, password, fullName));
                        break;

                    case "Counselor":
                        users.add(new Counselor(userId, username, password, fullName));
                        break;

                    case "Receptionist":
                        users.add(new Receptionist(userId, username, password, fullName));
                        break;

                    case "Student":
                        users.add(new Student(userId, username, password, fullName));
                        break;
                }
            }
        }

        return users;
    }
}
