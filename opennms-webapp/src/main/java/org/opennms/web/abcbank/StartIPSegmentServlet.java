package org.opennms.web.abcbank;

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
        String tmp = request.getParameter("ipSegID");
        int id = Integer.parseInt(tmp);
        IPSegmentOperater op = new IPSegmentOperater();
        try {
            op.updateByID(id, "state", "启用");
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            op.updateByID(id, "createtime", sf.format(date));
            op.updateByID(id, "stoptime", "null");

            response.setContentType("text/html;charset=gb2312");
            PrintWriter pw=response.getWriter();
            pw.print("<script language='javascript'>alert('成功启用' );window.location=('/opennms/abcbank/ipsegment.jsp');</script>");
            pw.close();
//            RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/abcbank/ipsegment.jsp");
//            dispatcher.forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
