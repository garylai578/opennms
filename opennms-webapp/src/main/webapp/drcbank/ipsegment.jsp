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

<%@page import="org.opennms.core.bank.IPSegmentOperater"%>
<%@page import="org.opennms.core.bank.IPSegment" %>

<%
    IPSegmentOperater op = new IPSegmentOperater();
%>

<jsp:include page="/includes/header.jsp" flush="false" >
    <jsp:param name="title" value="IP地址段分配" />
    <jsp:param name="headTitle" value="IP地址段分配" />
    <jsp:param name="breadcrumb" value="<a href='drcbank/index.jsp'>IP管理</a>" />
    <jsp:param name="breadcrumb" value="IP地址段分配" />
</jsp:include>

<script type="text/javascript" >

    function addNewUser()
    {
        document.allUsers.action="admin/userGroupView/users/newUser.jsp?action=new";
        document.allUsers.submit();

    }

    function detailUser(userID)
    {
        document.allUsers.action="admin/userGroupView/users/userDetail.jsp?userID=" + userID;
        document.allUsers.submit();
    }

    function deleteUser(userID)
    {
        document.allUsers.action="admin/userGroupView/users/deleteUser";
        document.allUsers.userID.value=userID;
        document.allUsers.submit();
    }

    function modifyUser(userID)
    {
        document.allUsers.action="admin/userGroupView/users/modifyUser";
        document.allUsers.userID.value=userID;
        document.allUsers.submit();
    }

    function renameUser(userID)
    {
        document.allUsers.userID.value=userID;
        var newID = prompt("输入新用户名。", userID);

        if (newID != null && newID != "")
        {
            document.allUsers.newID.value = newID;
            document.allUsers.action="admin/userGroupView/users/renameUser";
            document.allUsers.submit();
        }
    }

</script>


