package com.project.docxtopdf.models.bo;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {
    private Thread taskWorkerThread;
    private TaskWorker taskWorker;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String realPath = sce.getServletContext().getRealPath("/");
        taskWorker = new TaskWorker(realPath);
        taskWorkerThread = new Thread(taskWorker);
        taskWorkerThread.start();
        System.out.println("TaskWorker started.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (taskWorker != null) {
            taskWorker.stop();
        }
        if (taskWorkerThread != null) {
            try {
                taskWorkerThread.interrupt();
                taskWorkerThread.join(5000); // Chờ tối đa 5 giây
            } catch (InterruptedException e) {
                System.err.println("Error stopping TaskWorker thread: " + e.getMessage());
            }
        }
        System.out.println("TaskWorker stopped.");
    }
}
