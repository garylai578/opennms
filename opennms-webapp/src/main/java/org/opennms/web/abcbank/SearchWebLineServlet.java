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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by laiguanhui on 2016/3/18.
 */
public class SearchWebLineServlet extends HttpServlet {
    private static final long serialVersionUID = 3191671785887252427L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String type = request.getParameter("type");
        String applicant = request.getParameter("applicant");
        String approver = request.getParameter("approver");
        String bank = request.getParameter("bank");

        Map<String, String> colAndValue = new HashMap<String, String>();

        if(type != null && !"".equals(type))
            colAndValue.put("type", type);
        if(applicant != null && !"".equals(applicant))
            colAndValue.put("applicant", applicant);
        if(approver != null && !"".equals(approver))
            colAndValue.put("approver", approver);
        if(bank != null && !"".equals(bank))
            colAndValue.put("bank", bank);

        WebLineOperator op = new WebLineOperator();
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter pw=response.getWriter();

        try {
            WebLine[] rs = op.andSearch(colAndValue);

            if(rs != null && rs.length > 0){
                request.getSession().setAttribute("webLines", rs);
                request.setAttribute("type", request.getParameter("type"));
                request.setAttribute("applicant", request.getParameter("applicant"));
                request.setAttribute("approver", request.getParameter("approver"));
                request.setAttribute("bank", request.getParameter("bank"));
                request.setAttribute("dept", request.getParameter("dept"));
                request.getRequestDispatcher("webline.jsp").forward(request, response);
            } else {
                pw.print("<script language='javascript'>alert('查询无结果，请更换查询内容！' );window.location=('/opennms/abcbank/webline.jsp?curPage=" + request.getParameter("curPage")
                        + "&type=" + request.getParameter("type") + "&applicant=" + request.getParameter("applicant") + "&approver=" + request.getParameter("approver")
                        + "&bank=" + request.getParameter("bank") + "');</script>");
                pw.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
