package com.project.docxtopdf.controllers;

import java.io.*;

import com.project.docxtopdf.models.bean.User;
import com.project.docxtopdf.models.bo.UserBO;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "login", value = "/login")
public class LoginController extends HttpServlet {

    public  LoginController() {
        super();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user = UserBO.checkLogin(username, password);
        if (user != null) {
            request.getSession().setAttribute("user", user);
            response.sendRedirect("home.jsp");
        }
        else {
            response.sendRedirect("login.jsp?error=1");
        }
    }
}
