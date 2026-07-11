package onlinequiz;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;

public class QuizFrame extends JFrame {
    private String username;
    private String ageGroup;
    private String level;
    private List<Question> questions;
    private List<Integer> userAnswers;

    private int currentIndex = 0;
    private int score = 0;

    private JLabel lblQuestion, lblProgress, lblTimer;
    private JRadioButton opt1, opt2, opt3, opt4;
    private ButtonGroup options;
    private JButton btnNext, btnSubmit, btnSkip;

    private Timer quizTimer;
    private int timeLeft = 180; // 3 minutes

    public QuizFrame(String username, String ageGroup, String level, List<Question> questions) {
        this.username = username;
        this.ageGroup = ageGroup;
        this.level = level;
        this.questions = questions;
        this.userAnswers = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) userAnswers.add(-1);

        setTitle("Quiz - " + username);
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top Panel with Progress and Timer
        JPanel topPanel = new JPanel(new BorderLayout());
        lblProgress = new JLabel("Question 1 of " + questions.size());
        lblProgress.setFont(new Font("Arial", Font.PLAIN, 14));
        lblTimer = new JLabel("Time Left: 03:00", SwingConstants.RIGHT);
        lblTimer.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(lblProgress, BorderLayout.WEST);
        topPanel.add(lblTimer, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Center Panel: Question + Options
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblQuestion = new JLabel("", SwingConstants.LEFT);
        lblQuestion.setFont(new Font("Arial", Font.BOLD, 16));
        lblQuestion.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        centerPanel.add(lblQuestion, BorderLayout.NORTH);

        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        opt1 = new JRadioButton();
        opt2 = new JRadioButton();
        opt3 = new JRadioButton();
        opt4 = new JRadioButton();

        options = new ButtonGroup();
        options.add(opt1);
        options.add(opt2);
        options.add(opt3);
        options.add(opt4);

        optionsPanel.add(opt1);
        optionsPanel.add(opt2);
        optionsPanel.add(opt3);
        optionsPanel.add(opt4);

        centerPanel.add(optionsPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel btnPanel = new JPanel();
        btnNext = new JButton("Next");
        btnSkip = new JButton("Skip");
        btnSubmit = new JButton("Submit"); // Submit always enabled
        btnPanel.add(btnNext);
        btnPanel.add(btnSkip);
        btnPanel.add(btnSubmit);
        add(btnPanel, BorderLayout.SOUTH);

        // Show first question
        showQuestion(currentIndex);

        // Button Listeners
        btnNext.addActionListener(e -> handleNext());
        btnSkip.addActionListener(e -> handleSkip());
        btnSubmit.addActionListener(e -> handleSubmit());

        // Start Timer
        startTimer();
    }

    private void showQuestion(int index) {
        Question q = questions.get(index);
        lblQuestion.setText("Q" + (index + 1) + ": " + q.getQuestionText());
        lblProgress.setText("Question " + (index + 1) + " of " + questions.size());

        opt1.setText(q.getOption1());
        opt2.setText(q.getOption2());
        opt3.setText(q.getOption3());
        opt4.setText(q.getOption4());

        options.clearSelection();

        int savedAns = userAnswers.get(index);
        if (savedAns == 1) opt1.setSelected(true);
        else if (savedAns == 2) opt2.setSelected(true);
        else if (savedAns == 3) opt3.setSelected(true);
        else if (savedAns == 4) opt4.setSelected(true);

        btnNext.setEnabled(index < questions.size() - 1);
    }

    private void handleNext() {
        saveAnswer();
        currentIndex++;
        if (currentIndex < questions.size()) showQuestion(currentIndex);
    }

    private void handleSkip() {
        currentIndex++;
        if (currentIndex < questions.size()) showQuestion(currentIndex);
    }

    private void handleSubmit() {
        saveAnswer();
        calculateScore();
        quizTimer.stop();

        saveScoreToDB(username, ageGroup, level, score);

        JOptionPane.showMessageDialog(this,
                "Quiz Finished!\n" +
                        "User: " + username + "\n" +
                        "Score: " + score + "/" + questions.size());

        dispose();
        new ResultFrame(questions, userAnswers, ageGroup, level).setVisible(true);
    }

    private void saveAnswer() {
        int selected = -1;
        if (opt1.isSelected()) selected = 1;
        else if (opt2.isSelected()) selected = 2;
        else if (opt3.isSelected()) selected = 3;
        else if (opt4.isSelected()) selected = 4;

        userAnswers.set(currentIndex, selected);
    }

    private void calculateScore() {
        score = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (userAnswers.get(i) == questions.get(i).getAnswer()) score++;
        }
    }

    private void startTimer() {
        quizTimer = new Timer(1000, e -> {
            timeLeft--;
            int minutes = timeLeft / 60;
            int seconds = timeLeft % 60;
            lblTimer.setText(String.format("Time Left: %02d:%02d", minutes, seconds));

            if (timeLeft <= 0) {
                quizTimer.stop();
                JOptionPane.showMessageDialog(this, "Time's up! Auto-submitting quiz.");
                handleSubmit();
            }
        });
        quizTimer.start();
    }

    private void saveScoreToDB(String username, String ageGroup, String level, int score) {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO leaderboard(username, ageGroup, level, score) VALUES (?,?,?,?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, ageGroup);
            pst.setString(3, level);
            pst.setInt(4, score);
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
