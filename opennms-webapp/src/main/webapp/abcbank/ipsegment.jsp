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
<%@ page import="java.io.*" %>
<%@ page import="java.util.Properties" %>

<%
    IPSegmentOperater op = new IPSegmentOperater();
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

<jsp:include page="/includes/header.jsp" flush="false" >
    <jsp:param name="title" value="IP地址段分配" />
    <jsp:param name="headTitle" value="IP地址段分配" />
    <jsp:param name="breadcrumb" value="<a href='drcbank/index.jsp'>IP管理</a>" />
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
        document.allIPSegments.rowID=row;
        document.allIPSegments.submit();
    }

</script>

<form method="post" name="allIPSegments">
    <input type="hidden" name="redirect"/>
    <input type="hidden" name="ipSegID"/>
    <input type="hidden" name="rowID"/>

    <h3>IP地址段分配</h3>

    <a id="doNewIPSegment" href="javascript:addIPSegment()"><img src="images/add1.gif" alt="新增IP段" border="0"></a>
    <a href="javascript:addIPSegment()">新增IP段</a>

    <br/>
    <br/>

    <table width="100%" border="1" cellspacing="0" cellpadding="2" bordercolor="black">

        <tr bgcolor="#999999">
            <td width="5%"><b>操作</b></td>
            <td width="10%"><b>网关</b></td>
            <td width="10%"><b>掩码</b></td>
            <td width="20%"><b>IP段</b></td>
            <td width="10%"><b>网点名称</b></td>
            <td width="5%"><b>网点类型</b></td>
            <td width="10%"><b>启用日期</b></td>
            <td width="5%"><b>使用情况</b></td>
        </tr>
        <%
            IPSegment[] ips = op.selectAll();
            int row = 0;
            for(IPSegment ip : ips){
                Integer ipId = ip.getId();
                String gateway = ip.getGateway();
                String mask = ip.getMask();
                String startIP = ip.getStartIP();
                String endIP = ip.getEndIP();
                String name = ip.getBankname();
                String type = ip.getBanktype();
                String time = ip.getCreateTime();
                String state = ip.getState();
                String comment = ip.getComment();
        %>
        <tr bgcolor=<%=row%2==0 ? "#ffffff" : "#cccccc"%>>
            <td width="7%" rowspan="2" align="center" style="vertical-align:middle;">
                <a id="<%= "ips("+ipId+").doStop" %>" href="javascript:stopIPSegment('<%=ipId%>')" onclick="return confirm('你确定要停用IP段： <%=startIP + "-" + endIP%> ?')">停用</a>
                &nbsp;&nbsp;
                <a id="<%= "ips("+ipId+").doStart" %>" href="javascript:startIPSegment('<%=ipId%>')" onclick="return confirm('你确定启要IP段： <%=startIP + "-" + endIP%> ?')">启用</a>
                &nbsp;&nbsp;
                <a id="<%= "ips("+ipId+").doModify" %>" href="javascript:modifyIPSegment('<%=ipId%>', '<%=row%>')">修改</a>
            </td>

            <td width="10%">
                <div id="gateway-<%=row%>">
                    <%= ((gateway == null || gateway.equals("")) ? "&nbsp;" : gateway) %>
                </div>
            </td>

            <td width="10%">
                <div id="mask-<%=row%>">
                    <%= ((mask == null || mask.equals("")) ? "&nbsp;" : mask) %>
                </div>
            </td>

            <td width="20%">
                <div id="ipsegment-<%=row%>">
                    <%= ((startIP == null || startIP.equals("") || endIP == null || endIP.equals("")) ? "&nbsp;" : startIP + "-" + endIP) %>
                </div>
            </td>

            <td width="10%">
                <div>
                    <select id="bankname-<%=row%>">
                        <option value="<%= ((name == null || name.equals("")) ? 0 : name) %>" selected=""><%= ((name == null || name.equals("")) ? "&nbsp;" : name) %></option>
                        <%
                            for(int i = 0; i < bankNames.length; ++i){
                        %>
                        <option value="<%=bankNames[i]%>"><%=bankNames[i]%></option>
                        <%
                            }
                        %>
                    </select>
                </div>
            </td>

            <td width="5%">
                <div>
                    <select id="banktype-<%=row%>">
                        <option value="<%= ((type == null || type.equals("")) ? 0 : type) %>" selected=""><%= ((type == null || type.equals("")) ? 0 : type) %></option>
                        <%
                            for(int i = 0; i < bankTypes.length; ++i){
                        %>
                        <option value="<%=bankTypes[i]%>"><%=bankTypes[i]%></option>
                        <%
                            }
                        %>
                    </select>

                </div>
            </td>

            <td width="10%">
                <div id="createdate-<%=row%>">
                    <%= ((time == null || time.equals("")) ? "&nbsp;" : time) %>
                </div>
            </td>

            <td width="5%">
                <div id=state-"<%=row%>">
                    <%= ((state == null || state.equals("")) ? "&nbsp;" : state) %>
                </div>
            </td>
        </tr>

        <tr bgcolor=<%=row%2==0 ? "#ffffff" : "#cccccc"%>>
            <td colspan="7">
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
