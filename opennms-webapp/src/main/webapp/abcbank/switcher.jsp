<%--
  Created by IntelliJ IDEA.
  User: laiguanhui
  Date: 2016/2/16
  Time: 14:56
  To change this template use File | Settings | File Templates.
--%>

<%@page language="java"
        contentType="text/html;charset=UTF-8"
        session="true"
%>

<%@page import="org.opennms.core.bank.Switcher" %>
<%@ page import="org.opennms.core.bank.SwitcherOperator" %>
<%@ page import="org.opennms.core.resource.Vault" %>
<%@ page import="org.opennms.core.utils.DBUtils" %>
<%@ page import="org.opennms.web.element.Interface" %>
<%@ page import="org.opennms.web.element.NetworkElementFactory" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.Statement" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<jsp:include page="/includes/header.jsp" flush="false">
    <jsp:param name="title" value="交换机配置管理" />
    <jsp:param name="headTitle" value="交换机配置管理" />
    <jsp:param name="breadcrumb" value="<a href='admin/index.jsp'>管理</a>" />
    <jsp:param name="breadcrumb" value="交换机配置管理" />
</jsp:include>

<%@include file="/abcbank/getVars.jsp"%>

<%!
    int pageCount;
    int curPage = 1;
%>

<%
    final DBUtils d = new DBUtils(getClass());
    List<Integer> nodeIds = new ArrayList<Integer>();
    Map<String, Integer> ipNodeidMap = new HashMap<String, Integer>();
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
        Interface[] allInterfaces= NetworkElementFactory.getInstance(this.getServletConfig().getServletContext()).getActiveInterfacesOnNode(id);
        for (int i = 0; i < allInterfaces.length; i++) {
            Interface intf = allInterfaces[i];
            ipNodeidMap.put(intf.getIpAddress(), id);
        }
    }

    String swip = request.getParameter("switcherIP");
    String groupReturn, nameReturn, brandReturn, ipReturn, commentReturn, update;
    if(request.getAttribute("group") != null )
        groupReturn = (String)request.getAttribute("group");
    else {
        groupReturn = request.getParameter("group");
        if(groupReturn == null)
            groupReturn = "";
    }

    if(request.getAttribute("name") != null)
        nameReturn = (String)request.getAttribute("name");
    else {
        nameReturn = request.getParameter("name");
        if(nameReturn == null)
            nameReturn = "";
    }

    if(request.getAttribute("brand") != null)
        brandReturn = (String)request.getAttribute("brand");
    else {
        brandReturn = request.getParameter("brand");
        if(brandReturn == null)
            brandReturn = "";
    }

    if(request.getAttribute("ip") != null)
        ipReturn = (String)request.getAttribute("ip");
    else {
        ipReturn = request.getParameter("ip");
        if(ipReturn == null)
            ipReturn = "";
    }

    if(request.getAttribute("comment") != null)
        commentReturn = (String)request.getAttribute("comment");
    else {
        commentReturn = request.getParameter("comment");
        if(commentReturn == null)
            commentReturn = "";
    }

    if(request.getAttribute("update") != null)
        update = (String)request.getAttribute("update");
    else
        update = request.getParameter("update");

    Map<String, String> colAndValue = new HashMap<String, String>();
    if(groupReturn != null && !"".equals(groupReturn) && !"null".equals(groupReturn))
        colAndValue.put("groups", groupReturn);
    if(nameReturn != null && !"".equals(nameReturn) && !"null".equals(nameReturn))
        colAndValue.put("name", nameReturn);
    if(brandReturn != null && !"".equals(brandReturn) && !"null".equals(brandReturn))
        colAndValue.put("brand", brandReturn);
    if(ipReturn != null && !"".equals(ipReturn) && !"null".equals(ipReturn))
        colAndValue.put("host", ipReturn);
    if(commentReturn != null && !"".equals(commentReturn) && !"null".equals(commentReturn))
        colAndValue.put("comment", commentReturn);

    SwitcherOperator op = new SwitcherOperator();
    Switcher[] ss = (Switcher[])session.getAttribute("switchers");
    if(ss == null || (update != null && update.equals("true")))
        ss = op.andSelect(colAndValue);
    session.setAttribute("switchers", ss);

    int size = ss.length;
    pageCount = (size%PAGESIZE==0)?(size/PAGESIZE):(size/PAGESIZE+1);
    String tmp = request.getParameter("curPage");
    if(tmp==null || "".equals(tmp) || "null".equals(tmp)){
        tmp="1";
    }
    curPage = Integer.parseInt(tmp);
    if(pageCount > 0 && curPage >= pageCount)
        curPage = pageCount;
    int swAtArray = (curPage - 1) * PAGESIZE;
