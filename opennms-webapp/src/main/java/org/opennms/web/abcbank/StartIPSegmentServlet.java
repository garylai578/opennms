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
public class StartIPSegmentServlet extends HttpServlet {

    private static final long serialVersionUID = -1759156388543404811L;

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getRemoteUser();
        String[] ids = request.getParameter("ipSegID").split("\t");
        String ipSegs = request.getParameter("ipSegs");
        IPSegmentOperater op = new IPSegmentOperater();
        for(String idString : ids) {
            int id = Integer.parseInt(idString);
            try {
                op.updateByID(id, "state", "在用");
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                op.updateByID(id, "createtime", sf.format(date));
                op.updateByID(id, "stoptime", "null");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        request.setAttribute("update", "true");
        BankLogWriter.getSingle().writeLog("用户[" + userId + "]启用IP段[" + ipSegs + "]");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter pw = response.getWriter();
        String curPage = request.getParameter("curPage");
        if(curPage == null)
            curPage = "1";
        pw.print("<script language='javascript'>alert('成功启用' );window.location=('/opennms/abcbank/ipsegment.jsp?update=true&curPage="
                + curPage + "&searchIpSeg=" + request.getParameter("searchIpSeg") + "&bank=" + request.getParameter("bank") + "&state=" + request.getParameter("state") + "');</script>");
        pw.close();
//            RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/abcbank/ipsegment.jsp");
//            dispatcher.forward(request, response);

    }
}
