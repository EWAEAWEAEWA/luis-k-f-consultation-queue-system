
package com.consultation.view;

// ----- NECESSARY IMPORTS -----
import com.consultation.controller.ConsultationController;
import com.consultation.model.*; // Import all models including TimeSlot

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter; // Ensure specific DateTimeFormatter is used if needed
import java.util.ArrayList; // Use java.util.ArrayList
import java.util.Calendar; // For JSpinner Date model
import java.util.Comparator; // Use java.util.Comparator
import java.util.Date; // For JSpinner Date model
import java.util.List; // Use java.util.List (NOT java.awt.List)
import java.util.Map; // Need this for analytics
import java.util.stream.Collectors; // Use java.util.stream.Collectors
// ----- END IMPORTS -----

/**
 * Main GUI class for the Consultation Queue System.
 * Provides user interface for login, registration, booking, viewing appointments,
 * managing queues (for staff), managing schedule (for staff), viewing staff availability (for students)
 * and viewing notifications. Includes basic analytics view for staff.
 */
public class ConsultationGUI extends JFrame {
    private ConsultationController controller;
    private User currentUser;

    // Main Panels
    private JPanel mainCardPanel;
    private CardLayout mainCardLayout;
    private JPanel loginPanel;
    private JPanel dashboardPanel;

    // Dashboard Components
    private JPanel leftNavPanel;
    private JPanel centerContentPanel;
    private CardLayout centerCardLayout;
    private JPanel rightNotificationsPanel;

    // --- Styling Constants ---
    // (Keep existing styling constants)
    private static final Color COLOR_BACKGROUND = new Color(245, 247, 250);
    private static final Color COLOR_BACKGROUND_DARK = new Color(45, 55, 72);
    private static final Color COLOR_PRIMARY = new Color(59, 130, 246);
    private static final Color COLOR_PRIMARY_DARK = new Color(37, 99, 235);
    private static final Color COLOR_SECONDARY = new Color(14, 165, 233);
    private static final Color COLOR_SUCCESS = new Color(16, 185, 129);
    private static final Color COLOR_DANGER = new Color(239, 68, 68);
    private static final Color COLOR_DANGER_DARK = new Color(220, 38, 38);
    private static final Color COLOR_WHITE = Color.WHITE;
    private static final Color COLOR_TEXT_DARK = new Color(31, 41, 55);
    private static final Color COLOR_TEXT_LIGHT = new Color(229, 231, 235);
    private static final Color COLOR_TEXT_MUTED = new Color(107, 114, 128);
    private static final Color COLOR_BORDER = new Color(209, 213, 219);
    private static final Color COLOR_TABLE_HEADER = new Color(243, 244, 246);
    private static final Color COLOR_TABLE_ROW_ODD = new Color(249, 250, 251);
    private static final Color COLOR_TABLE_ROW_EVEN = COLOR_WHITE;
    private static final Color COLOR_TABLE_SELECTION_BG = new Color(191, 219, 254);
    private static final Color COLOR_TABLE_SELECTION_FG = COLOR_TEXT_DARK;
    private static final Color COLOR_NOTIFICATION_READ_BG = COLOR_TABLE_HEADER;
    private static final Color COLOR_NOTIFICATION_READ_FG = COLOR_TEXT_MUTED;

    private static final Font FONT_MAIN = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    private static final Border BORDER_PANEL_PADDING = new EmptyBorder(20, 25, 20, 25);
    private static final Border BORDER_COMPONENT_PADDING = new EmptyBorder(5, 8, 5, 8);
    private static final Border BORDER_BUTTON_PADDING = new EmptyBorder(8, 18, 8, 18);
    private static final Border BORDER_INPUT_DEFAULT = new CompoundBorder(new LineBorder(COLOR_BORDER, 1), BORDER_COMPONENT_PADDING);


    // --- Card names ---
    private static final String CARD_LOGIN = "LOGIN";
    private static final String CARD_DASHBOARD = "DASHBOARD";
    private static final String CARD_BOOK_APPOINTMENT = "BookAppointment";
    private static final String CARD_MY_APPOINTMENTS = "MyAppointments";
    private static final String CARD_STAFF_AVAILABILITY = "StaffAvailability"; // Student view
    private static final String CARD_NOTIFICATIONS = "Notifications"; // Common center view
    private static final String CARD_QUEUE_STATUS_STAFF = "QueueStatusStaff"; // Staff view
    private static final String CARD_MANAGE_APPOINTMENTS = "ManageAppointments"; // Staff view
    private static final String CARD_MANAGE_SCHEDULE = "ManageSchedule"; // Staff view
    private static final String CARD_STAFF_ANALYTICS = "StaffAnalytics"; // Staff view


    // --- Table Models ---
    private DefaultTableModel myAppointmentsTableModel;
    private DefaultTableModel queueStatusStaffTableModel;
    private DefaultTableModel manageAppointmentsTableModel;
    private DefaultTableModel staffAvailabilityTableModel;


    // --- Components needing refresh/access ---
    // Staff Queue Panel
    private JPanel queueSizeBox;
    private JPanel avgWaitTimeBox;
    private JPanel completedTodayBox;
    // Notifications
    private JList<Notification> notificationList;
    private DefaultListModel<Notification> notificationListModel;
    // Staff Schedule Panel
    private JList<TimeSlot> scheduleList;
    private DefaultListModel<TimeSlot> scheduleListModel;
    private JSpinner dateSpinner;
    // Staff Analytics Panel Labels
    private JLabel analyticsTotalWeekLabel;
    private JLabel analyticsTotalMonthLabel;
    private JLabel analyticsAvgDurationLabel;
    private JLabel analyticsTopSubjectLabel;
    private JLabel analyticsPeakDayLabel;


    // --- Constructor ---
    public ConsultationGUI(ConsultationController controller) {
        if (controller == null) {
             throw new IllegalArgumentException("ConsultationController cannot be null");
        }
        this.controller = controller;
        initializeUI();
    }

    // --- UI Initialization ---
    private void initializeUI() {
        setTitle("Consultation Queue System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);

        mainCardLayout = new CardLayout();
        mainCardPanel = new JPanel(mainCardLayout);
        mainCardPanel.setBackground(COLOR_BACKGROUND);

        loginPanel = createLoginPanel();
        dashboardPanel = createDashboardPanel(); // Only creates structure initially

        mainCardPanel.add(loginPanel, CARD_LOGIN);
        mainCardPanel.add(dashboardPanel, CARD_DASHBOARD);

        add(mainCardPanel);
        showLoginPanel(); // Start with the login view
    }

    // --- Panel Creation Methods ---

    /** Creates the login panel */
    private JPanel createLoginPanel() {
        // (Implementation from previous answer is correct - keep it)
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(new EmptyBorder(50, 50, 50, 50));
        GridBagConstraints gbc = new GridBagConstraints();

        // Title
        JLabel titleLabel = new JLabel("TIP Consultation Queue System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(COLOR_TEXT_DARK);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(0, 0, 50, 0); gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        // Form Area
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(COLOR_WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(COLOR_BORDER, 1), new EmptyBorder(40, 50, 40, 50)));
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(12, 10, 12, 10);
        formGbc.anchor = GridBagConstraints.LINE_END;

        // Components
        JTextField usernameField = createStyledTextField(25);
        JPasswordField passwordField = createStyledPasswordField(25);
        JButton loginButton = createStyledButton("Login", COLOR_PRIMARY, COLOR_WHITE);
        JButton registerButton = createStyledButton("Register", COLOR_SECONDARY, COLOR_WHITE);
        addHoverEffect(loginButton, COLOR_PRIMARY, COLOR_PRIMARY_DARK);
        addHoverEffect(registerButton, COLOR_SECONDARY, COLOR_SECONDARY.darker());

        // Layout Form Components
        formGbc.gridx = 0; formGbc.gridy = 0; formPanel.add(createStyledLabel("Username:"), formGbc);
        formGbc.gridx = 1; formGbc.anchor = GridBagConstraints.LINE_START; formPanel.add(usernameField, formGbc);

        formGbc.gridx = 0; formGbc.gridy = 1; formGbc.anchor = GridBagConstraints.LINE_END; formPanel.add(createStyledLabel("Password:"), formGbc);
        formGbc.gridx = 1; formGbc.anchor = GridBagConstraints.LINE_START; formPanel.add(passwordField, formGbc);

        // Button Panel within Form
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(COLOR_WHITE); // Match form background
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0)); // Space above buttons
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        formGbc.gridx = 0; formGbc.gridy = 2; formGbc.gridwidth = 2; formGbc.anchor = GridBagConstraints.CENTER; formGbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(buttonPanel, formGbc);

        // Add form panel to main login panel
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER; gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(formPanel, gbc);

        // --- Action Listeners ---
        loginButton.addActionListener(e -> handleLogin(usernameField.getText(), new String(passwordField.getPassword())));
        passwordField.addActionListener(e -> handleLogin(usernameField.getText(), new String(passwordField.getPassword()))); // Allow login on Enter in password field
        registerButton.addActionListener(e -> showRegistrationDialog());

