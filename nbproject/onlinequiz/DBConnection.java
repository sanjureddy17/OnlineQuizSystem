package onlinequiz;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/quizdb";
    private static final String USER = "root";      // your MySQL username
    private static final String PASSWORD = "Sanju17*"; // your MySQL password
    private static Connection conn = null;

    // Get Connection
    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL 8+ driver
                conn = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Database Connected!");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("❌ Database Connection Failed!");
            e.printStackTrace();
        }
        return conn;
    }

    // Close Connection
    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("🔒 Database Connection Closed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
