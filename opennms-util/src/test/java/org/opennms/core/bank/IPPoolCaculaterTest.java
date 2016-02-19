package org.opennms.core.bank;

import junit.framework.TestCase;

public class IPPoolCaculaterTest extends TestCase{

    public void testGetStartIP(){
        String initIP;
        String startIP;
        String endIP;
        int ipNum;

        IPPoolCaculater ic = new IPPoolCaculater("192.168.0.8", 16);
        assertEquals("255.255.255.240", ic.getIPPool().getNetMask());
        assertEquals( "192.168.0.16", ic.getIPPool().getStartIP());
        assertEquals("192.168.0.31", ic.getIPPool().getEndIP());

        ic = new IPPoolCaculater("192.168.0.0", 1);
        assertEquals("255.255.255.255",ic.getIPPool().getNetMask() );
        assertEquals( "192.168.0.0", ic.getIPPool().getStartIP());
        assertEquals( "192.168.0.0", ic.getIPPool().getEndIP());

        ic = new IPPoolCaculater("192.168.0.0", 8);
        assertEquals( "255.255.255.248", ic.getIPPool().getNetMask());
        assertEquals( "192.168.0.0", ic.getIPPool().getStartIP());
        assertEquals("192.168.0.7", ic.getIPPool().getEndIP());

        ic = new IPPoolCaculater("192.168.0.0", 128);
        assertEquals("255.255.255.128", ic.getIPPool().getNetMask());
        assertEquals( "192.168.0.0", ic.getIPPool().getStartIP());
        assertEquals("192.168.0.127", ic.getIPPool().getEndIP());

        ic = new IPPoolCaculater("192.168.0.8", 8);
        assertEquals("255.255.255.248", ic.getIPPool().getNetMask());
        assertEquals( "192.168.0.8", ic.getIPPool().getStartIP());
        assertEquals("192.168.0.15", ic.getIPPool().getEndIP());

        ic = new IPPoolCaculater("192.168.0.64", 16);
        assertEquals("255.255.255.240", ic.getIPPool().getNetMask());
        assertEquals( "192.168.0.64", ic.getIPPool().getStartIP());
        assertEquals("192.168.0.79", ic.getIPPool().getEndIP());

        ic = new IPPoolCaculater("192.168.0.1",64);
        assertEquals("255.255.255.192", ic.getIPPool().getNetMask());
        assertEquals("192.168.0.64", ic.getIPPool().getStartIP());
        assertEquals("192.168.0.127", ic.getIPPool().getEndIP());

        ic = new IPPoolCaculater("192.168.0.35", 32);
        assertEquals("255.255.255.224", ic.getIPPool().getNetMask());
        assertEquals("192.168.0.64", ic.getIPPool().getStartIP());
        assertEquals("192.168.0.95", ic.getIPPool().getEndIP());

        ic = new IPPoolCaculater("192.168.0.96", 64);
        assertEquals("255.255.255.192", ic.getIPPool().getNetMask());
        assertEquals("192.168.0.128", ic.getIPPool().getStartIP());
        assertEquals("192.168.0.191", ic.getIPPool().getEndIP());

        ic = new IPPoolCaculater("192.168.0.99", 32);
        assertEquals("255.255.255.224", ic.getIPPool().getNetMask());
        assertEquals("192.168.0.128", ic.getIPPool().getStartIP());
        assertEquals("192.168.0.159", ic.getIPPool().getEndIP());

        ic = new IPPoolCaculater("192.168.0.152", 128);
        assertEquals("255.255.255.128", ic.getIPPool().getNetMask());
        assertEquals("0.0.0.0", ic.getIPPool().getStartIP());
        assertEquals("0.0.0.0", ic.getIPPool().getEndIP());
    }

}
