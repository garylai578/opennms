/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2012 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2012 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.netmgt.linkd;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennms.core.test.MockLogAppender;
import org.opennms.core.test.OpenNMSJUnit4ClassRunner;
import org.opennms.core.test.db.annotations.JUnitTemporaryDatabase;
import org.opennms.core.test.snmp.annotations.JUnitSnmpAgent;
import org.opennms.core.test.snmp.annotations.JUnitSnmpAgents;
import org.opennms.core.utils.BeanUtils;
import org.opennms.netmgt.capsd.Capsd;
import org.opennms.netmgt.dao.IpInterfaceDao;
import org.opennms.netmgt.model.OnmsIpInterface;
import org.opennms.netmgt.model.OnmsSnmpInterface;
import org.opennms.test.JUnitConfigurationEnvironment;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author <a href="mailto:brozow@opennms.org">Mathew Brozowski</a>
 * @author <a href="mailto:antonio@opennme.it">Antonio Russo</a>
 * @author <a href="mailto:alejandro@opennms.org">Alejandro Galue</a>
 */

@RunWith(OpenNMSJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath:/META-INF/opennms/mockEventIpcManager.xml",
        "classpath:/META-INF/opennms/applicationContext-dao.xml",
        "classpath:/META-INF/opennms/applicationContext-soa.xml",
        "classpath:/META-INF/opennms/applicationContext-daemon.xml",
        "classpath:/META-INF/opennms/applicationContext-commonConfigs.xml",
        "classpath:/META-INF/opennms/applicationContext-capsd.xml",
        // import simple defined events
        "classpath:/META-INF/opennms/smallEventConfDao.xml",
        // Override the capsd config with a stripped-down version
        "classpath:/META-INF/opennms/capsdTest.xml",
        // override snmp-config configuration
        "classpath:/META-INF/opennms/applicationContext-proxy-snmp.xml"
})
@JUnitConfigurationEnvironment(systemProperties="org.opennms.provisiond.enableDiscovery=false")
@JUnitTemporaryDatabase
public class LinkdNms17216CapsdNetworkBuilderTest extends LinkdNms17216NetworkBuilder implements InitializingBean {

    
    @Autowired
    private IpInterfaceDao m_interfaceDao;

