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
<%@ page import="org.opennms.netmgt.config.UserFactory" %>
<%@ page import="org.opennms.netmgt.config.UserManager" %>
<%@ page import="org.opennms.netmgt.config.users.Contact" %>
<%@ page import="org.opennms.netmgt.config.users.User" %>
<%@ page import="java.io.*" %>
<%@ page import="java.text.ParseException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Properties" %>
<%@ page import="org.opennms.core.bank.BankLogWriter3" %>


<%
    BankLogWriter3 writer3 = new BankLogWriter3();
    writer3.writeLog("writer3");

    final HttpSession userSession = request.getSession(false);
    User user;
    String userID = request.getRemoteUser();
    UserManager userFactory;
    String group="";
    if (userSession != null) {
        UserFactory.init();
        userFactory = UserFactory.getInstance();
        Map users = userFactory.getUsers();
        user = (User) users.get(userID);
        Contact[] con = user.getContact();
        for(Contact c : con) {
            if (c.getType() != null && c.getType().equals("textPage")) {
                group = c.getServiceProvider(); // 获取该用户所属分行
                break;
            }
        }
    }

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

    IPSegmentOperater op = new IPSegmentOperater();

    IPSegment[] ips = (IPSegment[])request.getAttribute("ipSeg");
    if(ips == null)
        ips = op.selectAll("");
    int nums = ips.length;
    String[] ipSegs = op.getIPSegments();
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

    function stopIPSegment(id)
    {
        document.allIPSegments.action="abcbank/stopIPSegment";
        document.allIPSegments.ipSegID.value=id;
        this.method="post";
        document.allIPSegments.submit();
    }

    function startIPSegment(id)
    {
        document.allIPSegments.action="abcbank/startIPSegment";
        document.allIPSegments.ipSegID.value=id;
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
        var ipSeg = document.getElementById("search").value;
        if(ipSeg==null || ipSeg==0) {
            window.location.href="abcbank/ipsegment.jsp";
            return;
        }
        document.allIPSegments.action="abcbank/searchIPSegment";
        document.allIPSegments.submit();
    }

</script>

