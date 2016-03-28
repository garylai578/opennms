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
public class AddWebLineServlet extends HttpServlet {

    private static final long serialVersionUID = 5322209775976767374L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String type = request.getParameter("type");
        String applicant = request.getParameter("applicant");
        String approver = request.getParameter("approver");
        String contact = request.getParameter("contact");
        String dept = request.getParameter("dept");
        String address = request.getParameter("address");
        String start_date = request.getParameter("start_date");
        String rent = request.getParameter("rent");
        String vlan_num = request.getParameter("vlan_num");
        String port = request.getParameter("port");
        String inter = request.getParameter("inter");
        String comment = request.getParameter("comment");

        try{
            WebLine line = new WebLine();
            line.setType(type);
            line.setApplicant(applicant);
            line.setApprover(approver);
            line.setContact(contact);
            line.setDept(dept);
            line.setAddress(address);
            line.setStart_date(start_date);
            line.setRent(rent);
            line.setVlan_num(vlan_num);
            line.setPort(port);
            line.setInter(inter);
            line.setComment(comment);
            WebLineOperator op = new WebLineOperator();
            op.insert(line);
        }catch (SQLException e) {
            e.printStackTrace();
        }

        response.setContentType("text/html;charset=gb2312");
        PrintWriter pw=response.getWriter();
        pw.print("<script language='javascript'>alert('成功添加！' );window.location=('/opennms/abcbank/webline.jsp');</script>");
        pw.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
