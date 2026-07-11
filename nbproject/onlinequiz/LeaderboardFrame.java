package onlinequiz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class LeaderboardFrame extends JFrame {

    public LeaderboardFrame(String ageGroup, String level) {
        setTitle("Leaderboard - " + ageGroup + " | " + level);
        setSize(500, 300);
        setLocationRelativeTo(null);

        String[] columns = {"Rank", "Username", "Score"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane, BorderLayout.CENTER);

        loadLeaderboard(model, ageGroup, level);
    }

    private void loadLeaderboard(DefaultTableModel model, String ageGroup, String level) {
        try {
            Connection con = DBConnection.getConnection();
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
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