    @Autowired
    private Capsd m_capsd;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        BeanUtils.assertAutowiring(this);
    }

    @Before
    public void setUp() throws Exception {
        Properties p = new Properties();
        p.setProperty("log4j.logger.org.hibernate.SQL", "WARN");

        MockLogAppender.setupLogging(p);
        assertTrue("Capsd must not be null", m_capsd != null);
//        assertTrue("Linkd must not be null", m_linkd != null);
        
    }


    @Test
    @JUnitSnmpAgents(value={
            @JUnitSnmpAgent(host=ROUTER1_IP, port=161, resource="classpath:linkd/nms17216/router1-walk.txt")
    })
    @Transactional
    public final void testRouter1() throws MarshalException, ValidationException, IOException {
        m_capsd.init();
        m_capsd.start();
        m_capsd.scanSuspectInterface(ROUTER1_IP);
        

        List<OnmsIpInterface> ips = m_interfaceDao.findByIpAddress(ROUTER1_IP);
        assertTrue("Has only one ip interface", ips.size() == 1);

        OnmsIpInterface ip = ips.get(0);

        for (OnmsIpInterface ipinterface: ip.getNode().getIpInterfaces()) {
            if (ipinterface.getIfIndex() != null )
                System.out.println("ROUTER1_IP_IF_MAP.put(InetAddress.getByName(\""+ipinterface.getIpHostName()+"\"), "+ipinterface.getIfIndex()+");");
        }

        for (OnmsSnmpInterface snmpinterface: ip.getNode().getSnmpInterfaces()) {
            if ( snmpinterface.getIfName() != null)
            System.out.println("ROUTER1_IF_IFNAME_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getIfName()+"\");");
            if (snmpinterface.getIfDescr() != null)
            System.out.println("ROUTER1_IF_IFDESCR_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getIfDescr()+"\");");
            if (snmpinterface.getPhysAddr() != null)
            System.out.println("ROUTER1_IF_MAC_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getPhysAddr()+"\");");            
            if (snmpinterface.getIfAlias() != null)
            System.out.println("ROUTER1_IF_IFALIAS_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getIfAlias()+"\");");            
        }
        
        m_capsd.stop();

        
    }

    @Test
    @JUnitSnmpAgents(value={
            @JUnitSnmpAgent(host=ROUTER2_IP, port=161, resource="classpath:linkd/nms17216/router2-walk.txt")
    })
    @Transactional
    public final void testRouter2() throws MarshalException, ValidationException, IOException {
        m_capsd.init();
        m_capsd.start();
        m_capsd.scanSuspectInterface(ROUTER2_IP);
        

        List<OnmsIpInterface> ips = m_interfaceDao.findByIpAddress(ROUTER2_IP);
        assertTrue("Has only one ip interface", ips.size() == 1);

        OnmsIpInterface ip = ips.get(0);

        for (OnmsIpInterface ipinterface: ip.getNode().getIpInterfaces()) {
            if (ipinterface.getIfIndex() != null )
                System.out.println("ROUTER2_IP_IF_MAP.put(InetAddress.getByName(\""+ipinterface.getIpHostName()+"\"), "+ipinterface.getIfIndex()+");");
        }

        for (OnmsSnmpInterface snmpinterface: ip.getNode().getSnmpInterfaces()) {
            if ( snmpinterface.getIfName() != null)
            System.out.println("ROUTER2_IF_IFNAME_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getIfName()+"\");");
            if (snmpinterface.getIfDescr() != null)
            System.out.println("ROUTER2_IF_IFDESCR_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getIfDescr()+"\");");
            if (snmpinterface.getPhysAddr() != null)
            System.out.println("ROUTER2_IF_MAC_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getPhysAddr()+"\");");            
            if (snmpinterface.getIfAlias() != null)
            System.out.println("ROUTER2_IF_IFALIAS_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getIfAlias()+"\");");            
        }
        
        m_capsd.stop();
    }
    
    @Test
    @JUnitSnmpAgents(value={
            @JUnitSnmpAgent(host=ROUTER3_IP, port=161, resource="classpath:linkd/nms17216/router3-walk.txt")
    })
    @Transactional
    public final void testRouter3() throws MarshalException, ValidationException, IOException {
        m_capsd.init();
        m_capsd.start();
        m_capsd.scanSuspectInterface(ROUTER3_IP);
        

        List<OnmsIpInterface> ips = m_interfaceDao.findByIpAddress(ROUTER3_IP);
        assertTrue("Has only one ip interface", ips.size() == 1);

        OnmsIpInterface ip = ips.get(0);

        for (OnmsIpInterface ipinterface: ip.getNode().getIpInterfaces()) {
            if (ipinterface.getIfIndex() != null )
                System.out.println("ROUTER3_IP_IF_MAP.put(InetAddress.getByName(\""+ipinterface.getIpHostName()+"\"), "+ipinterface.getIfIndex()+");");
        }

        for (OnmsSnmpInterface snmpinterface: ip.getNode().getSnmpInterfaces()) {
            if ( snmpinterface.getIfName() != null)
            System.out.println("ROUTER3_IF_IFNAME_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getIfName()+"\");");
            if (snmpinterface.getIfDescr() != null)
            System.out.println("ROUTER3_IF_IFDESCR_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getIfDescr()+"\");");
            if (snmpinterface.getPhysAddr() != null)
            System.out.println("ROUTER3_IF_MAC_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getPhysAddr()+"\");");            
            if (snmpinterface.getIfAlias() != null)
            System.out.println("ROUTER3_IF_IFALIAS_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getIfAlias()+"\");");            
        }
        
        m_capsd.stop();
    }

    @Test
    @JUnitSnmpAgents(value={
            @JUnitSnmpAgent(host=ROUTER4_IP, port=161, resource="classpath:linkd/nms17216/router4-walk.txt")
    })
    @Transactional
    public final void testRouter4() throws MarshalException, ValidationException, IOException {
        m_capsd.init();
        m_capsd.start();
        m_capsd.scanSuspectInterface(ROUTER4_IP);
        

        List<OnmsIpInterface> ips = m_interfaceDao.findByIpAddress(ROUTER4_IP);
        assertTrue("Has only one ip interface", ips.size() == 1);

        OnmsIpInterface ip = ips.get(0);

        for (OnmsIpInterface ipinterface: ip.getNode().getIpInterfaces()) {
            if (ipinterface.getIfIndex() != null )
                System.out.println("ROUTER4_IP_IF_MAP.put(InetAddress.getByName(\""+ipinterface.getIpHostName()+"\"), "+ipinterface.getIfIndex()+");");
        }

        for (OnmsSnmpInterface snmpinterface: ip.getNode().getSnmpInterfaces()) {
            if ( snmpinterface.getIfName() != null)
            System.out.println("ROUTER4_IF_IFNAME_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getIfName()+"\");");
            if (snmpinterface.getIfDescr() != null)
            System.out.println("ROUTER4_IF_IFDESCR_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getIfDescr()+"\");");
            if (snmpinterface.getPhysAddr() != null)
            System.out.println("ROUTER4_IF_MAC_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getPhysAddr()+"\");");            
            if (snmpinterface.getIfAlias() != null)
            System.out.println("ROUTER4_IF_IFALIAS_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getIfAlias()+"\");");            
        }
        
        m_capsd.stop();
    }

    @Test
    @JUnitSnmpAgents(value={
            @JUnitSnmpAgent(host=SWITCH1_IP, port=161, resource="classpath:linkd/nms17216/switch1-walk.txt")
    })
    @Transactional
    public final void testSwitch1() throws MarshalException, ValidationException, IOException {
        m_capsd.init();
        m_capsd.start();
        m_capsd.scanSuspectInterface(SWITCH1_IP);
        

        List<OnmsIpInterface> ips = m_interfaceDao.findByIpAddress(SWITCH1_IP);
        assertTrue("Has only one ip interface", ips.size() == 1);

        OnmsIpInterface ip = ips.get(0);

        for (OnmsIpInterface ipinterface: ip.getNode().getIpInterfaces()) {
            if (ipinterface.getIfIndex() != null )
                System.out.println("SWITCH1_IP_IF_MAP.put(InetAddress.getByName(\""+ipinterface.getIpHostName()+"\"), "+ipinterface.getIfIndex()+");");
        }

        for (OnmsSnmpInterface snmpinterface: ip.getNode().getSnmpInterfaces()) {
            if ( snmpinterface.getIfName() != null)
            System.out.println("SWITCH1_IF_IFNAME_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getIfName()+"\");");
            if (snmpinterface.getIfDescr() != null)
            System.out.println("SWITCH1_IF_IFDESCR_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getIfDescr()+"\");");
            if (snmpinterface.getPhysAddr() != null)
            System.out.println("SWITCH1_IF_MAC_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getPhysAddr()+"\");");            
            if (snmpinterface.getIfAlias() != null)
            System.out.println("SWITCH1_IF_IFALIAS_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getIfAlias()+"\");");            
        }
        
        m_capsd.stop();
    }

    @Test
    @JUnitSnmpAgents(value={
            @JUnitSnmpAgent(host=SWITCH2_IP, port=161, resource="classpath:linkd/nms17216/switch2-walk.txt")
    })
    @Transactional
    public final void testSwitch2() throws MarshalException, ValidationException, IOException {
        m_capsd.init();
        m_capsd.start();
        m_capsd.scanSuspectInterface(SWITCH2_IP);
        

        List<OnmsIpInterface> ips = m_interfaceDao.findByIpAddress(SWITCH2_IP);
        assertTrue("Has only one ip interface", ips.size() == 1);

        OnmsIpInterface ip = ips.get(0);

        for (OnmsIpInterface ipinterface: ip.getNode().getIpInterfaces()) {
            if (ipinterface.getIfIndex() != null )
                System.out.println("SWITCH2_IP_IF_MAP.put(InetAddress.getByName(\""+ipinterface.getIpHostName()+"\"), "+ipinterface.getIfIndex()+");");
        }

        for (OnmsSnmpInterface snmpinterface: ip.getNode().getSnmpInterfaces()) {
            if ( snmpinterface.getIfName() != null)
            System.out.println("SWITCH2_IF_IFNAME_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getIfName()+"\");");
            if (snmpinterface.getIfDescr() != null)
            System.out.println("SWITCH2_IF_IFDESCR_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getIfDescr()+"\");");
            if (snmpinterface.getPhysAddr() != null)
            System.out.println("SWITCH2_IF_MAC_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getPhysAddr()+"\");");            
            if (snmpinterface.getIfAlias() != null)
                System.out.println("SWITCH2_IF_IFALIAS_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getIfAlias()+"\");");            
        }
        
        m_capsd.stop();
    }

    @Test
    @JUnitSnmpAgents(value={
            @JUnitSnmpAgent(host=SWITCH3_IP, port=161, resource="classpath:linkd/nms17216/switch3-walk.txt")
    })
    @Transactional
    public final void testSwitch3() throws MarshalException, ValidationException, IOException {
        m_capsd.init();
        m_capsd.start();
        m_capsd.scanSuspectInterface(SWITCH3_IP);
        

        List<OnmsIpInterface> ips = m_interfaceDao.findByIpAddress(SWITCH3_IP);
        assertTrue("Has only one ip interface", ips.size() == 1);

        OnmsIpInterface ip = ips.get(0);

        for (OnmsIpInterface ipinterface: ip.getNode().getIpInterfaces()) {
            if (ipinterface.getIfIndex() != null )
                System.out.println("SWITCH3_IP_IF_MAP.put(InetAddress.getByName(\""+ipinterface.getIpHostName()+"\"), "+ipinterface.getIfIndex()+");");
        }

        for (OnmsSnmpInterface snmpinterface: ip.getNode().getSnmpInterfaces()) {
            if ( snmpinterface.getIfName() != null)
            System.out.println("SWITCH3_IF_IFNAME_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getIfName()+"\");");
            if (snmpinterface.getIfDescr() != null)
            System.out.println("SWITCH3_IF_IFDESCR_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getIfDescr()+"\");");
            if (snmpinterface.getPhysAddr() != null)
            System.out.println("SWITCH3_IF_MAC_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getPhysAddr()+"\");");            
            if (snmpinterface.getIfAlias() != null)
                System.out.println("SWITCH3_IF_IFALIAS_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getIfAlias()+"\");");            
        }
        
        m_capsd.stop();
    }

    @Test
    @JUnitSnmpAgents(value={
            @JUnitSnmpAgent(host=SWITCH4_IP, port=161, resource="classpath:linkd/nms17216/switch4-walk.txt")
    })
    @Transactional
    public final void testSwitch4() throws MarshalException, ValidationException, IOException {
        m_capsd.init();
        m_capsd.start();
        m_capsd.scanSuspectInterface(SWITCH4_IP);
        

        List<OnmsIpInterface> ips = m_interfaceDao.findByIpAddress(SWITCH4_IP);
        assertTrue("Has only one ip interface", ips.size() == 1);

        OnmsIpInterface ip = ips.get(0);

        for (OnmsIpInterface ipinterface: ip.getNode().getIpInterfaces()) {
            if (ipinterface.getIfIndex() != null )
                System.out.println("SWITCH4_IP_IF_MAP.put(InetAddress.getByName(\""+ipinterface.getIpHostName()+"\"), "+ipinterface.getIfIndex()+");");
        }

        for (OnmsSnmpInterface snmpinterface: ip.getNode().getSnmpInterfaces()) {
            if ( snmpinterface.getIfName() != null)
            System.out.println("SWITCH4_IF_IFNAME_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getIfName()+"\");");
            if (snmpinterface.getIfDescr() != null)
            System.out.println("SWITCH4_IF_IFDESCR_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getIfDescr()+"\");");
            if (snmpinterface.getPhysAddr() != null)
            System.out.println("SWITCH4_IF_MAC_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getPhysAddr()+"\");");            
            if (snmpinterface.getIfAlias() != null)
                System.out.println("SWITCH4_IF_IFALIAS_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getIfAlias()+"\");");            
        }
        
        m_capsd.stop();
    }

    @Test
    @JUnitSnmpAgents(value={
            @JUnitSnmpAgent(host=SWITCH5_IP, port=161, resource="classpath:linkd/nms17216/switch5-walk.txt")
    })
    @Transactional
    public final void testSwitch5() throws MarshalException, ValidationException, IOException {
        m_capsd.init();
        m_capsd.start();
        m_capsd.scanSuspectInterface(SWITCH5_IP);
        

        List<OnmsIpInterface> ips = m_interfaceDao.findByIpAddress(SWITCH5_IP);
        assertTrue("Has only one ip interface", ips.size() == 1);

        OnmsIpInterface ip = ips.get(0);

        for (OnmsIpInterface ipinterface: ip.getNode().getIpInterfaces()) {
            if (ipinterface.getIfIndex() != null )
                System.out.println("SWITCH5_IP_IF_MAP.put(InetAddress.getByName(\""+ipinterface.getIpHostName()+"\"), "+ipinterface.getIfIndex()+");");
        }

        for (OnmsSnmpInterface snmpinterface: ip.getNode().getSnmpInterfaces()) {
            if ( snmpinterface.getIfName() != null)
            System.out.println("SWITCH5_IF_IFNAME_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getIfName()+"\");");
            if (snmpinterface.getIfDescr() != null)
            System.out.println("SWITCH5_IF_IFDESCR_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getIfDescr()+"\");");
            if (snmpinterface.getPhysAddr() != null)
            System.out.println("SWITCH5_IF_MAC_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getPhysAddr()+"\");");            
            if (snmpinterface.getIfAlias() != null)
                System.out.println("SWITCH5_IF_IFALIAS_MAP.put("+snmpinterface.getIfIndex()+", \""+snmpinterface.getIfAlias()+"\");");            
        }
        
        m_capsd.stop();
    }


}
