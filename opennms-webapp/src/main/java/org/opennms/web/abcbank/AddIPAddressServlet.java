package org.opennms.web.abcbank;

import org.apache.log4j.Logger;
import org.opennms.core.bank.BankIPAddress;
import org.opennms.core.bank.BankIPAddressOp;
import org.opennms.core.bank.BankLogWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by laiguanhui on 2016/3/16.
 */

public class AddIPAddressServlet extends HttpServlet  {
    private static final long serialVersionUID = 1483329198641646857L;
    final static Logger log =  Logger.getLogger(AddIPSegmentServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String ipAddr = request.getParameter("ipAddr").trim();
        String network_type = request.getParameter("network_type");
        String mask = request.getParameter("mask").trim();
        String gateway = request.getParameter("gateway").trim();
        String mac = request.getParameter("mac").trim();
        String apply_date = request.getParameter("apply_date");
        String start_date = request.getParameter("start_date");
        String users = request.getParameter("users");
        String bank = request.getParameter("bank");
        String dept = request.getParameter("dept");
        String equip_type = request.getParameter("equip_type");
        String equip_brand = request.getParameter("equip_brand");
        String model = request.getParameter("model");
        String application = request.getParameter("application");
        String state = request.getParameter("state");
        String comment = request.getParameter("comment");
        PrintWriter pw=response.getWriter();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String userId = request.getRemoteUser();

        try {
            //首先检查是否有停用超过7天的ip，有则更新，没有则新建
            int flag = 0;
            BankIPAddressOp op = new BankIPAddressOp();
            BankIPAddress[] rs = op.selectAllUnused();
            for (BankIPAddress ip : rs) {
                int id = Integer.parseInt(ip.getId());
                if (ipAddr.equals(ip.getIp())) {
                    String stopTime = ip.getStop_date();
                    SimpleDateFormat sf2 = new SimpleDateFormat("yyyy-MM-dd");
                    if (stopTime != null) {
                        log.debug("stoptime:" + stopTime);
                        try {
                            long today = sf2.parse(sf2.format(date)).getTime();
                            long stop = sf2.parse(stopTime).getTime();
                            long inten = (today - stop) / (1000 * 60 * 60 * 24);
                            log.debug("inten:" + inten);
                            if (inten > 7) {
                                op.update(ip);
                                flag = 1;
                                log.debug("update ipsegment where id =" + id);
                                break;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            //如果在已有停用的ip段里面找不到合适的，则新建一个。
            if (flag == 0) {
                BankIPAddress ip = new BankIPAddress();
                ip.setIp(ipAddr);
                ip.setNetwork_type(network_type);
                ip.setMask(mask);
                ip.setGateway(gateway);
                ip.setMac(mac);
                ip.setApply_date(apply_date);
                ip.setStart_date(start_date);
                ip.setUsers(users);
                ip.setBank(bank);
                ip.setDept(dept);
                ip.setEquip_type(equip_type);
                ip.setEquip_brand(equip_brand);
                ip.setModel(model);
                ip.setApplication(application);
                ip.setState(state);
                ip.setComment(comment);
                op.insert(ip);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        BankLogWriter.getSingle().writeLog("用户[" + userId + "]新增IP[" + ipAddr + "]");

        response.setContentType("text/html;charset=gb2312");
        pw.print("<script language='javascript'>alert('成功添加！' );window.location=('/opennms/abcbank/ipaddress.jsp');</script>");
        pw.close();

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
