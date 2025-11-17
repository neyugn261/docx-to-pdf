package com.project.docxtopdf.controllers;

import java.io.*;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "upload", value = "/upload")
public class UploadController extends HttpServlet {

    public UploadController() {
        super();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    }
}
