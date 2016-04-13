<%--
  Created by IntelliJ IDEA.
  User: laiguanhui
  Date: 2016/2/16
  Time: 14:56
  To change this template use File | Settings | File Templates.
--%>

<%@page language="java"
        contentType="text/html;charset=UTF-8"
        session="true"
%>

<%@page import="org.opennms.core.bank.Switcher" %>
<%@ page import="org.opennms.core.bank.SwitcherOperator" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.Properties" %>

<jsp:include page="/includes/header.jsp" flush="false">
    <jsp:param name="title" value="交换机配置管理" />
    <jsp:param name="headTitle" value="交换机配置管理" />
    <jsp:param name="breadcrumb" value="<a href='admin/index.jsp'>管理</a>" />
    <jsp:param name="breadcrumb" value="交换机配置管理" />
</jsp:include>

<%
    SwitcherOperator op = new SwitcherOperator();

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

<script type="text/javascript" >

    function addSwitcher()
    {
        document.allSwitchers.action="abcbank/newSwitcher.jsp";
        document.allSwitchers.submit();
    }

    function deleteSwitcher(id)
    {
        document.allSwitchers.action="abcbank/deleteSwitcher";
        document.allSwitchers.switcherId.value=id;
        document.allSwitchers.submit();
    }

    function recoverySwitcher(rowID)
    {

        var value = document.getElementById("recovery-"+rowID).getAttribute("value");

        if(value==0 || value=="")
                alert("备份命令为空！");
        else {
            document.allSwitchers.action = "abcbank/recoverySwitcher";
            document.allSwitchers.rowID.value = rowID;
            document.allSwitchers.submit();
        }
    }

    function backupSwitcher(rowID)
    {
        document.allSwitchers.action="abcbank/backupSwitcher";
        document.allSwitchers.rowID.value=rowID;
        document.allSwitchers.submit();
    }

    function backupSwitcherCycle(rowID)
    {
        document.allSwitchers.action="abcbank/backupSwitcher";
        document.allSwitchers.rowID.value=rowID;
        document.allSwitchers.isCycle.value=1;
        document.allSwitchers.submit();
    }

    function managePorts(id){
        document.allSwitchers.action="abcbank/manageSwitcherPorts.jsp?id="+id;
        document.allSwitchers.submit();
    }

    function bundingIP(id){
        document.allSwitchers.action="abcbank/bundingIP.jsp?id="+id;
        document.allSwitchers.submit();
    }


    time = new Array("1点","2点","3点","4点","5点","6点","7点","8点","9点","10点","11点","12点","13点","14点","15点",
            "16点","17点","18点","19点","20点","21点","22点","23点","24点");
    week = new Array("周一","周二","周三","周四","周五","周六","周日");
    day = new Array("1日","2日","3日","4日","5日","6日","7日","8日","9日","10日","11日","12日","13日","14日","15日",
            "16日","17日","18日","19日","20日","21日","22日","23日","24日","25日","26日","27日","28日","29日","30日","31日");

    function changelocation(backup,row)
    {
        var backupCycle=backup;

        if(backupCycle=='每天'){
            var tmp = document.getElementById("cycle2_"+row);
            while(tmp.hasChildNodes()){
                tmp.removeChild(tmp.firstChild);
            }
            document.getElementById("cycle3_"+row).setAttribute("style", "display:none");
            for(var i = 0; i < time.length; ++i)
                document.getElementById("cycle2_"+row).appendChild(new Option(time[i],time[i]));
        }

        if(backupCycle=='每周'){
            var tmp = document.getElementById("cycle2_"+row);
            while(tmp.hasChildNodes()){
                tmp.removeChild(tmp.firstChild);
            }
            for(var i = 0; i < week.length; ++i)
                document.getElementById("cycle2_"+row).appendChild(new Option(week[i], week[i]));

            document.getElementById("cycle3_"+row).setAttribute("style","display");
            tmp = document.getElementById("cycle3_"+row);
            while(tmp.hasChildNodes()){
                tmp.removeChild(tmp.firstChild);
            }
            for(var i = 0; i < time.length; ++i)
                document.getElementById("cycle3_"+row).appendChild(new Option(time[i], time[i]));
        }

        if(backupCycle=='每月'){
            var tmp = document.getElementById("cycle2_"+row);
            while(tmp.hasChildNodes()){
                tmp.removeChild(tmp.firstChild);
            }
            for(var i = 0; i < day.length; ++i)
                document.getElementById("cycle2_"+row).appendChild(new Option(day[i], day[i]));

            document.getElementById("cycle3_"+row).setAttribute("style", "display");
            tmp = document.getElementById("cycle3_"+row);
            while(tmp.hasChildNodes()){
                tmp.removeChild(tmp.firstChild);
            }
            for(var i = 0; i < time.length; ++i)
                document.getElementById("cycle3_"+row).appendChild(new Option(time[i], time[i]));
        }
    }

</script>

