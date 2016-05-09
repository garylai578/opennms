package org.opennms.web.abcbank;

import org.opennms.core.bank.BankLogWriter;
import org.opennms.core.bank.SwitcherStatsOperator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * Created by laiguanhui on 2016/5/6.
 */
public class DelSwitcherStatsServlet extends HttpServlet {
    private static final long serialVersionUID = 6535987553893077958L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getRemoteUser();
        String ip = request.getParameter("ip");
        SwitcherStatsOperator op = new SwitcherStatsOperator();
        try {
            op.delete(ip);
            BankLogWriter.getSingle().writeLog("用户[" + userId + "]移除流量统计中的交换机[" + ip + "]");
            response.setContentType("text/html;charset=gb2312");
            PrintWriter pw=response.getWriter();
            pw.print("<script language='javascript'>alert('成功移除！' );window.location=('/opennms/report/switcher.jsp');</script>");
            pw.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
