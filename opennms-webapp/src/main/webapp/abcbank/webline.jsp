<%@ taglib prefix="html" uri="http://jakarta.apache.org/struts/tags-html" %>
<%--
  Created by IntelliJ IDEA.
  User: laiguanhui
  Date: 2016/3/17
  Time: 17:20
  To change this template use File | Settings | File Templates.
--%>
<%@page language="java"
        contentType="text/html"
        session="true"
%>
<script language="javascript" type="text/javascript" src="/opennms/js/My97DatePicker/WdatePicker.js"></script>

<%@ page import="org.opennms.core.bank.WebLine" %>
<%@ page import="org.opennms.core.bank.WebLineOperator" %>
<%@ page import="org.opennms.core.resource.Vault" %>
<%@ page import="org.opennms.core.utils.DBUtils" %>
<%@ page import="org.opennms.web.element.Interface" %>
<%@ page import="org.opennms.web.element.NetworkElementFactory" %>
<%@ page import="org.opennms.web.springframework.security.Authentication" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.Statement" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>

<%@include file="/abcbank/getVars.jsp"%>

<%!
    int pageCount;
    int curPage = 1;
%>

<%
    String bankReturn, deptReturn, typeReturn, applicantReturn, approverReturn, update;

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

    if(request.getAttribute("type") != null)
        typeReturn = (String)request.getAttribute("type");
    else {
        typeReturn = request.getParameter("type");
        if(typeReturn == null)
            typeReturn = "";
    }

    if(request.getAttribute("applicant") != null)
        applicantReturn = (String)request.getAttribute("applicant");
    else {
        applicantReturn = request.getParameter("applicant");
        if(applicantReturn == null)
            applicantReturn = "";
    }

    if(request.getAttribute("approver") != null)
        approverReturn = (String)request.getAttribute("approver");
    else {
        approverReturn = request.getParameter("approver");
        if(approverReturn == null)
            approverReturn = "";
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
        colAndValue.put("type", typeReturn);
    if(applicantReturn != null && !"".equals(applicantReturn) && !"null".equals(applicantReturn))
        colAndValue.put("applicant", applicantReturn);
    if(approverReturn != null && !"".equals(approverReturn) && !"null".equals(approverReturn))
        colAndValue.put("approver", approverReturn);

    WebLineOperator op = new WebLineOperator();
    WebLine[] lines = (WebLine[])session.getAttribute("webLines");
    if(lines == null || (update != null && update.equals("true"))){
        if(request.isUserInRole(Authentication.ROLE_ADMIN))
            lines = op.andSearch(colAndValue);
        else {
            colAndValue.put("bank", group);
            lines = op.andSearch(colAndValue);
        }
    }
    session.setAttribute("webLines", lines);

    int row = 0;
    int nums = 0;
	int size = lines.length;
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
%>

<jsp:include page="/includes/header.jsp" flush="false" >
    <jsp:param name="title" value="线路台帐" />
    <jsp:param name="headTitle" value="线路台帐" />
    <jsp:param name="breadcrumb" value="<a href='abcbank/index.jsp'>台帐管理</a>" />
    <jsp:param name="breadcrumb" value="线路台帐" />
</jsp:include>

<script type="text/javascript" >

    function addWebLine()
    {
        document.allWebLines.action="abcbank/newWebLine.jsp";
        document.allWebLines.submit();
    }

    function delectSelected(numbers){
        var selectedId = "";
        for (var i = 0; i < numbers; ++i) {
            var item = document.getElementById("choose-"+i);
            if (item.checked == true) {
                selectedId += document.getElementById("id-" + i).value + "\t";
            }
        }
        deleteWebLine(selectedId);
    }

    function deleteWebLine(id)
    {
        document.allWebLines.action="abcbank/deleteWebLine";
        document.allWebLines.webLineID.value=id;
        document.allWebLines.submit();
    }

    function searchWebLine()
    {
        document.getElementById("bank").removeAttribute("disabled");
        document.allWebLines.action="abcbank/searchWebLine";
        document.allWebLines.submit();
    }

    function outputExcel(row){
        document.allWebLines.action="abcbank/exportWeblineExcel";
        document.allWebLines.rows.value=row;
        document.allWebLines.submit();
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

    function modifyWebline(id, row)
    {
        document.getElementById("state-"+row).removeAttribute("disabled");
        document.allWebLines.action="abcbank/updateWebLine";
        document.allWebLines.rowID.value=row;
        document.allWebLines.webLineID.value=id;
        document.allWebLines.submit();
    }

    function downLoad(row){
        document.allWebLines.rowID.value=row;
        document.allWebLines.action="abcbank/downLoad";
        document.allWebLines.submit();
    }
</script>

<form method="post" name="allWebLines">
    <input type="hidden" name="webLineID"/>
    <input type="hidden" name="rowID"/>
    <input type="hidden" name="searchKey"/>
    <input type="hidden" name="rows"/>

    <h3>线路台帐</h3>
    <table>

        <td align="left">
            <a id="doNewIPSegment" href="javascript:addWebLine()"><img src="images/add1.gif" alt="新增线路" border="0"></a>
            <a href="javascript:addWebLine()">新增线路</a>
        </td>

        <td align="left">&nbsp;&nbsp;
            <strong>专线类型：</strong><select id="type" name="type">
                <option value="" selected="">请选择</option>
                <%
                    for(int i = 0; i < weblineTypes.length; ++i){
                %>
                <option value="<%=weblineTypes[i]%>" <%=((typeReturn != null && typeReturn.equals(weblineTypes[i])) ? "selected" : "")%>> <%=weblineTypes[i]%></option>
                <%
                    }
                %>
            </select>&nbsp;&nbsp;
            <strong>申请人:</strong><input id="applicant" name="applicant" size="8" value="<%=(applicantReturn==null) ? "" : applicantReturn%>">&nbsp;&nbsp;
            <strong>审批人:</strong><input id="approver" name="approver" size="8" value="<%=(approverReturn==null) ? "" : approverReturn%>">&nbsp;&nbsp;
            <strong>所属支行（分行）：</strong><select id="bank" name="bank" onChange="selectDepts(this.value, 'dept')" <%=(group == null || "".equals(group)) ? "" : "disabled"%>>
                <option value="" selected="">请选择</option>
                <%
                    if(group != null && !"".equals(group))
                        bankReturn = group;
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
            <a id="doSearch" href="javascript:searchWebLine()"><img src="images/search.png" alt="搜索" border="0"></a>
            <a id="search" href="javascript:searchWebLine()">搜索</a>
        </td>

        <td align="left">
            <a id="output" href="javascript:outputExcel(<%=nums%>)"><img src="images/output.jpg" alt="输出报表" border=""0></a>
            <a href="javascript:outputExcel(<%=nums%>)">输出报表</a>
        </td>
    </table>

    <div style="overflow: auto; width: 100%;">
    <table border="1" cellspacing="0" cellpadding="2" bordercolor="black" class="tab_css_1">

        <tr class="header1">
            <td style="width: 30px"><b>选择</b></td>
            <td width="5%"><b>操作</b></td>
            <td style="width: 100px"><b>专线IP</b></td>
            <td width="5%"><b>专线状态</b></td>
            <td width="5%"><b>专线类型</b></td>
            <td width="5%"><b>申请人</b></td>
            <td width="10%"><b>联系方式</b></td>
            <td width="5%"><b>审批人</b></td>
            <td width="8%"><b>所属分行（支行）</b></td>
            <td width="8%"><b>所属网点（部门）</b></td>
            <td width="15%"><b>地址</b></td>
            <td width="5%"><b>开通日期</b></td>
            <td width="5%"><b>月租</b></td>
            <td width="5%"><b>VLAN编号</b></td>
            <td width="5%"><b>物理端口号</b></td>
            <td width="5%"><b>运营商接口号</b></td>
            <td width="5%"><b>附件</b></td>
            <td width="10%"><b>备注</b></td>
        </tr>
        <%
            int lineAtArray = (curPage - 1) * PAGESIZE;

            for(int j = lineAtArray; j < lineAtArray + PAGESIZE && j < lines.length; ++j){
                WebLine line = lines[j];
                String lineId = line.getId();
                String ip = line.getIp();
                String state = line.getState();
                String type = line.getType();
                String applicant = line.getApplicant();
                String approver = line.getApprover();
                String contact = line.getContact();
                String bank = line.getBank();
                String dept = line.getDept();
                String address = line.getAddress();
                String start_date = line.getStart_date();
                String rent = line.getRent();
                String vlan_num = line.getVlan_num();
                String port = line.getPort();
                String inter = line.getInter();
                String attach = line.getAttatch();
                String comment = line.getComment();
        %>
        <tr <%if (state.equals("待审批")) out.print("class=\"lineUnused\"");%>>
            <td>
                <div>
                    <input id="choose-<%=row%>" type="checkbox" value="" />
                </div>
            </td>

            <td align="center" style="vertical-align:middle;">
                <a id="<%= "ips("+lineId+").doStop" %>" href="javascript:deleteWebLine('<%=lineId%>')" onclick="return confirm('确定要删除该专线？')">删除</a>
                <%//只有管理员或者待审批的才能够修改
                    if((group != null && !"".equals(group) && state.equals(weblineStates[0])) || group.equals("")){
                %>
                        <a id="<%= "ips("+lineId+").doUpdate" %>" href="javascript:modifyWebline('<%=lineId%>', '<%=row%>')">修改</a>
                <%
                    }
                %>
            </td>

            <input type="hidden" id="id-<%=row%>" name="id-<%=row%>" value="<%=lineId %>"/>

            <td>
                <div id="ip-<%=row%>" >
                    <%
                        if(ipNodeidMap.containsKey(ip)){
                            out.print("<a href=\"/opennms/element/node.jsp?node="+ ipNodeidMap.get(ip) +"\">" + ip + "</a>");
                        }
                        else {
                            out.print((ip == null || ip.equals("")) ? "&nbsp;" : ip);
                        }
                    %>
                    <input type="hidden" id="ip-<%=row%>" name="ip-<%=row%>" value="<%= ((ip == null || ip.equals("")) ? "&nbsp;" : ip) %>"/>
                </div>
            </td>

            <td>
                <select id="state-<%=row%>" name="state-<%=row%>" <%=(group == null || "".equals(group)) ? "" : "disabled"%>>
                    <option value="" selected="">请选择</option>
                    <%
                        for(int i = 0; i < weblineStates.length; ++i){
                    %>
                    <option value="<%=weblineStates[i]%>" <%=((state != null && weblineStates[i].equals(state)) ? "selected" : "")%>><%=weblineStates[i]%></option>
                    <%
                        }
                    %>
                </select>
            </td>

            <td>
                <div id="type-<%=row%>" >
<%--                    <%= ((type == null || type.equals("")) ? "&nbsp;" : type) %>--%>
    <%--<input type="hidden" name="type-<%=row%>" value="<%= ((type == null || type.equals("")) ? "&nbsp;" : type) %>"/>--%>
                    <select name="type-<%=row%>">
                        <%
                            for(int i = 0; i < weblineStates.length; ++i){
                        %>
                        <option value="<%=weblineTypes[i]%>" <%=((type != null && type.equals(weblineTypes[i])) ? "selected" : "")%>><%=weblineTypes[i]%></option>
                        <%
                            }
                        %>
                    </select>

                </div>
            </td>

            <td>
                <div id="applicant-<%=row%>">
                    <%--<%= ((applicant == null || applicant.equals("")) ? "&nbsp;" : applicant) %>--%>
                    <input size="9"  name="applicant-<%=row%>" value="<%= ((applicant == null || applicant.equals("")) ? "&nbsp;" : applicant) %>"/>
                </div>
            </td>

            <td>
                <div id="contact-<%=row%>">
<%--                    <%= ((contact == null || contact.equals("")) ? "&nbsp;" : contact) %>--%>
                    <input name="contact-<%=row%>" value="<%= ((contact == null || contact.equals("")) ? "&nbsp;" : contact) %>" size="9"/>
                </div>
            </td>

            <td>
                <div id="approver-<%=row%>" >
                    <%--<%= ((approver == null || approver.equals("")) ? "&nbsp;" : approver) %>--%>
                    <input size="9"  name="approver-<%=row%>" value="<%= ((approver == null || approver.equals("")) ? "&nbsp;" : approver) %>"/>
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
                <div style="float:left">
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
<%--            <td>
                <div id="bank-<%=row%>">
                    <%= ((bank == null || bank.equals("")) ? "&nbsp;" : bank) %>
                    <input type="hidden"  name="bank-<%=row%>" value="<%= ((bank == null || bank.equals("")) ? "&nbsp;" : bank) %>"/>
                </div>
            </td>

            <td>
                <div id="dept-<%=row%>">
                    <%= ((dept == null || dept.equals("")) ? "&nbsp;" : dept) %>
                    <input type="hidden"  name="dept-<%=row%>" value="<%= ((dept == null || dept.equals("")) ? "&nbsp;" : dept) %>"/>
                </div>
            </td>--%>

            <td>
                <div id="address-<%=row%>">
                   <%-- <%= ((address == null || address.equals("")) ? "&nbsp;" : address) %>--%>
                    <input size="9" name="address-<%=row%>" value="<%= ((address == null || address.equals("")) ? "&nbsp;" : address) %>"/>
                </div>
            </td>

            <td>
                <div id="start_date-<%=row%>">
<%--                    <%= ((start_date == null || start_date.equals("")) ? "" : start_date) %>--%>
                    <input type="text" size="9"  name="start_date-<%=row%>" class="Wdate" onClick="WdatePicker()" value="<%= ((start_date == null || start_date.equals("")) ? "" : start_date) %>"/>
                </div>
            </td>

            <td>
                <div id="rent-<%=row%>">
                   <%-- <%= ((rent == null || rent.equals("")) ? "&nbsp;" : rent) %>--%>
                    <input size="9" name="rent-<%=row%>" value="<%= ((rent == null || rent.equals("")) ? "&nbsp;" : rent) %>"/>
                </div>
            </td>

            <td>
                <div id="vlan_num-<%=row%>">
                    <%--<%= ((vlan_num == null || vlan_num.equals("")) ? "&nbsp;" : vlan_num) %>--%>
                    <input size="9" name="vlan_num-<%=row%>" value="<%= ((vlan_num == null || vlan_num.equals("")) ? "&nbsp;" : vlan_num) %>"/>
                </div>
            </td>

            <td>
                <div id="port-<%=row%>">
                    <%--<%= ((port == null || port.equals("")) ? "&nbsp;" : port) %>--%>
                    <input size="9" name="port-<%=row%>" value="<%= ((port == null || port.equals("")) ? "&nbsp;" : port) %>"/>
                </div>
            </td>

            <td>
                <div id="inter-<%=row%>">
                   <%-- <%= ((inter == null || inter.equals("")) ? "&nbsp;" : inter) %>--%>
                    <input size="9" name="inter-<%=row%>" value="<%= ((inter == null || inter.equals("")) ? "&nbsp;" : inter) %>"/>
                </div>
            </td>

            <td>
                <div>
                    <%
                        if(attach == null || attach.equals("")){
                    %>
                    点击下载
                    <%
                        }else{
                    %>
                    <a href="javascript:downLoad('<%=row%>')">点击下载</a>
                    <%
                        }
                    %>
                    <input id="attach-<%=row%>" name="attach-<%=row%>"  type="hidden" value="<%=attach%>" />
                    <%--<input id="attach-<%=row%>" name="attach-<%=row%>" type="text" size="8" value="<%= ((attach == null || attach.equals("")) ? "&nbsp;" : attach) %>"/>--%>
                </div>
            </td>

            <td>
                <div>
                    <input id="comment-<%=row%>" name="comment-<%=row%>" type="text" size="8" value="<%= ((comment == null || comment.equals("")) ? "无备注；" : comment) %>"/>
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
            <input type="button" onclick="javascript:deleteSelected(<%=row%>)" value="删除选中"/>&nbsp;
        </div>

        <br>
        <div>&nbsp;
        <a href = "abcbank/webline.jsp?curPage=1&type=<%=typeReturn%>&applicant=<%=applicantReturn%>&approver=<%=approverReturn%>&bank=<%=bankReturn%>&dept=<%=deptReturn%>" >首页</a>
        <%
            if(curPage - 1 > 0)
                out.print("<a href = 'abcbank/webline.jsp?curPage=" + (curPage - 1) + "&type=" + typeReturn + "&applicant=" + applicantReturn
                        + "&approver=" + approverReturn + "&bank=" + bankReturn + "&dept=" + deptReturn + "' >上一页</a>");
            else
                out.print("上一页");
        %>
        <%
            if(curPage + 1 <= pageCount)
                out.print("<a href = 'abcbank/webline.jsp?curPage=" + (curPage + 1) + "&type=" + typeReturn + "&applicant=" + applicantReturn
                        + "&approver=" + approverReturn + "&bank=" + bankReturn + "&dept=" + deptReturn + "' >下一页</a>");
            else
                out.print("下一页");
        %>
        <a href = "abcbank/webline.jsp?curPage=<%=pageCount%>&type=<%=typeReturn%>&applicant=<%=applicantReturn%>&approver=<%=approverReturn%>&bank=<%=bankReturn%>&dept=<%=deptReturn%>" >尾页</a>
        第<%=curPage%>页/共<%=pageCount%>页
        </div>
        </div>
    <input type="hidden" name="curPage" value="<%=curPage%>"/>

</form>

<jsp:include page="/includes/footer.jsp" flush="false" />

