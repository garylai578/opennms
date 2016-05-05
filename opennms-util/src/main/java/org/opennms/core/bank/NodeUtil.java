package org.opennms.core.bank;

import org.apache.log4j.Logger;
import org.opennms.core.resource.Vault;
import org.opennms.core.utils.DBUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by laiguanhui on 2016/5/5.
 */
public class NodeUtil {
    private final DBUtils d = new DBUtils(getClass());
    final static Logger log =  Logger.getLogger(NodeUtil.class);

    /**
     * 从asserts表中获取column列的值
     * @param nodeId nodeId
     * @param column 列名
     * @return column列对应的值
     * @throws SQLException
     */
    public String getContent(int nodeId, String column) throws SQLException {
        String result = "";
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            String sql = "SELECT " + column +" FROM assets where nodeid=" + nodeId;
            log.debug("sql:" + sql);
            ResultSet rs = stmt.executeQuery(sql);
            d.watch(rs);
            if(rs.next())
                result = rs.getString(column);
        } finally {
            d.cleanUp();
        }
        log.debug("result:" + result);
        return result;
    }
}
