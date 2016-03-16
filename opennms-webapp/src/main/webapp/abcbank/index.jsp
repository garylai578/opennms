<%@ page import="org.opennms.core.bank.IPSegment" %>
<%@ page import="org.opennms.core.bank.IPSegmentOperater" %>
<%--
  Created by IntelliJ IDEA.
  User: laiguanhui
  Date: 2016/2/16
  Time: 14:56
  To change this template use File | Settings | File Templates.
--%>


<%@page language="java"
        contentType="text/html"
        session="true"
%>

<jsp:include page="/includes/header.jsp" flush="false" >
    <jsp:param name="title" value="IP地址段分配" />
    <jsp:param name="headTitle" value="IP地址段分配" />
    <jsp:param name="breadcrumb" value="<a href='drcbank/index.jsp'>IP管理</a>" />
    <jsp:param name="breadcrumb" value="IP地址段分配" />
</jsp:include>

<%
    IPSegmentOperater op = new IPSegmentOperater();
    IPSegment[] ips = op.selectAll();
    for(IPSegment ip : ips) {
        String gateway = ip.getGateway();
        String mask = ip.getMask();
        String startIP = ip.getStartIP();
        String endIP = ip.getEndIP();
        String name = ip.getBankname();
        String type = ip.getBanktype();
        String time = ip.getCreateTime();
        String state = ip.getState();
        String comment = ip.getComment();
        String ipId = ip.getId();
        out.print(ipId);
    }
%>

<script type="text/javascript" >

    function addIPSegment()
    {
        document.allIPSegments.action="abcbank/newIPSegment.jsp";
        document.allIPSegments.submit();
    }

    function stopIPSegment(id)
    {

    }

    function startIPSegment(id)
    {

    }

    function modifyIPSegment(id)
    {

    }

</script>


<form method="post" name="allIPSegments">
    <input type="hidden" name="redirect"/>
    <input type="hidden" name="ipSegID"/>
    <input type="hidden" name="newID"/>
    <input type="hidden" name="password"/>

    <h3>IP地址段分配</h3>

    <a id="doNewIPSegment" href="javascript:addIPSegment()"><img src="images/add1.gif" alt="新增IP段" border="0"></a>
    <a href="javascript:0">新增IP段</a>

    <br/>
    <br/>

    <table width="100%" border="1" cellspacing="0" cellpadding="2" bordercolor="black">

        <tr bgcolor="#999999">
            <td width="5%"><b>操作</b></td>
            <!--td width="5%"><b>修改</b></td-->
            <td width="10%"><b>网关</b></td>
            <td width="10%"><b>掩码</b></td>
            <td width="20%"><b>IP段</b></td>
            <td width="10%"><b>网点名称</b></td>
            <td width="5%"><b>网点类型</b></td>
            <td width="10%"><b>启用日期</b></td>
            <td width="5%"><b>使用情况</b></td>
            <!--
            <td width="10%"><b>Num Service</b></td>
            <td width="10%"><b>Num Pin</b></td>
            <td width="15%"><b>Text Service</b></td>
            <td width="15%"><b>Text Pin</b></td>
            -->
        </tr>
        <%
                int row=1;
                String gateway = "172.16.0.1";
                String mask ="255.255.255.0";
                String startIP = "172.16.0.1";
                String endIP = "172.16.0.255";
                String name = "东城支行";
                String type = "网点";
                String time = "2016-2-22";
                String state = "在用";
                String comment ="无";
                int id = 1;
        %>
        <tr bgcolor=<%=row%2==0 ? "#ffffff" : "#cccccc"%>>
            <td width="7%" rowspan="2" align="center" style="vertical-align:middle;">
                <a id="<%= "ips("+id+").doStop" %>" href="javascript:stopIPSegment('<%=id%>')" onclick="return confirm('你确定要停用IP段： <%=startIP + "-" + endIP%> ?')">停用</a>
                &nbsp;&nbsp;
                <a id="<%= "ips("+id+").doStart" %>" href="javascript:startIPSegment('<%=id%>')" onclick="return confirm('你确定启要IP段： <%=startIP + "-" + endIP%> ?')">启用</a>
                &nbsp;&nbsp;
                <a id="<%= "ips("+id+").doModify" %>" href="javascript:modifyIPSegment('<%=id%>')">修改</a>
            </td>

            <td width="10%">
                <div id="<%= "gateway" %>">
                    <%= ((gateway == null || gateway.equals("")) ? "&nbsp;" : gateway) %>
                </div>
            </td>

            <td width="10%">
                <div id="<%= "mask" %>">
                    <%= ((mask == null || mask.equals("")) ? "&nbsp;" : mask) %>
                </div>
            </td>

            <td width="20%">
                <div id="<%= "ipsegment" %>">
                    <%= ((startIP == null || startIP.equals("") || endIP == null || endIP.equals("")) ? "&nbsp;" : startIP + "-" + endIP) %>
                </div>
            </td>

            <td width="10%">
                <div id="<%= "bankname" %>">
                    <%= ((name == null || name.equals("")) ? "&nbsp;" : name) %>
                </div>
            </td>

            <td width="5%">
                <div id="<%= "banktype" %>">
                    <%= ((type == null || type.equals("")) ? "&nbsp;" : type) %>
                </div>
            </td>

            <td width="10%">
                <div id="<%= "createdate" %>">
                    <%= ((time == null || time.equals("")) ? "&nbsp;" : time) %>
                </div>
            </td>

            <td width="5%">
                <div id="<%= "state" %>">
                    <%= ((state == null || state.equals("")) ? "&nbsp;" : state) %>
                </div>
            </td>
        </tr>

        <tr bgcolor=<%=row%2==0 ? "#ffffff" : "#cccccc"%>>
            <td colspan="7">
                <div id="<%= "comment" %>">
                    <%= ((comment == null || comment.equals("")) ? "无备注；" : comment) %>
                </div>
            </td>
        </tr>



    </table>

</form>

<jsp:include page="/includes/footer.jsp" flush="false" />
