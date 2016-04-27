<%--
  Created by IntelliJ IDEA.
  User: laiguanhui
  Date: 2016/4/27
  Time: 10:46
  To change this template use File | Settings | File Templates.
--%>
<%@page language="java"
        contentType="text/html;charset=UTF-8"
        session="true"
%>

<jsp:include page="/includes/header.jsp" flush="false">
    <jsp:param name="title" value="交换机配置管理" />
    <jsp:param name="headTitle" value="交换机配置管理" />
    <jsp:param name="breadcrumb" value="<a href='admin/index.jsp'>管理</a>" />
    <jsp:param name="breadcrumb" value="<a href='abcbank/switcher.jsp'>交换机配置管理</a>" />
    <jsp:param name="breadcrumb" value="文件上传" />
</jsp:include>

<form method="post" name="batch" enctype="multipart/form-data" action="abcbank/switcher.jsp">
    <br>
    &nbsp;&nbsp;
    请上传交换机命令文件（txt格式，第一行为#start，最后一行为#end，中间每行一条命令，交换机已经自动登录到特权模式，请从特权模式开始编写指令）
    <br/>
    <br/>
    &nbsp;&nbsp;
    <input type="file" name="batchOper" size="15">
    <br/>
    <br/>
    &nbsp;&nbsp;
    <input type="submit" id="ss('').batch" style="width:55px;height:23px;" value="确定">

</form>

<jsp:include page="/includes/footer.jsp" flush="false" />

