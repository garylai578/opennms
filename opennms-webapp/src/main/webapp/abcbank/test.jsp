<%@ page import="org.opennms.netmgt.config.UserFactory" %>
<%@ page import="org.opennms.netmgt.config.UserManager" %>
<%@ page import="org.opennms.netmgt.config.users.Contact" %>
<%@ page import="org.opennms.netmgt.config.users.User" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Properties" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <script type="text/javascript" src="jquery-1.6.4.min.js"></script>

    <title>${pageContext.request.remoteUser}</title>
</head>


<body>
<%
    String name = (String)request.getRemoteUser();
    out.print("<br>name:" + name);

    final HttpSession userSession = request.getSession(false);
    User user = null;
    String userid = "";
    UserManager userFactory;
/*    try {
        UserFactory.init();
        userFactory = UserFactory.getInstance();
    } catch (Throwable e) {
        throw new ServletException("UserFactory:modify() " + e);
    }*/

    if (userSession != null) {
        UserFactory.init();
        userFactory = UserFactory.getInstance();
        Map users = userFactory.getUsers();
        user = (User)users.get("test");
/*        Iterator i = users.keySet().iterator();
        int row = 0;
        while(i.hasNext()) {
            String key = (String)i.next();
            User curUser = (User) users.get(key);
            out.print("<br>" + key);


        }*/
    }

//    out.print("<br>usr:" + user.getFullName());
    out.print("<br>name:" + user.getFullName());
List<Contact> list = user.getContactCollection();
    for(Contact ll : list){
        out.print("<br>list:" + ll.getServiceProvider());
    }

    Contact[] con = user.getContact();
    out.print("<br>group:" + con[5].getServiceProvider());
    for(Contact c : con) {
        if(c.getType() != null)
        out.print("<br>type:" + c.getType());
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
    for(String bankname : bankNames)
            out.print("<br>bank name:" + bankname);
//    String bankTypes = pro.getProperty("abc-bankname");
//    out.println("<br>tyrp:" + bankTypes);
%>

<select id="select" onkeydown="Select.del(this,event)" onkeypress="Select.write(this,event)">
    <option value=""></option>
    <option value="aaa">aaa</option>
    <option value="bbb">bbb</option>
    <option value="ccc">ccc</option>
</select>
<input type="button" value="获取选择值" id="test" onclick="test();"/>
<script>
    var Select = {
        del : function(obj,e){
            if((e.keyCode||e.which||e.charCode) == 8){
                var opt = obj.options[0];
                opt.text = opt.value = opt.value.substring(0, opt.value.length>0?opt.value.length-1:0);
            }
        },
        write : function(obj,e){
            if((e.keyCode||e.which||e.charCode) == 8)return ;
            var opt = obj.options[0];
            opt.selected = "selected";
            opt.text = opt.value += String.fromCharCode(e.charCode||e.which||e.keyCode);
        }
    }
    function test(){
        alert(document.getElementById("select").value);
    }
</script><br />
</body>
</html>
