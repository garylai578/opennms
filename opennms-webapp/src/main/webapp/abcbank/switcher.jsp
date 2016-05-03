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

<jsp:include page="/includes/header.jsp" flush="false">
    <jsp:param name="title" value="交换机配置管理" />
    <jsp:param name="headTitle" value="交换机配置管理" />
    <jsp:param name="breadcrumb" value="<a href='admin/index.jsp'>管理</a>" />
    <jsp:param name="breadcrumb" value="交换机配置管理" />
</jsp:include>

<%
    SwitcherOperator op = new SwitcherOperator();

    //读取交换机批量操作的文件内容
    String batchComm = "";
    int MAX_SIZE = 102400 * 102400;
    String contentType = request.getContentType();
    int formDataLength = 0;
    DataInputStream in = null;
    if (contentType != null && contentType.indexOf("multipart/form-data") >= 0) {
        //读入上传的数据
        in = new DataInputStream(request.getInputStream());
        formDataLength = request.getContentLength();
        if (formDataLength > MAX_SIZE) {
            out.print("<script language='javascript'>alert('上传的文件字节数不可以超过：" +  MAX_SIZE + "');window.location=('index.jsp')</script>");
            return;
        }
        byte dataBytes[] = new byte[formDataLength];
        int byteRead = 0;
        int totalBytesRead = 0;
        //上传的数据保存在byte数组
        while(totalBytesRead < formDataLength){
            byteRead = in.read(dataBytes,totalBytesRead,formDataLength);
            totalBytesRead += byteRead;
        }
        //根据byte数组创建字符串
        String file = new String(dataBytes);
        String startFlag = "#start";
        String endFlag = "#end";
        if(file.indexOf(startFlag) < 0 || file.indexOf(endFlag) < 0 ) {
            out.print("<script language='javascript'>alert('上传文件格式不对，缺少#start或#end');window.location=('index.jsp')</script>");
            return;
        }
        int startPos = file.indexOf(startFlag) + startFlag.length();
        int endPos = file.indexOf(endFlag);

        batchComm = file.substring(startPos, endPos);
    }
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

    function batchOperator(rows){
        var op = document.allSwitchers.batchComm.value;
        if(op == null || op == ""){
            alert("请首先点击“上传”按钮");
            return;
        }

        var sw="";
        for (var i = 0; i < rows; ++i) {
            var choose = document.getElementById("choose-"+i);
            if (choose.checked == true)
                sw += i + "\t";
        }
        if(sw==""){
            alert("请选择需要操作的交换机");
            return;
        }

        document.allSwitchers.sws.value = sw;
        document.allSwitchers.action="abcbank/batchOperateSwitchers";
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

    function searchSwitcher()
    {
        document.allSwitchers.action="abcbank/searchSwitcher";
        document.allSwitchers.submit();
    }

</script>

<form method="post" name="allSwitchers">
    <input type="hidden" name="rowID"/>
    <input type="hidden" name="isCycle" value="0"/>
    <input type="hidden" name="switcherId" />
    <input type="hidden" name="sws"/>
    <input type="hidden" name="batchComm" value="<%=((batchComm==null)?"":batchComm)%>"/>

    <h3>交换机配置管理</h3>

    <table>
        <td>
            <a id="doNewIPSegment" href="javascript:addSwitcher()"><img src="images/add1.gif" alt="新增交换机" border="0"></a>
            <a href="javascript:addSwitcher()">新增交换机</a>
        </td>

        <td align="left">
            <select id="searchType" name="searchType">
                <option value="">请选择筛选的字段</option>
                <option value="name">名称</option>
                <option value="groups">分组</option>
                <option value="brand">品牌</option>
                <option value="host">IP</option>
                <option value="comment">备注</option>
            </select>
            <input id="searchCont" name="searchCont" size="18%" placeholder="请输入要搜索的内容" value="">
            <a id="doSearch" href="javascript:searchSwitcher()"><img src="images/search.png" alt="筛选" border="0"></a>
            <a id="" href="javascript:searchSwitcher()">筛选</a>
        </td>

        <td>
            <div><a id="log" href="abcbank/switcher.log">查看日志</a></div>
        </td>
    </table>

    <table width="100%" border="1" cellspacing="0" cellpadding="2" bordercolor="black">

        <tr bgcolor="#999999">
            <td width="3%" align="center"><b>选择</b></td>
            <td width="3%" align="center"><b>名称</b></td>
            <td width="3%" align="center"><b>分组</b></td>
            <td width="3%" align="center"><b>品牌</b></td>
            <td width="3%" align="center"><b>IP</b></td>
            <td width="3%" align="center"><b>用户名</b></td>
            <td width="5%" align="center"><b>备份命令</b></td>
            <td width="5%" align="center"><b>恢复命令</b></td>
            <td width="3%" align="center"><b>广域网地址/掩码</b></td>
            <td width="3%" align="center"><b>Lookback地址/掩码</b></td>
            <td width="3%" align="center"><b>Vlan 150地址1/掩码</b></td>
            <td width="3%" align="center"><b>Vlan 150地址2/掩码</b></td>
            <td width="3%" align="center"><b>Vlan 160地址1/掩码</b></td>
            <td width="3%" align="center"><b>Vlan 160地址2/掩码</b></td>
            <td width="3%" align="center"><b>Vlan 170地址1/掩码</b></td>
            <td width="3%" align="center"><b>Vlan 170地址2/掩码</b></td>
            <td width="3%" align="center"><b>ospf进程名</b></td>
            <td width="3%" align="center"><b>area号</b></td>
            <td width="3%" align="center"><b>备注</b></td>
        </tr>
        <%
            Switcher[] ss = (Switcher[])request.getAttribute("switchers");
            if(ss == null)
                ss = op.selectAll();
            int row = 0;
            for(Switcher sw : ss){
                String id = sw.getId();
                String name = sw.getName();
                String group = sw.getGroup();
                String host = sw.getHost();
                String user = sw.getUser();
                String password = sw.getPassword();
                String brand = sw.getBrand();
                String backup = sw.getBackup();
                String recovery = sw.getRecovery();
                String comment = sw.getComment();
                String wan_ip = sw.getWan_ip();
                String lookback = sw.getLookback_ip();
                String vlan150_ip1 = sw.getVlan150_ip1();
                String vlan150_ip2 = sw.getVlan150_ip2();
                String vlan160_ip1 = sw.getVlan160_ip1();
                String vlan160_ip2 = sw.getVlan160_ip2();
                String vlan170_ip1 = sw.getVlan170_ip1();
                String vlan170_ip2 = sw.getVlan170_ip2();
                String ospf = sw.getOspf();
                String area = sw.getArea();
                session.setAttribute("host-"+id, host);
                session.setAttribute("user-"+id, user);
                session.setAttribute("password-"+id, password);
        %>
        <tr bgcolor=<%=row%2==0 ? "#ffffff" : "#cccccc"%>>
            <td width="3%" rowspan="2" align="center">
                <div>
                    <input id="choose-<%=row%>" type="checkbox" value="" />
                </div>
            </td>

            <td width="3%" rowspan="2"  align="center">
                <div>
                    <%= ((name == null || name.equals("")) ? "&nbsp;" : name) %>
                    <input type="hidden" name="name-<%=row%>" id="name-<%=row%>" value=""/>
                </div>
            </td>

            <td width="3%" align="center">
                <div>
                    <%= ((group == null || group.equals("")) ? "&nbsp;" : group) %>
                    <input type="hidden"  name="group-<%=row%>" id="group-<%=row%>" value=""/>
                </div>
            </td>

            <td width="3%" align="center">
                <div id="brand-<%=row%>">
                    <%= ((brand == null || brand.equals("")) ? "&nbsp;" : brand) %>
                    <input type="hidden" name="brand-<%=row%>" value="<%= ((brand == null || brand.equals("")) ? "&nbsp;" : brand) %>"/>
                </div>
            </td>

            <td width="3%" align="center">
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

            <td width="5%" align="center">
                <div id="backup-<%=row%>">
                    <%= ((backup == null || backup.equals("")) ? "&nbsp;" : backup) %>
                    <input type="hidden" name="backup-<%=row%>" value="<%= ((backup == null || backup.equals("")) ? "&nbsp;" : backup) %>"/>
                </div>
            </td>

            <td width="5%" align="center">
                <div>
                    <%= ((recovery == null || recovery.equals("")) ? "&nbsp;" : recovery) %>
                    <input type="hidden" name="recovery-<%=row%>" id="recovery-<%=row%>" value="<%= ((recovery == null || recovery.equals("")) ? "" : recovery) %>"/>
                </div>
            </td>

            <td width="3%" align="center">
                <div id="wan_ip-<%=row%>">
                    <%= ((wan_ip == null || wan_ip.equals("")) ? "&nbsp;" : wan_ip) %>
                </div>
            </td>
            <td width="3%" align="center">
                <div id="lookback-<%=row%>">
                    <%= ((lookback == null || lookback.equals("")) ? "&nbsp;" : lookback) %>
                </div>
            </td>
            <td width="3%" align="center">
                <div id="vlan150_ip1-<%=row%>">
                    <%= ((vlan150_ip1 == null || vlan150_ip1.equals("")) ? "&nbsp;" : vlan150_ip1) %>
                </div>
            </td>
            <td width="3%" align="center">
                <div id="vlan150_ip2-<%=row%>">
                    <%= ((vlan150_ip2 == null || vlan150_ip2.equals("")) ? "&nbsp;" : vlan150_ip2) %>
                </div>
            </td>
            <td width="3%" align="center">
                <div id="vlan160_ip1-<%=row%>">
                    <%= ((vlan160_ip1 == null || vlan160_ip1.equals("")) ? "&nbsp;" : vlan160_ip1) %>
                </div>
            </td>
            <td width="3%" align="center">
                <div id="-<%=row%>">
                    <%= ((vlan160_ip2 == null || vlan160_ip2.equals("")) ? "&nbsp;" : vlan160_ip2) %>
                </div>
            </td>
            <td width="3%" align="center">
                <div id="vlan170_ip1-<%=row%>">
                    <%= ((vlan170_ip1 == null || vlan170_ip1.equals("")) ? "&nbsp;" : vlan170_ip1) %>
                </div>
            </td>
            <td width="3%" align="center">
                <div id="vlan170_ip2-<%=row%>">
                    <%= ((vlan170_ip2 == null || vlan170_ip2.equals("")) ? "&nbsp;" : vlan170_ip2) %>
                </div>
            </td>
            <td width="3%" align="center">
                <div id="ospf-<%=row%>">
                    <%= ((ospf == null || ospf.equals("")) ? "&nbsp;" : ospf) %>
                </div>
            </td>
            <td width="3%" align="center">
                <div id="area-<%=row%>">
                    <%= ((area == null || area.equals("")) ? "&nbsp;" : area) %>
                </div>
            </td>

            <td width="3%" align="center">
                <div id="comment-<%=row%>">
                    <%= ((comment == null || comment.equals("")) ? "&nbsp;" : comment) %>
                </div>
            </td>

            <input type="hidden" name="password-<%=row%>" value="<%= ((password == null || password.equals("")) ? "&nbsp;" : password) %>"/>

        </tr>

        <tr bgcolor="#cccccc">
            <td width="15%" colspan="17"> &nbsp;&nbsp;<b>操作：</b>
                <a id="<%= "ss("+id+").doDelete" %>" href="javascript:deleteSwitcher('<%=id%>')" onclick="return confirm('你确定要删除： <%=host%> ?')">删除</a>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <a id="<%= "ss("+id+").ports" %>" href="javascript:managePorts('<%=row%>')">端口操作</a>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <a id="<%= "ss("+id+").doBunding" %>" href="javascript:bundingIP('<%=row%>')">地址绑定</a>
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
    &nbsp;&nbsp;
    批量操作：请先选中需要批量操作的交换机，然后上传批量操作文件并点击确定
    <br/>
    <br/>
    &nbsp;&nbsp;
    <input type="button" onclick="window.location='/opennms/abcbank/importFile.jsp'" value="上传">
    <input type="button" onclick="javascript:batchOperator('<%=row%>')" value="确定">

</form>

<jsp:include page="/includes/footer.jsp" flush="false" />