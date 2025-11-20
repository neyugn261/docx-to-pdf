package com.project.docxtopdf.models.bo;

import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {
    private Thread taskWorkerThread;
    private TaskWorker taskWorker;
}
