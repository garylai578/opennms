package org.opennms.web.abcbank;

import org.apache.log4j.Logger;
import org.opennms.core.bank.BankLogWriter;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by laiguanhui on 2016/2/22.
 */
public class AddIPSegmentServlet extends HttpServlet {
    private static final long serialVersionUID = -3675392550713648442L;
    private ServletConfig config;
    final static Logger log =  Logger.getLogger(AddIPSegmentServlet.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.config = config;
    }

    /** {@inheritDoc} */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getRemoteUser();
        String ipSeg = request.getParameter("ipSeg");
        PrintWriter pw=response.getWriter();
        response.setContentType("text/html;charset=gb2312");
        String backMsg = "成功添加！";
        String startIP = "";
        String endIP = "";
        try {
            int flag = 0;
            String numString = request.getParameter("ipNum");
            int num = Integer.parseInt(numString);
            String name = request.getParameter("bankName");
            String type = request.getParameter("bankType");
            String comment = request.getParameter("comments");
            String dept = request.getParameter("dept");
            name = name + "/" + dept;

            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            //首先检查停用超过7天的ip段是否符合条件
            IPSegmentOperater op = new IPSegmentOperater();
            IPSegment[] rs = op.selectAllUnused(ipSeg);
            for(IPSegment ip : rs){
                int id = Integer.parseInt(ip.getId());
                int end = Integer.parseInt(ip.getEndIP().trim().split("\\.")[3]);
                int start = Integer.parseInt(ip.getStartIP().trim().split("\\.")[3]);
//                log.warn("id:" + id +". end:" + end + ". start:" +start + ". num:" + num);
                if(end - start + 1 == num) {
                    //对停用时间超过7天的ip段进行重新分配
                    String stopTime = ip.getStopTime();
                    SimpleDateFormat sf2 = new SimpleDateFormat("yyyy-MM-dd");
                    if (stopTime != null) {
                        log.debug("stoptime:" + stopTime);
                        try {
                            long today = sf2.parse(sf2.format(date)).getTime();
                            long stop = sf2.parse(stopTime).getTime();
                            long inten = (today - stop) / (1000 * 60 * 60 * 24);
                            log.debug("inten:" + inten);
                            if (inten > 7) {
                                op.updateByID(id, "state", "在用");
                                op.updateByID(id, "createtime", sf.format(date));
                                op.updateByID(id, "stoptime", "null");
                                op.updateByID(id, "name", name);
                                op.updateByID(id, "type", type);
                                op.updateByID(id, "comment", comment);
                                flag = 1;
                                log.debug("update ipsegment where id =" + id);
                                startIP = ip.getStartIP();
                                endIP = ip.getEndIP();
                                break;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            //如果在已有停用的ip段里面找不到合适的，则新建一个。
            if(flag == 0) {
                String[] ipSegs = ipSeg.split("-");
                String stopIP = ipSegs[1];
                String initIP = op.selectLastIP(ipSeg);
                if (initIP == null) {
                    initIP = ipSegs[0];
                }

                IPPoolCaculater cal = new IPPoolCaculater(initIP, stopIP, num);
                int result = cal.caculate();
                if(result == 0){
                    pw.print("<script language='javascript'>alert('所选ip段不够分配，请选择其他ip段！' );window.location=('/opennms/abcbank/ipsegment.jsp?curPage="
                            + request.getParameter("curPage") + "&bank=" + request.getParameter("bank") + "&dept=" + request.getParameter("dept") + "&state=" + request.getParameter("state") + "');</script>");
                    pw.close();
                    return;
                }else if(result == 1) {
                    IPSegment seg = new IPSegment();
                    seg.setSegment(ipSeg);
                    seg.setIpPool(cal.getIPPool());
                    seg.setState("在用");
                    seg.setBankname(name);
                    seg.setBanktype(type);
                    seg.setComment(comment);
                    seg.setCreateTime(sf.format(date));
                    op.insert(seg);
                    startIP = seg.getStartIP();
                    endIP = seg.getEndIP();
                    request.setAttribute("update", "true");
                }else{
                    backMsg = "分配ip段失败！";
                }
            }

            BankLogWriter.getSingle().writeLog("用户[" + userId +"]新增IP段[" + startIP + "-" + endIP + "]：" + backMsg);
            pw.print("<script language='javascript'>alert('"+ backMsg + "' );window.location=('/opennms/abcbank/ipaddress.jsp?curPage="
                    + request.getParameter("curPage") + "&bank=" + request.getParameter("bank") + "&dept=" + request.getParameter("dept") + "&state=" + request.getParameter("state") + "');</script>");
            pw.close();

//            RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/abcbank/ipsegment.jsp");
//            dispatcher.forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
