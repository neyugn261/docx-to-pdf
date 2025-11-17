package com.project.docxtopdf.controllers;

import java.io.*;

import com.project.docxtopdf.models.bo.UserBO;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "register", value = "/register")
public class RegisterController extends  HttpServlet {
    public RegisterController() {
        super();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        boolean success = UserBO.addUser(username, password);
        if (success) {
            response.sendRedirect("login.jsp");
        } else {
            response.sendRedirect("register.jsp?error=1");
        }
    }
}
