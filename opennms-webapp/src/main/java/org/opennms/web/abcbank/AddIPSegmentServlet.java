package org.opennms.web.abcbank;

import org.opennms.core.bank.IPPoolCaculater;
import org.opennms.core.bank.IPSegment;
import org.opennms.core.bank.IPSegmentOperater;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 * Created by laiguanhui on 2016/2/22.
 */
public class AddIPSegmentServlet extends HttpServlet {
    private static final long serialVersionUID = -3675392550713648442L;

    /** {@inheritDoc} */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int num = Integer.getInteger(request.getParameter("ip_num"));
            String name = request.getParameter("bank_name");
            String type = request.getParameter("bank_type");
            String comment = request.getParameter("comment");

            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date date = new java.util.Date();

            IPSegmentOperater op = new IPSegmentOperater();
            IPPoolCaculater cal = new IPPoolCaculater(op.selectLastIP(), num);

            IPSegment seg = new IPSegment();
            seg.setIpPool(cal.getIPPool());
            seg.setState("在用");
            seg.setBankname(name);
            seg.setBanktype(type);
            seg.setComment(comment);
            seg.setCreateTime(sf.format(date));

            op.insert(seg);
            RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/abcbank/ipsegment.jsp");
            dispatcher.forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
