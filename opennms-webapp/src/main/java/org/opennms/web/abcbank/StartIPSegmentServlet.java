package org.opennms.web.abcbank;

import org.opennms.core.bank.IPSegmentOperater;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by laiguanhui on 2016/2/22.
 */
public class StartIPSegmentServlet extends HttpServlet {

    private static final long serialVersionUID = -1759156388543404811L;

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.getInteger(request.getParameter("ipSegID"));
        IPSegmentOperater op = new IPSegmentOperater();
        try {
            op.updateByID(id, "state", "启用");
            RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/abcbank/ipsegment.jsp");
            dispatcher.forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
