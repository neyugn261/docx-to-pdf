CREATE DATABASE IF NOT EXISTS docx_to_pdf_converter;
USE docx_to_pdf_converter;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tasks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    original_name VARCHAR(255) NOT NULL, # Tên tệp gốc
    stored_path VARCHAR(300) NOT NULL,
    output_path VARCHAR(300),
    status ENUM('PENDING','PROCESSING','DONE','ERROR') DEFAULT 'PENDING',
    created_at DATETIME DEFAULT NOW(),
    completed_at DATETIME NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
