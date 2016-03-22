package org.opennms.core.bank;

/**
 * Created by laiguanhui on 2016/3/15.
 */
public class BankIPAddress {
    String id;
    String ip;
    String network_type;
    String mask;
    String gateway;
    String mac;
    String apply_date;
    String start_date;
    String users;
    String bank;
    String dept;
    String model;
    String equip_brand;
    String equip_type;
    String application;
    String state;
    String comment;
    String stop_date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getNetwork_type() {
        return network_type;
    }

    public void setNetwork_type(String network_type) {
        this.network_type = network_type;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getApply_date() {
        return apply_date;
    }

    public void setApply_date(String apply_date) {
        this.apply_date = apply_date;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getEquip_brand() {
        return equip_brand;
    }

    public void setEquip_brand(String equip_brand) {
        this.equip_brand = equip_brand;
    }

    public String getEquip_type() {
        return equip_type;
    }

    public void setEquip_type(String equip_type) {
        this.equip_type = equip_type;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
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

    public String getStop_date() {
        return stop_date;
    }

    public void setStop_date(String stop_date) {
        this.stop_date = stop_date;
    }

    public String toInsertValue() {
        String value = "";
        String[] colsString = {mask, gateway, mac, network_type, users, bank, dept, model, equip_type, equip_brand, application, state, comment};
        String[] colsDate = {start_date, stop_date, apply_date};

        if(ip==null)
            value += "''";
        else
            value += "'" + ip + "'";

        for (String col: colsString) {
            if(col == null)
                value += ", ''";
            else
                value += ", '" + col + "'";
        }

        for(String col : colsDate)
            if(col == null || col.equals(""))
                value += ", null";
            else
                value += ", '" + col + "'";

        return value;
    }
}
