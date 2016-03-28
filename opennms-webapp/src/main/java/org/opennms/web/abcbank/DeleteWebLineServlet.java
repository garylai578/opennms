package org.opennms.web.abcbank;

import org.opennms.core.bank.WebLineOperator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * Created by laiguanhui on 2016/3/18.
 */
public class DeleteWebLineServlet extends HttpServlet {
    private static final long serialVersionUID = 4289429456093660875L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tmp = request.getParameter("webLineID");
        int id = Integer.parseInt(tmp);
        WebLineOperator op = new WebLineOperator();
        try {
            op.delete(id);
            response.setContentType("text/html;charset=gb2312");
            PrintWriter pw=response.getWriter();
            pw.print("<script language='javascript'>alert('成功删除！' );window.location=('/opennms/abcbank/webline.jsp');</script>");
            pw.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
