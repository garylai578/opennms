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
public class DelBundingMacServlet extends HttpServlet {

    private static final long serialVersionUID = 1125829759773406226L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String host = request.getParameter("host");
        String user = request.getParameter("user");
        String password = request.getParameter("password");
        String p = request.getParameter("port");
        int port = Integer.parseInt(p);
        String macs = request.getParameter("macs");
        String[] macList = macs.split("\n");

        SwitcherUtil su = new SwitcherUtil(host, user, password, port);
        String results = su.deletBunding(macList);

        request.setAttribute("host", host);
        request.setAttribute("user", user);
        request.setAttribute("password", password);
        request.setAttribute("backContent", results.replaceAll("@result_split_flag@", ""));
        request.setAttribute("macs", macs);

        request.getRequestDispatcher("bundingIP.jsp").forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
