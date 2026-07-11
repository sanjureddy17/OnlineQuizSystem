package onlinequiz;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AdminPanel extends JFrame {
    private final DefaultListModel<Question> listModel = new DefaultListModel<>();
    private final JList<Question> qList = new JList<>(listModel);
    private final JTextArea txtQuestion = new JTextArea(3, 30);
    private final JTextField o1 = new JTextField(25), o2 = new JTextField(25),
            o3 = new JTextField(25), o4 = new JTextField(25);
    private final JComboBox<Integer> answerCb = new JComboBox<>(new Integer[]{1, 2, 3, 4});

    // New fields for Age Group and Level
    private final JComboBox<String> ageGroupCb = new JComboBox<>(new String[]{"5-10", "11-18", "18+"});
    private final JComboBox<String> levelCb = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});

    private final JButton addBtn = new JButton("Add"), updateBtn = new JButton("Update"),
            deleteBtn = new JButton("Delete"), refreshBtn = new JButton("Refresh"),
            addAdminBtn = new JButton("Add New Admin"), removeAdminBtn = new JButton("Remove Admin");

    private final DefaultListModel<String> adminListModel = new DefaultListModel<>();
    private final JList<String> adminList = new JList<>(adminListModel);

    private String adminUsername;

    public AdminPanel(String username) {
        this.adminUsername = username;

        setTitle("Admin Panel - Logged in as " + username);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel
        JLabel lblWelcome = new JLabel("Welcome Admin: " + username, SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 16));
        add(lblWelcome, BorderLayout.NORTH);

        // Left panel (questions + admin list)
        JPanel left = new JPanel(new BorderLayout());
        left.add(new JScrollPane(qList), BorderLayout.CENTER);

        JPanel leftBtns = new JPanel();
        leftBtns.add(refreshBtn);
        leftBtns.add(deleteBtn);
        left.add(leftBtns, BorderLayout.SOUTH);

        // Admin list panel
        JPanel adminPanel = new JPanel(new BorderLayout());
        adminPanel.setBorder(BorderFactory.createTitledBorder("Current Admins"));
        adminPanel.add(new JScrollPane(adminList), BorderLayout.CENTER);
        left.add(adminPanel, BorderLayout.NORTH);

        add(left, BorderLayout.WEST);

        // Right panel (question details + actions)
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.add(new JLabel("Question:"));
        right.add(new JScrollPane(txtQuestion));
        right.add(new JLabel("Option 1:")); right.add(o1);
        right.add(new JLabel("Option 2:")); right.add(o2);
        right.add(new JLabel("Option 3:")); right.add(o3);
        right.add(new JLabel("Option 4:")); right.add(o4);

        JPanel ansPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ansPanel.add(new JLabel("Answer index:"));
        ansPanel.add(answerCb);
        right.add(ansPanel);

        // Add Age Group & Level
        JPanel agePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        agePanel.add(new JLabel("Age Group:"));
        agePanel.add(ageGroupCb);
        right.add(agePanel);

        JPanel levelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        levelPanel.add(new JLabel("Level:"));
        levelPanel.add(levelCb);
        right.add(levelPanel);

        JPanel action = new JPanel();
        action.add(addBtn);
        action.add(updateBtn);
        action.add(addAdminBtn);
        action.add(removeAdminBtn);
        right.add(action);
        add(right, BorderLayout.CENTER);

        // Load data initially
        loadQuestions();
        loadAdmins();

        // Button listeners
        refreshBtn.addActionListener(e -> loadQuestions());
        addBtn.addActionListener(e -> addQuestion());
        deleteBtn.addActionListener(e -> deleteQuestion());
        updateBtn.addActionListener(e -> updateQuestion());
        addAdminBtn.addActionListener(e -> addNewAdmin());
        removeAdminBtn.addActionListener(e -> removeAdmin());
        qList.addListSelectionListener(e -> populateFields());
    }

    // Questions CRUD
    private void loadQuestions() {
        listModel.clear();
        String sql = "SELECT * FROM questions ORDER BY id";
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            while (rs.next()) {
                Question q = new Question(
                        rs.getInt("id"),
                        rs.getString("question_text"),
                        rs.getString("option1"),
                        rs.getString("option2"),
                        rs.getString("option3"),
                        rs.getString("option4"),
                        rs.getInt("correct_option"),
                        rs.getString("age_group"),
                        rs.getString("level")
                );
                listModel.addElement(q);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading questions:\n" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void addQuestion() {
        String q = txtQuestion.getText().trim();
        if (q.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter question"); return; }

        String sql = "INSERT INTO questions (question_text, option1, option2, option3, option4, correct_option, age_group, level) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setString(1, q);
            p.setString(2, o1.getText().trim());
            p.setString(3, o2.getText().trim());
            p.setString(4, o3.getText().trim());
            p.setString(5, o4.getText().trim());
            p.setInt(6, (Integer) answerCb.getSelectedItem());
            p.setString(7, (String) ageGroupCb.getSelectedItem());
            p.setString(8, (String) levelCb.getSelectedItem());
            p.executeUpdate();

            clearFields();
            loadQuestions();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding question:\n" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void deleteQuestion() {
        Question sel = qList.getSelectedValue();
        if (sel == null) { JOptionPane.showMessageDialog(this, "Select a question"); return; }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected question?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        String sql = "DELETE FROM questions WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, sel.getId());
            p.executeUpdate();
            loadQuestions();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting question:\n" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void updateQuestion() {
        Question sel = qList.getSelectedValue();
        if (sel == null) { JOptionPane.showMessageDialog(this, "Select a question"); return; }

        String sql = "UPDATE questions SET question_text=?, option1=?, option2=?, option3=?, option4=?, correct_option=?, age_group=?, level=? WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setString(1, txtQuestion.getText().trim());
            p.setString(2, o1.getText().trim());
            p.setString(3, o2.getText().trim());
            p.setString(4, o3.getText().trim());
            p.setString(5, o4.getText().trim());
            p.setInt(6, (Integer) answerCb.getSelectedItem());
            p.setString(7, (String) ageGroupCb.getSelectedItem());
            p.setString(8, (String) levelCb.getSelectedItem());
            p.setInt(9, sel.getId());
            p.executeUpdate();

            loadQuestions();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating question:\n" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void populateFields() {
        Question sel = qList.getSelectedValue();
        if (sel == null) return;

        txtQuestion.setText(sel.getQuestion());
        o1.setText(sel.getOption1());
        o2.setText(sel.getOption2());
        o3.setText(sel.getOption3());
        o4.setText(sel.getOption4());
        answerCb.setSelectedItem(sel.getAnswer());
        ageGroupCb.setSelectedItem(sel.getAgeGroup());
        levelCb.setSelectedItem(sel.getLevel());
    }

    private void clearFields() {
        txtQuestion.setText("");
        o1.setText("");
        o2.setText("");
        o3.setText("");
        o4.setText("");
        answerCb.setSelectedIndex(0);
        ageGroupCb.setSelectedIndex(0);
        levelCb.setSelectedIndex(0);
    }

    // Admin management (unchanged)
    private void loadAdmins() {
        adminListModel.clear();
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT username FROM users WHERE role='admin' ORDER BY id")) {

            while (rs.next()) {
                adminListModel.addElement(rs.getString("username"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading admins:\n" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void addNewAdmin() {
        String username = JOptionPane.showInputDialog(this, "Enter new admin username:");
        if (username == null || username.trim().isEmpty()) return;

        String password = JOptionPane.showInputDialog(this, "Enter new admin password:");
        if (password == null || password.trim().isEmpty()) return;

        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO users (username, password, role, score) VALUES (?, ?, 'admin', 0)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, username.trim());
            pst.setString(2, password.trim());
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "✅ New admin added successfully!");
            loadAdmins();
            loadQuestions();
            qList.clearSelection();
            clearFields();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "⚠️ Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void removeAdmin() {
        String selectedAdmin = adminList.getSelectedValue();
        if (selectedAdmin == null) {
            JOptionPane.showMessageDialog(this, "⚠️ Select an admin to remove!");
            return;
        }
        if (selectedAdmin.equals(adminUsername)) {
            JOptionPane.showMessageDialog(this, "⚠️ You cannot remove yourself!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove admin: " + selectedAdmin + "?",
                "Confirm Removal", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection con = DBConnection.getConnection()) {
            String sql = "DELETE FROM users WHERE username=? AND role='admin'";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, selectedAdmin);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "✅ Admin removed successfully!");
            loadAdmins();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "⚠️ Error removing admin: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
