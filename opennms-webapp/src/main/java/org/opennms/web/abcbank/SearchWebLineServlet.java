package org.opennms.web.abcbank;

import org.opennms.core.bank.WebLine;
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
public class SearchWebLineServlet extends HttpServlet {
    private static final long serialVersionUID = 3191671785887252427L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String key = request.getParameter("searchKey");
        WebLineOperator op = new WebLineOperator();
        response.setContentType("text/html;charset=gb2312");
        PrintWriter pw=response.getWriter();

        try {
            String[] cols = {"applicant", "dept", "type", "start_date"};
            WebLine[] rs = op.search(cols, key);

            if(rs != null && rs.length > 0){
                request.setAttribute("webLines", rs);
                request.getRequestDispatcher("webline.jsp").forward(request, response);
            } else {
                pw.print("<script language='javascript'>alert('查询无结果，请更换查询内容！' );window.location=('/opennms/abcbank/webline.jsp');</script>");
                pw.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
