/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.ui;

import cms.core.User;
import javax.swing.*;
import java.awt.*;

public class ReceptionistDashboard extends JFrame {

    private User user;

    public ReceptionistDashboard(User user) {
        this.user = user;

        setTitle("Receptionist Dashboard");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel lblWelcome = new JLabel("Welcome Receptionist: " + user.getFullName(), SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 18));

        //add features with JButton like the example below (logout is one of the features btw)
        JButton btnLogout = new JButton("Logout");

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        
        //add panel for the new features like example below
        panel.add(btnLogout);

        add(lblWelcome, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
    }
}
