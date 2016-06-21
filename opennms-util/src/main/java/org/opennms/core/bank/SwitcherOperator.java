package org.opennms.core.bank;

import org.apache.log4j.Logger;
import org.opennms.core.resource.Vault;
import org.opennms.core.utils.DBUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by laiguanhui on 2016/3/30.
 */
public class SwitcherOperator {
    private final DBUtils d = new DBUtils(getClass());
    final static Logger log =  Logger.getLogger(SwitcherOperator.class);

    /**
     * Insert a record into table switcher
     * @param s the inserting record
     * @throws SQLException
     */
    public void insert(Switcher s) throws SQLException {
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            String insert = "insert into switcher(name, groups, brand, host, username,password,backup, recovery, wan_ip, lookback_ip, vlan150_ip1, vlan150_ip2, vlan160_ip1, vlan160_ip2, vlan170_ip1, vlan170_ip2, ospf, area, comment) values ("
                    + s.toInsertString() + ")";
            log.debug("insert sql = " + insert);
            int rc = stmt.executeUpdate(insert);
            log.debug( "and the rc = " + rc);
        } finally {
            d.cleanUp();
        }
    }

    /**
     * update the col value with newValue.
     * @param id the id of the record
     * @param sw
     * @throws SQLException
     */
    public void updateByID(int id, Switcher sw) throws SQLException {
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            String sql;
            sql = "update switcher set name = '" + sw.getName() + "', groups='"+ sw.getGroup() +"', brand='"+ sw.getBrand() +"', host='"+ sw.getHost() +"', username='" +
                    sw.getUser() +"', password='" + sw.getPassword() + "', backup='"+ sw.getBackup() + "', recovery='" + sw.getRecovery() + "', wan_ip='" +
                    sw.getWan_ip() +"', lookback_ip='" + sw.getLookback_ip() + "', vlan150_ip1='"+ sw.getVlan150_ip1() + "', vlan150_ip2='" + sw.getVlan150_ip2() + "', vlan160_ip1='" +
                    sw.getVlan160_ip1()  + "', vlan160_ip2='" + sw.getVlan160_ip2() + "', vlan170_ip1='" + sw.getVlan170_ip1() + "', vlan170_ip2='" + sw.getVlan170_ip2() + "', ospf='" +
                    sw.getOspf() + "', area='" + sw.getArea() + "', comment='" + sw.getComment() + "'" +" where id =" + id;
            log.debug("update by id, SQL = " + sql);
            int rc = stmt.executeUpdate(sql);
            log.debug(", and the rc= " + rc);
        } finally {
            d.cleanUp();
        }
    }

    /**
     * 根据给定的content搜索指定的列column，如果col为空
     * @param column 待搜索的列
     * @param key 搜索的内容
     * @return 符合条件的Swither数组
     */
    public Switcher[] select(String column, String key) throws SQLException {
        Switcher[] result = null;
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            String sql;
            boolean flag = false;
            ResultSet rs = stmt.executeQuery("SELECT * FROM switcher");
            ResultSetMetaData rsmd = rs.getMetaData();
            for(int i = 0; i <rsmd.getColumnCount(); ++i) {
                if(rsmd.getColumnName(i+1).equals(column))
                    flag = true;
            }
            log.debug("colunm:" + column + "\t key:" + key);
            if(column == null || key == null || !flag || key.equals(""))
                sql = "SELECT * FROM switcher";
            else
                sql = "SELECT * FROM switcher WHERE " + column + " LIKE '%" + key + "%'";

            log.debug("search sql: " + sql);
            rs = stmt.executeQuery(sql);
            d.watch(rs);
            result = rs2Switcher(rs);
        } finally {
            d.cleanUp();
        }

        return result;
    }

    /**
     * Select all from table swticher
     *
     * @return IPSegment[]: all results.
     * @throws SQLException
     */
    public Switcher[] selectAll() throws SQLException {
        Switcher[] result = null;
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            ResultSet rs = stmt.executeQuery("select * FROM switcher order by id");
            d.watch(rs);
            result = rs2Switcher(rs);
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
    public Switcher[] andSelect(Map<String, String> colsAndValues) throws SQLException  {
        Switcher[] result = null;
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            String sql = "SELECT * FROM switcher WHERE ";
            Set<String> cols = colsAndValues.keySet();
            for(String col : cols){
                String value = colsAndValues.get(col);
                sql += col + " LIKE '%"+ value + "%' and ";
            }

            if(cols.size() == 0)
                sql = "SELECT * FROM switcher";
            else
                sql = sql.substring(0, sql.length()-4);

            log.debug("search sql: " + sql);
            ResultSet rs = stmt.executeQuery(sql);
            d.watch(rs);
            result = rs2Switcher(rs);
        } finally {
            d.cleanUp();
        }

        return result;
    }

    /**
     * Delete a record from table switcher according to the id
     *
     * @param id
     * @throws SQLException
     */
    public void delete(int id) throws SQLException {
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            String sql = "delete from switcher where id = " + id + "";
            log.debug("delete switcher by id, SQL =" +  sql);
            int rc = stmt.executeUpdate(sql);
            log.debug(", and rc =" + rc);
        } finally {
            d.cleanUp();
        }
    }

    private Switcher[] rs2Switcher(ResultSet rs) throws SQLException {
        Switcher[] result;
        List<Switcher> list = new ArrayList<Switcher>();

        while(rs.next()){
            Switcher switcher = new Switcher();
            switcher.setId(String.valueOf(rs.getInt("id")));
            switcher.setName(rs.getString("name"));
            switcher.setGroup(rs.getString("groups"));
            switcher.setBrand(rs.getString("brand"));
            switcher.setHost(rs.getString("host"));
            switcher.setUser(rs.getString("username"));
            switcher.setPassword(rs.getString("password"));
            switcher.setBackup(rs.getString("backup"));
            switcher.setRecovery(rs.getString("recovery"));
            switcher.setWan_ip(rs.getString("wan_ip"));
            switcher.setLookback_ip(rs.getString("lookback_ip"));
            switcher.setVlan150_ip1(rs.getString("vlan150_ip1"));
            switcher.setVlan150_ip2(rs.getString("vlan150_ip2"));
            switcher.setVlan160_ip1(rs.getString("vlan160_ip1"));
            switcher.setVlan160_ip2(rs.getString("vlan160_ip2"));
            switcher.setVlan170_ip1(rs.getString("vlan170_ip1"));
            switcher.setVlan170_ip2(rs.getString("vlan170_ip2"));
            switcher.setOspf(rs.getString("ospf"));
            switcher.setArea(rs.getString("area"));
            switcher.setComment(rs.getString("comment"));
            list.add(switcher);
        }
        result = list.toArray(new Switcher[list.size()]);
        return result;
    }

}
