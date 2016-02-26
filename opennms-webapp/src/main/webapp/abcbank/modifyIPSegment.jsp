<%--
  Created by IntelliJ IDEA.
  User: laiguanhui
  Date: 2016/2/22
  Time: 8:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" session="true"%>

<jsp:include page="/includes/header.jsp" flush="false">
    <jsp:param name="title" value="修改IP段" />
    <jsp:param name="headTitle" value="修改IP段" />
    <jsp:param name="breadcrumb" value="<a href='abcbank/index.jsp'>IP管理</a>" />
    <jsp:param name="breadcrumb" value="<a href='abcbank/ipsegment.jsp'>IP地址段分配</a>" />
    <jsp:param name="breadcrumb" value="修改IP段" />
</jsp:include>

<script type="text/javascript">
    var isCommitted = false;
    function validateFormInput()
    {
        if(isCommitted == true)
            return false;
        isCommitted = true;
        var num = new String(document.newIPs.ip_num.value);
        var name = new String(document.newIPs.bank_name.value);
        var type = new String(document.newIPs.bank_type.value);
        if(num==0) {
            alert("请选择所需的IP数量！");
            return false;
        }else if(name == 0){
            alert("请选择所属网点！");
            return false;
        }else if(type == 0){
            alert("请选择网点所属类型！");
            return false;
        }else{
            document.newIPs.action = "abcbank/modifyIPSegment";
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
    <table>
        <tr>
            <td>所需IP数量：</td>
            <td>
                <select id="ip_num">
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
            <td>网点名称：</td>
            <td>
                <select id="bank_name">
                    <option value="0" selected="">请选择</option>
                    <option value="东莞分行">东莞分行</option>
                    <option value="莞城支行">莞城支行</option>
                    <option value="南城支行">南城支行</option>
                    <option value="东城支行">东城支行</option>
                    <option value="万江支行">万江支行</option>
                </select>
            </td>
        </tr>

        <tr>
            <td>网点类型：</td>
            <td>
                <select id="bank_type">
                    <option value="0" selected="">请选择</option>
                    <option value="网点">网点</option>
                    <option value="离行点">离行点</option>
                </select>
            </td>
        </tr>

        <tr>
            <td>备注：</td>
            <td>
                <input id="comment" type="text" size="100"/>
            </td>
        </tr>

        <tr>
            <td><input id="doOK" type="submit" value="确认"/></td>
            <td><input id="doCancel" type="button" value="取消" onclick="cancel()"/></td>
        </tr>
    </table>
</form>

<jsp:include page="/includes/footer.jsp" flush="false" />
