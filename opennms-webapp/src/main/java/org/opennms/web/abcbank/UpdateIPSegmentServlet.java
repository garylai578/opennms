package org.opennms.web.abcbank;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by laiguanhui on 2016/2/23.
 */
public class UpdateIPSegmentServlet extends HttpServlet {
    private static final long serialVersionUID = -7718296603096545783L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("ipSegID");
        String row = request.getParameter("rowID");
        String gateway = request.getParameter("gateway-" + row);
        String mask = request.getParameter("mask-" + row);
        String ipsegment = request.getParameter("ipsegment-" + row);
        String bankname = request.getParameter("bankname-" + row);
        String banktype = request.getParameter("banktype-" + row);
        String createdate = request.getParameter("createdate-" + row);
        String state = request.getParameter("state-" + row);
        String comment =request.getParameter("comment-" + row);

    }

}
