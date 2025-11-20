package com.project.docxtopdf.controllers;

import com.project.docxtopdf.models.bo.Converter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "convert", value = "/convert")
@MultipartConfig(
        fileSizeThreshold = 1 * 1024 * 1024,
        maxFileSize = 10 * 1024 * 1024,
        maxRequestSize = 20 * 1024 * 1024
)
public class ConvertController extends HttpServlet {
    public ConvertController() {
        super();
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (request.getSession().getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        String fileName = (String) request.getAttribute("filename");
        var filePart = request.getPart("file");
        var fileContent = filePart.getInputStream();

        String pdfFilePath = request.getServletContext().getRealPath("/") + Converter.PDF_DIR;
//        Converter.convertDocxToPdf(fileContent, fileName, pdfFilePath);
        // Gọi ConvertBO
        response.getWriter().println("Conversion initiated for file: " + fileName);
    }
}
