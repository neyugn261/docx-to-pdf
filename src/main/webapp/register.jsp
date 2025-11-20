<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
	<head>
		<title>Register</title>
	</head>
	<body>
		<h2>Register</h2>
		<form action="register" method="post">
			<label for="username">Username:</label>
			<input
				type="text"
				id="username"
				name="username"
				required
			/><br /><br />
			<label for="password">Password:</label>
			<input
				type="password"
				id="password"
				name="password"
				required
			/><br /><br />
			<input type="submit" value="Register" />
		</form>
		<p>Already have an account? <a href="login.jsp">Login here</a></p>
	</body>
</html>