<form method="post" name="allSwitchers">
    <input type="hidden" name="rowID"/>
    <input type="hidden" name="isCycle" value="0"/>
    <input type="hidden" name="switcherId" />

    <h3>交换机配置管理</h3>

    <table>
        <td>
            <a id="doNewIPSegment" href="javascript:addSwitcher()"><img src="images/add1.gif" alt="新增交换机" border="0"></a>
            <a href="javascript:addSwitcher()">新增交换机</a>
        </td>

        <td>
            <div><a id="log" href="abcbank/switcher.log">日志</a></div>
        </td>
    </table>

    <table width="100%" border="1" cellspacing="0" cellpadding="2" bordercolor="black">

        <tr bgcolor="#999999">
            <td width="3%" align="center"><b>品牌</b></td>
            <td width="3%" align="center"><b>IP</b></td>
            <td width="3%" align="center"><b>用户名</b></td>
            <td width="8%" align="center"><b>备份命令</b></td>
            <td width="8%" align="center"><b>恢复命令</b></td>
            <td width="5%" align="center"><b>备注</b></td>
        </tr>
        <%
            Switcher[] ss = op.selectAll();
            int row = 0;
            for(Switcher sw : ss){
                String id = sw.getId();
                String host = sw.getHost();
                String user = sw.getUser();
                String password = sw.getPassword();
                String brand = sw.getBrand();
                String backup = sw.getBackup();
                String recovery = sw.getRecovery();
                String comment = sw.getComment();
                session.setAttribute("host-"+id, host);
                session.setAttribute("user-"+id, user);
                session.setAttribute("password-"+id, password);
        %>
        <tr bgcolor=<%=row%2==0 ? "#ffffff" : "#cccccc"%>>
            <td width="3%" rowspan="2"  align="center">
                <div id="brand-<%=row%>">
                    <%= ((brand == null || brand.equals("")) ? "&nbsp;" : brand) %>
                    <input type="hidden" name="brand-<%=row%>" value="<%= ((brand == null || brand.equals("")) ? "&nbsp;" : brand) %>"/>
                </div>
            </td>

            <td width="3%" rowspan="2" align="center">
                <div id="host-<%=row%>">
                    <%= ((host == null || host.equals("")) ? "&nbsp;" : host) %>
                    <input type="hidden" name="host-<%=row%>" value="<%= ((host == null || host.equals("")) ? "&nbsp;" : host) %>"/>
                </div>
            </td>

            <td width="3%" align="center">
                <div id="user-<%=row%>">
                    <%= ((user == null || user.equals("")) ? "&nbsp;" : user) %>
                    <input type="hidden" name="user-<%=row%>" value="<%= ((user == null || user.equals("")) ? "&nbsp;" : user) %>"/>
                </div>
            </td>

            <td width="8%" align="center">
                <div id="backup-<%=row%>">
                    <%= ((backup == null || backup.equals("")) ? "&nbsp;" : backup) %>
                    <input type="hidden" name="backup-<%=row%>" value="<%= ((backup == null || backup.equals("")) ? "&nbsp;" : backup) %>"/>
                </div>
            </td>

            <td width="8%" align="center">
                <div>
                    <%= ((recovery == null || recovery.equals("")) ? "&nbsp;" : recovery) %>
                    <input type="hidden" name="recovery-<%=row%>" id="recovery-<%=row%>" value="<%= ((recovery == null || recovery.equals("")) ? "" : recovery) %>"/>
                </div>
            </td>

            <td width="5%" align="center">
                <div id="comment-<%=row%>">
                    <%= ((comment == null || comment.equals("")) ? "&nbsp;" : comment) %>
                </div>
            </td>

            <input type="hidden" name="password-<%=row%>" value="<%= ((password == null || password.equals("")) ? "&nbsp;" : password) %>"/>

        </tr>

        <tr bgcolor="#cccccc">
            <td width="15%" colspan="4"> &nbsp;&nbsp;<b>操作：</b>
                <a id="<%= "ss("+id+").doDelete" %>" href="javascript:deleteSwitcher('<%=id%>')" onclick="return confirm('你确定要删除： <%=host%> ?')">删除</a>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <a id="<%= "ss("+id+").ports" %>" href="javascript:managePorts('<%=id%>')">端口开关</a>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <a id="<%= "ss("+id+").doBunding" %>" href="javascript:bundingIP('<%=id%>')">地址绑定</a>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <a id="<%= "ss("+id+").doRecovery" %>" href="javascript:recoverySwitcher('<%=row%>')" onclick="return confirm('你确定要恢复： <%=host%>交换机的配置 ?')">恢复系统</a>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <a id="<%= "ss("+id+").doBackup" %>" href="javascript:backupSwitcher('<%=row%>')">备份系统</a>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <a id="<%= "ss("+id+").doBackupCycle" %>" href="javascript:backupSwitcherCycle('<%=row%>')">定期备份</a>

                <select id="cycle1_<%=row%>" name="cycle1_<%=row%>" onChange="changelocation(document.allSwitchers.cycle1_<%=row%>.options[document.allSwitchers.cycle1_<%=row%>.selectedIndex].value,<%=row%>)" size="1">
                    <option value="">请选择</option>
                    <option value="每天">每天</option>
                    <option value="每周">每周</option>
                    <%--<option value="每月">每月</option>--%>
                </select>
                <select id="cycle2_<%=row%>" name="cycle2_<%=row%>">
                    <option value="" selected>请选择</option>
                </select>
                <select id="cycle3_<%=row%>" name="cycle3_<%=row%>" style="display:none">
                    <option value="" selected>请选择</option>
                </select>
            </td>
        </tr>

        <%
                row++;
            }
        %>
    </table>
</form>

<jsp:include page="/includes/footer.jsp" flush="false" />