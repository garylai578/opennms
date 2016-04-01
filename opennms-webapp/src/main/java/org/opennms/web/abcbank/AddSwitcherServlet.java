package org.opennms.web.abcbank;

import org.opennms.core.bank.Switcher;
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
public class AddSwitcherServlet extends HttpServlet {

    private static final long serialVersionUID = 1706602049908810024L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String brand = request.getParameter("brand");
        String host = request.getParameter("host");
        String user = request.getParameter("user");
        String password = request.getParameter("password");
        String backup = request.getParameter("backup");
        String recovery = request.getParameter("recovery");
        String comment = request.getParameter("comment");

        try{
            Switcher switcher = new Switcher();
            switcher.setBrand(brand);
            switcher.setHost(host);
            switcher.setUser(user);
            switcher.setPassword(password);
            switcher.setBackup(backup);
            switcher.setRecovery(recovery);
            switcher.setComment(comment);
            SwitcherOperator op = new SwitcherOperator();
            op.insert(switcher);
        }catch (SQLException e) {
            e.printStackTrace();
        }

        response.setContentType("text/html;charset=gb2312");
        PrintWriter pw=response.getWriter();
        pw.print("<script language='javascript'>alert('成功添加！' );window.location=('/opennms/abcbank/switcher.jsp');</script>");
        pw.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
