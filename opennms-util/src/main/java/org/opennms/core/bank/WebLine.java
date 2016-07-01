package org.opennms.core.bank;

/**
 * Created by laiguanhui on 2016/3/18.
 */
public class WebLine {

    private String id;
    private String ip;
    private String state;
    private String type;
    private String applicant;
    private String contact;
    private String approver;
    private String bank;
    private String dept;
    private String address;
    private String start_date;
    private String rent;
    private String vlan_num;
    private String port;
    private String inter;
    private String comment;
    private String group;
    private String attach;

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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getRent() {
        return rent;
    }

    public void setRent(String rent) {
        this.rent = rent;
    }

    public String getVlan_num() {
        return vlan_num;
    }

    public void setVlan_num(String vlan_num) {
        this.vlan_num = vlan_num;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getInter() {
        return inter;
    }

    public void setInter(String inter) {
        this.inter = inter;
    }

    public String getAttatch() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment){
        this.comment = comment;
    }

    public String getGroup() { return group; }

    public void setGroup(String group) { this.group = group; }

    public String toInsertValue() {
        String value = "";
        String[] colsString = {ip, state, type, applicant, approver, contact, bank, dept, address, rent, vlan_num, port, inter, attach, comment, group};

        for (String col: colsString) {
            if(col == null)
                value += "'',";
            else
                value += "'" + col + "', ";
        }

        if(start_date == null || start_date.equals(""))
            value += "null";
        else
            value += "'" + start_date + "'";

        return value;
    }
}
