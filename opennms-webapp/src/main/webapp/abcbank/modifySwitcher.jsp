<%@ page import="org.opennms.core.bank.DesUtil" %><%--
  Created by IntelliJ IDEA.
  User: laiguanhui
  Date: 2016/3/31
  Time: 16:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" session="true"%>

<jsp:include page="/includes/header.jsp" flush="false">
    <jsp:param name="title" value="修改交换机" />
    <jsp:param name="headTitle" value="修改交换机" />
    <jsp:param name="breadcrumb" value="<a href='admin/index.jsp'>管理</a>" />
    <jsp:param name="breadcrumb" value="<a href='abcbank/switcher.jsp'>交换机配置管理</a>" />
    <jsp:param name="breadcrumb" value="修改交换机" />
</jsp:include>

<%@include file="/abcbank/getVars.jsp"%>

<%
    String sid = request.getParameter("id");
    String row = request.getParameter("row");
    String name = request.getParameter("name-"+row);
    String brand = request.getParameter("brand-" + row);
    String sgroup = request.getParameter("group-" + row);
    String host = request.getParameter("host-" + row);
    String suser = request.getParameter("user-" + row);
    String password = request.getParameter("password-" + row);
    String backup = request.getParameter("backup-" + row);
    String recovery = request.getParameter("recovery-" + row);
    String wan_ip = request.getParameter("wan_ip-" + row);
    String lookback = request.getParameter("lookback-" + row);
    String vlan150_ip1 = request.getParameter("vlan150_ip1-" + row);
    String vlan150_ip2 = request.getParameter("vlan150_ip2-" + row);
    String vlan160_ip1 = request.getParameter("vlan160_ip1-" + row);
    String vlan160_ip2 = request.getParameter("vlan160_ip2-" + row);
    String vlan170_ip1 = request.getParameter("vlan170_ip1-" + row);
    String vlan170_ip2 = request.getParameter("vlan170_ip2-" + row);
    String ospf = request.getParameter("ospf-" + row);
    String area = request.getParameter("area-" + row);
    String comment = request.getParameter("comment-" + row);

    DesUtil du = new DesUtil();
    String[] passwords = du.decrypt(password).split("@pwd_split_tag@");

%>

