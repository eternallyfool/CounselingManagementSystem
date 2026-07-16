/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.core;

import cms.io.UserFileRepository;
import java.util.List;

public class AuthService {

    private UserFileRepository userRepo;

    public AuthService() {
        userRepo = new UserFileRepository();
    }

    public User login(String username, String password) {
        List<User> users = userRepo.getAllUsers();

        for (User user : users) {
            if (user.getUsername().equals(username)
                    && user.getPassword().equals(password)) {
                return user;
            }
        }

        return null;
    }
}