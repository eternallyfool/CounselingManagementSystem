/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package cms.ui;

import cms.core.User;
import cms.core.Appointment;
import cms.core.AppointmentService;
import cms.core.QueueNumber;
import cms.core.QueueService;
import cms.core.CounselorProfile;
import cms.core.CounselorProfileService;
import cms.exception.InvalidInputException;
import cms.exception.DataNotFoundException;
import cms.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.util.List;
import cms.core.UserService;
import cms.core.User;

public class StudentDashboard extends javax.swing.JFrame {

    private User loggedInStudent;
    private AppointmentService apptService;
    private QueueService queueService;
    private CounselorProfileService counselorService;
    private UserService userService;

    public StudentDashboard(User student) {
        setUndecorated(true);
        initComponents();
        
        this.loggedInStudent = student;
        this.apptService = new AppointmentService();
        this.queueService = new QueueService();
        this.counselorService = new CounselorProfileService();
        this.userService = new UserService();
        
        applyDesign();
        loadDashboardStats();
        loadAppointmentsTable();
        loadQueueTable();
        loadCounselorCards();
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
        List<Appointment> myAppts = apptService.getAppointmentsForStudent(loggedInStudent.getUserId());
        
        long totalAppts = myAppts.size();
        long completed = myAppts.stream().filter(a -> a.getStatus().equals(Appointment.STATUS_COMPLETED)).count();
        long upcoming = myAppts.stream().filter(a -> a.getStatus().equals(Appointment.STATUS_BOOKED)).count();
        long cancelled = myAppts.stream().filter(a -> a.getStatus().equals(Appointment.STATUS_CANCELLED)).count();

        setupStatCard(card1, "TOTAL SESSIONS", String.valueOf(totalAppts), UIUtils.VINTAGE_GOLD);
        setupStatCard(card2, "COMPLETED", String.valueOf(completed), UIUtils.VINTAGE_CREAM);
        setupStatCard(card3, "UPCOMING", String.valueOf(upcoming), UIUtils.VINTAGE_GOLD);
        setupStatCard(card4, "CANCELLED", String.valueOf(cancelled), UIUtils.VINTAGE_CREAM);
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

    private void loadAppointmentsTable() {
        String[] columns = {"Appt ID", "Counselor ID", "Type", "Date", "Time", "Status", "Reason"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        for (Appointment appt : apptService.getAppointmentsForStudent(loggedInStudent.getUserId())) {
            model.addRow(new Object[]{ appt.getAppointmentId(), appt.getCounselorId(), appt.getBookingType(), appt.getAppointmentDate(), appt.getTimeRange(), appt.getStatus(), appt.getReason() });
        }
        tblAppointments.setModel(model);
        

        tblAppointments.getColumnModel().getColumn(0).setMinWidth(80); 
        tblAppointments.getColumnModel().getColumn(0).setPreferredWidth(80);
        
        tblAppointments.getColumnModel().getColumn(1).setMinWidth(110);
        tblAppointments.getColumnModel().getColumn(1).setPreferredWidth(110);
        
        tblAppointments.getColumnModel().getColumn(2).setMinWidth(80);
        tblAppointments.getColumnModel().getColumn(2).setPreferredWidth(80);
        
        tblAppointments.getColumnModel().getColumn(3).setMinWidth(100);
        tblAppointments.getColumnModel().getColumn(3).setPreferredWidth(100);
        
        tblAppointments.getColumnModel().getColumn(4).setMinWidth(100);
        tblAppointments.getColumnModel().getColumn(4).setPreferredWidth(100);
        
        tblAppointments.getColumnModel().getColumn(5).setMinWidth(90);
        tblAppointments.getColumnModel().getColumn(5).setPreferredWidth(90);
        
        tblAppointments.getColumnModel().getColumn(6).setMinWidth(200); 
        tblAppointments.getColumnModel().getColumn(6).setPreferredWidth(300);
        
        tblAppointments.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        tblAppointments.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
     
    @Override
    public void mouseMoved(java.awt.event.MouseEvent e) {
        int row = tblAppointments.rowAtPoint(e.getPoint());
        int col = tblAppointments.columnAtPoint(e.getPoint());
            if (row > -1 && col > -1) {
                Object value = tblAppointments.getValueAt(row, col);
                    if (value != null && value.toString().length() > 20) {
                        tblAppointments.setToolTipText(value.toString());
                    } else {
                        tblAppointments.setToolTipText(null);
                    }
                }
            }
        });
        
        tblAppointments.addMouseListener(new java.awt.event.MouseAdapter() {
            
        @Override
        public void mouseClicked(java.awt.event.MouseEvent e) {
            if (e.getClickCount() == 2) { 
                    int row = tblAppointments.getSelectedRow();
                    int col = tblAppointments.getSelectedColumn();
                    if (row > -1 && col > -1) {
                        Object value = tblAppointments.getValueAt(row, col);
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
                            
                            String colName = tblAppointments.getColumnName(col);
                            JOptionPane.showMessageDialog(StudentDashboard.this, scrollPane, colName + " Details", JOptionPane.PLAIN_MESSAGE);
                        }
                    }
                }
            }
        });
    }

    private void loadQueueTable() {
        String[] columns = {"Appt ID", "Queue No.", "Status", "Issued At"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        for (QueueNumber q : queueService.getQueueNumbersForStudent(loggedInStudent.getUserId())) {
            model.addRow(new Object[]{ q.getAppointmentId(), q.getDisplayQueueNo(), q.getQueueStatus(), q.getIssuedAt() });
        }
        tblQueue.setModel(model);
    }

    private void loadCounselorCards() {
        counselorGridPanel.removeAll();
        counselorGridPanel.setBackground(UIUtils.VINTAGE_BG);
        
        List<CounselorProfile> profiles = counselorService.getAllProfiles();
        
        for (CounselorProfile profile : profiles) {
            User counselorUser = userService.findById(profile.getCounselorId());
            String rawName = counselorUser != null ? counselorUser.getFullName() : profile.getCounselorId();
            String fullName = rawName.replaceAll("([a-z])([A-Z])", "$1 $2");
            
            JPanel card = new JPanel(new BorderLayout(0, 10));
            card.setBackground(UIUtils.VINTAGE_PANEL);
            card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            UIUtils.AvatarLabel avatar = new UIUtils.AvatarLabel(profile.getCounselorId(), fullName, 120);
            JPanel imgPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            imgPanel.setBackground(UIUtils.VINTAGE_PANEL);
            imgPanel.add(avatar);
            card.add(imgPanel, BorderLayout.NORTH);
            
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBackground(UIUtils.VINTAGE_PANEL);
            
            JLabel lblName = new JLabel(fullName, SwingConstants.CENTER);
            lblName.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lblName.setForeground(UIUtils.VINTAGE_GOLD);
            lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel lblId = new JLabel("ID: " + profile.getCounselorId());
            lblId.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblId.setForeground(UIUtils.VINTAGE_CREAM);
            lblId.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel lblSpec = new JLabel(profile.getSpecialization());
            lblSpec.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            lblSpec.setForeground(UIUtils.VINTAGE_CREAM.darker());
            lblSpec.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel lblEmail = new JLabel(profile.getEmail());
            lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblEmail.setForeground(UIUtils.VINTAGE_CREAM.darker());
            lblEmail.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel lblPhone = new JLabel(profile.getPhone());
            lblPhone.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblPhone.setForeground(UIUtils.VINTAGE_CREAM.darker());
            lblPhone.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            infoPanel.add(lblName);
            infoPanel.add(Box.createVerticalStrut(5));
            infoPanel.add(lblId);
            infoPanel.add(Box.createVerticalStrut(8));
            infoPanel.add(lblSpec);
            infoPanel.add(Box.createVerticalStrut(8));
            infoPanel.add(lblEmail);
            infoPanel.add(Box.createVerticalStrut(5));
            infoPanel.add(lblPhone);
            
            card.add(infoPanel, BorderLayout.CENTER);
            counselorGridPanel.add(card);
        }
        
        if (profiles.isEmpty()) {
            JLabel emptyLbl = new JLabel("No counselor profiles available.");
            emptyLbl.setForeground(UIUtils.VINTAGE_CREAM);
            counselorGridPanel.add(emptyLbl);
        }
        
        counselorGridPanel.revalidate();
        counselorGridPanel.repaint();
    }

    private void setActiveButton(JButton activeBtn) {
        JButton[] buttons = {btnDashboard, btnAppointments, btnQueue, btnCounselors};
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
        panelAppointments.setBackground(UIUtils.VINTAGE_BG);
        panelQueue.setBackground(UIUtils.VINTAGE_BG);
        panelCounselors.setBackground(UIUtils.VINTAGE_BG);
        card1.setBackground(UIUtils.VINTAGE_ROSE); 
        card2.setBackground(UIUtils.VINTAGE_ROSE);
        card3.setBackground(UIUtils.VINTAGE_ROSE); 
        card4.setBackground(UIUtils.VINTAGE_ROSE);
        lblHeaderTitle.setForeground(UIUtils.VINTAGE_GOLD);
        lblStudentName.setForeground(UIUtils.VINTAGE_CREAM.darker());
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
        setTitle("APU CMS - Student Dashboard");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
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

        UIUtils.AvatarLabel avatar = new UIUtils.AvatarLabel(loggedInStudent.getUserId(), loggedInStudent.getFullName(), 100);
        avatarWrapper.add(avatar);
        
        avatarWrapper.setBorder(new EmptyBorder(10, 0, 20, 0));
        
        sidebarPanel.add(avatarWrapper);

        UIUtils.styleMenuButton(btnDashboard);
        UIUtils.styleMenuButton(btnAppointments);
        UIUtils.styleMenuButton(btnQueue);
        UIUtils.styleMenuButton(btnCounselors);
        UIUtils.styleMenuButton(btnLogout);
        
        btnDashboard.setText(" DASHBOARD");
        btnAppointments.setText(" MY APPOINTMENTS");
        btnQueue.setText(" MY QUEUE NO.");
        btnCounselors.setText(" VIEW COUNSELORS");
        btnLogout.setText(" LOGOUT");

        btnDashboard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnAppointments.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnQueue.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnCounselors.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        sidebarPanel.add(btnDashboard);
        sidebarPanel.add(btnAppointments);
        sidebarPanel.add(btnQueue);
        sidebarPanel.add(btnCounselors);
        sidebarPanel.add(Box.createVerticalGlue());
        
        lblStudentName.setText(loggedInStudent.getFullName());
        lblStudentName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStudentName.setForeground(UIUtils.VINTAGE_CREAM.darker());
        lblStudentName.setMaximumSize(new Dimension(230, 50)); 
        lblStudentName.setHorizontalAlignment(SwingConstants.CENTER);
        lblStudentName.setBorder(new EmptyBorder(10, 10, 10, 10));
        sidebarPanel.add(lblStudentName);
        sidebarPanel.add(btnLogout);
        setActiveButton(btnDashboard);
    }

        private void setupContent() {
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 30, 25));
        lblHeaderTitle.setFont(new Font("Georgia", Font.BOLD, 28));
        lblHeaderTitle.setForeground(UIUtils.VINTAGE_GOLD);
        
        UIUtils.styleActionButton(btnBookAppt, UIUtils.VINTAGE_GOLD);
        UIUtils.styleActionButton(btnRescheduleAppt, UIUtils.VINTAGE_GOLD);
        UIUtils.styleActionButton(btnCancelAppt, UIUtils.VINTAGE_CREAM);
        
        styleTable(tblAppointments);
        styleTable(tblQueue);
        
        panelAppointments.setLayout(new BorderLayout());
        panelAppointments.setBackground(UIUtils.VINTAGE_BG);
        panelAppointments.add(jPanel1, BorderLayout.NORTH); 
        panelAppointments.add(jScrollPane1, BorderLayout.CENTER); 
        
        jPanel1.setOpaque(true); 
        jPanel1.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        jScrollPane1.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20)); 

        panelQueue.setLayout(new BorderLayout());
        panelQueue.setBackground(UIUtils.VINTAGE_BG);
        panelQueue.add(jScrollPane2, BorderLayout.CENTER);
        jScrollPane2.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 

        panelCounselors.setLayout(new BorderLayout());
        panelCounselors.setBackground(UIUtils.VINTAGE_BG);
        panelCounselors.add(jScrollPane3, BorderLayout.CENTER);
        jScrollPane3.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        jScrollPane3.setBackground(UIUtils.VINTAGE_BG);
        counselorGridPanel.setBackground(UIUtils.VINTAGE_BG);
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
        btnAppointments = new javax.swing.JButton();
        btnQueue = new javax.swing.JButton();
        btnCounselors = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        lblStudentName = new javax.swing.JLabel();
        headerPanel = new javax.swing.JPanel();
        lblHeaderTitle = new javax.swing.JLabel();
        contentPanel = new javax.swing.JPanel();
        panelDashboard = new javax.swing.JPanel();
        card1 = new javax.swing.JPanel();
        card2 = new javax.swing.JPanel();
        card3 = new javax.swing.JPanel();
        card4 = new javax.swing.JPanel();
        panelAppointments = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        btnBookAppt = new javax.swing.JButton();
        btnRescheduleAppt = new javax.swing.JButton();
        btnCancelAppt = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAppointments = new javax.swing.JTable();
        panelQueue = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblQueue = new javax.swing.JTable();
        panelCounselors = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        counselorGridPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnDashboard.setText("Dashboard");
        btnDashboard.addActionListener(this::btnDashboardActionPerformed);

        btnAppointments.setText("Appointments");
        btnAppointments.addActionListener(this::btnAppointmentsActionPerformed);

        btnQueue.setText("Queue");
        btnQueue.addActionListener(this::btnQueueActionPerformed);

        btnCounselors.setText("Counselors");
        btnCounselors.addActionListener(this::btnCounselorsActionPerformed);

        btnLogout.setText("Logout");
        btnLogout.addActionListener(this::btnLogoutActionPerformed);

        lblStudentName.setText("jLabel1");

        javax.swing.GroupLayout sidebarPanelLayout = new javax.swing.GroupLayout(sidebarPanel);
        sidebarPanel.setLayout(sidebarPanelLayout);
        sidebarPanelLayout.setHorizontalGroup(
            sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidebarPanelLayout.createSequentialGroup()
                .addGroup(sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sidebarPanelLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(lblStudentName))
                    .addGroup(sidebarPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnAppointments, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(sidebarPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnDashboard))
                    .addGroup(sidebarPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnQueue))
                    .addGroup(sidebarPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnCounselors))
                    .addGroup(sidebarPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnLogout)))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        sidebarPanelLayout.setVerticalGroup(
            sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidebarPanelLayout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addComponent(lblStudentName)
                .addGap(35, 35, 35)
                .addComponent(btnDashboard)
                .addGap(18, 18, 18)
                .addComponent(btnAppointments)
                .addGap(18, 18, 18)
                .addComponent(btnQueue)
                .addGap(18, 18, 18)
                .addComponent(btnCounselors)
                .addGap(18, 18, 18)
                .addComponent(btnLogout)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblHeaderTitle.setText("WELCOME STUDENT");

        javax.swing.GroupLayout headerPanelLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, headerPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblHeaderTitle)
                .addGap(70, 70, 70))
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, headerPanelLayout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addComponent(lblHeaderTitle)
                .addContainerGap())
        );

        contentPanel.setLayout(new java.awt.CardLayout());

        panelDashboard.setLayout(new java.awt.GridLayout(1, 2, 15, 0));

        javax.swing.GroupLayout card1Layout = new javax.swing.GroupLayout(card1);
        card1.setLayout(card1Layout);
        card1Layout.setHorizontalGroup(
            card1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 130, Short.MAX_VALUE)
        );
        card1Layout.setVerticalGroup(
            card1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 398, Short.MAX_VALUE)
        );

        panelDashboard.add(card1);

        javax.swing.GroupLayout card2Layout = new javax.swing.GroupLayout(card2);
        card2.setLayout(card2Layout);
        card2Layout.setHorizontalGroup(
            card2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 130, Short.MAX_VALUE)
        );
        card2Layout.setVerticalGroup(
            card2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 398, Short.MAX_VALUE)
        );

        panelDashboard.add(card2);

        javax.swing.GroupLayout card3Layout = new javax.swing.GroupLayout(card3);
        card3.setLayout(card3Layout);
        card3Layout.setHorizontalGroup(
            card3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 130, Short.MAX_VALUE)
        );
        card3Layout.setVerticalGroup(
            card3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 398, Short.MAX_VALUE)
        );

        panelDashboard.add(card3);

        javax.swing.GroupLayout card4Layout = new javax.swing.GroupLayout(card4);
        card4.setLayout(card4Layout);
        card4Layout.setHorizontalGroup(
            card4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 130, Short.MAX_VALUE)
        );
        card4Layout.setVerticalGroup(
            card4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 398, Short.MAX_VALUE)
        );

        panelDashboard.add(card4);

        contentPanel.add(panelDashboard, "cardDashboard");

        btnBookAppt.setText("Book");
        btnBookAppt.addActionListener(this::btnBookApptActionPerformed);

        btnRescheduleAppt.setText("Reschedule");
        btnRescheduleAppt.addActionListener(this::btnRescheduleApptActionPerformed);

        btnCancelAppt.setText("Cancel");
        btnCancelAppt.addActionListener(this::btnCancelApptActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(btnBookAppt)
                .addGap(66, 66, 66)
                .addComponent(btnRescheduleAppt)
                .addGap(63, 63, 63)
                .addComponent(btnCancelAppt)
                .addContainerGap(36, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBookAppt)
                    .addComponent(btnRescheduleAppt)
                    .addComponent(btnCancelAppt))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jScrollPane1.setPreferredSize(new java.awt.Dimension(50, 50));

        tblAppointments.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblAppointments);

        javax.swing.GroupLayout panelAppointmentsLayout = new javax.swing.GroupLayout(panelAppointments);
        panelAppointments.setLayout(panelAppointmentsLayout);
        panelAppointmentsLayout.setHorizontalGroup(
            panelAppointmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAppointmentsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(panelAppointmentsLayout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelAppointmentsLayout.setVerticalGroup(
            panelAppointmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAppointmentsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                .addContainerGap())
        );

        contentPanel.add(panelAppointments, "cardAppointments");

        jScrollPane2.setPreferredSize(new java.awt.Dimension(50, 50));

        tblQueue.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblQueue);

        javax.swing.GroupLayout panelQueueLayout = new javax.swing.GroupLayout(panelQueue);
        panelQueue.setLayout(panelQueueLayout);
        panelQueueLayout.setHorizontalGroup(
            panelQueueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelQueueLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelQueueLayout.setVerticalGroup(
            panelQueueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelQueueLayout.createSequentialGroup()
                .addContainerGap(69, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        contentPanel.add(panelQueue, "cardQueue");

        counselorGridPanel.setLayout(new java.awt.GridLayout(0, 3, 20, 20));
        jScrollPane3.setViewportView(counselorGridPanel);

        javax.swing.GroupLayout panelCounselorsLayout = new javax.swing.GroupLayout(panelCounselors);
        panelCounselors.setLayout(panelCounselorsLayout);
        panelCounselorsLayout.setHorizontalGroup(
            panelCounselorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelCounselorsLayout.createSequentialGroup()
                .addContainerGap(39, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 487, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
        );
        panelCounselorsLayout.setVerticalGroup(
            panelCounselorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCounselorsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
                .addContainerGap())
        );

        contentPanel.add(panelCounselors, "cardCounselors");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(sidebarPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
        lblHeaderTitle.setText("STUDENT OVERVIEW");
        setActiveButton(btnDashboard);
    }//GEN-LAST:event_btnDashboardActionPerformed

    private void btnCounselorsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCounselorsActionPerformed
        CardLayout cl = (CardLayout)(contentPanel.getLayout());
        cl.show(contentPanel, "cardCounselors");
        lblHeaderTitle.setText("COUNSELOR PROFILES");
        setActiveButton(btnCounselors);
    }//GEN-LAST:event_btnCounselorsActionPerformed

    private void btnAppointmentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAppointmentsActionPerformed
        CardLayout cl = (CardLayout)(contentPanel.getLayout());
        cl.show(contentPanel, "cardAppointments");
        lblHeaderTitle.setText("MY APPOINTMENTS");
        setActiveButton(btnAppointments);
    }//GEN-LAST:event_btnAppointmentsActionPerformed

    private void btnQueueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQueueActionPerformed
        CardLayout cl = (CardLayout)(contentPanel.getLayout());
        cl.show(contentPanel, "cardQueue");
        lblHeaderTitle.setText("MY WALK-IN QUEUE");
        setActiveButton(btnQueue);
    }//GEN-LAST:event_btnQueueActionPerformed

    private void btnBookApptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBookApptActionPerformed
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIUtils.VINTAGE_PANEL);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtCounselorId = new JTextField("U2");
        String[] types = {Appointment.TYPE_ONLINE, Appointment.TYPE_WALK_IN};
        JComboBox<String> cmbType = new JComboBox<>(types);
        JTextField txtDate = new JTextField("2026-06-15");
        JTextField txtStart = new JTextField("10:00");
        JTextField txtEnd = new JTextField("10:30");
        JTextField txtReason = new JTextField("Need help with stress management");

        styleInputField(txtCounselorId); styleInputField(txtDate);
        styleInputField(txtStart); styleInputField(txtEnd); styleInputField(txtReason);
        
        JLabel lblCoun = new JLabel("Counselor ID:"); lblCoun.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblType = new JLabel("Type:"); lblType.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblDate = new JLabel("Date:"); lblDate.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblStart = new JLabel("Start:"); lblStart.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblEnd = new JLabel("End:"); lblEnd.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblReason = new JLabel("Reason:"); lblReason.setForeground(UIUtils.VINTAGE_CREAM);

        gbc.gridx = 0; gbc.gridy = 0; panel.add(lblCoun, gbc);
        gbc.gridx = 1; panel.add(txtCounselorId, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(lblType, gbc);
        gbc.gridx = 1; panel.add(cmbType, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(lblDate, gbc);
        gbc.gridx = 1; panel.add(txtDate, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panel.add(lblStart, gbc);
        gbc.gridx = 1; panel.add(txtStart, gbc);
        gbc.gridx = 0; gbc.gridy = 4; panel.add(lblEnd, gbc);
        gbc.gridx = 1; panel.add(txtEnd, gbc);
        gbc.gridx = 0; gbc.gridy = 5; panel.add(lblReason, gbc);
        gbc.gridx = 1; panel.add(txtReason, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "BOOK APPOINTMENT", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                apptService.createAppointment(
                    loggedInStudent.getUserId(), 
                    txtCounselorId.getText().trim(),
                    cmbType.getSelectedItem().toString(),
                    txtDate.getText().trim(),
                    txtStart.getText().trim(),
                    txtEnd.getText().trim(),
                    txtReason.getText().trim(),
                    loggedInStudent.getUserId()
                );
                JOptionPane.showMessageDialog(this, "Appointment booked successfully!", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                loadAppointmentsTable();
                loadDashboardStats();
            } catch (InvalidInputException | DataNotFoundException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "VALIDATION ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnBookApptActionPerformed

    private void btnRescheduleApptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRescheduleApptActionPerformed
        int selectedRow = tblAppointments.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to reschedule.", "NO SELECTION", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String apptId = tblAppointments.getValueAt(selectedRow, 0).toString();
        String status = tblAppointments.getValueAt(selectedRow, 5).toString();
        if (!status.equals(Appointment.STATUS_BOOKED) && !status.equals(Appointment.STATUS_RESCHEDULED)) {
            JOptionPane.showMessageDialog(this, "Only active appointments can be rescheduled.", "INVALID STATUS", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField txtDate = new JTextField("2026-06-20");
        JTextField txtStart = new JTextField("14:00");
        JTextField txtEnd = new JTextField("14:30");
        styleInputField(txtDate); styleInputField(txtStart); styleInputField(txtEnd);
        
        JLabel lblDate = new JLabel("New Date:"); lblDate.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblStart = new JLabel("New Start:"); lblStart.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblEnd = new JLabel("New End:"); lblEnd.setForeground(UIUtils.VINTAGE_CREAM);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIUtils.VINTAGE_PANEL);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; panel.add(lblDate, gbc);
        gbc.gridx = 1; panel.add(txtDate, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(lblStart, gbc);
        gbc.gridx = 1; panel.add(txtStart, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(lblEnd, gbc);
        gbc.gridx = 1; panel.add(txtEnd, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "RESCHEDULE APPOINTMENT", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                apptService.rescheduleAppointment(apptId, txtDate.getText(), txtStart.getText(), txtEnd.getText());
                JOptionPane.showMessageDialog(this, "Appointment rescheduled!", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                loadAppointmentsTable();
            } catch (InvalidInputException | DataNotFoundException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnRescheduleApptActionPerformed

    private void btnCancelApptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelApptActionPerformed
        int selectedRow = tblAppointments.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to cancel.", "NO SELECTION", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String apptId = tblAppointments.getValueAt(selectedRow, 0).toString();
        String status = tblAppointments.getValueAt(selectedRow, 5).toString();
        if (status.equals(Appointment.STATUS_COMPLETED) || status.equals(Appointment.STATUS_CANCELLED)) {
            JOptionPane.showMessageDialog(this, "Cannot cancel a completed or already cancelled appointment.", "INVALID STATUS", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Cancel appointment " + apptId + "?", "CONFIRM", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                apptService.cancelAppointment(apptId);
                JOptionPane.showMessageDialog(this, "Appointment cancelled.", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                loadAppointmentsTable();
                loadDashboardStats();
            } catch (InvalidInputException | DataNotFoundException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnCancelApptActionPerformed
    
    
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
            java.util.logging.Logger.getLogger(StudentDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
         java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAppointments;
    private javax.swing.JButton btnBookAppt;
    private javax.swing.JButton btnCancelAppt;
    private javax.swing.JButton btnCounselors;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnQueue;
    private javax.swing.JButton btnRescheduleAppt;
    private javax.swing.JPanel card1;
    private javax.swing.JPanel card2;
    private javax.swing.JPanel card3;
    private javax.swing.JPanel card4;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JPanel counselorGridPanel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblHeaderTitle;
    private javax.swing.JLabel lblStudentName;
    private javax.swing.JPanel panelAppointments;
    private javax.swing.JPanel panelCounselors;
    private javax.swing.JPanel panelDashboard;
    private javax.swing.JPanel panelQueue;
    private javax.swing.JPanel sidebarPanel;
    private javax.swing.JTable tblAppointments;
    private javax.swing.JTable tblQueue;
    // End of variables declaration//GEN-END:variables
}
