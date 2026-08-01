/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package cms.ui;

import cms.core.User;
import cms.core.UserService;
import cms.core.Roster;
import cms.core.RosterService;
import cms.core.ReportSummary;
import cms.core.ReportService;
import cms.io.AppointmentFileRepository;
import cms.exception.InvalidInputException;
import cms.exception.DataNotFoundException;
import cms.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

public class AdminDashboard extends javax.swing.JFrame {

    private User loggedInAdmin;
    private UserService userService;
    private RosterService rosterService;
    private ReportService reportService;
    private AppointmentFileRepository apptRepo;

    public AdminDashboard(User admin) {
        setUndecorated(true);
        initComponents();
        
        this.loggedInAdmin = admin;
        this.userService = new UserService();
        this.rosterService = new RosterService();
        this.reportService = new ReportService();
        this.apptRepo = new AppointmentFileRepository();
        
        applyDesign();
        loadDashboardStats();
        loadUsersTable();
        loadRostersTable();
        loadReportsTable();
    }
    
    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(35);
        table.setForeground(UIUtils.VINTAGE_CREAM); 
        table.setBackground(UIUtils.VINTAGE_PANEL); 
        table.setSelectionBackground(UIUtils.VINTAGE_GOLD);
        table.setSelectionForeground(UIUtils.VINTAGE_PANEL);
        table.setGridColor(UIUtils.VINTAGE_SHADOW);
        
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(UIUtils.VINTAGE_SHADOW); 
        table.getTableHeader().setForeground(UIUtils.VINTAGE_GOLD); 
        table.getTableHeader().setBorder(null);
    }

    private void loadDashboardStats() {
        List<User> users = userService.getAllUsers();
        long totalUsers = users.size();
        long totalStudents = users.stream().filter(u -> u.getRole().equals("Student")).count();
        long totalCounselors = users.stream().filter(u -> u.getRole().equals("Counselor")).count();
        long totalAppts = apptRepo.getAllAppointments().size();

        setupStatCard(card1, "TOTAL USERS", String.valueOf(totalUsers), UIUtils.VINTAGE_GOLD);
        setupStatCard(card2, "STUDENTS", String.valueOf(totalStudents), UIUtils.VINTAGE_CREAM);
        setupStatCard(card3, "COUNSELORS", String.valueOf(totalCounselors), UIUtils.VINTAGE_GOLD);
        setupStatCard(card4, "APPOINTMENTS", String.valueOf(totalAppts), UIUtils.VINTAGE_CREAM);
    }

    private void setupStatCard(JPanel card, String title, String value, Color accentColor) {
        card.removeAll();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(UIUtils.VINTAGE_CREAM.darker());
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 48));
        lblValue.setForeground(accentColor);
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        lblValue.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void loadUsersTable() {
        String[] columns = {"User ID", "Username", "Role", "Full Name", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        for (User user : userService.getAllUsers()) {
            model.addRow(new Object[]{ user.getUserId(), user.getUsername(), user.getRole(), user.getFullName(), user.getStatus() });
        }
        tblUsers.setModel(model);
    }

    private void loadRostersTable() {
        String[] columns = {"Roster ID", "Counselor ID", "Date", "Start", "End", "Room", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        for (Roster r : rosterService.getAllRosters()) {
            model.addRow(new Object[]{ r.getRosterId(), r.getCounselorId(), r.getWorkDate(), r.getStartTime(), r.getEndTime(), r.getRoom(), r.getAvailabilityStatus() });
        }
        tblRosters.setModel(model);
    }

    private void loadReportsTable() {
        String[] columns = {"Report ID", "Type", "Period Start", "Period End", "Total Appts", "Completed", "Cancelled"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        for (ReportSummary r : reportService.getSavedReports()) {
            model.addRow(new Object[]{ r.getReportId(), r.getReportType(), r.getPeriodStart(), r.getPeriodEnd(), r.getTotalAppointments(), r.getCompleted(), r.getCancelled() });
        }
        tblReports.setModel(model);
    }

    private void setActiveButton(JButton activeBtn) {
        JButton[] buttons = {btnDashboard, btnUsers, btnRosters, btnReports};
        for (JButton btn : buttons) {
            btn.setForeground(UIUtils.VINTAGE_CREAM.darker());
            btn.setBorder(new EmptyBorder(10, 15, 10, 20));
        }
        activeBtn.setForeground(UIUtils.VINTAGE_GOLD);
        activeBtn.setBorder(new CompoundBorder(new MatteBorder(0, 5, 0, 0, UIUtils.VINTAGE_GOLD), new EmptyBorder(10, 10, 10, 20)));
    }

    private void updateThemeColors() {
        getContentPane().setBackground(UIUtils.VINTAGE_BG);
        sidebarPanel.setBackground(UIUtils.VINTAGE_PANEL);
        headerPanel.setBackground(UIUtils.VINTAGE_BG);
        contentPanel.setBackground(UIUtils.VINTAGE_BG);
        panelDashboard.setBackground(UIUtils.VINTAGE_BG);
        panelUsers.setBackground(UIUtils.VINTAGE_BG);
        panelRosters.setBackground(UIUtils.VINTAGE_BG);
        panelReports.setBackground(UIUtils.VINTAGE_BG);
        card1.setBackground(UIUtils.VINTAGE_PANEL); 
        card2.setBackground(UIUtils.VINTAGE_PANEL);
        card3.setBackground(UIUtils.VINTAGE_PANEL); 
        card4.setBackground(UIUtils.VINTAGE_PANEL);
        lblHeaderTitle.setForeground(UIUtils.VINTAGE_GOLD);
        lblAdminName.setForeground(UIUtils.VINTAGE_CREAM.darker());
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void styleInputField(JTextField field) {
        field.setBackground(UIUtils.VINTAGE_SHADOW);
        field.setForeground(UIUtils.VINTAGE_CREAM);
        field.setCaretColor(UIUtils.VINTAGE_GOLD);
        field.setBorder(new MatteBorder(0, 0, 2, 0, UIUtils.VINTAGE_GOLD));
        field.setPreferredSize(new Dimension(200, 30));
    }

        private void applyDesign() {
        setTitle("APU CMS - Admin Dashboard");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        
        getContentPane().removeAll();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(UIUtils.VINTAGE_BG);
        
        // window drag
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
        
        // stucture panel methodically
        sidebarPanel.setBackground(UIUtils.VINTAGE_PANEL);
        headerPanel.setBackground(UIUtils.VINTAGE_BG);
        contentPanel.setBackground(UIUtils.VINTAGE_BG);
        
        getContentPane().add(sidebarPanel, BorderLayout.WEST);
        getContentPane().add(headerPanel, BorderLayout.NORTH);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        
        
        headerPanel.addMouseListener(dragListener);
        headerPanel.addMouseMotionListener(dragListener);
        sidebarPanel.addMouseListener(dragListener);
        sidebarPanel.addMouseMotionListener(dragListener);
        
        updateThemeColors();
        setupSidebar();
        setupContent();
    }

    private void setupSidebar() {
        sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        sidebarPanel.setBackground(UIUtils.VINTAGE_ROSE);
        sidebarPanel.removeAll();

        JLabel lblLogo = new JLabel("CMS");
        lblLogo.setFont(new Font("Georgia", Font.BOLD, 40));
        lblLogo.setForeground(UIUtils.VINTAGE_GOLD);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblLogo.setBorder(new EmptyBorder(0, 0, 30, 0));
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        sidebarPanel.add(lblLogo);

        UIUtils.styleMenuButton(btnDashboard);
        UIUtils.styleMenuButton(btnUsers);
        UIUtils.styleMenuButton(btnRosters);
        UIUtils.styleMenuButton(btnReports);
        UIUtils.styleMenuButton(btnLogout);
        
        btnDashboard.setText(" DASHBOARD");
        btnUsers.setText(" MANAGE USERS");
        btnRosters.setText(" STAFF ROSTERS");
        btnReports.setText(" REPORTS");
        btnLogout.setText(" LOGOUT");

        btnDashboard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnUsers.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnRosters.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnReports.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        sidebarPanel.add(btnDashboard);
        sidebarPanel.add(btnUsers);
        sidebarPanel.add(btnRosters);
        sidebarPanel.add(btnReports);
        sidebarPanel.add(Box.createVerticalGlue());
        
        String spacedName = loggedInAdmin.getFullName().replaceAll("([a-z])([A-Z])", "$1 $2");
        lblAdminName.setText(spacedName);
        lblAdminName.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblAdminName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAdminName.setForeground(UIUtils.VINTAGE_CREAM.darker());
        lblAdminName.setBorder(new EmptyBorder(10, 20, 10, 20));
        sidebarPanel.add(lblAdminName);
        sidebarPanel.add(btnLogout);

        setActiveButton(btnDashboard);
    }

    private void setupContent() {
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 30, 25));
        lblHeaderTitle.setFont(new Font("Georgia", Font.BOLD, 28));
        lblHeaderTitle.setForeground(UIUtils.VINTAGE_GOLD);
        
        UIUtils.styleActionButton(btnAddUser, UIUtils.VINTAGE_GOLD);
        UIUtils.styleActionButton(btnToggleStatus, UIUtils.VINTAGE_CREAM);
        UIUtils.styleActionButton(btnAddRoster, UIUtils.VINTAGE_GOLD);
        UIUtils.styleActionButton(btnGenerateReport, UIUtils.VINTAGE_GOLD);
        UIUtils.styleActionButton(btnDeleteUser, UIUtils.VINTAGE_GOLD);

        
        styleTable(tblUsers);
        styleTable(tblRosters);
        styleTable(tblReports);
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sidebarPanel = new javax.swing.JPanel();
        btnDashboard = new javax.swing.JButton();
        btnUsers = new javax.swing.JButton();
        btnRosters = new javax.swing.JButton();
        btnReports = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        lblAdminName = new javax.swing.JLabel();
        headerPanel = new javax.swing.JPanel();
        lblHeaderTitle = new javax.swing.JLabel();
        contentPanel = new javax.swing.JPanel();
        panelDashboard = new javax.swing.JPanel();
        card1 = new javax.swing.JPanel();
        card2 = new javax.swing.JPanel();
        card3 = new javax.swing.JPanel();
        card4 = new javax.swing.JPanel();
        panelUsers = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        btnAddUser = new javax.swing.JButton();
        btnToggleStatus = new javax.swing.JButton();
        btnDeleteUser = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblUsers = new javax.swing.JTable();
        panelRosters = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        btnAddRoster = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblRosters = new javax.swing.JTable();
        panelReports = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        btnGenerateReport = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblReports = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnDashboard.setText("Dashboard");
        btnDashboard.addActionListener(this::btnDashboardActionPerformed);

        btnUsers.setText("Users");
        btnUsers.addActionListener(this::btnUsersActionPerformed);

        btnRosters.setText("Rosters");
        btnRosters.addActionListener(this::btnRostersActionPerformed);

        btnReports.setText("Reports");
        btnReports.addActionListener(this::btnReportsActionPerformed);

        btnLogout.setText("Logout");
        btnLogout.addActionListener(this::btnLogoutActionPerformed);

        lblAdminName.setText("jLabel1");

        javax.swing.GroupLayout sidebarPanelLayout = new javax.swing.GroupLayout(sidebarPanel);
        sidebarPanel.setLayout(sidebarPanelLayout);
        sidebarPanelLayout.setHorizontalGroup(
            sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidebarPanelLayout.createSequentialGroup()
                .addGroup(sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sidebarPanelLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnLogout)
                            .addGroup(sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(btnReports, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                                .addComponent(btnRosters, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnUsers, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(btnDashboard)))
                    .addGroup(sidebarPanelLayout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(lblAdminName)))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        sidebarPanelLayout.setVerticalGroup(
            sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidebarPanelLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(lblAdminName)
                .addGap(40, 40, 40)
                .addComponent(btnDashboard)
                .addGap(18, 18, 18)
                .addComponent(btnUsers)
                .addGap(18, 18, 18)
                .addComponent(btnRosters)
                .addGap(18, 18, 18)
                .addComponent(btnReports)
                .addGap(18, 18, 18)
                .addComponent(btnLogout)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblHeaderTitle.setText("Admin");

        javax.swing.GroupLayout headerPanelLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, headerPanelLayout.createSequentialGroup()
                .addContainerGap(72, Short.MAX_VALUE)
                .addComponent(lblHeaderTitle)
                .addGap(70, 70, 70))
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, headerPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblHeaderTitle)
                .addContainerGap())
        );

        contentPanel.setLayout(new java.awt.CardLayout());

        panelDashboard.setLayout(new java.awt.GridLayout(1, 2, 15, 0));

        javax.swing.GroupLayout card1Layout = new javax.swing.GroupLayout(card1);
        card1.setLayout(card1Layout);
        card1Layout.setHorizontalGroup(
            card1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 138, Short.MAX_VALUE)
        );
        card1Layout.setVerticalGroup(
            card1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 410, Short.MAX_VALUE)
        );

        panelDashboard.add(card1);

        javax.swing.GroupLayout card2Layout = new javax.swing.GroupLayout(card2);
        card2.setLayout(card2Layout);
        card2Layout.setHorizontalGroup(
            card2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 138, Short.MAX_VALUE)
        );
        card2Layout.setVerticalGroup(
            card2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 410, Short.MAX_VALUE)
        );

        panelDashboard.add(card2);

        javax.swing.GroupLayout card3Layout = new javax.swing.GroupLayout(card3);
        card3.setLayout(card3Layout);
        card3Layout.setHorizontalGroup(
            card3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 138, Short.MAX_VALUE)
        );
        card3Layout.setVerticalGroup(
            card3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 410, Short.MAX_VALUE)
        );

        panelDashboard.add(card3);

        javax.swing.GroupLayout card4Layout = new javax.swing.GroupLayout(card4);
        card4.setLayout(card4Layout);
        card4Layout.setHorizontalGroup(
            card4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 138, Short.MAX_VALUE)
        );
        card4Layout.setVerticalGroup(
            card4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 410, Short.MAX_VALUE)
        );

        panelDashboard.add(card4);

        contentPanel.add(panelDashboard, "cardDashboard");

        btnAddUser.setText("Add User");
        btnAddUser.addActionListener(this::btnAddUserActionPerformed);

        btnToggleStatus.setText("Toggle Status");
        btnToggleStatus.addActionListener(this::btnToggleStatusActionPerformed);

        btnDeleteUser.setText("Delete User");
        btnDeleteUser.addActionListener(this::btnDeleteUserActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnAddUser)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                .addComponent(btnDeleteUser)
                .addGap(33, 33, 33)
                .addComponent(btnToggleStatus)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddUser)
                    .addComponent(btnToggleStatus)
                    .addComponent(btnDeleteUser))
                .addContainerGap(65, Short.MAX_VALUE))
        );

        jScrollPane1.setPreferredSize(new java.awt.Dimension(50, 50));

        tblUsers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblUsers);

        javax.swing.GroupLayout panelUsersLayout = new javax.swing.GroupLayout(panelUsers);
        panelUsers.setLayout(panelUsersLayout);
        panelUsersLayout.setHorizontalGroup(
            panelUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelUsersLayout.createSequentialGroup()
                .addGap(93, 93, 93)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(145, Short.MAX_VALUE))
            .addGroup(panelUsersLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelUsersLayout.setVerticalGroup(
            panelUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelUsersLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
                .addContainerGap())
        );

        contentPanel.add(panelUsers, "cardUsers");

        btnAddRoster.setText("Add Roster");
        btnAddRoster.addActionListener(this::btnAddRosterActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(183, Short.MAX_VALUE)
                .addComponent(btnAddRoster)
                .addGap(169, 169, 169))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addComponent(btnAddRoster)
                .addGap(20, 20, 20))
        );

        jScrollPane2.setPreferredSize(new java.awt.Dimension(50, 50));

        tblRosters.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tblRosters);

        javax.swing.GroupLayout panelRostersLayout = new javax.swing.GroupLayout(panelRosters);
        panelRosters.setLayout(panelRostersLayout);
        panelRostersLayout.setHorizontalGroup(
            panelRostersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRostersLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 587, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(panelRostersLayout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelRostersLayout.setVerticalGroup(
            panelRostersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRostersLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                .addContainerGap())
        );

        contentPanel.add(panelRosters, "cardRosters");

        btnGenerateReport.setText("Generate Report");
        btnGenerateReport.addActionListener(this::btnGenerateReportActionPerformed);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(176, 176, 176)
                .addComponent(btnGenerateReport)
                .addContainerGap(176, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(btnGenerateReport)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jScrollPane3.setPreferredSize(new java.awt.Dimension(50, 50));

        tblReports.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(tblReports);

        javax.swing.GroupLayout panelReportsLayout = new javax.swing.GroupLayout(panelReports);
        panelReports.setLayout(panelReportsLayout);
        panelReportsLayout.setHorizontalGroup(
            panelReportsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelReportsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 587, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(panelReportsLayout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(84, Short.MAX_VALUE))
        );
        panelReportsLayout.setVerticalGroup(
            panelReportsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelReportsLayout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                .addContainerGap())
        );

        contentPanel.add(panelReports, "cardReports");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(sidebarPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(contentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(203, 203, 203))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(sidebarPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    
    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        this.dispose();
        new LoginFrame().setVisible(true);
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnDashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDashboardActionPerformed
        CardLayout cl = (CardLayout)(contentPanel.getLayout());
        cl.show(contentPanel, "cardDashboard");
        lblHeaderTitle.setText("DASHBOARD OVERVIEW");
        setActiveButton(btnDashboard);
    }//GEN-LAST:event_btnDashboardActionPerformed

    private void btnUsersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUsersActionPerformed
        CardLayout cl = (CardLayout)(contentPanel.getLayout());
        cl.show(contentPanel, "cardUsers");
        lblHeaderTitle.setText("MANAGE SYSTEM USERS");
        setActiveButton(btnUsers);
    }//GEN-LAST:event_btnUsersActionPerformed

    private void btnRostersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRostersActionPerformed
        CardLayout cl = (CardLayout)(contentPanel.getLayout());
        cl.show(contentPanel, "cardRosters");
        lblHeaderTitle.setText("MANAGE STAFF ROSTERS");
        setActiveButton(btnRosters);
    }//GEN-LAST:event_btnRostersActionPerformed

    private void btnReportsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportsActionPerformed
        CardLayout cl = (CardLayout)(contentPanel.getLayout());
        cl.show(contentPanel, "cardReports");
        lblHeaderTitle.setText("GENERATE & VIEW REPORTS");
        setActiveButton(btnReports);
    }//GEN-LAST:event_btnReportsActionPerformed

    private void btnAddUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddUserActionPerformed
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIUtils.VINTAGE_PANEL);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtUsername = new JTextField();
        JPasswordField txtPassword = new JPasswordField();
        JTextField txtFullName = new JTextField();
        String[] roles = {"Admin", "Counselor", "Receptionist", "Student"};
        JComboBox<String> cmbRole = new JComboBox<>(roles);

        styleInputField(txtUsername); styleInputField(txtPassword); styleInputField(txtFullName);
        
        
        
        JLabel lblUser = new JLabel("Username:"); lblUser.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblPass = new JLabel("Password"); lblPass.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblName = new JLabel("Full Name"); lblName.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblRole = new JLabel("Role:"); lblRole.setForeground(UIUtils.VINTAGE_CREAM);
        
        gbc.gridx = 0; gbc.gridy = 0; panel.add(lblUser, gbc);
        gbc.gridx = 1; panel.add(txtUsername, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(lblPass, gbc);
        gbc.gridx = 1; panel.add(txtPassword, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(lblName, gbc);
        gbc.gridx = 1; panel.add(txtFullName, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panel.add(lblRole, gbc);
        gbc.gridx = 1; panel.add(cmbRole, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "CREATE NEW USER", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                userService.createUser(
                    txtUsername.getText().trim(),
                    new String(txtPassword.getPassword()).trim(),
                    cmbRole.getSelectedItem().toString(),
                    txtFullName.getText().trim()
                );
                JOptionPane.showMessageDialog(this, "User created successfully!", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                loadUsersTable();
                loadDashboardStats();
            } catch (InvalidInputException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "VALIDATION ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnAddUserActionPerformed

    private void btnToggleStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToggleStatusActionPerformed
        int selectedRow = tblUsers.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user from the table first.", "NO SELECTION", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String userId = tblUsers.getValueAt(selectedRow, 0).toString();
        String currentStatus = tblUsers.getValueAt(selectedRow, 4).toString();
        String newStatus = currentStatus.equals(User.STATUS_ACTIVE) ? User.STATUS_INACTIVE : User.STATUS_ACTIVE;

        try {
            userService.setUserStatus(userId, newStatus);
            JOptionPane.showMessageDialog(this, "User status updated to " + newStatus, "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
            loadUsersTable();
        } catch (InvalidInputException | DataNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnToggleStatusActionPerformed

    private void btnAddRosterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddRosterActionPerformed
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIUtils.VINTAGE_PANEL);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtCounselorId = new JTextField("U2");
        JTextField txtDate = new JTextField("2026-06-10");
        JTextField txtStart = new JTextField("09:00");
        JTextField txtEnd = new JTextField("12:00");
        JTextField txtRoom = new JTextField("Room A");
        String[] statuses = {Roster.STATUS_AVAILABLE, Roster.STATUS_UNAVAILABLE, Roster.STATUS_FULL, Roster.STATUS_LEAVE};
        JComboBox<String> cmbStatus = new JComboBox<>(statuses);

        styleInputField(txtCounselorId); styleInputField(txtDate); styleInputField(txtStart); styleInputField(txtEnd); styleInputField(txtRoom);
        
        JLabel lblCoun = new JLabel("Counselor ID:"); lblCoun.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblDate = new JLabel("Date (yyyy-MM-dd):"); lblDate.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblStart = new JLabel("Start (HH:mm):"); lblStart.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblEnd = new JLabel("End (HH:mm):"); lblEnd.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblRoom = new JLabel("Room:"); lblRoom.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblStat = new JLabel("Status:"); lblStat.setForeground(UIUtils.VINTAGE_CREAM);
        
        gbc.gridx = 0; gbc.gridy = 0; panel.add(lblCoun, gbc);
        gbc.gridx = 1; panel.add(txtCounselorId, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(lblDate, gbc);
        gbc.gridx = 1; panel.add(txtDate, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(lblStart, gbc);
        gbc.gridx = 1; panel.add(txtStart, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panel.add(lblEnd, gbc);
        gbc.gridx = 1; panel.add(txtEnd, gbc);
        gbc.gridx = 0; gbc.gridy = 4; panel.add(lblRoom, gbc);
        gbc.gridx = 1; panel.add(txtRoom, gbc);
        gbc.gridx = 0; gbc.gridy = 5; panel.add(lblStat, gbc);
        gbc.gridx = 1; panel.add(cmbStatus, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "ASSIGN STAFF ROSTER", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                rosterService.addRoster(
                    txtCounselorId.getText().trim(),
                    txtDate.getText().trim(),
                    txtStart.getText().trim(),
                    txtEnd.getText().trim(),
                    txtRoom.getText().trim(),
                    cmbStatus.getSelectedItem().toString()
                );
                JOptionPane.showMessageDialog(this, "Roster assigned successfully!", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                loadRostersTable();
            } catch (InvalidInputException | DataNotFoundException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "VALIDATION ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnAddRosterActionPerformed

    private void btnGenerateReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerateReportActionPerformed
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIUtils.VINTAGE_PANEL);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] types = {"Daily", "Monthly", "Quarterly", "Yearly"};
        JComboBox<String> cmbType = new JComboBox<>(types);
        JTextField txtStart = new JTextField("2026-06-01");
        JTextField txtEnd = new JTextField("2026-06-30");

        styleInputField(txtStart); styleInputField(txtEnd);
        
        JLabel lblRep = new JLabel("Report Type:"); lblRep.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblStart = new JLabel("Start Date:"); lblStart.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblEnd = new JLabel("End Date:"); lblEnd.setForeground(UIUtils.VINTAGE_CREAM);

        gbc.gridx = 0; gbc.gridy = 0; panel.add(lblRep, gbc);
        gbc.gridx = 1; panel.add(cmbType, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(lblStart, gbc);
        gbc.gridx = 1; panel.add(txtStart, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(lblEnd, gbc);
        gbc.gridx = 1; panel.add(txtEnd, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "GENERATE REPORT", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                ReportSummary report = reportService.generateAndSaveReport(
                    cmbType.getSelectedItem().toString(),
                    txtStart.getText().trim(),
                    txtEnd.getText().trim(),
                    loggedInAdmin.getUserId()
                );
                
                String reportText = reportService.formatReport(report, loggedInAdmin.getFullName());
                JTextArea textArea = new JTextArea(reportText);
                textArea.setEditable(false);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
                JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "REPORT GENERATED", JOptionPane.INFORMATION_MESSAGE);
                
                loadReportsTable();
            } catch (InvalidInputException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "VALIDATION ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnGenerateReportActionPerformed

    private void btnDeleteUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteUserActionPerformed
    int selectedRow = tblUsers.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a user to delete.");
        return;
    }

    String userId = tblUsers.getValueAt(selectedRow, 0).toString();

    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to delete User ID: " + userId + "?",
        "Confirm Deletion",
        JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
        try {
            userService.deleteUser(userId);
            loadUsersTable(); // refresh table
            JOptionPane.showMessageDialog(this, "User deleted successfully.");
        } catch (DataNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "User not found.");
        }
    }
    }//GEN-LAST:event_btnDeleteUserActionPerformed
    
    
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
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddRoster;
    private javax.swing.JButton btnAddUser;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnDeleteUser;
    private javax.swing.JButton btnGenerateReport;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnReports;
    private javax.swing.JButton btnRosters;
    private javax.swing.JButton btnToggleStatus;
    private javax.swing.JButton btnUsers;
    private javax.swing.JPanel card1;
    private javax.swing.JPanel card2;
    private javax.swing.JPanel card3;
    private javax.swing.JPanel card4;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblAdminName;
    private javax.swing.JLabel lblHeaderTitle;
    private javax.swing.JPanel panelDashboard;
    private javax.swing.JPanel panelReports;
    private javax.swing.JPanel panelRosters;
    private javax.swing.JPanel panelUsers;
    private javax.swing.JPanel sidebarPanel;
    private javax.swing.JTable tblReports;
    private javax.swing.JTable tblRosters;
    private javax.swing.JTable tblUsers;
    // End of variables declaration//GEN-END:variables
}
