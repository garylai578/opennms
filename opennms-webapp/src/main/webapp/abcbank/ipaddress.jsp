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

<%@ page import="org.opennms.core.bank.BankIPAddress" %>
<%@ page import="org.opennms.core.bank.BankIPAddressOp" %>
<%@ page import="org.opennms.web.springframework.security.Authentication" %>
<%@ page import="java.text.ParseException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.opennms.netmgt.config.UserFactory" %>
<%@ page import="org.opennms.netmgt.config.UserManager" %>
<%@ page import="org.opennms.netmgt.config.users.Contact" %>
<%@ page import="org.opennms.netmgt.config.users.User" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Properties" %>

<%
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
    String[] networkTypes = pro.getProperty("abc-networktype").split("/");

    BankIPAddressOp op = new BankIPAddressOp();

    BankIPAddress[] ips = (BankIPAddress[])request.getAttribute("ip_addresses");
    if(ips == null){
        if(request.isUserInRole(Authentication.ROLE_ADMIN))
            ips = op.selectAll("");
        else
            ips = op.selectAll(group);
    }
    int nums = ips.length;
%>

<jsp:include page="/includes/header.jsp" flush="false" >
    <jsp:param name="title" value="IP地址台帐" />
    <jsp:param name="headTitle" value="IP地址台帐" />
    <jsp:param name="breadcrumb" value="<a href='abcbank/index.jsp'>台帐管理</a>" />
    <jsp:param name="breadcrumb" value="IP地址台帐" />
</jsp:include>

<script type="text/javascript" >

    function addIPAddress()
    {
        document.allIPSegments.action="abcbank/newIPAddress.jsp";
        document.allIPSegments.submit();
    }

    function stopIPAddress(id, row)
    {
        document.allIPSegments.action="abcbank/stopIPAddress";
        document.allIPSegments.ipAddrID.value=id;
        document.allIPSegments.rowID.value=row;
        this.method="post";
        document.allIPSegments.submit();
    }

    function startIPAddress(id, row)
    {
        document.allIPSegments.action="abcbank/startIPAddress";
        document.allIPSegments.ipAddrID.value=id;
        document.allIPSegments.rowID.value=row;
        document.allIPSegments.submit();
    }

    function modifyIPAddress(id,row)
    {
        document.allIPSegments.action="abcbank/updateIPAddress";
        document.allIPSegments.ipAddrID.value=id;
        document.allIPSegments.rowID.value=row;
        document.allIPSegments.submit();
    }

    function searchIPAddress()
    {
        document.allIPSegments.action="abcbank/searchIPAddress";
        document.allIPSegments.submit();
    }

    function outputExcel(row){
        document.allIPSegments.action="abcbank/exportIPAddress";
        document.allIPSegments.rows.value=row;
        document.allIPSegments.submit();
    }

</script>

<form method="post" name="allIPSegments">
    <input type="hidden" name="ipAddrID"/>
    <input type="hidden" name="rowID"/>
    <input type="hidden" name="rows"/>
    <input type="hidden" name="searchAreas" />

    <h3>IP地址台帐管理</h3>
    <table>

    <td align="left">
        <a id="doNewIPSegment" href="javascript:addIPAddress()"><img src="images/add1.gif" alt="新增IP地址" border="0"></a>
        <a href="javascript:addIPAddress()">新增IP地址</a>
    </td>

    <td align="left">&nbsp;&nbsp;
        <strong>网络类型：</strong><select id="network_type" name="network_type">
                    <option value="" selected="">请选择</option>
                    <%
                        for(int i = 0; i < networkTypes.length; ++i){
                    %>
                    <option value="<%=networkTypes[i]%>"> <%=networkTypes[i]%></option>
                    <%
                        }
                    %>
                 </select>&nbsp;&nbsp;
        <strong>设备使用人:</strong><input id="users" name="users" size="12" value="">&nbsp;&nbsp;
        <strong>所属支行（分行）：</strong><select id="bank" name="bank">
                            <option value="" selected="">请选择</option>
                            <%
                                for(int i = 0; i < bankNames.length; ++i){
                            %>
                            <option value="<%=bankNames[i]%>"><%=bankNames[i]%></option>
                            <%
                                }
                            %>
                        </select>&nbsp;&nbsp;
        <strong>所属网点（部门）：</strong><input id="dept" name="dept" size="12" value="">&nbsp;&nbsp;
