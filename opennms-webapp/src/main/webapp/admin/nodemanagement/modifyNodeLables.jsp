<%@ page import="org.opennms.core.resource.Vault" %>
<%@ page import="org.opennms.core.utils.DBUtils" %>
<%@ page import="org.opennms.netmgt.utils.NodeLabel" %>
<%@ page import="org.opennms.web.element.Interface" %>
<%@ page import="org.opennms.web.element.NetworkElementFactory" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.Statement" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: laiguanhui
  Date: 2016/6/8
  Time: 10:12
  To change this template use File | Settings | File Templates.
--%>
<%@page language="java"
        contentType="text/html"
        session="true"
%>

<jsp:include page="/includes/header.jsp" flush="false" >
    <jsp:param name="title" value="修改节点名称" />
    <jsp:param name="headTitle" value="修改节点名称" />
    <jsp:param name="breadcrumb" value="<a href='admin/index.jsp'>管理</a>" />
    <jsp:param name="breadcrumb" value="修改节点名称" />
</jsp:include>

<%
    final DBUtils d = new DBUtils(getClass());
    List<Integer> nodeIds = new ArrayList<Integer>();
    try {
        Connection conn = Vault.getDbConnection();
        d.watch(conn);
        Statement stmt = conn.createStatement();
        d.watch(stmt);
        ResultSet rs = stmt.executeQuery("SELECT nodeid FROM assets");
        d.watch(rs);
        while(rs.next())
            nodeIds.add(rs.getInt("nodeid"));
    } finally {
        d.cleanUp();
    }

%>

<script type="text/javascript">

   function modifyNodeLables(row){
       var nodeids="";
       var nodeLables="";
       for (var i = 0; i < row; ++i) {
           var newLable = document.getElementById("newLable-"+i).value;
           if (newLable != "") {
               nodeLables += newLable + "\t";
               nodeids += document.getElementById("nodeid-"+i).value + "\t";
           }
       }
       doModify(nodeids, nodeLables);
    }

   function modifyNodeLable(nodeid, row){
       var nodeLable = document.getElementById("newLable-"+row).value;
       doModify(nodeid, nodeLable);
   }

   function doModify(nodeids, nodeLables){
       document.nodes.nodeids.value = nodeids;
       document.nodes.nodeLables.value = nodeLables;
       document.nodes.action="admin/nodeLabelsChange";
       document.nodes.submit();
   }

    function cancel()
    {
        document.nodes.action="admin/index.jsp";
        document.nodes.submit();
    }
</script>

<form method="post" name="nodes">
    <input type="hidden" name="nodeids"/>
    <input type="hidden" name="nodeLables"/>

    <table width="100%" border="1" cellspacing="0" cellpadding="2" bordercolor="black">

        <tr bgcolor="#999999">
            <td width="10%"><b>节点IP</b></td>
            <td width="10%"><b>当前名称</b></td>
            <td width="10%"><b>新名称</b></td>
            <td width="3%"><b>操作</b></td>
        </tr>

        <%
            int row;
            for(row = 0; row < nodeIds.size(); ++row) {
                Integer id = nodeIds.get(row);
                Interface[] availIntfs = NetworkElementFactory.getInstance(this.getServletConfig().getServletContext()).getActiveInterfacesOnNode(id);
                String ip = "";
                NodeLabel currentLabel = NodeLabel.retrieveLabel(id);
                for(Interface inter : availIntfs) {
                    ip += inter.getIpAddress() + ", ";
                }
                ip = ip.substring(0, ip.length()-2);
        %>
        <tr>
            <td width="10%">
                <%=ip%>
            </td>

            <td width="10%">
                <%=currentLabel.getLabel()%>
            </td>

            <td width="10%">
                <input id="newLable-<%=row%>" size="30" name="newLable-<%=row%>" value=""/>
            </td>

            <td width="3%">
                <a id="<%= "node("+id+").modify" %>" href="javascript:modifyNodeLable('<%=id%>', '<%=row%>')">修改</a>
                <input type="hidden", id="nodeid-<%=row%>" name="nodeid-<%=row%>" value="<%=id%>">
            </td>
        </tr>
        <%
            }
        %>
    </table>
    &nbsp;&nbsp;
    <input id="doOK" type="button" value="确认" onclick="javascript:modifyNodeLables(<%=row%>)"/>
    &nbsp;&nbsp;
    <input id="doCancel" type="button" value="取消" onclick="cancel()"/>

</form>

<jsp:include page="/includes/footer.jsp" flush="false" />
