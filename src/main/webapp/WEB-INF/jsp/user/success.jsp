<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>登录成功页面</title>
</head>
欢迎你${user.username } <br/>
<a href="${pageContext.request.contextPath }/user/logout">退出</a><br/>
<a href="${pageContext.request.contextPath }/student/welcome">访问student相关</a><br/>
<a href="${pageContext.request.contextPath }/teacher/welcome">访问teacher相关</a>
</body>
</html>
