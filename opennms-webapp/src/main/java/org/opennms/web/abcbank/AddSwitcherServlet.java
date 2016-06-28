package org.opennms.web.abcbank;

import org.opennms.core.bank.BankLogWriter;
import org.opennms.core.bank.DesUtil;
import org.opennms.core.bank.Switcher;
import org.opennms.core.bank.SwitcherOperator;
import org.opennms.core.utils.ThreadCategory;
import org.opennms.netmgt.EventConstants;
import org.opennms.netmgt.config.DiscoveryConfigFactory;
import org.opennms.netmgt.config.discovery.DiscoveryConfiguration;
import org.opennms.netmgt.config.discovery.Specific;
import org.opennms.netmgt.model.events.EventBuilder;
import org.opennms.netmgt.model.events.EventProxy;
import org.opennms.web.admin.discovery.AddSpecIP;
import org.opennms.web.admin.discovery.GeneralSettingsLoader;
import org.opennms.web.admin.discovery.ModifyDiscoveryConfigurationServlet;
import org.opennms.web.api.Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

/**
 * Created by laiguanhui on 2016/3/31.
 */
public class AddSwitcherServlet extends HttpServlet {

    private static final long serialVersionUID = 1706602049908810024L;
    protected static ThreadCategory log = ThreadCategory.getInstance("WEB");

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getRemoteUser();
        String name = request.getParameter("name");
        String group = request.getParameter("group");
        String brand = request.getParameter("brand");
        String host = request.getParameter("host").trim();
        String user = request.getParameter("user");
        String password = request.getParameter("password");
        String backup = request.getParameter("backup");
        String recovery = request.getParameter("recovery");
        String wan_ip = request.getParameter("wan_ip");
        String lookback_ip = request.getParameter("lookback_ip").trim();
        String vlan150_ip1 = request.getParameter("vlan150_ip1").trim();
        String vlan150_ip2 = request.getParameter("vlan150_ip2").trim();
        String vlan160_ip1 = request.getParameter("vlan160_ip1").trim();
        String vlan160_ip2 = request.getParameter("vlan160_ip2").trim();
        String vlan170_ip1 = request.getParameter("vlan170_ip1").trim();
        String vlan170_ip2 = request.getParameter("vlan170_ip2").trim();
        String ospf = request.getParameter("ospf");
        String area = request.getParameter("area");
        String comment = request.getParameter("comment");

        try{
            //对密码进行DES加密
            DesUtil du = new DesUtil();
            password = du.encrypt(password);

            Switcher switcher = new Switcher();
            switcher.setName(name);
            switcher.setGroup(group);
            switcher.setBrand(brand);
            switcher.setHost(host);
            switcher.setUser(user);
            switcher.setPassword(password);
            switcher.setBackup(backup);
            switcher.setRecovery(recovery);
            switcher.setWan_ip(wan_ip);
            switcher.setLookback_ip(lookback_ip);
            switcher.setVlan150_ip1(vlan150_ip1);
            switcher.setVlan150_ip2(vlan150_ip2);
            switcher.setVlan160_ip1(vlan160_ip1);
            switcher.setVlan160_ip2(vlan160_ip2);
            switcher.setVlan170_ip1(vlan170_ip1);
            switcher.setVlan170_ip2(vlan170_ip2);
            switcher.setOspf(ospf);
            switcher.setArea(area);
            switcher.setComment(comment);
            SwitcherOperator op = new SwitcherOperator();
            op.insert(switcher);

            AddSpecIP add = new AddSpecIP();
            add.addIP(request, host);
        }catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        BankLogWriter.getSingle().writeLog("用户[" + userId + "]新增交换机[" + host + "]");
        response.setContentType("text/html;charset=gb2312");
        PrintWriter pw=response.getWriter();
        pw.print("<script language='javascript'>alert('成功添加！' );window.location=('/opennms/abcbank/switcher.jsp?update=true&curPage=" + request.getParameter("curPage") + "');</script>");
        pw.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    // 将ip添加到节点中
    private void addSpecificIP(HttpServletRequest request, String ip) throws ServletException {
        Specific newSpecific = new Specific();
        newSpecific.setContent(ip);
        newSpecific.setTimeout(2000);
        newSpecific.setRetries(2);

        DiscoveryConfiguration config = ModifyDiscoveryConfigurationServlet.getDiscoveryConfig();
        //load current general settings
        config = GeneralSettingsLoader.load(request,config);
        config.addSpecific(newSpecific);

        DiscoveryConfigFactory dcf=null;
        try{
            if (log.isDebugEnabled()) {
                StringWriter configString = new StringWriter();
                config.marshal(configString);
                log.debug(configString.toString().trim());
            }
            DiscoveryConfigFactory.init();
            dcf = DiscoveryConfigFactory.getInstance();
            dcf.saveConfiguration(config);
            BankLogWriter.getSingle().writeLog("");
        }catch(Throwable ex){
            log.error("Error while saving configuration. "+ex);
            throw new ServletException(ex);
        }

        EventProxy proxy = null;
        try {
            proxy = Util.createEventProxy();
        } catch (Throwable me) {
            log.error(me.getMessage());
        }

        EventBuilder bldr = new EventBuilder(EventConstants.DISCOVERYCONFIG_CHANGED_EVENT_UEI, "ActionDiscoveryServlet");
        bldr.setHost("host");

        try {
            proxy.send(bldr.getEvent());
        } catch (Throwable me) {
            log.error(me.getMessage());
        }

        log.info("Restart Discovery requested!");
    }
}
