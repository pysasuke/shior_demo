<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<html>
<head>
    <title>Register</title>
</head>
<body>

<form action="${pageContext.request.contextPath }/user/register"  method="post">
    <div>
        <label>userName:</label>
        <input type="text" name="username" placeholder="请输入用户名(3-16个字符)" value="${user.username}"/><br/>
    </div>
    <div>
        <label>password:</label>
        <input type="password" name="password" placeholder="请输入密码(3-16个字符)" value="${user.password}"><br/>
    </div>
    <div>
        <label>email:</label>
        <input type="email" name="email" placeholder="Enter email" value="${user.email}"><br/>
    </div>
    <div>
        <label>sex:</label>
        <div>
            <label>
                <input type="radio" name="sex" id="optionsRadios1" value="male" checked> male
            </label>
        </div>
        <div>
            <label>
                <input type="radio" name="sex" id="optionsRadios2" value="female">female
            </label>
        </div>
    </div>
    <div>
        <input type="submit" value="register"/>${error }
    </div>

</form>


</body>
</html>
