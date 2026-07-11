package onlinequiz;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    JTextField txtUsername;
    JPasswordField txtPassword;
    JButton btnUserLogin, btnAdminLogin, btnRegister;
    JCheckBox showPassword;

    public LoginFrame() {
        setTitle("Quiz Login / Register");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("Welcome to Quiz System", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(new Color(30, 144, 255));
        add(lblTitle, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUser = new JLabel("Username:");
        gbc.gridx = 0; gbc.gridy = 0;
        centerPanel.add(lblUser, gbc);

        txtUsername = new JTextField(15);
        gbc.gridx = 1;
        centerPanel.add(txtUsername, gbc);

        JLabel lblPass = new JLabel("Password:");
        gbc.gridx = 0; gbc.gridy = 1;
        centerPanel.add(lblPass, gbc);

        txtPassword = new JPasswordField(15);
        gbc.gridx = 1;
        centerPanel.add(txtPassword, gbc);

        showPassword = new JCheckBox("Show Password");
        gbc.gridx = 1; gbc.gridy = 2;
        centerPanel.add(showPassword, gbc);
        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) txtPassword.setEchoChar((char) 0);
            else txtPassword.setEchoChar('•');
        });

        add(centerPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnUserLogin = new JButton("User Login");
        btnAdminLogin = new JButton("Admin Login");
        btnRegister = new JButton("Register");

        btnPanel.add(btnUserLogin);
        btnPanel.add(btnAdminLogin);
        btnPanel.add(btnRegister);
        add(btnPanel, BorderLayout.SOUTH);

        btnUserLogin.addActionListener(e -> login(false));
        btnAdminLogin.addActionListener(e -> login(true));
        btnRegister.addActionListener(e -> {
            new RegisterForm().setVisible(true);
        });

        setVisible(true);
    }

    private void login(boolean isAdminLogin) {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter username and password");
            return;
        }

        String sql = "SELECT role, score FROM users WHERE username=? AND password=?";
        if (isAdminLogin) sql += " AND role='admin'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                int score = rs.getInt("score");

                JOptionPane.showMessageDialog(this,
                        "✅ Login Successful!\nUsername: " + username +
                                "\nRole: " + role +
                                (role.equalsIgnoreCase("user") ? "\nCurrent Score: " + score : "")
                );

                dispose(); // Close login frame

                if (role.equalsIgnoreCase("admin")) {
                    new AdminPanel(username).setVisible(true);
                } else {
                    // 🔥 Open Selection Frame before UserPanel
                    new SelectionFrame(username, score).setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "⚠️ Invalid credentials!");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
