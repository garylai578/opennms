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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

/**
 * Created by laiguanhui on 2016/5/5.
 */
public class Flow {

    private String octer = "public"; // 共同体
    final String TIME_OID = ".1.3.6.1.2.1.1.3.0"; // System Uptime OID，所有设备一样
    String IpAddress; // 设备IP地址
    ArrayList<String> FlowOidGroup; // 流量OID，可能有多个ifOutOctets OID: .1.3.6.1.2.1.2.2.1.16 , ifInOctets OID: .1.3.6.1.2.1.2.2.1.10
    private String NowTime; // 端口流量的采集时间
    public boolean isSuccess = false;

    /**
     * 构造器：IP地址
     * @param IpAddress ip地址
     */
    public Flow(String IpAddress) {
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
     * 获取端口流量的采集时间
     * @return 端口流量的采集时间，格式：yyyy-MM-dd HH:mm
     */
    public String getNowTime() {
        return NowTime;
    }

    /**
     * 计算交换机的端口流量，思路是：采集两次设备数据，用流量值的差值，除以时间的差值，即是当前的流量值，时间间隔我用的是5秒
     * @param FlowOidGroup 流量OID，可以有多个
     */
    @SuppressWarnings("unchecked")
    public long calcFlowValue(ArrayList<String> FlowOidGroup) {
        long result = 0;
        try {
            BankLogWriter.getSingle().writeLog("获取并计算交换机流量：" + IpAddress);
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
            request.add(new VariableBinding(new OID(TIME_OID)));// OID_sysUpTime
            for (String FlowOid : FlowOidGroup) {
                request.add(new VariableBinding(new OID(FlowOid)));
            }
            // 取两次数据，间隔10秒，算差值
            long[] time = new long[2];
            long[][] flow = new long[2][FlowOidGroup.size()];
            for (int count = 0; count < 2; count++) {
                ResponseEvent respEvt = snmp.send(request, target);// 发送请求
                if (respEvt != null && respEvt.getResponse() != null) { // 从目的设备取值，得到Vector
                    Vector<VariableBinding> revBindings = (Vector<VariableBinding>)respEvt.getResponse().getVariableBindings();
                    String TimeTicks = revBindings.elementAt(0).getVariable().toString().trim();
                    BankLogWriter.getSingle().writeLog("获取交换机更新时间：" + TimeTicks);
                    String[] TimeString = TimeTicks.split(" ");// 得到时间字符串数组
                    // 取时间 186 days, 21:26:15.24，也有可能没有day，就是不到一天
                    if (TimeTicks.contains("day")) {
                        time[count] = Long.parseLong(TimeString[0]) * 24 * 3600 + Long.parseLong(TimeString[2].split(":")[0]) * 3600 + Long.parseLong(TimeString[2].split(":")[1]) * 60
                                + Math.round(Double.parseDouble(TimeString[2].split(":")[2]));
                    } else {
                        time[count] = Long.parseLong(TimeString[0].split(":")[0]) * 3600
                                + Long.parseLong(TimeString[0].split(":")[1]) * 60
                                + Math.round(Double.parseDouble(TimeString[0].split(":")[2]));
                    }
                    // 取端口流量
                    for (int i = 0; i < FlowOidGroup.size(); i++) {
                        BankLogWriter.getSingle().writeLog("获取交换机端口流量：" + revBindings.elementAt(i + 1).getVariable().toString());
                        flow[count][i] = Long.parseLong(revBindings.elementAt(i + 1).getVariable().toString());
                        BankLogWriter.getSingle().writeLog("" + flow[count][i]);
                    }
                    isSuccess = true;
                } else {
                    isSuccess = false;
                }
                if (count == 0)
                    Thread.sleep(5000);// 延时5秒后，第二次取值
            }
            snmp.close();
            transport.close();
            // 计算并为时间和最终流量赋值
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            NowTime = sdf.format(c.getTime());// 当前时间
            long AllSubValue = 0;
            for (int i = 0; i < FlowOidGroup.size(); i++) {
                long sub = flow[1][i] - flow[0][i];
                /*
                 * 端口流量值为无符号32位，超出后就归0，所以如果两次取值差值为负，
                 * 必然出现一次归0的情况，由于单个端口的流量不可能超过每5秒1*2^32字节
                 */
                if (sub < 0) {
                    // 因为端口流量为无符号32位，所以最大值是有符号32位的两倍
                    sub += 2L * Integer.MAX_VALUE;
                }
                AllSubValue += sub;
            }
            if (time[1] - time[0] != 0) { // 字节换算成兆比特才是最终流量
                result = (long) (AllSubValue / 1024.0 / 1024 * 8 / (time[1] - time[0]));
                isSuccess = true;
            } else {
                BankLogWriter.getSingle().writeLog("地址：" + IpAddress + "的交换机流量数据采集失败！");
                isSuccess = false;
            }
        } catch (IOException e ) {
            BankLogWriter.getSingle().writeLog("地址：" + IpAddress + "的交换机流量数据采集IO异常：" + e.getMessage());
            e.printStackTrace();
            isSuccess  = false;
        } catch (InterruptedException e){
            BankLogWriter.getSingle().writeLog("地址：" + IpAddress + "的交换机流量数据采集中断异常：" + e.getMessage());
            e.printStackTrace();
            isSuccess  = false;
        } catch(NumberFormatException e){
            BankLogWriter.getSingle().writeLog("地址：" + IpAddress + "的交换机流量数据采集解析异常：" + e.getMessage());
            e.printStackTrace();
            isSuccess  = false;
        }
        return result;
    }

}
