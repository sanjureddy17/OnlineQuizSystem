package onlinequiz;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterForm extends JFrame {
    JTextField txtFirstName, txtLastName, txtUsername, txtDOB, txtEmail, txtPhone;
    JPasswordField txtPassword;
    JRadioButton rbMale, rbFemale;
    JButton btnRegister, btnCancel;

    public RegisterForm() {
        setTitle("User Registration");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel lblTitle = new JLabel("Register New User", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(new Color(34, 139, 34));
        add(lblTitle, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // First Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("First Name:"), gbc);
        txtFirstName = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtFirstName, gbc);

        // Last Name
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Last Name:"), gbc);
        txtLastName = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtLastName, gbc);

        // Username
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Username:"), gbc);
        txtUsername = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtUsername, gbc);

        // DOB
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Date of Birth:"), gbc);
        txtDOB = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtDOB, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Email:"), gbc);
        txtEmail = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtEmail, gbc);

        // Phone
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Phone:"), gbc);
        txtPhone = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtPhone, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Password:"), gbc);
        txtPassword = new JPasswordField(15);
        gbc.gridx = 1;
        formPanel.add(txtPassword, gbc);

        // Gender
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Gender:"), gbc);
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rbMale = new JRadioButton("Male");
        rbFemale = new JRadioButton("Female");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbMale); bg.add(rbFemale);
        genderPanel.add(rbMale); genderPanel.add(rbFemale);
        gbc.gridx = 1;
        formPanel.add(genderPanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel();
        btnRegister = new JButton("Register");
        btnCancel = new JButton("Cancel");
        btnPanel.add(btnRegister);
        btnPanel.add(btnCancel);
        add(btnPanel, BorderLayout.SOUTH);

        // --- Actions ---
        btnRegister.addActionListener(e -> registerUser());
        btnCancel.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void registerUser() {
        String fname = txtFirstName.getText().trim();
        String lname = txtLastName.getText().trim();
        String username = txtUsername.getText().trim();
        String dob = txtDOB.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String gender = rbMale.isSelected() ? "Male" : rbFemale.isSelected() ? "Female" : "";

        if (fname.isEmpty() || lname.isEmpty() || username.isEmpty() || dob.isEmpty() ||
                email.isEmpty() || phone.isEmpty() || password.isEmpty() || gender.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ All fields are required!");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO users (first_name, last_name, username, dob, email, phone, password, gender, role, score) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'user', 0)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, fname);
            pst.setString(2, lname);
            pst.setString(3, username);
            pst.setString(4, dob);
            pst.setString(5, email);
            pst.setString(6, phone);
            pst.setString(7, password);
            pst.setString(8, gender);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "✅ Registration Successful!");
                dispose(); // Close form
            } else {
                JOptionPane.showMessageDialog(this, "❌ Registration Failed!");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "⚠️ Error: " + ex.getMessage());
        }
    }
}
