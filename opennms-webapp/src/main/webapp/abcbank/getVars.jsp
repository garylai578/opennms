<%@ page import="org.opennms.netmgt.config.UserFactory" %>
<%@ page import="org.opennms.netmgt.config.UserManager" %>
<%@ page import="org.opennms.netmgt.config.users.Contact" %>
<%@ page import="org.opennms.netmgt.config.users.User" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Properties" %>
<%@ page import="java.util.Set" %><%--
  Created by IntelliJ IDEA.
  User: laiguanhui
  Date: 2016/4/29
  Time: 16:04
  To change this template use File | Settings | File Templates.
--%>
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
    String[] bankTypes = pro.getProperty("abc-banktype").split("/");
    String[] networkTypes = pro.getProperty("abc-networktype").split("/");
    String[] weblineTypes = pro.getProperty("abc-weblinetype").split("/");
    String[] switcherBrands = pro.getProperty("abc-switcherbrand").split("/");
    String[] switcherGroups = pro.getProperty("abc-switcherGroup").split("/");
    Map<String, String[]> bankAndDepts = new HashMap<String, String[]>();

    for(int i = 0; i < bankNames.length; ++i){
        String[] depts = pro.getProperty("abc-bankdept-"+(i+1)).split("/");
        bankAndDepts.put(bankNames[i], depts);
    }
%>

<script type="text/javascript">
    //分行（支行）与下属网点的二级联动
    banks=[];
    depts=[];
    <%
    Set<String> banks = bankAndDepts.keySet();
    int index = 0;
    for(String bank : banks){
        out.println("banks[" + index + "] = '" + bank + "'");
        String[] depts = bankAndDepts.get(bank);
        String deptsString = "";
        for(String dept : depts){
            deptsString += "'" + dept + "',";
        }
        out.println("depts[" + index + "]=[" + deptsString.substring(0, deptsString.length()-1) + "];");
        ++index;
    }
    %>

    function selectDepts(bank, dept){
        var deptElemt = document.getElementById(dept);
        while(deptElemt.hasChildNodes()){
            deptElemt.removeChild(deptElemt.firstChild);
        }
        deptElemt.appendChild(new Option("请选择", ""));

        for(var i = 0; i < banks.length; ++i){
            if(banks[i] == bank){
                for(var j = 0; j < depts[i].length; ++j)
                    deptElemt.appendChild(new Option(depts[i][j],depts[i][j]));
            }
        }
    }
</script>
