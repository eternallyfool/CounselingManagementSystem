/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package cms.ui;

import cms.core.User;
import cms.core.UserService;
import cms.core.StudentService;
import cms.core.StudentProfile;
import cms.core.AppointmentService;
import cms.core.QueueService;
import cms.core.QueueNumber;
import cms.core.Appointment;
import cms.exception.InvalidInputException;
import cms.exception.DataNotFoundException;
import cms.util.UIUtils;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.util.List;

public class ReceptionistDashboard extends javax.swing.JFrame {

    private User loggedInReceptionist;
    private UserService userService;
    private StudentService studentService;
    private AppointmentService apptService;
    private QueueService queueService;

    public ReceptionistDashboard(User receptionist) {
        setUndecorated(true);
        initComponents();
        
        this.loggedInReceptionist = receptionist;
        this.userService = new UserService();
        this.studentService = new StudentService();
        this.apptService = new AppointmentService();
        this.queueService = new QueueService();
        
        applyDesign();
        loadDashboardStats();
        loadStudentsTable();
        loadAppointmentsTable();
        loadQueueTable();
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
                            JOptionPane.showMessageDialog(ReceptionistDashboard.this, scrollPane, colName + " Details", JOptionPane.PLAIN_MESSAGE);
                        }
                    }
                }
            }
        });
    }

    private void loadDashboardStats() {
        long totalAppts = apptService.getAllAppointments().size();
        long walkIns = apptService.getAllAppointments().stream().filter(a -> a.getBookingType().equals(Appointment.TYPE_WALK_IN)).count();
        long waitingQueue = queueService.getAllQueueNumbers().stream().filter(q -> q.getQueueStatus().equals(QueueNumber.STATUS_WAITING)).count();
        long completed = apptService.getAllAppointments().stream().filter(a -> a.getStatus().equals(Appointment.STATUS_COMPLETED)).count();

        setupStatCard(card1, "TOTAL APPOINTMENTS", String.valueOf(totalAppts), UIUtils.VINTAGE_GOLD);
        setupStatCard(card2, "WALK-INS", String.valueOf(walkIns), UIUtils.VINTAGE_CREAM);
        setupStatCard(card3, "WAITING IN QUEUE", String.valueOf(waitingQueue), UIUtils.VINTAGE_GOLD);
        setupStatCard(card4, "COMPLETED", String.valueOf(completed), UIUtils.VINTAGE_CREAM);
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

    private void loadStudentsTable() {
        String[] columns = {"User ID", "Student No", "Full Name", "Email", "Phone", "Programme", "Intake", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        List<User> students = userService.getUsersByRole("Student");
        for (User user : students) {
            StudentProfile profile = studentService.findProfileByUserId(user.getUserId());
            if (profile != null) {
                model.addRow(new Object[]{ user.getUserId(), profile.getStudentNo(), user.getFullName(), profile.getEmail(), profile.getPhone(), profile.getProgramme(), profile.getIntake(), user.getStatus() });
            }
        }
        tblStudents.setModel(model);
        
        tblStudents.getColumnModel().getColumn(0).setMinWidth(80);
        tblStudents.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblStudents.getColumnModel().getColumn(1).setMinWidth(100);
        tblStudents.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblStudents.getColumnModel().getColumn(2).setMinWidth(150);
        tblStudents.getColumnModel().getColumn(2).setPreferredWidth(180);
        tblStudents.getColumnModel().getColumn(3).setMinWidth(180);
        tblStudents.getColumnModel().getColumn(3).setPreferredWidth(200);
        tblStudents.getColumnModel().getColumn(4).setMinWidth(100);
        tblStudents.getColumnModel().getColumn(4).setPreferredWidth(110);
        tblStudents.getColumnModel().getColumn(5).setMinWidth(120);
        tblStudents.getColumnModel().getColumn(5).setPreferredWidth(130);
        tblStudents.getColumnModel().getColumn(6).setMinWidth(120);
        tblStudents.getColumnModel().getColumn(6).setPreferredWidth(130);
        tblStudents.getColumnModel().getColumn(7).setMinWidth(80);
        tblStudents.getColumnModel().getColumn(7).setPreferredWidth(90);
        tblStudents.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        styleTableInteraction(tblStudents);
    }

    private void loadAppointmentsTable() {
        String[] columns = {"Appt ID", "Student ID", "Counselor ID", "Type", "Date", "Time", "Status", "Reason"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        for (Appointment appt : apptService.getAllAppointments()) {
            model.addRow(new Object[]{ appt.getAppointmentId(), appt.getStudentUserId(), appt.getCounselorId(), appt.getBookingType(), appt.getAppointmentDate(), appt.getTimeRange(), appt.getStatus(), appt.getReason() });
        }
        tblAppointments.setModel(model);
        
        tblAppointments.getColumnModel().getColumn(0).setMinWidth(80);
        tblAppointments.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblAppointments.getColumnModel().getColumn(1).setMinWidth(80);
        tblAppointments.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblAppointments.getColumnModel().getColumn(2).setMinWidth(100);
        tblAppointments.getColumnModel().getColumn(2).setPreferredWidth(110);
        tblAppointments.getColumnModel().getColumn(3).setMinWidth(80);
        tblAppointments.getColumnModel().getColumn(3).setPreferredWidth(80);
        tblAppointments.getColumnModel().getColumn(4).setMinWidth(100);
        tblAppointments.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblAppointments.getColumnModel().getColumn(5).setMinWidth(100);
        tblAppointments.getColumnModel().getColumn(5).setPreferredWidth(100);
        tblAppointments.getColumnModel().getColumn(6).setMinWidth(90);
        tblAppointments.getColumnModel().getColumn(6).setPreferredWidth(90);
        tblAppointments.getColumnModel().getColumn(7).setMinWidth(200);
        tblAppointments.getColumnModel().getColumn(7).setPreferredWidth(300);
        tblAppointments.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        styleTableInteraction(tblAppointments);
    }

    private void loadQueueTable() {
        String[] columns = {"Appt ID", "Queue No.", "Status", "Issued At"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        for (QueueNumber q : queueService.getAllQueueNumbers()) {
            model.addRow(new Object[]{ q.getAppointmentId(), q.getDisplayQueueNo(), q.getQueueStatus(), q.getIssuedAt() });
        }
        tblQueue.setModel(model);
        
        tblQueue.getColumnModel().getColumn(0).setMinWidth(100);
        tblQueue.getColumnModel().getColumn(0).setPreferredWidth(150);
        tblQueue.getColumnModel().getColumn(1).setMinWidth(100);
        tblQueue.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblQueue.getColumnModel().getColumn(2).setMinWidth(100);
        tblQueue.getColumnModel().getColumn(2).setPreferredWidth(150);
        tblQueue.getColumnModel().getColumn(3).setMinWidth(150);
        tblQueue.getColumnModel().getColumn(3).setPreferredWidth(200);
        tblQueue.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        styleTableInteraction(tblQueue);
    }
    
    private void setActiveButton(JButton activeBtn) {
        JButton[] buttons = {btnDashboard, btnStudents, btnAppointments, btnQueue};
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
        panelStudents.setBackground(UIUtils.VINTAGE_BG);
        panelAppointments.setBackground(UIUtils.VINTAGE_BG);
        panelQueue.setBackground(UIUtils.VINTAGE_BG);
        card1.setBackground(UIUtils.VINTAGE_ROSE); 
        card2.setBackground(UIUtils.VINTAGE_ROSE);
        card3.setBackground(UIUtils.VINTAGE_ROSE); 
        card4.setBackground(UIUtils.VINTAGE_ROSE);
        lblHeaderTitle.setForeground(UIUtils.VINTAGE_GOLD);
        lblReceptionistName.setForeground(UIUtils.VINTAGE_CREAM.darker());
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
        setTitle("APU CMS - Receptionist Dashboard");
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

        UIUtils.AvatarLabel avatar = new UIUtils.AvatarLabel(loggedInReceptionist.getUserId(), loggedInReceptionist.getFullName(), 100);
        avatarWrapper.add(avatar);
        avatarWrapper.setBorder(new EmptyBorder(10, 0, 20, 0));
        sidebarPanel.add(avatarWrapper);

        UIUtils.styleMenuButton(btnDashboard);
        UIUtils.styleMenuButton(btnStudents);
        UIUtils.styleMenuButton(btnAppointments);
        UIUtils.styleMenuButton(btnQueue);
        UIUtils.styleMenuButton(btnLogout);
        
        btnDashboard.setText(" DASHBOARD");
        btnStudents.setText(" MANAGE STUDENTS");
        btnAppointments.setText(" APPOINTMENTS");
        btnQueue.setText(" WALK-IN QUEUE");
        btnLogout.setText(" LOGOUT");

        btnDashboard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnStudents.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnAppointments.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnQueue.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        sidebarPanel.add(btnDashboard);
        sidebarPanel.add(btnStudents);
        sidebarPanel.add(btnAppointments);
        sidebarPanel.add(btnQueue);
        sidebarPanel.add(Box.createVerticalGlue());
        
        lblReceptionistName.setText(loggedInReceptionist.getFullName());
        lblReceptionistName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblReceptionistName.setForeground(UIUtils.VINTAGE_CREAM.darker());
        lblReceptionistName.setMaximumSize(new Dimension(230, 50)); 
        lblReceptionistName.setHorizontalAlignment(SwingConstants.CENTER);
        lblReceptionistName.setBorder(new EmptyBorder(10, 10, 10, 10));
        sidebarPanel.add(lblReceptionistName);
        sidebarPanel.add(btnLogout);
        setActiveButton(btnDashboard);
        
    }

        private void setupContent() {
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 30, 25));
        lblHeaderTitle.setFont(new Font("Georgia", Font.BOLD, 28));
        lblHeaderTitle.setForeground(UIUtils.VINTAGE_GOLD);
        
        UIUtils.styleActionButton(btnAddStudent, UIUtils.VINTAGE_GOLD);
        UIUtils.styleActionButton(btnEditStudent, UIUtils.VINTAGE_GOLD);
        UIUtils.styleActionButton(btnToggleStudentStatus, UIUtils.VINTAGE_CREAM);
        
        UIUtils.styleActionButton(btnBookAppt, UIUtils.VINTAGE_GOLD);
        UIUtils.styleActionButton(btnRescheduleAppt, UIUtils.VINTAGE_GOLD);
        UIUtils.styleActionButton(btnAssignCounselor, UIUtils.VINTAGE_GOLD);
        UIUtils.styleActionButton(btnCancelAppt, UIUtils.VINTAGE_CREAM);
        
        UIUtils.styleActionButton(btnGenerateQueue, UIUtils.VINTAGE_GOLD);
        
        styleTable(tblStudents);
        styleTable(tblAppointments);
        styleTable(tblQueue);

        panelStudents.setLayout(new BorderLayout());
        panelStudents.setBackground(UIUtils.VINTAGE_BG);
        panelStudents.add(jPanel1, BorderLayout.NORTH); 
        panelStudents.add(jScrollPane1, BorderLayout.CENTER); 
        
        jPanel1.setOpaque(true); 
        jPanel1.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); 
        jScrollPane1.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20)); 

        panelAppointments.setLayout(new BorderLayout());
        panelAppointments.setBackground(UIUtils.VINTAGE_BG);
        panelAppointments.add(jPanel2, BorderLayout.NORTH);
        panelAppointments.add(jScrollPane2, BorderLayout.CENTER);
        
        jPanel2.setOpaque(true);
        jPanel2.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        jScrollPane2.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        panelQueue.setLayout(new BorderLayout());
        panelQueue.setBackground(UIUtils.VINTAGE_BG);
        panelQueue.add(jPanel3, BorderLayout.NORTH);
        panelQueue.add(jScrollPane3, BorderLayout.CENTER);
        
        jPanel3.setOpaque(true);
        jPanel3.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10)); // Keep your custom flow layout
        jPanel3.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        jScrollPane3.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
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
        btnStudents = new javax.swing.JButton();
        btnAppointments = new javax.swing.JButton();
        btnQueue = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        lblReceptionistName = new javax.swing.JLabel();
        headerPanel = new javax.swing.JPanel();
        lblHeaderTitle = new javax.swing.JLabel();
        contentPanel = new javax.swing.JPanel();
        panelDashboard = new javax.swing.JPanel();
        card1 = new javax.swing.JPanel();
        card2 = new javax.swing.JPanel();
        card3 = new javax.swing.JPanel();
        card4 = new javax.swing.JPanel();
        panelStudents = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        btnAddStudent = new javax.swing.JButton();
        btnEditStudent = new javax.swing.JButton();
        btnToggleStudentStatus = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblStudents = new javax.swing.JTable();
        panelAppointments = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        btnBookAppt = new javax.swing.JButton();
        btnRescheduleAppt = new javax.swing.JButton();
        btnAssignCounselor = new javax.swing.JButton();
        btnCancelAppt = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblAppointments = new javax.swing.JTable();
        panelQueue = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        btnGenerateQueue = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblQueue = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(640, 360));

        btnDashboard.setText("Dashboard");
        btnDashboard.addActionListener(this::btnDashboardActionPerformed);

        btnStudents.setText("Students");
        btnStudents.addActionListener(this::btnStudentsActionPerformed);

        btnAppointments.setText("Appointments");
        btnAppointments.addActionListener(this::btnAppointmentsActionPerformed);

        btnQueue.setText("Queue");
        btnQueue.addActionListener(this::btnQueueActionPerformed);

        btnLogout.setText("Logout");
        btnLogout.addActionListener(this::btnLogoutActionPerformed);

        lblReceptionistName.setText("jLabel1");

        javax.swing.GroupLayout sidebarPanelLayout = new javax.swing.GroupLayout(sidebarPanel);
        sidebarPanel.setLayout(sidebarPanelLayout);
        sidebarPanelLayout.setHorizontalGroup(
            sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidebarPanelLayout.createSequentialGroup()
                .addGroup(sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sidebarPanelLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(lblReceptionistName))
                    .addGroup(sidebarPanelLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnAppointments)
                            .addComponent(btnDashboard, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnStudents, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnQueue, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnLogout, javax.swing.GroupLayout.Alignment.LEADING))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        sidebarPanelLayout.setVerticalGroup(
            sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidebarPanelLayout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addComponent(lblReceptionistName)
                .addGap(18, 18, 18)
                .addComponent(btnDashboard)
                .addGap(18, 18, 18)
                .addComponent(btnStudents)
                .addGap(18, 18, 18)
                .addComponent(btnAppointments)
                .addGap(18, 18, 18)
                .addComponent(btnQueue)
                .addGap(18, 18, 18)
                .addComponent(btnLogout)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblHeaderTitle.setText("WELCOME RECEPTIONIST");

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
            .addGap(0, 152, Short.MAX_VALUE)
        );
        card1Layout.setVerticalGroup(
            card1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 364, Short.MAX_VALUE)
        );

        panelDashboard.add(card1);

        javax.swing.GroupLayout card2Layout = new javax.swing.GroupLayout(card2);
        card2.setLayout(card2Layout);
        card2Layout.setHorizontalGroup(
            card2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 152, Short.MAX_VALUE)
        );
        card2Layout.setVerticalGroup(
            card2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 364, Short.MAX_VALUE)
        );

        panelDashboard.add(card2);

        javax.swing.GroupLayout card3Layout = new javax.swing.GroupLayout(card3);
        card3.setLayout(card3Layout);
        card3Layout.setHorizontalGroup(
            card3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 152, Short.MAX_VALUE)
        );
        card3Layout.setVerticalGroup(
            card3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 364, Short.MAX_VALUE)
        );

        panelDashboard.add(card3);

        javax.swing.GroupLayout card4Layout = new javax.swing.GroupLayout(card4);
        card4.setLayout(card4Layout);
        card4Layout.setHorizontalGroup(
            card4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 152, Short.MAX_VALUE)
        );
        card4Layout.setVerticalGroup(
            card4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 364, Short.MAX_VALUE)
        );

        panelDashboard.add(card4);

        contentPanel.add(panelDashboard, "cardDashboard");

        btnAddStudent.setText("Add Student");
        btnAddStudent.addActionListener(this::btnAddStudentActionPerformed);

        btnEditStudent.setText("Edit Student");
        btnEditStudent.addActionListener(this::btnEditStudentActionPerformed);

        btnToggleStudentStatus.setText("Toggle Student Status");
        btnToggleStudentStatus.addActionListener(this::btnToggleStudentStatusActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(btnAddStudent)
                .addGap(18, 18, 18)
                .addComponent(btnEditStudent)
                .addGap(18, 18, 18)
                .addComponent(btnToggleStudentStatus)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddStudent)
                    .addComponent(btnEditStudent)
                    .addComponent(btnToggleStudentStatus))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jScrollPane1.setPreferredSize(new java.awt.Dimension(50, 50));

        tblStudents.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblStudents);

        javax.swing.GroupLayout panelStudentsLayout = new javax.swing.GroupLayout(panelStudents);
        panelStudents.setLayout(panelStudentsLayout);
        panelStudentsLayout.setHorizontalGroup(
            panelStudentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStudentsLayout.createSequentialGroup()
                .addGap(93, 93, 93)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(143, Short.MAX_VALUE))
            .addGroup(panelStudentsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelStudentsLayout.setVerticalGroup(
            panelStudentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStudentsLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
                .addContainerGap())
        );

        contentPanel.add(panelStudents, "cardStudents");

        btnBookAppt.setText("Book");
        btnBookAppt.addActionListener(this::btnBookApptActionPerformed);

        btnRescheduleAppt.setText("Reshedule");
        btnRescheduleAppt.addActionListener(this::btnRescheduleApptActionPerformed);

        btnAssignCounselor.setText("Assign Counselor");
        btnAssignCounselor.addActionListener(this::btnAssignCounselorActionPerformed);

        btnCancelAppt.setText("Cancel");
        btnCancelAppt.addActionListener(this::btnCancelApptActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(btnBookAppt)
                .addGap(18, 18, 18)
                .addComponent(btnRescheduleAppt)
                .addGap(18, 18, 18)
                .addComponent(btnAssignCounselor)
                .addGap(18, 18, 18)
                .addComponent(btnCancelAppt)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBookAppt)
                    .addComponent(btnRescheduleAppt)
                    .addComponent(btnAssignCounselor)
                    .addComponent(btnCancelAppt))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jScrollPane2.setPreferredSize(new java.awt.Dimension(50, 50));

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
        jScrollPane2.setViewportView(tblAppointments);

        javax.swing.GroupLayout panelAppointmentsLayout = new javax.swing.GroupLayout(panelAppointments);
        panelAppointments.setLayout(panelAppointmentsLayout);
        panelAppointmentsLayout.setHorizontalGroup(
            panelAppointmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAppointmentsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 642, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(panelAppointmentsLayout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelAppointmentsLayout.setVerticalGroup(
            panelAppointmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAppointmentsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                .addContainerGap())
        );

        contentPanel.add(panelAppointments, "cardAppointments");

        btnGenerateQueue.setText("Generate Queue");
        btnGenerateQueue.addActionListener(this::btnGenerateQueueActionPerformed);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(226, Short.MAX_VALUE)
                .addComponent(btnGenerateQueue)
                .addGap(126, 126, 126))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(btnGenerateQueue)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jScrollPane3.setPreferredSize(new java.awt.Dimension(50, 50));

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
        jScrollPane3.setViewportView(tblQueue);

        javax.swing.GroupLayout panelQueueLayout = new javax.swing.GroupLayout(panelQueue);
        panelQueue.setLayout(panelQueueLayout);
        panelQueueLayout.setHorizontalGroup(
            panelQueueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelQueueLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 642, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(panelQueueLayout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(140, Short.MAX_VALUE))
        );
        panelQueueLayout.setVerticalGroup(
            panelQueueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelQueueLayout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
                .addContainerGap())
        );

        contentPanel.add(panelQueue, "cardQueue");

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
        lblHeaderTitle.setText("FRONT DESK OVERVIEW");
        setActiveButton(btnDashboard);
    }//GEN-LAST:event_btnDashboardActionPerformed

    private void btnStudentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStudentsActionPerformed
        CardLayout cl = (CardLayout)(contentPanel.getLayout());
        cl.show(contentPanel, "cardStudents");
        lblHeaderTitle.setText("MANAGE STUDENT ACCOUNTS");
        setActiveButton(btnStudents);
    }//GEN-LAST:event_btnStudentsActionPerformed

    private void btnAppointmentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAppointmentsActionPerformed
        CardLayout cl = (CardLayout)(contentPanel.getLayout());
        cl.show(contentPanel, "cardAppointments");
        lblHeaderTitle.setText("MANAGE APPOINTMENTS");
        setActiveButton(btnAppointments);
    }//GEN-LAST:event_btnAppointmentsActionPerformed

    private void btnQueueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQueueActionPerformed
        CardLayout cl = (CardLayout)(contentPanel.getLayout());
        cl.show(contentPanel, "cardQueue");
        lblHeaderTitle.setText("WALK-IN QUEUE MANAGEMENT");
        setActiveButton(btnQueue);
    }//GEN-LAST:event_btnQueueActionPerformed

    private void btnAddStudentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddStudentActionPerformed
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIUtils.VINTAGE_PANEL);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtUsername = new JTextField();
        JPasswordField txtPassword = new JPasswordField();
        JTextField txtFullName = new JTextField();
        JTextField txtStudentNo = new JTextField("TP000000");
        JTextField txtEmail = new JTextField("@mail.apu.edu.my");
        JTextField txtPhone = new JTextField("0123456789");
        JTextField txtProgramme = new JTextField("Computer Science");
        JTextField txtIntake = new JTextField("UCDF2507ICT(SE)");

        styleInputField(txtUsername); styleInputField(txtPassword); styleInputField(txtFullName);
        styleInputField(txtStudentNo); styleInputField(txtEmail); styleInputField(txtPhone);
        styleInputField(txtProgramme); styleInputField(txtIntake);
        
        JLabel lblUser = new JLabel("Username:"); lblUser.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblPass = new JLabel("Password:"); lblPass.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblName = new JLabel("Full Name:"); lblName.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblNo = new JLabel("Student No:"); lblNo.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblEmail = new JLabel("Email:"); lblEmail.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblPhone = new JLabel("Phone:"); lblPhone.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblProg = new JLabel("Programme:"); lblProg.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblIntake = new JLabel("Intake:"); lblIntake.setForeground(UIUtils.VINTAGE_CREAM);

        gbc.gridx = 0; gbc.gridy = 0; panel.add(lblUser, gbc);
        gbc.gridx = 1; panel.add(txtUsername, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(lblPass, gbc);
        gbc.gridx = 1; panel.add(txtPassword, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(lblName, gbc);
        gbc.gridx = 1; panel.add(txtFullName, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panel.add(lblNo, gbc);
        gbc.gridx = 1; panel.add(txtStudentNo, gbc);
        gbc.gridx = 0; gbc.gridy = 4; panel.add(lblEmail, gbc);
        gbc.gridx = 1; panel.add(txtEmail, gbc);
        gbc.gridx = 0; gbc.gridy = 5; panel.add(lblPhone, gbc);
        gbc.gridx = 1; panel.add(txtPhone, gbc);
        gbc.gridx = 0; gbc.gridy = 6; panel.add(lblProg, gbc);
        gbc.gridx = 1; panel.add(txtProgramme, gbc);
        gbc.gridx = 0; gbc.gridy = 7; panel.add(lblIntake, gbc);
        gbc.gridx = 1; panel.add(txtIntake, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "REGISTER NEW STUDENT", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                User newUser = userService.createUser(
                    txtUsername.getText().trim(),
                    new String(txtPassword.getPassword()).trim(),
                    "Student",
                    txtFullName.getText().trim()
                );
                
                studentService.saveProfile(
                    newUser.getUserId(),
                    txtStudentNo.getText().trim(),
                    txtEmail.getText().trim(),
                    txtPhone.getText().trim(),
                    txtProgramme.getText().trim(),
                    txtIntake.getText().trim()
                );
                
                JOptionPane.showMessageDialog(this, "Student account created successfully!", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                loadStudentsTable();
                loadDashboardStats();
            } catch (InvalidInputException | DataNotFoundException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "VALIDATION ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnAddStudentActionPerformed

    private void btnEditStudentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditStudentActionPerformed
        int selectedRow = tblStudents.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to edit.", "NO SELECTION", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String userId = tblStudents.getValueAt(selectedRow, 0).toString();
        StudentProfile existing = studentService.findProfileByUserId(userId);
        if (existing == null) return;

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIUtils.getPanelColor());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtStudentNo = new JTextField(existing.getStudentNo());
        JTextField txtEmail = new JTextField(existing.getEmail());
        JTextField txtPhone = new JTextField(existing.getPhone());
        JTextField txtProgramme = new JTextField(existing.getProgramme());
        JTextField txtIntake = new JTextField(existing.getIntake());

        styleInputField(txtStudentNo); styleInputField(txtEmail); styleInputField(txtPhone);
        styleInputField(txtProgramme); styleInputField(txtIntake);
        
        JLabel lblNo = new JLabel("Student No:"); lblNo.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblEmail = new JLabel("Email:"); lblEmail.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblPhone = new JLabel("Phone:"); lblPhone.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblProg = new JLabel("Programme:"); lblProg.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblIntake = new JLabel("Intake:"); lblIntake.setForeground(UIUtils.VINTAGE_CREAM);

        gbc.gridx = 0; gbc.gridy = 0; panel.add(lblNo, gbc);
        gbc.gridx = 1; panel.add(txtStudentNo, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(lblEmail, gbc);
        gbc.gridx = 1; panel.add(txtEmail, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(lblPhone, gbc);
        gbc.gridx = 1; panel.add(txtPhone, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panel.add(lblProg, gbc);
        gbc.gridx = 1; panel.add(txtProgramme, gbc);
        gbc.gridx = 0; gbc.gridy = 4; panel.add(lblIntake, gbc);
        gbc.gridx = 1; panel.add(txtIntake, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "EDIT STUDENT PROFILE", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                studentService.saveProfile(userId, txtStudentNo.getText(), txtEmail.getText(), txtPhone.getText(), txtProgramme.getText(), txtIntake.getText());
                JOptionPane.showMessageDialog(this, "Profile updated successfully!", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                loadStudentsTable();
            } catch (InvalidInputException | DataNotFoundException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnEditStudentActionPerformed

    private void btnToggleStudentStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToggleStudentStatusActionPerformed
        int selectedRow = tblStudents.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student.", "NO SELECTION", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String userId = tblStudents.getValueAt(selectedRow, 0).toString();
        String currentStatus = tblStudents.getValueAt(selectedRow, 7).toString();
        String newStatus = currentStatus.equals(User.STATUS_ACTIVE) ? User.STATUS_INACTIVE : User.STATUS_ACTIVE;

        try {
            userService.setUserStatus(userId, newStatus);
            JOptionPane.showMessageDialog(this, "Student status updated to " + newStatus, "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
            loadStudentsTable();
        } catch (InvalidInputException | DataNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnToggleStudentStatusActionPerformed

    private void btnBookApptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBookApptActionPerformed
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIUtils.VINTAGE_PANEL);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtStudentId = new JTextField("U4"); 
        JTextField txtCounselorId = new JTextField("U2"); 
        String[] types = {Appointment.TYPE_ONLINE, Appointment.TYPE_WALK_IN};
        JComboBox<String> cmbType = new JComboBox<>(types);
        JTextField txtDate = new JTextField("2026-06-15");
        JTextField txtStart = new JTextField("10:00");
        JTextField txtEnd = new JTextField("10:30");
        JTextField txtReason = new JTextField("Walk-in consultation");

        styleInputField(txtStudentId); styleInputField(txtCounselorId); styleInputField(txtDate);
        styleInputField(txtStart); styleInputField(txtEnd); styleInputField(txtReason);
        
        JLabel lblStudent = new JLabel("Student ID:"); lblStudent.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblCounselor = new JLabel("Counselor ID:"); lblCounselor.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblType = new JLabel("Type:"); lblType.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblDate = new JLabel("Date:"); lblDate.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblStart = new JLabel("Start:"); lblStart.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblEnd = new JLabel("End:"); lblEnd.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblReason = new JLabel("Reason:"); lblReason.setForeground(UIUtils.VINTAGE_CREAM);

        gbc.gridx = 0; gbc.gridy = 0; panel.add(lblStudent, gbc);
        gbc.gridx = 1; panel.add(txtStudentId, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(lblCounselor, gbc);
        gbc.gridx = 1; panel.add(txtCounselorId, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(lblType, gbc);
        gbc.gridx = 1; panel.add(cmbType, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panel.add(lblDate, gbc);
        gbc.gridx = 1; panel.add(txtDate, gbc);
        gbc.gridx = 0; gbc.gridy = 4; panel.add(lblStart, gbc);
        gbc.gridx = 1; panel.add(txtStart, gbc);
        gbc.gridx = 0; gbc.gridy = 5; panel.add(lblEnd, gbc);
        gbc.gridx = 1; panel.add(txtEnd, gbc);
        gbc.gridx = 0; gbc.gridy = 6; panel.add(lblReason, gbc);
        gbc.gridx = 1; panel.add(txtReason, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "BOOK APPOINTMENT", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                apptService.createAppointment(
                    txtStudentId.getText().trim(),
                    txtCounselorId.getText().trim(),
                    cmbType.getSelectedItem().toString(),
                    txtDate.getText().trim(),
                    txtStart.getText().trim(),
                    txtEnd.getText().trim(),
                    txtReason.getText().trim(),
                    loggedInReceptionist.getUserId() // The receptionist is the one creating it
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
        String status = tblAppointments.getValueAt(selectedRow, 6).toString();
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

    private void btnAssignCounselorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAssignCounselorActionPerformed
        int selectedRow = tblAppointments.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment.", "NO SELECTION", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String apptId = tblAppointments.getValueAt(selectedRow, 0).toString();
        String newCounselorId = JOptionPane.showInputDialog(this, "Enter new Counselor ID:", "ASSIGN COUNSELOR", JOptionPane.PLAIN_MESSAGE);

        if (newCounselorId != null && !newCounselorId.trim().isEmpty()) {
            try {
                apptService.assignCounselor(apptId, newCounselorId.trim());
                JOptionPane.showMessageDialog(this, "Counselor assigned successfully!", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                loadAppointmentsTable();
            } catch (InvalidInputException | DataNotFoundException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnAssignCounselorActionPerformed

    private void btnCancelApptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelApptActionPerformed
        int selectedRow = tblAppointments.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to cancel.", "NO SELECTION", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String apptId = tblAppointments.getValueAt(selectedRow, 0).toString();
        String status = tblAppointments.getValueAt(selectedRow, 6).toString();
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

    private void btnGenerateQueueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerateQueueActionPerformed
         int selectedRow = tblAppointments.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a Walk-In appointment first.", "NO SELECTION", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String apptId = tblAppointments.getValueAt(selectedRow, 0).toString();
        String type = tblAppointments.getValueAt(selectedRow, 3).toString();
        if (!type.equals(Appointment.TYPE_WALK_IN)) {
            JOptionPane.showMessageDialog(this, "Queue numbers can only be generated for Walk-In appointments.", "INVALID TYPE", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            QueueNumber qNum = queueService.generateQueueNumber(apptId);
            JOptionPane.showMessageDialog(this, "Queue Number Generated: " + qNum.getDisplayQueueNo(), "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
            loadQueueTable();
            loadDashboardStats();
        } catch (DataNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnGenerateQueueActionPerformed
    
    
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
            java.util.logging.Logger.getLogger(ReceptionistDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddStudent;
    private javax.swing.JButton btnAppointments;
    private javax.swing.JButton btnAssignCounselor;
    private javax.swing.JButton btnBookAppt;
    private javax.swing.JButton btnCancelAppt;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnEditStudent;
    private javax.swing.JButton btnGenerateQueue;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnQueue;
    private javax.swing.JButton btnRescheduleAppt;
    private javax.swing.JButton btnStudents;
    private javax.swing.JButton btnToggleStudentStatus;
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
    private javax.swing.JLabel lblHeaderTitle;
    private javax.swing.JLabel lblReceptionistName;
    private javax.swing.JPanel panelAppointments;
    private javax.swing.JPanel panelDashboard;
    private javax.swing.JPanel panelQueue;
    private javax.swing.JPanel panelStudents;
    private javax.swing.JPanel sidebarPanel;
    private javax.swing.JTable tblAppointments;
    private javax.swing.JTable tblQueue;
    private javax.swing.JTable tblStudents;
    // End of variables declaration//GEN-END:variables
}