<%--        <input id="searchArea-0" type="checkbox" value="network_type" checked>网络类型
        <input id="searchArea-1" type="checkbox" value="users" checked>设备使用人
        <input id="searchArea-2" type="checkbox" value="bank" checked>所属支行（分行）
        <input id="searchArea-3" type="checkbox" value="dept" checked>所属网点（部门）&nbsp;--%>
        <a id="doSearch" href="javascript:searchIPAddress()"><img src="images/search.png" alt="搜索" border="0"></a>
        <a id="" href="javascript:searchIPAddress()">搜索</a>
    </td>

        <td align="left">
            <a id="output" href="javascript:outputExcel(<%=nums%>)"><img src="images/output.jpg" alt="输出报表" border=""0></a>
            <a href="javascript:outputExcel(<%=nums%>)">输出报表</a>
        </td>
    </table>

    <div style="overflow: auto; width: 100%;">
    <table width="100%" border="1" cellspacing="0" cellpadding="2" bordercolor="black" class="tab_css_1">

        <tr class="header1">
            <td style="width: 100px"><b>操作</b></td>
            <td><b>ip地址</b></td>
            <td><b>网络类型</b></td>
            <td style="width: 100px"><b>掩码</b></td>
            <td><b>网关</b></td>
            <td><b>mac地址</b></td>
            <td><b>申请时间</b></td>
            <td><b>启用日期</b></td>
            <td><b>设备使用人</b></td>
            <td><b>所属支行（分行）</b></td>
            <td><b>所属网点（部门）</b></td>
            <td><b>设备类型</b></td>
            <td><b>设备品牌</b></td>
            <td><b>设备型号</b></td>
            <td><b>用途</b></td>
            <td><b>使用情况</b></td>
            <td><b>备注</b></td>
        </tr>
        <%
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
        <tr <%if (state.equals("停用")) out.print("class=\"lineUnused\"");%>>
            <td align="center" style="vertical-align:middle;">
                <a id="<%= "ips("+ipId+").doStop" %>" href="javascript:stopIPAddress('<%=ipId%>', '<%=row%>')" onclick="return confirm('确定要停用该IP？')">停用</a>
                &nbsp;&nbsp;
                <a id="<%= "ips("+ipId+").doStart" %>" href="javascript:startIPAddress('<%=ipId%>', '<%=row%>')">启用</a>
                &nbsp;&nbsp;
                <a id="<%= "ips("+ipId+").doModify" %>" href="javascript:modifyIPAddress('<%=ipId%>', '<%=row%>')">变更</a>
            </td>

            <input type="hidden" name="id-<%=row%>" value="<%=ipId %>"/>

            <td>
                <div id="ipaddr-<%=row%>">
                    <%= ((ipaddr == null || ipaddr.equals("")) ? "&nbsp;" : ipaddr) %>
                    <input type="hidden" name="ipaddr-<%=row%>" value="<%= ((ipaddr == null || ipaddr.equals("")) ? "&nbsp;" : ipaddr) %>"/>
                </div>
            </td>

            <td>
                <div>
                    <select id="network_type-<%=row%>" name="network_type-<%=row%>">
                        <%
                            if(network_type == null || network_type.equals(""))
                                out.print("<option value=\"0\" selected=\"\">请选择</option>");
                        %>
                        <%
                            for(int i = 0; i < networkTypes.length; ++i){
                        %>
                        <option value="<%=networkTypes[i]%>" <%if(network_type.equals(networkTypes[i])) out.print("selected=\"\"");%>><%=networkTypes[i]%></option>
                        <%
                            }
                        %>
                    </select>
                </div>
            </td>

            <td>
                <div id="mask-<%=row%>">
                    <%= ((mask == null || mask.equals("")) ? "&nbsp;" : mask) %>
                    <input type="hidden" name="mask-<%=row%>" value="<%= ((mask == null || mask.equals("")) ? "&nbsp;" : mask) %>"/>
                </div>
            </td>

            <td>
                <div id="gateway-<%=row%>">
                    <%= ((gateway == null || gateway.equals("")) ? "&nbsp;" : gateway) %>
                    <input type="hidden" name="gateway-<%=row%>" value="<%= ((gateway == null || gateway.equals("")) ? "&nbsp;" : gateway) %>"/>
                </div>
            </td>

            <td>
                <div id="mac-<%=row%>">
                    <%= ((mac == null || mac.equals("")) ? "&nbsp;" : mac) %>
                    <input type="hidden" name="mac-<%=row%>" value="<%= ((mac == null || mac.equals("")) ? "&nbsp;" : mac) %>"/>
                </div>
            </td>

            <td>
                <div id="apply_date-<%=row%>">
                    <%= ((apply_date == null || apply_date.equals("")) ? "&nbsp;" : apply_date) %>
                    <input type="hidden" name="apply_date-<%=row%>" value="<%= ((apply_date == null || apply_date.equals("")) ? "&nbsp;" : apply_date) %>"/>
                </div>
            </td>

            <td>
                <div id="start_date-<%=row%>">
                    <%= ((start_date == null || start_date.equals("")) ? "&nbsp;" : start_date) %>
                    <input type="hidden" name="start_date-<%=row%>" value="<%= ((start_date == null || start_date.equals("")) ? "&nbsp;" : start_date) %>"/>
                </div>
            </td>

            <td>
                <div>
                    <input  id="users-<%=row%>" size="5" name="users-<%=row%>" value="<%= ((usres == null || usres.equals("")) ? "&nbsp;" : usres) %>" >
                </div>
            </td>

            <td>
                <div>
                    <select id="bank-<%=row%>" name="bank-<%=row%>">
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
                <div>
                    <input id="dept-<%=row%>" name="dept-<%=row%>" size="6" value="<%= ((dept == null || dept.equals("")) ? "&nbsp;" : dept) %>">
                </div>
            </td>

            <td>
                <div id="model-<%=row%>">
                    <input id="model-<%=row%>" name="model-<%=row%>" size="6" value="<%= ((model == null || model.equals("")) ? "&nbsp;" : model) %>">
                </div>
            </td>

            <td>
                <div>
                    <input id="equip_brand-<%=row%>" name="equip_brand-<%=row%>" size="5" value="<%= ((equip_brand == null || equip_brand.equals("")) ? "&nbsp;" : equip_brand) %>">
                </div>
            </td>

            <td>
                <div>
                    <input id="equip_type-<%=row%>" name="equip_type-<%=row%>" size="6" value="<%= ((equip_type == null || equip_type.equals("")) ? "&nbsp;" : equip_type) %>">
                </div>
            </td>

            <td>
                <div>
                    <input id="app-<%=row%>" name="app-<%=row%>" size="6" value="<%= ((app == null || app.equals("")) ? "&nbsp;" : app) %>">
                </div>
            </td>

            <td>
                <div id="state-<%=row%>">
                    <%= ((state == null || state.equals("")) ? "&nbsp;" : state) %>
                    <input type="hidden" name="state-<%=row%>" value="<%= ((state == null || state.equals("")) ? "&nbsp;" : state) %>"/>
                </div>
            </td>

            <td>
                <div>
                    <input id="comment-<%=row%>" name="comment-<%=row%>" size="8" type="text" value="<%= ((comment == null || comment.equals("")) ? "无备注；" : comment) %>"/>
                </div>
            </td>
        </tr>
        <%
                row++;
            }
        %>
    </table>
</div>
</form>

<jsp:include page="/includes/footer.jsp" flush="false" />
