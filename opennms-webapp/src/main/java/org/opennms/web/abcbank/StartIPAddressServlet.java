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
        String rowID = request.getParameter("rowID");
        String ip = request.getParameter("ipaddr-"+rowID);
        String tmp = request.getParameter("ipAddrID");
        int id = Integer.parseInt(tmp);
        BankIPAddressOp op = new BankIPAddressOp();
        try {
            op.updateByID(id, "state", "'在用'");
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            op.updateByID(id, "start_date", "'" + sf.format(date) + "'");
            op.updateByID(id, "stop_date", "null");

            BankLogWriter.getSingle().writeLog("用户[" + userId + "]启用IP[" + ip + "]");
            response.setContentType("text/html;charset=gb2312");
            PrintWriter pw=response.getWriter();
            pw.print("<script language='javascript'>alert('成功启用' );window.location=('/opennms/abcbank/ipaddress.jsp');</script>");
            pw.close();
//            RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/abcbank/ipsegment.jsp");
//            dispatcher.forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
