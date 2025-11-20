<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<% if (session.getAttribute("user") == null) {
       response.sendRedirect("login.jsp");
   } else { %>
<form action="upload" method="post" enctype="multipart/form-data">
    <label for="file">Choose file to upload:</label>
    <input type="file" id="file" name="file" required><br><br>
    <input type="submit" value="Upload File">
</form>
<% } %>
</body>
</html>
