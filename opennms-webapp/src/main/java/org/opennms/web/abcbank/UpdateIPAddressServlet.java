package org.opennms.web.abcbank;

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
public class UpdateIPAddressServlet extends HttpServlet {

    private static final long serialVersionUID = 1697488814583614306L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String tmp = request.getParameter("ipAddrID");
        int id = Integer.parseInt(tmp);
        String row = request.getParameter("rowID");
        String network_type = request.getParameter("network_type-" + row);
        String users = request.getParameter("users-" + row);
        String bank = request.getParameter("bank-" + row);
        String dept = request.getParameter("dept-" + row);
        String model = request.getParameter("model-" + row);
        String equip_brand = request.getParameter("equip_brand-" + row);
        String equip_type = request.getParameter("equip_brand-" + row);
        String application = request.getParameter("app-" + row);
        String comment = request.getParameter("comment-" + row);

        BankIPAddressOp op = new BankIPAddressOp();
        try{
            op.updateByID(id, "network_type", "'" + network_type +"'");
            op.updateByID(id, "users", "'" + users + "'");
            op.updateByID(id, "bank", "'" + bank + "'");
            op.updateByID(id, "dept", "'" + dept + "'");
            op.updateByID(id, "model", "'" + model + "'");
            op.updateByID(id, "equip_brand", "'" + equip_brand + "'");
            op.updateByID(id, "equip_type", "'" + equip_type + "'");
            op.updateByID(id, "application", "'" + application + "'");
            op.updateByID(id, "comment", comment);

            response.setContentType("text/html;charset=gb2312");
            PrintWriter pw=response.getWriter();
            pw.print("<script language='javascript'>alert('修改成功！');window.location=('/opennms/abcbank/ipaddress.jsp');</script>");
            pw.close();

//            RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/abcbank/ipsegment.jsp");
//            dispatcher.forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
