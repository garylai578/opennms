package org.opennms.core.bank;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by laiguanhui on 2016/3/15.
 */
public class BankIPAddressOp {

    public BankIPAddress[] selectAll(){
        List<BankIPAddress> list = new ArrayList<BankIPAddress>();
        BankIPAddress ip = new BankIPAddress();
        ip.setId("1");
        ip.setGateway("172.16.0.0");
        ip.setApplication("OA系统");
        ip.setApply_date("2016-03-12");
        ip.setBank("东城");
        ip.setComment("no");
        ip.setDept("信息科");
        ip.setEquip_brand("华为");
        ip.setEquip_type("交换机");
        ip.setIp("172.16.0.21");
        ip.setMac("25-56-52-52-50-25-52-j4");
        ip.setState("在用");
        ip.setNetwork_type("办公网");
        ip.setStart_date("2016-03-11");
        ip.setUsers("张三四");
        ip.setStop_date("2016-03-15");
        ip.setMask("255.255.255.0");
        ip.setModel("model-123");
        list.add(ip);

        return  list.toArray(new BankIPAddress[list.size()]);
    }
}
