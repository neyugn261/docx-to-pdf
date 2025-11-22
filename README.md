# Docx to PDF Converter

This is a Java web application that converts DOCX files to PDF format. It uses a background worker to process conversion tasks asynchronously.

## Features

- User registration and login
- Upload DOCX files
- Asynchronous conversion of DOCX to PDF
- View conversion history
- Download converted PDF files

## Technology Stack

- **Backend**: Java, Jakarta Servlet
- **Frontend**: JSP, HTML, CSS
- **Database**: MySQL
- **Build Tool**: Maven
- **Libraries**:
  - `jakarta.servlet-api`: For servlet programming
  - `mysql-connector-java`: MySQL JDBC driver
  - `jbcrypt`: For password hashing
  - `itextpdf`: For PDF generation
  - `poi-ooxml`: For reading DOCX files

## Environment Setup

### Prerequisites

- **Java Development Kit (JDK)**: Version 11 or higher.
- **Apache Maven**: To build the project.
- **MySQL Server**: As the database.
- **Web Server**: A servlet container like Apache Tomcat (version 10.1 or higher).
- **Docker** (Optional): For a containerized setup.

### 1. Database Setup

1.  **Create Database**: Create a MySQL database named `docx_to_pdf_converter`.
    ```sql
    CREATE DATABASE IF NOT EXISTS docx_to_pdf_converter;
    ```
2.  **Run Initialization Script**: Execute the `src/main/resources/database/init.sql` script to create the `users` and `tasks` tables. This will set up the necessary schema for the application.

### 2. Configuration

1.  **Copy Configuration File**:
    Rename `src/main/resources/config/config.properties.example` to `config.properties`.

2.  **Update Database Credentials**:
    Open `src/main/resources/config/config.properties` and update the database connection details:
    ```properties
    db.host=localhost
    db.port=3306
    db.name=docx_to_pdf_converter
    db.user=your_db_user
    db.pass=your_db_password
    ```
    _Note: The default configuration assumes a local MySQL instance with user `root` and no password. Adjust as necessary._

## Build and Run

### Option 1: Using Docker (Recommended)

The project includes a `docker-compose.yml` file for easy setup.

1.  **Build and Start Containers**:
    Run the following command in the project root directory:

    ```bash
    docker-compose up --build
    ```

    This will:

    - Build the Java application using Maven.
    - Create a Docker image for the application.
    - Start two services: `app` (the web application) and `db` (the MySQL database).

2.  **Access the Application**:
    Open your web browser and go to `http://localhost:8080/docx-to-pdf`.

### Option 2: Manual Build with Tomcat

1.  **Build the Project**:
    Use Maven to build the project. This will generate a `.war` file in the `target/` directory.

    ```bash
    mvn clean package
    ```

    The output file will be named `docx-to-pdf-1.0-SNAPSHOT.war`.

2.  **Deploy to Tomcat**:

    - Copy the generated `.war` file to the `webapps` directory of your Apache Tomcat installation.
    - Start the Tomcat server.

3.  **Access the Application**:
    The application will be available at `http://localhost:8080/docx-to-pdf-1.0-SNAPSHOT`. You can rename the `.war` file to `docx-to-pdf.war` to access it at `http://localhost:8080/docx-to-pdf`.

## How It Works

1.  **User Interaction**:

    - Users register and log in.
    - After logging in, they are redirected to `home.jsp`, which is mapped to the `/history` servlet.
    - The `HistoryController` fetches the user's past and current conversion tasks from the database and displays them.

2.  **File Upload**:

    - The user uploads a `.docx` file via the form on `home.jsp`.
    - The `UploadController` receives the file.
    - `UploadBO` saves the file to the `uploads/` directory with a unique name and creates a new task in the `tasks` table with a `PENDING` status.

3.  **Asynchronous Conversion**:

    - The `AppContextListener` starts a background thread (`TaskWorker`) when the application starts.
    - The `TaskWorker` periodically checks the database for `PENDING` tasks using `TaskDAO.getPendingTask()`.
    - When a pending task is found, the worker:
      - Updates the task status to `PROCESSING`.
      - Calls the `Converter` class to perform the DOCX to PDF conversion.
      - The `Converter` uses Apache POI to read the `.docx` file and iTextPDF to create the `.pdf` file.
      - The output PDF is saved in the `pdfs/` directory.
      - If successful, the task status is updated to `DONE`, and the output path is saved.
      - If an error occurs, the status is set to `ERROR`.

4.  **Viewing and Downloading**:
    - The `home.jsp` page automatically refreshes every 5 seconds if there are any `PENDING` or `PROCESSING` tasks.
    - Once a task's status is `DONE`, a "Download" link appears next to it.
    - The `DownloadController` handles the file download, ensuring that only the user who owns the task can download the corresponding file.
