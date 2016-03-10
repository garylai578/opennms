package org.opennms.web.abcbank;

import org.opennms.core.bank.IPSegmentOperater;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * Created by laiguanhui on 2016/2/22.
 */
public class StopIPSegmentServlet extends HttpServlet {

    private static final long serialVersionUID = 42396362620706028L;

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tmp = request.getParameter("ipSegID");
        int id = Integer.parseInt(tmp);
        IPSegmentOperater op = new IPSegmentOperater();
        try {
            op.updateByID(id, "state", "停用");

            response.setContentType("text/html;charset=gb2312");
            PrintWriter pw=response.getWriter();
            pw.print("<script language='javascript'>alert('成功停用' )</script>");
            RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/abcbank/ipsegment.jsp");
            dispatcher.forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
