package com.project.docxtopdf.models.bo;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.project.docxtopdf.enums.TaskStatus;
import com.project.docxtopdf.models.bean.Task;

public class TaskWorker implements Runnable {
    private volatile boolean running = true;
    private final String uploadDir;
    private final String outputDir;

    public TaskWorker(String realPath) {
        this.uploadDir = realPath + UploadBO.UPLOAD_DIR;
        this.outputDir = realPath + Converter.PDF_DIR;
    }

    @Override
    public void run() {
        while (running) {
            Task task = TaskBO.getPendingTask();
            if (task != null) {
                processTask(task);
            } else {
                try {
                    // Đợi 5 giây nếu không có task
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    running = false;
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void processTask(Task task) {
        try {
            System.out.println("Processing task ID: " + task.getId());
            TaskBO.updateTaskStatus(task.getId(), TaskStatus.PROCESSING);

            String docxFilePath = uploadDir + File.separator + task.getStoredPath();
            File docxFile = new File(docxFilePath);

            if (!docxFile.exists()) {
                throw new Exception("File not found: " + docxFilePath);
            }

            try (InputStream docxInputStream = new FileInputStream(docxFile)) {
                // convertDocxToPdf trả về đường dẫn tương đối (ví dụ: pdfs/file.pdf)
                String relativePath = Converter.convertDocxToPdf(docxInputStream, task.getOriginalName(), outputDir);
                TaskBO.updateTaskStatus(task.getId(), TaskStatus.DONE, relativePath);
                System.out.println("Task ID: " + task.getId() + " completed. Relative output: " + relativePath);
            }

        } catch (Exception e) {
            System.err.println("Error processing task ID: " + task.getId() + " - " + e.getMessage());
            e.printStackTrace();
            TaskBO.updateTaskStatus(task.getId(), TaskStatus.ERROR);
        }
    }

    public void stop() {
        running = false;
    }
}
