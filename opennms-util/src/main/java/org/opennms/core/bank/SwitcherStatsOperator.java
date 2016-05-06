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

/**
 * Created by laiguanhui on 2016/5/6.
 */
public class SwitcherStatsOperator {
    private final DBUtils d = new DBUtils(getClass());
    final static Logger log =  Logger.getLogger(SwitcherStatsOperator.class);

    /**
     * 向SwitcherStats表中插入一条数据
     * @param data 待插入数据
     * @throws SQLException
     */
    public void insert(SwitcherStats data) throws SQLException {
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            String insert = "insert into switcherStats(ip, name, groups, flow, comment) values (" + data.toInsertValue() + ")";
            log.debug("insert sql = " + insert);
            int rc = stmt.executeUpdate(insert);
            log.debug( "and the rc = " + rc);
        } finally {
            d.cleanUp();
        }
    }


    /**
     * 将所有交换机流量统计信息检索出来
     * @return SwitcherStats[]: 交换机流量统计信息
     * @throws SQLException
     */
    public SwitcherStats[] selectAll() throws SQLException{
        SwitcherStats[] result = null;
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            String sql = "select * FROM switcherStats";
            ResultSet rs = stmt.executeQuery(sql);
            d.watch(rs);
            result = rs2SwitcherStats(rs);
        } finally {
            d.cleanUp();
        }
        return result;
    }

    private SwitcherStats[] rs2SwitcherStats(ResultSet rs) throws SQLException {
        SwitcherStats[] result = null;
        List<SwitcherStats> list = new ArrayList<SwitcherStats>();

        while(rs.next()){
            SwitcherStats ss = new SwitcherStats(rs.getString("ip"));
            ss.setName(rs.getString("name"));
            ss.setGroup(rs.getString("groups"));
            ss.setComment(rs.getString("comment"));
            ss.setFlow(rs.getString("flow"));
            list.add(ss);
        }
        result = list.toArray(new SwitcherStats[list.size()]);
        return result;
    }

    /**
     * 在switcherStats表中删除一条记录
     *
     * @param ipAddr: 待删除记录的ip地址
     * @throws SQLException
     */
    public void delete(String ipAddr) throws SQLException {
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            String sql = "delete from switcherStats where ip = '" + ipAddr + "'";
            log.debug("delete switcherStats, SQL =" +  sql);
            int rc = stmt.executeUpdate(sql);
            log.debug(", and rc =" + rc);
        } finally {
            d.cleanUp();
        }
    }

    /**
     * update the col value with newValue.
     * @param ip 待更新记录的ip地址
     * @param colName
     * @param newValue
     * @throws SQLException
     */
    public int update(String ip, String colName, String newValue) throws SQLException {
        int rc;
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            String sql;
            sql = "update switcherStats set " + colName + " = " + newValue + " where ip ='" + ip + "'";
            log.debug("update switcherStats by ip, SQL = " + sql);
            rc = stmt.executeUpdate(sql);
            log.debug(", and the rc= " + rc);
        } finally {
            d.cleanUp();
        }
        return rc;
    }

}
