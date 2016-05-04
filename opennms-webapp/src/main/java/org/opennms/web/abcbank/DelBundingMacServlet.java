package org.opennms.web.abcbank;

import org.opennms.core.bank.BankLogWriter;
import org.opennms.core.bank.SwitcherUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by laiguanhui on 2016/4/16.
 */
public class DelBundingMacServlet extends HttpServlet {

    private static final long serialVersionUID = 1125829759773406226L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getRemoteUser();
        String host = request.getParameter("host");
        String user = request.getParameter("user");
        String password = request.getParameter("password");
        String p = request.getParameter("port");
        int port = Integer.parseInt(p);
        String macs = request.getParameter("delBundingMACs");
        String[] macList = macs.split("\t");

        SwitcherUtil su = new SwitcherUtil(host, user, password, port);
        String results = su.deletBunding(macList);
        su.diconnect();

        request.setAttribute("host", host);
        request.setAttribute("user", user);
        request.setAttribute("password", password);
        request.setAttribute("backContent", results.replaceAll("@result_split_flag@", "\n"));
        request.setAttribute("macs", macs);

        String logs = "";
        String[] backContent = results.split("@result_split_flag@");
        for(int i = 0 ; i < macList.length; ++i)
            logs += "mac[" + macList[i] + "]" + backContent[i] + ", ";
        BankLogWriter.getSingle().writeLog("用户[" + userId + "]删除交换机[" + host + "]旧的绑定关系，结果是：" + logs.substring(0, logs.length()-3));
        request.getRequestDispatcher("bundingIP.jsp").forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
