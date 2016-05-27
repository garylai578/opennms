package org.opennms.web.abcbank;

import org.opennms.core.bank.BankLogWriter;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by laiguanhui on 2016/5/18.
 */
public class SwitcherFlowMonitor {
    private String octer = "public"; // 共同体
    private String IpAddress; // 设备IP地址
    private ArrayList<String> FlowOidGroup; // 流量OID，可能有多个ifOutOctets OID: .1.3.6.1.2.1.2.2.1.16 , ifInOctets OID: .1.3.6.1.2.1.2.2.1.10
    private boolean isSuccess = false;

    /**
     * 构造器：IP地址
     * @param IpAddress ip地址
     */
    public SwitcherFlowMonitor(String IpAddress) {
        this.IpAddress = IpAddress;
    }

    /**
     * 获取共同体
     * @return 共同体
     */
    public String getOcter() {
        return octer;
    }

    /**
     * 设置共同体
     * @param octer 共同体
     */
    public void setOcter(String octer){
        this.octer = octer;
    }

    /**
     * 获取交换机端口的当前流量
     * @param flowOIDGroup 流量OID，可以有多个
     * @return 当前流量，单位是KB
     */
    public long getFlowValue(ArrayList<String> flowOIDGroup) {
        long flowValue = 0L;
        try {
            Address targetAddress = GenericAddress.parse("udp:" + IpAddress + "/161");
            TransportMapping transport = new DefaultUdpTransportMapping();
            Snmp snmp = new Snmp(transport);
            transport.listen();// 监听
            CommunityTarget target = new CommunityTarget();

            // 目标对象相关设置
            target.setCommunity(new OctetString(octer));// 设置共同体名
            target.setAddress(targetAddress);// 设置目标Agent地址
            target.setRetries(2);// 重试次数
            target.setTimeout(3000);// 超时设置
            target.setVersion(1);// 版本

            PDU request = new PDU();
            request.setType(PDU.GET);// 操作类型GET
            for (String flowOID : flowOIDGroup) {
                request.add(new VariableBinding(new OID(flowOID)));
            }

            ResponseEvent respEvt = snmp.send(request, target);// 发送请求
            if (respEvt != null && respEvt.getResponse() != null) { // 从目的设备取值，得到Vector
                Vector<VariableBinding> revBindings = (Vector<VariableBinding>)respEvt.getResponse().getVariableBindings();
                // 取端口流量
                for (int i = 0; i < flowOIDGroup.size(); i++) {
//                    BankLogWriter.getSingle().writeLog("获取交换机端口流量,oid[" + flowOIDGroup.get(i) + "], 结果[" + revBindings.elementAt(i).getVariable().toString() + "]");
                    try{
                        flowValue += Long.parseLong(revBindings.elementAt(i).getVariable().toString()) / (1000*8.0); //将bit转换为KB然后累加，否则有可能会超出long的最大值
                    }catch(NumberFormatException e){
//                        BankLogWriter.getSingle().writeLog("交换机[" + IpAddress + "]流量数据采集解析异常：" + e.getMessage());
//                        e.printStackTrace();
                        isSuccess  = false;
                    }
                }
                isSuccess = true;
            } else {
                isSuccess = false;
            }

            snmp.close();
            transport.close();
        } catch (IOException e ) {
            BankLogWriter.getSingle().writeLog("交换机[" + IpAddress + "】的流量数据采集IO异常：" + e.getMessage());
            e.printStackTrace();
            isSuccess  = false;
        } catch(NumberFormatException e){
            BankLogWriter.getSingle().writeLog("交换机[" + IpAddress + "]的流量数据采集解析异常：" + e.getMessage());
            e.printStackTrace();
            isSuccess  = false;
        }
        return flowValue;
    }

}
