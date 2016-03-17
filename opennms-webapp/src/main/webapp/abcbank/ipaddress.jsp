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

<%@ page import="java.io.*" %>
<%@ page import="java.util.Properties" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.ParseException" %>
<%@ page import="org.opennms.core.bank.BankIPAddress" %>
<%@ page import="org.opennms.core.bank.BankIPAddressOp" %>

<%
    BankIPAddressOp op = new BankIPAddressOp();

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
//    String[] bankTypes = pro.getProperty("abc-banktype").split("/");
%>

<jsp:include page="/includes/header.jsp" flush="false" >
    <jsp:param name="title" value="IP地址台帐" />
    <jsp:param name="headTitle" value="IP地址台帐" />
    <jsp:param name="breadcrumb" value="<a href='abcbank/index.jsp'>IP管理</a>" />
    <jsp:param name="breadcrumb" value="IP地址台帐" />
</jsp:include>

<script type="text/javascript" >

    function addIPAddress()
    {
        document.allIPSegments.action="abcbank/newIPAddress.jsp";
        document.allIPSegments.submit();
    }

    function stopIPAddress(id)
    {
        document.allIPSegments.action="abcbank/stopIPAddress";
        document.allIPSegments.ipAddrID.value=id;
        this.method="post";
        document.allIPSegments.submit();
    }

    function startIPAddress(id)
    {
        document.allIPSegments.action="abcbank/startIPAddress";
        document.allIPSegments.ipAddrID.value=id;
        document.allIPSegments.submit();
    }

    function modifyIPAddress(id,row)
    {
        document.allIPSegments.action="abcbank/updateIPSegment";
        document.allIPSegments.ipAddrID.value=id;
        document.allIPSegments.rowID.value=row;
        document.allIPSegments.bankName.value=document.getElementById("bank-"+row).value;
        document.allIPSegments.bankType.value=document.getElementById("equip_type-"+row).value;
        document.allIPSegments.comments.value=document.getElementById("comment-"+row).value;
        document.allIPSegments.submit();
    }

</script>

