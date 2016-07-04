<%--
  Created by IntelliJ IDEA.
  User: laiguanhui
  Date: 2016/3/18
  Time: 9:56
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" session="true"%>

<script language="javascript" type="text/javascript" src="/opennms/js/My97DatePicker/WdatePicker.js"></script>

<jsp:include page="/includes/header.jsp" flush="false">
    <jsp:param name="title" value="新增专线" />
    <jsp:param name="headTitle" value="新增专线" />
    <jsp:param name="breadcrumb" value="<a href='abcbank/index.jsp'>IP管理</a>" />
    <jsp:param name="breadcrumb" value="<a href='abcbank/webline.jsp'>线路台帐</a>" />
    <jsp:param name="breadcrumb" value="新增专线" />
</jsp:include>

<script type='text/javascript' src='js/ipv6/ipv6.js'></script>

<%@include file="/abcbank/getVars.jsp"%>

<script type="text/javascript">
    var isCommitted = false;
    function validateFormInput()
    {
        if(isCommitted == true)
            return false;
        isCommitted = true;
        var ip = new String(document.newWebLine.ip.value);
        var type = new String(document.newWebLine.type.value);
        var applicant = new String(document.newWebLine.applicant.value);
        var contact = new String(document.newWebLine.contact.value);
        var bank = new String(document.newWebLine.bank.value);
        var address = new String(document.newWebLine.address.value);
        var approver = new String(document.newWebLine.approver.value);

        if(ip != null  && ip != "" && !isValidIPAddress(ip)){
            alert("IP地址无效：" + ip);
            isCommitted = false;
            return false;
        }else if(type== 0 || type=="") {
            alert("请选择专线类型！");
            isCommitted = false;
            return false;
        }else if(applicant == 0 || applicant==""){
            alert("请填写申请人！");
            isCommitted = false;
            return false;
        }else if(contact == 0 || contact ==""){
            alert("请填写联系方式！");
            isCommitted = false;
            return false;
        }else if(approver == 0 || approver ==""){
            alert("请填写审批人！");
            isCommitted = false;
            return false;
        }else if(bank == 0 || bank ==""){
            alert("请选择所属分行（支行）！");
            isCommitted = false;
            return false;
        }else if(address == 0 || address ==""){
            alert("请填写地址！");
            isCommitted = false;
            return false;
        }else{
            document.newWebLine.action = "abcbank/newWebLine";
            return true;
        }
    }

    function cancel()
    {
        document.newWebLine.action="abcbank/webline.jsp";
        document.newWebLine.submit();
    }

    function checkNum(obj) {
        //检查是否是非数字值
        var flag = 0;
        if (isNaN(obj.value)) {

            flag = 1;
        }
        if (obj != null) {
            //检查小数点后是否对于两位
            if (obj.value.toString().split(".").length > 1 && obj.value.toString().split(".")[1].length > 2) {
                flag = 1;
            }
        }
        if (flag) {
            alert("月租:"+obj.value+" 格式有误，请重新输入！");
            obj.value = "";
        }
    }

</script>

<h3>请填写以下资料</h3>

<form id="newIPs" method="post" name="newWebLine" onsubmit="return validateFormInput();">
    <input type="hidden" name="ipNum"/>
    <input type="hidden" name="bankName"/>
    <input type="hidden" name="bankType"/>
    <input type="hidden" name="comments"/>
    <input type="hidden" name="group" value="<%=group%>"/>
    <input type="hidden" name="state" value="待审批"/>
    <table>
        <tr>
            <td>专线IP：</td>
            <td>
                <input id="ip" name="ip" type="text" size="100"/>
            </td>
        </tr>

        <tr>
            <td>*专线类型：</td>
            <td>
                <select id="type" name="type">
                    <option value="0" selected="">请选择</option>
                    <%
                        for(String webline : weblineTypes){
                    %>
                    <option value="<%=webline%>"><%=webline%></option>
                    <%
                        }
                    %>
                </select>
            </td>
        </tr>

        <tr>
            <td>*申请人：</td>
            <td>
                <input id="applicant" name="applicant" type="text" size="100"/>
            </td>
        </tr>

        <tr>
            <td>*联系方式：</td>
            <td>
                <input id="contact" name="contact" type="text" size="100"/>
            </td>
        </tr>

        <tr>
            <td>*审批人：</td>
            <td>
                <input id="approver" name="approver" type="text" size="100"/>
            </td>
        </tr>

        <tr>
            <td>*所属支行（分行）：</td>
            <td>
                <select id="bank" name="bank" onChange="selectDepts(this.value, 'dept')">
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
            <td>*地址：</td>
            <td>
                <input id="address" name="address" type="text" size="100"/>
            </td>
        </tr>

        <tr>
            <td>开通日期：</td>
            <td>
                <input id="start_date" name="start_date" class="Wdate" type="text" onClick="WdatePicker()" size="100"/>
            </td>
        </tr>

        <tr>
            <td>月租：</td>
            <td>
                <input id="rent" name="rent" type="text" size="100" onkeyup="checkNum(this)" value="0"/>
            </td>
        </tr>

        <tr>
            <td>VLAN编号：</td>
            <td>
                <input id="vlan_num" name="vlan_num" type="text" size="100"/>
            </td>
        </tr>

        <tr>
            <td>物理端口号：</td>
            <td>
                <input id="port" name="port" type="text" size="100"/>
            </td>
        </tr>

        <tr>
            <td>运营商接口号：</td>
            <td>
                <input id="inter" name="inter" type="text" size="100"/>
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

