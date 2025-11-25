package com.project.docxtopdf.controllers;

import java.io.IOException;

import com.project.docxtopdf.models.bean.User;
import com.project.docxtopdf.models.bo.UploadBO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/upload")
@MultipartConfig(
        fileSizeThreshold = 1 * 1024 * 1024,
        maxFileSize = 10 * 1024 * 1024,
        maxRequestSize = 20 * 1024 * 1024
)
public class UploadServerlet extends HttpServlet {

    public UploadServerlet() {
        super();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        var session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
        }
        else {
            var user = (User) session.getAttribute("user");
            var filePart = request.getPart("file");
            var originalFileName = filePart.getSubmittedFileName();
            var fileContent = filePart.getInputStream();

            String uploadPath = request.getServletContext().getRealPath("/");
            System.out.println("Upload Path: " + uploadPath);

            try {
                UploadBO.uploadFile(
                        user.getId(),
                        originalFileName,
                        fileContent,
                        uploadPath
                );
                response.sendRedirect("home?upload=success");
            } catch (IllegalArgumentException e) {
                response.sendRedirect("home?upload=error&message" + e.getMessage());
            }
        }
    }
}
