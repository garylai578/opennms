package org.opennms.core.bank;

/** 用于表示用户的1个IP地址段的所用信息
 *
 * Created by laiguanhui on 2016/2/16.
 */
public class IPSegment {
    private Integer id;
    private IPPool ipPool;
    private String gateway;
    private String mask;
    private String startIP;
    private String endIP;
    private String bankname;
    private String createTime;
    private String banktype;
    private String state;
    private String comment;

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

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

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

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getBanktype() {
        return banktype;
    }

    public void setBanktype(String banktype) {
        this.banktype = banktype;
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

    public String toInsertValues() {
        return  "'" + gateway + "', '" + mask + "', '" + startIP + "', '" + endIP + "', '"  + bankname + "', '"  + createTime + "', '"   + banktype + "', '"  + state + "', '"  + comment +"'";

    }
}
