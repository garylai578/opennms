<%--
  Created by IntelliJ IDEA.
  User: laiguanhui
  Date: 2016/5/6
  Time: 8:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="org.opennms.core.bank.SwitcherStats" %>
<%@ page import="org.opennms.core.bank.SwitcherStatsOperator" %>

<%@ page import="org.opennms.core.resource.Vault" %>
<%@ page import="org.opennms.core.utils.DBUtils" %>
<%@ page import="org.opennms.web.element.Interface" %>
<%@ page import="org.opennms.web.element.NetworkElementFactory" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.Statement" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>

<%@page language="java"
        contentType="text/html"
        session="true"
%>

<jsp:include page="/includes/header.jsp" flush="false" >
    <jsp:param name="title" value="交换机流量配置" />
    <jsp:param name="headTitle" value="交换机流量配置" />
    <jsp:param name="breadcrumb" value="<a href='report/index.jsp'>报表</a>" />
    <jsp:param name="breadcrumb" value="<a href='report/switcher.jsp'>交换机流量统计</a>" />
    <jsp:param name="breadcrumb" value="交换机流量配置" />
</jsp:include>

<%
    SwitcherStatsOperator so = new SwitcherStatsOperator();
    SwitcherStats[] sss = so.selectAll();
    List<String> selectedIPs = new ArrayList<String>();
    for(SwitcherStats ss :sss){
        selectedIPs.add(ss.getIp());
    }

    final DBUtils d = new DBUtils(getClass());
    List<Integer> nodeIds = new ArrayList<Integer>();
    List<SwitcherStats> ips = new ArrayList<SwitcherStats>();
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
    for(Integer id : nodeIds) {
        Interface[] availIntfs = NetworkElementFactory.getInstance(this.getServletConfig().getServletContext()).getActiveInterfacesOnNode(id);
        for (int i = 0; i < availIntfs.length; i++) {
            Interface intf = availIntfs[i];
            SwitcherStats ip = new SwitcherStats(intf.getIpAddress(), id+"");
            ips.add(ip);
        }
    }
    Collections.sort(ips, SwitcherStats.IPComparator);
    int len1 = ips.size() / 2 + 1;
%>

<script type="text/javascript">

    function statistics(){
        var ips="";
        for (var i = 0; i < <%=ips.size()%>; ++i) {
            var choose = document.getElementById("choose-"+i);
            if (choose.checked == true) {
                ips += document.getElementById("ips-"+i).value + "\t";
            }
        }

        document.switcher.ips.value = ips;
        document.switcher.action = "abcbank/confSwitcherStats";
        document.switcher.submit();
    }

    function cancel()
    {
        document.switcher.action="report/configSwitchers.jsp";
        document.switcher.submit();
    }
</script>

<form method="post" name="switcher">
    <input type="hidden" name="ips"/>

    <h3>交换机流量统计配置</h3>
    <div class="boxWrapper">
        <div>请选择需要统计的交换机</div>
        <div class="TwoColLeft">
            <ul class="plain">
                <%
                    for(int i = 0; i < len1 ; ++i){
                        String ip = ips.get(i).getIp();
                %>
                <li>
                    <div><input id="choose-<%=i%>" type="checkbox" value=""  <%if(selectedIPs.contains(ip)) out.print("checked"); %> /><%=ip%>
                        <input type="hidden" id="ips-<%=i%>" value="<%=ip%>"/>
                    </div>
                </li>
                <%
                    }
                %>
            </ul>
        </div>

        <div class="TwoColRight">
            <ul class="plain">
                <%
                    for(int i = len1; i < ips.size() ; ++i){
                        String ip = ips.get(i).getIp();
                %>
                <li>
                    <div><input id="choose-<%=i%>" type="checkbox" value=""   <%if(selectedIPs.contains(ip)) out.print("checked"); %> /><%=ip%>
                        <input type="hidden" id="ips-<%=i%>" value="<%=ip%>"/>
                    </div>
                </li>
                <%
                    }
                %>
            </ul>
        </div>

        <div class="spacer"><!-- --></div>

        <div>或输入需要统计的交换机IP(每行一个)</div>
        <div>
            <textarea id="input-ips" name="input-ips" rows="10" style="width:50%; overflow: auto;"></textarea>
        </div>
    </div>
    <div class="spacer"><!-- --></div>
    &nbsp;&nbsp;
    <input id="doOK" type="submit" value="确认"  href="javascript:statistics()"/>
    &nbsp;&nbsp;
    <input id="doCancel" type="button" value="取消" onclick="cancel()"/>
</form>

<jsp:include page="/includes/footer.jsp" flush="false" />