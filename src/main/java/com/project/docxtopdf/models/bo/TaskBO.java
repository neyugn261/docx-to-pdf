package com.project.docxtopdf.models.bo;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.project.docxtopdf.enums.TaskStatus;
import com.project.docxtopdf.models.bean.Task;
import com.project.docxtopdf.models.dao.TaskDAO;
import com.project.docxtopdf.utils.Converter;

public class TaskBO {
    
    // ===== QUEUE MANAGEMENT =====
    private static TaskBO instance;
    private final BlockingQueue<Task> taskQueue;
    private final ExecutorService executorService;
    private volatile boolean running = false;
    private Thread workerThread;
    private String uploadDir;
    private String outputDir;
    private static final int MAX_CONCURRENT_TASKS = 5;

    private TaskBO() {
        this.taskQueue = new LinkedBlockingQueue<>();
        this.executorService = Executors.newFixedThreadPool(MAX_CONCURRENT_TASKS);
    }

    public static synchronized TaskBO getInstance() {
        if (instance == null) {
            instance = new TaskBO();
        }
        return instance;
    }

    // ===== WORKER LIFECYCLE =====
    
    /**
     * Khởi động worker (gọi từ AppContextListener)
     */
    public void startWorker(String realPath) {
        if (running) {
            System.out.println("TaskWorker already running");
            return;
        }
        
        this.uploadDir = realPath + UploadBO.UPLOAD_DIR;
        this.outputDir = realPath + Converter.PDF_DIR;
        this.running = true;
        
        // Load pending tasks từ DB vào queue
        loadPendingTasksFromDatabase();
        
        // Khởi động worker thread
        workerThread = new Thread(this::runWorker);
        workerThread.start();
        
        System.out.println("TaskWorker started with in-memory queue");
        System.out.println("Queue size: " + taskQueue.size() + " tasks");
    }

    
    public void stopWorker() {
        System.out.println("Stopping TaskWorker...");
        running = false;
        
        if (workerThread != null) {
            workerThread.interrupt();
            try {
                workerThread.join(5000);
            } catch (InterruptedException e) {
                System.err.println("Error stopping worker thread: " + e.getMessage());
            }
        }
        
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                System.err.println("Some tasks were forcefully terminated");
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        System.out.println("TaskWorker stopped.");
    }
    
    private void runWorker() {
        System.out.println("TaskWorker running - using in-memory queue (no DB polling)");
        
        while (running) {
            try {
                // Blocking call - chờ cho đến khi có task
                Task task = taskQueue.take();
                
                System.out.println("Task received from queue: " + task.getId());
                executorService.submit(() -> processTask(task));
                
            } catch (InterruptedException e) {
                running = false;
                Thread.currentThread().interrupt();
                System.out.println("TaskWorker interrupted - shutting down");
            }
        }
    }

   
    private void processTask(Task task) {
        try {
            System.out.println("Processing task ID: " + task.getId());
            updateTaskStatus(task.getId(), TaskStatus.PROCESSING);

            String docxFilePath = uploadDir + File.separator + task.getStoredPath();
            File docxFile = new File(docxFilePath);

            if (!docxFile.exists()) {
                throw new Exception("File not found: " + docxFilePath);
            }

            try (InputStream docxInputStream = new FileInputStream(docxFile)) {
                String relativePath = Converter.convertDocxToPdf(docxInputStream, task.getOriginalName(), outputDir);
                updateTaskStatus(task.getId(), TaskStatus.DONE, relativePath);
                System.out.println("Task ID: " + task.getId() + " completed. Output: " + relativePath);
            }

        } catch (Exception e) {
            System.err.println("Error processing task ID: " + task.getId() + " - " + e.getMessage());
            e.printStackTrace();
            updateTaskStatus(task.getId(), TaskStatus.ERROR);
        }
    }

    // ===== QUEUE OPERATIONS =====
    
  
    public boolean submitTask(Task task) {
        try {
            boolean offered = taskQueue.offer(task);
            if (offered) {
                System.out.println("Task submitted to queue: " + task.getOriginalName());
            }
            return offered;
        } catch (Exception e) {
            System.err.println("Failed to submit task to queue: " + e.getMessage());
            return false;
        }
    }

    
    private void loadPendingTasksFromDatabase() {
        try {
            List<Task> pendingTasks = TaskDAO.getAllPendingTasks();
            int loadedCount = 0;
            
            for (Task task : pendingTasks) {
                if (taskQueue.offer(task)) {
                    loadedCount++;
                }
            }
            
            System.out.println("Loaded " + loadedCount + " pending tasks from database into queue");
        } catch (Exception e) {
            System.err.println("Error loading pending tasks: " + e.getMessage());
        }
    }

    public int getQueueSize() {
        return taskQueue.size();
    }

    // ===== BUSINESS LOGIC METHODS =====
    
    public static List<Task> getTasksByUserId(String userId) {
        return TaskDAO.getTasksByUserId(userId);
    }
    
    public static Task getPendingTask() {
        return TaskDAO.getPendingTask();
    }
    
    public static List<Task> getAllPendingTasks() {
        return TaskDAO.getAllPendingTasks();
    }
    
    public static void updateTaskStatus(String taskId, String status) {
        TaskDAO.updateTaskStatus(taskId, status);
    }
    
    public static void updateTaskStatus(String taskId, String status, String outputPath) {
        TaskDAO.updateTaskStatus(taskId, status, outputPath);
    }    
    
    public static Task getTaskForDownload(String userId, String taskId) {
        List<Task> tasks = TaskDAO.getTasksByUserId(userId);
        
        // Tìm task thuộc về user
        Task task = tasks.stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .orElse(null);
        
        // Kiểm tra task tồn tại và đã hoàn thành
        if (task != null && "DONE".equals(task.getStatus()) 
                && task.getOutputPath() != null 
                && !task.getOutputPath().isEmpty()) {
            return task;
        }
        
        return null;
    }
}
