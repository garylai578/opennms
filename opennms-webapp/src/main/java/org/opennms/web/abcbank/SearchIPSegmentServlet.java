package org.opennms.web.abcbank;

import org.opennms.core.bank.IPSegment;
import org.opennms.core.bank.IPSegmentOperater;

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
 * Created by laiguanhui on 2016/4/28.
 */
public class SearchIPSegmentServlet extends HttpServlet {

    private static final long serialVersionUID = -687949691527127672L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String bank = request.getParameter("bank");
        String dept = request.getParameter("dept");
        String state = request.getParameter("state");

        IPSegmentOperater op = new IPSegmentOperater();
        response.setContentType("text/html;charset=gb2312");
        PrintWriter pw=response.getWriter();

        Map<String, String> colAndValue = new HashMap<String, String>();

        if(bank != null && !"".equals(bank)) {
            bank = bank + "/" + dept;
            colAndValue.put("name", bank);
        }
        if(state != null && !"".equals(state))
            colAndValue.put("state", state);

        try {
            IPSegment[] rs = op.andSelect(colAndValue);
            if(rs != null && rs.length > 0){
                request.setAttribute("ipSeg", rs);
                request.setAttribute("bank", request.getParameter("bank"));
                request.setAttribute("dept", request.getParameter("dept"));
                request.setAttribute("state", request.getParameter("state"));
                request.getRequestDispatcher("ipsegment.jsp").forward(request, response);
            } else {
                pw.print("<script language='javascript'>alert('查询无结果，请更换查询内容！' );window.location=('/opennms/abcbank/ipaddress.jsp?curPage="
                        + request.getParameter("curPage") + "&bank=" + request.getParameter("bank") + "&dept=" + request.getParameter("dept") + "&state=" + request.getParameter("state") + "');</script>");
                pw.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
