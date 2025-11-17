package com.project.docxtopdf.controllers;

import java.io.*;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "download", value = "/download")
public class DownloadController extends  HttpServlet {

    public  DownloadController() {
        super();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//            String filePath = request.getParameter("file");
//            File downloadFile = new File(filePath);
//            FileInputStream inStream = new FileInputStream(downloadFile);
//
//            ServletContext context = getServletContext();
//
//            String mimeType = context.getMimeType(filePath);
//            if (mimeType == null) {
//                mimeType = "application/octet-stream";
//            }
//
//            response.setContentType(mimeType);
//            response.setContentLength((int) downloadFile.length());
//
//            String headerKey = "Content-Disposition";
//            String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
//            response.setHeader(headerKey, headerValue);
//
//            OutputStream outStream = response.getOutputStream();
//
//            byte[] buffer = new byte[4096];
//            int bytesRead = -1;
//
//            while ((bytesRead = inStream.read(buffer)) != -1) {
//                outStream.write(buffer, 0, bytesRead);
//            }
//
//            inStream.close();
//            outStream.close();
    }
}
