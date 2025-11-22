package com.project.docxtopdf.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.project.docxtopdf.models.bean.User;
import com.project.docxtopdf.models.dao.TaskDAO;

import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "download", value = "/download")
public class DownloadController extends HttpServlet {

    public DownloadController() {
        super();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You need to login first.");
            return;
        }

        String taskIdStr = request.getParameter("taskId");
        if (taskIdStr == null || taskIdStr.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing taskId.");
            return;
        }

        int taskId;
        try {
            taskId = Integer.parseInt(taskIdStr);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid taskId.");
            return;
        }

        var user = (User) session.getAttribute("user");
        List<com.project.docxtopdf.models.bean.Task> tasks = TaskDAO.getTasksByUserId(user.getId());

        var taskOptional = tasks.stream().filter(t -> t.getId() == taskId).findFirst();

        if (taskOptional.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Task not found or you don't have permission.");
            return;
        }

        var task = taskOptional.get();
        String relativePath = task.getOutputPath();

        if (relativePath == null || relativePath.isEmpty() || !"DONE".equals(task.getStatus())) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File is not ready or does not exist.");
            return;
        }

        // Xây dựng đường dẫn tuyệt đối từ đường dẫn tương đối
        String realPath = getServletContext().getRealPath("/");
        File downloadFile = new File(realPath, relativePath);
        
        System.out.println("Attempting to download file: " + downloadFile.getAbsolutePath());
        
        if (!downloadFile.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File does not exist on server: " + downloadFile.getAbsolutePath());
            return;
        }

        try (FileInputStream inStream = new FileInputStream(downloadFile)) {
            ServletContext context = getServletContext();
            String mimeType = context.getMimeType(relativePath);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            response.setContentType(mimeType);
            response.setContentLength((int) downloadFile.length());

            String headerKey = "Content-Disposition";
            String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
            response.setHeader(headerKey, headerValue);

            try (OutputStream outStream = response.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
            }
        }
    }
}
