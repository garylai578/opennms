package org.opennms.web.abcbank;

import org.apache.log4j.Logger;
import org.opennms.core.bank.BankIPAddress;
import org.opennms.core.bank.BankIPAddressOp;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by laiguanhui on 2016/3/17.
 */
public class SearchIPAddressServlet extends HttpServlet {
    private static final long serialVersionUID = 7199011739202086578L;
    final static Logger log =  Logger.getLogger(SearchIPAddressServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String networkType = request.getParameter("network_type");
        String users = request.getParameter("users");
        String dept = request.getParameter("dept");
        String bank = request.getParameter("bank");
        Map<String, String> colAndValue = new HashMap<String, String>();

        if(networkType != null && !"".equals(networkType))
            colAndValue.put("network_type", networkType);
        if(users != null && !"".equals(users))
            colAndValue.put("users", users);
        if(dept != null && !"".equals(dept))
            colAndValue.put("dept", dept);
        if(bank != null && !"".equals(bank))
            colAndValue.put("bank", bank);

        BankIPAddressOp op = new BankIPAddressOp();
        response.setContentType("text/html;charset=gb2312");
        PrintWriter pw=response.getWriter();

        try {
            BankIPAddress[] rs = op.unionSearch(colAndValue);
            if(rs != null && rs.length > 0){
                request.setAttribute("ip_addresses", rs);
                request.getRequestDispatcher("ipaddress.jsp").forward(request, response);
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