<script type="text/javascript" src="js/abcbank.js"></script>
<script type="text/javascript">
    var isCommitted = false;
    function validateFormInput()
    {
        if(isCommitted == true)
            return false;
        isCommitted = true;
        var group = new String(document.newSwitcher.group.value);
        var name = new String(document.newSwitcher.name.value);
        var host = trimStr(new String(document.newSwitcher.host.value));
        var user = new String(document.newSwitcher.user.value);
        var password = new String(document.newSwitcher.password.value);
        var password2 = new String(document.newSwitcher.password2.value);
        var backup = new String(document.newSwitcher.backup.value);
        var wan_ip = new String(document.newSwitcher.wan_ip.value);
        var lookback_ip = trimStr(new String(document.newSwitcher.lookback_ip.value));
        var vlan150_ip1 = trimStr(new String(document.newSwitcher.vlan150_ip1.value));
        var vlan150_ip2 = trimStr(new String(document.newSwitcher.vlan150_ip2.value));
        var vlan160_ip1 = trimStr(new String(document.newSwitcher.vlan160_ip1.value));
        var vlan160_ip2 = trimStr(new String(document.newSwitcher.vlan160_ip2.value));
        var vlan170_ip1 = trimStr(new String(document.newSwitcher.vlan170_ip1.value));
        var vlan170_ip2 = trimStr(new String(document.newSwitcher.vlan170_ip2.value));

        if(name==0 || name=="") {
            alert("请填写交换机名称！");
            isCommitted = false;
            return false;
        }else if(group == 0 || group ==""){
            alert("请选择交换机分组！");
            isCommitted = false;
            return false;
        }else if(host==0 || host=="" || !judgeIP(host)) {
            alert("请填写正确的管理IP！");
            isCommitted = false;
            return false;
        }else if(user == 0 || user==""){
            alert("请填写管理员帐号！");
            isCommitted = false;
            return false;
        }else if(password == 0 || password ==""){
            alert("请填写管理员密码！");
            isCommitted = false;
            return false;
        }else if(password2 == 0 || password2 ==""){
            alert("请填写特权模式密码！");
            isCommitted = false;
            return false;
        }else if(backup == 0 || backup ==""){
            alert("请填写交换机的备份命令！");
            isCommitted = false;
            return false;
        }else if(wan_ip != "" && !judgeIPAndMask(wan_ip)){
            alert("请填写正确的广域网地址/掩码！");
            isCommitted = false;
            return false;
        }else if(lookback_ip != "" && !judgeIPAndMask(lookback_ip)){
            alert("请填写正确的Lookback地址/掩码！");
            isCommitted = false;
            return false;
        }else if(vlan150_ip1 != "" && !judgeIPAndMask(vlan150_ip1)){
            alert("请填写正确的Vlan 150地址1/掩码！");
            isCommitted = false;
            return false;
        }else if(vlan150_ip2 != "" && !judgeIPAndMask(vlan150_ip2)){
            alert("请填写正确的Vlan 150地址2/掩码！");
            isCommitted = false;
            return false;
        }else if(vlan160_ip1 != "" && !judgeIPAndMask(vlan160_ip1)){
            alert("请填写正确的Vlan 160地址1/掩码！");
            isCommitted = false;
            return false;
        }else if(vlan160_ip2 != "" && !judgeIPAndMask(vlan160_ip2)){
            alert("请填写正确的Vlan 160地址2/掩码！");
            isCommitted = false;
            return false;
        }else if(vlan170_ip1 != "" && !judgeIPAndMask(vlan170_ip1)){
            alert("请填写正确的Vlan 150地址1/掩码！");
            isCommitted = false;
            return false;
        }else if(vlan170_ip2 != "" && !judgeIPAndMask(vlan170_ip2)){
            alert("请填写正确的Vlan 170地址2/掩码！");
            isCommitted = false;
            return false;
        }else {
            document.newSwitcher.password.value = password + "@pwd_split_tag@" + password2;
            document.newSwitcher.action = "abcbank/updateSwitcher";
            document.newSwitcher.submit();
            return true;
        }
    }

    function cancel()
    {
        document.newSwitcher.action="abcbank/switcher.jsp";
        document.newSwitcher.submit();
    }

</script>

<h3>请填写以下资料</h3>

