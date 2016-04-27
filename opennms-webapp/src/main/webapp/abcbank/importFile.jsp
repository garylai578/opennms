<%--
  Created by IntelliJ IDEA.
  User: laiguanhui
  Date: 2016/4/27
  Time: 10:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>文件上传</title>
</head>
<body>

</body>
<form method="post" name="batch" enctype="multipart/form-data" action="index.jsp">
    &nbsp;&nbsp;
    请上传交换机命令文件（txt格式，第一行为#start，最后一行为#end，中间每行一条命令，交换机已经自动登录到特权模式，请从特权模式开始编写指令）
    <br/>
    &nbsp;&nbsp;
    <input type="file" name="batchOper" size="15">
    <br/>
    &nbsp;&nbsp;
    <input type="submit" id="ss('').batch" style="width:55px;height:23px;" value="确定">

</form>
</html>
