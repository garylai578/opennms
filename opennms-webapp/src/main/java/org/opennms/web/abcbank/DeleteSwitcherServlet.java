package org.opennms.web.abcbank;

import org.opennms.core.bank.SwitcherOperator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * Created by laiguanhui on 2016/3/31.
 */
public class DeleteSwitcherServlet extends HttpServlet {
    private static final long serialVersionUID = 1720170509575935035L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tmp = request.getParameter("switcherId");
        int id = Integer.parseInt(tmp);
        SwitcherOperator op = new SwitcherOperator();
        try {
            op.delete(id);
            response.setContentType("text/html;charset=gb2312");
            PrintWriter pw=response.getWriter();
            pw.print("<script language='javascript'>alert('成功删除！' );window.location=('/opennms/abcbank/switcher.jsp');</script>");
            pw.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
