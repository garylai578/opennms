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
public class StopIPAddressServlet extends HttpServlet {

    private static final long serialVersionUID = -4281314723738137770L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getRemoteUser();
        String rowID = request.getParameter("rowID");
        String ip = request.getParameter("ipaddr-"+rowID);
        String tmp = request.getParameter("ipAddrID");
        int id = Integer.parseInt(tmp);
        BankIPAddressOp op = new BankIPAddressOp();
        try {
            op.updateByID(id, "state", "'停用'");
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            op.updateByID(id, "stop_date", "'"+ sf.format(date) + "'");

            BankLogWriter.getSingle().writeLog("用户[" + userId + "]停用IP[" + ip + "]");
            response.setContentType("text/html;charset=gb2312");
            PrintWriter pw=response.getWriter();
            pw.print("<script language='javascript'>alert('成功停用' );window.location=('/opennms/abcbank/ipaddress.jsp');</script>");
            pw.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
