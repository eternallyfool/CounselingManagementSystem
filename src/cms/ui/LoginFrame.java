/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.ui;

import cms.core.AuthService;
import cms.core.User;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private AuthService authService;

    public LoginFrame() {
        authService = new AuthService();

        setTitle("Counseling Management System - Login");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel lblTitle = new JLabel("Counseling Management System", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel lblUsername = new JLabel("Username:");
        JLabel lblPassword = new JLabel("Password:");

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();

        btnLogin = new JButton("Login");

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.add(lblUsername);
        formPanel.add(txtUsername);
        formPanel.add(lblPassword);
        formPanel.add(txtPassword);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnLogin);

        setLayout(new BorderLayout(10, 10));
        add(lblTitle, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        btnLogin.addActionListener(e -> login());
    }

    private void login() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.");
            return;
        }

        User user = authService.login(username, password);

        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid username or password.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Welcome, " + user.getFullName());

        this.dispose();

        switch (user.getRole()) {
            case "Admin":
                new AdminDashboard(user).setVisible(true);
                break;

            case "Counselor":
                new CounselorDashboard(user).setVisible(true);
                break;

            case "Receptionist":
                new ReceptionistDashboard(user).setVisible(true);
                break;

            case "Student":
                new StudentDashboard(user).setVisible(true);
                break;

            default:
                JOptionPane.showMessageDialog(this, "Unknown user role.");
        }
    }
}
