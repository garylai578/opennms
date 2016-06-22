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

<%@page import="org.opennms.core.bank.IPSegment"%>
<%@page import="org.opennms.core.bank.IPSegmentOperater" %>
<%@ page import="java.text.ParseException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>


<%@include file="/abcbank/getVars.jsp"%>

<%!
    int pageCount;
    int curPage = 1;
%>

<%
    IPSegmentOperater op = new IPSegmentOperater();
    String bankReturn,deptReturn, stateReturn, update;

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

    if(request.getAttribute("state") != null)
        stateReturn = (String)request.getAttribute("state");
    else {
        stateReturn = request.getParameter("state");
        if(stateReturn == null)
            stateReturn = "";
    }

    update = (String)request.getAttribute("update");

    IPSegment[] ips = null;
    if(request.getAttribute("ipSeg") != null)
        ips = (IPSegment[])request.getAttribute("ipSeg");
    if(ips == null || (update != null && update.equals("true")))
        ips = op.selectAll("");
    int nums = ips.length;
    List<IPSegment> ipSegmentList = new LinkedList<IPSegment>();
    for(IPSegment ip : ips) {
        //如果停用的时间超过7天，则不显示
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String stop_date = ip.getStopTime();
        String state = ip.getState();
        if (stop_date != null && state.contains("停用")) {
            try {
                long today = sf.parse(sf.format(date)).getTime();
                long stop = sf.parse(stop_date).getTime();
                long inten = (today - stop) / (1000 * 60 * 60 * 24);
                if (inten >= 7)
                    continue;
                else
                    ipSegmentList.add(ip);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else
            ipSegmentList.add(ip);
    }
%>

<jsp:include page="/includes/header.jsp" flush="false" >
    <jsp:param name="title" value="IP地址段分配" />
    <jsp:param name="headTitle" value="IP地址段分配" />
    <jsp:param name="breadcrumb" value="<a href='abcbank/index.jsp'>台帐管理</a>" />
    <jsp:param name="breadcrumb" value="IP地址段分配" />
</jsp:include>

<script type="text/javascript" >

    function addIPSegment()
    {
        document.allIPSegments.action="abcbank/newIPSegment.jsp";
        document.allIPSegments.submit();
    }

    function stopSelected(numbers){
        var selectedId = "";
        var ipsegs = "";
        for (var i = 0; i < numbers; ++i) {
            var item = document.getElementById("choose-"+i);
            if (item.checked == true) {
                selectedId += document.getElementById("id-" + i).value + "\t";
                ipsegs += document.getElementById("startIP-"+i).value + "-" + document.getElementById("endIP-"+i).value + "\t";
            }
        }
        stopIPSegment(selectedId, ipsegs);
    }

    function stopIPSegment(id, ipsegs)
    {
        document.allIPSegments.action="abcbank/stopIPSegment";
        document.allIPSegments.ipSegID.value=id;
        document.allIPSegments.ipSegs.value = ipsegs;
        document.allIPSegments.submit();
    }

    function startSelected(numbers){
        var selectedId = "";
        var ipsegs = "";
        for (var i = 0; i < numbers; ++i) {
            var item = document.getElementById("choose-"+i);
            if (item.checked == true) {
                selectedId += document.getElementById("id-" + i).value + "\t";
                ipsegs += document.getElementById("startIP-"+i).value + "-" + document.getElementById("endIP-"+i).value + "\t";
            }
        }
        startIPSegment(selectedId, ipsegs);
    }

    function startIPSegment(id, ipsegs)
    {
        document.allIPSegments.action="abcbank/startIPSegment";
        document.allIPSegments.ipSegID.value=id;
        document.allIPSegments.ipSegs.value = ipsegs;
        document.allIPSegments.submit();
    }

    function modifyIPSegment(id,row)
    {
        document.allIPSegments.action="abcbank/updateIPSegment";
        document.allIPSegments.ipSegID.value=id;
        document.allIPSegments.rowID.value=row;
        document.allIPSegments.bankName.value=document.getElementById("bankname-"+row).value;
        document.allIPSegments.bankType.value=document.getElementById("banktype-"+row).value;
        document.allIPSegments.comments.value=document.getElementById("comment-"+row).value;
        document.allIPSegments.submit();
    }

    function outputExcel(row){
        document.allIPSegments.action="abcbank/exportIPSegment";
        document.allIPSegments.rows.value=row;
        document.allIPSegments.submit();
    }

    function searchIPSegment()
    {
        document.allIPSegments.action="abcbank/searchIPSegment";
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
    <input type="hidden" name="ipSegID"/>
    <input type="hidden" name="rowID"/>
    <input type="hidden" name="bankName"/>
    <input type="hidden" name="bankType"/>
    <input type="hidden" name="comments"/>
    <input type="hidden" name="rows"/>
    <input type="hidden" name="ipSegs"/>

    <h3>IP地址段分配</h3>

    <table>
    <td align="left">
        <a id="doNewIPSegment" href="javascript:addIPSegment()"><img src="images/add1.gif" alt="新增IP段" border="0"></a>
        <a href="javascript:addIPSegment()">新增IP段</a>
    </td>

    <td align="left">&nbsp;&nbsp;
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
        <strong>使用情况：</strong><select id="state" name="state">
            <option value="" selected="">请选择</option>
            <option value="在用" <%=((stateReturn != null && stateReturn.equals("在用")) ? "selected" : "")%>>在用</option>
            <option value="停用" <%=((stateReturn != null && stateReturn.equals("停用")) ? "selected" : "")%>>停用</option>
        </select>&nbsp;&nbsp;
        <a id="doSearch" href="javascript:searchIPSegment()"><img src="images/search.png" alt="搜索" border="0"></a>
        <a id="search" href="javascript:searchIPSegment()">搜索</a>
    </td>

    <td align="left">
        <a id="output" href="javascript:outputExcel(<%=nums%>)"><img src="images/output.jpg" alt="输出报表" border=""0></a>
        <a href="javascript:outputExcel(<%=nums%>)">输出报表</a>
    </td>
    </table>

    <div style="overflow: auto; width: 100%;">
    <table width="100%" border="1" cellspacing="0" cellpadding="2" bordercolor="black">

        <tr id="header1">
            <td><b>选择</b></td>
            <td><b>操作</b></td>
            <td><b>所属IP段</b></td>
            <td><b>网关</b></td>
            <td><b>掩码</b></td>
            <td><b>开始IP-结束IP</b></td>
            <td><b>所属分行（支行）</b></td>
            <td><b>所属网点（部门）</b></td>
            <td><b>网点类型</b></td>
            <td><b>启用日期</b></td>
            <td><b>使用情况</b></td>
            <td><b>备注</b></td>
        </tr>
        <%
            int size = ipSegmentList.size();
            pageCount = (size%PAGESIZE==0)?(size/PAGESIZE):(size/PAGESIZE+1);
            String tmp = request.getParameter("curPage");
            if(tmp==null){
                tmp="1";
            }
            curPage = Integer.parseInt(tmp);
            if(curPage >= pageCount)
                curPage = pageCount;
            int ipAtList = (curPage - 1) * PAGESIZE;

            int row = 0;
            for(int j = ipAtList; j < ipAtList + PAGESIZE && j < ipSegmentList.size(); j++){
                IPSegment ip  =  ipSegmentList.get(j);
                String ipId = ip.getId();
                String ipSeg = ip.getSegment();
                String gateway = ip.getGateway();
                String mask = ip.getMask();
                String startIP = ip.getStartIP();
                String endIP = ip.getEndIP();
                String name = ip.getBankname();
                String type = ip.getBanktype();
                String time = ip.getCreateTime();
                String stopTime = ip.getStopTime();
                String state = ip.getState();
                String comment = ip.getComment();

                String[] bankAndDept = name.split("/");
                name = bankAndDept[0];
                String dept = "";
                if(bankAndDept.length == 2)
                    dept = bankAndDept[1];
        %>
        <tr  <%if (state.equals("停用")) out.print("class=\"lineUnused\"");%>>
            <td>
                <div>
                    <input id="choose-<%=row%>" type="checkbox" value="" />
                </div>
            </td>

            <td align="center" style="text-align:center;vertical-align:middle;">
                <a id="<%= "ips("+ipId+").doStop" %>" href="javascript:stopIPSegment('<%=ipId%>', '<%=row%>')" onclick="return confirm('确定要停用该IP段？')">停用</a>
                &nbsp;&nbsp;
                <a id="<%= "ips("+ipId+").doStart" %>" href="javascript:startIPSegment('<%=ipId%>', '<%=row%>')">启用</a>
                &nbsp;&nbsp;
                <a id="<%= "ips("+ipId+").doModify" %>" href="javascript:modifyIPSegment('<%=ipId%>', '<%=row%>')">修改</a>
            </td>

            <input type="hidden" id="id-<%=row%>" name="id-<%=row%>" value="<%=ipId %>"/>

            <td>
                <div id="ipSeg-<%=row%>">
                    <%= ((ipSeg == null || ipSeg.equals("")) ? "&nbsp;" : ipSeg) %>
                    <input type="hidden" name="ipSeg-<%=row%>" value="<%= ((ipSeg == null || ipSeg.equals("")) ? "&nbsp;" : ipSeg) %>"/>
                </div>
            </td>

            <td>
                <div id="gateway-<%=row%>">
                    <%= ((gateway == null || gateway.equals("")) ? "&nbsp;" : gateway) %>
                    <input type="hidden" name="gateway-<%=row%>" value="<%= ((gateway == null || gateway.equals("")) ? "&nbsp;" : gateway) %>"/>
                </div>
            </td>

            <td>
                <div id="mask-<%=row%>">
                    <%= ((mask == null || mask.equals("")) ? "&nbsp;" : mask) %>
                    <input type="hidden" name="mask-<%=row%>" value="<%= ((mask == null || mask.equals("")) ? "&nbsp;" : mask) %>"/>
                </div>
            </td>

            <td>
                <div id="ipsegment-<%=row%>">
                    <%= ((startIP == null || startIP.equals("") || endIP == null || endIP.equals("")) ? "&nbsp;" : startIP + "-" + endIP) %>
                    <input type="hidden" id="startIP-<%=row%>" name="startIP-<%=row%>" value="<%= ((startIP == null || startIP.equals("")) ? "&nbsp;" : startIP) %>"/>
                    <input type="hidden" id="endIP-<%=row%>" name="endIP-<%=row%>" value="<%= ((endIP == null || endIP.equals("")) ? "&nbsp;" : endIP) %>"/>
                </div>
            </td>

            <td>
                <div>
                    <select id="bankname-<%=row%>" name="bankname-<%=row%>" onChange="selectDepts(this.value, 'dept-<%=row%>')">
                        <%
                            if(name == null || name.equals(""))
                                out.print("<option value=\"0\" selected=\"\">请选择</option>");
                        %>
                        <%
                            for(int i = 0; i < bankNames.length; ++i){
                        %>
                        <option value="<%=bankNames[i]%>"<%if(name.equals(bankNames[i])) out.print("selected=\"\"");%>><%=bankNames[i]%></option>
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
                            String[] depts = bankAndDepts.get(name);
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
                <div>
                    <select id="banktype-<%=row%>" name="banktype-<%=row%>">
                        <%
                            if(type == null || type.equals(""))
                                out.print("<option value=\"0\" selected=\"\">请选择</option>");
                        %>
                        <%
                            for(int i = 0; i < bankTypes.length; ++i){
                        %>
                        <option value="<%=bankTypes[i]%>" <%if(type.equals(bankTypes[i])) out.print("selected=\"\"");%>><%=bankTypes[i]%></option>
                        <%
                            }
                        %>
                    </select>

                </div>
            </td>

            <td>
                <div id="createdate-<%=row%>">
                    <%= ((time == null || time.equals("")) ? "&nbsp;" : time) %>
                    <input type="hidden" name="createdate-<%=row%>" value="<%= ((time == null || time.equals("")) ? "&nbsp;" : time) %>"/>
                </div>
            </td>

            <td>
                <div id=state-"<%=row%>">
                    <%= ((state == null || state.equals("")) ? "&nbsp;" : state) %>
                    <input type="hidden" name="state-<%=row%>" value="<%= ((state == null || state.equals("")) ? "&nbsp;" : state) %>"/>
                </div>
            </td>

            <td>
                <div>
                    <input id="comment-<%=row%>" type="text" size="8" value="<%= ((comment == null || comment.equals("")) ? "无备注；" : comment) %>"/>
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
            <input type="button" onclick="javascript:stopSelected(<%=row%>)" value="停用选中"/>&nbsp;
            <input type="button" onclick="javascript:startSelected(<%=row%>)" value="启用选中"/>&nbsp;
        </div>

        <br>
        <div>&nbsp;
        <a href = "abcbank/ipsegment.jsp?curPage=1&bank=<%=bankReturn%>&dept=<%=deptReturn%>&state=<%=stateReturn%>" >首页</a>
        <%
            if(curPage - 1 > 0)
                out.print("<a href = 'abcbank/ipsegment.jsp?curPage=" + (curPage - 1) + "&bank=" + bankReturn + "&dept=" + deptReturn + "&state=" + stateReturn + "' >上一页</a>");
            else
                out.print("上一页");
        %>
        <%
            if(curPage + 1 <= pageCount)
                out.print("<a href = 'abcbank/ipsegment.jsp?curPage=" + (curPage + 1) + "&bank=" + bankReturn + "&dept=" + deptReturn + "&state=" + stateReturn + "' >下一页</a>");
            else
                out.print("下一页");
        %>
        <a href = "abcbank/ipsegment.jsp?curPage=<%=pageCount%>&bank=<%=bankReturn%>&dept=<%=deptReturn%>&state=<%=stateReturn%>" >尾页</a>
        第<%=curPage%>页/共<%=pageCount%>页
        </div>
    </div>
    <input type="hidden" name="curPage" value="<%=curPage%>"/>
</form>

<jsp:include page="/includes/footer.jsp" flush="false" />
