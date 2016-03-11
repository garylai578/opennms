package org.opennms.web.abcbank;

import org.opennms.core.bank.IPPoolCaculater;
import org.opennms.core.bank.IPSegment;
import org.opennms.core.bank.IPSegmentOperater;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 * Created by laiguanhui on 2016/2/22.
 */
public class AddIPSegmentServlet extends HttpServlet {
    private static final long serialVersionUID = -3675392550713648442L;
    private ServletConfig config;

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.config = config;
    }

    /** {@inheritDoc} */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String initIP = this.config.getInitParameter("InitIP");
        PrintWriter pw=response.getWriter();
        try {
            String numString = request.getParameter("ipNum");
            int num = Integer.parseInt(numString);
            String name = request.getParameter("bankName");
            String type = request.getParameter("bankType");
            String comment = request.getParameter("comments");

            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date date = new java.util.Date();

            IPSegmentOperater op = new IPSegmentOperater();

            pw.print("initIP start:" + initIP);

            initIP = op.selectLastIP();

            pw.print("initIP:" + initIP);
            pw.close();

            if(initIP==null){
                initIP = this.config.getInitParameter("InitIP");
            }

            pw.print("cal start:initIP and num = " + initIP + " " + num);
            pw.flush();

            IPPoolCaculater cal = new IPPoolCaculater(initIP, num);

            pw.print("cal end:" + cal.getIPPool().getStartIP());
            pw.flush();

            IPSegment seg = new IPSegment();
            seg.setIpPool(cal.getIPPool());
            seg.setState("在用");
            seg.setBankname(name);
            seg.setBanktype(type);
            seg.setComment(comment);
            seg.setCreateTime(sf.format(date));

            pw.print("seg:" + seg.getEndIP());
            pw.flush();
            op.insert(seg);

            response.setContentType("text/html;charset=gb2312");
            pw.print("<script language='javascript'>alert('成功添加！' );window.location=('/opennms/abcbank/ipsegment.jsp');</script>");
            pw.close();

//            RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/abcbank/ipsegment.jsp");
//            dispatcher.forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
