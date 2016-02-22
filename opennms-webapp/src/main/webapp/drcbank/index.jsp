<%--
  Created by IntelliJ IDEA.
  User: laiguanhui
  Date: 2016/2/19
  Time: 9:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="org.opennms.core.utils.DBTools" %>
<%@ page import="org.opennms.core.bank.IPSegment" %>

<html>
<head>
    <title>IP管理</title>
</head>
<body>
<div>hello world</div>

<%
    IPSegment ip = new IPSegment();
    ip.setBankname("test");
%>

<div >
    password=<%=(DBTools.DEFAULT_URL) %>
    bankname=<%=(ip.getBankname()) %>
</div>
</body>
</html>
