<%@ page import="com.project.docxtopdf.models.bean.User" %> <%@ page
import="java.util.List" %> <%@ page
import="com.project.docxtopdf.models.bean.Task" %> <%@ page
contentType="text/html;charset=UTF-8" language="java" %> <% User user = (User)
session.getAttribute("user"); if (user == null) {
response.sendRedirect("login.jsp"); return; } List<Task>
  tasks = (List<Task
    >) request.getAttribute("tasks"); boolean hasProcessingTask = tasks != null
    && tasks.stream().anyMatch(t -> "PROCESSING".equals(t.getStatus()) ||
    "PENDING".equals(t.getStatus())); %>
    <html>
      <head>
        <title>Home</title>
        <style>
          body {
            font-family: sans-serif;
          }
          .container {
            width: 80%;
            margin: 0 auto;
          }
          .upload-form {
            margin-bottom: 20px;
            padding: 15px;
            border: 1px solid #ccc;
            border-radius: 5px;
          }
          .task-table {
            width: 100%;
            border-collapse: collapse;
          }
          .task-table th,
          .task-table td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
          }
          .task-table th {
            background-color: #f2f2f2;
          }
          .status-PENDING {
            color: orange;
          }
          .status-PROCESSING {
            color: blue;
          }
          .status-DONE {
            color: green;
          }
          .status-ERROR {
            color: red;
          }
          .message {
            padding: 10px;
            margin-bottom: 15px;
            border-radius: 4px;
          }
          .message-success {
            background-color: #d4edda;
            color: #155724;
          }
          .message-error {
            background-color: #f8d7da;
            color: #721c24;
          }
        </style>
      </head>
      <body>
        <div class="container">
          <h1>Welcome, <%= user.getUsername() %>!</h1>

          <%-- Hiển thị thông báo upload --%> <% String uploadStatus =
          request.getParameter("upload"); if (uploadStatus != null) { if
          ("success".equals(uploadStatus)) { %>
          <div class="message message-success">
            File uploaded successfully! It is now being processed.
          </div>
          <% } else if ("error".equals(uploadStatus)) { String errorMessage =
          request.getParameter("message"); if (errorMessage == null ||
          errorMessage.isEmpty()) { errorMessage = "Unknown error"; } %>
          <div class="message message-error">
            Upload failed: <%= errorMessage %>
          </div>
          <% } %> <% } %>

          <div class="upload-form">
            <h2>Upload a new DOCX file</h2>
            <form action="upload" method="post" enctype="multipart/form-data">
              <input type="file" name="file" accept=".doc,.docx" required />
              <button type="submit">Upload and Convert</button>
            </form>
          </div>

          <h2>Your Conversion History (<a href="history">Refresh</a>)</h2>
          <table class="task-table">
            <thead>
              <tr>
                <th>File Name</th>
                <th>Status</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              <% if (tasks != null && !tasks.isEmpty()) { for (Task task :
              tasks) { %>
              <tr>
                <td><%= task.getOriginalName() %></td>
                <td class="status-<%= task.getStatus() %>">
                  <%= task.getStatus() %>
                </td>
                <td>
                  <% if ("DONE".equals(task.getStatus())) { %>
                  <a href="download?taskId=<%= task.getId() %>">Download PDF</a>
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
          <br />
          <a href="index.jsp">Logout</a>
        </div>

        <% if (hasProcessingTask) { %>
        <script>
          // Tự động tải lại trang để cập nhật trạng thái
          setTimeout(() => {
            window.location.href = "history";
          }, 5000); // 5 giây
        </script>
        <% } %>
      </body>
    </html>
  </Task></Task
>
