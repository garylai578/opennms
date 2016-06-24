<%@ page import="org.opennms.core.bank.IPSegmentOperater" %><%--
  Created by IntelliJ IDEA.
  User: laiguanhui
  Date: 2016/2/22
  Time: 8:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" session="true"%>

<jsp:include page="/includes/header.jsp" flush="false">
    <jsp:param name="title" value="新增IP段" />
    <jsp:param name="headTitle" value="新增IP段" />
    <jsp:param name="breadcrumb" value="<a href='abcbank/index.jsp'>IP管理</a>" />
    <jsp:param name="breadcrumb" value="<a href='abcbank/ipsegment.jsp'>IP地址段分配</a>" />
    <jsp:param name="breadcrumb" value="新增IP段" />
</jsp:include>

<%@include file="/abcbank/getVars.jsp"%>

<%
    IPSegmentOperater operater = new IPSegmentOperater();
    String[] ipsegs = operater.getIPSegments();
%>
<script type='text/javascript' src='js/ipv6/ipv6.js'></script>

<script>
    var Select = {
        del : function(obj,e){
            if((e.keyCode||e.which||e.charCode) == 8){
                var opt = obj.options[0];
                opt.text = opt.value = opt.value.substring(0, opt.value.length>0?opt.value.length-1:0);
            }
        },
        write : function(obj,e){
            if((e.keyCode||e.which||e.charCode) == 8)return ;
            var opt = obj.options[0];
            opt.selected = "selected";
            opt.text = opt.value += String.fromCharCode(e.charCode||e.which||e.keyCode);
        }
    }
    function test(){
        alert(document.getElementById("select").value);
    }
</script>

<script type="text/javascript">
    var isCommitted = false;
    function validateFormInput()
    {
        if(isCommitted == true)
            return false;
        isCommitted = true;
        var seg = new String(document.newIPs.selectIpSeg.value);
        var startIP = new String(document.newIPs.startIP.value);
        var endIP = new String(document.newIPs.endIP.value);
        var num = new String(document.newIPs.ipNum.value);
        var name = new String(document.newIPs.bankName.value);
        var type = new String(document.newIPs.bankType.value);
        if(seg==null || seg==0){
            if(startIP == null || startIP == 0 || endIP == null || endIP == 0) {
                alert("请选择或输入所属的IP段");
                isCommitted = false;
                return false;
            }else{
                if(!isValidIPAddress(startIP) || !isValidIPAddress(endIP)){
                    alert("输入的IP段格式不正确");
                    isCommitted = false;
                    return false;
                }else{
                    document.newIPs.ipSeg.value = startIP + "-" + endIP;
                }
            }
        }else{
            document.newIPs.ipSeg.value = seg;
        }

        if(num==0) {
            alert("请选择所需的IP数量！");
            isCommitted = false;
            return false;
        }else if(name == 0){
            alert("请选择所属网点！");
            isCommitted = false;
            return false;
        }else if(type == 0){
            alert("请选择网点所属类型！");
            isCommitted = false;
            return false;
        }else{
            document.newIPs.action = "abcbank/newIPSegment";
            document.newIPs.submit();
            return true;
        }
    }

    function cancel()
    {
        document.newIPs.action="abcbank/ipsegment.jsp";
        document.newIPs.submit();
    }

</script>

<h3>请填写以下资料</h3>

<form id="newIPs" method="post" name="newIPs" onsubmit="return validateFormInput();">
    <input name="ipSeg" type="hidden"/>
    <table>
        <tr>
            <td>*所属IP段：</td>
            <td>
                <select id="selectIpSeg" name="selectIpSeg">
                    <option value="">请选择</option>
                    <%
                        for(String seg : ipsegs){
                    %>
                    <option value="<%=seg%>"><%=seg%></option>
                    <%
                        }
                    %>
                </select>&nbsp;或直接输入：
                <input id="startIP" name="startIP" type="text" size="15" placeholder="开始IP" />-
                <input id="endIP" name="endIP" type="text" size="15" placeholder="结束IP"/>-
                (若同时选择和输入，以前者为准。)
            </td>
        </tr>

        <tr>
            <td>*所需IP数量：</td>
            <td>
                <select id="ipNum" name="ipNum">
                    <option value="0" selected="">0</option>
                    <option value="2">2</option>
                    <option value="4">4</option>
                    <option value="8">8</option>
                    <option value="16">16</option>
                    <option value="32">32</option>
                    <option value="64">64</option>
                    <option value="128">128</option>
                </select>个
            </td>
        </tr>

        <tr>
            <td>*所属支行（分行）：</td>
            <td>
                <select id="bank" name="bankName" onChange="selectDepts(this.value, 'dept')">
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
                <select id="dept" name="deptName">
                    <option value="" selected>请选择</option>
                </select>
            </td>
        </tr>

        <tr>
            <td>*网点类型：</td>
            <td>
                <select id="bankType" name="bankType">
                    <option value="0" selected="">请选择</option>
                    <%
                        for(int i = 0; i < bankTypes.length; ++i){
                    %>
                    <option value="<%=bankTypes[i]%>"><%=bankTypes[i]%></option>
                    <%
                        }
                    %>
                </select>
            </td>
        </tr>

        <tr>
            <td>备注：</td>
            <td>
                <input id="comments" name="comments" type="text" size="100"/>
            </td>
        </tr>

        <tr>
            <td><input id="doOK" type="submit" value="确认"  href="javascript:validateFormInput()"/></td>
            <td><input id="doCancel" type="button" value="取消" onclick="cancel()"/></td>
        </tr>
    </table>
</form>

<jsp:include page="/includes/footer.jsp" flush="false" />
