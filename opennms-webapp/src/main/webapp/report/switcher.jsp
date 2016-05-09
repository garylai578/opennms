<%--
  Created by IntelliJ IDEA.
  User: laiguanhui
  Date: 2016/5/5
  Time: 14:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="org.opennms.core.bank.SwitcherStats" %>
<%@ page import="org.opennms.core.bank.SwitcherStatsOperator" %>

<%@page language="java"
        contentType="text/html"
        session="true"
%>

<jsp:include page="/includes/header.jsp" flush="false" >
    <jsp:param name="title" value="交换机流量统计" />
    <jsp:param name="headTitle" value="交换机流量统计" />
    <jsp:param name="breadcrumb" value="<a href='report/index.jsp'>报表</a>" />
    <jsp:param name="breadcrumb" value="交换机流量统计" />
</jsp:include>

<%
    SwitcherStatsOperator operator = new SwitcherStatsOperator();
    SwitcherStats[] stats = operator.selectAll();
%>

<script type="text/javascript">

    function deleteSwitcher(id){
        document.switcher.ip.value = id;
        document.switcher.action="abcbank/delSwitcherStats";
        document.switcher.submit();
    }

    function startMonitor(){
        document.switcher.action="abcbank/startSwitcherMonitor";
        document.switcher.submit();
    }
</script>

<form method="post" name="switcher">
    <input type="hidden" name="ip"/>

    <h3>交换机流量统计(请先配置需要统计流量的交换机)</h3>

    <table width="100%" border="1" cellspacing="0" cellpadding="2" bordercolor="black">
        <tr bgcolor="#999999">
            <td width="3%"><b>操作</b></td>
            <td width="5%"><b>IP</b></td>
            <td width="5%"><b>方向</b></td>
            <%
                for(int i = 1; i <= 24; ++i){
            %>
            <td width="3%"><b><%=i%>点</b></td>
            <%
                }
            %>
        </tr>

        <%
            int row=0;
            for(SwitcherStats stat : stats){
                String ip = stat.getIp();
                String flow = stat.getFlow();
        %>
        <tr bgcolor=<%=row%2==0 ? "#ffffff" : "#cccccc"%>>
            <td width="3%" rowspan="2" align="center" style="vertical-align:middle;">
                <a id="<%= "ips("+ip+").doStop" %>" href="javascript:deleteSwitcher('<%=ip%>')" onclick="return confirm('确定要移除该交换机？')">移除</a>
            </td>

            <td width="5%" rowspan="2" align="center" style="vertical-align:middle;">
                <div id="ip-<%=row%>">
                    <%= ((ip == null || ip.equals("")) ? "&nbsp;" : ip) %>
                    <input type="hidden" name="ipaddr-<%=row%>" value="<%= ((ip == null || ip.equals("")) ? "&nbsp;" : ip) %>"/>
                </div>
            </td>

            <td>
                <b>输入</b>
            </td>

            <%
                String[] flowString = flow.split("/t");
                for(int i = 0; i < 24; ++i){
            %>
            <td width="3%">
                <div>
                    <%
                        if(flowString.length ==2) {
                            String[] inFlows = flowString[0].split(",");
                            out.print(inFlows[i]);
                        }else {
                            out.print("--");
                        }
                    %>
                </div>
            </td>
            <%
                }
            %>
        </tr>

        <tr>
            <td>
                <b>输出</b>
            </td>

        <%
            for(int i = 0; i < 24; ++i){
        %>
        <td width="3%">
            <div>
                <%
                    if(flowString.length ==2) {
                        String[] inFlows = flowString[1].split(",");
                        out.print(inFlows[i]);
                    }else {
                        out.print("--");
                    }
                %>
            </div>
        </td>
        <%

                }
                row++;
            }
        %>
    </table>

    <input id="add" type="button" value="增加交换机"  onclick="location.href='/opennms/report/configSwitchers.jsp'"/>
    <input id="add" type="button" value="启动流量监控" onclick="javascript:startMonitor();"/>

</form>

<jsp:include page="/includes/footer.jsp" flush="false" />