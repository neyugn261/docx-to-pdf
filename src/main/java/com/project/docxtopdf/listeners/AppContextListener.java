package com.project.docxtopdf.listeners;

import com.project.docxtopdf.models.bo.TaskBO;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("=== Application Starting ===");
        
        String realPath = sce.getServletContext().getRealPath("/");
        
        // Khởi động TaskBO worker
        TaskBO taskBO = TaskBO.getInstance();
        taskBO.startWorker(realPath);
        
        System.out.println("=== Application Ready ===");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("=== Application Shutting Down ===");
        
        // Dừng TaskBO worker
        TaskBO taskBO = TaskBO.getInstance();
        taskBO.stopWorker();
        
        System.out.println("=== Application Stopped ===");
    }
}
