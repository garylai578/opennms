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

/** Apply the operates(including CRUD) of the ipSegment table.
 *
 * Created by laiguanhui on 2016/2/16.
 */
public class IPSegmentOperater {
    private final DBUtils d = new DBUtils(getClass());
    final static Logger log =  Logger.getLogger(IPSegmentOperater.class);

    public void insert(IPSegment ipSegment) throws SQLException {
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            String sql = "insert into ipSegment(segment, gateway, mask, startIP, endIP, name, createTime, type, state, comment) values (" + ipSegment.toInsertValues() + ")";
            log.debug("IPSegmentOperater.insert:" + sql);
            int rc = stmt.executeUpdate(sql);
            log.debug("IPSegmentOperater.insert: SQL update result = " + rc);
        } finally {
            d.cleanUp();
        }
    }

    /**
     * Select all from table ipSegment
     * @param ipSeg：待搜索的ip段，如果为空，则搜索所有
     * @return IPSegment[]: all results.
     * @throws SQLException
     */
    public IPSegment[] selectAll(String ipSeg) throws SQLException {
        IPSegment[] result = null;
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            String sql = "select * FROM ipSegment";
            if(ipSeg != null && !ipSeg.equals(""))
                sql += " where segment='" + ipSeg + "'";
            sql += " order by segment";
            ResultSet rs = stmt.executeQuery(sql);
            d.watch(rs);
            result = rs2IPSegment(rs);
        } finally {
            d.cleanUp();
        }

        return result;
    }

    /**
     * Select all unused ipsegment from table ipSegment
     *
     * @param ipSeg 待查询的ip段，如果为空，则查询所有
     * @return IPSegment[]: all results.
     * @throws SQLException
     */
    public IPSegment[] selectAllUnused(String ipSeg) throws  SQLException {
        IPSegment[] result = null;
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            String sql = "select * FROM ipSegment WHERE state = '停用'";
            if(ipSeg != null && !ipSeg.equals(""))
                sql += " and segment='" + ipSeg + "'";
            ResultSet rs = stmt.executeQuery(sql);
            d.watch(rs);
            result = rs2IPSegment(rs);
        } finally {
            d.cleanUp();
        }

        return result;
    }

    /**
     * 在ip段ipSeg里查询最后一个ip.
     * @param ipSeg 待查询的ip段，如果为空，则查询所有
     * @return the last ip
     */
    public String selectLastIP(String ipSeg) throws SQLException {
        String lastIP = null;
        log.warn("select last ip start:");
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            String sql = "select * FROM ipSegment";
            if(ipSeg != null && ! ipSeg.equals(""))
                sql += " where segment='" + ipSeg + "'";
            sql += " order by id DESC ";
            ResultSet rs = stmt.executeQuery(sql);
            d.watch(rs);
            if(rs.next())
                lastIP = rs.getString("endip");
            log.debug("select last ip:" + lastIP);
        } finally {
            d.cleanUp();
        }
        return lastIP;
    }

    public IPSegment selectById(String id) throws  SQLException {
        IPSegment[] result = null;
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            ResultSet rs = stmt.executeQuery("select * FROM ipSegment where id = " + id);
            d.watch(rs);
            result = rs2IPSegment(rs);
        } finally {
            d.cleanUp();
        }

        return result[0];
    }

    /**
     * Delete a record from table ipSegment according to the gateway
     *
     * @param ipSegment
     * @throws SQLException
     */
    public void delete(IPSegment ipSegment) throws SQLException {
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            int rc = stmt.executeUpdate("delete from ipSegment where gateway = '" + ipSegment.getIpPool().getStartIP() + "'");
            log.debug("IPSegmentOperater.delete: SQL update result = " + rc);
        } finally {
            d.cleanUp();
        }
    }

    /**
     * Delete a record from table ipSegment according to the gateway
     *
     * @param gateWay
     * @throws SQLException
     */
    public void delete(String gateWay) throws SQLException {
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            int rc = stmt.executeUpdate("delete from ipSegment where gateway = '" + gateWay + "'");
            log.debug("IPSegmentOperater.delete: SQL update result = " + rc);
        } finally {
            d.cleanUp();
        }
    }

    /**
     * Update a record.
     *
     * @param oldGateWay
     * @param newIpSegment
     * @throws SQLException
     */
    public void update(String oldGateWay, IPSegment newIpSegment) throws SQLException{
        delete(oldGateWay);
        insert(newIpSegment);
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
            if(newValue.equals("null"))
                sql = "update ipSegment set " + colName + " = " + newValue + " where id =" + id;
            else
                sql = "update ipSegment set " + colName + " = '" + newValue + "' where id =" + id;
            int rc = stmt.executeUpdate(sql);
            log.debug("IPSegmentOperater.update by id, SQL = " + sql + ". rc= " + rc);
        } finally {
            d.cleanUp();
        }
    }

    private IPSegment[] rs2IPSegment(ResultSet rs) throws SQLException {
        IPSegment[] result = null;
        List<IPSegment> list = new ArrayList<IPSegment>();

        while(rs.next()){
            IPSegment ip = new IPSegment();
            ip.setId(String.valueOf(rs.getInt("id")));
            ip.setSegment(rs.getString("segment"));
            ip.setGateway(rs.getString("gateway"));
            ip.setMask(rs.getString("mask"));
            ip.setStartIP(rs.getString("startip"));
            ip.setEndIP(rs.getString("endip"));
            ip.setBankname(rs.getString("name"));
            ip.setCreateTime(rs.getString("createtime"));
            ip.setStopTime(rs.getString("stoptime"));
            ip.setBanktype(rs.getString("type"));
            ip.setState(rs.getString("state"));
            ip.setComment(rs.getString("comment"));
            list.add(ip);
        }
        result = list.toArray(new IPSegment[list.size()]);
        return result;
    }

    /**
     * 获取目前数据库中所有的IP段信息
     * @return IP段数组
     */
    public String[] getIPSegments() {
        List<String> list = new ArrayList<String>();
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            ResultSet rs = stmt.executeQuery("select distinct(segment) from ipsegment;");
            d.watch(rs);
            while(rs.next()){
                list.add(rs.getString("segment"));
            }
        } catch(SQLException e){
            e.printStackTrace();
        } finally{
            d.cleanUp();
        }

        return list.toArray(new String[list.size()]);
    }

    /**
     * search the cols with key
     * @param colsAndValues columns and the searching values
     * @return the searching result
     * @throws SQLException
     */
    public IPSegment[] andSelect(Map<String, String> colsAndValues) throws SQLException {
        IPSegment[] result = null;
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            String sql = "SELECT * FROM ipsegment WHERE ";
            Set<String> cols = colsAndValues.keySet();
            for(String col : cols){
                String value = colsAndValues.get(col);
                if(col.equals("createTime") || col.equals("stoptime"))
                    sql += col + " = '" + value + "' and ";
                else
                    sql += col + " LIKE '%"+ value + "%' and ";
            }

            if(cols.size() == 0)
                sql = "SELECT * FROM ipsegment";
            else
                sql = sql.substring(0, sql.length()-4);

            log.debug("search sql: " + sql);
            ResultSet rs = stmt.executeQuery(sql);
            d.watch(rs);
            result = rs2IPSegment(rs);
        } finally {
            d.cleanUp();
        }

        return result;
    }
}
