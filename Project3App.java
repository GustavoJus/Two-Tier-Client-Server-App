/*
Name: Gustavo Juscamayta
Course: CNT 4714 Spring 2026
Assignment title: Project 3 – A Two-tier Client-Server Application
Date: March 15, 2026

Class: Project3App
*/

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.*;
import java.util.Properties;

public class Project3App extends JFrame {

    private JComboBox<String> dbUrlCombo;
    private JComboBox<String> userPropsCombo;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton connectButton;
    private JButton disconnectButton;
    private JLabel connectionStatusLabel;

    private JTextArea sqlInputArea;
    private JButton executeButton;
    private JButton clearSqlButton;

    private JTable resultTable;
    private JButton clearResultButton;
    private JButton closeButton;

    private Connection dbConnection = null;

    public Project3App() {
        setTitle("SQL CLIENT APPLICATION - (GJR - CNT 4714 - SPRING 2026 - PROJECT 3)");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(740, 750);
        setLocationRelativeTo(null);
        setResizable(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                disconnectFromDatabase();
                System.exit(0);
            }
        });

        getContentPane().setBackground(new Color(192, 192, 192));
        setLayout(new BorderLayout(5, 5));

        add(buildConnectionPanel(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel buildConnectionPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(new Color(192, 192, 192));
        outer.setBorder(new EmptyBorder(8, 8, 4, 8));

        JLabel title = new JLabel("Connection Details", SwingConstants.CENTER);
        title.setForeground(new Color(0, 0, 200));
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        outer.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(new Color(192, 192, 192));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // DB URL Properties
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        JLabel dbLabel = new JLabel("DB URL Properties");
        dbLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        content.add(dbLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        dbUrlCombo = new JComboBox<>(new String[]{"project3.properties", "bikedb.properties"});
        dbUrlCombo.setBackground(Color.WHITE);
        dbUrlCombo.setPreferredSize(new Dimension(220, 28));
        content.add(dbUrlCombo, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        connectButton = new JButton("Connect to Database");
        connectButton.setBackground(new Color(0, 0, 180));
        connectButton.setForeground(Color.WHITE);
        connectButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        connectButton.setFocusPainted(false);
        connectButton.setPreferredSize(new Dimension(220, 32));
        content.add(connectButton, gbc);

        // User Properties
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel userLabel = new JLabel("User Properties");
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        content.add(userLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        userPropsCombo = new JComboBox<>(new String[]{"root.properties", "client1.properties", "client2.properties"});
        userPropsCombo.setBackground(Color.WHITE);
        content.add(userPropsCombo, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        disconnectButton = new JButton("Disconnect From Database");
        disconnectButton.setBackground(new Color(180, 0, 0));
        disconnectButton.setForeground(Color.WHITE);
        disconnectButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        disconnectButton.setFocusPainted(false);
        disconnectButton.setPreferredSize(new Dimension(220, 32));
        content.add(disconnectButton, gbc);

        // Username
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        JLabel unLabel = new JLabel("Username");
        unLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        content.add(unLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        usernameField = new JTextField();
        usernameField.setBackground(new Color(50, 50, 50));
        usernameField.setForeground(Color.WHITE);
        usernameField.setCaretColor(Color.WHITE);
        usernameField.setPreferredSize(new Dimension(220, 25));
        content.add(usernameField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        JLabel statusTitle = new JLabel("CONNECTION STATUS", SwingConstants.CENTER);
        statusTitle.setFont(new Font("SansSerif", Font.BOLD, 11));
        content.add(statusTitle, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        JLabel pwLabel = new JLabel("Password");
        pwLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        content.add(pwLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        passwordField = new JPasswordField();
        passwordField.setBackground(new Color(50, 50, 50));
        passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setPreferredSize(new Dimension(220, 25));
        content.add(passwordField, gbc);

        gbc.gridx = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        connectionStatusLabel = new JLabel("NO CONNECTION ESTABLISHED", SwingConstants.CENTER);
        connectionStatusLabel.setBackground(Color.RED);
        connectionStatusLabel.setForeground(Color.WHITE);
        connectionStatusLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        connectionStatusLabel.setOpaque(true);
        connectionStatusLabel.setBorder(new EmptyBorder(4, 10, 4, 10));
        connectionStatusLabel.setPreferredSize(new Dimension(240, 30));
        content.add(connectionStatusLabel, gbc);

        outer.add(content, BorderLayout.CENTER);

        connectButton.addActionListener(e -> connectToDatabase());
        disconnectButton.addActionListener(e -> disconnectFromDatabase());

        return outer;
    }

    private JPanel buildCenterPanel() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(new Color(192, 192, 192));
        wrapper.setBorder(new EmptyBorder(4, 8, 4, 8));

        // ---- SQL Input Section ----
        JPanel inputSection = new JPanel(new BorderLayout(4, 4));
        inputSection.setBackground(new Color(192, 192, 192));

        JLabel inputTitle = new JLabel("SQL Command Input Window", SwingConstants.CENTER);
        inputTitle.setForeground(new Color(0, 0, 200));
        inputTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        inputSection.add(inputTitle, BorderLayout.NORTH);

        sqlInputArea = new JTextArea(7, 60);
        sqlInputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        sqlInputArea.setBackground(Color.WHITE);
        sqlInputArea.setLineWrap(true);
        sqlInputArea.setWrapStyleWord(true);
        JScrollPane sqlScroll = new JScrollPane(sqlInputArea);
        sqlScroll.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        inputSection.add(sqlScroll, BorderLayout.CENTER);

        JPanel sqlBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 4));
        sqlBtnPanel.setBackground(new Color(192, 192, 192));

        executeButton = new JButton("Execute SQL Command");
        executeButton.setBackground(new Color(80, 200, 0));
        executeButton.setForeground(Color.BLACK);
        executeButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        executeButton.setFocusPainted(false);
        executeButton.setPreferredSize(new Dimension(200, 34));

        clearSqlButton = new JButton("Clear SQL Command");
        clearSqlButton.setBackground(new Color(230, 220, 0));
        clearSqlButton.setForeground(Color.BLACK);
        clearSqlButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        clearSqlButton.setFocusPainted(false);
        clearSqlButton.setPreferredSize(new Dimension(200, 34));

        sqlBtnPanel.add(executeButton);
        sqlBtnPanel.add(clearSqlButton);
        inputSection.add(sqlBtnPanel, BorderLayout.SOUTH);

        // ---- SQL Result Section ----
        JPanel resultSection = new JPanel(new BorderLayout(4, 4));
        resultSection.setBackground(new Color(192, 192, 192));

        JLabel resultTitle = new JLabel("SQL Execution Result Window", SwingConstants.CENTER);
        resultTitle.setForeground(new Color(0, 0, 200));
        resultTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        resultSection.add(resultTitle, BorderLayout.NORTH);

        resultTable = new JTable();
        resultTable.setBackground(Color.BLUE);
        resultTable.setForeground(Color.WHITE);
        resultTable.setGridColor(Color.WHITE);
        resultTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        resultTable.setRowHeight(20);
        resultTable.getTableHeader().setBackground(new Color(192, 192, 192));
        resultTable.getTableHeader().setForeground(Color.BLACK);
        resultTable.getTableHeader().setFont(new Font("SansSerif", Font.PLAIN, 12));
        JScrollPane resultScroll = new JScrollPane(resultTable);
        resultScroll.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        resultScroll.setPreferredSize(new Dimension(700, 200));
        resultSection.add(resultScroll, BorderLayout.CENTER);

        wrapper.add(inputSection);
        wrapper.add(Box.createVerticalStrut(4));
        wrapper.add(new JSeparator(SwingConstants.HORIZONTAL));
        wrapper.add(Box.createVerticalStrut(4));
        wrapper.add(resultSection);

        executeButton.addActionListener(e -> executeSql());
        clearSqlButton.addActionListener(e -> sqlInputArea.setText(""));

        return wrapper;
    }

    private JPanel buildBottomPanel() {
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(new Color(192, 192, 192));
        bottom.setBorder(new EmptyBorder(4, 8, 8, 8));

        clearResultButton = new JButton("Clear Result Window");
        clearResultButton.setBackground(new Color(230, 220, 0));
        clearResultButton.setForeground(Color.BLACK);
        clearResultButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        clearResultButton.setFocusPainted(false);
        clearResultButton.setPreferredSize(new Dimension(190, 34));

        closeButton = new JButton("Close Application");
        closeButton.setBackground(new Color(200, 0, 0));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        closeButton.setFocusPainted(false);
        closeButton.setPreferredSize(new Dimension(190, 34));

        bottom.add(clearResultButton, BorderLayout.WEST);
        bottom.add(closeButton, BorderLayout.EAST);

        clearResultButton.addActionListener(e ->
            resultTable.setModel(new javax.swing.table.DefaultTableModel()));
        closeButton.addActionListener(e -> {
            disconnectFromDatabase();
            System.exit(0);
        });

        return bottom;
    }

    // ---- Database Logic ----

    private void connectToDatabase() {
        try {
            String dbPropsFile = (String) dbUrlCombo.getSelectedItem();
            String userPropsFile = (String) userPropsCombo.getSelectedItem();

            Properties dbProps   = loadProperties(dbPropsFile);
            Properties userProps = loadProperties(userPropsFile);

            String enteredUser = usernameField.getText().trim();
            String enteredPass = new String(passwordField.getPassword()).trim();
            String propsUser   = userProps.getProperty("db.username", "").trim();
            String propsPass   = userProps.getProperty("db.password", "").trim();

            // Credential verification
            if (!enteredUser.equals(propsUser) || !enteredPass.equals(propsPass)) {
                connectionStatusLabel.setText("NO CONNECTION - Credentials Mismatch!");
                connectionStatusLabel.setBackground(Color.RED);
                return;
            }

            String url    = dbProps.getProperty("db.url", "");
            String driver = dbProps.getProperty("db.driver", "");

            if (!driver.isEmpty()) Class.forName(driver);

            if (dbConnection != null && !dbConnection.isClosed())
                dbConnection.close();

            dbConnection = DriverManager.getConnection(url, enteredUser, enteredPass);

            connectionStatusLabel.setText(url);
            connectionStatusLabel.setBackground(new Color(0, 160, 0));

        } catch (Exception ex) {
            connectionStatusLabel.setText("NO CONNECTION ESTABLISHED");
            connectionStatusLabel.setBackground(Color.RED);
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void disconnectFromDatabase() {
        try {
            if (dbConnection != null && !dbConnection.isClosed())
                dbConnection.close();
        } catch (SQLException ignored) {}
        dbConnection = null;
        connectionStatusLabel.setText("NO CONNECTION ESTABLISHED");
        connectionStatusLabel.setBackground(Color.RED);
    }

    private void executeSql() {
        String sql = sqlInputArea.getText().trim();
        if (sql.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a SQL command.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (dbConnection == null) {
            JOptionPane.showMessageDialog(this, "No database connection.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (sql.toLowerCase().trim().startsWith("select") || sql.toLowerCase().trim().startsWith("(select")) {
                Statement stmt = dbConnection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = stmt.executeQuery(sql);
                resultTable.setModel(new ResultSetTableModel(rs));
                logOperation(getCurrentUsername(), true);
            } else {
                Statement stmt = dbConnection.createStatement();
                int rows = stmt.executeUpdate(sql);
                stmt.close();
                JOptionPane.showMessageDialog(this,
                    "Successful Update..." + rows + " rows updated.",
                    "Successful Update", JOptionPane.INFORMATION_MESSAGE);
                logOperation(getCurrentUsername(), false);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getCurrentUsername() {
        try {
            if (dbConnection != null && !dbConnection.isClosed()) {
                Statement stmt = dbConnection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT CURRENT_USER()");
                if (rs.next()) return rs.getString(1);
            }
        } catch (SQLException ignored) {}
        return usernameField.getText().trim() + "@localhost";
    }

    private void logOperation(String username, boolean isQuery) {
        try {
            Properties appProps = loadProperties("project3app.properties");
            String driver = appProps.getProperty("db.driver", "");
            String url    = appProps.getProperty("db.url", "");
            String user   = appProps.getProperty("db.username", "");
            String pass   = appProps.getProperty("db.password", "");

            if (!driver.isEmpty()) Class.forName(driver);
            Connection logConn = DriverManager.getConnection(url, user, pass);

            // Check if user row exists
            PreparedStatement check = logConn.prepareStatement(
                "SELECT login_username FROM operationscount WHERE login_username = ?");
            check.setString(1, username);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                // Update existing row
                String col = isQuery ? "num_queries" : "num_updates";
                PreparedStatement upd = logConn.prepareStatement(
                    "UPDATE operationscount SET " + col + " = " + col + " + 1 WHERE login_username = ?");
                upd.setString(1, username);
                upd.executeUpdate();
                upd.close();
            } else {
                // Insert new row
                PreparedStatement ins = logConn.prepareStatement(
                    "INSERT INTO operationscount VALUES (?, ?, ?)");
                ins.setString(1, username);
                ins.setInt(2, isQuery ? 1 : 0);
                ins.setInt(3, isQuery ? 0 : 1);
                ins.executeUpdate();
                ins.close();
            }
            rs.close();
            check.close();
            logConn.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Logging error: " + ex.getMessage(),
                "Log Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Properties loadProperties(String filename) throws IOException {
        Properties props = new Properties();
        File f = new File(filename);
        if (f.exists()) {
            try (FileInputStream fis = new FileInputStream(f)) {
                props.load(fis);
            }
        } else {
            throw new FileNotFoundException("Properties file not found: " + filename);
        }
        return props;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(Project3App::new);
    }
}