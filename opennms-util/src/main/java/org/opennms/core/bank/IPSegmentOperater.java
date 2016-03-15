package org.opennms.core.bank;

import org.apache.log4j.Logger;
import org.opennms.core.resource.Vault;
import org.opennms.core.utils.DBUtils;
import org.opennms.core.utils.ThreadCategory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
            int rc = stmt.executeUpdate(("insert into ipSegment(gateway, mask, startIP, endIP, name, createTime, type, state, comment) values (" + ipSegment.toInsertValues() + ")"));
            log.debug("IPSegmentOperater.insert: SQL update result = " + rc);
        } finally {
            d.cleanUp();
        }
    }

    /**
     * Select all from table ipSegment
     *
     * @return IPSegment[]: all results.
     * @throws SQLException
     */
    public IPSegment[] selectAll() throws SQLException {
        IPSegment[] result = null;
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            ResultSet rs = stmt.executeQuery("select * FROM ipSegment order by id");
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
     * @return IPSegment[]: all results.
     * @throws SQLException
     */
    public IPSegment[] selectAllUnused() throws  SQLException {
        IPSegment[] result = null;
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            ResultSet rs = stmt.executeQuery("select * FROM ipSegment WHERE state = '停用'");
            d.watch(rs);
            result = rs2IPSegment(rs);
        } finally {
            d.cleanUp();
        }

        return result;
    }

    /**
     * Select the last ip from table ipSegment.
     * @return the last ip
     */
    public String selectLastIP() throws SQLException {
        String lastIP = null;
        log.warn("select last ip start:");
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            ResultSet rs = stmt.executeQuery("select * FROM ipSegment order by id DESC ");
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
            int rc = stmt.executeUpdate(("delete from ipSegment where gateway = '" + ipSegment.getIpPool().getStartIP() + "')"));
            log().debug("IPSegmentOperater.delete: SQL update result = " + rc);
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
            int rc = stmt.executeUpdate(("delete from ipSegment where gateway = '" + gateWay + "')"));
            log().debug("IPSegmentOperater.delete: SQL update result = " + rc);
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
            int rc = stmt.executeUpdate(("update ipSegment set " + colName + " = '" + newValue + "' where id =" + id));
            log.debug("IPSegmentOperater.update by id: SQL update result = " + rc);
        } finally {
            d.cleanUp();
        }
    }

    private ThreadCategory log() {
        return ThreadCategory.getInstance(getClass());
    }

    private IPSegment[] rs2IPSegment(ResultSet rs) throws SQLException {
        IPSegment[] result = null;
        List<IPSegment> list = new ArrayList<IPSegment>();

        while(rs.next()){
            IPSegment ip = new IPSegment();
            ip.setId(String.valueOf(rs.getInt("id")));
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
}
