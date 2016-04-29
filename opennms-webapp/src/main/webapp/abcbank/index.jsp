<%@ page import="org.opennms.web.springframework.security.Authentication" %>
<%@page language="java"
        contentType="text/html"
        session="true"
%>


<jsp:include page="/includes/header.jsp" flush="false" >
    <jsp:param name="title" value="台帐" />
    <jsp:param name="headTitle" value="台帐" />
    <jsp:param name="breadcrumb" value="台帐" />
</jsp:include>

<div class="TwoColLeft">
    <h3>台帐管理</h3>
    <div class="boxWrapper">
        <ul class="plain">
            <%
                if(request.isUserInRole(Authentication.ROLE_ADMIN))
                {
            %>
            <li><a href="abcbank/ipsegment.jsp">IP地址段分配</a></li>
            <%
                }
            %>
            <li><a href="abcbank/ipaddress.jsp">IP地址台帐</a></li>
            <li><a href="abcbank/webline.jsp">线路台帐</a></li>
        </ul>
    </div>

</div>

<div class="TwoColRight">
    <h3>说明</h3>
    <div class="boxWrapper">
        <p><b>IP地址段分配</b>:对各网段新申请IP地址请求自动划分IP段和生成掩码，对停用IP段进行回收再分配，并提供报表输出功能。
        </p>

        <p><b>IP地址台帐</b>:管理支行及分行IP地址信息，具备录入、删除、查询和报表输出功能。
        </p>

        <p><b>线路台帐</b>:管理分行租用的各种线路，具备录入、删除、查询和报表输出功能。
        </p>

    </div>
</div>
<hr />
<jsp:include page="/includes/footer.jsp" flush="false"/>
