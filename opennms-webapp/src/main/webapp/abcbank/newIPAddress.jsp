<%@ page import="java.util.Set" %><%--
  Created by IntelliJ IDEA.
  User: laiguanhui
  Date: 2016/3/16
  Time: 14:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" session="true"%>
<script language="javascript" type="text/javascript" src="/opennms/js/My97DatePicker/WdatePicker.js"></script>

<jsp:include page="/includes/header.jsp" flush="false">
    <jsp:param name="title" value="新增IP地址" />
    <jsp:param name="headTitle" value="新增IP地址" />
    <jsp:param name="breadcrumb" value="<a href='abcbank/index.jsp'>IP管理</a>" />
    <jsp:param name="breadcrumb" value="<a href='abcbank/ipaddress.jsp'>IP地址台帐</a>" />
    <jsp:param name="breadcrumb" value="新增IP地址" />
</jsp:include>

<%@include file="/abcbank/getVars.jsp"%>

<script type="text/javascript" src="js/abcbank.js"></script>

<script type="text/javascript">
    var isCommitted = false;
    function validateFormInput()
    {
        if(isCommitted == true)
            return false;
        isCommitted = true;
        var ip = trimStr(new String(document.newIPs.ipAddr.value));
        var mask = trimStr(new String(document.newIPs.mask.value));
        var gateway = trimStr(new String(document.newIPs.gateway.value));
        var bank = trimStr(new String(document.newIPs.bank.value));
        if(ip==null || ip==0) {
            alert("请输入IP地址！");
            isCommitted = false;
            return false;
        }else if(mask == null|| mask==0){
            alert("请输入掩码！");
            isCommitted = false;
            return false;
        }else if(gateway == 0 || gateway == 0){
            alert("请输入网关！");
            isCommitted = false;
            return false;
        }else if(!judgeIP(ip)){
            alert("IP地址格式不正确！");
            isCommitted = false;
            return false;
        }else if(!judgeIP(mask)){
            alert("掩码地址格式不正确！");
            isCommitted = false;
            return false;
        }else if(!judgeIP(gateway)){
            alert("网关地址格式不正确！");
            isCommitted = false;
            return false;
        }else if(bank == 0){
            alert("请选择所属支行（分行）！");
            isCommitted = false;
            return false;
        }else{
            document.newIPs.action = "abcbank/newIPAddress";

            return true;
        }
    }

    function cancel()
    {
        document.newIPs.action="abcbank/ipaddress.jsp";
        document.newIPs.submit();
    }
</script>


<h3>请填写以下资料</h3>

<form id="newIPs" method="post" name="newIPs" onsubmit="return validateFormInput();">
    <table>
        <tr>
            <td>*IP地址：</td>
            <td>
                <input id="ipAddr" name = "ipAddr" type="text" size="50"/>
            </td>
        </tr>

        <tr>
            <td>网络类型：</td>
            <td>
                <select id="network_type" name="network_type">
                    <option value="0" selected="">请选择</option>
                    <%
                        for(int i = 0; i < networkTypes.length; ++i){
                    %>
                    <option value="<%=networkTypes[i]%>"><%=networkTypes[i]%></option>
                    <%
                        }
                    %>
                </select>
            </td>
        </tr>

        <tr>
            <td>*掩码：</td>
            <td>
                <input id="mask" name="mask" type="text" size="50"/>
            </td>
        </tr>

        <tr>
            <td>*网关：</td>
            <td>
                <input id="gateway" name="gateway" type="text" size="50"/>
            </td>
        </tr>

        <tr>
            <td>MAC地址：</td>
            <td>
                <input id="mac" name="mac" type="text" size="50"/>
            </td>
        </tr>

        <tr>
            <td>申请时间：</td>
            <td>
                <input id="apply_date" name="apply_date" class="Wdate" type="text" onClick="WdatePicker()">
            </td>
        </tr>

        <tr>
            <td>启用时间：</td>
            <td>
                <input id="start_date" name="start_date" class="Wdate" type="text" onClick="WdatePicker()">
            </td>
        </tr>

        <tr>
            <td>设备使用人：</td>
            <td>
                <input id="users" name="users" type="text" size="50"/>
            </td>
        </tr>

        <tr>
            <td>*所属支行（分行）：</td>
            <td>
                <select id="bank" name="bank" onChange="selectDepts(this.value)">
                    <option value="0" selected="">请选择</option>
                    <%
                        for(int i = 0; i < bankNames.length; ++i){
                    %>
                    <option value="<%=bankNames[i]%>"><%=bankNames[i]%></option>
                    <%
                        }
                    %>
                </select>
            </td>
        </tr>

        <tr>
            <td>所属网点（部门）：</td>
            <td>
                <select id="dept" name="dept">
                    <option value="" selected>请选择</option>
                </select>
            </td>
        </tr>

        <tr>
            <td>设备类型：</td>
            <td>
                <input id="equip_type" name="equip_type" type="text" size="50"/>
            </td>
        </tr>

        <tr>
            <td>设备品牌：</td>
            <td>
                <input id="equip_brand" name="equip_brand" type="text" size="50"/>
            </td>
        </tr>

        <tr>
            <td>设备型号：</td>
            <td>
                <input id="model" name="model" type="text" size="50"/>
            </td>
        </tr>

        <tr>
            <td>设备用途：</td>
            <td>
                <input id="application" name="application" type="text" size="50"/>
            </td>
        </tr>

        <tr>
            <td>使用情况：</td>
            <td>
                <select id="state" name="state">
                    <option value="在用" selected="">在用</option>
                    <option value="停用">停用</option>
                </select>
            </td>
        </tr>
        <tr>
            <td>备注：</td>
            <td>
                <input id="comment" name="comment" type="text" size="100"/>
            </td>
        </tr>

        <tr>
            <td><input id="doOK" type="submit" value="确认"  href="javascript:validateFormInput()"/></td>
            <td><input id="doCancel" type="button" value="取消" onclick="cancel()"/></td>
        </tr>
    </table>
</form>

<jsp:include page="/includes/footer.jsp" flush="false" />
