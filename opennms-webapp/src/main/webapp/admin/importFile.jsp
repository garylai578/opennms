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
    <jsp:param name="title" value="配置SNMP通过IP" />
    <jsp:param name="headTitle" value="配置SNMP通过IP" />
    <jsp:param name="breadcrumb" value="<a href='admin/index.jsp'>管理</a>" />
    <jsp:param name="breadcrumb" value="<a href='admin/snmpConfig.jsp'>配置SNMP通过IP</a>" />
    <jsp:param name="breadcrumb" value="文件上传" />
</jsp:include>

<form method="post" name="batch" enctype="multipart/form-data" action="admin/snmpConfig.jsp">
    <br>
    &nbsp;&nbsp;
    请上传通过IP配置SNMP的文件（txt格式，第一行为#start，最后一行为#end，中间每行对应IP和SNMP的信息，
    分别是：*第一个IP地址,最后一个IP地址,*团体名,超时,版本,重试,端口。以半角逗号隔开，其中标星号的为必填）
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

