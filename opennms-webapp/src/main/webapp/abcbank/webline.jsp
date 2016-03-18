<%@ taglib prefix="html" uri="http://jakarta.apache.org/struts/tags-html" %>
<%--
  Created by IntelliJ IDEA.
  User: laiguanhui
  Date: 2016/3/17
  Time: 17:20
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
<%@ page import="org.opennms.core.bank.WebLine" %>
<%@ page import="org.opennms.core.bank.WebLineOperator" %>

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
//    String[] bankTypes = pro.getProperty("abc-banktype").split("/");
%>

<jsp:include page="/includes/header.jsp" flush="false" >
    <jsp:param name="title" value="线路台帐" />
    <jsp:param name="headTitle" value="线路台帐" />
    <jsp:param name="breadcrumb" value="<a href='abcbank/index.jsp'>IP管理</a>" />
    <jsp:param name="breadcrumb" value="线路台帐" />
</jsp:include>

<script type="text/javascript" >

    function addWebLine()
    {
        document.allWebLines.action="abcbank/newWebLine.jsp";
        document.allWebLines.submit();
    }

    function deleteWebLine(id)
    {
        document.allWebLines.action="abcbank/deleteWebLine";
        document.allWebLines.webLineID.value=id;
        document.allWebLines.submit();
    }

    function searchWebLine()
    {
        var key = document.getElementById("search").value;
        if(key==null || key==0) {
            window.location.href="abcbank/webline.jsp";
            return;
        }
        document.allWebLines.action="abcbank/searchWebLine";
        document.allWebLines.searchKey.value=key;
        document.allWebLines.submit();
    }

</script>

<form method="post" name="allWebLines">
    <input type="hidden" name="webLineID"/>
    <input type="hidden" name="rowID"/>
    <input type="hidden" name="searchKey"/>

    <h3>线路台帐</h3>
    <table>

        <td align="left">
            <a id="doNewIPSegment" href="javascript:addWebLine()"><img src="images/add1.gif" alt="新增线路" border="0"></a>
            <a href="javascript:addWebLine()">新增线路</a>
        </td>

        <td align="left">
            <input id="search" name="search" size="18%" placeholder="请输入要搜索的关键字" value="">
            <a id="doSearch" href="javascript:searchWebLine()"><img src="images/search.png" alt="搜索" border="0"></a>
            <a id="" href="javascript:searchWebLine()">搜索</a>
        </td>

        <td align="left">
            <a id="output" href="javasrcipt:outputFile()"><img src="images/output.jpg" alt="输出报表" border=""0></a>
            <a href="javasrcipt:outputFile()">输出报表</a>
        </td>
    </table>

    <table width="100%" border="1" cellspacing="0" cellpadding="2" bordercolor="black">

        <tr bgcolor="#999999">
            <td width="5%"><b>操作</b></td>
            <td width="5%"><b>专线类型</b></td>
            <td width="5%"><b>申请人</b></td>
            <td width="10%"><b>联系方式</b></td>
            <td width="5%"><b>审批人</b></td>
            <td width="8%"><b>使用机构</b></td>
            <td width="15%"><b>地址</b></td>
            <td width="5%"><b>开通日期</b></td>
            <td width="5%"><b>月租</b></td>
            <td width="5%"><b>VLAN编号</b></td>
            <td width="5%"><b>物理端口号</b></td>
            <td width="5%"><b>运营商接口号</b></td>
        </tr>
        <%
            WebLineOperator op = new WebLineOperator();
            WebLine[] lines = (WebLine[])request.getAttribute("webLines");
            if(lines == null)
                lines = op.selectAll();;

            int row = 0;
            for(WebLine line : lines){
                String lineId = line.getId();
                String type = line.getType();
                String applicant = line.getApplicant();
                String approver = line.getApprover();
                String contact = line.getContact();
                String dept = line.getDept();
                String address = line.getAddress();
                String start_date = line.getStart_date();
                String rent = line.getRent();
                String vlan_num = line.getVlan_num();
                String port = line.getPort();
                String inter = line.getInter();
                String comment = line.getComment();
        %>
        <tr bgcolor=<%=row%2==0 ? "#ffffff" : "#cccccc"%>>
            <td width="5%" rowspan="2" align="center" style="vertical-align:middle;">
                <a id="<%= "ips("+lineId+").doStop" %>" href="javascript:deleteWebLine('<%=lineId%>')" onclick="return confirm('确定要删除该专线？')">删除</a>
            </td>

            <td width="5%">
                <div id="type-<%=row%>" name="type-<%=row%>">
                    <%= ((type == null || type.equals("")) ? "&nbsp;" : type) %>
                </div>
            </td>

            <td width="5%">
                <div id="applicant-<%=row%>" name="applicant-<%=row%>">
                    <%= ((applicant == null || applicant.equals("")) ? "&nbsp;" : applicant) %>
                </div>
            </td>

            <td width="10%">
                <div id="contact-<%=row%>" name="contact-<%=row%>">
                    <%= ((contact == null || contact.equals("")) ? "&nbsp;" : contact) %>
                </div>
            </td>

            <td width="5%">
                <div id="approver-<%=row%>" name="approver-<%=row%>">
                    <%= ((approver == null || approver.equals("")) ? "&nbsp;" : approver) %>
                </div>
            </td>

            <td width="8%">
                <div id="dept-<%=row%>" name="dept-<%=row%>">
                    <%= ((dept == null || dept.equals("")) ? "&nbsp;" : dept) %>
                </div>
            </td>

            <td width="15%">
                <div id="address-<%=row%>" name="address-<%=row%>">
                    <%= ((address == null || address.equals("")) ? "&nbsp;" : address) %>
                </div>
            </td>

            <td width="5%">
                <div id="start_date-<%=row%>" name="start_date-<%=row%>">
                    <%= ((start_date == null || start_date.equals("")) ? "&nbsp;" : start_date) %>
                </div>
            </td>

            <td width="5%">
                <div id="rent-<%=row%>" name="rent-<%=row%>">
                    <%= ((rent == null || rent.equals("")) ? "&nbsp;" : rent) %>
                </div>
            </td>

            <td width="5%">
                <div id="vlan_num-<%=row%>" name="vlan_num-<%=row%>">
                    <%= ((vlan_num == null || vlan_num.equals("")) ? "&nbsp;" : vlan_num) %>
                </div>
            </td>

            <td width="5%">
                <div id="port-<%=row%>" name="port-<%=row%>">
                    <%= ((port == null || port.equals("")) ? "&nbsp;" : port) %>
                </div>
            </td>

            <td width="5%">
                <div id="inter-<%=row%>" name="inter-<%=row%>">
                    <%= ((inter == null || inter.equals("")) ? "&nbsp;" : inter) %>
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

