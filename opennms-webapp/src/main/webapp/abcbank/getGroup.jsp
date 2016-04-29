<%@ page import="org.opennms.netmgt.config.UserFactory" %>
<%@ page import="org.opennms.netmgt.config.UserManager" %>
<%@ page import="org.opennms.netmgt.config.users.Contact" %>
<%@ page import="org.opennms.netmgt.config.users.User" %>
<%@ page import="java.util.Map" %><%--
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
%>
