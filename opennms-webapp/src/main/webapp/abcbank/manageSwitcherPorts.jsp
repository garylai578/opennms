<%--
  Created by IntelliJ IDEA.
  User: laiguanhui
  Date: 2016/4/12
  Time: 12:33
  To change this template use File | Settings | File Templates.
--%>
<%@page language="java"
        contentType="text/html;charset=UTF-8"
        session="true"
%>

<%@page import="org.opennms.core.bank.SwitcherUtil" %>

<jsp:include page="/includes/header.jsp" flush="false">
    <jsp:param name="title" value="交换机配置管理" />
    <jsp:param name="headTitle" value="交换机配置管理" />
    <jsp:param name="breadcrumb" value="<a href='admin/index.jsp'>管理</a>" />
    <jsp:param name="breadcrumb" value="<a href='abcbank/switcher.jsp'>交换机配置管理</a>" />
    <jsp:param name="breadcrumb" value="交换机端口开关" />
</jsp:include>

<%
    String id = request.getParameter("id");
    String host = request.getParameter("host-" + id);
    String user = request.getParameter("user-" + id);
    String password = request.getParameter("password-" + id);
%>

<script type="text/javascript" >
    function upInterface(inter){
        document.manageSwticher.action="abcbank/manageSwitcher";
        document.manageSwticher.interface.value=inter;
        document.manageSwticher.type.value="up-interface";
        document.manageSwticher.submit();
    }

    function downInterface(inter){
        document.manageSwticher.action="abcbank/manageSwitcher";
        document.manageSwticher.interface.value=inter;
        document.manageSwticher.type.value="down-interface";
        document.manageSwticher.submit();
    }

    function dot1x(inter){
        document.manageSwticher.action="abcbank/manageSwitcher";
        document.manageSwticher.interface.value=inter;
        document.manageSwticher.type.value="dot1x";
        document.manageSwticher.submit();
    }

    function undoDot1x(inter){
        document.manageSwticher.action="abcbank/manageSwitcher";
        document.manageSwticher.interface.value=inter;
        document.manageSwticher.type.value="undoDot1x";
        document.manageSwticher.submit();
    }

</script>

<form method="post" name="manageSwticher">
    <input type="hidden" name="interface"/>
    <input type="hidden" name="type" />

<br>
    <div>
        交换机IP：<%=host%>
    </div>

    <table width="100%" border="1" cellspacing="0" cellpadding="2" bordercolor="black">
        <tr bgcolor="#999999">
            <td width="3%" align="center"><b>序号</b></td>
            <td width="3%" align="center"><b>端口号</b></td>
            <td width="3%" align="center"><b>端口状态</b></td>
            <td width="3%" align="center"><b>dot1x认证</b></td>
            <td width="8%" align="center"><b>操作</b></td>
        </tr>

        <%
            SwitcherUtil util = new SwitcherUtil(host, user, password);
            String[] interfaces = util.getInterfaces();
            String[] status = util.getStates();
            String[] dot1x = util.getDot1x();
            int size = interfaces.length;
            int row = 0;
            for(int i = 0; i < size; ++i){

        %>
        <tr bgcolor=<%=row%2==0 ? "#ffffff" : "#cccccc"%>>
            <td width="3%" align="center">
                <div id="id-<%=row%>">
                    <%=row+1%>
                </div>
            </td>

            <td width="3%" align="center">
                <div id="interface-<%=row%>">
                    <%= interfaces[i] %>
                </div>
            </td>

            <td width="3%" align="center">
                <div id="status-<%=row%>">
                    <%= status[i] %>
                </div>
            </td>

            <td width="3%" align="center">
                <div id="dot1x-<%=row%>">
                    <%= dot1x[i] %>
                </div>
            </td>

            <td width="3%" align="center">
                <a id="<%= "ss("+id+").doUp" %>" href="javascript:upInterface('<%=interfaces[i]%>')">开启端口</a>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <a id="<%= "ss("+id+").doDown" %>" href="javascript:downInterface('<%=interfaces[i]%>')" >关闭端口</a>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <a id="<%= "ss("+id+").doDot1x" %>" href="javascript:dot1x('<%=interfaces[i]%>')">端口认证</a>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <a id="<%= "ss("+id+").undoDot1x" %>" href="javascript:undoDot1x('<%=interfaces[i]%>')">取消认证</a>
                &nbsp;&nbsp;&nbsp;&nbsp;
            </td>

        <%
                row++;
                }
        %>

    </table>
</form>

<jsp:include page="/includes/footer.jsp" flush="false" />
