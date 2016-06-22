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
        String[] ids = request.getParameter("ipSegID").split("\t");
        String ipSegs = request.getParameter("ipSegs");
        IPSegmentOperater op = new IPSegmentOperater();

        for(String idString : ids) {
            int id = Integer.parseInt(idString);
            try {
                op.updateByID(id, "state", "停用");
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                op.updateByID(id, "stoptime", sf.format(date));

//            RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/abcbank/ipsegment.jsp");
//            dispatcher.forward(request, response);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        BankLogWriter.getSingle().writeLog("用户[" + userId + "]停用IP段[" + ipSegs + "]");
        response.setContentType("text/html;charset=gb2312");
        PrintWriter pw = response.getWriter();
        String curPage = request.getParameter("curPage");
        request.setAttribute("update", "true");
        if(curPage == null)
            curPage = "1";
        pw.print("<script language='javascript'>alert('成功停用' );window.location=('/opennms/abcbank/ipsegment.jsp?curPage="
                + curPage + "&bank=" + request.getParameter("bank") + "&dept=" + request.getParameter("dept") + "&state=" + request.getParameter("state") + "');</script>");
        pw.close();
    }
}
