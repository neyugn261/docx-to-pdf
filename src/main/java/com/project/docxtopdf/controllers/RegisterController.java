package com.project.docxtopdf.controllers;

import java.io.*;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "register", value = "/register")
public class RegisterController extends  HttpServlet {
    public RegisterController() {
        super();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
            String user = request.getParameter("user");
            String pass = request.getParameter("pass");
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request, response);
    }
}
