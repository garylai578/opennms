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
    <jsp:param name="breadcrumb" value="交换机端口操作" />
</jsp:include>

<%
    String id = request.getParameter("id");
    String host = request.getParameter("host-" + id);
    String user = request.getParameter("user-" + id);
    String password = request.getParameter("password-" + id);
    if(id == null)
        id = (String)request.getAttribute("id");
    if(host == null)
        host = (String)request.getAttribute("host-" + id);
    if(user == null)
        user = (String)request.getAttribute("user-" + id);
    if(password == null)
        password = (String)request.getAttribute("password-" + id);
%>

<script type="text/javascript" >

    function upInterfaces(rows){
        var interfaces="";
        for (var i = 0; i < rows; ++i) {

            var inter = document.getElementById("choose-"+i);
            if (inter.checked == true)
                interfaces += document.getElementById("interfaces-"+i).value + "\t";
        }
        upInterface(interfaces);
    }

    function upInterface(inter){
        document.manageSwticher.action="abcbank/manageSwitcher";
        document.manageSwticher.interface.value=inter;
        document.manageSwticher.type.value="up-interface";
        document.manageSwticher.submit();
    }

    function downInterfaces(rows){
        var interfaces="";
        for (var i = 0; i < rows; ++i) {

            var inter = document.getElementById("choose-"+i);
            if (inter.checked == true)
                interfaces += document.getElementById("interfaces-"+i).value + "\t";
        }
        downInterface(interfaces);
    }

    function downInterface(inter){
        document.manageSwticher.action="abcbank/manageSwitcher";
        document.manageSwticher.interface.value=inter;
        document.manageSwticher.type.value="down-interface";
        document.manageSwticher.submit();
    }

    function dot1xs(rows){
        var interfaces="";
        for (var i = 0; i < rows; ++i) {

            var inter = document.getElementById("choose-"+i);
            if (inter.checked == true)
                interfaces += document.getElementById("interfaces-"+i).value + "\t";
        }
        dot1x(interfaces);
    }

    function dot1x(inter){
        document.manageSwticher.action="abcbank/manageSwitcher";
        document.manageSwticher.interface.value=inter;
        document.manageSwticher.type.value="dot1x";
        document.manageSwticher.submit();
    }

    function undoDot1xs(rows){
        var interfaces="";
        for (var i = 0; i < rows; ++i) {

            var inter = document.getElementById("choose-"+i);
            if (inter.checked == true) {
                interfaces += document.getElementById("interfaces-"+i).value + "\t";
            }
        }
        undoDot1x(interfaces);
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
    <input type="hidden" name="host" value="<%=host%>"/>
    <input type="hidden" name="user" value="<%=user%>"/>
    <input type="hidden" name="password" value="<%=password%>"/>
    <input type="hidden" name="id" value="<%=id%>"/>
<br>
    <div>
        交换机IP：<%=host%>
    </div>

    <table width="100%" border="1" cellspacing="0" cellpadding="2" bordercolor="black">
        <tr bgcolor="#999999">
            <td width="3%" align="center"><b>选择</b></td>
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
//            util.diconnect();
            int size = interfaces.length;
            int row = 0;
            for(int i = 0; i < size; ++i){

        %>
        <tr bgcolor=<%=row%2==0 ? "#ffffff" : "#cccccc"%>>
            <td width="3%" align="center">
                <div id="id-<%=row%>">
                    <input id="choose-<%=row%>" type="checkbox" value="" />
                </div>
            </td>

            <td width="3%" align="center">
                <div id="interface-<%=row%>">
                    <%= interfaces[i] %>
                    <input id="interfaces-<%=row%>" type="hidden" value="<%=interfaces[i]%>">
                </div>
            </td>

            <td width="3%" align="center">
                <div id="status-<%=row%>">
                    <%
                        if(status[i].equals("down"))
                            out.print("<font color='red'>" + status[i] + "</font>");
                        else if(status[i].equals("up"))
                            out.print(status[i]);
                    %>
                </div>
            </td>

            <td width="3%" align="center">
                <div id="dot1x-<%=row%>">
                    <%
                        if(dot1x[i].equals("none"))
                            out.print("<font color='red'>" + dot1x[i] + "</font>");
                        else if(dot1x[i].equals("auto"))
                            out.print(dot1x[i]);
                    %>
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

    批量操作：
    <input type="submit" id="ss('<%=row%>').doUp" href="javascript:upInterfaces('<%=row%>')" value="开启端口">
    &nbsp;&nbsp;&nbsp;&nbsp;
    <input type="submit" id="ss('<%=row%>').doDown" href="javascript:downInterfaces('<%=row%>')" value="关闭端口">
    &nbsp;&nbsp;&nbsp;&nbsp;
    <input type="submit" id="ss('<%=row%>').doDot1x"href="javascript:dot1xs('<%=row%>')" value="端口认证">
    &nbsp;&nbsp;&nbsp;&nbsp;
    <input type="submit" id="ss('<%=row%>').undoDot1x" href="javascript:undoDot1xs('<%=row%>')" value="取消认证">
    &nbsp;&nbsp;&nbsp;&nbsp;
</form>

<jsp:include page="/includes/footer.jsp" flush="false" />
