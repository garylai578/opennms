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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by laiguanhui on 2016/4/28.
 */
public class SearchSwitcherServlet extends HttpServlet {

    private static final long serialVersionUID = -8470878390247382363L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String group = request.getParameter("group");
        String brand = request.getParameter("brand");
        String ip = request.getParameter("ip");
        String comment = request.getParameter("comment");
        Map<String, String> colAndValue = new HashMap<String, String>();

        if(name != null && !"".equals(name))
            colAndValue.put("name", name);
        if(group != null && !"".equals(group))
            colAndValue.put("groups", group);
        if(brand != null && !"".equals(brand))
            colAndValue.put("brand", brand);
        if(ip != null && !"".equals(ip))
            colAndValue.put("host", ip);
        if(comment != null && !"".equals(comment))
            colAndValue.put("comment", comment);

        SwitcherOperator op = new SwitcherOperator();
        response.setContentType("text/html;charset=gb2312");
        PrintWriter pw=response.getWriter();

        try {
            Switcher[] rs = op.andSelect(colAndValue);
            if(rs != null && rs.length > 0){
                request.setAttribute("switchers", rs);
                request.setAttribute("name", request.getParameter("name"));
                request.setAttribute("group", request.getParameter("group"));
                request.setAttribute("brand", request.getParameter("brand"));
                request.setAttribute("ip", request.getParameter("ip"));
                request.setAttribute("comment", request.getParameter("comment"));
                request.getRequestDispatcher("switcher.jsp").forward(request, response);
            } else {
                pw.print("<script language='javascript'>alert('查询无结果，请更换查询内容！' );window.location=('/opennms/abcbank/switcher.jsp');</script>");
                pw.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