<form method="post" name="allIPSegments">
    <input type="hidden" name="ipAddrID"/>
    <input type="hidden" name="rowID"/>
    <input type="hidden" name="bankName"/>
    <input type="hidden" name="bankType"/>
    <input type="hidden" name="comments"/>

    <h3>IP地址段分配</h3>

    <a id="doNewIPSegment" href="javascript:addIPAddress()"><img src="images/add1.gif" alt="新增IP地址" border="0"></a>
    <a href="javascript:addIPAddress()">新增IP地址</a>

    <br/>
    <br/>

    <table width="100%" border="1" cellspacing="0" cellpadding="2" bordercolor="black">

        <tr bgcolor="#999999">
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
            <td width="5%"><b>使用情况</b></td>
        </tr>
        <%
            BankIPAddress[] ips = op.selectAll();
            int row = 0;
            for(BankIPAddress ip : ips){
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

                //如果停用的时间超过7天，则不显示
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                if(stop_date != null && state.contains("停用")){
                    out.print(ipId);
                    out.close();
                    try {
                        long today = sf.parse(sf.format(date)).getTime();
                        long stop = sf.parse(stop_date).getTime();
                        long inten = (today - stop) / (1000 * 60 * 60 * 24);
                        if(inten > 7)
                            continue;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
        %>
        <tr bgcolor=<%=row%2==0 ? "#ffffff" : "#cccccc"%>>
            <td width="8%" rowspan="2" align="center" style="vertical-align:middle;">
                <a id="<%= "ips("+ipId+").doStop" %>" href="javascript:stopIPAddress('<%=ipId%>')">停用</a>
                &nbsp;&nbsp;
                <a id="<%= "ips("+ipId+").doStart" %>" href="javascript:startIPAddress('<%=ipId%>')">启用</a>
                &nbsp;&nbsp;
                <a id="<%= "ips("+ipId+").doModify" %>" href="javascript:modifyIPAddress('<%=ipId%>', '<%=row%>')">变更</a>
            </td>

            <td width="5%">
                <div id="ipaddr-<%=row%>">
                    <%= ((ipaddr == null || ipaddr.equals("")) ? "&nbsp;" : ipaddr) %>
                </div>
            </td>

            <td width="5%">
                <div>
                    <select id="network_type-<%=row%>">
                        <%
                            if(network_type == null || network_type.equals(""))
                                out.print("<option value=\"0\" selected=\"\">请选择</option>");
                        %>
                        <option value="生产网" <%if(network_type.equals("生产网")) out.print("selected=\"\""); %>>生产网</option>
                        <option value="办公网" <%if(network_type.equals("办公网")) out.print("selected=\"\""); %>>办公网</option>
                        <option value="监控网" <%if(network_type.equals("监控网")) out.print("selected=\"\""); %>>监控网</option>
                        <option value="外网" <%if(network_type.equals("外网")) out.print("selected=\"\""); %>>外网</option>
                    </select>
                </div>
            </td>

            <td width="5%">
                <div id="mask-<%=row%>">
                    <%= ((mask == null || mask.equals("")) ? "&nbsp;" : mask) %>
                </div>
            </td>

            <td width="5%">
                <div id="gateway-<%=row%>">
                    <%= ((gateway == null || gateway.equals("")) ? "&nbsp;" : gateway) %>
                </div>
            </td>

            <td width="5%">
                <div id="mac-<%=row%>">
                    <%= ((mac == null || mac.equals("")) ? "&nbsp;" : mac) %>
                </div>
            </td>

            <td width="5%">
                <div id="apply_date-<%=row%>">
                    <%= ((apply_date == null || apply_date.equals("")) ? "&nbsp;" : apply_date) %>
                </div>
            </td>

            <td width="5%">
                <div id="start_date-<%=row%>">
                    <%= ((start_date == null || start_date.equals("")) ? "&nbsp;" : start_date) %>
                </div>
            </td>

            <td width="5%">
                <div id="usres-<%=row%>">
                    <%= ((usres == null || usres.equals("")) ? "&nbsp;" : usres) %>
                </div>
            </td>

            <td width="5%">
                <div>
                    <select id="bank-<%=row%>">
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

            <td width="5%">
                <div id="dept-<%=row%>">
                    <%= ((dept == null || dept.equals("")) ? "&nbsp;" : dept) %>
                </div>
            </td>

            <td width="5%">
                <div id="model-<%=row%>">
                    <%= ((model == null || model.equals("")) ? "&nbsp;" : model) %>
                </div>
            </td>

            <td width="5%">
                <div id="equip_brand-<%=row%>">
                    <%= ((equip_brand == null || equip_brand.equals("")) ? "&nbsp;" : equip_brand) %>
                </div>
            </td>

            <td width="5%">
                <div id="equip_type-<%=row%>">
                    <%= ((equip_type == null || equip_type.equals("")) ? "&nbsp;" : equip_type) %>
                </div>
            </td>

            <td width="5%">
                <div id="app-<%=row%>">
                    <%= ((app == null || app.equals("")) ? "&nbsp;" : app) %>
                </div>
            </td>

            <td width="5%">
                <div id="state-<%=row%>">
                    <%= ((state == null || state.equals("")) ? "&nbsp;" : state) %>
                </div>
            </td>
        </tr>

        <tr bgcolor=<%=row%2==0 ? "#ffffff" : "#cccccc"%>>
            <td colspan="15">
                <div>
                    <input id="comment-<%=row%>" type="text" size="100%" value="<%= ((comment == null || comment.equals("")) ? "无备注；" : comment) %>"/>
                </div>
            </td>
        </tr>
        <%
                row++;
            }
        %>
    </table>

</form>

<jsp:include page="/includes/footer.jsp" flush="false" />