<form method="post" name="allIPSegments">
    <input type="hidden" name="ipSegID"/>
    <input type="hidden" name="rowID"/>
    <input type="hidden" name="bankName"/>
    <input type="hidden" name="bankType"/>
    <input type="hidden" name="comments"/>
    <input type="hidden" name="rows"/>

    <h3>IP地址段分配</h3>

    <table>
    <td align="left">
        <a id="doNewIPSegment" href="javascript:addIPSegment()"><img src="images/add1.gif" alt="新增IP段" border="0"></a>
        <a href="javascript:addIPSegment()">新增IP段</a>
    </td>

    <td align="left">
        <select id="search" name="search">
            <option value="">请选择需要筛选的IP段</option>
            <%
                for(String seg : ipSegs){
            %>
            <option value="<%=seg%>"><%=seg%></option>
            <%
                }
            %>
        </select>
        <a id="doSearch" href="javascript:searchIPSegment()"><img src="images/search.png" alt="筛选" border="0"></a>
        <a id="" href="javascript:searchIPSegment()">筛选</a>
    </td>

    <td align="left">
        <a id="output" href="javascript:outputExcel(<%=nums%>)"><img src="images/output.jpg" alt="输出报表" border=""0></a>
        <a href="javascript:outputExcel(<%=nums%>)">输出报表</a>
    </td>
    </table>

    <table width="100%" border="1" cellspacing="0" cellpadding="2" bordercolor="black">

        <tr bgcolor="#999999">
            <td width="8%"><b>操作</b></td>
            <td width="10"><b>IP段</b></td>
            <td width="10%"><b>网关</b></td>
            <td width="10%"><b>掩码</b></td>
            <td width="20%"><b>IP段</b></td>
            <td width="10%"><b>网点名称</b></td>
            <td width="5%"><b>网点类型</b></td>
            <td width="10%"><b>启用日期</b></td>
            <td width="5%"><b>使用情况</b></td>
        </tr>
        <%
            int row = 0;
            for(IPSegment ip : ips){
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

                //如果停用的时间超过7天，则不显示
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                if(stopTime != null && state.equals("停用")){
                    try {
                        long today = sf.parse(sf.format(date)).getTime();
                        long stop = sf.parse(stopTime).getTime();
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
                <a id="<%= "ips("+ipId+").doStop" %>" href="javascript:stopIPSegment('<%=ipId%>')">停用</a>
                &nbsp;&nbsp;
                <a id="<%= "ips("+ipId+").doStart" %>" href="javascript:startIPSegment('<%=ipId%>')">启用</a>
                &nbsp;&nbsp;
                <a id="<%= "ips("+ipId+").doModify" %>" href="javascript:modifyIPSegment('<%=ipId%>', '<%=row%>')">修改</a>
            </td>

            <input type="hidden" name="id-<%=row%>" value="<%=ipId %>"/>

            <td width="10%">
                <div id="ipSeg-<%=row%>">
                    <%= ((ipSeg == null || ipSeg.equals("")) ? "&nbsp;" : ipSeg) %>
                    <input type="hidden" name="ipSeg-<%=row%>" value="<%= ((ipSeg == null || ipSeg.equals("")) ? "&nbsp;" : ipSeg) %>"/>
                </div>
            </td>

            <td width="10%">
                <div id="gateway-<%=row%>">
                    <%= ((gateway == null || gateway.equals("")) ? "&nbsp;" : gateway) %>
                    <input type="hidden" name="gateway-<%=row%>" value="<%= ((gateway == null || gateway.equals("")) ? "&nbsp;" : gateway) %>"/>
                </div>
            </td>

            <td width="10%">
                <div id="mask-<%=row%>">
                    <%= ((mask == null || mask.equals("")) ? "&nbsp;" : mask) %>
                    <input type="hidden" name="mask-<%=row%>" value="<%= ((mask == null || mask.equals("")) ? "&nbsp;" : mask) %>"/>
                </div>
            </td>

            <td width="20%">
                <div id="ipsegment-<%=row%>">
                    <%= ((startIP == null || startIP.equals("") || endIP == null || endIP.equals("")) ? "&nbsp;" : startIP + "-" + endIP) %>
                    <input type="hidden" name="startIP-<%=row%>" value="<%= ((startIP == null || startIP.equals("")) ? "&nbsp;" : startIP) %>"/>
                    <input type="hidden" name="endIP-<%=row%>" value="<%= ((endIP == null || endIP.equals("")) ? "&nbsp;" : endIP) %>"/>
                </div>
            </td>

            <td width="10%">
                <div>
                    <select id="bankname-<%=row%>" name="bankname-<%=row%>">
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

            <td width="5%">
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

            <td width="10%">
                <div id="createdate-<%=row%>">
                    <%= ((time == null || time.equals("")) ? "&nbsp;" : time) %>
                    <input type="hidden" name="createdate-<%=row%>" value="<%= ((time == null || time.equals("")) ? "&nbsp;" : time) %>"/>
                </div>
            </td>

            <td width="5%">
                <div id=state-"<%=row%>">
                    <%= ((state == null || state.equals("")) ? "&nbsp;" : state) %>
                    <input type="hidden" name="state-<%=row%>" value="<%= ((state == null || state.equals("")) ? "&nbsp;" : state) %>"/>
                </div>
            </td>
        </tr>

        <tr bgcolor=<%=row%2==0 ? "#ffffff" : "#cccccc"%>>
            <td colspan="8">
                <div>
                    <input id="comment-<%=row%>" type="text" size="100" value="<%= ((comment == null || comment.equals("")) ? "无备注；" : comment) %>"/>
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
