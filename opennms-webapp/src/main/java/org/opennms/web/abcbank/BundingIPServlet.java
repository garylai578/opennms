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
        String[] inters = {request.getParameter("inter0"), request.getParameter("inter1"), request.getParameter("inter2")};
        String[] ips = request.getParameter("ips").split("\n");

        String[] backContent; //交换机的输出
        String[] result;      //结果
        boolean no_dot1x_before = false;
        boolean dot1x_after = false;

        if(no_dot1x_before_s.equals("1"))
            no_dot1x_before = true;
        if(dot1x_after_s.equals("1"))
            dot1x_after = false;

        SwitcherUtil su = new SwitcherUtil(host, user, password, port);
        String results = su.bundingIPs(ips, no_dot1x_before, dot1x_after, inters);
        String successFlag = "";    //判断交换机返回结果是否成功的标志, 待确定 TODO
        backContent = results.split("@result_split_flag@");
        result = new String[backContent.length];
        for(int i = 0; i < backContent.length; ++i) {
            if (backContent[i].contains(successFlag))
                result[i] = "绑定成功";
            else
                result[i] = "绑定失败";
        }
        request.setAttribute("host", host);
        request.setAttribute("user", user);
        request.setAttribute("password", password);
        request.setAttribute("backContent", results.replaceAll("@result_split_flag@", ""));
        request.setAttribute("result", result);

        request.getRequestDispatcher("bundingIP.jsp").forward(request, response);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
