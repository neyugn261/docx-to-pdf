package com.project.docxtopdf.controllers;

import java.io.*;

import com.project.docxtopdf.models.bean.User;
import com.project.docxtopdf.models.bo.UploadBO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "upload", value = "/upload")
@MultipartConfig(
        fileSizeThreshold = 1 * 1024 * 1024,
        maxFileSize = 10 * 1024 * 1024,
        maxRequestSize = 20 * 1024 * 1024
)
public class UploadController extends HttpServlet {

    public UploadController() {
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
                String filename = UploadBO.uploadFile(
                        user.getId(),
                        originalFileName,
                        fileContent,
                        uploadPath + UploadBO.UPLOAD_DIR
                );
                request.setAttribute("filename", filename);
                request.getRequestDispatcher("/convert").forward(request, response);
            } catch (IllegalArgumentException e) {
                response.sendRedirect("home.jsp?upload=error&message=" + e.getMessage());
            }
        }
    }
}
