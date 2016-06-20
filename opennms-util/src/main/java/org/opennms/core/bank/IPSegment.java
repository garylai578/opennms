package org.opennms.core.bank;

import java.util.Comparator;

/** 用于表示用户的1个IP地址段的所用信息
 *
 * Created by laiguanhui on 2016/2/16.
 */
public class IPSegment {
    private String id;
    private String segment;
    private String gateway;
    private String mask;
    private String startIP;
    private String endIP;
    private String bankname;
    private String banktype;
    private String createTime;
    private String state;
    private String comment;
    private String stopTime;
    private IPPool ipPool;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSegment(){ return segment; }

    public void setSegment(String segment) {this.segment = segment; }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getStartIP() {
        return startIP;
    }

    public void setStartIP(String startIP) {
        this.startIP = startIP;
    }

    public String getEndIP() {
        return endIP;
    }

    public void setEndIP(String endIP) {
        this.endIP = endIP;
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public String getBanktype() {
        return banktype;
    }

    public void setBanktype(String banktype) {
        this.banktype = banktype;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public IPPool getIpPool() {
        return ipPool;
    }

    public void setIpPool(IPPool ipPool) {
        this.ipPool = ipPool;
        this.mask = ipPool.getNetMask();
        this.gateway = ipPool.getStartIP();
        this.startIP = ipPool.getStartIP();
        this.endIP = ipPool.getEndIP();
    }

    public String toInsertValues() {
        return  "'" + segment + "', '" + gateway + "', '" + mask + "', '" + startIP + "', '" + endIP + "', '"  + bankname + "', '"  + createTime + "', '"   + banktype + "', '"  + state + "', '"  + comment +"'";

    }

    public static Comparator IPComparator=new Comparator(){
        @Override
        public int compare(Object arg0, Object arg1) {
            IPSegment ip1=(IPSegment)arg0;
            IPSegment ip2=(IPSegment)arg1;
            return SwitcherStats.compartTo(ip1.startIP, ip2.startIP);
        }
    };

}
