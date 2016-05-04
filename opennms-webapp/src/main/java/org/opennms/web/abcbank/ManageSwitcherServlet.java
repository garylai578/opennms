package org.opennms.web.abcbank;

import org.opennms.core.bank.BankLogWriter;
import org.opennms.core.bank.SwitcherUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by laiguanhui on 2016/4/12.
 */
public class ManageSwitcherServlet extends HttpServlet {

    private static final long serialVersionUID = -2653800565575021616L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getRemoteUser();
        String id = request.getParameter("id");
        String host = request.getParameter("host");
        String user = request.getParameter("user");
        String password = request.getParameter("password");
        String inters = request.getParameter("interface");
        SwitcherUtil util = new SwitcherUtil(host, user, password);
        String type = request.getParameter("type");
        int result = 0;
        String msg, manage="";

        String[] interfaces = inters.split("\t");
        for(String inter : interfaces) {
            if (type.equals("up-interface")) {
                manage = "开启交换机[" + host + "]的端口[" + inter + "]";
                result = util.upInterface(inter);
            }else if (type.equals("down-interface")) {
                manage = "关闭交换机[" + host + "]的端口[" + inter + "]";
                result = util.downInterface(inter);
            }else if (type.equals("dot1x")) {
                manage = "开启交换机[" + host + "]的端口认证[" + inter + "]";
                result = util.dot1X(inter);
            }else if (type.equals("undoDot1x")) {
                manage = "取消交换机[" + host + "]的端口认证[" + inter + "]";
                result = util.undoDot1X(inter);
            }

            if (result == 1)
                msg = "操作成功！";
            else
                msg = "操作失败，请重试或联系管理员！！";

            BankLogWriter.getSingle().writeLog("用户[" + userId + "]" + manage + "，" + msg);
        }
        util.diconnect();

        request.setAttribute("id", id);
        request.setAttribute("host-"+id, host);
        request.setAttribute("user-"+id, user);
        request.setAttribute("password-"+id, password);
        request.getRequestDispatcher("manageSwitcherPorts.jsp").forward(request, response);

       /* response.setContentType("text/html;charset=gb2312");
        PrintWriter pw=response.getWriter();
        pw.print("<script language='javascript'>alert('"+ msg +"' );window.location=('/opennms/abcbank/manageSwitcherPorts.jsp');</script>");
        pw.close();*/
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
