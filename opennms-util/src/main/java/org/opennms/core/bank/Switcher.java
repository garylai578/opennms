package org.opennms.core.bank;

/**
 * Created by laiguanhui on 2016/3/30.
 */
public class Switcher {
    String id;
    String brand;
    String host;
    String user;
    String password;
    String backup;
    String recovery;
    String comment;

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
        String[] colsString = {brand, host, user, password, backup, recovery, comment};

        for (String col: colsString) {
            if(col == null)
                value += "'',";
            else
                value += "'" + col + "', ";
        }
        return value.substring(0, value.length()-2);
    }
}
