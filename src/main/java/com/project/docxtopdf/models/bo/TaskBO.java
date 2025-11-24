package com.project.docxtopdf.models.bo;

import java.util.List;

import com.project.docxtopdf.models.bean.Task;
import com.project.docxtopdf.models.dao.TaskDAO;

public class TaskBO {
    
    public static List<Task> getTasksByUserId(String userId) {
        return TaskDAO.getTasksByUserId(userId);
    }
    
    public static Task getPendingTask() {
        return TaskDAO.getPendingTask();
    }
    
    public static void updateTaskStatus(String taskId, String status) {
        TaskDAO.updateTaskStatus(taskId, status);
    }
    
    public static void updateTaskStatus(String taskId, String status, String outputPath) {
        TaskDAO.updateTaskStatus(taskId, status, outputPath);
    }
}
