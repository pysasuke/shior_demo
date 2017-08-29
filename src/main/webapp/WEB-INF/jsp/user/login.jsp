<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>登录页面</title>
</head>
<body>
<form action="${pageContext.request.contextPath }/user/login" method="post">
    username:<input type="text" name="username"/><br>
    password:<input type="password" name="password"/><br>
    <input type="submit" value="登陆">${error }
</form>
</body>
</html>
