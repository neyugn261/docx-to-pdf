package com.project.docxtopdf.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.project.docxtopdf.models.bean.User;
import com.project.docxtopdf.models.bo.TaskBO;

import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/download")
public class DownloadServerlet extends HttpServlet {

    public DownloadServerlet() {
        super();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }

        String taskId = request.getParameter("taskId");
        if (taskId == null || taskId.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing taskId.");
            return;
        }

        var user = (User) session.getAttribute("user");
        
        // Gọi BO để xử lý business logic
        var task = TaskBO.getTaskForDownload(user.getId(), taskId);
        
        if (task == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Task not found or you don't have permission.");
            return;
        }

        String relativePath = task.getOutputPath();

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
