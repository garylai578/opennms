package org.opennms.core.bank;

import org.apache.log4j.Logger;
import org.opennms.core.resource.Vault;
import org.opennms.core.utils.DBUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by laiguanhui on 2016/3/15.
 */
public class BankIPAddressOp {

    private final DBUtils d = new DBUtils(getClass());
    final static Logger log =  Logger.getLogger(BankIPAddressOp.class);

    public void insert(BankIPAddress ipaddr) throws SQLException {
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            String insert = "insert into ipaddress(ip,mask, gateway, mac, network_type, users, bank, dept, model, equip_type, equip_brand, application, state, comment, start_date, stop_date, apply_date" +
                    ") values (" + ipaddr.toInsertValue() + ")";
            log.debug("insert sql = " + insert);
            int rc = stmt.executeUpdate(insert);
            log.debug( "and the rc = " + rc);
        } finally {
            d.cleanUp();
        }
    }

    /**
     * 将所属分行或支行的ip地址检索出来
     * @param group 所属支行/分行，如果group为""，则检索所有
     * @return BankIPAddress[]: ip地址信息
     * @throws SQLException
     */
    public BankIPAddress[] selectAll(String group) throws SQLException{
//        List<BankIPAddress> list = new ArrayList<BankIPAddress>();
//        BankIPAddress ip = new BankIPAddress();

        BankIPAddress[] result = null;
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            String sql = "select * FROM ipaddress";
            if(!group.equals(""))
                sql += " where bank='" + group + "'";
            sql += " order by id";
            ResultSet rs = stmt.executeQuery(sql);
            d.watch(rs);
            result = rs2IPAddress(rs);
        } finally {
            d.cleanUp();
        }
        return result;
    }

    /**
     * Select all unused ipaddress from table ipaddress
     * @return BankIPAddress[]: all result
     * @throws SQLException
     */
    public BankIPAddress[] selectAllUnused() throws SQLException{
        BankIPAddress[] result = null;
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            ResultSet rs = stmt.executeQuery("select * FROM ipaddress WHERE state = '停用'");
            d.watch(rs);
            result = rs2IPAddress(rs);
        } finally {
            d.cleanUp();
        }
        return result;
    }

    private BankIPAddress[] rs2IPAddress(ResultSet rs) throws SQLException {
        BankIPAddress[] result = null;
        List<BankIPAddress> list = new ArrayList<BankIPAddress>();

        while(rs.next()){
            BankIPAddress ip = new BankIPAddress();
            ip.setId(String.valueOf(rs.getInt("id")));
            ip.setIp(rs.getString("ip"));
            ip.setGateway(rs.getString("gateway"));
            ip.setMask(rs.getString("mask"));
            ip.setMac(rs.getString("mac"));
            ip.setNetwork_type(rs.getString("network_type"));
            ip.setStart_date(rs.getString("start_date"));
            ip.setStop_date(rs.getString("stop_date"));
            ip.setApply_date(rs.getString("apply_date"));
            ip.setUsers(rs.getString("users"));
            ip.setBank(rs.getString("bank"));
            ip.setDept(rs.getString("dept"));
            ip.setModel(rs.getString("model"));
            ip.setEquip_type(rs.getString("equip_type"));
            ip.setEquip_brand(rs.getString("equip_brand"));
            ip.setApplication(rs.getString("application"));
            ip.setState(rs.getString("state"));
            ip.setComment(rs.getString("comment"));
            list.add(ip);
        }
        result = list.toArray(new BankIPAddress[list.size()]);
        return result;
    }

    /**
     * Delete a record from table ipaddress according
     *
     * @param ipAddr: the deleting ipaddress
     * @throws SQLException
     */
    public void delete(BankIPAddress ipAddr) throws SQLException {
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            String sql = "delete from ipaddress where ip = '" + ipAddr.getIp() + "'";
            log.debug("delete ipAddr, SQL =" +  sql);
            int rc = stmt.executeUpdate(sql);
            log.debug(", and rc =" + rc);
        } finally {
            d.cleanUp();
        }
    }

    /**
     * Delete a record from table ipaddress according to the id
     *
     * @param id: the id of the ipaddress
     * @throws SQLException
     */
    public void delete(String id) throws SQLException {
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            String sql = "delete from ipaddress where id = " + id + "";
            log.debug("delete by id, SQL =" +  sql);
            int rc = stmt.executeUpdate(sql);
            log.debug(", and rc =" + rc);
        } finally {
            d.cleanUp();
        }
    }

    /**
     * update the col value with newValue.
     * @param id
     * @param colName
     * @param newValue
     * @throws SQLException
     */
    public void updateByID(int id, String colName, String newValue) throws SQLException {
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            String sql;
            sql = "update ipaddress set " + colName + " = " + newValue + " where id =" + id;
            log.debug("update by id, SQL = " + sql);
            int rc = stmt.executeUpdate(sql);
            log.debug(", and the rc= " + rc);
        } finally {
            d.cleanUp();
        }
    }

    /**
     * Update the ipaddress
     * @param ipAddr the ipaddress needed updating
     */
    public void update(BankIPAddress ipAddr) throws SQLException {
            delete(ipAddr.getId());
            insert(ipAddr);
    }

    /**
     * search the cols with key
     * @param cols columns name
     * @param key key value
     * @return the searching result
     * @throws SQLException
     */
    public BankIPAddress[] search(String[] cols, String key) throws SQLException {
        BankIPAddress[] result = null;
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            String sql = "";
            for(String col : cols){
                if(col.equals("start_date") || col.equals("stop_date") || col.equals("apply_date"))
                    sql += "SELECT * FROM ipaddress WHERE " + col + " = '" + key + "' union all ";
                else
                    sql += "SELECT * FROM ipaddress WHERE " + col + " LIKE '%"+ key + "%' union all ";
            }
            sql = sql.substring(0, sql.length()-10);
            log.debug("search sql: " + sql);
            ResultSet rs = stmt.executeQuery(sql);
            d.watch(rs);
            result = rs2IPAddress(rs);
        } finally {
            d.cleanUp();
        }

        return result;
    }

    /**
     * search the cols with key
     * @param colsAndValues columns and the searching values
     * @return the searching result
     * @throws SQLException
     */
    public BankIPAddress[] unionSearch(Map<String, String> colsAndValues) throws SQLException {
        BankIPAddress[] result = null;
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            String sql = "";
            Set<String> cols = colsAndValues.keySet();
            for(String col : cols){
                String value = colsAndValues.get(col);
                if(col.equals("start_date") || col.equals("stop_date") || col.equals("apply_date"))
                    sql += "SELECT * FROM ipaddress WHERE " + col + " = '" + value + "' union all ";
                else
                    sql += "SELECT * FROM ipaddress WHERE " + col + " LIKE '%"+ value + "%' union all ";
            }

            sql = sql.substring(0, sql.length()-10);
            log.debug("search sql: " + sql);
            ResultSet rs = stmt.executeQuery(sql);
            d.watch(rs);
            result = rs2IPAddress(rs);
        } finally {
            d.cleanUp();
        }

        return result;
    }
}
