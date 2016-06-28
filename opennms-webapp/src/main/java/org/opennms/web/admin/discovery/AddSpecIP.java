package org.opennms.web.admin.discovery;

import org.opennms.core.bank.BankLogWriter;
import org.opennms.netmgt.EventConstants;
import org.opennms.netmgt.config.DiscoveryConfigFactory;
import org.opennms.netmgt.config.discovery.DiscoveryConfiguration;
import org.opennms.netmgt.config.discovery.Specific;
import org.opennms.netmgt.model.events.EventBuilder;
import org.opennms.netmgt.model.events.EventProxy;
import org.opennms.web.api.Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by laiguanhui on 2016/6/28.
 */
public class AddSpecIP {

    public void addIP(HttpServletRequest request, String ip) throws ServletException {
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

            DiscoveryConfigFactory.init();
            dcf = DiscoveryConfigFactory.getInstance();
            dcf.saveConfiguration(config);
        }catch(Throwable ex){
            throw new ServletException(ex);
        }

        EventProxy proxy = null;
        try {
            proxy = Util.createEventProxy();
        } catch (Throwable me) {
            me.printStackTrace();
        }

        EventBuilder bldr = new EventBuilder(EventConstants.DISCOVERYCONFIG_CHANGED_EVENT_UEI, "ActionDiscoveryServlet");
        bldr.setHost("host");

        try {
            proxy.send(bldr.getEvent());
        } catch (Throwable me) {
            me.printStackTrace();
        }
        BankLogWriter.getSingle().writeLog("Restart Discovery requested, add IP:" + ip);
    }
}
