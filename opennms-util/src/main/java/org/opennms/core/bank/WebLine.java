package org.opennms.core.bank;

/**
 * Created by laiguanhui on 2016/3/18.
 */
public class WebLine {

    private String id, type, applicant, approver, contact, dept, address, start_date, rent, vlan_num, port, inter, comment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment){
        this.comment = comment;
    }

    public String toInsertValue() {
        String value = "";
        String[] colsString = {type, applicant, approver, contact, dept, address, rent, vlan_num, port, inter, comment};

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
