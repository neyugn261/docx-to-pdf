<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Login</title>
  </head>
  <body>
    <% if (session.getAttribute("user") != null) {
    response.sendRedirect("history"); return; } %>
    <h2>Login</h2>
    <% if (request.getParameter("error") != null) { %>
    <p style="color: red">Invalid username or password. Please try again.</p>
    <% } %>

    <form action="login" method="post">
      <label for="username">Username:</label>
      <input type="text" id="username" name="username" required /><br /><br />
      <label for="password">Password:</label>
      <input
        type="password"
        id="password"
        name="password"
        required
      /><br /><br />
      <input type="submit" value="Login" />
    </form>
    <p>Don't have an account? <a href="register.jsp">Register here</a></p>
  </body>
</html>
