package org.opennms.web.abcbank;

import org.opennms.core.bank.BankLogWriter;
import org.opennms.core.bank.IPSegmentOperater;

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
 * Created by laiguanhui on 2016/2/22.
 */
public class StopIPSegmentServlet extends HttpServlet {

    private static final long serialVersionUID = 42396362620706028L;

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getRemoteUser();
        String rowID = request.getParameter("rowID");
        String startIP = request.getParameter("startIP-"+rowID);
        String endIP = request.getParameter("endIP-"+rowID);
        String tmp = request.getParameter("ipSegID");
        int id = Integer.parseInt(tmp);
        IPSegmentOperater op = new IPSegmentOperater();
        try {
            op.updateByID(id, "state", "停用");
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            op.updateByID(id, "stoptime", sf.format(date));

            BankLogWriter.getSingle().writeLog("用户[" + userId + "]停用IP段[" + startIP + "-" + endIP + "]");

            response.setContentType("text/html;charset=gb2312");
            PrintWriter pw=response.getWriter();
            pw.print("<script language='javascript'>alert('成功停用' );window.location=('/opennms/abcbank/ipsegment.jsp');</script>");
            pw.close();
//            RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/abcbank/ipsegment.jsp");
//            dispatcher.forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
