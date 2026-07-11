package onlinequiz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import java.awt.*;
import java.sql.*;
import java.util.List;

public class ResultFrame extends JFrame {

    public ResultFrame(List<Question> questions, List<Integer> userAnswers, String ageGroup, String level) {
        setTitle("Quiz Results & Leaderboard");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- Results Area ---
        JTextPane resultPane = new JTextPane();
        resultPane.setEditable(false);
        StyledDocument doc = resultPane.getStyledDocument();

        // Define styles
        Style correctStyle = resultPane.addStyle("Correct", null);
        StyleConstants.setForeground(correctStyle, Color.GREEN.darker());
        StyleConstants.setBold(correctStyle, true);

        Style wrongStyle = resultPane.addStyle("Wrong", null);
        StyleConstants.setForeground(wrongStyle, Color.RED);
        StyleConstants.setBold(wrongStyle, true);

        Style normalStyle = resultPane.addStyle("Normal", null);
        StyleConstants.setForeground(normalStyle, Color.BLACK);

        try {
            for (int i = 0; i < questions.size(); i++) {
                Question q = questions.get(i);
                int userAns = userAnswers.get(i);
                boolean correct = (userAns == q.getAnswer());

                doc.insertString(doc.getLength(), "Q" + (i + 1) + ": " + q.getQuestionText() + "\n", normalStyle);
                doc.insertString(doc.getLength(), "Your Answer: " + 
                        (userAns == -1 ? "Not Answered" : getOptionText(q, userAns)) + "\n", 
                        correct ? correctStyle : wrongStyle);
                doc.insertString(doc.getLength(), "Correct Answer: " + getOptionText(q, q.getAnswer()) + "\n\n", normalStyle);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(resultPane);
        scrollPane.setPreferredSize(new Dimension(450, 450));

        // --- Leaderboard Area ---
        String[] columns = {"Rank", "Username", "Score"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setEnabled(false);
        table.setRowHeight(25);
        JScrollPane leaderboardPane = new JScrollPane(table);
        leaderboardPane.setPreferredSize(new Dimension(300, 450));

        loadLeaderboard(model, ageGroup, level);

        // --- Split Panel ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, leaderboardPane);
        splitPane.setDividerLocation(500);
        add(splitPane, BorderLayout.CENTER);
    }

    private String getOptionText(Question q, int option) {
        switch (option) {
            case 1: return q.getOption1();
            case 2: return q.getOption2();
            case 3: return q.getOption3();
            case 4: return q.getOption4();
            default: return "Invalid";
        }
    }

    private void loadLeaderboard(DefaultTableModel model, String ageGroup, String level) {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT username, score FROM leaderboard " +
                         "WHERE ageGroup=? AND level=? " +
                         "ORDER BY score DESC, quiz_date ASC LIMIT 5";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, ageGroup);
            pst.setString(2, level);
            ResultSet rs = pst.executeQuery();

            int rank = 1;
            while (rs.next()) {
                String user = rs.getString("username");
                int score = rs.getInt("score");
                model.addRow(new Object[]{rank, user, score});
                rank++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
