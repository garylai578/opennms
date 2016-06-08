package org.opennms.web.admin.nodeManagement;

import org.opennms.core.bank.BankLogWriter;
import org.opennms.netmgt.EventConstants;
import org.opennms.netmgt.model.events.EventBuilder;
import org.opennms.netmgt.model.events.EventProxy;
import org.opennms.web.api.Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.opennms.core.utils.InetAddressUtils.addr;

/**
 * Created by laiguanhui on 2016/6/7.
 */
public class SnmpBatchConfigServlet extends HttpServlet {
    private static final long serialVersionUID = -7420802609905871689L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String batchComm = request.getParameter("batchComm");
        String[] batchCommands = batchComm.split("\n");
        String msg = "已完成";
        BankLogWriter log = BankLogWriter.getSingle();
        log.writeLog("batch:" + batchComm);
        for(int i = 0; i < batchCommands.length; ++i){
            String line = batchCommands[i];
            log.writeLog("line:" + line);
            String[] items = line.split(","); //第一个IP地址,最后一个IP地址,团体名,超时,版本,重试,端口
            if(items.length != 7) {
                msg += "；第" + (i+1) + "行格式有误";
                continue;
            }

            String firstIPAddress = items[0];
            if(!isIP(firstIPAddress)){
                msg += "；ip地址有误：" + firstIPAddress;
                continue;
            }
            String lastIPAddress = items[1];
            String communityString = items[2];
            if("".equals(communityString)){
                msg += "；ip地址[" + firstIPAddress + "]对应的团体名不能为空";
                continue;
            }

            String timeout = items[3];
            String version = items[4];
            String retryCount = items[5];
            String port = items[6];
            EventBuilder bldr = new EventBuilder(EventConstants.CONFIGURE_SNMP_EVENT_UEI, "web ui");
            bldr.setInterface(addr(firstIPAddress));
            bldr.setService("SNMP");

            bldr.addParam(EventConstants.PARM_FIRST_IP_ADDRESS, firstIPAddress);
            bldr.addParam(EventConstants.PARM_LAST_IP_ADDRESS, lastIPAddress);
            bldr.addParam(EventConstants.PARM_COMMUNITY_STRING, communityString);

            if ( timeout.length() > 0) {
                bldr.addParam(EventConstants.PARM_TIMEOUT, timeout);
            }
            if ( port.length() > 0 ) {
                bldr.addParam(EventConstants.PARM_PORT, port);
            }
            if ( retryCount.length() > 0 ) {
                bldr.addParam(EventConstants.PARM_RETRY_COUNT, retryCount);
            }
            if ( version.length() > 0 ) {
                bldr.addParam(EventConstants.PARM_VERSION, version);
            }else{
                bldr.addParam(EventConstants.PARM_VERSION, "v2c");
            }
            try {
                EventProxy eventProxy = Util.createEventProxy();
                if (eventProxy != null) {
                    eventProxy.send(bldr.getEvent());
                } else {
                    throw new ServletException("EventProxy对象为null, 不能发送事件：" + bldr.getEvent().getUei());
                }
            } catch (Throwable e) {
                throw new ServletException("不能发送事件：" + bldr.getEvent().getUei(), e);
            }
        }

        if("".equals(msg))
            msg = "成功导入";
        response.setContentType("text/html;charset=gb2312");
        PrintWriter pw=response.getWriter();
        pw.print("<script language='javascript'>alert('批量操作结果：" + msg + "' );window.location=('/opennms/admin/snmpConfig.jsp');</script>");
        pw.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    private boolean isIP(String addr)
    {
        if(addr.length() < 7 || addr.length() > 15 || "".equals(addr))
        {
            return false;
        }
        /**
         * 判断IP格式和范围
         */
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

        Pattern pat = Pattern.compile(rexp);

        Matcher mat = pat.matcher(addr);

        boolean ipAddress = mat.find();

        return ipAddress;
    }
}
