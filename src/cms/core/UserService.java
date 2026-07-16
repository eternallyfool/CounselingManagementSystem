/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.core;

import cms.exception.DataNotFoundException;
import cms.exception.InvalidInputException;
import cms.io.UserFileRepository;
import cms.util.ValidationUtil;
import java.util.List;

public class UserService {

    private UserFileRepository userRepository;

    public UserService() {
        userRepository = new UserFileRepository();
    }

    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    public User findById(String userId) {
        return userRepository.findById(userId);
    }

    public User createUser(String username, String password, String role, String fullName)
            throws InvalidInputException {
        validateUserFields(username, password, role, fullName);

        if (userRepository.findByUsername(username) != null) {
            throw new InvalidInputException("Username already exists.");
        }

        String userId = userRepository.getNextUserId();
        User user = buildUser(userId, username.trim(), password, role, fullName.trim(), User.STATUS_ACTIVE);
        userRepository.addUser(user);
        return user;
    }

    public void updateUser(User user) throws DataNotFoundException {
        if (!userRepository.updateUser(user)) {
            throw new DataNotFoundException("User was not found.");
        }
    }

    public void setUserStatus(String userId, String status) throws DataNotFoundException, InvalidInputException {
        if (!User.STATUS_ACTIVE.equals(status) && !User.STATUS_INACTIVE.equals(status)) {
            throw new InvalidInputException("Status must be Active or Inactive.");
        }

        User user = userRepository.findById(userId);

        if (user == null) {
            throw new DataNotFoundException("User was not found.");
        }

        user.setStatus(status);
        updateUser(user);
    }

    private void validateUserFields(String username, String password, String role, String fullName)
            throws InvalidInputException {
        if (ValidationUtil.isEmpty(username) || ValidationUtil.isEmpty(password)
                || ValidationUtil.isEmpty(role) || ValidationUtil.isEmpty(fullName)) {
            throw new InvalidInputException("Please fill in all user fields.");
        }

        if (containsSeparator(username, password, role, fullName)) {
            throw new InvalidInputException("The | symbol cannot be used in saved data.");
        }

        if (!role.equals("Admin") && !role.equals("Counselor")
                && !role.equals("Receptionist") && !role.equals("Student")) {
            throw new InvalidInputException("Invalid user role.");
        }
    }

    private boolean containsSeparator(String... values) {
        for (String value : values) {
            if (ValidationUtil.containsSeparator(value)) {
                return true;
            }
        }

        return false;
    }

    private User buildUser(String userId, String username, String password, String role,
            String fullName, String status) {
        switch (role) {
            case "Admin":
                return new Admin(userId, username, password, fullName, status);
            case "Counselor":
                return new Counselor(userId, username, password, fullName, status);
            case "Receptionist":
                return new Receptionist(userId, username, password, fullName, status);
            case "Student":
                return new Student(userId, username, password, fullName, status);
            default:
                throw new IllegalArgumentException("Unsupported role: " + role);
        }
    }
}
