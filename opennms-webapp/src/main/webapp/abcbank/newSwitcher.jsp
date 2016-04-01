<%--
  Created by IntelliJ IDEA.
  User: laiguanhui
  Date: 2016/3/31
  Time: 16:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="java.io.*" %>
<%@ page import="java.util.Properties" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" session="true"%>

<jsp:include page="/includes/header.jsp" flush="false">
    <jsp:param name="title" value="新增交换机" />
    <jsp:param name="headTitle" value="新增交换机" />
    <jsp:param name="breadcrumb" value="<a href='admin/index.jsp'>管理</a>" />
    <jsp:param name="breadcrumb" value="<a href='abcbank/switcher.jsp'>交换机配置管理</a>" />
    <jsp:param name="breadcrumb" value="新增交换机" />
</jsp:include>

<%
    Properties pro = new Properties();
    String path = application.getRealPath("/");
    try{
        //读取配置文件
        InputStream in = new FileInputStream(path + "/abcbank/abc-configuration.properties");
        BufferedReader bf = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        pro.load(bf);
    } catch(FileNotFoundException e){
        out.println(e);
    } catch(IOException e){
        out.println(e);
    }

    //通过key获取配置文件
    String[] bankNames = pro.getProperty("abc-bankname").split("/");
    String[] bankTypes = pro.getProperty("abc-banktype").split("/");
%>

<script type="text/javascript">
    var isCommitted = false;
    function validateFormInput()
    {
        if(isCommitted == true)
            return false;
        isCommitted = true;
        var host = new String(document.newSwitcher.host.value);
        var user = new String(document.newSwitcher.user.value);
        var password = new String(document.newSwitcher.password.value);
        var backup = new String(document.newSwitcher.backup.value);
        if(host==0 || host=="") {
            alert("请填写管理IP！");
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
        }else if(backup == 0 || backup ==""){
            alert("请填写交换机的备份命令！");
            isCommitted = false;
            return false;
        }else{
            document.newSwitcher.action = "abcbank/addSwitcher";
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
    <table>
        <tr>
            <td>交换机品牌：</td>
            <td>
                <select id="brand" name="brand">
                    <option value="0" selected="">请选择</option>
                    <option value="华为">华为</option>
                    <option value="华三">华三</option>
                    <option value="思科">思科</option>
                    <option value="锐捷">锐捷</option>
                    <option value="其他">其他</option>
                </select>
            </td>
        </tr>

        <tr>
            <td>*管理IP地址：</td>
            <td>
                <input id="host" name="host" type="text" size="100" required="required"/>
            </td>
        </tr>

        <tr>
            <td>*管理员帐号：</td>
            <td>
                <input id="user" name="user" type="text" size="100" required="required"/>
            </td>
        </tr>

        <tr>
            <td>*管理员密码：</td>
            <td>
                <input id="password" name="password" type="password" size="100" required="required"/>
            </td>
        </tr>

        <tr>
            <td>*备份命令：</td>
            <td>
                <input id="backup" name="backup" type="text" size="100" required="required"/>
            </td>
        </tr>

        <tr>
            <td>恢复命令：</td>
            <td>
                <input id="recovery" name="recovery" type="text" size="100"/>
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
