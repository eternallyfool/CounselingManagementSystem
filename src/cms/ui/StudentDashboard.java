/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.ui;

import cms.core.User;
import javax.swing.*;
import java.awt.*;

public class StudentDashboard extends JFrame {

    private User user;

    public StudentDashboard(User user) {
        this.user = user;

        setTitle("Student Dashboard");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel lblWelcome = new JLabel("Welcome Student: " + user.getFullName(), SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 18));

        JButton btnQueue = new JButton("View Queue Number");
        JButton btnBook = new JButton("Book / Reschedule / Cancel Appointment");
        JButton btnHistory = new JButton("View Appointment History");
        JButton btnProfiles = new JButton("View Counselor Profiles");
        JButton btnLogout = new JButton("Logout");

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.add(btnQueue);
        panel.add(btnBook);
        panel.add(btnHistory);
        panel.add(btnProfiles);
        panel.add(btnLogout);

        add(lblWelcome, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
    }
}
