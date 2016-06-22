package org.opennms.web.abcbank;

import org.opennms.core.bank.BankLogWriter;
import org.opennms.core.bank.IPSegmentOperater;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * Created by laiguanhui on 2016/2/23.
 */
public class UpdateIPSegmentServlet extends HttpServlet {
    private static final long serialVersionUID = -7718296603096545783L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String tmp = request.getParameter("ipSegID");
        int id = Integer.parseInt(tmp);
        String row = request.getParameter("rowID");
        String bankname = request.getParameter("bankName");
        String dept = request.getParameter("dept-"+row);
        String banktype = request.getParameter("bankType");
        String comment =request.getParameter("comments");

        String userId = request.getRemoteUser();
        String startIP = request.getParameter("startIP-"+row);
        String endIP = request.getParameter("endIP-"+row);

        IPSegmentOperater op = new IPSegmentOperater();
        try{
            op.updateByID(id, "name", bankname + "/" + dept);
            op.updateByID(id, "type", banktype);
            op.updateByID(id, "comment", comment);

            BankLogWriter.getSingle().writeLog("用户[" + userId + "]修改IP段[" + startIP + "-" + endIP + "]，网点名称修改为：" + bankname + "；网点类型修改为：" + banktype + "；备注修改为：" + comment);
            request.setAttribute("update", "true");
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter pw=response.getWriter();
            pw.print("<script language='javascript'>alert('修改成功！');window.location=('/opennms/abcbank/ipsegment.jsp?curPage="
                    + request.getParameter("curPage") + "&bank=" + request.getParameter("bank") + "&dept=" + request.getParameter("dept") + "&state=" + request.getParameter("state") + "');</script>");
            pw.close();

//            RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/abcbank/ipsegment.jsp");
//            dispatcher.forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
