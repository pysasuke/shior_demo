<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>权限不足跳转页面</title>
</head>
<body>
认证未通过，或者权限不足<br/>
<a href="${pageContext.request.contextPath }/user/logout">退出</a>
</body>
</html>