<form method="post" name="allUsers">
    <input type="hidden" name="redirect"/>
    <input type="hidden" name="userID"/>
    <input type="hidden" name="newID"/>
    <input type="hidden" name="password"/>

    <h3>IP地址段分配</h3>

    <a id="doNewUser" href="javascript:0"><img src="images/add1.gif" alt="新增IP段" border="0"></a>
    <a href="javascript:0">新增IP段</a>

    <br/>
    <br/>

    <table width="100%" border="1" cellspacing="0" cellpadding="2" bordercolor="black">

        <tr bgcolor="#999999">
            <td width="5%"><b>停用</b></td>
            <td width="5%"><b>修改</b></td>
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
            IPSegment[] ips = op.selectAll();
            int row = 0;
            for(IPSegment ip : ips){
                String gateway = ip.getGateway();
                String mask = ip.getMask();
                String startIP = ip.getStartIP();
                String endIP = ip.getEndIP();
                String name = ip.getBankname();
                String type = ip.getBanktype();
                String time = ip.getCreateTime();
                String state = ip.getState();
                String comment = ip.getComment();

        %>
        <tr bgcolor=<%=row%2==0 ? "#ffffff" : "#cccccc"%>>
            <td width="5%" rowspan="2" align="center">
            </td>

            <td width="5%" rowspan="2" align="center">
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
            <td colspan="5">
                <div id="<%= "comment" %>">
                    <%= ((comment == null || comment.equals("")) ? "无备注；" : comment) %>
                </div>
            </td>
        </tr>
        <%
                row++;
            }
        %>

         <%--
            Iterator i = users.keySet().iterator();
            int row = 0;
            while(i.hasNext())
            {
                User curUser = (User)users.get(i.next());
                String userid = curUser.getUserId();
                String email = userFactory.getEmail(userid);
                String pagerEmail = userFactory.getPagerEmail(userid);
                String xmppAddress = userFactory.getXMPPAddress(userid);
                String numericService = userFactory.getNumericPage(userid);
                String textService = userFactory.getTextPage(userid);
                String numericPin = userFactory.getNumericPin(userid);
                String textPin = userFactory.getTextPin(userid);
        %>
        <!--
        <tr bgcolor=<%=row%2==0 ? "#ffffff" : "#cccccc"%>>
            <% if (!curUser.getUserId().equals("admin")) { %>
            <td width="5%" rowspan="2" align="center">
                <a id="<%= "users("+curUser.getUserId()+").doDelete" %>" href="javascript:deleteUser('<%=curUser.getUserId()%>')" onclick="return confirm('你确定要删除用户 <%=curUser.getUserId()%>?')"><img src="images/trash.gif" alt="<%="删除 " + curUser.getUserId()%>"></a>

            </td>
            <% } else { %>
            <td width="5%" rowspan="2" align="center">
                <img id="<%= "users("+curUser.getUserId()+").doDelete" %>" src="images/trash.gif" alt="不能删除管理员用户">
            </td>
            <% } %>
            <td width="5%" rowspan="2" align="center">
                <a id="<%= "users("+curUser.getUserId()+").doModify" %>" href="javascript:modifyUser('<%=curUser.getUserId()%>')"><img src="images/modify.gif"></a>
            </td>
            <td width="5%" rowspan="2" align="center">
                <% if ( !curUser.getUserId().equals("admin")) { %>
                <input id="<%= "users("+curUser.getUserId()+").doRename" %>" type="button" name="rename" value="重命名" onclick="renameUser('<%=curUser.getUserId()%>')">
                <% } else { %>
                <input id="<%= "users("+curUser.getUserId()+").doRename" %>" type="button" name="rename" value="重命名" onclick="alert('抱歉，管理员用户不能更名。')">
                <% } %>
            </td>
            <td width="5%">
                <a id="<%= "users("+curUser.getUserId()+").doDetails" %>" href="javascript:detailUser('<%=curUser.getUserId()%>')"><%=curUser.getUserId()%></a>
            </td>
            <td width="15%">
                <div id="<%= "users("+curUser.getUserId()+").fullName" %>">
                    <% if(curUser.getFullName() != null){ %>
                    <%= (curUser.getFullName().equals("") ? "&nbsp;" : curUser.getFullName()) %>
                    <% } %>
                </div>
            </td>
            <td width="15%">
                <div id="<%= "users("+curUser.getUserId()+").email" %>">
                    <%= ((email == null || email.equals("")) ? "&nbsp;" : email) %>
                </div>
            </td>
            <td width="15%">
                <div id="<%= "users("+curUser.getUserId()+").pagerEmail" %>">
                    <%= ((pagerEmail == null || pagerEmail.equals("")) ? "&nbsp;" : pagerEmail) %>
                </div>
            </td>
            <td width="15">
                <div id="<%= "users("+curUser.getUserId()+").xmppAddress" %>">
                    <%= ((xmppAddress == null || xmppAddress.equals("")) ? "&nbsp;" : xmppAddress) %>
                </div>
            </td>

          <td width="10%">
            <div id="<%= "users("+curUser.getUserId()+").numericService" %>">
            <%= ((numericService == null || numericService.equals("")) ? "&nbsp;" : numericService) %>
            </div>
          </td>
          <td width="10%">
            <div id="<%= "users("+curUser.getUserId()+").numericPin" %>">
            <%= ((numericPin == null || numericPin.equals("")) ? "&nbsp;" : numericPin) %>
            </div>
          </td>
          <td width="15%">
           <div id="<%= "users("+curUser.getUserId()+").textService" %>">
            <%= ((textService == null || textService.equals("")) ? "&nbsp;" : textService) %>
            </div>
          </td>
          <td width="15%">
           <div id="<%= "users("+curUser.getUserId()+").textPin" %>">
            <%= ((textPin == null || textPin.equals("")) ? "&nbsp;" : textPin) %>
           </div>
          </td>

        </tr>
        <tr bgcolor=<%=row%2==0 ? "#ffffff" : "#cccccc"%>>
            <td colspan="5">
                <div id="<%= "users("+curUser.getUserId()+").userComments" %>">
                    <% if(curUser.getUserComments() != null){ %>
                    <%= (curUser.getUserComments().equals("") ? "No Comments" : curUser.getUserComments()) %>

                    <% } %>
                </div>
            </td>
        </tr>
        -->
        --%>

    </table>

</form>

<jsp:include page="/includes/footer.jsp" flush="false" />
