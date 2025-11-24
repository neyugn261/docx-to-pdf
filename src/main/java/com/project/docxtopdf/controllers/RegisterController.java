package com.project.docxtopdf.controllers;

import java.io.*;

import com.project.docxtopdf.models.bo.UserBO;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/register")
public class RegisterController extends  HttpServlet {
    public RegisterController() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // Hiển thị trang register
            request.getRequestDispatcher("register.jsp").forward(request, response);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        boolean success = UserBO.addUser(username, password);
        if (success) {
            response.sendRedirect("login");
        } else {
            response.sendRedirect("register?error=1");
        }
    }
}
