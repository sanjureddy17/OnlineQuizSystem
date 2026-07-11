package onlinequiz;

import javax.swing.*;
import java.awt.*;

public class SelectionFrame extends JFrame {
    private JComboBox<String> ageGroup;
    private JComboBox<String> level;
    private JButton submitBtn;

    private String username;
    private int score;

    public SelectionFrame(String username, int score) {
        this.username = username;
        this.score = score;

        setTitle("Select Age Group & Level");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblAge = new JLabel("Select Age Group:");
        gbc.gridx = 0; gbc.gridy = 0;
        add(lblAge, gbc);

        ageGroup = new JComboBox<>(new String[]{"5-10", "11-18", "18+"});
        gbc.gridx = 1; gbc.gridy = 0;
        add(ageGroup, gbc);

        JLabel lblLevel = new JLabel("Select Difficulty Level:");
        gbc.gridx = 0; gbc.gridy = 1;
        add(lblLevel, gbc);

        level = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        gbc.gridx = 1; gbc.gridy = 1;
        add(level, gbc);

        submitBtn = new JButton("Start Quiz");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        add(submitBtn, gbc);

        submitBtn.addActionListener(e -> {
            String selectedAge = (String) ageGroup.getSelectedItem();
            String selectedLevel = (String) level.getSelectedItem();

            JOptionPane.showMessageDialog(this,
                    "Selected Age Group: " + selectedAge +
                            "\nSelected Level: " + selectedLevel);

            dispose();

            // ✅ Pass selections to UserPanel
            new UserPanel(username, score, selectedAge, selectedLevel).setVisible(true);
        });
    }
}
