package org.opennms.web.abcbank;

import org.opennms.core.bank.BankIPAddressOp;
import org.opennms.core.bank.BankLogWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by laiguanhui on 2016/3/17.
 */
public class StartIPAddressServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getRemoteUser();
        String[] ips = request.getParameter("ipAddr").split("\t");
        String[] ids = request.getParameter("ipAddrID").split("\t");

        BankIPAddressOp op = new BankIPAddressOp();

        for(int i = 0; i < ips.length; ++i) {
            int id = Integer.parseInt(ids[i]);
            try {
                op.updateByID(id, "state", "'在用'");
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                op.updateByID(id, "start_date", "'" + sf.format(date) + "'");
                op.updateByID(id, "stop_date", "null");

//            RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/abcbank/ipsegment.jsp");
//            dispatcher.forward(request, response);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        BankLogWriter.getSingle().writeLog("用户[" + userId + "]启用IP[" + request.getParameter("ipAddr") + "]");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter pw = response.getWriter();
        request.setAttribute("update", "true");
        String curPage = request.getParameter("curPage");
        if(curPage == null)
            curPage = "1";
        pw.print("<script language='javascript'>alert('成功启用' );window.location=('/opennms/abcbank/ipaddress.jsp?update=true&curPage=" + curPage
                + "&bank=" + request.getParameter("bank") + "&dept=" + request.getParameter("dept") + "&network_type=" + request.getParameter("network_type") + "&users=" + request.getParameter("users") + "');</script>");
        pw.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
