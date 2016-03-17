package org.opennms.web.abcbank;

import org.opennms.core.bank.BankIPAddress;
import org.opennms.core.bank.BankIPAddressOp;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * Created by laiguanhui on 2016/3/17.
 */
public class SearchIPAddressServlet extends HttpServlet {
    private static final long serialVersionUID = 7199011739202086578L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String ip = request.getParameter("searchIP");
        BankIPAddressOp op = new BankIPAddressOp();
        response.setContentType("text/html;charset=gb2312");
        PrintWriter pw=response.getWriter();

        try {
            BankIPAddress[] rs = op.search("ip", ip);
            if(rs != null){
                request.setAttribute("ip_addresses", rs);
                request.getRequestDispatcher("/opennms/abcbank/ipaddress.jsp").forward(request, response);
            } else {
                pw.print("<script language='javascript'>alert('查询无结果，请更换查询内容！' );window.location=('/opennms/abcbank/ipaddress.jsp');</script>");
                pw.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
