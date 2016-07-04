package org.opennms.web.abcbank;

import org.opennms.core.bank.BankLogWriter;
import org.opennms.core.bank.WebLineOperator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * Created by laiguanhui on 2016/7/1.
 */
public class UpdateWeblineServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getRemoteUser();
        request.setCharacterEncoding("UTF-8");
        String tmp = request.getParameter("webLineID");
        int id = Integer.parseInt(tmp);
        String row = request.getParameter("rowID");
        String state = request.getParameter("state-" + row);
        String type = request.getParameter("type-" + row);
        String applicant = request.getParameter("applicant-" + row);
        String contact = request.getParameter("contact-" + row);
        String approver = request.getParameter("approver-" + row);
        String address = request.getParameter("address-" + row);
        String start_date = request.getParameter("start_date-" + row);
        String rent = request.getParameter("rent-" + row);
        String vlan_num = request.getParameter("vlan_num-"+row);
        String port = request.getParameter("port-" + row);
        String inter = request.getParameter("inter-" + row);
        String comment = request.getParameter("comment-" + row);

        WebLineOperator op = new WebLineOperator();
        try{
            op.updateByID(id, "state", "'" + state +"'");
            op.updateByID(id, "type", "'" + type + "'");
            op.updateByID(id, "applicant", "'" + applicant + "'");
            op.updateByID(id, "contact", "'" + contact + "'");
            op.updateByID(id, "approver", "'" + approver + "'");
            op.updateByID(id, "address", "'" + address + "'");
            op.updateByID(id, "rent", "'" + rent + "'");
            op.updateByID(id, "vlan_num", "'" + vlan_num + "'");
            op.updateByID(id, "port", "'" + port + "'");
            op.updateByID(id, "interface", "'" + inter + "'");
            op.updateByID(id, "comment", "'" + comment + "'");
            if(start_date != null && !start_date.equals(""))
                op.updateByID(id, "start_date", "'" + start_date + "'");

            BankLogWriter.getSingle().writeLog("用户[" + userId + "]更新专线[" + id  + "]，状态更新为：" + state + ", 类型更新为：" + type + "，申请人更新为："
                    + applicant + "，联系方式更新为：" + contact + ", 审批人更新为：" + approver + ", 地址更新为：" + address + "， 开通日期更新为：" + start_date
                    + "，月租更新为：" + rent + "，VLAN编号更新为：" + vlan_num + "，物理端口更新为：" + port + "运营商接口更新为：" + inter + "备注更新为：" +comment);
            response.setContentType("text/html;charset=UTF-8");
            request.setAttribute("update", "true");
            PrintWriter pw=response.getWriter();
            pw.print("<script language='javascript'>alert('修改成功' );window.location=('/opennms/abcbank/webline.jsp?update=true&curPage=" + request.getParameter("curPage")
                    + "&type=" + request.getParameter("type") + "&applicant=" + request.getParameter("applicant") + "&approver=" + request.getParameter("approver")
                    + "&bank=" + request.getParameter("bank") + "&dept=" + request.getParameter("dept") + "');</script>");
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
