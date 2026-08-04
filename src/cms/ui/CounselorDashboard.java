/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

package cms.ui;

import cms.core.User;
import cms.core.Appointment;
import cms.core.AppointmentService;
import cms.core.Roster;
import cms.core.RosterService;
import cms.core.ConsultationRecord;
import cms.core.ConsultationService;
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

public class CounselorDashboard extends javax.swing.JFrame {

    private User loggedInCounselor;
    private AppointmentService apptService;
    private RosterService rosterService;
    private ConsultationService consultService;

    public CounselorDashboard(User counselor) {
        setUndecorated(true);
        initComponents();
        
        this.loggedInCounselor = counselor;
        this.apptService = new AppointmentService();
        this.rosterService = new RosterService();
        this.consultService = new ConsultationService();
        
        applyDesign();
        loadDashboardStats();
        loadRosterTable();
        loadAppointmentsTable();
        loadRecordsTable();
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
                            JOptionPane.showMessageDialog(CounselorDashboard.this, scrollPane, colName + " Details", JOptionPane.PLAIN_MESSAGE);
                        }
                    }
                }
            }
        });
    }

    private void loadDashboardStats() {
        List<Appointment> myAppts = apptService.getAppointmentsForCounselor(loggedInCounselor.getUserId());
        long totalRosters = rosterService.getRostersForCounselor(loggedInCounselor.getUserId()).size();
        
        long totalAppts = myAppts.size();
        long completed = myAppts.stream().filter(a -> a.getStatus().equals(Appointment.STATUS_COMPLETED)).count();
        long upcoming = myAppts.stream().filter(a -> a.getStatus().equals(Appointment.STATUS_BOOKED)).count();

        setupStatCard(card1, "TOTAL SESSIONS", String.valueOf(totalAppts), UIUtils.VINTAGE_GOLD);
        setupStatCard(card2, "COMPLETED", String.valueOf(completed), UIUtils.VINTAGE_CREAM);
        setupStatCard(card3, "UPCOMING", String.valueOf(upcoming), UIUtils.VINTAGE_GOLD);
        setupStatCard(card4, "ROSTER SHIFTS", String.valueOf(totalRosters), UIUtils.VINTAGE_CREAM);
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

    private void loadRosterTable() {
        String[] columns = {"Roster ID", "Date", "Start", "End", "Room", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        for (Roster r : rosterService.getRostersForCounselor(loggedInCounselor.getUserId())) {
            model.addRow(new Object[]{ r.getRosterId(), r.getWorkDate(), r.getStartTime(), r.getEndTime(), r.getRoom(), r.getAvailabilityStatus() });
        }
        tblRoster.setModel(model);
        
        tblRoster.getColumnModel().getColumn(0).setMinWidth(80); 
        tblRoster.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblRoster.getColumnModel().getColumn(1).setMinWidth(100);
        tblRoster.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblRoster.getColumnModel().getColumn(2).setMinWidth(80); 
        tblRoster.getColumnModel().getColumn(2).setPreferredWidth(80);
        tblRoster.getColumnModel().getColumn(3).setMinWidth(80);
        tblRoster.getColumnModel().getColumn(3).setPreferredWidth(80);
        tblRoster.getColumnModel().getColumn(4).setMinWidth(150);
        tblRoster.getColumnModel().getColumn(4).setPreferredWidth(200);
        tblRoster.getColumnModel().getColumn(5).setMinWidth(100); 
        tblRoster.getColumnModel().getColumn(5).setPreferredWidth(100);
        tblRoster.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        styleTableInteraction(tblRoster);
    }

    private void loadAppointmentsTable() {
        String[] columns = {"Appt ID", "Student ID", "Type", "Date", "Time", "Status", "Reason"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        for (Appointment appt : apptService.getAppointmentsForCounselor(loggedInCounselor.getUserId())) {
            model.addRow(new Object[]{ appt.getAppointmentId(), appt.getStudentUserId(), appt.getBookingType(), appt.getAppointmentDate(), appt.getTimeRange(), appt.getStatus(), appt.getReason() });
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
        
        styleTableInteraction(tblAppointments);
    }

    private void loadRecordsTable() {
        String[] columns = {"Record ID", "Appt ID", "Case Notes", "Recommendation", "Follow-up Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        for (ConsultationRecord record : consultService.getRecordsForCounselor(loggedInCounselor.getUserId())) {
            model.addRow(new Object[]{ record.getRecordId(), record.getAppointmentId(), record.getNotes(), record.getRecommendation(), record.getFollowUpDate() });
        }
        tblRecords.setModel(model);
        

        tblRecords.getColumnModel().getColumn(0).setMinWidth(80);
        tblRecords.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblRecords.getColumnModel().getColumn(1).setMinWidth(80);
        tblRecords.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblRecords.getColumnModel().getColumn(2).setMinWidth(250);
        tblRecords.getColumnModel().getColumn(2).setPreferredWidth(300);
        tblRecords.getColumnModel().getColumn(3).setMinWidth(250);
        tblRecords.getColumnModel().getColumn(3).setPreferredWidth(300);
        tblRecords.getColumnModel().getColumn(4).setMinWidth(120);
        tblRecords.getColumnModel().getColumn(4).setPreferredWidth(120);
        tblRecords.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        styleTableInteraction(tblRecords);
    }

    private void setActiveButton(JButton activeBtn) {
        JButton[] buttons = {btnDashboard, btnRoster, btnAppointments, btnRecords};
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
        panelRoster.setBackground(UIUtils.VINTAGE_BG);
        panelAppointments.setBackground(UIUtils.VINTAGE_BG);
        panelRecords.setBackground(UIUtils.VINTAGE_BG);
        card1.setBackground(UIUtils.VINTAGE_ROSE); 
        card2.setBackground(UIUtils.VINTAGE_ROSE);
        card3.setBackground(UIUtils.VINTAGE_ROSE); 
        card4.setBackground(UIUtils.VINTAGE_ROSE);
        lblHeaderTitle.setForeground(UIUtils.VINTAGE_GOLD);
        lblCounselorName.setForeground(UIUtils.VINTAGE_CREAM.darker());
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void styleInputField(JTextField field) {
        field.setBackground(UIUtils.VINTAGE_SHADOW);
        field.setForeground(UIUtils.VINTAGE_CREAM);
        field.setCaretColor(UIUtils.VINTAGE_GOLD);
        field.setBorder(new MatteBorder(0, 0, 2, 0, UIUtils.VINTAGE_GOLD));
        field.setPreferredSize(new Dimension(200, 30));
    }

    private void styleInputField(JTextArea field) {
        field.setBackground(UIUtils.VINTAGE_SHADOW);
        field.setForeground(UIUtils.VINTAGE_CREAM);
        field.setCaretColor(UIUtils.VINTAGE_GOLD);
        field.setBorder(new MatteBorder(0, 0, 2, 0, UIUtils.VINTAGE_GOLD));
        field.setLineWrap(true);
        field.setWrapStyleWord(true);    
    }

    private void applyDesign() {
        setTitle("APU CMS - Counselor Dashboard");
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

        UIUtils.AvatarLabel avatar = new UIUtils.AvatarLabel(loggedInCounselor.getUserId(), loggedInCounselor.getFullName(), 100);
        avatarWrapper.add(avatar);
        avatarWrapper.setBorder(new EmptyBorder(10, 0, 20, 0));
        sidebarPanel.add(avatarWrapper);

        UIUtils.styleMenuButton(btnDashboard);
        UIUtils.styleMenuButton(btnRoster);
        UIUtils.styleMenuButton(btnAppointments);
        UIUtils.styleMenuButton(btnRecords);
        UIUtils.styleMenuButton(btnLogout);
        
        btnDashboard.setText(" DASHBOARD");
        btnRoster.setText(" MY ROSTER");
        btnAppointments.setText(" APPOINTMENTS");
        btnRecords.setText(" CASE NOTES");
        btnLogout.setText(" LOGOUT");

        btnDashboard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnRoster.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnAppointments.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnRecords.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        sidebarPanel.add(btnDashboard);
        sidebarPanel.add(btnRoster);
        sidebarPanel.add(btnAppointments);
        sidebarPanel.add(btnRecords);
        sidebarPanel.add(Box.createVerticalGlue());
        
        lblCounselorName.setText(loggedInCounselor.getFullName());
        lblCounselorName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCounselorName.setForeground(UIUtils.VINTAGE_CREAM.darker()); 
        lblCounselorName.setMaximumSize(new Dimension(230, 50)); 
        lblCounselorName.setHorizontalAlignment(SwingConstants.CENTER);
        lblCounselorName.setBorder(new EmptyBorder(10, 10, 10, 10));
        sidebarPanel.add(lblCounselorName);
        sidebarPanel.add(btnLogout);

        setActiveButton(btnDashboard);
    }

        private void setupContent() {
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 30, 25));
        lblHeaderTitle.setFont(new Font("Georgia", Font.BOLD, 28));
        lblHeaderTitle.setForeground(UIUtils.VINTAGE_GOLD);
        
        UIUtils.styleActionButton(btnWriteNote, UIUtils.VINTAGE_GOLD);
        
        styleTable(tblRoster);
        styleTable(tblAppointments);
        styleTable(tblRecords);

        panelRoster.setLayout(new BorderLayout());
        panelRoster.setBackground(UIUtils.VINTAGE_BG);
        panelRoster.add(jScrollPane1, BorderLayout.CENTER);
        jScrollPane1.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 

        panelAppointments.setLayout(new BorderLayout());
        panelAppointments.setBackground(UIUtils.VINTAGE_BG);
        panelAppointments.add(jPanel2, BorderLayout.NORTH); 
        panelAppointments.add(jScrollPane2, BorderLayout.CENTER); 
        
        jPanel2.setOpaque(true); 
        jPanel2.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); 
        jPanel2.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
        jScrollPane2.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20)); 

        panelRecords.setLayout(new BorderLayout());
        panelRecords.setBackground(UIUtils.VINTAGE_BG);
        panelRecords.add(jScrollPane3, BorderLayout.CENTER);
        jScrollPane3.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
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
        btnRoster = new javax.swing.JButton();
        btnAppointments = new javax.swing.JButton();
        btnRecords = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        lblCounselorName = new javax.swing.JLabel();
        headerPanel = new javax.swing.JPanel();
        lblHeaderTitle = new javax.swing.JLabel();
        contentPanel = new javax.swing.JPanel();
        panelDashboard = new javax.swing.JPanel();
        card1 = new javax.swing.JPanel();
        card2 = new javax.swing.JPanel();
        card3 = new javax.swing.JPanel();
        card4 = new javax.swing.JPanel();
        panelRoster = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblRoster = new javax.swing.JTable();
        panelAppointments = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        btnWriteNote = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblAppointments = new javax.swing.JTable();
        panelRecords = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblRecords = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnDashboard.setText("Dashboard");
        btnDashboard.addActionListener(this::btnDashboardActionPerformed);

        btnRoster.setText("Roster");
        btnRoster.addActionListener(this::btnRosterActionPerformed);

        btnAppointments.setText("Appointments");
        btnAppointments.addActionListener(this::btnAppointmentsActionPerformed);

        btnRecords.setText("Records");
        btnRecords.addActionListener(this::btnRecordsActionPerformed);

        btnLogout.setText("Logout");
        btnLogout.addActionListener(this::btnLogoutActionPerformed);

        lblCounselorName.setText("jLabel1");

        javax.swing.GroupLayout sidebarPanelLayout = new javax.swing.GroupLayout(sidebarPanel);
        sidebarPanel.setLayout(sidebarPanelLayout);
        sidebarPanelLayout.setHorizontalGroup(
            sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidebarPanelLayout.createSequentialGroup()
                .addGroup(sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sidebarPanelLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(lblCounselorName))
                    .addGroup(sidebarPanelLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnDashboard)
                            .addComponent(btnRoster))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sidebarPanelLayout.createSequentialGroup()
                .addGap(0, 15, Short.MAX_VALUE)
                .addGroup(sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAppointments)
                    .addGroup(sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btnLogout)
                        .addComponent(btnRecords)))
                .addGap(21, 21, 21))
        );
        sidebarPanelLayout.setVerticalGroup(
            sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidebarPanelLayout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addComponent(lblCounselorName)
                .addGap(18, 18, 18)
                .addComponent(btnDashboard)
                .addGap(18, 18, 18)
                .addComponent(btnRoster)
                .addGap(18, 18, 18)
                .addComponent(btnAppointments)
                .addGap(18, 18, 18)
                .addComponent(btnRecords)
                .addGap(18, 18, 18)
                .addComponent(btnLogout)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblHeaderTitle.setText("WELCOME COUNSELOR");

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
            .addGap(0, 120, Short.MAX_VALUE)
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
            .addGap(0, 120, Short.MAX_VALUE)
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
            .addGap(0, 120, Short.MAX_VALUE)
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
            .addGap(0, 120, Short.MAX_VALUE)
        );
        card4Layout.setVerticalGroup(
            card4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 364, Short.MAX_VALUE)
        );

        panelDashboard.add(card4);

        contentPanel.add(panelDashboard, "cardDashboard");

        jScrollPane1.setPreferredSize(new java.awt.Dimension(50, 50));

        tblRoster.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblRoster);

        javax.swing.GroupLayout panelRosterLayout = new javax.swing.GroupLayout(panelRoster);
        panelRoster.setLayout(panelRosterLayout);
        panelRosterLayout.setHorizontalGroup(
            panelRosterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRosterLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelRosterLayout.setVerticalGroup(
            panelRosterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRosterLayout.createSequentialGroup()
                .addContainerGap(26, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        contentPanel.add(panelRoster, "cardRoster");

        btnWriteNote.setText("Write Note");
        btnWriteNote.addActionListener(this::btnWriteNoteActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(175, 175, 175)
                .addComponent(btnWriteNote)
                .addContainerGap(177, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(btnWriteNote)
                .addContainerGap(21, Short.MAX_VALUE))
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
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
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

        jScrollPane3.setPreferredSize(new java.awt.Dimension(50, 50));

        tblRecords.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(tblRecords);

        javax.swing.GroupLayout panelRecordsLayout = new javax.swing.GroupLayout(panelRecords);
        panelRecords.setLayout(panelRecordsLayout);
        panelRecordsLayout.setHorizontalGroup(
            panelRecordsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRecordsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelRecordsLayout.setVerticalGroup(
            panelRecordsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRecordsLayout.createSequentialGroup()
                .addContainerGap(26, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        contentPanel.add(panelRecords, "cardRecords");

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
        lblHeaderTitle.setText("COUNSELOR OVERVIEW");
        setActiveButton(btnDashboard);
    }//GEN-LAST:event_btnDashboardActionPerformed

    private void btnRosterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRosterActionPerformed
        CardLayout cl = (CardLayout)(contentPanel.getLayout());
        cl.show(contentPanel, "cardRoster");
        lblHeaderTitle.setText("MY PERSONAL ROSTER");
        setActiveButton(btnRoster);
    }//GEN-LAST:event_btnRosterActionPerformed

    private void btnAppointmentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAppointmentsActionPerformed
        CardLayout cl = (CardLayout)(contentPanel.getLayout());
        cl.show(contentPanel, "cardAppointments");
        lblHeaderTitle.setText("ASSIGNED APPOINTMENTS");
        setActiveButton(btnAppointments);
    }//GEN-LAST:event_btnAppointmentsActionPerformed

    private void btnRecordsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRecordsActionPerformed
        CardLayout cl = (CardLayout)(contentPanel.getLayout());
        cl.show(contentPanel, "cardRecords");
        lblHeaderTitle.setText("STUDENT CONSULTATION RECORDS");
        setActiveButton(btnRecords);
    }//GEN-LAST:event_btnRecordsActionPerformed

    private void btnWriteNoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnWriteNoteActionPerformed
         int selectedRow = tblAppointments.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment from the table first.", "NO SELECTION", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String apptId = tblAppointments.getValueAt(selectedRow, 0).toString();
        String currentStatus = tblAppointments.getValueAt(selectedRow, 5).toString();
        
        if (currentStatus.equals(Appointment.STATUS_COMPLETED) || currentStatus.equals(Appointment.STATUS_CANCELLED)) {
            JOptionPane.showMessageDialog(this, "Cannot write notes for a completed or cancelled appointment.", "INVALID STATUS", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIUtils.VINTAGE_PANEL);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextArea txtNotes = new JTextArea(5, 20);
        JTextArea txtRecommendation = new JTextArea(3, 20);
        JTextField txtFollowUp = new JTextField("-");
        
        styleInputField(txtNotes); 
        styleInputField(txtRecommendation); 
        styleInputField(txtFollowUp);
        
        JScrollPane notesScroll = new JScrollPane(txtNotes);
        JScrollPane recScroll = new JScrollPane(txtRecommendation);
        notesScroll.setBorder(null);
        recScroll.setBorder(null);

        JLabel lblNotes = new JLabel("Case Notes:"); lblNotes.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblRec = new JLabel("Recommendation:"); lblRec.setForeground(UIUtils.VINTAGE_CREAM);
        JLabel lblFol = new JLabel("Follow-up (yyyy-MM-dd or '-'):"); lblFol.setForeground(UIUtils.VINTAGE_CREAM);

        gbc.gridx = 0; gbc.gridy = 0; panel.add(lblNotes, gbc);
        gbc.gridx = 1; panel.add(notesScroll, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(lblRec, gbc);
        gbc.gridx = 1; panel.add(recScroll, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(lblFol, gbc);
        gbc.gridx = 1; panel.add(txtFollowUp, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "CASE NOTES FOR " + apptId, 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                consultService.saveConsultation(
                    apptId,
                    txtNotes.getText().trim(),
                    txtRecommendation.getText().trim(),
                    txtFollowUp.getText().trim()
                );
                
                JOptionPane.showMessageDialog(this, "Case note saved! Appointment marked as Completed.", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                
                loadAppointmentsTable();
                loadRecordsTable();
                loadDashboardStats();
                
            } catch (InvalidInputException | DataNotFoundException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "VALIDATION ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnWriteNoteActionPerformed
    
    
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
            java.util.logging.Logger.getLogger(CounselorDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAppointments;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnRecords;
    private javax.swing.JButton btnRoster;
    private javax.swing.JButton btnWriteNote;
    private javax.swing.JPanel card1;
    private javax.swing.JPanel card2;
    private javax.swing.JPanel card3;
    private javax.swing.JPanel card4;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblCounselorName;
    private javax.swing.JLabel lblHeaderTitle;
    private javax.swing.JPanel panelAppointments;
    private javax.swing.JPanel panelDashboard;
    private javax.swing.JPanel panelRecords;
    private javax.swing.JPanel panelRoster;
    private javax.swing.JPanel sidebarPanel;
    private javax.swing.JTable tblAppointments;
    private javax.swing.JTable tblRecords;
    private javax.swing.JTable tblRoster;
    // End of variables declaration//GEN-END:variables
}
