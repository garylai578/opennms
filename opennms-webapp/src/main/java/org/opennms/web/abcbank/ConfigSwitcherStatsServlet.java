package org.opennms.web.abcbank;

import org.opennms.core.bank.BankLogWriter;
import org.opennms.core.bank.SwitcherStats;
import org.opennms.core.bank.SwitcherStatsOperator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by laiguanhui on 2016/5/5.
 */
public class ConfigSwitcherStatsServlet extends HttpServlet {

    private static final long serialVersionUID = 7336480973529686866L;
    List<String> oldIPs = new ArrayList<String>();
    List<String> newIPs = new ArrayList<String>();
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getRemoteUser();
        String selIPs = request.getParameter("ips");
        String inputIPs = request.getParameter("input-ips");

        // 获取已经存在的ip列表
        SwitcherStats[] sss = new SwitcherStats[0];
        try {
            SwitcherStatsOperator so = new SwitcherStatsOperator();
            sss = so.selectAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(SwitcherStats ss :sss){
            oldIPs.add(ss.getIp());
        }
        insertSwitcherStats(selIPs.split("\t"));
        insertSwitcherStats(inputIPs.split("\r\n"));

        // 写操作日志
        String newIPString = "";
        for(String ip : newIPs)
            newIPString += ip + ", ";
        BankLogWriter.getSingle().writeLog("用户[" + userId + "]增加需要统计流量的交换机[" + newIPString + "]");

        response.setContentType("text/html;charset=gb2312");
        PrintWriter pw=response.getWriter();
        pw.print("<script language='javascript'>alert('配置成功！' );window.location=('/opennms/report/switcher.jsp');</script>");
        pw.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
    
    private void insertSwitcherStats(String[] ips){
        SwitcherStatsOperator op = new SwitcherStatsOperator();
        for(String ip : ips){
            if(!oldIPs.contains(ip)) {
                SwitcherStats ss = new SwitcherStats(ip);
                try {
                    newIPs.add(ip);
                    op.insert(ss);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
