package com.project.docxtopdf.controllers;

import java.io.IOException;
import java.util.List;

import com.project.docxtopdf.models.bean.Task;
import com.project.docxtopdf.models.bean.User;
import com.project.docxtopdf.models.bo.TaskBO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/home")
public class HomeController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        // Kiểm tra đăng nhập
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }

        // Lấy danh sách tasks và hiển thị trang home
        User user = (User) session.getAttribute("user");
        List<Task> tasks = TaskBO.getTasksByUserId(user.getId());

        request.setAttribute("tasks", tasks);
        request.getRequestDispatcher("home.jsp").forward(request, response);
    }
}
