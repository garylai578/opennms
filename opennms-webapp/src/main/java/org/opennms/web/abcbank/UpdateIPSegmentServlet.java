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
 * Created by laiguanhui on 2016/2/23.
 */
public class UpdateIPSegmentServlet extends HttpServlet {
    private static final long serialVersionUID = -7718296603096545783L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("ipSegID"));
        String row = request.getParameter("rowID");
        String bankname = request.getParameter("bankname-" + row);
        String banktype = request.getParameter("banktype-" + row);
        String comment =request.getParameter("comment-" + row);

        IPSegmentOperater op = new IPSegmentOperater();
        try{
            op.updateByID(id, "name", bankname);
            op.updateByID(id, "type", banktype);
            op.updateByID(id, "comment", comment);

            PrintWriter pw=response.getWriter();
            pw.write("<script language='javascript'>alert('修改成功')</script>");
            pw.close();

            RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/abcbank/ipsegment.jsp");
            dispatcher.forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
