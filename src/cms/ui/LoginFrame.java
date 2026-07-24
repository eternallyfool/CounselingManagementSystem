/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package cms.ui;

import cms.io.UserFileRepository;
import cms.core.User;
import cms.util.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(LoginFrame.class.getName());
    private UserFileRepository userRepo;

    public LoginFrame() {
        setUndecorated(true);
        initComponents();
        userRepo = new UserFileRepository();
        applyVintageDesign();
    }

            private void applyVintageDesign() {
        // frame setup
        setTitle("APU Counseling System");
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 24, 24));
        
        getContentPane().removeAll();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UIUtils.VINTAGE_BG);
        
        final Point[] dragPoint = new Point[1];
        MouseAdapter dragListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { dragPoint[0] = e.getPoint(); }
            @Override
            public void mouseDragged(MouseEvent e) {
                Point current = getLocation();
                int xDiff = e.getX() - dragPoint[0].x;
                int yDiff = e.getY() - dragPoint[0].y;
                setLocation(current.x + xDiff, current.y + yDiff);
            }
        };
        getContentPane().addMouseListener(dragListener);
        getContentPane().addMouseMotionListener(dragListener);

        rightPanel.setBackground(UIUtils.VINTAGE_PANEL);
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(15, 60, 15, 60));
        rightPanel.addMouseListener(dragListener);
        rightPanel.addMouseMotionListener(dragListener);

        // close button
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        topBar.setOpaque(false);
        btnClose.setText("×");
        btnClose.setFont(new Font("Georgia", Font.BOLD, 24));
        btnClose.setForeground(UIUtils.VINTAGE_GOLD);
        btnClose.setBorderPainted(false);
        btnClose.setFocusPainted(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        topBar.add(btnClose);
        rightPanel.add(topBar, BorderLayout.NORTH);

        // center
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 0;

        // title
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 10, 0);
        lblTitle.setText("Counseling Management");
        lblTitle.setFont(new Font("Playfair Display", Font.BOLD, 34));
        lblTitle.setForeground(UIUtils.VINTAGE_GOLD);
        formPanel.add(lblTitle, gbc);

        // decorate line under the bar
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 25, 0);
        JSeparator sep = new JSeparator();
        sep.setForeground(UIUtils.VINTAGE_GOLD);
        sep.setPreferredSize(new Dimension(280, 2));
        formPanel.add(sep, gbc);

        // label for username
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 0, 4, 0);
        lblUsername.setText("USERNAME");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblUsername.setForeground(UIUtils.VINTAGE_CREAM);
        formPanel.add(lblUsername, gbc);

        // field for username
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 15, 0);
        UIUtils.styleTextField(txtUsername);
        txtUsername.setPreferredSize(new Dimension(320, 42));
        formPanel.add(txtUsername, gbc);

        // label for password
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 0, 4, 0);
        lblPassword.setText("PASSWORD");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblPassword.setForeground(UIUtils.VINTAGE_CREAM);
        formPanel.add(lblPassword, gbc);

        // field for password
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 15, 0);
        UIUtils.stylePasswordField(txtPassword);
        txtPassword.setPreferredSize(new Dimension(320, 42));
        formPanel.add(txtPassword, gbc);

        // label for error
        gbc.gridy = 6;
        gbc.insets = new Insets(5, 0, 10, 0);
        lblError.setText(" ");
        lblError.setFont(new Font("Georgia", Font.ITALIC, 13));
        lblError.setForeground(UIUtils.VINTAGE_ROSE);
        formPanel.add(lblError, gbc);

        // function to login
        gbc.gridy = 7;
        gbc.insets = new Insets(15, 0, 10, 0);
        UIUtils.styleActionButton(btnLogin, UIUtils.VINTAGE_GOLD);
        btnLogin.setText("Enter System");
        btnLogin.setFont(new Font("Playfair Display", Font.BOLD, 16));
        btnLogin.setPreferredSize(new Dimension(320, 46));
        formPanel.add(btnLogin, gbc);
        
        rightPanel.add(formPanel, BorderLayout.CENTER);
        
        //footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        JLabel lblFooter = new JLabel("© APU Counseling Services");
        lblFooter.setFont(new Font("Georgia", Font.ITALIC, 11));
        lblFooter.setForeground(UIUtils.VINTAGE_CREAM.darker());
        footerPanel.add(lblFooter);
        rightPanel.add(footerPanel, BorderLayout.SOUTH);


        getContentPane().add(rightPanel, BorderLayout.CENTER);

        this.setLocationRelativeTo(null);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rightPanel = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        lblTitle = new javax.swing.JLabel();
        lblUsername = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        btnLogin = new javax.swing.JButton();
        lblError = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnClose.setText("X");
        btnClose.addActionListener(this::btnCloseActionPerformed);

        lblTitle.setText("Welcome to Counseling Management System");

        lblUsername.setText("Username:");

        txtUsername.addActionListener(this::txtUsernameActionPerformed);

        lblPassword.setText("Password:");

        btnLogin.setText("Login");
        btnLogin.addActionListener(this::btnLoginActionPerformed);

        lblError.setText("Error");

        txtPassword.addActionListener(this::txtPasswordActionPerformed);

        javax.swing.GroupLayout rightPanelLayout = new javax.swing.GroupLayout(rightPanel);
        rightPanel.setLayout(rightPanelLayout);
        rightPanelLayout.setHorizontalGroup(
            rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(rightPanelLayout.createSequentialGroup()
                        .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(rightPanelLayout.createSequentialGroup()
                                .addGap(118, 118, 118)
                                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblTitle)
                                    .addGroup(rightPanelLayout.createSequentialGroup()
                                        .addGap(89, 89, 89)
                                        .addComponent(btnLogin))
                                    .addGroup(rightPanelLayout.createSequentialGroup()
                                        .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblUsername)
                                            .addComponent(lblPassword))
                                        .addGap(133, 133, 133)
                                        .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(rightPanelLayout.createSequentialGroup()
                                .addGap(231, 231, 231)
                                .addComponent(lblError)))
                        .addGap(0, 222, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, rightPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnClose)))
                .addContainerGap())
        );
        rightPanelLayout.setVerticalGroup(
            rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addComponent(btnClose)
                .addGap(57, 57, 57)
                .addComponent(lblTitle)
                .addGap(45, 45, 45)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblUsername)
                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(45, 45, 45)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPassword)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                .addComponent(btnLogin)
                .addGap(18, 18, 18)
                .addComponent(lblError)
                .addGap(29, 29, 29))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(132, 132, 132)
                .addComponent(rightPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rightPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    
    
    private void txtUsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsernameActionPerformed
        txtPassword.requestFocus();
    }//GEN-LAST:event_txtUsernameActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnCloseActionPerformed

    private void txtPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPasswordActionPerformed
        btnLogin.doClick();
    }//GEN-LAST:event_txtPasswordActionPerformed

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Please fill in all fields.");
            return;
        }

        // authenticate using users repository
        User user = userRepo.findByUsername(username);

        if (user == null || !user.checkPassword(password)) {
            lblError.setText("Invalid username or password.");
            txtPassword.setText("");
        } else if (!user.isActive()) {
            lblError.setText("Account is inactive. Contact Admin.");
        } else {
            lblError.setText("Login successful! Loading...");
            lblError.setForeground(UIUtils.VINTAGE_GOLD);
            
            SwingUtilities.invokeLater(() -> {
                JFrame dashboard = null;
                switch (user.getRole()) {
                    case "Admin":
                        dashboard = new AdminDashboard(user);
                        break;
                    case "Counselor":
                        dashboard = new CounselorDashboard(user);
                        break;
                    case "Receptionist":
                        dashboard = new ReceptionistDashboard(user);
                        break;
                    case "Student":
                        dashboard = new StudentDashboard(user);
                        break;
                }
                
                if (dashboard != null) {
                    dashboard.setVisible(true);
                    dispose();
                }
            });
        } 
    }//GEN-LAST:event_btnLoginActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
         try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnLogin;
    private javax.swing.JLabel lblError;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
