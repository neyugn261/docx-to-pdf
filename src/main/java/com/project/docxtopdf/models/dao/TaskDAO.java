package com.project.docxtopdf.models.dao;

import java.util.ArrayList;
import java.util.List;

import com.project.docxtopdf.models.bean.Task;
import com.project.docxtopdf.models.bo.Database;

public class TaskDAO {

    public static void saveTask(int userId, String originalFileName, String storedFileName, String status) {
        String sql = "INSERT INTO tasks (user_id, original_name, stored_path, status) VALUES (?, ?, ?, ?)";

        try (var conn = Database.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, originalFileName);
            stmt.setString(3, storedFileName);
            stmt.setString(4, status);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateTaskStatus(int taskId, String status) {
        String sql = "UPDATE tasks SET status = ? WHERE id = ?";

        try (var conn = Database.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, taskId);
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

    public static Task getPendingTask() {
        String sql = "SELECT * FROM tasks WHERE status = 'PENDING' ORDER BY created_at ASC LIMIT 1";
        try (var conn = Database.getConnection();
             var stmt = conn.prepareStatement(sql);
             var rs = stmt.executeQuery()) {
            if (rs.next()) {
                Task task = new Task();
                task.setId(rs.getInt("id"));
                task.setUserId(rs.getInt("user_id"));
                task.setOriginalName(rs.getString("original_name"));
                task.setStoredPath(rs.getString("stored_path"));
                task.setStatus(rs.getString("status"));
                return task;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static List<Task> getTasksByUserId(int userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ? ORDER BY created_at DESC";
        try (var conn = Database.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (var rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Task task = new Task();
                    task.setId(rs.getInt("id"));
                    task.setUserId(rs.getInt("user_id"));
                    task.setOriginalName(rs.getString("original_name"));
                    task.setStoredPath(rs.getString("stored_path"));
                    task.setOutputPath(rs.getString("output_path"));
                    task.setStatus(rs.getString("status"));
                    tasks.add(task);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tasks;
    }
}
