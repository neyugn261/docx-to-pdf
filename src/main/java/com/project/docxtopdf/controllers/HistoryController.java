package com.project.docxtopdf.controllers;

import java.io.IOException;
import java.util.List;

import com.project.docxtopdf.models.bean.Task;
import com.project.docxtopdf.models.bean.User;
import com.project.docxtopdf.models.dao.TaskDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "history", value = "/history")
public class HistoryController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        var session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        List<Task> tasks = TaskDAO.getTasksByUserId(user.getId());

        request.setAttribute("tasks", tasks);
        request.getRequestDispatcher("home.jsp").forward(request, response);
    }
}
