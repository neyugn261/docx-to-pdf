package com.project.docxtopdf.models.dao;

import com.project.docxtopdf.models.bo.Database;

public class TaskDAO {

    public static void saveTask(String userId, String originalFileName, String storedFileName, String status) {
        String sql = "INSERT INTO tasks (user_id, original_name, stored_path, status) VALUES (?, ?, ?, ?)";

        try (var conn = Database.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, originalFileName);
            stmt.setString(3, storedFileName);
            stmt.setString(4, status);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateTaskStatus(int taskId, String status, String outputPath) {
        String sql = "UPDATE tasks SET status = ?, output_path = ?, completed_at = NOW() WHERE id = ?";

        try (var conn = Database.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, outputPath);
            stmt.setInt(3, taskId);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
