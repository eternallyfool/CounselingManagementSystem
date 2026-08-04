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
import cms.core.Appointment;
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
    
    private void styleTableInteraction(JTable table) {
        table.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row > -1 && col > -1) {
                    Object value = table.getValueAt(row, col);
                    if (value != null && value.toString().length() > 20) {
                        table.setToolTipText(value.toString());
                    } else {
                        table.setToolTipText(null);
                    }
                }
            }
        });
        
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) { 
                    int row = table.getSelectedRow();
                    int col = table.getSelectedColumn();
                    if (row > -1 && col > -1) {
                        Object value = table.getValueAt(row, col);
                        if (value != null) {
                            JTextArea textArea = new JTextArea(value.toString());
                            textArea.setEditable(false);
                            textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                            textArea.setBackground(UIUtils.VINTAGE_SHADOW);
                            textArea.setForeground(UIUtils.VINTAGE_CREAM);
                            textArea.setLineWrap(true);
                            textArea.setWrapStyleWord(true);
                            textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                            
                            JScrollPane scrollPane = new JScrollPane(textArea);
                            scrollPane.setPreferredSize(new Dimension(400, 200));
                            
                            String colName = table.getColumnName(col);
                            JOptionPane.showMessageDialog(AdminDashboard.this, scrollPane, colName + " Details", JOptionPane.PLAIN_MESSAGE);
                        }
                    }
                }
            }
        });
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
        
        tblUsers.getColumnModel().getColumn(0).setMinWidth(80); 
        tblUsers.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblUsers.getColumnModel().getColumn(1).setMinWidth(120); 
        tblUsers.getColumnModel().getColumn(1).setPreferredWidth(120);
        tblUsers.getColumnModel().getColumn(2).setMinWidth(100); 
        tblUsers.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblUsers.getColumnModel().getColumn(3).setMinWidth(200);
        tblUsers.getColumnModel().getColumn(3).setPreferredWidth(250);
        tblUsers.getColumnModel().getColumn(4).setMinWidth(100);
        tblUsers.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblUsers.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        styleTableInteraction(tblUsers);
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
        
        tblRosters.getColumnModel().getColumn(0).setMinWidth(80); 
        tblRosters.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblRosters.getColumnModel().getColumn(1).setMinWidth(110); 
        tblRosters.getColumnModel().getColumn(1).setPreferredWidth(110);
        tblRosters.getColumnModel().getColumn(2).setMinWidth(100);
        tblRosters.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblRosters.getColumnModel().getColumn(3).setMinWidth(80); 
        tblRosters.getColumnModel().getColumn(3).setPreferredWidth(80);
        tblRosters.getColumnModel().getColumn(4).setMinWidth(80); 
        tblRosters.getColumnModel().getColumn(4).setPreferredWidth(80);
        tblRosters.getColumnModel().getColumn(5).setMinWidth(150);
        tblRosters.getColumnModel().getColumn(5).setPreferredWidth(200);
        tblRosters.getColumnModel().getColumn(6).setMinWidth(100);
        tblRosters.getColumnModel().getColumn(6).setPreferredWidth(100);
        tblRosters.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        styleTableInteraction(tblRosters);
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
        
        tblReports.getColumnModel().getColumn(0).setMinWidth(80);
        tblReports.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblReports.getColumnModel().getColumn(1).setMinWidth(80); 
        tblReports.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblReports.getColumnModel().getColumn(2).setMinWidth(100); 
        tblReports.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblReports.getColumnModel().getColumn(3).setMinWidth(100); 
        tblReports.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblReports.getColumnModel().getColumn(4).setMinWidth(100);
        tblReports.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblReports.getColumnModel().getColumn(5).setMinWidth(100);
        tblReports.getColumnModel().getColumn(5).setPreferredWidth(100);
        tblReports.getColumnModel().getColumn(6).setMinWidth(100);
        tblReports.getColumnModel().getColumn(6).setPreferredWidth(100);
        tblReports.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        styleTableInteraction(tblReports);
    }
    
        private void loadAppointmentStats() {
        List<Appointment> allAppts = apptRepo.getAllAppointments();
        
        long total = allAppts.size();
        long completed = allAppts.stream().filter(a -> a.getStatus().equals(Appointment.STATUS_COMPLETED)).count();
        long cancelled = allAppts.stream().filter(a -> a.getStatus().equals(Appointment.STATUS_CANCELLED)).count();
        long walkIn = allAppts.stream().filter(a -> a.getBookingType().equals(Appointment.TYPE_WALK_IN)).count();
        long online = allAppts.stream().filter(a -> a.getBookingType().equals(Appointment.TYPE_ONLINE)).count();

        setupStatCard(card5, "TOTAL APPOINTMENTS", String.valueOf(total), UIUtils.VINTAGE_GOLD);
        setupStatCard(card6, "COMPLETED", String.valueOf(completed), UIUtils.VINTAGE_CREAM);
        setupStatCard(card7, "CANCELLED", String.valueOf(cancelled), UIUtils.VINTAGE_CREAM);
        setupStatCard(card8, "WALK-IN VS ONLINE", walkIn + " / " + online, UIUtils.VINTAGE_GOLD);
        
        String[] columns = {"Appt ID", "Student ID", "Counselor ID", "Type", "Date", "Status", "Reason"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        for (Appointment appt : allAppts) {
            model.addRow(new Object[]{ 
                appt.getAppointmentId(), appt.getStudentUserId(), appt.getCounselorId(), 
                appt.getBookingType(), appt.getAppointmentDate(), appt.getStatus(), appt.getReason() 
            });
        }
        tblApptStats.setModel(model);
        
        tblApptStats.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblApptStats.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblApptStats.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblApptStats.getColumnModel().getColumn(3).setPreferredWidth(80);
        tblApptStats.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblApptStats.getColumnModel().getColumn(5).setPreferredWidth(100);
        tblApptStats.getColumnModel().getColumn(6).setPreferredWidth(250);
        
        panelAppointments.revalidate();
        panelAppointments.repaint();
    }

    private void setActiveButton(JButton activeBtn) {
        JButton[] buttons = {btnDashboard, btnUsers, btnRosters, btnReports, btnAppointments};
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
        card1.setBackground(UIUtils.VINTAGE_ROSE); 
        card2.setBackground(UIUtils.VINTAGE_ROSE);
        card3.setBackground(UIUtils.VINTAGE_ROSE); 
        card4.setBackground(UIUtils.VINTAGE_ROSE);
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
        sidebarPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        sidebarPanel.setBackground(UIUtils.VINTAGE_PANEL);
        sidebarPanel.removeAll();

        JPanel avatarWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        avatarWrapper.setOpaque(false);
        avatarWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        UIUtils.AvatarLabel avatar = new UIUtils.AvatarLabel(loggedInAdmin.getUserId(), loggedInAdmin.getFullName(), 100);
        avatarWrapper.add(avatar);
        avatarWrapper.setBorder(new EmptyBorder(10, 0, 20, 0));
        sidebarPanel.add(avatarWrapper);

        UIUtils.styleMenuButton(btnDashboard);
        UIUtils.styleMenuButton(btnUsers);
        UIUtils.styleMenuButton(btnRosters);
        UIUtils.styleMenuButton(btnAppointments);
        UIUtils.styleMenuButton(btnReports);
        UIUtils.styleMenuButton(btnLogout);
        
        btnDashboard.setText(" DASHBOARD");
        btnUsers.setText(" MANAGE USERS");
        btnRosters.setText(" STAFF ROSTERS");
        btnAppointments.setText(" APPOINTMENTS");
        btnReports.setText(" REPORTS");
        btnLogout.setText(" LOGOUT");

        btnDashboard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnUsers.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnRosters.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnAppointments.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnReports.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        sidebarPanel.add(btnDashboard);
        sidebarPanel.add(btnUsers);
        sidebarPanel.add(btnRosters);
        sidebarPanel.add(btnAppointments);
        sidebarPanel.add(btnReports);
        sidebarPanel.add(Box.createVerticalGlue());
        
        lblAdminName.setText(loggedInAdmin.getFullName());
        lblAdminName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAdminName.setForeground(UIUtils.VINTAGE_CREAM.darker());
        lblAdminName.setMaximumSize(new Dimension(230, 50));
        lblAdminName.setHorizontalAlignment(SwingConstants.CENTER);
        lblAdminName.setBorder(new EmptyBorder(10, 10, 10, 10));
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
        UIUtils.styleActionButton(btnEditUser, UIUtils.VINTAGE_GOLD);
        UIUtils.styleActionButton(btnEditRoster, UIUtils.VINTAGE_CREAM);
        UIUtils.styleActionButton(btnDeleteRoster, UIUtils.VINTAGE_CREAM);
        UIUtils.styleActionButton(btnGenerateReport, UIUtils.VINTAGE_GOLD);
        UIUtils.styleActionButton(btnDeleteUser, UIUtils.VINTAGE_GOLD);
        
        styleTable(tblUsers);
        styleTable(tblRosters);
        styleTable(tblReports);

        panelUsers.setLayout(new BorderLayout());
        panelUsers.setBackground(UIUtils.VINTAGE_BG);
        panelUsers.add(jPanel1, BorderLayout.NORTH); 
        panelUsers.add(jScrollPane1, BorderLayout.CENTER); 
        
        jPanel1.setOpaque(true); 
        jPanel1.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
        jPanel1.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); 
        jScrollPane1.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20)); 

        panelRosters.setLayout(new BorderLayout());
        panelRosters.setBackground(UIUtils.VINTAGE_BG);
        panelRosters.add(jPanel2, BorderLayout.NORTH);
        panelRosters.add(jScrollPane2, BorderLayout.CENTER);
        
        jPanel2.setOpaque(true);
        jPanel2.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
        jPanel2.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        jScrollPane2.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        panelReports.setLayout(new BorderLayout());
        panelReports.setBackground(UIUtils.VINTAGE_BG);
        panelReports.add(jPanel3, BorderLayout.NORTH);
        panelReports.add(jScrollPane3, BorderLayout.CENTER);
        
        jPanel3.setOpaque(true);
        jPanel3.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
        jPanel3.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        jScrollPane3.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        UIUtils.styleMenuButton(btnAppointments);
        btnAppointments.setText("APPOINTMENTS");
        btnAppointments.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        
        panelAppointments.setLayout(new BorderLayout());
        panelAppointments.setBackground(UIUtils.VINTAGE_BG);
        panelAppointments.removeAll();

        JPanel apptCardsWrapper = new JPanel(new GridLayout(1, 4, 15, 0));
        apptCardsWrapper.setBackground(UIUtils.VINTAGE_BG);
        apptCardsWrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        apptCardsWrapper.setPreferredSize(new Dimension(getWidth(), 160));
        
        card5.setBackground(UIUtils.VINTAGE_PANEL); 
        card6.setBackground(UIUtils.VINTAGE_PANEL);
        card7.setBackground(UIUtils.VINTAGE_PANEL); 
        card8.setBackground(UIUtils.VINTAGE_PANEL);
        
        apptCardsWrapper.add(card5);
        apptCardsWrapper.add(card6);
        apptCardsWrapper.add(card7);
        apptCardsWrapper.add(card8);

        panelAppointments.add(apptCardsWrapper, BorderLayout.NORTH);
        panelAppointments.add(jScrollPane4, BorderLayout.CENTER);
        
        jScrollPane4.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        styleTable(tblApptStats);
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
        btnAppointments = new javax.swing.JButton();
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
        btnEditUser = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblUsers = new javax.swing.JTable();
        panelRosters = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        btnAddRoster = new javax.swing.JButton();
        btnEditRoster = new javax.swing.JButton();
        btnDeleteRoster = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblRosters = new javax.swing.JTable();
        panelReports = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        btnGenerateReport = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblReports = new javax.swing.JTable();
        panelAppointments = new javax.swing.JPanel();
        card5 = new javax.swing.JPanel();
        card6 = new javax.swing.JPanel();
        card7 = new javax.swing.JPanel();
        card8 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblApptStats = new javax.swing.JTable();

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

        btnAppointments.setText("Appointments");
        btnAppointments.addActionListener(this::btnAppointmentsActionPerformed);

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
                            .addComponent(btnDashboard)
                            .addComponent(btnAppointments)))
                    .addGroup(sidebarPanelLayout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(lblAdminName)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(btnAppointments)
                .addGap(18, 18, 18)
                .addComponent(btnReports)
                .addGap(14, 14, 14)
                .addComponent(btnLogout)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblHeaderTitle.setText("WELCOME ADMIN");

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
            .addGap(0, 486, Short.MAX_VALUE)
        );
        card1Layout.setVerticalGroup(
            card1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1424, Short.MAX_VALUE)
        );

        panelDashboard.add(card1);

        javax.swing.GroupLayout card2Layout = new javax.swing.GroupLayout(card2);
        card2.setLayout(card2Layout);
        card2Layout.setHorizontalGroup(
            card2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 486, Short.MAX_VALUE)
        );
        card2Layout.setVerticalGroup(
            card2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1424, Short.MAX_VALUE)
        );

        panelDashboard.add(card2);

        javax.swing.GroupLayout card3Layout = new javax.swing.GroupLayout(card3);
        card3.setLayout(card3Layout);
        card3Layout.setHorizontalGroup(
            card3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 486, Short.MAX_VALUE)
        );
        card3Layout.setVerticalGroup(
            card3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1424, Short.MAX_VALUE)
        );

        panelDashboard.add(card3);

        javax.swing.GroupLayout card4Layout = new javax.swing.GroupLayout(card4);
        card4.setLayout(card4Layout);
        card4Layout.setHorizontalGroup(
            card4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 486, Short.MAX_VALUE)
        );
        card4Layout.setVerticalGroup(
            card4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1424, Short.MAX_VALUE)
        );

        panelDashboard.add(card4);

        contentPanel.add(panelDashboard, "cardDashboard");

        btnAddUser.setText("Add User");
        btnAddUser.addActionListener(this::btnAddUserActionPerformed);

        btnToggleStatus.setText("Toggle Status");
        btnToggleStatus.addActionListener(this::btnToggleStatusActionPerformed);

        btnDeleteUser.setText("Delete User");
        btnDeleteUser.addActionListener(this::btnDeleteUserActionPerformed);

        btnEditUser.setText("Edit User");
        btnEditUser.addActionListener(this::btnEditUserActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnAddUser)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addComponent(btnEditUser)
                .addGap(18, 18, 18)
                .addComponent(btnDeleteUser)
                .addGap(26, 26, 26)
                .addComponent(btnToggleStatus)
                .addGap(21, 21, 21))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddUser)
                    .addComponent(btnToggleStatus)
                    .addComponent(btnDeleteUser)
                    .addComponent(btnEditUser))
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
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(panelUsersLayout.createSequentialGroup()
                .addGap(93, 93, 93)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1441, Short.MAX_VALUE))
        );
        panelUsersLayout.setVerticalGroup(
            panelUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelUsersLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1266, Short.MAX_VALUE)
                .addContainerGap())
        );

        contentPanel.add(panelUsers, "cardUsers");

        btnAddRoster.setText("Add Roster");
        btnAddRoster.addActionListener(this::btnAddRosterActionPerformed);

        btnEditRoster.setText("Edit Roster");
        btnEditRoster.addActionListener(this::btnEditRosterActionPerformed);

        btnDeleteRoster.setText("Delete Roster");
        btnDeleteRoster.addActionListener(this::btnDeleteRosterActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(86, 86, 86)
                .addComponent(btnAddRoster)
                .addGap(48, 48, 48)
                .addComponent(btnEditRoster)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addComponent(btnDeleteRoster)
                .addGap(29, 29, 29))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddRoster)
                    .addComponent(btnEditRoster)
                    .addComponent(btnDeleteRoster))
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
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1977, Short.MAX_VALUE)
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
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1329, Short.MAX_VALUE)
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
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1977, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(panelReportsLayout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1474, Short.MAX_VALUE))
        );
        panelReportsLayout.setVerticalGroup(
            panelReportsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelReportsLayout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1347, Short.MAX_VALUE)
                .addContainerGap())
        );

        contentPanel.add(panelReports, "cardReports");

        javax.swing.GroupLayout card5Layout = new javax.swing.GroupLayout(card5);
        card5.setLayout(card5Layout);
        card5Layout.setHorizontalGroup(
            card5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 689, Short.MAX_VALUE)
        );
        card5Layout.setVerticalGroup(
            card5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 463, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout card6Layout = new javax.swing.GroupLayout(card6);
        card6.setLayout(card6Layout);
        card6Layout.setHorizontalGroup(
            card6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1830, Short.MAX_VALUE)
        );
        card6Layout.setVerticalGroup(
            card6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 463, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout card7Layout = new javax.swing.GroupLayout(card7);
        card7.setLayout(card7Layout);
        card7Layout.setHorizontalGroup(
            card7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1830, Short.MAX_VALUE)
        );
        card7Layout.setVerticalGroup(
            card7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 463, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout card8Layout = new javax.swing.GroupLayout(card8);
        card8.setLayout(card8Layout);
        card8Layout.setHorizontalGroup(
            card8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 689, Short.MAX_VALUE)
        );
        card8Layout.setVerticalGroup(
            card8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 463, Short.MAX_VALUE)
        );

        tblApptStats.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane4.setViewportView(tblApptStats);

        javax.swing.GroupLayout panelAppointmentsLayout = new javax.swing.GroupLayout(panelAppointments);
        panelAppointments.setLayout(panelAppointmentsLayout);
        panelAppointmentsLayout.setHorizontalGroup(
            panelAppointmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(card6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(panelAppointmentsLayout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(card5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(card8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(card7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        panelAppointmentsLayout.setVerticalGroup(
            panelAppointmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAppointmentsLayout.createSequentialGroup()
                .addComponent(card6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(panelAppointmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 463, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(card7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        contentPanel.add(panelAppointments, "cardAppointments");

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

    private void btnAppointmentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAppointmentsActionPerformed
        CardLayout cl = (CardLayout)(contentPanel.getLayout());
        cl.show(contentPanel, "cardAppointments");
        lblHeaderTitle.setText("APPOINTMENT STATISTICS");
        setActiveButton(btnAppointments); // You will need to add btnAppointments to your setActiveButton method
        
        loadAppointmentStats();
    }//GEN-LAST:event_btnAppointmentsActionPerformed

    private void btnEditRosterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditRosterActionPerformed
        int selectedRow = tblRosters.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a roster to edit.", "NO SELECTION", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String rosterId = tblRosters.getValueAt(selectedRow, 0).toString();
        
        JTextField txtCounselorId = new JTextField(tblRosters.getValueAt(selectedRow, 1).toString());
        JTextField txtDate = new JTextField(tblRosters.getValueAt(selectedRow, 2).toString());
        JTextField txtStart = new JTextField(tblRosters.getValueAt(selectedRow, 3).toString());
        JTextField txtEnd = new JTextField(tblRosters.getValueAt(selectedRow, 4).toString());
        JTextField txtRoom = new JTextField(tblRosters.getValueAt(selectedRow, 5).toString());
        String[] statuses = {Roster.STATUS_AVAILABLE, Roster.STATUS_UNAVAILABLE, Roster.STATUS_FULL, Roster.STATUS_LEAVE};
        JComboBox<String> cmbStatus = new JComboBox<>(statuses);
        cmbStatus.setSelectedItem(tblRosters.getValueAt(selectedRow, 6).toString());

        styleInputField(txtCounselorId); styleInputField(txtDate); styleInputField(txtStart); styleInputField(txtEnd); styleInputField(txtRoom);
        
        JLabel lblCoun = new JLabel("Counselor ID:"); lblCoun.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblDate = new JLabel("Date:"); lblDate.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblStart = new JLabel("Start:"); lblStart.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblEnd = new JLabel("End:"); lblEnd.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblRoom = new JLabel("Room:"); lblRoom.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblStat = new JLabel("Status:"); lblStat.setForeground(UIUtils.VINTAGE_CREAM);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIUtils.VINTAGE_PANEL);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
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

        int result = JOptionPane.showConfirmDialog(this, panel, "EDIT ROSTER", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                rosterService.updateRoster(
                    rosterId,
                    txtCounselorId.getText().trim(),
                    txtDate.getText().trim(),
                    txtStart.getText().trim(),
                    txtEnd.getText().trim(),
                    txtRoom.getText().trim(),
                    cmbStatus.getSelectedItem().toString()
                );
                JOptionPane.showMessageDialog(this, "Roster updated successfully!", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                loadRostersTable();
            } catch (InvalidInputException | DataNotFoundException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "VALIDATION ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnEditRosterActionPerformed

    private void btnDeleteRosterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteRosterActionPerformed
        int selectedRow = tblRosters.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a roster to delete.", "NO SELECTION", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String rosterId = tblRosters.getValueAt(selectedRow, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete Roster ID: " + rosterId + "?", "CONFIRM DELETION", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                rosterService.deleteRoster(rosterId);
                JOptionPane.showMessageDialog(this, "Roster deleted successfully.", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                loadRostersTable();
            } catch (DataNotFoundException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnDeleteRosterActionPerformed

    private void btnEditUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditUserActionPerformed
         int selectedRow = tblUsers.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.", "NO SELECTION", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String userId = tblUsers.getValueAt(selectedRow, 0).toString();
        User existingUser = userService.findById(userId);
        if (existingUser == null) return;

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIUtils.VINTAGE_PANEL);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtUsername = new JTextField(existingUser.getUsername());
        txtUsername.setEditable(false); // Username shouldn't change
        JPasswordField txtPassword = new JPasswordField(existingUser.getPassword());
        JTextField txtFullName = new JTextField(existingUser.getFullName());
        String[] roles = {"Admin", "Counselor", "Receptionist", "Student"};
        JComboBox<String> cmbRole = new JComboBox<>(roles);
        cmbRole.setSelectedItem(existingUser.getRole());

        styleInputField(txtUsername); styleInputField(txtPassword); styleInputField(txtFullName);
        
        JLabel lblUser = new JLabel("Username:"); lblUser.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblPass = new JLabel("Password:"); lblPass.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblName = new JLabel("Full Name:"); lblName.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblRole = new JLabel("Role:"); lblRole.setForeground(UIUtils.VINTAGE_CREAM);

        gbc.gridx = 0; gbc.gridy = 0; panel.add(lblUser, gbc);
        gbc.gridx = 1; panel.add(txtUsername, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(lblPass, gbc);
        gbc.gridx = 1; panel.add(txtPassword, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(lblName, gbc);
        gbc.gridx = 1; panel.add(txtFullName, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panel.add(lblRole, gbc);
        gbc.gridx = 1; panel.add(cmbRole, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "EDIT USER", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String role = cmbRole.getSelectedItem().toString();
                String password = new String(txtPassword.getPassword()).trim();
                String fullName = txtFullName.getText().trim();
                
                User updatedUser = null;
                switch (role) {
                    case "Admin": updatedUser = new cms.core.Admin(userId, existingUser.getUsername(), password, fullName, existingUser.getStatus()); break;
                    case "Counselor": updatedUser = new cms.core.Counselor(userId, existingUser.getUsername(), password, fullName, existingUser.getStatus()); break;
                    case "Receptionist": updatedUser = new cms.core.Receptionist(userId, existingUser.getUsername(), password, fullName, existingUser.getStatus()); break;
                    case "Student": updatedUser = new cms.core.Student(userId, existingUser.getUsername(), password, fullName, existingUser.getStatus()); break;
                }
                
                userService.updateUser(updatedUser);
                JOptionPane.showMessageDialog(this, "User updated successfully!", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                loadUsersTable();
            } catch (DataNotFoundException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnEditUserActionPerformed
    
    
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
    private javax.swing.JButton btnAppointments;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnDeleteRoster;
    private javax.swing.JButton btnDeleteUser;
    private javax.swing.JButton btnEditRoster;
    private javax.swing.JButton btnEditUser;
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
    private javax.swing.JPanel card5;
    private javax.swing.JPanel card6;
    private javax.swing.JPanel card7;
    private javax.swing.JPanel card8;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblAdminName;
    private javax.swing.JLabel lblHeaderTitle;
    private javax.swing.JPanel panelAppointments;
    private javax.swing.JPanel panelDashboard;
    private javax.swing.JPanel panelReports;
    private javax.swing.JPanel panelRosters;
    private javax.swing.JPanel panelUsers;
    private javax.swing.JPanel sidebarPanel;
    private javax.swing.JTable tblApptStats;
    private javax.swing.JTable tblReports;
    private javax.swing.JTable tblRosters;
    private javax.swing.JTable tblUsers;
    // End of variables declaration//GEN-END:variables
}
