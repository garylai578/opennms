<%--
  Created by IntelliJ IDEA.
  User: laiguanhui
  Date: 2016/3/15
  Time: 14:56
  To change this template use File | Settings | File Templates.
--%>


<%@page language="java"
        contentType="text/html"
        session="true"
%>

<%@ page import="org.opennms.core.bank.BankIPAddress" %>
<%@ page import="org.opennms.core.bank.BankIPAddressOp" %>
<%@ page import="org.opennms.web.springframework.security.Authentication" %>
<%@ page import="java.text.ParseException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>

<%@include file="/abcbank/getVars.jsp"%>

<%!
    int pageCount;
    int curPage = 1;
%>

<%
    String bankReturn, deptReturn, typeReturn, usersReturn, update;

    if(request.getAttribute("bank") != null )
        bankReturn = (String)request.getAttribute("bank");
    else {
        bankReturn = request.getParameter("bank");
        if(bankReturn == null)
            bankReturn = "";
    }

    if(request.getAttribute("dept") != null)
        deptReturn = (String)request.getAttribute("dept");
    else {
        deptReturn = request.getParameter("dept");
        if(deptReturn == null)
            deptReturn = "";
    }

    if(request.getAttribute("network_type") != null)
        typeReturn = (String)request.getAttribute("network_type");
    else {
        typeReturn = request.getParameter("network_type");
        if(typeReturn == null)
            typeReturn = "";
    }

    if(request.getAttribute("users") != null)
        usersReturn = (String)request.getAttribute("users");
    else {
        usersReturn = request.getParameter("users");
        if(usersReturn == null)
            usersReturn = "";
    }

    if(request.getAttribute("update") != null)
        update = (String)request.getAttribute("update");
    else
        update = request.getParameter("update");

    Map<String, String> colAndValue = new HashMap<String, String>();
    if(bankReturn != null && !"".equals(bankReturn) && !"null".equals(bankReturn))
        colAndValue.put("bank", bankReturn);
    if(deptReturn != null && !"".equals(deptReturn) && !"null".equals(deptReturn))
        colAndValue.put("dept", deptReturn);
    if(typeReturn != null && !"".equals(typeReturn) && !"null".equals(typeReturn))
        colAndValue.put("network_type", typeReturn);
    if(usersReturn != null && !"".equals(usersReturn) && !"null".equals(usersReturn))
        colAndValue.put("users", usersReturn);

    BankIPAddressOp op = new BankIPAddressOp();
    BankIPAddress[] ips = (BankIPAddress[])session.getAttribute("ip_addresses");
    List<BankIPAddress> ipsList = new LinkedList<BankIPAddress>();
    if(ips == null || (update != null && update.equals("true"))){
        if(request.isUserInRole(Authentication.ROLE_ADMIN))
            ips = op.unionSearch(colAndValue);
        else {
            colAndValue.put("bank", group);
            ips = op.unionSearch(colAndValue);
        }
    }
    session.setAttribute("ip_addresses", ips);

    for(BankIPAddress ip : ips) {
        //如果停用的时间超过7天，则不显示
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String stop_date = ip.getStop_date();
        String state = ip.getState();
        if (stop_date != null && state.contains("停用")) {
            try {
                long today = sf.parse(sf.format(date)).getTime();
                long stop = sf.parse(stop_date).getTime();
                long inten = (today - stop) / (1000 * 60 * 60 * 24);
                if (inten >= 7)
                    continue;
                else
                    ipsList.add(ip);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else
            ipsList.add(ip);
    }
    ips = ipsList.toArray(new BankIPAddress[ipsList.size()]);
    int nums=0;

	int size = ips.length;
	pageCount = (size%PAGESIZE==0)?(size/PAGESIZE):(size/PAGESIZE+1);
	String tmp = request.getParameter("curPage");
    if(tmp==null || "".equals(tmp) || "null".equals(tmp)){
        tmp="1";
    }
	curPage = Integer.parseInt(tmp);
	if(pageCount > 0 && curPage >= pageCount){
		curPage = pageCount;
		nums = size%PAGESIZE;
	}else{
		nums = PAGESIZE;
	}
	
%>

<jsp:include page="/includes/header.jsp" flush="false" >
    <jsp:param name="title" value="IP地址台帐" />
    <jsp:param name="headTitle" value="IP地址台帐" />
    <jsp:param name="breadcrumb" value="<a href='abcbank/index.jsp'>台帐管理</a>" />
    <jsp:param name="breadcrumb" value="IP地址台帐" />
</jsp:include>

<script type="text/javascript" >

    function addIPAddress()
    {
        document.allIPSegments.action="abcbank/newIPAddress.jsp";
        document.allIPSegments.submit();
    }

    function stopSelectedIPAddress(numbers){
        var selectedId = "";
        var selectedIP = "";
        for (var i = 0; i < numbers; ++i) {
            var item = document.getElementById("choose-"+i);
            if (item.checked == true) {
                selectedId += document.getElementById("id-" + i).value + "\t";
                selectedIP += document.getElementById("ipaddr-" + i).value + "\t";
            }
        }
        stopIPAddress(selectedId, selectedIP);
    }

    function stopIPAddress(id, ip)
    {
        document.allIPSegments.action="abcbank/stopIPAddress";
        document.allIPSegments.ipAddrID.value=id;
        document.allIPSegments.ipAddr.value=ip;
        document.allIPSegments.submit();
    }

    function startSelectedIPAddress(numbers){
        var selectedId = "";
        var selectedIP = "";
        for (var i = 0; i < numbers; ++i) {
            var item = document.getElementById("choose-"+i);
            if (item.checked == true) {
                selectedId += document.getElementById("id-" + i).value + "\t";
                selectedIP += document.getElementById("ipaddr-" + i).value + "\t";
            }
        }
        startIPAddress(selectedId, selectedIP);
    }

    function startIPAddress(id, ip)
    {
        document.allIPSegments.action="abcbank/startIPAddress";
        document.allIPSegments.ipAddrID.value=id;
        document.allIPSegments.ipAddr.value=ip;
        document.allIPSegments.submit();
    }

    function modifyIPAddress(id,row)
    {
        document.allIPSegments.action="abcbank/updateIPAddress";
        document.allIPSegments.ipAddrID.value=id;
        document.allIPSegments.rowID.value=row;
        document.allIPSegments.submit();
    }

    function searchIPAddress()
    {
        document.allIPSegments.action="abcbank/searchIPAddress";
        document.allIPSegments.submit();
    }

    function outputExcel(row){
        document.allIPSegments.action="abcbank/exportIPAddress";
        document.allIPSegments.rows.value=row;
        document.allIPSegments.submit();
    }

    function selectAll(numbers) {
        for (var i = 0; i <= numbers; ++i) {
            var choose = document.getElementById("choose-"+i);
            choose.checked = true;
        }
    }

    function unselectAll(numbers) {
        for (var i = 0; i <= numbers; ++i) {
            var choose = document.getElementById("choose-"+i);
            choose.checked = false;
        }
    }

</script>

<form method="post" name="allIPSegments">
    <input type="hidden" name="ipAddrID"/>
    <input type="hidden" name="ipAddr"/>
    <input type="hidden" name="rowID"/>
    <input type="hidden" name="rows"/>
    <input type="hidden" name="searchAreas" />

    <h3>IP地址台帐管理</h3>
    <table>

    <td align="left">
        <a id="doNewIPSegment" href="javascript:addIPAddress()"><img src="images/add1.gif" alt="新增IP地址" border="0"></a>
        <a href="javascript:addIPAddress()">新增IP地址</a>
    </td>

    <td align="left">&nbsp;&nbsp;
        <strong>网络类型：</strong><select id="network_type" name="network_type">
                    <option value="" selected="">请选择</option>
                    <%
                        for(int i = 0; i < networkTypes.length; ++i){
                    %>
                    <option value="<%=networkTypes[i]%>" <%=((typeReturn != null && networkTypes[i].equals(typeReturn)) ? "selected" : "")%>> <%=networkTypes[i]%></option>
                    <%
                        }
                    %>
                 </select>&nbsp;&nbsp;
        <strong>设备使用人:</strong><input id="users" name="users" size="12" value="<%=(usersReturn==null?"":usersReturn)%>">&nbsp;&nbsp;
        <strong>所属支行（分行）：</strong><select id="bank" name="bank" onChange="selectDepts(this.value, 'dept')">
                            <option value="" selected="">请选择</option>
                            <%
                                for(int i = 0; i < bankNames.length; ++i){
                            %>
                            <option value="<%=bankNames[i]%>" <%=((bankReturn != null && bankNames[i].equals(bankReturn)) ? "selected" : "")%>><%=bankNames[i]%></option>
                            <%
                                }
                            %>
                        </select>&nbsp;&nbsp;
        <strong>所属网点（部门）：</strong><select id="dept" name="dept">
            <option value="" selected>请选择</option>
            <%
                if(!"".equals(bankReturn)){
                    String[] depts = bankAndDepts.get(bankReturn);
                    if(depts != null)
                        for(String dep : depts){
                            String selected = "";
                            if(dep.equals(deptReturn))
                                selected = "selected";
            %>
            <option value="<%=dep%>" <%=selected%>><%=dep%></option>
            <%
                        }
                }
            %>
        </select>  &nbsp;&nbsp;
<%--        <input id="searchArea-0" type="checkbox" value="network_type" checked>网络类型
        <input id="searchArea-1" type="checkbox" value="users" checked>设备使用人
        <input id="searchArea-2" type="checkbox" value="bank" checked>所属支行（分行）
        <input id="searchArea-3" type="checkbox" value="dept" checked>所属网点（部门）&nbsp;--%>
        <a id="doSearch" href="javascript:searchIPAddress()"><img src="images/search.png" alt="搜索" border="0"></a>
        <a id="search" href="javascript:searchIPAddress()">搜索</a>
    </td>

        <td align="left">
            <a id="output" href="javascript:outputExcel(<%=nums%>)"><img src="images/output.jpg" alt="输出报表" border=""0></a>
            <a href="javascript:outputExcel(<%=nums%>)">输出报表</a>
        </td>
    </table>

    <div style="overflow: auto; width: 100%;">
    <table width="100%" border="1" cellspacing="0" cellpadding="2" bordercolor="black" class="tab_css_1">

        <tr class="header1">
            <td style="width: 30px"><b>选择</b></td>
            <td style="width: 120px"><b>操作</b></td>
            <td style="width: 100px"><b>ip地址</b></td>
            <td><b>网络类型</b></td>
            <td style="width: 100px"><b>掩码</b></td>
            <td style="width: 90px"><b>网关</b></td>
            <td style="width: 100px"><b>mac地址</b></td>
            <td><b>申请时间</b></td>
            <td><b>启用日期</b></td>
            <td><b>设备使用人</b></td>
            <td><b>所属支行（分行）</b></td>
            <td><b>所属网点（部门）</b></td>
            <td><b>设备类型</b></td>
            <td><b>设备品牌</b></td>
            <td><b>设备型号</b></td>
            <td><b>用途</b></td>
            <td style="width: 50px"><b>使用情况</b></td>
            <td><b>备注</b></td>
        </tr>
        <%
            int ipAtArray = (curPage - 1) * PAGESIZE;
            int row = 0;
            for(int j = ipAtArray; j < ipAtArray + PAGESIZE && j < ips.length; j++){
                BankIPAddress ip = ips[j];
                String ipId = ip.getId();
                String ipaddr = ip.getIp();
                String gateway = ip.getGateway();
                String mask = ip.getMask();
                String mac = ip.getMac();
                String network_type = ip.getNetwork_type();
                String start_date = ip.getStart_date();
                String stop_date = ip.getStop_date();
                String apply_date = ip.getApply_date();
                String usres = ip.getUsers();
                String bank = ip.getBank();
                String dept = ip.getDept();
                String model = ip.getModel();
                String equip_type = ip.getEquip_type();
                String equip_brand = ip.getEquip_brand();
                String app = ip.getApplication();
                String state = ip.getState();
                String comment = ip.getComment();
        %>
        <tr <%if (state.equals("停用")) out.print("class=\"lineUnused\"");%>>
            <td>
                <div>
                    <input id="choose-<%=row%>" type="checkbox" value="" />
                </div>
            </td>
            <td align="center" style="vertical-align:middle;">
                <a id="<%= "ips("+ipId+").doStop" %>" href="javascript:stopIPAddress('<%=ipId%>', '<%=ipaddr%>')" onclick="return confirm('确定要停用该IP？')">停用</a>
                &nbsp;&nbsp;
                <a id="<%= "ips("+ipId+").doStart" %>" href="javascript:startIPAddress('<%=ipId%>', '<%=ipaddr%>')">启用</a>
                &nbsp;&nbsp;
                <a id="<%= "ips("+ipId+").doModify" %>" href="javascript:modifyIPAddress('<%=ipId%>', '<%=row%>')">变更</a>
            </td>

            <input type="hidden" id="id-<%=row%>" name="id-<%=row%>" value="<%=ipId %>"/>

            <td>
                <div>
                    <%= ((ipaddr == null || ipaddr.equals("")) ? "&nbsp;" : ipaddr) %>
                    <input type="hidden" id="ipaddr-<%=row%>" name="ipaddr-<%=row%>" value="<%= ((ipaddr == null || ipaddr.equals("")) ? "&nbsp;" : ipaddr) %>"/>
                </div>
            </td>

            <td>
                <div>
                    <select id="network_type-<%=row%>" name="network_type-<%=row%>">
                        <%
                            if(network_type == null || network_type.equals(""))
                                out.print("<option value=\"0\" selected=\"\">请选择</option>");
                        %>
                        <%
                            for(int i = 0; i < networkTypes.length; ++i){
                        %>
                        <option value="<%=networkTypes[i]%>" <%if(network_type.equals(networkTypes[i])) out.print("selected=\"\"");%>><%=networkTypes[i]%></option>
                        <%
                            }
                        %>
                    </select>
                </div>
            </td>

            <td>
                <div id="mask-<%=row%>">
                    <%= ((mask == null || mask.equals("")) ? "&nbsp;" : mask) %>
                    <input type="hidden" name="mask-<%=row%>" value="<%= ((mask == null || mask.equals("")) ? "&nbsp;" : mask) %>"/>
                </div>
            </td>

            <td>
                <div id="gateway-<%=row%>">
                    <%= ((gateway == null || gateway.equals("")) ? "&nbsp;" : gateway) %>
                    <input type="hidden" name="gateway-<%=row%>" value="<%= ((gateway == null || gateway.equals("")) ? "&nbsp;" : gateway) %>"/>
                </div>
            </td>

            <td>
                <div id="mac-<%=row%>">
                    <%= ((mac == null || mac.equals("")) ? "&nbsp;" : mac) %>
                    <input type="hidden" name="mac-<%=row%>" value="<%= ((mac == null || mac.equals("")) ? "&nbsp;" : mac) %>"/>
                </div>
            </td>

            <td>
                <div id="apply_date-<%=row%>">
                    <%= ((apply_date == null || apply_date.equals("")) ? "&nbsp;" : apply_date) %>
                    <input type="hidden" name="apply_date-<%=row%>" value="<%= ((apply_date == null || apply_date.equals("")) ? "&nbsp;" : apply_date) %>"/>
                </div>
            </td>

            <td>
                <div id="start_date-<%=row%>">
                    <%= ((start_date == null || start_date.equals("")) ? "&nbsp;" : start_date) %>
                    <input type="hidden" name="start_date-<%=row%>" value="<%= ((start_date == null || start_date.equals("")) ? "&nbsp;" : start_date) %>"/>
                </div>
            </td>

            <td>
                <div>
                    <input  id="users-<%=row%>" size="5" name="users-<%=row%>" value="<%= ((usres == null || usres.equals("")) ? "&nbsp;" : usres) %>" >
                </div>
            </td>

            <td>
                <div>
                    <select id="bank-<%=row%>" name="bank-<%=row%>" onChange="selectDepts(this.value, 'dept-<%=row%>')">
                        <%
                            if(bank == null || bank.equals(""))
                                out.print("<option value=\"0\" selected=\"\">请选择</option>");
                        %>
                        <%
                            for(int i = 0; i < bankNames.length; ++i){
                        %>
                        <option value="<%=bankNames[i]%>"  <%if(bank.equals(bankNames[i])) out.print("selected=\"\"");%>><%=bankNames[i]%></option>
                        <%
                            }
                        %>
                    </select>
                </div>
            </td>

            <td>
                <div>
                    <select id="dept-<%=row%>" name="dept-<%=row%>">
                        <%
                            if(dept == null || dept.equals(""))
                                out.print("<option value=\"0\" selected=\"\">请选择</option>");
                        %>
                        <%
                            String[] depts = bankAndDepts.get(bank);
                            for(int i = 0; i < depts.length; ++i){
                        %>
                        <option value="<%=depts[i]%>"<%if(dept.equals(depts[i])) out.print("selected=\"\"");%>><%=depts[i]%></option>
                        <%
                            }
                        %>
                    </select>
                </div>
            </td>

            <td>
                <div id="model-<%=row%>">
                    <input id="model-<%=row%>" name="model-<%=row%>" size="6" value="<%= ((model == null || model.equals("")) ? "&nbsp;" : model) %>">
                </div>
            </td>

            <td>
                <div>
                    <input id="equip_brand-<%=row%>" name="equip_brand-<%=row%>" size="5" value="<%= ((equip_brand == null || equip_brand.equals("")) ? "&nbsp;" : equip_brand) %>">
                </div>
            </td>

            <td>
                <div>
                    <input id="equip_type-<%=row%>" name="equip_type-<%=row%>" size="6" value="<%= ((equip_type == null || equip_type.equals("")) ? "&nbsp;" : equip_type) %>">
                </div>
            </td>

            <td>
                <div>
                    <input id="app-<%=row%>" name="app-<%=row%>" size="6" value="<%= ((app == null || app.equals("")) ? "&nbsp;" : app) %>">
                </div>
            </td>

            <td>
                <div id="state-<%=row%>">
                    <%= ((state == null || state.equals("")) ? "&nbsp;" : state) %>
                    <input type="hidden" name="state-<%=row%>" value="<%= ((state == null || state.equals("")) ? "&nbsp;" : state) %>"/>
                </div>
            </td>

            <td>
                <div>
                    <input id="comment-<%=row%>" name="comment-<%=row%>" size="8" type="text" value="<%= ((comment == null || comment.equals("")) ? "无备注；" : comment) %>"/>
                </div>
            </td>
        </tr>
        <%
                row++;
            }
        %>
    </table>
        <div>&nbsp;
        <input type="button" onclick="javascript:selectAll(<%=row%>)" value="全选"/>&nbsp;
            <input type="button" onclick="javascript:unselectAll(<%=row%>)" value="全不选"/>&nbsp;
            <input type="button" onclick="javascript:stopSelectedIPAddress(<%=row%>)" value="停用选中"/>&nbsp;
            <input type="button" onclick="javascript:startSelectedIPAddress(<%=row%>)" value="启用选中"/>&nbsp;
        </div>
    </div>
    <div>&nbsp;
        <a href = "abcbank/ipaddress.jsp?curPage=1&bank=<%=bankReturn%>&dept=<%=deptReturn%>&network_type=<%=typeReturn%>&users=<%=usersReturn%>" >首页</a>
        <%
            if(curPage - 1 > 0)
                out.print("<a href = 'abcbank/ipaddress.jsp?curPage=" + (curPage - 1) + "&bank=" + bankReturn + "&dept=" + deptReturn + "&network_type=" + typeReturn + "&users=" + usersReturn + "' >上一页</a>");
            else
                out.print("上一页");
        %>
        <%
            if(curPage + 1 <= pageCount)
                out.print("<a href = 'abcbank/ipaddress.jsp?curPage=" + (curPage + 1) + "&bank=" + bankReturn + "&dept=" + deptReturn + "&network_type=" + typeReturn + "&users=" + usersReturn + "' >下一页</a>");
            else
                out.print("下一页");
        %>
        <a href = "abcbank/ipaddress.jsp?curPage=<%=pageCount%>&bank=<%=bankReturn%>&dept=<%=deptReturn%>&network_type=<%=typeReturn%>&users=<%=usersReturn%>" >尾页</a>
        第<%=curPage%>页/共<%=pageCount%>页
    </div>
    <input type="hidden" name="curPage" value="<%=curPage%>"/>
</form>

<jsp:include page="/includes/footer.jsp" flush="false" />
