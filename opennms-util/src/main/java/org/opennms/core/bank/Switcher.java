package org.opennms.core.bank;

/**
 * Created by laiguanhui on 2016/3/30.
 */
public class Switcher {
    String id;
    String name;
    String group;
    String brand;
    String host;
    String user;
    String password;
    String backup;
    String recovery;
    String wan_ip;
    String lookback_ip;
    String vlan150_ip1;
    String vlan150_ip2;
    String vlan160_ip1;
    String vlan160_ip2;
    String vlan170_ip1;
    String vlan170_ip2;
    String ospf;
    String area;
    String comment;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getWan_ip() {
        return wan_ip;
    }

    public void setWan_ip(String wan_ip) {
        this.wan_ip = wan_ip;
    }

    public String getLookback_ip() {
        return lookback_ip;
    }

    public void setLookback_ip(String lookback_ip) {
        this.lookback_ip = lookback_ip;
    }

    public String getVlan150_ip1() {
        return vlan150_ip1;
    }

    public void setVlan150_ip1(String vlan150_ip1) {
        this.vlan150_ip1 = vlan150_ip1;
    }

    public String getVlan150_ip2() {
        return vlan150_ip2;
    }

    public void setVlan150_ip2(String vlan150_ip2) {
        this.vlan150_ip2 = vlan150_ip2;
    }

    public String getVlan160_ip1() {
        return vlan160_ip1;
    }

    public void setVlan160_ip1(String vlan160_ip1) {
        this.vlan160_ip1 = vlan160_ip1;
    }

    public String getVlan160_ip2() {
        return vlan160_ip2;
    }

    public void setVlan160_ip2(String vlan160_ip2) {
        this.vlan160_ip2 = vlan160_ip2;
    }

    public String getVlan170_ip1() {
        return vlan170_ip1;
    }

    public void setVlan170_ip1(String vlan170_ip1) {
        this.vlan170_ip1 = vlan170_ip1;
    }

    public String getVlan170_ip2() {
        return vlan170_ip2;
    }

    public void setVlan170_ip2(String vlan170_ip2) {
        this.vlan170_ip2 = vlan170_ip2;
    }

    public String getOspf() {
        return ospf;
    }

    public void setOspf(String ospf) {
        this.ospf = ospf;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBackup() {
        return backup;
    }

    public void setBackup(String backup) {
        this.backup = backup;
    }

    public String getRecovery() {
        return recovery;
    }

    public void setRecovery(String recovery) {
        this.recovery = recovery;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String toInsertString(){
        String value = "";
        String[] colsString = {name, group, brand, host, user,password,backup, recovery, wan_ip, lookback_ip, vlan150_ip1, vlan150_ip2, vlan160_ip1, vlan160_ip2, vlan170_ip1, vlan170_ip2, ospf, area, comment};

        for (String col: colsString) {
            if(col == null)
                value += "'',";
            else
                value += "'" + col + "', ";
        }
        return value.substring(0, value.length()-2);
    }
}
