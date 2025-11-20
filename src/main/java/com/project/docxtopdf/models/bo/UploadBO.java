package com.project.docxtopdf.models.bo;

import com.project.docxtopdf.models.dao.TaskDAO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

public class UploadBO {
    public static final String UPLOAD_DIR = "uploads/";

    private static boolean isValidExtension(String fileName) {
        String lowerCaseFileName = fileName.toLowerCase();
        return lowerCaseFileName.endsWith(".docx") || lowerCaseFileName.endsWith(".doc");
    }

    private static String generateUniqueFileName(String originalFileName) {
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));
        String baseName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
        String uniqueSuffix = "_" + System.currentTimeMillis();
        return baseName + uniqueSuffix + fileExtension;
    }

    private static void saveFile(InputStream fileContent, String path) {
        System.out.println("Saving file to: " + path);

        File file = new File(path);

        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new RuntimeException("Failed to create parent directory: " + parentDir.getAbsolutePath());
            }
        }

        try {
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    throw new IOException("Failed to create file: " + path);
                }
                System.out.println("File created successfully: " + path);
            } else {
                System.out.println("File already exists, will overwrite: " + path);
            }
        } catch (IOException e) {
            throw new RuntimeException("File creation failed: " + e.getMessage(), e);
        }

        try (fileContent; var outStream = new FileOutputStream(file)) {
            fileContent.transferTo(outStream);
            System.out.println("File saved successfully: " + path);
        } catch (IOException e) {
            throw new RuntimeException("File saving failed: " + e.getMessage(), e);
        }
    }

    public static String uploadFile(String userId, String originalFileName, InputStream fileContent, String uploadDir) {
        String safeFileName = Paths.get(originalFileName).getFileName().toString();

        if (safeFileName.contains("..")) {
            throw new IllegalArgumentException("Invalid file name");
        }

        if (!isValidExtension(safeFileName)) {
            throw new IllegalArgumentException("Unsupported file type");
        }

        String uniqueFileName = generateUniqueFileName(safeFileName);

        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists()) {
            if (!uploadDirectory.mkdirs()) {
                throw new RuntimeException("Failed to create upload directory: " + uploadDir);
            }
            System.out.println("Upload directory created: " + uploadDir);
        }

        String storedFilePath = uploadDirectory + File.separator + uniqueFileName;
        saveFile(fileContent, storedFilePath);

        TaskDAO.saveTask(userId, safeFileName, uniqueFileName, "PENDING");

        return uniqueFileName;
    }
}
