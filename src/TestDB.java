import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestDB {
    public static void main(String[] args) {
        Connection conn = null;
        try {
            // Load driver (not always required in newer versions, but safe)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Change root + password if needed
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/quizdb?useSSL=false&serverTimezone=UTC",
                "root",         // your MySQL username
                "Sanju17*"  // your MySQL password
            );

            if (conn != null) {
                System.out.println("✅ Database Connected!");
            } else {
                System.out.println("❌ Connection Failed!");
            }

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("❌ Connection Failed!");
        }
    }
}