%>

<script type="text/javascript" >

    function addSwitcher()
    {
        document.allSwitchers.action="abcbank/newSwitcher.jsp";
        document.allSwitchers.submit();
    }

    function deleteSwitcher(id, host)
    {
        document.allSwitchers.action="abcbank/deleteSwitcher";
        document.allSwitchers.switcherId.value=id;
        document.allSwitchers.switchHost.value=host;
        document.allSwitchers.submit();
    }

    function recoverySwitcher(rowID)
    {

        var value = document.getElementById("recovery-"+rowID).getAttribute("value");

        if(value==0 || value=="")
                alert("备份命令为空！");
        else {
            document.allSwitchers.action = "abcbank/recoverySwitcher";
            document.allSwitchers.rowID.value = rowID;
            document.allSwitchers.submit();
        }
    }

    function backupSwitcher(rowID)
    {
        document.allSwitchers.action="abcbank/backupSwitcher";
        document.allSwitchers.rowID.value=rowID;
        document.allSwitchers.submit();
    }

    function backupSwitcherCycle(rowID)
    {
        document.allSwitchers.action="abcbank/backupSwitcher";
        document.allSwitchers.rowID.value=rowID;
        document.allSwitchers.isCycle.value=1;
        document.allSwitchers.submit();
    }

    function managePorts(id){
        document.allSwitchers.action="abcbank/manageSwitcherPorts.jsp?id="+id;
        document.allSwitchers.submit();
    }

    function bundingIP(id){
        document.allSwitchers.action="abcbank/bundingIP.jsp?id="+id;
        document.allSwitchers.submit();
    }

    function batchOperator(rows){
        var op = document.allSwitchers.batchComm.value;
        if(op == null || op == ""){
            alert("请输入批量操作命令");
            return;
        }
        document.allSwitchers.batchComm.value = op.replace(/\r\n/g, "\n")

        var sw="";
        for (var i = 0; i < rows; ++i) {
            var choose = document.getElementById("choose-"+i);
            if (choose.checked == true)
                sw += i + "\t";
        }
        if(sw==""){
            alert("请选择需要操作的交换机");
            return;
        }

        document.allSwitchers.sws.value = sw;
        document.allSwitchers.action="abcbank/batchOperateSwitchers";
        document.allSwitchers.submit();
    }

    time = new Array("1点","2点","3点","4点","5点","6点","7点","8点","9点","10点","11点","12点","13点","14点","15点",
            "16点","17点","18点","19点","20点","21点","22点","23点","24点");
    week = new Array("周一","周二","周三","周四","周五","周六","周日");
    day = new Array("1日","2日","3日","4日","5日","6日","7日","8日","9日","10日","11日","12日","13日","14日","15日",
            "16日","17日","18日","19日","20日","21日","22日","23日","24日","25日","26日","27日","28日","29日","30日","31日");

    function changelocation(backup,row)
    {
        var backupCycle=backup;

        if(backupCycle=='每天'){
            var tmp = document.getElementById("cycle2_"+row);
            while(tmp.hasChildNodes()){
                tmp.removeChild(tmp.firstChild);
            }
            document.getElementById("cycle3_"+row).setAttribute("style", "display:none");
            for(var i = 0; i < time.length; ++i)
                document.getElementById("cycle2_"+row).appendChild(new Option(time[i],time[i]));
        }

        if(backupCycle=='每周'){
            var tmp = document.getElementById("cycle2_"+row);
            while(tmp.hasChildNodes()){
                tmp.removeChild(tmp.firstChild);
            }
            for(var i = 0; i < week.length; ++i)
                document.getElementById("cycle2_"+row).appendChild(new Option(week[i], week[i]));

            document.getElementById("cycle3_"+row).setAttribute("style","display");
            tmp = document.getElementById("cycle3_"+row);
            while(tmp.hasChildNodes()){
                tmp.removeChild(tmp.firstChild);
            }
            for(var i = 0; i < time.length; ++i)
                document.getElementById("cycle3_"+row).appendChild(new Option(time[i], time[i]));
        }

        if(backupCycle=='每月'){
            var tmp = document.getElementById("cycle2_"+row);
            while(tmp.hasChildNodes()){
                tmp.removeChild(tmp.firstChild);
            }
            for(var i = 0; i < day.length; ++i)
                document.getElementById("cycle2_"+row).appendChild(new Option(day[i], day[i]));

            document.getElementById("cycle3_"+row).setAttribute("style", "display");
            tmp = document.getElementById("cycle3_"+row);
            while(tmp.hasChildNodes()){
                tmp.removeChild(tmp.firstChild);
            }
            for(var i = 0; i < time.length; ++i)
                document.getElementById("cycle3_"+row).appendChild(new Option(time[i], time[i]));
        }
    }

    function modifySwitcher(id, row){
        document.allSwitchers.action="abcbank/modifySwitcher.jsp?id="+id+"&row="+row;
        document.allSwitchers.submit();
    }

    function searchSwitcher()
    {
        document.allSwitchers.action="abcbank/searchSwitcher";
        document.allSwitchers.submit();
    }

