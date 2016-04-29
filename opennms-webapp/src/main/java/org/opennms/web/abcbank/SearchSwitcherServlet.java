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
 * Created by laiguanhui on 2016/4/28.
 */
public class SearchSwitcherServlet extends HttpServlet {

    private static final long serialVersionUID = -8470878390247382363L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String type = request.getParameter("searchType");
        String content = request.getParameter("searchCont");

        SwitcherOperator op = new SwitcherOperator();
        response.setContentType("text/html;charset=gb2312");
        PrintWriter pw=response.getWriter();

        try {
            Switcher[] rs = op.select(type, content);
            if(rs != null && rs.length > 0){
                request.setAttribute("switchers", rs);
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
