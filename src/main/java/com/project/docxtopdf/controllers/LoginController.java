package com.project.docxtopdf.controllers;

import java.io.IOException;

import com.project.docxtopdf.models.bean.User;
import com.project.docxtopdf.models.bo.UserBO;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
            response.sendRedirect("history");
        }
        else {
            response.sendRedirect("login.jsp?error=1");
        }
    }
}
