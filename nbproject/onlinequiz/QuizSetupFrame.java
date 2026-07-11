package onlinequiz;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class QuizSetupFrame extends JFrame {
    private JComboBox<String> ageGroupBox;
    private JComboBox<String> levelBox;
    private JButton startBtn;

    private String username;
    private int score;

    public QuizSetupFrame(String username, int score) {
        this.username = username;
        this.score = score;

        setTitle("Select Age & Level");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new GridLayout(3, 2, 10, 10));

        // Age groups
        add(new JLabel("Select Age Group:"));
        ageGroupBox = new JComboBox<>(new String[]{"5-10", "11-18", "18+"});
        add(ageGroupBox);

        // Levels
        add(new JLabel("Select Difficulty Level:"));
        levelBox = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        add(levelBox);

        // Button
        startBtn = new JButton("Start Quiz");
        add(new JLabel()); // Empty cell
        add(startBtn);

        startBtn.addActionListener(e -> {
            String ageGroup = (String) ageGroupBox.getSelectedItem();
            String level = (String) levelBox.getSelectedItem();

            // Pass to UserPanel
            UserPanel panel = new UserPanel(username, score, ageGroup, level);
            panel.setVisible(true);
            dispose();
        });
    }
}
