package com.project.docxtopdf.models.dao;

import java.sql.SQLException;
import java.util.UUID;

import com.project.docxtopdf.models.bean.User;
import com.project.docxtopdf.utils.Database;
import com.project.docxtopdf.utils.PasswordUtil;

public class UserDAO {

    public static User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ?";
        User user = null;
        try (var conn = Database.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHashedPassword = rs.getString("password");
                    if (PasswordUtil.checkPassword(password, storedHashedPassword)) {
                        user = new User(
                                rs.getString("id"),
                                rs.getString("username")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public static boolean register(String username, String password) {
        String userId = UUID.randomUUID().toString();
        String sql = "INSERT INTO users (id, username, password) VALUES (?, ?, ?)";
        String hashedPassword = PasswordUtil.hashPassword(password);
        try (var conn = Database.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, username);
            stmt.setString(3, hashedPassword);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.err.println("Error: Username already exists");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }
}
