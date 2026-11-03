package dao;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {

    public boolean validateAdmin(String username, String password) {
        String query = "SELECT * FROM admin WHERE username = ? AND password = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.err.println("[DAO ERROR] Cannot validate admin: Database connection is not available.");
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Admin validation failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