<form id="newSwitcher" method="post" name="newSwitcher" onsubmit="return validateFormInput();">
    <input type="hidden" name="sid" value="<%=sid%>">
    <table>
        <tr>
            <td>*交换机名称：</td>
            <td>
                <input id="name" name="name" type="text" size="100" value="<%=name%>" required="required"/>
            </td>
        </tr>

        <tr>
            <td>交换机品牌：</td>
            <td>
                <select id="brand" name="brand">
                    <%
                        for(String switcherBrand : switcherBrands){
                            if(switcherBrand.equals(brand)){
                    %>
                    <option value="<%=brand%>" selected=""><%=brand%></option>
                    <%
                            }else{
                    %>
                    <option value="<%=switcherBrand%>"><%=switcherBrand%></option>
                    <%
                            }
                        }
                    %>
                </select>
            </td>
        </tr>

        <tr>
            <td>*交换机分组：</td>
            <td>
                <select id="group" name="group" >
                    <%
                        for(String switcherGroup : switcherGroups){
                            if(switcherGroup.equals(sgroup)){
                    %>
                    <option value="<%=sgroup%>" selected=""><%=sgroup%></option>
                    <%
                            }else{
                    %>
                    <option value="<%=switcherGroup%>"><%=switcherGroup%></option>
                    <%
                            }
                        }
                    %>
                </select>
            </td>
        </tr>

        <tr>
            <td>*管理IP地址：</td>
            <td>
                <input id="host" name="host" type="text" size="100" value="<%=host%>" required="required"/>
            </td>
        </tr>

        <tr>
            <td>*管理员帐号：</td>
            <td>
                <input id="user" name="user" type="text" size="100" value="<%=suser%>" required="required"/>
            </td>
        </tr>

        <tr>
            <td>*管理员密码：</td>
            <td>
                <input id="password" name="password" type="password" size="100" value="<%=passwords[0]%>"/>
            </td>
        </tr>

        <tr>
            <td>*特权模式密码：</td>
            <td>
                <input id="password2" name="password2" type="password" size="100" value="<%=passwords[1]%>"/>
            </td>
        </tr>

        <tr>
            <td>*备份命令：</td>
            <td>
                <input id="backup" name="backup" type="text" size="100" value="<%=backup%>" required="required"/>
            </td>
        </tr>

        <tr>
            <td>恢复命令：</td>
            <td>
                <input id="recovery" name="recovery" type="text" size="100" value="<%=recovery%>"/>
            </td>
        </tr>

        <tr>
            <td>广域网地址/掩码：</td>
            <td>
                <input id="wan_ip" name="wan_ip" type="text" size="100" value="<%=wan_ip%>"/>
            </td>
        </tr>

        <tr>
            <td>Lookback地址/掩码：</td>
            <td>
                <input id="lookback_ip" name="lookback_ip" type="text" size="100" value="<%=lookback%>"/>
            </td>
        </tr>

        <tr>
            <td>Vlan 150地址1/掩码：</td>
            <td>
                <input id="vlan150_ip1" name="vlan150_ip1" type="text" size="100" value="<%=vlan150_ip1%>"/>
            </td>
        </tr>

        <tr>
            <td>Vlan 150地址2/掩码：</td>
            <td>
                <input id="vlan150_ip2" name="vlan150_ip2" type="text" size="100" value="<%=vlan150_ip2%>"/>
            </td>
        </tr>

        <tr>
            <td>Vlan 160地址1/掩码：</td>
            <td>
                <input id="vlan160_ip1" name="vlan160_ip1" type="text" size="100" value="<%=vlan160_ip1%>"/>
            </td>
        </tr>

        <tr>
            <td>Vlan 160地址2/掩码：</td>
            <td>
                <input id="vlan160_ip2" name="vlan160_ip2" type="text" size="100" value="<%=vlan160_ip2%>"/>
            </td>
        </tr>

        <tr>
            <td>Vlan 170地址1/掩码：</td>
            <td>
                <input id="vlan170_ip1" name="vlan170_ip1" type="text" size="100" value="<%=vlan170_ip1%>"/>
            </td>
        </tr>

        <tr>
            <td>Vlan 170地址2/掩码：</td>
            <td>
                <input id="vlan170_ip2" name="vlan170_ip2" type="text" size="100" value="<%=vlan170_ip2%>"/>
            </td>
        </tr>

        <tr>
            <td>ospf进程名：</td>
            <td>
                <input id="ospf" name="ospf" type="text" size="100" value="<%=ospf%>"/>
            </td>
        </tr>

        <tr>
            <td>area号：</td>
            <td>
                <input id="area" name="area" type="text" size="100" value="<%=area%>"/>
            </td>
        </tr>


        <tr>
            <td>备注：</td>
            <td>
                <input id="comment" name="comment" type="text" size="100" value="<%=comment%>"/>
            </td>
        </tr>

        <tr>
            <td><input id="doOK" type="submit" value="保存"  href="javascript:validateFormInput()"/></td>
            <td><input id="doCancel" type="button" value="取消" onclick="cancel()"/></td>
        </tr>
    </table>
</form>

<jsp:include page="/includes/footer.jsp" flush="false" />