</script>

<form method="post" name="allSwitchers">
    <input type="hidden" name="rowID"/>
    <input type="hidden" name="isCycle" value="0"/>
    <input type="hidden" name="switcherId" />
    <input type="hidden" name="switchHost"/>
    <input type="hidden" name="sws"/>

    <h3>交换机配置管理</h3>

    <table>
        <td>
            <a id="doNewIPSegment" href="javascript:addSwitcher()"><img src="images/add1.gif" alt="新增交换机" border="0"></a>
            <a href="javascript:addSwitcher()">新增交换机</a>
        </td>

        <td align="left">&nbsp;&nbsp;
            <strong>名称:</strong><input id="name" name="name" size="12" value="<%=(nameReturn == null ? "" : nameReturn)%>">&nbsp;&nbsp;
            <strong>分组：</strong><select id="group" name="grpup">
                <option value="" selected="">请选择</option>
                <%
                    for(int i = 0; i < switcherGroups.length; ++i){
                %>
                <option value="<%=switcherGroups[i]%>" <%=((groupReturn!=null && groupReturn.equals(switcherGroups[i])) ? "selected" : "")%>><%=switcherGroups[i]%></option>
                <%
                    }
                %>
            </select>&nbsp;&nbsp;
            <strong>品牌：</strong><select id="brand" name="brand">
                <option value="" selected="">请选择</option>
                <%
                    for(int i = 0; i < switcherBrands.length; ++i){
                %>
                <option value="<%=switcherBrands[i]%>" <%=((brandReturn!=null && brandReturn.equals(switcherBrands[i])) ? "selected" : "")%>><%=switcherBrands[i]%></option>
                <%
                    }
                %>
            </select>&nbsp;&nbsp;
            <strong>IP:</strong><input id="ip" name="ip" size="12" value="<%=(ipReturn == null) ? "" : ipReturn%>">&nbsp;&nbsp;
            <strong>备注:</strong><input id="comment" name="comment" size="12" value="<%=(commentReturn == null) ? "" : commentReturn%>">&nbsp;&nbsp;
            <a id="doSearch" href="javascript:searchSwitcher()"><img src="images/search.png" alt="搜索" border="0"></a>
            <a id="search" href="javascript:searchSwitcher()">搜索</a>
        </td>

        <td>
            <div><a id="log" href="abcbank/switcher.log">查看日志</a></div>
        </td>
    </table>

    <div style="overflow: auto; width: 100%;">
    <table border="1" cellspacing="0" cellpadding="2" bordercolor="black" class="tab_css_1">

        <tr class="header1">
            <td style="width: 30px"><b>选择</b></td>
            <td><b>名称</b></td>
            <td><b>分组</b></td>
            <td><b>品牌</b></td>
            <td style="width: 100px"><b>IP</b></td>
            <td><b>用户名</b></td>
            <td style="width: 150px"><b>备份命令</b></td>
            <td style="width: 150px"><b>恢复命令</b></td>
            <td class="iptd"><b>广域网地址/掩码</b></td>
            <td class="iptd"><b>Lookback地址/掩码</b></td>
            <td class="iptd"><b>Vlan 150地址1/掩码</b></td>
            <td class="iptd"><b>Vlan 150地址2/掩码</b></td>
            <td class="iptd"><b>Vlan 160地址1/掩码</b></td>
            <td class="iptd"><b>Vlan 160地址2/掩码</b></td>
            <td class="iptd"><b>Vlan 170地址1/掩码</b></td>
            <td class="iptd"><b>Vlan 170地址2/掩码</b></td>
            <td><b>ospf进程名</b></td>
            <td><b>area号</b></td>
            <td class="iptd"><b>备注</b></td>
        </tr>
        <%
            int row = 0;
            for(int j = swAtArray; j < ss.length && j < swAtArray + PAGESIZE; ++j){
                Switcher sw = ss[j];
                String id = sw.getId();
                String name = sw.getName();
                String sgroup = sw.getGroup();
                String host = sw.getHost();
                String suser = sw.getUser();
                String password = sw.getPassword();
                String brand = sw.getBrand();
                String backup = sw.getBackup();
                String recovery = sw.getRecovery();
                String comment = sw.getComment();
                String wan_ip = sw.getWan_ip();
                String lookback = sw.getLookback_ip();
                String vlan150_ip1 = sw.getVlan150_ip1();
                String vlan150_ip2 = sw.getVlan150_ip2();
                String vlan160_ip1 = sw.getVlan160_ip1();
                String vlan160_ip2 = sw.getVlan160_ip2();
                String vlan170_ip1 = sw.getVlan170_ip1();
                String vlan170_ip2 = sw.getVlan170_ip2();
                String ospf = sw.getOspf();
                String area = sw.getArea();
                session.setAttribute("host-"+id, host);
                session.setAttribute("user-"+id, suser);
                session.setAttribute("password-"+id, password);
        %>
        <tr <%if(swip != null && host.equals(swip)) out.print("class=\"selected\"");%>>
            <td rowspan="2">
                <div>
                    <input id="choose-<%=row%>" type="checkbox" value="" />
                </div>
            </td>

            <td rowspan="2">
                <div>
                    <%= ((name == null || name.equals("")) ? "&nbsp;" : name) %>
                    <input type="hidden" name="name-<%=row%>" id="name-<%=row%>" value="<%=name%>"/>
                </div>
            </td>

            <td>
                <div>
                    <%= ((sgroup == null || sgroup.equals("")) ? "&nbsp;" : sgroup) %>
                    <input type="hidden"  name="group-<%=row%>" id="group-<%=row%>" value="<%=sgroup%>"/>
                </div>
            </td>

            <td>
                <div id="brand-<%=row%>">
                    <%= ((brand == null || brand.equals("")) ? "&nbsp;" : brand) %>
                    <input type="hidden" name="brand-<%=row%>" value="<%= brand %>"/>
                </div>
            </td>

            <td>
                <div id="host-<%=row%>">
                    <%
                        if(ipNodeidMap.containsKey(host)){
                          out.print("<a href=\"/opennms/element/node.jsp?node="+ ipNodeidMap.get(host) +"\">" + host + "</a>");
                        }
                        else {
                            out.print((host == null || host.equals("")) ? "&nbsp;" : host);
                        }
                    %>
                    <input type="hidden" name="host-<%=row%>" value="<%=host%>"/>
                </div>
            </td>

            <td>
                <div id="user-<%=row%>">
                    <%= ((suser == null || suser.equals("")) ? "&nbsp;" : suser) %>
                    <input type="hidden" name="user-<%=row%>" value="<%=suser%>"/>
                </div>
            </td>

            <td>
                <div id="backup-<%=row%>">
                    <%= ((backup == null || backup.equals("")) ? "&nbsp;" : backup) %>
                    <input type="hidden" name="backup-<%=row%>" value="<%=backup%>"/>
                </div>
            </td>

            <td>
                <div>
                    <%= ((recovery == null || recovery.equals("")) ? "&nbsp;" : recovery) %>
                    <input type="hidden" name="recovery-<%=row%>" id="recovery-<%=row%>" value="<%=recovery%>"/>
                </div>
            </td>

            <td>
                <div id="wan_ip-<%=row%>">
                    <%= ((wan_ip == null || wan_ip.equals("")) ? "&nbsp;" : wan_ip) %>
                    <input type="hidden" name="wan_ip-<%=row%>" value="<%=wan_ip%>"/>
                </div>
            </td>
            <td>
                <div id="lookback-<%=row%>">
                    <%= ((lookback == null || lookback.equals("")) ? "&nbsp;" : lookback) %>
                    <input type="hidden" name="lookback-<%=row%>" value="<%=lookback%>"/>
                </div>
            </td>
            <td>
                <div id="vlan150_ip1-<%=row%>">
                    <%= ((vlan150_ip1 == null || vlan150_ip1.equals("")) ? "&nbsp;" : vlan150_ip1) %>
                    <input type="hidden" name="vlan150_ip1-<%=row%>" value="<%=vlan150_ip1%>"/>
                </div>
            </td>
            <td>
                <div id="vlan150_ip2-<%=row%>">
                    <%= ((vlan150_ip2 == null || vlan150_ip2.equals("")) ? "&nbsp;" : vlan150_ip2) %>
                    <input type="hidden" name="vlan150_ip2-<%=row%>" value="<%=vlan150_ip2%>"/>
                </div>
            </td>
            <td>
                <div id="vlan160_ip1-<%=row%>">
                    <%= ((vlan160_ip1 == null || vlan160_ip1.equals("")) ? "&nbsp;" : vlan160_ip1) %>
                    <input type="hidden" name="vlan160_ip1-<%=row%>" value="<%=vlan160_ip1%>"/>
                </div>
            </td>
            <td>
                <div id="-<%=row%>">
                    <%= ((vlan160_ip2 == null || vlan160_ip2.equals("")) ? "&nbsp;" : vlan160_ip2) %>
                    <input type="hidden" name="vlan160_ip2-<%=row%>" value="<%=vlan160_ip2%>"/>
                </div>
            </td>
            <td>
                <div id="vlan170_ip1-<%=row%>">
                    <%= ((vlan170_ip1 == null || vlan170_ip1.equals("")) ? "&nbsp;" : vlan170_ip1) %>
                    <input type="hidden" name="vlan170_ip1-<%=row%>" value="<%=vlan170_ip1%>"/>
                </div>
            </td>
            <td>
                <div id="vlan170_ip2-<%=row%>">
                    <%= ((vlan170_ip2 == null || vlan170_ip2.equals("")) ? "&nbsp;" : vlan170_ip2) %>
                    <input type="hidden" name="vlan170_ip2-<%=row%>" value="<%=vlan170_ip2%>"/>
                </div>
            </td>
            <td>
                <div id="ospf-<%=row%>">
                    <%= ((ospf == null || ospf.equals("")) ? "&nbsp;" : ospf) %>
                    <input type="hidden" name="ospf-<%=row%>" value="<%=ospf%>"/>
                </div>
            </td>
            <td>
                <div id="area-<%=row%>">
                    <%= ((area == null || area.equals("")) ? "&nbsp;" : area) %>
                    <input type="hidden" name="area-<%=row%>" value="<%=area%>"/>
                </div>
            </td>

            <td>
                <div id="comment-<%=row%>">
                    <%= ((comment == null || comment.equals("")) ? "&nbsp;" : comment) %>
                    <input type="hidden" name="comment-<%=row%>" value="<%=comment%>"/>
                </div>
            </td>

            <input type="hidden" name="password-<%=row%>" value="<%= ((password == null || password.equals("")) ? "&nbsp;" : password) %>"/>

        </tr>

        <tr <%if(swip != null && host.equals(swip)) out.print("class=\"selected\"");%>>
            <td colspan="17" style="text-align:left"> &nbsp;&nbsp;<b>操作：</b>
                <a id="<%= "ss("+id+").doDelete" %>" href="javascript:deleteSwitcher('<%=id%>', '<%=host%>')" onclick="return confirm('你确定要删除： <%=host%> ?')">删除</a>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <a id="<%= "ss("+id+").doDelete" %>" href="javascript:modifySwitcher('<%=id%>', '<%=row%>')">修改</a>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <a id="<%= "ss("+id+").ports" %>" href="javascript:managePorts('<%=row%>')">端口操作</a>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <a id="<%= "ss("+id+").doBunding" %>" href="javascript:bundingIP('<%=row%>')">地址绑定</a>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <a id="<%= "ss("+id+").doRecovery" %>" href="javascript:recoverySwitcher('<%=row%>')" onclick="return confirm('你确定要恢复： <%=host%>交换机的配置 ?')">恢复系统</a>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <a id="<%= "ss("+id+").doBackup" %>" href="javascript:backupSwitcher('<%=row%>')">备份系统</a>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <a id="<%= "ss("+id+").doBackupCycle" %>" href="javascript:backupSwitcherCycle('<%=row%>')">定期备份</a>

                <select id="cycle1_<%=row%>" name="cycle1_<%=row%>" onChange="changelocation(document.allSwitchers.cycle1_<%=row%>.options[document.allSwitchers.cycle1_<%=row%>.selectedIndex].value,<%=row%>)" size="1">
                    <option value="">请选择</option>
                    <option value="每天">每天</option>
                    <option value="每周">每周</option>
                    <%--<option value="每月">每月</option>--%>
                </select>
                <select id="cycle2_<%=row%>" name="cycle2_<%=row%>">
                    <option value="" selected>请选择</option>
                </select>
                <select id="cycle3_<%=row%>" name="cycle3_<%=row%>" style="display:none">
                    <option value="" selected>请选择</option>
                </select>
            </td>
        </tr>

        <%
                row++;
            }
        %>
    </table>
    </div>
    <a href = "abcbank/switcher.jsp?curPage=1&group=<%=groupReturn%>&name=<%=nameReturn%>&brand=<%=brandReturn%>&ip=<%=ipReturn%>&comment=<%=commentReturn%>" >首页</a>
    <%
        if(curPage - 1 > 0)
            out.print("<a href = 'abcbank/switcher.jsp?curPage=" + (curPage - 1) + "&group=" + groupReturn + "&name=" + nameReturn + "&brand=" + brandReturn +
                    "&ip=" + ipReturn + "&comment=" + commentReturn + "' >上一页</a>");
        else
            out.print("上一页");
    %>
    <%
        if(curPage + 1 <= pageCount)
            out.print("<a href = 'abcbank/switcher.jsp?curPage=" + (curPage + 1)  + "&group=" + groupReturn + "&name=" + nameReturn + "&brand=" + brandReturn +
                    "&ip=" + ipReturn + "&comment=" + commentReturn + "' >下一页</a>");
        else
            out.print("下一页");
    %>
    <a href = "abcbank/switcher.jsp?curPage=<%=pageCount%>&group=<%=groupReturn%>&name=<%=nameReturn%>&brand=<%=brandReturn%>&ip=<%=ipReturn%>&comment=<%=commentReturn%>" >尾页</a>
    第<%=curPage%>页/共<%=pageCount%>页

    <br>
    <div>
        &nbsp;&nbsp;
        <div style="font-size:14px;">批量操作：请先选中需要批量操作的交换机，然后在下面输入批量操作命令（每行一条），最后点击下面的“执行”按钮</div>
        <textarea id="batchComm" name="batchComm" rows="20" style="width:900px; overflow: auto;"></textarea>
        <br>&nbsp;&nbsp;
        <input type="button" onclick="javascript:batchOperator('<%=row%>')" value="执行">
    </div>
</form>

<jsp:include page="/includes/footer.jsp" flush="false" />