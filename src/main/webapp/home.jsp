<%@ page import="com.project.docxtopdf.models.bean.User" %> <%@ page
import="java.util.List" %> <%@ page
import="com.project.docxtopdf.models.bean.Task" %> <%@ page
contentType="text/html;charset=UTF-8" language="java" %> <% User user = (User)
session.getAttribute("user"); if (user == null) {
response.sendRedirect("login"); return; } List<Task>
    tasks = (List<Task
        >) request.getAttribute("tasks"); boolean hasProcessingTask = tasks !=
        null && tasks.stream().anyMatch(t -> "PROCESSING".equals(t.getStatus())
        || "PENDING".equals(t.getStatus())); %>
        <html>
            <head>
                <title>Home</title>
                <meta
                    name="viewport"
                    content="width=device-width, initial-scale=1.0"
                />
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }
                    body {
                        font-family: Arial, sans-serif;
                        background: linear-gradient(
                            135deg,
                            #667eea 0%,
                            #764ba2 100%
                        );
                        min-height: 100vh;
                        padding: 20px;
                    }
                    .container {
                        max-width: 1500px;
                        margin: 0 auto;
                        background: white;
                        padding: 50px;
                        border-radius: 10px;
                        box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
                    }
                    .header {
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        margin-bottom: 30px;
                        padding-bottom: 20px;
                        border-bottom: 2px solid #f0f0f0;
                    }
                    h1 {
                        color: #333;
                        font-size: 24px;
                    }
                    .logout-btn {
                        padding: 8px 20px;
                        background: #667eea;
                        color: white;
                        text-decoration: none;
                        border-radius: 5px;
                        font-weight: 500;
                        transition: background 0.3s;
                    }
                    .logout-btn:hover {
                        background: #764ba2;
                    }
                    .upload-form {
                        margin-bottom: 30px;
                        padding: 20px;
                        background: #f9f9ff;
                        border-radius: 8px;
                        border: 2px dashed #667eea;
                    }
                    .upload-form h2 {
                        color: #333;
                        font-size: 18px;
                        margin-bottom: 15px;
                    }
                    .upload-form form {
                        display: flex;
                        gap: 10px;
                        align-items: center;
                        flex-wrap: wrap;
                    }
                    .upload-form input[type="file"] {
                        flex: 1;
                        padding: 10px;
                        border: 1px solid #ddd;
                        border-radius: 5px;
                        background: white;
                        min-width: 200px;
                    }
                    .upload-form button {
                        padding: 10px 20px;
                        background: linear-gradient(
                            135deg,
                            #667eea 0%,
                            #764ba2 100%
                        );
                        color: white;
                        border: none;
                        border-radius: 5px;
                        font-weight: 600;
                        cursor: pointer;
                        transition: transform 0.2s;
                    }
                    .upload-form button:hover {
                        transform: translateY(-2px);
                    }
                    .section-title {
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        margin-bottom: 15px;
                    }
                    .section-title h2 {
                        color: #333;
                        font-size: 18px;
                    }
                    .section-title a {
                        color: #667eea;
                        text-decoration: none;
                        font-weight: 500;
                    }
                    .section-title a:hover {
                        text-decoration: underline;
                    }
                    .task-table {
                        width: 100%;
                        border-collapse: collapse;
                        margin-bottom: 20px;
                    }
                    .task-table th,
                    .task-table td {
                        padding: 12px;
                        text-align: left;
                        border-bottom: 1px solid #e0e0e0;
                    }
                    .task-table th {
                        background: #f5f5f5;
                        color: #555;
                        font-weight: 600;
                    }
                    .task-table tr:hover {
                        background: #fafafa;
                    }
                    .status-badge {
                        padding: 4px 12px;
                        border-radius: 12px;
                        font-size: 13px;
                        font-weight: 500;
                        display: inline-block;
                    }
                    .status-PENDING {
                        background: #fff3cd;
                        color: #856404;
                    }
                    .status-PROCESSING {
                        background: #cfe2ff;
                        color: #084298;
                    }
                    .status-DONE {
                        background: #d1e7dd;
                        color: #0f5132;
                    }
                    .status-ERROR {
                        background: #f8d7da;
                        color: #842029;
                    }
                    .action-link {
                        color: #667eea;
                        text-decoration: none;
                        font-weight: 500;
                    }
                    .action-link:hover {
                        text-decoration: underline;
                    }
                    .message {
                        padding: 12px 15px;
                        margin-bottom: 20px;
                        border-radius: 5px;
                        font-size: 14px;
                    }
                    .message-success {
                        background-color: #d1e7dd;
                        color: #0f5132;
                        border: 1px solid #badbcc;
                    }
                    .message-error {
                        background-color: #f8d7da;
                        color: #842029;
                        border: 1px solid #f5c2c7;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Welcome, <%= user.getUsername() %>!</h1>
                        <a href="logout" class="logout-btn">Logout</a>
                    </div>

                    <%-- Hiển thị thông báo upload --%> <% String uploadStatus =
                    request.getParameter("upload"); if (uploadStatus != null) {
                    if ("success".equals(uploadStatus)) { %>
                    <div class="message message-success">
                        File uploaded successfully! It is now being processed.
                    </div>
                    <% } else if ("error".equals(uploadStatus)) { String
                    errorMessage = request.getParameter("message"); if
                    (errorMessage == null || errorMessage.isEmpty()) {
                    errorMessage = "Unknown error"; } %>
                    <div class="message message-error">
                        Upload failed: <%= errorMessage %>
                    </div>
                    <% } %> <% } %>

                    <div class="upload-form">
                        <h2>Upload a new DOCX file</h2>
                        <form
                            action="upload"
                            method="post"
                            enctype="multipart/form-data"
                        >
                            <input
                                type="file"
                                name="file"
                                accept=".doc,.docx"
                                required
                            />
                            <button type="submit">Upload and Convert</button>
                        </form>
                    </div>

                    <div class="section-title">
                        <h2>Your Conversion History</h2>
                        <a href="home">Refresh</a>
                    </div>
                    <table class="task-table">
                        <thead>
                            <tr>
                                <th>File Name</th>
                                <th>Status</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (tasks != null && !tasks.isEmpty()) { for
                            (Task task : tasks) { %>
                            <tr>
                                <td><%= task.getOriginalName() %></td>
                                <td>
                                    <span
                                        class="status-badge status-<%= task.getStatus() %>"
                                    >
                                        <%= task.getStatus() %>
                                    </span>
                                </td>
                                <td>
                                    <% if ("DONE".equals(task.getStatus())) { %>
                                    <a
                                        href="download?taskId=<%= task.getId() %>"
                                        class="action-link"
                                        >Download PDF</a
                                    >
                                    <% } else { %> - <% } %>
                                </td>
                            </tr>
                            <% } } else { %>
                            <tr>
                                <td colspan="3">No history found.</td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>

                <% if (hasProcessingTask) { %>
                <script>
                    // Tự động tải lại trang để cập nhật trạng thái
                    setTimeout(() => {
                        window.location.href = "home";
                    }, 5000); // 5 giây
                </script>
                <% } %>
            </body>
        </html>
    </Task></Task
>
