/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.ui;

import cms.core.User;
import javax.swing.*;
import java.awt.*;

public class CounselorDashboard extends JFrame {

    private User user;

    public CounselorDashboard(User user) {
        this.user = user;

        setTitle("Counselor Dashboard");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel lblWelcome = new JLabel("Welcome Counselor: " + user.getFullName(), SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 18));

        JButton btnRoster = new JButton("View Personal Roster");
        JButton btnAppointments = new JButton("View Assigned Appointments");
        JButton btnRecords = new JButton("Access Student Consultation Records");
        JButton btnNotes = new JButton("Add Consultation Notes and Recommendations");
        JButton btnLogout = new JButton("Logout");

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.add(btnRoster);
        panel.add(btnAppointments);
        panel.add(btnRecords);
        panel.add(btnNotes);
        panel.add(btnLogout);

        add(lblWelcome, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
    }
}