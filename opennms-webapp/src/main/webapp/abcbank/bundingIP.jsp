<%--
  Created by IntelliJ IDEA.
  User: laiguanhui
  Date: 2016/4/13
  Time: 14:51
  To change this template use File | Settings | File Templates.
--%>
<%@page language="java"
        contentType="text/html;charset=UTF-8"
        session="true"
%>

<%@page import="org.opennms.core.bank.Switcher" %>
<%@ page import="org.opennms.core.bank.SwitcherOperator" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.Properties" %>
<%@ page import="org.opennms.core.bank.SwitcherUtil" %>

<jsp:include page="/includes/header.jsp" flush="false">
    <jsp:param name="title" value="交换机配置管理" />
    <jsp:param name="headTitle" value="交换机配置管理" />
    <jsp:param name="breadcrumb" value="<a href='admin/index.jsp'>管理</a>" />
    <jsp:param name="breadcrumb" value="<a href='abcbank/switcher.jsp'>交换机配置管理</a>" />
    <jsp:param name="breadcrumb" value="交换机地址绑定" />
</jsp:include>

<%
    String id = request.getParameter("id");
    String host = request.getParameter("host-" + id);
    String user = request.getParameter("user-" + id);
    String password = request.getParameter("password-" + id);

    if(host == null)
        host = (String)request.getAttribute("host");
    if(user == null)
        user = (String)request.getAttribute("user");
    if(password == null)
        password = (String)request.getAttribute("password");

    String backContent = (String)request.getAttribute("backContent");
    String result = (String)request.getAttribute("result");
    String ips = (String)request.getAttribute("ips");
    String macs = (String)request.getAttribute("macs");
%>

<script type="text/javascript" >

    function bundingIP()
    {
        var no_dot1x = document.getElementById("no_dot1x");
        var dot1x = document.getElementById("dot1x");
        if (no_dot1x.checked == true)
            document.allSwitchers.no_dot1x_before.value = 1;
        if(dot1x.checked == true)
            document.allSwitchers.dot1x_after.value = 1;

        document.allSwitchers.action="abcbank/bundingIP";
        document.allSwitchers.submit();
    }

    function delBundingMAC()
    {
        document.allSwitchers.action="abcbank/deleteBundingMac";
        document.allSwitchers.submit();
    }
</script>


<form method="post" name="allSwitchers">
    <input type="hidden" name="host" value="<%=host%>"/>
    <input type="hidden" name="user" value="<%=user%>"/>
    <input type="hidden" name="password" value="<%=password%>" />
    <input type="hidden" name="no_dot1x_before" value="0"/>
    <input type="hidden" name="dot1x_after" value="0"/>

    <h3>交换机IP：<%=host%></h3>

    <table>
        <tr>
            <td>
                端口：<input id="port" name="port" type="text" value="23" size="5">
            </td>

            <td>
                认证操作交换机物理端范围：<input id="inter0" name="inter0" type="text" value="0" size="3">/<input id="inter1" name="inter1" type="text" value="11" size="3">-<input id="inter2" name="inter2" type="text" value="40" size="3">
            </td>
        </tr>

        <tr>
            <td colspan="2">
                <input id="no_dot1x" type="checkbox" value="" />执行前关认证
                &nbsp;&nbsp;&nbsp;&nbsp;
                <input id="dot1x" type="checkbox" value="" />执行后开认证
                &nbsp;&nbsp;&nbsp;&nbsp;
                <a href="javascript:bundingIP()" ><input type="button" value="执行"></a>
            </td>
        </tr>
    </table>

    <table width="100%" border="1" cellspacing="0" cellpadding="2" bordercolor="black">

        <tr bgcolor="#999999">
            <td width="5%" align="center"><b>待绑定IP（一行一个）</b></td>
            <td width="10%" align="center"><b>交换机返回内容</b></td>
            <td width="10%" align="center"><b>执行结果</b></td>
        </tr>

        <tr>
            <td>
                <textarea id="ips" name="ips" rows="20" style="width:100%; overflow: auto;"><%=((ips == null || ips.equals("")) ? "" : ips)%></textarea>
            </td>

            <td>
                <textarea id="backContent" name="backContent" rows="20" style="width:100%; overflow: auto;" disabled="disabled"><%=((backContent == null || backContent.equals("")) ? "&nbsp;" : backContent)%></textarea>
            </td>

            <td>
                <textarea id="result" name="result" rows="20" style="width:100%; overflow: auto;" disabled="disabled"><%= ((result == null || result.equals("")) ? "&nbsp;" : result) %></textarea>
            </td>
        </tr>
    </table>


    <table width="100%" border="1" cellspacing="0" cellpadding="2" bordercolor="black">
        强制删除旧绑定关系
        <tr bgcolor="#999999">
            <td width="5%" align="left" colspan="2"><b>在下面输入MAC列表，一行一个</b></td>
        </tr>

        <tr valign="top">
            <td>
                <textarea id="macs" name="macs" type="text" rows="10" style="width:100%; overflow: auto;"><%= ((macs == null || macs.equals("")) ? "" : macs) %></textarea>
                <a type="button" href="javascript:delBundingMAC()"><input type="button" value="删除旧绑定关系"/> </a>
            </td>

            <td valign="top">
                如果该设备以前已经做过绑定但更换了交换机端口，需要删除就绑定，删除旧绑定需要管理员上报MAC
            </td>
        </tr>
    </table>
</form>


<jsp:include page="/includes/footer.jsp" flush="false" />
