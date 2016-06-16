<%

%>
<script>
    function batchOperator(){
        document.snmpConfigForm.batchComm.value="192.168.1.196,,public,,v2c,,\n";
        document.snmpConfigForm.action = "admin/batchSnmpConfig";
    }
</script>

<form method="post" name="snmpConfigForm" onsubmit="return batchOperator();">
    <table width="100%" border="1" cellspacing="0" cellpadding="2" bordercolor="black">

        <tr bgcolor="#FF0000">
            <td width="7%"><b>操作</b></td>
            <td width="5%"><b>ip地址</b></td>
            <td width="5%"><b>网络类型</b></td>
            <td width="5%"><b>掩码</b></td>
            <td width="5%"><b>网关</b></td>
            <td width="5%"><b>mac地址</b></td>
            <td width="5%"><b>申请时间</b></td>
            <td width="5%"><b>启用日期</b></td>
            <td width="5%"><b>设备使用人</b></td>
            <td width="5%"><b>所属支行（分行）</b></td>
            <td width="5%"><b>所属网点（部门）</b></td>
            <td width="5%"><b>设备类型</b></td>
            <td width="5%"><b>设备品牌</b></td>
            <td width="5%"><b>设备型号</b></td>
            <td width="5%"><b>用途</b></td>
            <td width="3%"><b>使用情况</b></td>
        </tr>
        </table>
</form>