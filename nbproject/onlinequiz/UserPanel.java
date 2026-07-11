package onlinequiz;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class UserPanel extends JFrame {
    private final JButton startBtn = new JButton("Start Quiz");
    private final JLabel info = new JLabel("Click Start to begin your quiz", SwingConstants.CENTER);

    private String username;
    private int score;
    private String ageGroup;
    private String level;

    // Default constructor (for demo/guest)
    public UserPanel() {
        this("Guest", 0, "18+", "Easy");
    }

    // Main constructor
    public UserPanel(String username, int score, String ageGroup, String level) {
        this.username = username;
        this.score = score;
        this.ageGroup = ageGroup;
        this.level = level;

        setTitle("User Panel - " + username);
        setSize(500, 250);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ✅ Welcome message with details
        JLabel welcome = new JLabel(
                "Welcome " + username +
                " | Score: " + score +
                " | Age Group: " + ageGroup +
                " | Level: " + level,
                SwingConstants.CENTER
        );
        welcome.setFont(new Font("Arial", Font.BOLD, 15));
        welcome.setForeground(new Color(30, 144, 255));
        add(welcome, BorderLayout.NORTH);

        // ✅ Info label
        info.setFont(new Font("Arial", Font.PLAIN, 13));
        add(info, BorderLayout.CENTER);

        // ✅ Start button
        JPanel p = new JPanel();
        startBtn.setBackground(new Color(30, 144, 255));
        startBtn.setForeground(Color.WHITE);
        startBtn.setFocusPainted(false);
        startBtn.setFont(new Font("Arial", Font.BOLD, 14));
        p.add(startBtn);
        add(p, BorderLayout.SOUTH);

        startBtn.addActionListener(e -> startQuiz());
    }

    private void startQuiz() {
        ArrayList<Question> questions = new ArrayList<>();

        // include age_group and level in the select
        String sql =
            "SELECT id, question_text, option1, option2, option3, option4, " +
            "       correct_option AS answer, age_group, level " +
            "FROM questions " +
            "WHERE age_group=? AND level=? " +
            "ORDER BY id";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, ageGroup);
            ps.setString(2, level);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Construct Question with ageGroup and level (must match your Question class)
                    questions.add(new Question(
                            rs.getInt("id"),
                            rs.getString("question_text"),
                            rs.getString("option1"),
                            rs.getString("option2"),
                            rs.getString("option3"),
                            rs.getString("option4"),
                            rs.getInt("answer"),
                            rs.getString("age_group"),
                            rs.getString("level")
                    ));
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "⚠️ Error loading questions: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "⚠️ No questions available for:\nAge Group: " + ageGroup +
                    "\nLevel: " + level,
                    "No Questions", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Open Quiz Frame (constructor must accept username, ageGroup, level, questions)
        QuizFrame quiz = new QuizFrame(username, ageGroup, level, questions);
        quiz.setVisible(true);
        dispose(); // Close UserPanel when quiz starts
    }
}
