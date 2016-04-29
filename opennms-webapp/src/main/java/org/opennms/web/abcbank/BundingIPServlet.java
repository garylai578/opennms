package org.opennms.web.abcbank;

import org.opennms.core.bank.SwitcherUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by laiguanhui on 2016/4/16.
 */
public class BundingIPServlet extends HttpServlet {

    private static final long serialVersionUID = -1120246812517867864L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String host = request.getParameter("host");
        String user = request.getParameter("user");
        String password = request.getParameter("password");
        String p = request.getParameter("port");
        int port = Integer.parseInt(p);
        String no_dot1x_before_s = request.getParameter("no_dot1x_before");
        String dot1x_after_s = request.getParameter("dot1x_after");
        String interRange = "fastEthernet " + request.getParameter("inter0")+ "/" + request.getParameter("inter1") + "-" + request.getParameter("inter2");
	    String ip = request.getParameter("ips");
        String[] ips = ip.split("\r\n");

        String[] backContent; //交换机的输出
        String backResult = "";      //输出的结果
        boolean no_dot1x_before = false;
        boolean dot1x_after = false;

        if(no_dot1x_before_s.equals("1"))
            no_dot1x_before = true;
        if(dot1x_after_s.equals("1"))
            dot1x_after = true;

        SwitcherUtil su = new SwitcherUtil(host, user, password, port);
        String results = su.bundingIPs(ips, no_dot1x_before, dot1x_after, interRange);
        su.diconnect();
        String successFlag = "绑定成功";    //判断交换机返回结果是否成功的标志
        backContent = results.split("@result_split_flag@");
        for(int i = 0; i < backContent.length; ++i) {
            if (backContent[i].contains(successFlag))
                backResult += "绑定成功" + "\n";
            else
                backResult += "绑定失败或重复绑定" + "\n";
        }
        request.setAttribute("host", host);
        request.setAttribute("user", user);
        request.setAttribute("password", password);
        request.setAttribute("backContent", results.replaceAll("@result_split_flag@", "\n"));
	    request.setAttribute("ips", ip);
        request.setAttribute("result", backResult);

        request.getRequestDispatcher("bundingIP.jsp").forward(request, response);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
