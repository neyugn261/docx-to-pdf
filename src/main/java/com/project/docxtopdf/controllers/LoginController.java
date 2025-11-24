package com.project.docxtopdf.controllers;

import java.io.IOException;

import com.project.docxtopdf.models.bean.User;
import com.project.docxtopdf.models.bo.UserBO;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class LoginController extends HttpServlet {

    public  LoginController() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // Nếu đã đăng nhập, chuyển đến home
            var session = request.getSession(false);
            if (session != null && session.getAttribute("user") != null) {
                response.sendRedirect("home");
                return;
            }
            // Hiển thị trang login
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user = UserBO.checkLogin(username, password);
        if (user != null) {
            request.getSession().setAttribute("user", user);
            response.sendRedirect("home");
        }
        else {
            response.sendRedirect("login?error=1");
        }
    }
}