        return panel;
    }

    /** Creates the main dashboard structure (Nav, Center, Notifications) */
    private JPanel createDashboardPanel() {
        // (Implementation from previous answer is correct - keep it)
         JPanel panel = new JPanel(new BorderLayout(15, 15)); // Gaps between panels
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15)); // Padding around dashboard

        leftNavPanel = new JPanel(); leftNavPanel.setBackground(COLOR_BACKGROUND_DARK);

        centerCardLayout = new CardLayout();
        centerContentPanel = new JPanel(centerCardLayout);
        centerContentPanel.setBackground(COLOR_WHITE); centerContentPanel.setBorder(new LineBorder(COLOR_BORDER, 1));

        rightNotificationsPanel = createNotificationsPanelInternal();

        panel.add(leftNavPanel, BorderLayout.WEST);
        panel.add(centerContentPanel, BorderLayout.CENTER);
        panel.add(rightNotificationsPanel, BorderLayout.EAST);

        return panel;
    }

    // --- Dashboard Setup Helpers ---

    /** Sets up the entire dashboard after successful login */
    private void setupDashboard() {
        if (currentUser == null) { System.err.println("Error: setupDashboard called but currentUser is null."); showLoginPanel(); return; }
        System.out.println("Setting up dashboard for: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        setupLeftNavPanel();        // Configure nav
        setupCenterContentPanel();  // Create/Add center panels
        refreshAllViews();          // Populate with data
        showDefaultViewForRole();   // Show starting panel
    }

    /** Configures the left navigation panel based on the currentUser's role */
    private void setupLeftNavPanel() {
        // (Implementation from previous answer is correct - keep it, ensuring analytics button added for staff)
        if (currentUser == null) return;
        leftNavPanel.removeAll();
        leftNavPanel.setLayout(new BoxLayout(leftNavPanel, BoxLayout.Y_AXIS));
        leftNavPanel.setBackground(COLOR_BACKGROUND_DARK);
        leftNavPanel.setPreferredSize(new Dimension(220, 0));
        leftNavPanel.setBorder(new EmptyBorder(25, 15, 25, 15));

        // User Info
        JPanel userInfoPanel = new JPanel(); userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS)); userInfoPanel.setBackground(COLOR_BACKGROUND_DARK); userInfoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel nameLabel = new JLabel(currentUser.getName()); nameLabel.setFont(FONT_HEADER); nameLabel.setForeground(COLOR_TEXT_LIGHT); nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT); userInfoPanel.add(nameLabel);
        JLabel roleLabel = new JLabel(currentUser.getRole()); roleLabel.setFont(FONT_SMALL); roleLabel.setForeground(COLOR_TEXT_MUTED); roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); userInfoPanel.add(roleLabel);
        leftNavPanel.add(userInfoPanel);
        leftNavPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Separator
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL); separator.setForeground(COLOR_TEXT_MUTED.darker()); separator.setBackground(COLOR_TEXT_MUTED.darker()); separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        leftNavPanel.add(separator);
        leftNavPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Role-Specific Buttons
        String role = currentUser.getRole();
        if (role.equals("STUDENT")) {
            leftNavPanel.add(createNavButton("Book Appointment", CARD_BOOK_APPOINTMENT));
            leftNavPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            leftNavPanel.add(createNavButton("My Appointments", CARD_MY_APPOINTMENTS));
            leftNavPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            leftNavPanel.add(createNavButton("Staff Availability", CARD_STAFF_AVAILABILITY)); // Student view
            leftNavPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            leftNavPanel.add(createNavButton("Notifications", CARD_NOTIFICATIONS));
        } else if (role.equals("PROFESSOR") || role.equals("COUNSELOR")) {
            leftNavPanel.add(createNavButton("Queue Status", CARD_QUEUE_STATUS_STAFF));
            leftNavPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            leftNavPanel.add(createNavButton("Manage Appointments", CARD_MANAGE_APPOINTMENTS));
            leftNavPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            leftNavPanel.add(createNavButton("Manage Schedule", CARD_MANAGE_SCHEDULE));
            leftNavPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            // --- Add Analytics Button ---
             leftNavPanel.add(createNavButton("My Analytics", CARD_STAFF_ANALYTICS)); // <-- Added
             leftNavPanel.add(Box.createRigidArea(new Dimension(0, 8)));
             // ---
            leftNavPanel.add(createNavButton("Notifications", CARD_NOTIFICATIONS));
        }

        // Logout Button
        leftNavPanel.add(Box.createVerticalGlue());
        JButton logoutButton = createNavButton("Logout", "LOGOUT_ACTION"); logoutButton.setBackground(COLOR_DANGER_DARK.darker()); logoutButton.setForeground(COLOR_WHITE);
        addHoverEffect(logoutButton, COLOR_DANGER_DARK.darker(), COLOR_DANGER);
        leftNavPanel.add(logoutButton);

        leftNavPanel.revalidate(); leftNavPanel.repaint();
    }

    /** Determines and shows the default central panel based on user role */
    private void showDefaultViewForRole() {
        // (Implementation from previous answer is correct - keep it)
        if (currentUser == null) return;
        String defaultCard = "";
        switch (currentUser.getRole()) {
            case "STUDENT":     defaultCard = CARD_STAFF_AVAILABILITY; break;
            case "PROFESSOR":
            case "COUNSELOR":   defaultCard = CARD_QUEUE_STATUS_STAFF; break;
            default: System.err.println("WARN: showDefaultViewForRole - Unknown role: " + currentUser.getRole()); break;
        }
        if (!defaultCard.isEmpty()) {
             refreshSpecificView(defaultCard);
             centerCardLayout.show(centerContentPanel, defaultCard);
        } else {
            System.err.println("Could not determine default view, defaulting to login.");
            showLoginPanel();
        }
    }

    /** Factory method for creating styled navigation buttons */
    private JButton createNavButton(String text, String actionCommand) {
        // (Implementation from previous answer is correct - keep it)
        JButton button = new JButton(text); button.setFont(FONT_BOLD); button.setForeground(COLOR_TEXT_LIGHT); button.setBackground(COLOR_BACKGROUND_DARK); button.setOpaque(true); button.setBorder(new EmptyBorder(12, 20, 12, 20)); button.setFocusPainted(false); button.setBorderPainted(false); button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); button.setHorizontalAlignment(SwingConstants.LEFT); button.setAlignmentX(Component.LEFT_ALIGNMENT); button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height));
        if (!actionCommand.equals("LOGOUT_ACTION")) { Color baseBg = button.getBackground(); Color hoverBg = COLOR_PRIMARY_DARK; button.addMouseListener(new MouseAdapter() { @Override public void mouseEntered(MouseEvent e) { if(button.isEnabled()) button.setBackground(hoverBg); } @Override public void mouseExited(MouseEvent e) { if(button.isEnabled()) button.setBackground(baseBg); } }); }
        button.addActionListener(e -> handleNavAction(actionCommand)); return button;
    }

    /** Populates the center panel container with all possible view panels */
    private void setupCenterContentPanel() {
        // (Implementation from previous answer, adding analytics card)
         centerContentPanel.removeAll();

         // Student Panels
         centerContentPanel.add(createBookAppointmentPanel(), CARD_BOOK_APPOINTMENT);
         centerContentPanel.add(createMyAppointmentsPanel(), CARD_MY_APPOINTMENTS);
         centerContentPanel.add(createStaffAvailabilityPanel(), CARD_STAFF_AVAILABILITY);

         // Staff Panels
         centerContentPanel.add(createQueueStatusStaffPanel(), CARD_QUEUE_STATUS_STAFF);
         centerContentPanel.add(createManageAppointmentsPanel(), CARD_MANAGE_APPOINTMENTS);
         centerContentPanel.add(createManageSchedulePanel(), CARD_MANAGE_SCHEDULE);
         centerContentPanel.add(createStaffAnalyticsPanel(), CARD_STAFF_ANALYTICS); // <-- Added

         // Common Panels
         centerContentPanel.add(createNotificationsViewPanel(), CARD_NOTIFICATIONS);

         centerContentPanel.revalidate(); centerContentPanel.repaint();
    }

    // --- Content Panel Creation Methods ---

    /** Creates the panel for students to book appointments */
    private JPanel createBookAppointmentPanel() {
        // (Implementation from previous answer is correct - keep it)
         JPanel panel = new JPanel(new BorderLayout(15, 15)); panel.setBackground(COLOR_WHITE); panel.setBorder(BORDER_PANEL_PADDING); JLabel titleLabel = new JLabel("Book Appointment"); titleLabel.setFont(FONT_TITLE); titleLabel.setForeground(COLOR_TEXT_DARK); titleLabel.setBorder(new EmptyBorder(0, 0, 25, 0)); panel.add(titleLabel, BorderLayout.NORTH);
         JPanel formGrid = new JPanel(new GridBagLayout()); formGrid.setBackground(COLOR_WHITE); GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(8, 5, 8, 15); gbc.anchor = GridBagConstraints.LINE_END;
         JComboBox<String> professorComboBox = createStyledComboBox(); JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(30, 15, 120, 15)); durationSpinner.setFont(FONT_MAIN); ((JSpinner.DefaultEditor) durationSpinner.getEditor()).getTextField().setColumns(5); JComboBox<String> subjectComboBox = createStyledComboBox(); JTextArea descriptionArea = new JTextArea(5, 30); descriptionArea.setFont(FONT_MAIN); descriptionArea.setLineWrap(true); descriptionArea.setWrapStyleWord(true); JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea); descriptionScrollPane.setBorder(new LineBorder(COLOR_BORDER, 1));
         subjectComboBox.removeAllItems(); if (currentUser != null && currentUser.getRole().equals("STUDENT")) { subjectComboBox.addItem("-- Select Subject --"); if (currentUser.getSubjects() != null) { currentUser.getSubjects().stream().sorted().forEach(subjectComboBox::addItem); } subjectComboBox.addItem("Academic Advising"); subjectComboBox.setEnabled(true); } else { subjectComboBox.addItem("-- Login as Student --"); subjectComboBox.setEnabled(false); }
         int gridY = 0; gbc.gridx = 0; gbc.gridy = gridY; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0; formGrid.add(createStyledLabel("Subject:"), gbc); gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.LINE_START; formGrid.add(subjectComboBox, gbc);
         gridY++; gbc.gridx = 0; gbc.gridy = gridY; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0; gbc.anchor = GridBagConstraints.LINE_END; formGrid.add(createStyledLabel("Professor/Counselor:"), gbc); gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.LINE_START; formGrid.add(professorComboBox, gbc);
         gridY++; gbc.gridx = 0; gbc.gridy = gridY; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0; gbc.anchor = GridBagConstraints.LINE_END; formGrid.add(createStyledLabel("Est. Duration (min):"), gbc); gbc.gridx = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0; gbc.anchor = GridBagConstraints.LINE_START; formGrid.add(durationSpinner, gbc);
         gridY++; gbc.gridx = 0; gbc.gridy = gridY; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0; gbc.anchor = GridBagConstraints.NORTHEAST; gbc.insets = new Insets(8, 5, 8, 15); formGrid.add(createStyledLabel("Description (Optional):"), gbc); gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0; gbc.anchor = GridBagConstraints.LINE_START; formGrid.add(descriptionScrollPane, gbc);
         panel.add(formGrid, BorderLayout.CENTER);
         JButton bookButton = createStyledButton("Book Next Available Slot", COLOR_PRIMARY, COLOR_WHITE); addHoverEffect(bookButton, COLOR_PRIMARY, COLOR_PRIMARY_DARK); JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); buttonPanel.setBackground(COLOR_WHITE); buttonPanel.setBorder(new EmptyBorder(25, 0, 10, 0)); buttonPanel.add(bookButton); panel.add(buttonPanel, BorderLayout.SOUTH);
         subjectComboBox.addActionListener(e -> populateStaffComboBox(subjectComboBox, professorComboBox)); populateStaffComboBox(subjectComboBox, professorComboBox);
         bookButton.addActionListener(e -> handleBookAppointment(professorComboBox, subjectComboBox, durationSpinner, descriptionArea));
         return panel;
    }

    /** Creates the panel for students to view their own past and present appointments */
    private JPanel createMyAppointmentsPanel() {
        // (Implementation from previous answer is correct - keep it)
         JPanel panel = new JPanel(new BorderLayout(15, 15)); panel.setBackground(COLOR_WHITE); panel.setBorder(BORDER_PANEL_PADDING); JLabel tl = new JLabel("My Appointments"); tl.setFont(FONT_TITLE); tl.setForeground(COLOR_TEXT_DARK); tl.setBorder(new EmptyBorder(0, 0, 25, 0)); panel.add(tl, BorderLayout.NORTH);
         String[] columnNames = {"Professor/Counselor", "Date & Time", "Subject", "Status"}; myAppointmentsTableModel = new DefaultTableModel(columnNames, 0) { @Override public boolean isCellEditable(int r, int c){ return false; } }; JTable appointmentsTable = createStyledTable(myAppointmentsTableModel); JScrollPane scrollPane = new JScrollPane(appointmentsTable); scrollPane.setBorder(new LineBorder(COLOR_BORDER)); panel.add(scrollPane, BorderLayout.CENTER);
         JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); buttonPanel.setBackground(COLOR_WHITE); buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0)); JButton cancelButton = createStyledButton("Cancel Selected Pending", COLOR_DANGER, COLOR_WHITE); JButton refreshButton = createStyledButton("Refresh", COLOR_SECONDARY, COLOR_WHITE); addHoverEffect(cancelButton, COLOR_DANGER, COLOR_DANGER_DARK); addHoverEffect(refreshButton, COLOR_SECONDARY, COLOR_SECONDARY.darker()); buttonPanel.add(cancelButton); buttonPanel.add(refreshButton); panel.add(buttonPanel, BorderLayout.SOUTH);
         cancelButton.addActionListener(e -> handleCancelAppointment(appointmentsTable)); refreshButton.addActionListener(e -> { if (currentUser != null) refreshMyAppointmentsTable(myAppointmentsTableModel); });
         return panel;
    }

     /** Creates the panel for Students to view the current availability status and queue length for all Professors and Counselors */
     private JPanel createStaffAvailabilityPanel() {
         // (Implementation from previous answer is correct - keep it)
          JPanel panel = new JPanel(new BorderLayout(15, 15)); panel.setBackground(COLOR_WHITE); panel.setBorder(BORDER_PANEL_PADDING);
          JLabel titleLabel = new JLabel("Staff Availability & Queue Status"); titleLabel.setFont(FONT_TITLE); titleLabel.setForeground(COLOR_TEXT_DARK); titleLabel.setBorder(new EmptyBorder(0, 0, 25, 0)); panel.add(titleLabel, BorderLayout.NORTH);
          String[] columnNames = {"Staff Member", "Role", "Current Queue", "Status / Note"}; staffAvailabilityTableModel = new DefaultTableModel(columnNames, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } @Override public Class<?> getColumnClass(int columnIndex) { if (columnIndex == 2) return Integer.class; return String.class; } }; JTable staffTable = createStyledTable(staffAvailabilityTableModel); staffTable.setAutoCreateRowSorter(true);
          TableColumnModel cm = staffTable.getColumnModel(); cm.getColumn(0).setPreferredWidth(200); cm.getColumn(1).setPreferredWidth(100); cm.getColumn(2).setPreferredWidth(100); cm.getColumn(3).setPreferredWidth(250);
          DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer(); centerRenderer.setHorizontalAlignment(JLabel.CENTER); cm.getColumn(2).setCellRenderer(centerRenderer);
          JScrollPane scrollPane = new JScrollPane(staffTable); scrollPane.setBorder(new LineBorder(COLOR_BORDER)); panel.add(scrollPane, BorderLayout.CENTER);
          JPanel bottomControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); bottomControlsPanel.setBackground(COLOR_WHITE); bottomControlsPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
          JButton refreshButton = createStyledButton("Refresh List", COLOR_SECONDARY, COLOR_WHITE); addHoverEffect(refreshButton, COLOR_SECONDARY, COLOR_SECONDARY.darker()); bottomControlsPanel.add(refreshButton);
          JButton bookButton = createStyledButton("Book Appointment", COLOR_PRIMARY, COLOR_WHITE); addHoverEffect(bookButton, COLOR_PRIMARY, COLOR_PRIMARY_DARK); bookButton.addActionListener(e -> handleNavAction(CARD_BOOK_APPOINTMENT)); bottomControlsPanel.add(bookButton);
          panel.add(bottomControlsPanel, BorderLayout.SOUTH);
          refreshButton.addActionListener(e -> refreshStaffAvailabilityTable(staffAvailabilityTableModel));
          return panel;
     }

     /** Creates the panel for Staff to view and manage their consultation queue */
    private JPanel createQueueStatusStaffPanel() {
        // (Implementation from previous answer is correct - keep it, including ID column changes if made)
         JPanel panel = new JPanel(new BorderLayout(15, 15)); panel.setBackground(COLOR_WHITE); panel.setBorder(BORDER_PANEL_PADDING);
         JLabel titleLabel = new JLabel("My Consultation Queue"); titleLabel.setFont(FONT_TITLE); titleLabel.setForeground(COLOR_TEXT_DARK); titleLabel.setBorder(new EmptyBorder(0, 0, 25, 0)); panel.add(titleLabel, BorderLayout.NORTH);
         // Define columns, including a hidden ID column first
         String[] cn = {"ID", "Pos", "Student", "Time", "Duration", "Subject", "Priority", "Status"}; // Added "ID"
         queueStatusStaffTableModel = new DefaultTableModel(cn, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
         JTable queueTable = createStyledTable(queueStatusStaffTableModel);
         // Hide ID column (index 0)
         TableColumn idCol = queueTable.getColumnModel().getColumn(0); idCol.setMinWidth(0); idCol.setMaxWidth(0); idCol.setPreferredWidth(0); idCol.setResizable(false);
         // Set widths for other columns (indices +1 from original)
         TableColumnModel cm = queueTable.getColumnModel(); cm.getColumn(1).setPreferredWidth(40); cm.getColumn(1).setMaxWidth(60); cm.getColumn(2).setPreferredWidth(150); cm.getColumn(3).setPreferredWidth(130); cm.getColumn(4).setPreferredWidth(70); cm.getColumn(5).setPreferredWidth(180); cm.getColumn(6).setPreferredWidth(60); cm.getColumn(7).setPreferredWidth(90);
         JScrollPane scrollPane = new JScrollPane(queueTable); scrollPane.setBorder(new LineBorder(COLOR_BORDER)); panel.add(scrollPane, BorderLayout.CENTER);
         JPanel southPanel = new JPanel(new BorderLayout(10, 20)); southPanel.setBackground(COLOR_WHITE); southPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
         JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 5)); infoPanel.setBackground(COLOR_WHITE);
         queueSizeBox = createInfoBoxPanel(new JLabel("Queue: 0"), "Current number of PENDING appointments"); avgWaitTimeBox = createInfoBoxPanel(new JLabel("Avg Wait: 0 min"), "Estimated total duration of PENDING appointments"); completedTodayBox = createInfoBoxPanel(new JLabel("Completed: 0"), "Appointments completed today"); infoPanel.add(queueSizeBox); infoPanel.add(avgWaitTimeBox); infoPanel.add(completedTodayBox); southPanel.add(infoPanel, BorderLayout.NORTH);
         JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); buttonPanel.setBackground(COLOR_WHITE); buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
         JButton startNextButton = createStyledButton("Start Next", COLOR_PRIMARY, COLOR_WHITE); JButton completeButton = createStyledButton("Complete Selected", COLOR_SUCCESS, COLOR_WHITE); JButton refreshButton = createStyledButton("Refresh Queue", COLOR_SECONDARY, COLOR_WHITE);
         addHoverEffect(startNextButton, COLOR_PRIMARY, COLOR_PRIMARY_DARK); addHoverEffect(completeButton, COLOR_SUCCESS, COLOR_SUCCESS.darker()); addHoverEffect(refreshButton, COLOR_SECONDARY, COLOR_SECONDARY.darker()); buttonPanel.add(startNextButton); buttonPanel.add(completeButton); buttonPanel.add(refreshButton); southPanel.add(buttonPanel, BorderLayout.CENTER); panel.add(southPanel, BorderLayout.SOUTH);
         refreshButton.addActionListener(e -> { refreshQueueStatusStaffTable(queueStatusStaffTableModel); refreshQueueInfoLabels(); });
         startNextButton.addActionListener(e -> handleStartNextAppointment(queueTable));
         completeButton.addActionListener(e -> handleCompleteAppointment(queueTable));
         return panel;
    }

     /** Creates the panel for Staff to manage their appointments (cancel, delete, prioritize) */
     private JPanel createManageAppointmentsPanel() {
        // (Implementation from previous answer is correct - keep it, including ID column changes if made)
         JPanel panel = new JPanel(new BorderLayout(15, 15)); panel.setBackground(COLOR_WHITE); panel.setBorder(BORDER_PANEL_PADDING);
         JLabel titleLabel = new JLabel("Manage All My Appointments"); titleLabel.setFont(FONT_TITLE); titleLabel.setForeground(COLOR_TEXT_DARK); titleLabel.setBorder(new EmptyBorder(0, 0, 25, 0)); panel.add(titleLabel, BorderLayout.NORTH);
         // Define columns, include ID first (hidden)
         String[] columnNames = {"ID", "Time", "Student", "Subject", "Duration", "Status", "Priority"}; // Added ID
         manageAppointmentsTableModel = new DefaultTableModel(columnNames, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
         JTable appointmentsTable = createStyledTable(manageAppointmentsTableModel);
         // Hide ID column
         TableColumn idCol = appointmentsTable.getColumnModel().getColumn(0); idCol.setMinWidth(0); idCol.setMaxWidth(0); idCol.setPreferredWidth(0); idCol.setResizable(false);
         // Set widths for other columns (indices +1)
         TableColumnModel cm = appointmentsTable.getColumnModel(); cm.getColumn(1).setPreferredWidth(130); cm.getColumn(4).setPreferredWidth(70); cm.getColumn(5).setPreferredWidth(90); cm.getColumn(6).setPreferredWidth(60);
         JScrollPane scrollPane = new JScrollPane(appointmentsTable); scrollPane.setBorder(new LineBorder(COLOR_BORDER)); panel.add(scrollPane, BorderLayout.CENTER);
         JPanel southPanel = new JPanel(new BorderLayout(10, 10)); southPanel.setBackground(COLOR_WHITE); southPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
         JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); filterPanel.setBackground(COLOR_WHITE); filterPanel.add(createStyledLabel("Filter by Status:")); JComboBox<String> filterComboBox = createStyledComboBox(); filterComboBox.addItem("All"); filterComboBox.addItem("Pending"); filterComboBox.addItem("In Progress"); filterComboBox.addItem("Completed"); filterComboBox.addItem("Cancelled"); filterPanel.add(filterComboBox); southPanel.add(filterPanel, BorderLayout.NORTH);
         JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); buttonPanel.setBackground(COLOR_WHITE); buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0)); JButton togglePriorityButton = createStyledButton("Toggle Priority", COLOR_PRIMARY, COLOR_WHITE); JButton deleteButton = createStyledButton("Delete Record", COLOR_DANGER, COLOR_WHITE); JButton refreshButton = createStyledButton("Refresh List", COLOR_SECONDARY, COLOR_WHITE);
         addHoverEffect(togglePriorityButton, COLOR_PRIMARY, COLOR_PRIMARY_DARK); addHoverEffect(deleteButton, COLOR_DANGER, COLOR_DANGER_DARK); addHoverEffect(refreshButton, COLOR_SECONDARY, COLOR_SECONDARY.darker()); buttonPanel.add(togglePriorityButton); buttonPanel.add(deleteButton); buttonPanel.add(refreshButton); southPanel.add(buttonPanel, BorderLayout.CENTER); panel.add(southPanel, BorderLayout.SOUTH);
         filterComboBox.addActionListener(e -> refreshManageAppointmentsTable(manageAppointmentsTableModel, (String) filterComboBox.getSelectedItem())); refreshButton.addActionListener(e -> refreshManageAppointmentsTable(manageAppointmentsTableModel, (String) filterComboBox.getSelectedItem()));
         togglePriorityButton.addActionListener(e -> handleTogglePriority(appointmentsTable, filterComboBox));
         deleteButton.addActionListener(e -> handleDeleteAppointmentRecord(appointmentsTable, filterComboBox));
         return panel;
     }

    /** Creates the panel for Staff to manage their available time slots */
    private JPanel createManageSchedulePanel() {
        // (Implementation from previous answer is correct - keep it)
         JPanel panel = new JPanel(new BorderLayout(15, 15)); panel.setBackground(COLOR_WHITE); panel.setBorder(BORDER_PANEL_PADDING);
         JPanel topPanel = new JPanel(new BorderLayout(10,10)); topPanel.setBackground(COLOR_WHITE); topPanel.setBorder(new EmptyBorder(0,0,20,0));
         JLabel titleLabel = new JLabel("Manage My Availability"); titleLabel.setFont(FONT_TITLE); titleLabel.setForeground(COLOR_TEXT_DARK); topPanel.add(titleLabel, BorderLayout.NORTH);
         JPanel topControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); topControlsPanel.setBackground(COLOR_WHITE); topControlsPanel.add(createStyledLabel("Select Date:"));
         SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH); dateSpinner = new JSpinner(dateModel); JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"); dateSpinner.setEditor(dateEditor); dateSpinner.setFont(FONT_MAIN); dateSpinner.setBorder(BORDER_INPUT_DEFAULT); Dimension spinnerSize = new Dimension(130, dateSpinner.getPreferredSize().height); dateSpinner.setPreferredSize(spinnerSize); dateSpinner.setMaximumSize(spinnerSize); topControlsPanel.add(dateSpinner);
         JButton todayButton = createStyledButton("Today", COLOR_SECONDARY, COLOR_WHITE); addHoverEffect(todayButton, COLOR_SECONDARY, COLOR_SECONDARY.darker()); topControlsPanel.add(todayButton); topPanel.add(topControlsPanel, BorderLayout.CENTER); panel.add(topPanel, BorderLayout.NORTH);
         scheduleListModel = new DefaultListModel<>(); scheduleList = new JList<>(scheduleListModel); scheduleList.setFont(FONT_MAIN); scheduleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); scheduleList.setCellRenderer(new TimeSlotListRenderer()); scheduleList.setBackground(COLOR_WHITE); scheduleList.setBorder(new EmptyBorder(5,5,5,5)); JScrollPane scrollPane = new JScrollPane(scheduleList); scrollPane.setBorder(new LineBorder(COLOR_BORDER)); panel.add(scrollPane, BorderLayout.CENTER);
         JPanel bottomControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); bottomControlsPanel.setBackground(COLOR_WHITE); bottomControlsPanel.setBorder(new EmptyBorder(15, 0, 0, 0)); JButton addButton = createStyledButton("Add New Slot", COLOR_PRIMARY, COLOR_WHITE); JButton removeButton = createStyledButton("Remove Selected Available Slot", COLOR_DANGER, COLOR_WHITE); JButton refreshButton = createStyledButton("Refresh View", COLOR_SECONDARY, COLOR_WHITE); addHoverEffect(addButton, COLOR_PRIMARY, COLOR_PRIMARY_DARK); addHoverEffect(removeButton, COLOR_DANGER, COLOR_DANGER_DARK); addHoverEffect(refreshButton, COLOR_SECONDARY, COLOR_SECONDARY.darker()); bottomControlsPanel.add(addButton); bottomControlsPanel.add(removeButton); bottomControlsPanel.add(refreshButton); panel.add(bottomControlsPanel, BorderLayout.SOUTH);
         dateSpinner.addChangeListener(e -> refreshScheduleList()); todayButton.addActionListener(e -> { dateSpinner.setValue(new Date()); }); addButton.addActionListener(e -> showAddSlotDialog()); removeButton.addActionListener(e -> removeSelectedSlot()); refreshButton.addActionListener(e -> refreshScheduleList());
         return panel;
    }

    /** Creates the panel for Staff members to view basic analytics about their consultations. */
    private JPanel createStaffAnalyticsPanel() {
        // (Implementation from previous answer is correct - keep it)
         JPanel panel = new JPanel(new BorderLayout(15, 25)); panel.setBackground(COLOR_WHITE); panel.setBorder(BORDER_PANEL_PADDING);
         JLabel titleLabel = new JLabel("My Consultation Analytics"); titleLabel.setFont(FONT_TITLE); titleLabel.setForeground(COLOR_TEXT_DARK); titleLabel.setBorder(new EmptyBorder(0, 0, 25, 0)); panel.add(titleLabel, BorderLayout.NORTH);
         JPanel contentPanel = new JPanel(new GridBagLayout()); contentPanel.setBackground(COLOR_WHITE); GridBagConstraints gbc = new GridBagConstraints(); gbc.gridx = 0; gbc.gridy = GridBagConstraints.RELATIVE; gbc.anchor = GridBagConstraints.WEST; gbc.insets = new Insets(8, 0, 8, 20); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.0;
         Font labelFont = FONT_MAIN.deriveFont(14f); Font valueFont = FONT_BOLD.deriveFont(16f);
         analyticsTotalWeekLabel = new JLabel("-"); analyticsTotalMonthLabel = new JLabel("-"); analyticsAvgDurationLabel = new JLabel("- min"); analyticsTopSubjectLabel = new JLabel("N/A"); analyticsPeakDayLabel = new JLabel("N/A");
         analyticsTotalWeekLabel.setFont(valueFont); analyticsTotalWeekLabel.setForeground(COLOR_PRIMARY_DARK); analyticsTotalMonthLabel.setFont(valueFont); analyticsTotalMonthLabel.setForeground(COLOR_PRIMARY_DARK); analyticsAvgDurationLabel.setFont(valueFont); analyticsAvgDurationLabel.setForeground(COLOR_SECONDARY); analyticsTopSubjectLabel.setFont(valueFont); analyticsTopSubjectLabel.setForeground(COLOR_TEXT_DARK); analyticsPeakDayLabel.setFont(valueFont); analyticsPeakDayLabel.setForeground(COLOR_TEXT_DARK);
         contentPanel.add(createStyledLabel("Consultations Completed (This Week):"), gbc); gbc.gridx=1; gbc.weightx=1.0; contentPanel.add(analyticsTotalWeekLabel, gbc); gbc.gridx=0; gbc.weightx=0.0;
         contentPanel.add(createStyledLabel("Consultations Completed (This Month):"), gbc); gbc.gridx=1; gbc.weightx=1.0; contentPanel.add(analyticsTotalMonthLabel, gbc); gbc.gridx=0; gbc.weightx=0.0;
         contentPanel.add(createStyledLabel("Average Duration:"), gbc); gbc.gridx=1; gbc.weightx=1.0; contentPanel.add(analyticsAvgDurationLabel, gbc); gbc.gridx=0; gbc.weightx=0.0;
         contentPanel.add(createStyledLabel("Most Frequent Subject:"), gbc); gbc.gridx=1; gbc.weightx=1.0; contentPanel.add(analyticsTopSubjectLabel, gbc); gbc.gridx=0; gbc.weightx=0.0;
         contentPanel.add(createStyledLabel("Busiest Day of Week:"), gbc); gbc.gridx=1; gbc.weightx=1.0; contentPanel.add(analyticsPeakDayLabel, gbc); gbc.gridx=0; gbc.weightx=0.0;
         JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT)); centerWrapper.setBackground(COLOR_WHITE); centerWrapper.add(contentPanel); panel.add(centerWrapper, BorderLayout.CENTER);
         JPanel bottomControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); bottomControlsPanel.setBackground(COLOR_WHITE); bottomControlsPanel.setBorder(new EmptyBorder(25, 0, 0, 0));
         JButton refreshButton = createStyledButton("Refresh Analytics", COLOR_SECONDARY, COLOR_WHITE); addHoverEffect(refreshButton, COLOR_SECONDARY, COLOR_SECONDARY.darker()); bottomControlsPanel.add(refreshButton); panel.add(bottomControlsPanel, BorderLayout.SOUTH);
         refreshButton.addActionListener(e -> refreshStaffAnalyticsPanel());
         return panel;
     }

     /** Creates the central panel for displaying notifications (similar to right panel but bigger) */
     private JPanel createNotificationsViewPanel() {
        // (Implementation from previous answer is correct - keep it)
         JPanel panel = new JPanel(new BorderLayout(15, 15)); panel.setBackground(COLOR_WHITE); panel.setBorder(BORDER_PANEL_PADDING); JLabel tl = new JLabel("My Notifications"); tl.setFont(FONT_TITLE); tl.setForeground(COLOR_TEXT_DARK); tl.setBorder(new EmptyBorder(0, 0, 25, 0)); panel.add(tl, BorderLayout.NORTH);
         if (notificationListModel == null) notificationListModel = new DefaultListModel<>(); JList<Notification> centerNotificationList = new JList<>(notificationListModel); centerNotificationList.setFont(FONT_MAIN); centerNotificationList.setBackground(COLOR_WHITE); centerNotificationList.setForeground(COLOR_TEXT_DARK); centerNotificationList.setSelectionBackground(COLOR_TABLE_SELECTION_BG); centerNotificationList.setSelectionForeground(COLOR_TABLE_SELECTION_FG); centerNotificationList.setCellRenderer(new NotificationListRenderer()); JScrollPane sp = new JScrollPane(centerNotificationList); sp.setBorder(new LineBorder(COLOR_BORDER)); panel.add(sp, BorderLayout.CENTER);
         JPanel bp = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); bp.setBackground(COLOR_WHITE); bp.setBorder(new EmptyBorder(15, 0, 0, 0)); JButton marb = createStyledButton("Mark Selected Read", COLOR_PRIMARY, COLOR_WHITE); addHoverEffect(marb, COLOR_PRIMARY, COLOR_PRIMARY_DARK); marb.setToolTipText("Mark selected notification as read"); JButton marbAll = createStyledButton("Mark All Read", COLOR_TEXT_MUTED, COLOR_WHITE); addHoverEffect(marbAll, COLOR_TEXT_MUTED, COLOR_TEXT_DARK); marbAll.setToolTipText("Mark all notifications as read"); JButton rb = createStyledButton("Refresh", COLOR_SECONDARY, COLOR_WHITE); addHoverEffect(rb, COLOR_SECONDARY, COLOR_SECONDARY.darker()); bp.add(marb); bp.add(marbAll); bp.add(rb); panel.add(bp, BorderLayout.SOUTH);
         marb.addActionListener(e -> handleMarkOneNotificationRead(centerNotificationList)); marbAll.addActionListener(e -> handleMarkAllNotificationsRead()); rb.addActionListener(e -> refreshNotificationPanels());
         return panel;
     }

     /** Creates the right-side notification panel structure */
     private JPanel createNotificationsPanelInternal() {
         // (Implementation from previous answer is correct - keep it)
         JPanel panel = new JPanel(new BorderLayout(5, 15)); panel.setPreferredSize(new Dimension(280, 0)); panel.setBackground(COLOR_BACKGROUND); panel.setBorder(BorderFactory.createCompoundBorder( new MatteBorder(0, 1, 0, 0, COLOR_BORDER), new EmptyBorder(15, 15, 15, 15) ));
         JPanel headerPanel = new JPanel(new BorderLayout()); headerPanel.setOpaque(false); JLabel titleLabel = new JLabel("Notifications"); titleLabel.setFont(FONT_HEADER); titleLabel.setForeground(COLOR_TEXT_DARK); headerPanel.add(titleLabel, BorderLayout.WEST); JButton clearButton = new JButton("Mark All Read"); clearButton.setFont(FONT_SMALL); clearButton.setForeground(COLOR_TEXT_MUTED); clearButton.setOpaque(false); clearButton.setContentAreaFilled(false); clearButton.setBorderPainted(false); clearButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); clearButton.setToolTipText("Mark all notifications as read"); clearButton.addMouseListener(new MouseAdapter() { @Override public void mouseEntered(MouseEvent e) { clearButton.setForeground(COLOR_DANGER); } @Override public void mouseExited(MouseEvent e) { clearButton.setForeground(COLOR_TEXT_MUTED); } }); clearButton.addActionListener(e -> handleMarkAllNotificationsRead()); headerPanel.add(clearButton, BorderLayout.EAST); panel.add(headerPanel, BorderLayout.NORTH);
         if (notificationListModel == null) notificationListModel = new DefaultListModel<>(); if (notificationList == null) { notificationList = new JList<>(notificationListModel); notificationList.setFont(FONT_MAIN); notificationList.setBackground(COLOR_WHITE); notificationList.setForeground(COLOR_TEXT_DARK); notificationList.setSelectionBackground(COLOR_TABLE_SELECTION_BG); notificationList.setSelectionForeground(COLOR_TABLE_SELECTION_FG); notificationList.setCellRenderer(new NotificationListRenderer()); notificationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); } JScrollPane sp = new JScrollPane(notificationList); sp.setBorder(new LineBorder(COLOR_BORDER)); panel.add(sp, BorderLayout.CENTER); return panel;
     }


    // --- Dialogs ---

     /** Shows the user registration dialog */
    private void showRegistrationDialog() {
    JDialog dialog = new JDialog(this, "Register New User", true);
    dialog.setLayout(new BorderLayout(10, 15));
    dialog.getRootPane().setBorder(new EmptyBorder(20, 25, 20, 25));

    JPanel formPanel = new JPanel(new GridBagLayout());
    formPanel.setBackground(COLOR_WHITE);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 5, 8, 10);
    gbc.anchor = GridBagConstraints.LINE_END;

    JTextField usernameField = createStyledTextField(20);
    JPasswordField passwordField = createStyledPasswordField(20);
    JTextField nameField = createStyledTextField(20);
    JTextField emailField = createStyledTextField(20);
    JComboBox<String> roleComboBox = createStyledComboBox();
    roleComboBox.addItem("STUDENT");
    roleComboBox.addItem("PROFESSOR");
    roleComboBox.addItem("COUNSELOR");

    // --- New Subject Components ---
    JLabel subjectLabel = createStyledLabel("Subject Taught:");
    JTextField subjectField = createStyledTextField(20);
    subjectLabel.setVisible(false); // Initially hidden
    subjectField.setVisible(false); // Initially hidden
    // --- End New Subject Components ---

    int gridY = 0;
    gbc.gridx=0; gbc.gridy=gridY++; formPanel.add(createStyledLabel("Username:"), gbc);
    gbc.gridx=1; gbc.anchor=GridBagConstraints.LINE_START; gbc.fill=GridBagConstraints.HORIZONTAL; formPanel.add(usernameField, gbc);

    gbc.gridx=0; gbc.gridy=gridY++; gbc.anchor = GridBagConstraints.LINE_END; gbc.fill=GridBagConstraints.NONE; formPanel.add(createStyledLabel("Password:"), gbc);
    gbc.gridx=1; gbc.anchor=GridBagConstraints.LINE_START; gbc.fill=GridBagConstraints.HORIZONTAL; formPanel.add(passwordField, gbc);

    gbc.gridx=0; gbc.gridy=gridY++; gbc.anchor = GridBagConstraints.LINE_END; gbc.fill=GridBagConstraints.NONE; formPanel.add(createStyledLabel("Full Name:"), gbc);
    gbc.gridx=1; gbc.anchor=GridBagConstraints.LINE_START; gbc.fill=GridBagConstraints.HORIZONTAL; formPanel.add(nameField, gbc);

    gbc.gridx=0; gbc.gridy=gridY++; gbc.anchor = GridBagConstraints.LINE_END; gbc.fill=GridBagConstraints.NONE; formPanel.add(createStyledLabel("Email:"), gbc);
    gbc.gridx=1; gbc.anchor=GridBagConstraints.LINE_START; gbc.fill=GridBagConstraints.HORIZONTAL; formPanel.add(emailField, gbc);

    gbc.gridx=0; gbc.gridy=gridY++; gbc.anchor = GridBagConstraints.LINE_END; gbc.fill=GridBagConstraints.NONE; formPanel.add(createStyledLabel("Role:"), gbc);
    gbc.gridx=1; gbc.anchor=GridBagConstraints.LINE_START; gbc.fill=GridBagConstraints.HORIZONTAL; formPanel.add(roleComboBox, gbc);

    // --- Add Subject Components to Layout ---
    gbc.gridx=0; gbc.gridy=gridY++; gbc.anchor = GridBagConstraints.LINE_END; gbc.fill=GridBagConstraints.NONE; formPanel.add(subjectLabel, gbc);
    gbc.gridx=1; gbc.anchor=GridBagConstraints.LINE_START; gbc.fill=GridBagConstraints.HORIZONTAL; formPanel.add(subjectField, gbc);
    // --- End Add Subject Components ---

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonPanel.setBackground(COLOR_WHITE);
    JButton registerButton = createStyledButton("Register", COLOR_PRIMARY, COLOR_WHITE);
    JButton cancelButton = createStyledButton("Cancel", COLOR_TEXT_MUTED, COLOR_WHITE);
    addHoverEffect(registerButton, COLOR_PRIMARY, COLOR_PRIMARY_DARK);
    addHoverEffect(cancelButton, COLOR_TEXT_MUTED, COLOR_TEXT_DARK);
    buttonPanel.add(cancelButton);
    buttonPanel.add(registerButton);

    dialog.add(formPanel, BorderLayout.CENTER);
    dialog.add(buttonPanel, BorderLayout.SOUTH);

    // --- Action Listener for Role ComboBox ---
    roleComboBox.addItemListener(e -> {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            boolean isProfessor = "PROFESSOR".equals(e.getItem());
            subjectLabel.setVisible(isProfessor);
            subjectField.setVisible(isProfessor);
            // Repack the dialog to adjust size if fields appear/disappear
            dialog.pack();
        }
    });
    // --- End Action Listener ---

    registerButton.addActionListener(e -> handleRegistration(dialog, usernameField, passwordField, roleComboBox, nameField, emailField, subjectField)); // Pass subjectField
    cancelButton.addActionListener(e -> dialog.dispose());

    dialog.pack(); // Initial pack
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
 }
 // --- END: Modify showRegistrationDialog method ---



     /** Displays a dialog for adding a new time slot */
     private void showAddSlotDialog() {
         // (Implementation from previous answer is correct - keep it)
          if (currentUser == null || dateSpinner == null) return; Date selectedDateUtil = (Date) dateSpinner.getValue(); LocalDate selectedDate = selectedDateUtil.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
          JDialog dialog = new JDialog(this, "Add New Time Slot for " + selectedDate, true); dialog.setLayout(new BorderLayout(10, 15)); dialog.getRootPane().setBorder(new EmptyBorder(20, 25, 20, 25));
          SpinnerDateModel startModel = new SpinnerDateModel(); JSpinner startSpinner = new JSpinner(startModel); JSpinner.DateEditor startTimeEditor = new JSpinner.DateEditor(startSpinner, "HH:mm"); startSpinner.setEditor(startTimeEditor); Calendar startCal = Calendar.getInstance(); startCal.setTime(selectedDateUtil); startCal.set(Calendar.HOUR_OF_DAY, 9); startCal.set(Calendar.MINUTE, 0); startCal.set(Calendar.SECOND, 0); startSpinner.setValue(startCal.getTime());
          SpinnerDateModel endModel = new SpinnerDateModel(); JSpinner endSpinner = new JSpinner(endModel); JSpinner.DateEditor endTimeEditor = new JSpinner.DateEditor(endSpinner, "HH:mm"); endSpinner.setEditor(endTimeEditor); Calendar endCal = Calendar.getInstance(); endCal.setTime(selectedDateUtil); endCal.set(Calendar.HOUR_OF_DAY, 10); endCal.set(Calendar.MINUTE, 0); endCal.set(Calendar.SECOND, 0); endSpinner.setValue(endCal.getTime());
          startSpinner.setFont(FONT_MAIN); endSpinner.setFont(FONT_MAIN); Dimension timeSpinnerSize = new Dimension(80, startSpinner.getPreferredSize().height); startSpinner.setPreferredSize(timeSpinnerSize); endSpinner.setPreferredSize(timeSpinnerSize);
          JPanel inputPanel = new JPanel(new GridBagLayout()); inputPanel.setBackground(COLOR_WHITE); GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(5, 5, 5, 5); gbc.anchor = GridBagConstraints.LINE_END; gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(createStyledLabel("Start Time:"), gbc); gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START; inputPanel.add(startSpinner, gbc); gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.LINE_END; inputPanel.add(createStyledLabel("End Time:"), gbc); gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START; inputPanel.add(endSpinner, gbc);
          JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); buttonPanel.setBackground(COLOR_WHITE); JButton addButton = createStyledButton("Add Slot", COLOR_PRIMARY, COLOR_WHITE); JButton cancelButton = createStyledButton("Cancel", COLOR_TEXT_MUTED, COLOR_WHITE); addHoverEffect(addButton, COLOR_PRIMARY, COLOR_PRIMARY_DARK); addHoverEffect(cancelButton, COLOR_TEXT_MUTED, COLOR_TEXT_DARK); buttonPanel.add(cancelButton); buttonPanel.add(addButton);
          dialog.add(inputPanel, BorderLayout.CENTER); dialog.add(buttonPanel, BorderLayout.SOUTH);
          cancelButton.addActionListener(e -> dialog.dispose()); addButton.addActionListener(e -> handleAddSlotAction(dialog, startSpinner, endSpinner, selectedDate));
          dialog.pack(); dialog.setLocationRelativeTo(this); dialog.setVisible(true);
      }


    // --- Action Handler Methods ---

    /** Handles the login button action */
    private void handleLogin(String username, String password) {
        currentUser = controller.login(username, password);
        if (currentUser != null) {
            setupDashboard(); // Setup navigation, panels, refresh data
            showDashboardPanel(); // Switch view
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Handles the registration submission from the dialog */
    private void handleRegistration(JDialog parentDialog, JTextField usernameField, JPasswordField passwordField,
                               JComboBox<String> roleComboBox, JTextField nameField, JTextField emailField,
                               JTextField subjectField) { // Added subjectField parameter
    String u = usernameField.getText().trim();
    String p = new String(passwordField.getPassword());
    String r = (String) roleComboBox.getSelectedItem();
    String n = nameField.getText().trim();
    String em = emailField.getText().trim();
    String subj = null; // Initialize subject to null

    if (u.isEmpty() || p.isEmpty() || n.isEmpty() || em.isEmpty() || r == null) {
        JOptionPane.showMessageDialog(parentDialog, "Username, Password, Name, Email, and Role are required.", "Input Error", JOptionPane.WARNING_MESSAGE);
        return;
    }
    if (!em.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
        JOptionPane.showMessageDialog(parentDialog, "Please enter a valid email address.", "Input Error", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Get subject only if the role is Professor and the field is visible/filled
    if ("PROFESSOR".equals(r)) {
        subj = subjectField.getText().trim();
        if (subj.isEmpty()) {
             // Optional: Decide if subject is mandatory for professors
             // For now, we allow registration without a subject, it just won't be added.
             // You could add a JOptionPane warning here if desired.
             System.out.println("Registering Professor without a specified subject.");
             subj = null; // Ensure it's null if empty
        }
    }

    // Call the updated controller method
    User newUser = controller.registerUser(u, p, r, n, em, subj); // Pass subject string

    if (newUser != null) {
        JOptionPane.showMessageDialog(parentDialog, "Registration successful for " + n + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
        parentDialog.dispose();
    } else {
        JOptionPane.showMessageDialog(parentDialog, "Username '" + u + "' already exists. Please choose a different username.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
    }
 }

     /** Handles the navigation button clicks (excluding logout) */
     private void handleNavAction(String actionCommand) {
         // (Implementation from previous answer is correct - keep it)
         if(actionCommand.equals("LOGOUT_ACTION")){ int confirm = JOptionPane.showConfirmDialog( this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION); if (confirm == JOptionPane.YES_OPTION) { showLoginPanel(); } }
         else if (centerCardLayout != null && centerContentPanel != null) { refreshSpecificView(actionCommand); centerCardLayout.show(centerContentPanel, actionCommand); }
         else { System.err.println("Error navigating: centerCardLayout or centerContentPanel is null."); }
     }

     /** Handles populating the Professor/Counselor combo box based on selected subject */
     private void populateStaffComboBox(JComboBox<String> subjectComboBox, JComboBox<String> staffComboBox) {
         // (Implementation from previous answer is correct - keep it)
         staffComboBox.removeAllItems(); staffComboBox.setEnabled(false); Object selectedSubjectItem = subjectComboBox.getSelectedItem();
         if (selectedSubjectItem == null || selectedSubjectItem.toString().startsWith("--")) { staffComboBox.addItem("-- Select Subject First --"); return; } String selectedSubject = selectedSubjectItem.toString(); List<User> matchingStaff = new ArrayList<>();
         if ("Academic Advising".equalsIgnoreCase(selectedSubject)) { matchingStaff = controller.getAllUsers().stream().filter(u -> u.getRole().equals("COUNSELOR")).sorted(Comparator.comparing(User::getName)).collect(Collectors.toList()); }
         else { if (currentUser != null && currentUser.isEnrolledIn(selectedSubject)) { matchingStaff = controller.getAllUsers().stream().filter(u -> u.getRole().equals("PROFESSOR") && u.canTeach(selectedSubject)).sorted(Comparator.comparing(User::getName)).collect(Collectors.toList()); } else { staffComboBox.addItem("-- Not Enrolled --"); return; } }
         if (matchingStaff.isEmpty()) { staffComboBox.addItem(selectedSubject.equals("Academic Advising") ? "-- No Counselors Available --" : "-- No Professors for Subject --"); } else { staffComboBox.addItem("-- Select Staff --"); matchingStaff.forEach(staff -> staffComboBox.addItem(staff.getName() + " (" + staff.getUsername() + ")")); staffComboBox.setEnabled(true); staffComboBox.setSelectedIndex(0); }
     }

    /** Handles the "Book Next Available" button action */
    private void handleBookAppointment(JComboBox<String> staffComboBox, JComboBox<String> subjectComboBox, JSpinner durationSpinner, JTextArea descriptionArea) {
         // (Implementation from previous answer is correct - keep it)
         if (currentUser == null || !currentUser.getRole().equals("STUDENT")) { JOptionPane.showMessageDialog(this, "You must be logged in as a student to book.", "Booking Error", JOptionPane.ERROR_MESSAGE); return; }
         try { Object staffItem = staffComboBox.getSelectedItem(); Object subjectItem = subjectComboBox.getSelectedItem(); int duration = (int) durationSpinner.getValue();
             if (staffItem == null || staffItem.toString().startsWith("--")) { JOptionPane.showMessageDialog(this,"Please select a Professor or Counselor.","Input Error",JOptionPane.WARNING_MESSAGE); return; } if (subjectItem == null || subjectItem.toString().startsWith("--")) { JOptionPane.showMessageDialog(this,"Please select a Subject.","Input Error",JOptionPane.WARNING_MESSAGE); return; }
             String staffSelection = staffItem.toString(); String subject = subjectItem.toString(); String username = staffSelection.substring(staffSelection.indexOf("(") + 1, staffSelection.indexOf(")")); User selectedStaff = controller.getAllUsers().stream().filter(usr -> usr.getUsername().equals(username)).findFirst().orElse(null); if (selectedStaff == null) { throw new Exception("Could not find staff member with username: " + username); }
             Appointment bookedAppointment = controller.createAppointment(currentUser, selectedStaff, subject, duration);
             if (bookedAppointment != null) { JOptionPane.showMessageDialog(this, "Appointment request submitted successfully!\n\nWith: " + bookedAppointment.getProfessorOrCounselor().getName() + "\nTime: " + bookedAppointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "\nSubject: " + bookedAppointment.getSubject(), "Booking Submitted", JOptionPane.INFORMATION_MESSAGE); subjectComboBox.setSelectedIndex(0); durationSpinner.setValue(30); descriptionArea.setText(""); refreshSpecificView(CARD_MY_APPOINTMENTS); refreshSpecificView(CARD_STAFF_AVAILABILITY); refreshNotificationPanels(); }
             else { JOptionPane.showMessageDialog(this, "Could not book the appointment.\n\nPossible reasons:\n- No available time slots found for the selected staff.\n- You might not be enrolled in the selected subject (for Professors).\n\nPlease check the Staff Availability view or try again later.", "Booking Request Failed", JOptionPane.ERROR_MESSAGE); }
         } catch (Exception ex) { JOptionPane.showMessageDialog(this, "An unexpected error occurred during booking:\n" + ex.getMessage(), "Booking Error", JOptionPane.ERROR_MESSAGE); ex.printStackTrace(); }
     }

     /** Handles the "Cancel Selected Pending" button in My Appointments view */
     private void handleCancelAppointment(JTable appointmentsTable) {
         // (Implementation from previous answer is correct - keep it, ensure findAppointmentFromMyTable is robust or use ID)
         int selectedRow = appointmentsTable.getSelectedRow(); if (selectedRow < 0) { JOptionPane.showMessageDialog(this,"Please select an appointment to cancel.","No Selection",JOptionPane.WARNING_MESSAGE); return; }
         Appointment selectedAppointment = findAppointmentFromMyTable(appointmentsTable, selectedRow);
         if (selectedAppointment != null) { if (!"PENDING".equalsIgnoreCase(selectedAppointment.getStatus())) { JOptionPane.showMessageDialog(this,"You can only cancel appointments that are 'PENDING'.","Cannot Cancel",JOptionPane.WARNING_MESSAGE); return; }
             int confirm = JOptionPane.showConfirmDialog(this, "Cancel appointment with " + selectedAppointment.getProfessorOrCounselor().getName() + "?", "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
             if (confirm == JOptionPane.YES_OPTION) { if (controller.cancelAppointment(selectedAppointment)) { JOptionPane.showMessageDialog(this, "Appointment cancelled.", "Success", JOptionPane.INFORMATION_MESSAGE); refreshMyAppointmentsTable(myAppointmentsTableModel); refreshSpecificView(CARD_STAFF_AVAILABILITY); refreshNotificationPanels(); } else { JOptionPane.showMessageDialog(this,"Failed to cancel.", "Error", JOptionPane.ERROR_MESSAGE); refreshMyAppointmentsTable(myAppointmentsTableModel); } }
         } else { JOptionPane.showMessageDialog(this,"Cannot map selection. Refresh list.", "Error", JOptionPane.ERROR_MESSAGE); refreshMyAppointmentsTable(myAppointmentsTableModel); }
     }

     /** Handles the "Start Next" button in Staff Queue Status view */
    private void handleStartNextAppointment(JTable queueTable) {
         // (Implementation from previous answer is correct - keep it)
         if (currentUser == null) return; boolean alreadyInProgress = controller.getUserAppointments(currentUser).stream().anyMatch(app -> app.getStatus().equalsIgnoreCase("IN_PROGRESS")); if (alreadyInProgress) { JOptionPane.showMessageDialog(this,"Please complete the current consultation first.","Action Denied",JOptionPane.WARNING_MESSAGE); return; }
         Appointment nextApp = controller.getNextAppointment(currentUser.getUsername());
         if (nextApp != null) { JOptionPane.showMessageDialog(this, "Started consultation with " + nextApp.getStudent().getName() + ".", "Consultation Started", JOptionPane.INFORMATION_MESSAGE); refreshQueueStatusStaffTable(queueStatusStaffTableModel); refreshQueueInfoLabels(); selectAppointmentInTable(queueTable, nextApp); refreshNotificationPanels(); refreshSpecificView(CARD_MANAGE_APPOINTMENTS); }
         else { JOptionPane.showMessageDialog(this, "No pending appointments to start.", "Queue Empty or Busy", JOptionPane.INFORMATION_MESSAGE); refreshQueueStatusStaffTable(queueStatusStaffTableModel); refreshQueueInfoLabels(); }
     }

    /** Handles the "Complete Selected" button in Staff Queue Status view */
     private void handleCompleteAppointment(JTable queueTable) {
        // (Implementation using ID lookup from previous answer is best)
         int selectedRowVisual = queueTable.getSelectedRow(); if (selectedRowVisual < 0) { JOptionPane.showMessageDialog(this,"Select the 'Current' appointment.","No Selection",JOptionPane.WARNING_MESSAGE); return; }
         int selectedRowModel = queueTable.convertRowIndexToModel(selectedRowVisual); int appointmentId = -1;
         try { appointmentId = (int) queueTable.getModel().getValueAt(selectedRowModel, 0); } catch (Exception ex) { JOptionPane.showMessageDialog(this,"Error retrieving details. Refresh.","Selection Error",JOptionPane.ERROR_MESSAGE); return; }
         Appointment appointmentToComplete = controller.getAppointmentById(appointmentId);
         if (appointmentToComplete == null) { JOptionPane.showMessageDialog(this,"Appointment (ID: " + appointmentId + ") not found. Refresh.", "Not Found", JOptionPane.ERROR_MESSAGE); refreshQueueStatusStaffTable(queueStatusStaffTableModel); return; }
         if (appointmentToComplete.getStatus().equalsIgnoreCase("IN_PROGRESS")) { if (controller.updateAppointmentStatus(appointmentToComplete, "COMPLETED")) { JOptionPane.showMessageDialog(this, "Appointment completed.", "Success", JOptionPane.INFORMATION_MESSAGE); refreshQueueStatusStaffTable(queueStatusStaffTableModel); refreshQueueInfoLabels(); refreshNotificationPanels(); refreshSpecificView(CARD_MANAGE_APPOINTMENTS); } else { JOptionPane.showMessageDialog(this, "Failed to update status.", "Error", JOptionPane.ERROR_MESSAGE); refreshQueueStatusStaffTable(queueStatusStaffTableModel); } }
         else { JOptionPane.showMessageDialog(this,"Selected appointment not 'IN PROGRESS' (Status: " + appointmentToComplete.getStatus() + ").","Action Invalid",JOptionPane.WARNING_MESSAGE); refreshQueueStatusStaffTable(queueStatusStaffTableModel); }
     }

    /** Handles the "Toggle Priority" button in Manage Appointments view */
    private void handleTogglePriority(JTable appointmentsTable, JComboBox<String> filterComboBox) {
         // (Implementation using ID lookup from previous answer is best)
        int selectedRowVisual = appointmentsTable.getSelectedRow(); if (selectedRowVisual < 0) { JOptionPane.showMessageDialog(this, "Select an appointment.", "No Selection", JOptionPane.WARNING_MESSAGE); return; }
        int selectedRowModel = appointmentsTable.convertRowIndexToModel(selectedRowVisual); int appointmentId = -1;
         try { appointmentId = (int) appointmentsTable.getModel().getValueAt(selectedRowModel, 0); } catch (Exception ex) { JOptionPane.showMessageDialog(this,"Error retrieving details. Refresh.","Selection Error",JOptionPane.ERROR_MESSAGE); return; }
         Appointment selectedAppointment = controller.getAppointmentById(appointmentId);
         if (selectedAppointment != null) { String status = selectedAppointment.getStatus(); if (!"PENDING".equalsIgnoreCase(status)) { JOptionPane.showMessageDialog(this, "Priority only for PENDING. (Status: " + status + ")", "Action Denied", JOptionPane.WARNING_MESSAGE); return; }
             boolean newPriorityState = !selectedAppointment.isPriority(); String actionText = newPriorityState ? "set priority" : "remove priority"; String confirmMessage = "Confirm: " + actionText + " for appointment with " + selectedAppointment.getStudent().getName() + "?"; int confirm = JOptionPane.showConfirmDialog(this, confirmMessage, "Confirm Priority", JOptionPane.YES_NO_OPTION);
             if (confirm == JOptionPane.YES_OPTION) { boolean success = controller.setPriority(selectedAppointment, newPriorityState); if (success) { JOptionPane.showMessageDialog(this, "Priority updated.", "Success", JOptionPane.INFORMATION_MESSAGE); refreshManageAppointmentsTable(manageAppointmentsTableModel, (String) filterComboBox.getSelectedItem()); refreshSpecificView(CARD_QUEUE_STATUS_STAFF); refreshNotificationPanels(); } else { JOptionPane.showMessageDialog(this, "Failed to update priority.", "Error", JOptionPane.ERROR_MESSAGE); refreshManageAppointmentsTable(manageAppointmentsTableModel, (String) filterComboBox.getSelectedItem()); } }
         } else { JOptionPane.showMessageDialog(this, "Appointment (ID: " + appointmentId + ") not found. Refresh.", "Error", JOptionPane.ERROR_MESSAGE); refreshManageAppointmentsTable(manageAppointmentsTableModel, (String) filterComboBox.getSelectedItem()); }
     }

    /** Handles the "Delete Record" button in Manage Appointments view */
    private void handleDeleteAppointmentRecord(JTable appointmentsTable, JComboBox<String> filterComboBox) {
        // (Implementation using ID lookup from previous answer is best)
         int selectedRowVisual = appointmentsTable.getSelectedRow(); if (selectedRowVisual < 0) { JOptionPane.showMessageDialog(this, "Select a record.", "No Selection", JOptionPane.WARNING_MESSAGE); return; }
         int selectedRowModel = appointmentsTable.convertRowIndexToModel(selectedRowVisual); int appointmentId = -1;
         try { appointmentId = (int) appointmentsTable.getModel().getValueAt(selectedRowModel, 0); } catch (Exception ex) { JOptionPane.showMessageDialog(this,"Error retrieving details. Refresh.","Selection Error",JOptionPane.ERROR_MESSAGE); return; }
         Appointment appointmentToDelete = controller.getAppointmentById(appointmentId);
         if (appointmentToDelete != null) { String status = appointmentToDelete.getStatus(); if ("IN_PROGRESS".equalsIgnoreCase(status)) { JOptionPane.showMessageDialog(this, "Cannot delete IN PROGRESS appt.", "Action Denied", JOptionPane.WARNING_MESSAGE); return; }
             String confirmMessage = "Delete record for appt with " + appointmentToDelete.getStudent().getName() + " (" + status + ")?"; if ("PENDING".equalsIgnoreCase(status)) { confirmMessage += "\n(This will also CANCEL it)"; } else {confirmMessage += "\n(Remove record permanently)";} int confirmation = JOptionPane.showConfirmDialog(this, confirmMessage, "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
             if (confirmation == JOptionPane.YES_OPTION) { boolean success = controller.cancelAppointment(appointmentToDelete); if (success) { JOptionPane.showMessageDialog(this, "Record deleted.", "Success", JOptionPane.INFORMATION_MESSAGE); refreshManageAppointmentsTable(manageAppointmentsTableModel, (String) filterComboBox.getSelectedItem()); if ("PENDING".equalsIgnoreCase(status)) { refreshSpecificView(CARD_QUEUE_STATUS_STAFF); } refreshNotificationPanels(); } else { JOptionPane.showMessageDialog(this, "Failed deletion.", "Error", JOptionPane.ERROR_MESSAGE); refreshManageAppointmentsTable(manageAppointmentsTableModel, (String) filterComboBox.getSelectedItem()); } }
         } else { JOptionPane.showMessageDialog(this, "Appointment (ID: " + appointmentId + ") not found. Refresh.", "Error", JOptionPane.ERROR_MESSAGE); refreshManageAppointmentsTable(manageAppointmentsTableModel, (String) filterComboBox.getSelectedItem()); }
     }

     /** Handles the "Remove Selected Available Slot" button in Manage Schedule */
     private void removeSelectedSlot() {
         // (Implementation from previous answer is correct - keep it)
         if (currentUser == null || scheduleList == null || dateSpinner == null) return; TimeSlot selectedSlot = scheduleList.getSelectedValue(); if (selectedSlot == null) { JOptionPane.showMessageDialog(this, "Select an available slot.", "No Selection", JOptionPane.WARNING_MESSAGE); return; }
         if (!selectedSlot.isAvailable()) { JOptionPane.showMessageDialog(this, "Selected slot is BOOKED.","Cannot Remove",JOptionPane.ERROR_MESSAGE); return; }
         Date selectedDateUtil = (Date) dateSpinner.getValue(); LocalDate selectedDate = selectedDateUtil.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
         int confirm = JOptionPane.showConfirmDialog(this, "Remove slot: " + selectedDate + " @ " + selectedSlot.getStartTime() + "?", "Confirm Removal", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
         if (confirm == JOptionPane.YES_OPTION) { boolean success = controller.removeTimeSlot(currentUser, selectedDate, selectedSlot); if (success) { JOptionPane.showMessageDialog(this, "Slot removed.", "Success", JOptionPane.INFORMATION_MESSAGE); refreshScheduleList(); refreshNotificationPanels(); } else { JOptionPane.showMessageDialog(this, "Failed to remove slot.", "Error", JOptionPane.ERROR_MESSAGE); refreshScheduleList(); } }
     }

     /** Handles adding a new slot from the schedule management dialog */
    private void handleAddSlotAction(JDialog parentDialog, JSpinner startSpinner, JSpinner endSpinner, LocalDate selectedDate) {
        // (Implementation from previous answer is correct - keep it)
         if (currentUser == null) return; try { Date startTimeUtil = (Date) startSpinner.getValue(); Date endTimeUtil = (Date) endSpinner.getValue(); LocalTime startTime = startTimeUtil.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime().withSecond(0).withNano(0); LocalTime endTime = endTimeUtil.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime().withSecond(0).withNano(0);
             boolean success = controller.addTimeSlot(currentUser, selectedDate, startTime, endTime);
             if (success) { JOptionPane.showMessageDialog(parentDialog, "Slot added!", "Success", JOptionPane.INFORMATION_MESSAGE); refreshScheduleList(); refreshNotificationPanels(); parentDialog.dispose(); }
             else { JOptionPane.showMessageDialog(parentDialog, "Failed. Check overlap/time validity.", "Error", JOptionPane.ERROR_MESSAGE); }
         } catch (Exception ex) { JOptionPane.showMessageDialog(parentDialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); ex.printStackTrace(); }
       }

    /** Handles "Mark Selected Read" from Notifications center view */
    private void handleMarkOneNotificationRead(JList<Notification> listComponent) {
        // (Implementation from previous answer is correct - keep it)
          Notification selectedNotification = listComponent.getSelectedValue(); if (selectedNotification != null) { if (!selectedNotification.isRead()) { if (controller.markSingleNotificationAsRead(currentUser.getUsername(), selectedNotification.getId())) { refreshNotificationPanels(); } else { JOptionPane.showMessageDialog(this, "Failed.", "Error", JOptionPane.ERROR_MESSAGE); } } } else { JOptionPane.showMessageDialog(this, "Select a notification.", "No Selection", JOptionPane.WARNING_MESSAGE); }
     }

     /** Handles "Mark All Read" from either Notifications view or right panel */
     private void handleMarkAllNotificationsRead() {
        // (Implementation from previous answer is correct - keep it)
          if (currentUser == null || notificationListModel == null) return; boolean hasUnread = false; for(int i = 0; i < notificationListModel.getSize(); i++){ Notification item = notificationListModel.getElementAt(i); if(item != null && !item.isRead()){ hasUnread = true; break; } } if(!hasUnread){ JOptionPane.showMessageDialog(this, "No unread.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
          int confirm = JOptionPane.showConfirmDialog(this, "Mark all as read?", "Confirm", JOptionPane.YES_NO_OPTION); if (confirm == JOptionPane.YES_OPTION) { controller.markAllNotificationsAsRead(currentUser.getUsername()); refreshNotificationPanels(); }
      }


    // --- Refresh Methods ---

    /** Refreshes both the side panel and potentially center view notification lists */
    private void refreshNotificationPanels() {
        // (Implementation from previous answer is correct - keep it)
         if (notificationListModel == null || currentUser == null) { if(notificationListModel != null) notificationListModel.clear(); return; } refreshNotificationsList(notificationListModel);
     }

    /** Refreshes the main dashboard (called after login or major changes) */
    private void refreshAllViews() {
        // (Implementation from previous answer is correct - keep it)
        if (currentUser == null) { System.out.println("refreshAllViews skipped."); return; } System.out.println("Refreshing all views for " + currentUser.getUsername()); refreshNotificationPanels();
        if (currentUser.getRole().equals("STUDENT")) { if (myAppointmentsTableModel != null) refreshMyAppointmentsTable(myAppointmentsTableModel); if (staffAvailabilityTableModel != null) refreshStaffAvailabilityTable(staffAvailabilityTableModel); }
        else if (currentUser.getRole().equals("PROFESSOR") || currentUser.getRole().equals("COUNSELOR")) { if (queueStatusStaffTableModel != null) refreshQueueStatusStaffTable(queueStatusStaffTableModel); refreshQueueInfoLabels(); if (manageAppointmentsTableModel != null) { String cf = "All"; Component mp = findComponentInCenter(CARD_MANAGE_APPOINTMENTS); if (mp instanceof Container) { JComboBox<String> fc = findComboBox((Container) mp); if (fc != null && fc.getSelectedItem() != null) { cf = (String) fc.getSelectedItem(); } } refreshManageAppointmentsTable(manageAppointmentsTableModel, cf); } refreshScheduleList(); refreshStaffAnalyticsPanel(); } // <-- Added Analytics refresh
    }

    /** Refreshes a specific view panel identified by its card name */
    private void refreshSpecificView(String cardName) {
        // (Implementation from previous answer, adding analytics card)
         if (currentUser == null) { System.err.println("RefreshSpecificView needs currentUser"); return; } System.out.println("Refreshing view: " + cardName);
         switch (cardName) { case CARD_BOOK_APPOINTMENT: Component bp = findComponentInCenter(cardName); if(bp instanceof Container){ /* Maybe find & repopulate combos? */ } break; case CARD_MY_APPOINTMENTS: if(myAppointmentsTableModel!=null) refreshMyAppointmentsTable(myAppointmentsTableModel); break; case CARD_STAFF_AVAILABILITY: if(staffAvailabilityTableModel!=null) refreshStaffAvailabilityTable(staffAvailabilityTableModel); break;
             case CARD_QUEUE_STATUS_STAFF: if(queueStatusStaffTableModel!=null) refreshQueueStatusStaffTable(queueStatusStaffTableModel); refreshQueueInfoLabels(); break;
             case CARD_MANAGE_APPOINTMENTS: String cf="All"; Component mp = findComponentInCenter(cardName); if (mp instanceof Container) { JComboBox<String> fc = findComboBox((Container)mp); if (fc != null && fc.getSelectedItem() != null) cf = (String) fc.getSelectedItem(); } if (manageAppointmentsTableModel != null) refreshManageAppointmentsTable(manageAppointmentsTableModel, cf); break;
             case CARD_MANAGE_SCHEDULE: refreshScheduleList(); break;
             case CARD_STAFF_ANALYTICS: refreshStaffAnalyticsPanel(); break; // <-- Added
             case CARD_NOTIFICATIONS: refreshNotificationPanels(); break; default: System.err.println("Warn: Unknown card in refreshSpecificView: " + cardName); break;
         }
     }

     /** Refreshes the student's own appointments table */
     private void refreshMyAppointmentsTable(DefaultTableModel model) {
         // (Implementation from previous answer is correct - keep it)
          if (model == null || currentUser == null || !currentUser.getRole().equals("STUDENT")) { if(model != null) model.setRowCount(0); return; } model.setRowCount(0); List<Appointment> apps = controller.getUserAppointments(currentUser).stream().sorted(Comparator.comparing(Appointment::getAppointmentTime)).collect(Collectors.toList());
          if (apps.isEmpty()){ model.addRow(new Object[]{"No appointments found.", "", "", ""}); } else { DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); for (Appointment app : apps) { model.addRow(new Object[]{ app.getProfessorOrCounselor().getName(), app.getAppointmentTime().format(formatter), app.getSubject(), app.getStatus() }); } }
      }

    /** Refreshes the data model shared by notification lists */
     private void refreshNotificationsList(DefaultListModel<Notification> listModel) {
         // (Implementation from previous answer is correct - keep it)
          if (listModel == null || currentUser == null) return; List<Notification> notifs = controller.getUserNotifications(currentUser.getUsername()); listModel.clear(); notifs.forEach(listModel::addElement); if (notificationList != null) notificationList.repaint();
      }

     /** Refreshes the table showing staff availability to students */
     private void refreshStaffAvailabilityTable(DefaultTableModel model) {
         // (Implementation from previous answer is correct - keep it)
          if (model == null || controller == null) { if (model != null) model.setRowCount(0); System.err.println("WARN: refreshStaffAvailabilityTable - Model or Controller is null."); return; } model.setRowCount(0); List<User> staffMembers = controller.getAllUsers().stream().filter(u -> u.getRole().equals("PROFESSOR") || u.getRole().equals("COUNSELOR")).sorted(Comparator.comparing(User::getName)).collect(Collectors.toList());
          if (staffMembers.isEmpty()) { model.addRow(new Object[]{"No staff registered.", "", 0, ""}); } else { LocalDate today = LocalDate.now(); LocalTime now = LocalTime.now(); LocalDate tomorrow = today.plusDays(1);
              for (User staff : staffMembers) { String username = staff.getUsername(); int queueSize = controller.getQueueSize(username); boolean isInProgress = controller.getUserAppointments(staff).stream().anyMatch(app -> "IN_PROGRESS".equalsIgnoreCase(app.getStatus())); String statusNote;
                  if (isInProgress) { statusNote = "In Consultation"; } else { List<TimeSlot> availableToday = controller.getAvailableTimeSlots(username, today).stream().filter(ts -> ts.getStartTime().isAfter(now)).collect(Collectors.toList()); if (!availableToday.isEmpty()) { statusNote = (queueSize == 0) ? "Available Now" : "Available (Queue: " + queueSize; if(queueSize > 0){ int wt = controller.getEstimatedWaitTime(username); if (wt >= 0) { statusNote += " - Est. wait: " + wt + " min)"; } else { statusNote += ")"; } } else { statusNote += ""; } } else { List<TimeSlot> allSlotsToday = controller.getTimeSlotsForDate(username, today); if (!allSlotsToday.isEmpty()) { statusNote = "Fully Booked / Finished for Today"; } else { if (!controller.getAvailableTimeSlots(username, tomorrow).isEmpty()) { statusNote = "Available from Tomorrow"; } else { statusNote = "Check Schedule / Unavailable"; } } } }
                  model.addRow(new Object[]{ staff.getName(), staff.getRole(), queueSize, statusNote }); }
          }
      }

     /** Refreshes the staff's own queue status table */
    private void refreshQueueStatusStaffTable(DefaultTableModel model) {
        // (Implementation from previous answer adding ID column - keep it)
        if (model == null || currentUser == null || (!currentUser.getRole().equals("PROFESSOR") && !currentUser.getRole().equals("COUNSELOR"))) { if(model != null) model.setRowCount(0); return; } model.setRowCount(0); String staffUsername = currentUser.getUsername();
        Appointment inProgressAppointment = controller.getUserAppointments(currentUser).stream().filter(app -> app.getStatus().equalsIgnoreCase("IN_PROGRESS")).findFirst().orElse(null);
        QueueManager q = controller.getQueueManager(staffUsername); List<Appointment> pendingAppointments = new ArrayList<>(); if (q != null) { List<Appointment> prio = new ArrayList<>(q.getPriorityQueue()); List<Appointment> reg = new ArrayList<>(q.getRegularQueue()); prio.sort(Comparator.comparing(Appointment::getAppointmentTime)); reg.sort(Comparator.comparing(Appointment::getAppointmentTime)); pendingAppointments.addAll(prio); pendingAppointments.addAll(reg); }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm"); int positionCounter = 1;
        if (inProgressAppointment != null) { String timeDisplay = inProgressAppointment.getAppointmentTime().format(formatter); model.addRow(new Object[]{ inProgressAppointment.getId(), "Current", inProgressAppointment.getStudent().getName(), timeDisplay, inProgressAppointment.getEstimatedDuration() + " min", inProgressAppointment.getSubject(), inProgressAppointment.isPriority() ? "Yes" : "No", "In Progress" }); }
        for (Appointment app : pendingAppointments) { if ("PENDING".equalsIgnoreCase(app.getStatus())) { String timeDisplay = app.getAppointmentTime().format(formatter); model.addRow(new Object[]{ app.getId(), positionCounter++, app.getStudent().getName(), timeDisplay, app.getEstimatedDuration() + " min", app.getSubject(), app.isPriority() ? "Yes" : "No", app.getStatus() }); } }
        if (model.getRowCount() == 0){ model.addRow(new Object[]{-1, "-", "Queue is empty", "-", "-", "-", "-", "-"}); } // Add ID placeholder -1 for empty row
    }

    /** Refreshes the staff's "Manage Appointments" table with filtering */
     private void refreshManageAppointmentsTable(DefaultTableModel model, String statusFilter) {
        // (Implementation from previous answer adding ID column - keep it)
         if (model == null || currentUser == null || (!currentUser.getRole().equals("PROFESSOR") && !currentUser.getRole().equals("COUNSELOR"))) { if(model != null) model.setRowCount(0); return; } model.setRowCount(0);
         List<Appointment> apps = controller.getUserAppointments(currentUser); List<Appointment> filteredAndSortedApps = apps.stream().filter(app -> statusFilter == null || statusFilter.equalsIgnoreCase("All") || app.getStatus().equalsIgnoreCase(statusFilter)).sorted(Comparator.comparing(Appointment::getAppointmentTime)).collect(Collectors.toList());
         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
         if(filteredAndSortedApps.isEmpty()){ model.addRow(new Object[]{-1, "No appointments found.", "", "", "", statusFilter.equals("All")? "":statusFilter, ""}); } // Add ID placeholder
         else { for (Appointment app : filteredAndSortedApps) { model.addRow(new Object[]{ app.getId(), app.getAppointmentTime().format(formatter), app.getStudent().getName(), app.getSubject(), app.getEstimatedDuration() + " min", app.getStatus(), app.isPriority() ? "Yes" : "No" }); } }
     }

     /** Refreshes the staff's availability schedule list for the selected date */
    private void refreshScheduleList() {
         // (Implementation from previous answer is correct - keep it)
          if (scheduleListModel == null || dateSpinner == null || currentUser == null || (!currentUser.getRole().equals("PROFESSOR") && !currentUser.getRole().equals("COUNSELOR"))) { if(scheduleListModel != null) scheduleListModel.clear(); return; }
          try { Date selectedDateUtil = (Date) dateSpinner.getValue(); LocalDate localDate = selectedDateUtil.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate(); System.out.println("Refreshing schedule list for " + localDate); scheduleListModel.clear(); List<TimeSlot> slots = controller.getTimeSlotsForDate(currentUser.getUsername(), localDate);
              if(slots != null && !slots.isEmpty()){ slots.forEach(scheduleListModel::addElement); } else { /* Handle empty */ }
          } catch (Exception e) { System.err.println("Error refreshing schedule: " + e.getMessage()); e.printStackTrace(); scheduleListModel.clear(); JOptionPane.showMessageDialog(this, "Error loading schedule.", "Error", JOptionPane.ERROR_MESSAGE); }
          if (scheduleList != null) scheduleList.repaint();
     }

     /** Refreshes the staff queue status info boxes */
     private void refreshQueueInfoLabels() {
         // (Implementation from previous answer is correct - keep it)
          if (queueSizeBox == null || avgWaitTimeBox == null || completedTodayBox == null || currentUser == null || (!currentUser.getRole().equals("PROFESSOR") && !currentUser.getRole().equals("COUNSELOR"))) { if(queueSizeBox != null) updateInfoBoxText(queueSizeBox,"Queue: -"); if(avgWaitTimeBox != null) updateInfoBoxText(avgWaitTimeBox,"Wait: - min"); if(completedTodayBox != null) updateInfoBoxText(completedTodayBox,"Done: -"); return; }
          int qs = controller.getQueueSize(currentUser.getUsername()); int wt = controller.getEstimatedWaitTime(currentUser.getUsername()); LocalDate today = LocalDate.now(); long cc = controller.getUserAppointments(currentUser).stream().filter(a -> a.getStatus().equals("COMPLETED") && a.getAppointmentTime().toLocalDate().equals(today)).count();
          updateInfoBoxText(queueSizeBox, "Queue: " + qs); updateInfoBoxText(avgWaitTimeBox, "Wait: " + wt + " min"); updateInfoBoxText(completedTodayBox, "Done: " + cc);
     }

     /** Refreshes the staff analytics panel */
    private void refreshStaffAnalyticsPanel() {
        // (Implementation from previous answer is correct - keep it)
         if (currentUser == null || (!currentUser.getRole().equals("PROFESSOR") && !currentUser.getRole().equals("COUNSELOR")) || analyticsTotalWeekLabel == null ) { if(analyticsTotalWeekLabel != null) analyticsTotalWeekLabel.setText("-"); if(analyticsTotalMonthLabel != null) analyticsTotalMonthLabel.setText("-"); if(analyticsAvgDurationLabel != null) analyticsAvgDurationLabel.setText("- min"); if(analyticsTopSubjectLabel != null) analyticsTopSubjectLabel.setText("N/A"); if(analyticsPeakDayLabel != null) analyticsPeakDayLabel.setText("N/A"); System.err.println("WARN: Skipping analytics refresh - state invalid."); return; } System.out.println("Refreshing analytics panel for: " + currentUser.getUsername());
         Map<String, Object> analytics = controller.getStaffAnalytics(currentUser);
         long totalWeek = (long) analytics.getOrDefault("totalCompletedWeek", 0L); long totalMonth = (long) analytics.getOrDefault("totalCompletedMonth", 0L); double avgDuration = (double) analytics.getOrDefault("averageDurationMinutes", 0.0); String topSubject = (String) analytics.getOrDefault("mostFrequentSubject", "N/A"); String peakDay = (String) analytics.getOrDefault("peakDayOfWeek", "N/A");
         analyticsTotalWeekLabel.setText(String.valueOf(totalWeek)); analyticsTotalMonthLabel.setText(String.valueOf(totalMonth)); analyticsAvgDurationLabel.setText(String.format("%.1f min", avgDuration)); analyticsTopSubjectLabel.setText(topSubject); analyticsPeakDayLabel.setText( peakDay.equals("N/A") ? "N/A" : peakDay.substring(0, 1).toUpperCase() + peakDay.substring(1).toLowerCase() );
     }

    // --- View Switching ---
    /** Switches the main view to the Login panel */
    private void showLoginPanel() {
        // (Implementation from previous answer is correct - keep it)
         System.out.println("Showing Login Panel"); currentUser = null; if (mainCardLayout != null && mainCardPanel != null) { mainCardLayout.show(mainCardPanel, CARD_LOGIN); } else { System.err.println("Login Panel Switch Error: Layout or Panel is null"); }
    }

     /** Switches the main view to the Dashboard panel */
    private void showDashboardPanel() {
        // (Implementation from previous answer is correct - keep it)
         if(mainCardLayout != null && mainCardPanel != null) { if (currentUser == null) { System.err.println("Dashboard Switch Error: No user"); showLoginPanel(); } else { mainCardLayout.show(mainCardPanel, CARD_DASHBOARD); showDefaultViewForRole(); } } else { System.err.println("Dashboard Switch Error: Layout or Panel is null."); }
     }


    // --- Helper Methods (Data Finders - Consider using ID-based approach) ---

    /** Finds appointment corresponding to a row in the 'My Appointments' table */
    private Appointment findAppointmentFromMyTable(JTable table, int selectedRowVisual) {
         if (selectedRowVisual < 0 || currentUser == null || !currentUser.getRole().equals("STUDENT")) return null;
         // Convert visual row to model row (if table allows sorting)
         int modelRow = table.convertRowIndexToModel(selectedRowVisual);
         // *SAFER*: Assumes hidden ID column is added at index 0
         // try {
         //     int id = (int) table.getModel().getValueAt(modelRow, 0);
         //     return controller.getAppointmentById(id);
         // } catch(Exception e) {
         //     System.err.println("Error getting ID from My Appointments table row.");
         //     return null; // Fallback or handle error
         // }
         // Current Fragile approach:
         List<Appointment> userApps = controller.getUserAppointments(currentUser).stream() .sorted(Comparator.comparing(Appointment::getAppointmentTime)).collect(Collectors.toList());
         if (modelRow >= 0 && modelRow < userApps.size()) return userApps.get(modelRow); // Use modelRow
         return null;
     }

    /** Finds appointment corresponding to a row in the 'Manage Appointments' table */
     private Appointment findAppointmentFromManageTable(JTable table, int selectedRowVisual, String statusFilter) {
         if(selectedRowVisual < 0 || currentUser == null || !(currentUser.getRole().equals("PROFESSOR") || currentUser.getRole().equals("COUNSELOR"))) return null;
          int modelRow = table.convertRowIndexToModel(selectedRowVisual);
         // *SAFER*: Assumes hidden ID column is added at index 0
         // try {
         //     int id = (int) table.getModel().getValueAt(modelRow, 0);
         //     return controller.getAppointmentById(id);
         // } catch(Exception e) { /*...*/ return null; }
         // Current Fragile approach:
         List<Appointment> apps = controller.getUserAppointments(currentUser); List<Appointment> filtered = apps.stream().filter(app-> statusFilter == null || statusFilter.equalsIgnoreCase("All") || app.getStatus().equalsIgnoreCase(statusFilter)).sorted(Comparator.comparing(Appointment::getAppointmentTime)).collect(Collectors.toList());
         if (modelRow >= 0 && modelRow < filtered.size()) return filtered.get(modelRow);
         return null;
     }

     /** Finds appointment corresponding to a row in the 'Staff Queue Status' table */
     private Appointment findAppointmentFromQueueTable(JTable table, int selectedRowVisual) {
          if (selectedRowVisual < 0 || currentUser == null || !(currentUser.getRole().equals("PROFESSOR") || currentUser.getRole().equals("COUNSELOR"))) return null;
           int modelRow = table.convertRowIndexToModel(selectedRowVisual);
         // *SAFER*: Assumes hidden ID column is added at index 0
         try {
              // Check if the ID value is actually an Integer before casting
              Object idValue = table.getModel().getValueAt(modelRow, 0);
             if (idValue instanceof Integer) {
                 int id = (Integer) idValue;
                 // Check for placeholder ID (e.g., for empty row)
                  if (id == -1) return null;
                 return controller.getAppointmentById(id);
              } else {
                   System.err.println("WARN: Unexpected data type in ID column at row " + modelRow + ": " + (idValue != null ? idValue.getClass().getName() : "null"));
                   return null;
              }
         } catch(Exception e) {
              System.err.println("Error getting ID from Queue table row " + modelRow + ": " + e.getMessage());
              return null;
          }
         // Fallback/Original fragile logic commented out
         /* ... original reconstruction logic ... */
     }

     /** Selects and scrolls to a row corresponding to a target appointment in the queue table */
     private void selectAppointmentInTable(JTable table, Appointment targetAppointment) {
        // (Implementation using ID lookup preferred, ensure ID column exists)
         if(targetAppointment == null || table == null || table.getModel() == null) return;
         int targetId = targetAppointment.getId();
         DefaultTableModel model = (DefaultTableModel) table.getModel();
         for (int modelRow = 0; modelRow < model.getRowCount(); modelRow++) {
             try {
                 Object idValue = model.getValueAt(modelRow, 0); // Assumes ID is column 0
                  if (idValue instanceof Integer && ((Integer)idValue) == targetId) {
                      int viewRow = table.convertRowIndexToView(modelRow);
                      if (viewRow >= 0) {
                          table.setRowSelectionInterval(viewRow, viewRow);
                          table.scrollRectToVisible(table.getCellRect(viewRow, 0, true));
                      }
                     break; // Found
                 }
              } catch (Exception e) { /* ignore error getting ID during search */ }
          }
      }

    /** Finds a component within the center panel - Simplified/Less Robust */
     private Component findComponentInCenter(String cardName) {
         return centerContentPanel;
     }

     /** Recursively finds the first JComboBox<String> matching a heuristic within a container */
      private JComboBox<String> findComboBox(Container container) {
          // (Implementation from previous answer is correct - keep it)
          if (container == null) return null;
          for (Component comp : container.getComponents()) {
              if (comp instanceof JComboBox) {
                  try { @SuppressWarnings("unchecked") JComboBox<String> cb = (JComboBox<String>) comp; if (cb.getItemCount() > 1 && "All".equals(cb.getItemAt(0))) { return cb; } } catch (Exception e) { /* Ignore */ }
              } else if (comp instanceof Container) {
                  JComboBox<String> found = findComboBox((Container) comp); if (found != null) return found;
              }
          } return null;
      }

    // --- Styling Helper Methods ---

    /** Adds a hover effect (background change) to a JButton */
    private void addHoverEffect(JButton button, Color baseColor, Color hoverColor) {
        // (Implementation from previous answer is correct - keep it)
         button.addMouseListener(new MouseAdapter() { @Override public void mouseEntered(MouseEvent e) { if (button.isEnabled()) button.setBackground(hoverColor); } @Override public void mouseExited(MouseEvent e) { if (button.isEnabled()) button.setBackground(baseColor); } @Override public void mousePressed(MouseEvent e) { if (button.isEnabled()) button.setBackground(hoverColor.darker()); } @Override public void mouseReleased(MouseEvent e) { if (button.isEnabled()) { Point p = MouseInfo.getPointerInfo().getLocation(); SwingUtilities.convertPointFromScreen(p, button); button.setBackground(button.contains(p) ? hoverColor : baseColor); } } });
    }
    /** Creates a JButton with standard styling */
    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        // (Implementation from previous answer is correct - keep it)
        JButton button = new JButton(text); button.setFont(FONT_BOLD); button.setBackground(bgColor); button.setForeground(fgColor); button.setFocusPainted(false); button.setBorder(new CompoundBorder(new LineBorder(bgColor.darker(), 1), BORDER_BUTTON_PADDING)); button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); button.setOpaque(true); button.setToolTipText(text); return button;
    }
    /** Creates a JTextField with standard styling */
    private JTextField createStyledTextField(int columns) {
        // (Implementation from previous answer is correct - keep it)
        JTextField tf = new JTextField(columns); tf.setFont(FONT_MAIN); tf.setBorder(BORDER_INPUT_DEFAULT); return tf;
    }
    /** Creates a JPasswordField with standard styling */
    private JPasswordField createStyledPasswordField(int columns) {
        // (Implementation from previous answer is correct - keep it)
        JPasswordField pf = new JPasswordField(columns); pf.setFont(FONT_MAIN); pf.setBorder(BORDER_INPUT_DEFAULT); return pf;
    }
    /** Creates a JComboBox<String> with standard styling */
    private JComboBox<String> createStyledComboBox() {
        // (Implementation from previous answer is correct - keep it)
        JComboBox<String> cb = new JComboBox<>(); cb.setFont(FONT_MAIN); cb.setBackground(COLOR_WHITE); cb.setBorder(new LineBorder(COLOR_BORDER,1)); return cb;
    }
    /** Creates a JLabel with standard styling */
    private JLabel createStyledLabel(String text) {
        // (Implementation from previous answer is correct - keep it)
        JLabel label = new JLabel(text); label.setFont(FONT_MAIN); label.setForeground(COLOR_TEXT_DARK); return label;
    }
    /** Creates a styled JPanel for displaying info (like queue size boxes) */
    private JPanel createInfoBoxPanel(JLabel contentLabel, String toolTip) {
        // (Implementation from previous answer is correct - keep it)
         JPanel infoBox = new JPanel(new BorderLayout(5, 0)); infoBox.setBackground(COLOR_TABLE_HEADER); infoBox.setBorder(new CompoundBorder(new LineBorder(COLOR_BORDER, 1), new EmptyBorder(8, 12, 8, 12))); infoBox.setToolTipText(toolTip); contentLabel.setHorizontalAlignment(SwingConstants.CENTER); contentLabel.setFont(FONT_BOLD); contentLabel.setForeground(COLOR_TEXT_DARK); infoBox.add(contentLabel, BorderLayout.CENTER); return infoBox;
    }
    /** Safely updates the text of the JLabel inside an infoBoxPanel */
     private void updateInfoBoxText(JPanel infoBoxPanel, String newText) {
         // (Implementation from previous answer is correct - keep it)
          if (infoBoxPanel == null) return; for (Component c : infoBoxPanel.getComponents()) { if (c instanceof JLabel) { ((JLabel) c).setText(newText); return; } } System.err.println("WARN: Could not find JLabel in info box.");
      }
    /** Creates a JTable with standard styling and default renderer */
     private JTable createStyledTable(DefaultTableModel model) {
         // (Updated to handle specific column alignments better)
          JTable table = new JTable(model); table.setFont(FONT_MAIN); table.setRowHeight(30); table.setGridColor(COLOR_BORDER); table.setShowGrid(true); table.setIntercellSpacing(new Dimension(0, 0));
          JTableHeader header = table.getTableHeader(); header.setFont(FONT_BOLD); header.setBackground(COLOR_TABLE_HEADER); header.setForeground(COLOR_TEXT_DARK); header.setOpaque(true); header.setBorder(new LineBorder(COLOR_BORDER)); ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER); header.setReorderingAllowed(false);
          table.setSelectionBackground(COLOR_TABLE_SELECTION_BG); table.setSelectionForeground(COLOR_TABLE_SELECTION_FG); table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

          table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
              private final Border cellPadding = new EmptyBorder(5, 8, 5, 8);
              @Override public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                  Component cell = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                  if (cell instanceof JLabel) {
                      JLabel label = (JLabel) cell; label.setBorder(cellPadding); label.setOpaque(true);
                      // Default alignment
                      label.setHorizontalAlignment(SwingConstants.LEFT);
                       // Specific column alignments
                       String colName = tbl.getColumnName(col);
                       if ("Pos".equals(colName) || "Current Queue".equals(colName) || "Priority".equals(colName) || "Duration".equals(colName) || "Status".equals(colName)) {
                          label.setHorizontalAlignment(SwingConstants.CENTER);
                       }

                      if (isSelected) { label.setBackground(tbl.getSelectionBackground()); label.setForeground(tbl.getSelectionForeground()); }
                      else { label.setBackground(row % 2 == 0 ? COLOR_TABLE_ROW_EVEN : COLOR_TABLE_ROW_ODD); label.setForeground(tbl.getForeground()); }
                  } return cell;
              }
           }); return table;
     }

    // --- Custom List Cell Renderers ---

    /** Custom ListCellRenderer for displaying TimeSlot objects in the schedule list */
    class TimeSlotListRenderer extends DefaultListCellRenderer {
        // (Implementation from previous answer is correct - keep it)
         private final Border cellBorder = new EmptyBorder(6, 8, 6, 8); private final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm"); private final Color COLOR_BOOKED_BG_ODD = new Color(255, 235, 238); private final Color COLOR_BOOKED_BG_EVEN = new Color(255, 224, 230);
         @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
             JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus); label.setBorder(cellBorder); label.setOpaque(true);
             if (value instanceof TimeSlot) { TimeSlot slot = (TimeSlot) value; String timeText = slot.getStartTime().format(TIME_FORMAT) + " - " + slot.getEndTime().format(TIME_FORMAT); String statusText; Color currentBgColor; Color currentFgColor = COLOR_TEXT_DARK;
                 if (slot.isAvailable()) { statusText = "[AVAILABLE]"; currentBgColor = (index % 2 == 0) ? COLOR_TABLE_ROW_EVEN : COLOR_TABLE_ROW_ODD; currentFgColor = COLOR_SUCCESS.darker(); } else { statusText = "[BOOKED]"; currentBgColor = (index % 2 == 0) ? COLOR_BOOKED_BG_EVEN : COLOR_BOOKED_BG_ODD; currentFgColor = COLOR_DANGER; if (slot.getAppointment() != null && slot.getAppointment().getStudent() != null) { statusText += " by " + slot.getAppointment().getStudent().getName(); } else { statusText += " (Error?)"; } }
                 label.setText("<html>" + timeText + " <b>" + statusText + "</b></html>");
                 if (isSelected) { label.setBackground(list.getSelectionBackground()); label.setForeground(list.getSelectionForeground()); label.setFont(list.getFont().deriveFont(Font.BOLD)); } else { label.setBackground(currentBgColor); label.setForeground(currentFgColor); label.setFont(list.getFont()); } label.setToolTipText(timeText + " " + statusText + (slot.isAvailable() ? "" : " (Booked)"));
             } else if (value != null) { label.setText(value.toString()); label.setForeground(Color.GRAY); label.setBackground(list.getBackground()); label.setFont(list.getFont().deriveFont(Font.ITALIC)); if(isSelected){ label.setBackground(list.getSelectionBackground()); label.setForeground(list.getSelectionForeground()); } } else { label.setText(""); label.setBackground(list.getBackground()); } return label;
         }
     }

    /** Custom ListCellRenderer for displaying Notification objects */
    class NotificationListRenderer extends DefaultListCellRenderer {
         // (Implementation from previous answer is correct - keep it)
         private final Border cellBorder = new EmptyBorder(8, 10, 8, 10); private final Border separatorBorder = new MatteBorder(0, 0, 1, 0, COLOR_BORDER.brighter()); private final Border compoundBorder = new CompoundBorder(separatorBorder, cellBorder); private final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm"); private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd");
         @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
             if (!(value instanceof Notification)) { JLabel errLabel = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus); errLabel.setText("Invalid Item Type"); errLabel.setForeground(Color.RED); return errLabel; } Notification notification = (Notification) value; String dateTimeStr = notification.getTimestamp().format(DATE_FORMATTER) + " " + notification.getTimestamp().format(TIME_FORMATTER); String messageStr = escapeHtml(notification.getMessage()); int approxWidth = list.getWidth() > 0 ? list.getWidth() - 40 : 240; Color timeColor = notification.isRead() ? COLOR_NOTIFICATION_READ_FG : COLOR_TEXT_MUTED; Color msgColor = notification.isRead() ? COLOR_NOTIFICATION_READ_FG : COLOR_TEXT_DARK; Font msgFont = notification.isRead() ? FONT_MAIN : FONT_BOLD;
             String htmlText = String.format( "<html><body style='width: %dpx; margin: 0; padding: 0;'><font color='#%s' size='-2'>%s</font><br><font color='#%s'>%s</font></body></html>", approxWidth, toHexString(timeColor), dateTimeStr, toHexString(msgColor), messageStr );
             JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus); label.setText(htmlText); label.setBorder(compoundBorder); label.setOpaque(true); label.setFont(msgFont); label.setToolTipText(notification.getMessage());
             if (!isSelected) { label.setBackground(notification.isRead() ? COLOR_NOTIFICATION_READ_BG : COLOR_WHITE); label.setForeground(msgColor); } else { label.setBackground(list.getSelectionBackground()); label.setForeground(list.getSelectionForeground()); } return label;
         }
         private String toHexString(Color color) { String r=Integer.toHexString(color.getRed()); String g=Integer.toHexString(color.getGreen()); String b=Integer.toHexString(color.getBlue()); return String.format("%s%s%s", (r.length()==1?"0"+r:r), (g.length()==1?"0"+g:g), (b.length()==1?"0"+b:b)); }
         private String escapeHtml(String text) { if (text==null) return ""; return text.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&#39;"); }
     }

} // --- END OF ConsultationGUI CLASS ---



