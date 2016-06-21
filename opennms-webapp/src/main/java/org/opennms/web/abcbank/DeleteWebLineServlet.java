package org.opennms.web.abcbank;

import org.opennms.core.bank.BankLogWriter;
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
        String userId = request.getRemoteUser();
        String[] ids = request.getParameter("webLineID").split("\t");
        WebLineOperator op = new WebLineOperator();

        for(String idString : ids) {
            int id = Integer.parseInt(idString);
            try {
                op.delete(id);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        BankLogWriter.getSingle().writeLog("用户[" + userId + "]删除专线id[" + request.getParameter("webLineID") + "]");
        response.setContentType("text/html;charset=gb2312");
        PrintWriter pw = response.getWriter();
        pw.print("<script language='javascript'>alert('成功删除！' );window.location=('/opennms/abcbank/webline.jsp');</script>");
        pw.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
