package onlinequiz;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionLoader {

    public static List<Question> loadQuestions(String ageGroup, String level) {
        List<Question> questions = new ArrayList<>();

        String sql = "SELECT id, question_text, option1, option2, option3, option4, " +
                     "       correct_option, age_group, level " +
                     "FROM questions " +
                     "WHERE age_group=? AND level=?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, ageGroup);
            ps.setString(2, level);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    questions.add(new Question(
                            rs.getInt("id"),
                            rs.getString("question_text"),
                            rs.getString("option1"),
                            rs.getString("option2"),
                            rs.getString("option3"),
                            rs.getString("option4"),
                            rs.getInt("correct_option"),
                            rs.getString("age_group"),
                            rs.getString("level")
                    ));
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // ✅ Shuffle questions randomly
        Collections.shuffle(questions);

        // ✅ Keep only first 10
        if (questions.size() > 10) {
            return new ArrayList<>(questions.subList(0, 10));
        }

        return questions;
    }
}
