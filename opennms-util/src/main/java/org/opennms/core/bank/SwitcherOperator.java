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
            String insert = "insert into switcher(brand, host, username, password, backup, recovery, comment) values ("
                    + s.toInsertString() + ")";
            log.debug("insert sql = " + insert);
            int rc = stmt.executeUpdate(insert);
            log.debug( "and the rc = " + rc);
        } finally {
            d.cleanUp();
        }
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
            switcher.setBrand(rs.getString("brand"));
            switcher.setHost(rs.getString("host"));
            switcher.setUser(rs.getString("username"));
            switcher.setPassword(rs.getString("password"));
            switcher.setBackup(rs.getString("backup"));
            switcher.setRecovery(rs.getString("recovery"));
            switcher.setComment(rs.getString("comment"));
            list.add(switcher);
        }
        result = list.toArray(new Switcher[list.size()]);
        return result;
    }
}
