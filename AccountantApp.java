/*
Name: Gustavo Juscamayta
Course: CNT 4714 Spring 2026
Assignment title: Project 3 – A Specialized Accountant Application
Date: March 15, 2026

Class: AccountApp
*/

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.util.Properties;

public class AccountantApp extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel connectionStatusLabel;

    private JTextArea sqlCommandArea;
    private JTable resultTable;

    private JButton connectBtn;
    private JButton disconnectBtn;
    private JButton executeSQLBtn;
    private JButton clearSQLBtn;
    private JButton clearResultBtn;
    private JButton closeAppBtn;

    private Connection accountantConn;

    private static final String DB_PROPS_FILE    = "operationslog.properties";
    private static final String USER_PROPS_FILE  = "theaccountant.properties";
    private static final Color  BG_GRAY          = new Color(200, 200, 200);
    private static final Color  DARK_FIELD       = new Color(60, 60, 60);

    public AccountantApp() {
        setTitle("SPECIALIZED ACCOUNTANT APPLICATION - (GJR - CNT 4714 - SPRING 2026 - PROJECT 3)");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(780, 750);
        setBackground(BG_GRAY);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { closeApplication(); }
        });
        buildUI();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_GRAY);

        // ========== CONNECTION DETAILS PANEL ==========
        JPanel connPanel = new JPanel(new GridBagLayout());
        connPanel.setBackground(BG_GRAY);
        connPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.BLUE, 1),
            "Connection Details",
            TitledBorder.CENTER, TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 12), Color.BLUE));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 8, 4, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: DB URL Properties
        g.gridx = 0; g.gridy = 0; g.weightx = 0;
        connPanel.add(makeLabel("DB URL Properties"), g);

        JTextField dbField = new JTextField(DB_PROPS_FILE);
        dbField.setEditable(false);
        dbField.setBackground(Color.WHITE);
        dbField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.gridx = 1; g.weightx = 1.0;
        connPanel.add(dbField, g);

        connectBtn = makeButton("Connect to Database", Color.BLUE, Color.WHITE);
        g.gridx = 2; g.weightx = 0;
        connPanel.add(connectBtn, g);

        // Row 1: User Properties
        g.gridx = 0; g.gridy = 1; g.weightx = 0;
        connPanel.add(makeLabel("User Properties"), g);

        JTextField userField = new JTextField(USER_PROPS_FILE);
        userField.setEditable(false);
        userField.setBackground(Color.WHITE);
        userField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.gridx = 1; g.weightx = 1.0;
        connPanel.add(userField, g);

        disconnectBtn = makeButton("Disconnect From Database", Color.RED, Color.WHITE);
        g.gridx = 2; g.weightx = 0;
        connPanel.add(disconnectBtn, g);

        // Row 2: Username + CONNECTION STATUS label
        g.gridx = 0; g.gridy = 2; g.weightx = 0;
        connPanel.add(makeLabel("Username"), g);

        usernameField = new JTextField();
        usernameField.setBackground(DARK_FIELD);
        usernameField.setForeground(Color.WHITE);
        usernameField.setCaretColor(Color.WHITE);
        usernameField.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.gridx = 1; g.weightx = 1.0;
        connPanel.add(usernameField, g);

        JLabel statusLbl = new JLabel("CONNECTION STATUS", SwingConstants.CENTER);
        statusLbl.setForeground(Color.BLACK);
        statusLbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        g.gridx = 2; g.weightx = 0;
        connPanel.add(statusLbl, g);

        // Row 3: Password + status value
        g.gridx = 0; g.gridy = 3; g.weightx = 0;
        connPanel.add(makeLabel("Password"), g);

        passwordField = new JPasswordField();
        passwordField.setBackground(DARK_FIELD);
        passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(Color.WHITE);
        g.gridx = 1; g.weightx = 1.0;
        connPanel.add(passwordField, g);

        connectionStatusLabel = new JLabel("NO CONNECTION ESTABLISHED", SwingConstants.CENTER);
        connectionStatusLabel.setOpaque(true);
        connectionStatusLabel.setBackground(Color.RED);
        connectionStatusLabel.setForeground(Color.WHITE);
        connectionStatusLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        connectionStatusLabel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        g.gridx = 2; g.weightx = 0;
        connPanel.add(connectionStatusLabel, g);

        root.add(connPanel, BorderLayout.NORTH);

        // ========== SQL COMMAND PANEL ==========
        JPanel cmdPanel = new JPanel(new BorderLayout(0, 4));
        cmdPanel.setBackground(BG_GRAY);
        cmdPanel.setBorder(BorderFactory.createEmptyBorder(6, 8, 0, 8));

        JLabel cmdTitle = new JLabel("SQL Command Input Window", SwingConstants.CENTER);
        cmdTitle.setForeground(Color.BLUE);
        cmdTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        cmdPanel.add(cmdTitle, BorderLayout.NORTH);

        sqlCommandArea = new JTextArea(6, 50);
        sqlCommandArea.setBackground(Color.WHITE);
        sqlCommandArea.setForeground(Color.BLACK);
        sqlCommandArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        sqlCommandArea.setLineWrap(true);
        sqlCommandArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        cmdPanel.add(new JScrollPane(sqlCommandArea), BorderLayout.CENTER);

        JPanel cmdBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 6));
        cmdBtns.setBackground(BG_GRAY);
        executeSQLBtn = makeButton("Execute SQL Command", new Color(0, 180, 0), Color.BLACK);
        clearSQLBtn   = makeButton("Clear SQL Command",   Color.YELLOW,         Color.BLACK);
        cmdBtns.add(executeSQLBtn);
        cmdBtns.add(clearSQLBtn);
        cmdPanel.add(cmdBtns, BorderLayout.SOUTH);

        // ========== RESULT PANEL ==========
        JPanel resultPanel = new JPanel(new BorderLayout(0, 4));
        resultPanel.setBackground(BG_GRAY);
        resultPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 6, 8));

        JLabel resultTitle = new JLabel("SQL Execution Result Window", SwingConstants.CENTER);
        resultTitle.setForeground(Color.BLUE);
        resultTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        resultPanel.add(resultTitle, BorderLayout.NORTH);

        resultTable = new JTable();
        resultTable.setBackground(Color.BLUE);
        resultTable.setForeground(Color.WHITE);
        resultTable.setGridColor(Color.WHITE);
        resultTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        resultTable.setRowHeight(20);
        resultTable.getTableHeader().setBackground(BG_GRAY);
        resultTable.getTableHeader().setForeground(Color.BLACK);
        resultTable.getTableHeader().setFont(new Font("SansSerif", Font.PLAIN, 12));
        JScrollPane tableScroll = new JScrollPane(resultTable);
        tableScroll.setPreferredSize(new Dimension(700, 200));
        resultPanel.add(tableScroll, BorderLayout.CENTER);

        JPanel resultBtns = new JPanel(new BorderLayout());
        resultBtns.setBackground(BG_GRAY);
        resultBtns.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        clearResultBtn = makeButton("Clear Result Window", Color.YELLOW, Color.BLACK);
        closeAppBtn    = makeButton("Close Application",   Color.RED,    Color.WHITE);
        closeAppBtn.setFont(new Font("SansSerif", Font.BOLD, 13));

        JPanel leftBtns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftBtns.setBackground(BG_GRAY);
        leftBtns.add(clearResultBtn);

        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightBtns.setBackground(BG_GRAY);
        rightBtns.add(closeAppBtn);

        resultBtns.add(leftBtns,  BorderLayout.WEST);
        resultBtns.add(rightBtns, BorderLayout.EAST);
        resultPanel.add(resultBtns, BorderLayout.SOUTH);

        // ========== CENTER SPLIT ==========
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, cmdPanel, resultPanel);
        split.setDividerLocation(220);
        split.setBackground(BG_GRAY);
        split.setBorder(null);
        root.add(split, BorderLayout.CENTER);

        setContentPane(root);

        // ========== ACTIONS ==========
        connectBtn.addActionListener(e -> connectToDatabase());
        disconnectBtn.addActionListener(e -> disconnectFromDatabase());
        executeSQLBtn.addActionListener(e -> executeSQL());
        clearSQLBtn.addActionListener(e -> sqlCommandArea.setText(""));
        clearResultBtn.addActionListener(e ->
            resultTable.setModel(new javax.swing.table.DefaultTableModel()));
        closeAppBtn.addActionListener(e -> closeApplication());
    }

    private void connectToDatabase() {
        try {
            Properties dbProps   = loadProperties(DB_PROPS_FILE);
            Properties userProps = loadProperties(USER_PROPS_FILE);

            String enteredUser = usernameField.getText().trim();
            String enteredPass = new String(passwordField.getPassword()).trim();
            String propsUser   = userProps.getProperty("db.username", "").trim();
            String propsPass   = userProps.getProperty("db.password", "").trim();

            if (!enteredUser.equals(propsUser) || !enteredPass.equals(propsPass)) {
                connectionStatusLabel.setText("NO CONNECTION - Credentials Mismatch!");
                connectionStatusLabel.setBackground(Color.RED);
                return;
            }

            String url = dbProps.getProperty("db.url");
            Class.forName(dbProps.getProperty("db.driver"));

            if (accountantConn != null && !accountantConn.isClosed())
                accountantConn.close();

            accountantConn = DriverManager.getConnection(url, enteredUser, enteredPass);
            connectionStatusLabel.setText(url);
            connectionStatusLabel.setBackground(new Color(0, 200, 0));
            connectionStatusLabel.setForeground(Color.BLACK);

        } catch (Exception ex) {
            connectionStatusLabel.setText("NO CONNECTION ESTABLISHED");
            connectionStatusLabel.setBackground(Color.RED);
            connectionStatusLabel.setForeground(Color.WHITE);
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void disconnectFromDatabase() {
        try {
            if (accountantConn != null && !accountantConn.isClosed())
                accountantConn.close();
            connectionStatusLabel.setText("NO CONNECTION ESTABLISHED");
            connectionStatusLabel.setBackground(Color.RED);
            connectionStatusLabel.setForeground(Color.WHITE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void executeSQL() {
        if (accountantConn == null) {
            JOptionPane.showMessageDialog(this, "No connection established.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String sql = sqlCommandArea.getText().trim();
        if (sql.isEmpty()) return;

        try {
            Statement stmt = accountantConn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery(sql);
            resultTable.setModel(new ResultSetTableModel(rs));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Properties loadProperties(String filename) throws IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(filename)) {
            props.load(fis);
        }
        return props;
    }

    private void closeApplication() {
        try {
            if (accountantConn != null && !accountantConn.isClosed())
                accountantConn.close();
        } catch (SQLException ignored) {}
        System.exit(0);
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.BLACK);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return lbl;
    }

    private JButton makeButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AccountantApp::new);
    }
}