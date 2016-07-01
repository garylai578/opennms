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
 * Created by laiguanhui on 2016/3/18.
 */
public class WebLineOperator {

    private final DBUtils d = new DBUtils(getClass());
    final static Logger log =  Logger.getLogger(WebLineOperator.class);

    /**
     * insert a record into table webline.
     * @param value the record need to be inserted.
     * @throws SQLException
     */
    public void insert(WebLine value) throws SQLException {
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            String insert = "insert into webline(ip, state, type, applicant, approver, contact, bank, dept, address, rent, vlan_num, port, interface, attach, comment, grp, start_date" +
                    ") values (" + value.toInsertValue() + ")";
            log.debug("insert sql = " + insert);
            int rc = stmt.executeUpdate(insert);
            log.debug( "and the rc = " + rc);
        } finally {
            d.cleanUp();
        }
    }

    /**
     * 将所有属于group的专线信息检索出来
     * @param group 所属分组，如果为""，则检索所有
     * @return WebLine[]: 专线信息
     * @throws SQLException
     */
    public WebLine[] selectAll(String group) throws SQLException{
        WebLine[] result = null;
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            String sql = "select * FROM webline";
            if(!group.equals(""))
                sql += " where grp='" + group + "'";
            sql += " order by id";
            ResultSet rs = stmt.executeQuery(sql);
            d.watch(rs);
            result = rs2WebLines(rs);
        } finally {
            d.cleanUp();
        }
        return result;
    }

    /**
     * Delete a record from table webline according
     *
     * @param webLine: the deleting web line
     * @throws SQLException
     */
    public void delete(WebLine webLine) throws SQLException {
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            String sql = "delete from webline where id = '" + webLine.getId() + "'";
            log.debug("delete webLine, SQL =" +  sql);
            int rc = stmt.executeUpdate(sql);
            log.debug(", and rc =" + rc);
        } finally {
            d.cleanUp();
        }
    }

    /**
     * Delete a record from table webline according to the id
     *
     * @param id: the id of the web line
     * @throws SQLException
     */
    public void delete(int id) throws SQLException {
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            String sql = "delete from webline where id = " + id + "";
            log.debug("delete web line by id, SQL =" +  sql);
            int rc = stmt.executeUpdate(sql);
            log.debug(", and rc =" + rc);
        } finally {
            d.cleanUp();
        }
    }

    /**
     * search the col with key
     * @param cols columns name
     * @param key key value
     * @return the searching result
     * @throws SQLException
     */
    public WebLine[] search(String[] cols, String key) throws SQLException {
        WebLine[] result = null;
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            String sql = "";
            for(String col: cols) {
                if(col.equals("start_date"))
                    sql += "SELECT * FROM webline WHERE " + col + " = '" + key + "' union all ";
                else
                    sql += "SELECT * FROM webline WHERE " + col + " LIKE '%" + key + "%' union all ";
            }
            sql = sql.substring(0, sql.length()-10);
            log.debug("search sql: " + sql);
            ResultSet rs = stmt.executeQuery(sql);
            d.watch(rs);
            result = rs2WebLines(rs);

        } finally {
            d.cleanUp();
        }

        return result;
    }

    /**
     * search the cols with key
     * @param colsAndValues columns name
     * @return the searching result
     * @throws SQLException
     */
    public WebLine[] andSearch(Map<String, String> colsAndValues) throws SQLException  {
        WebLine[] result = null;
        try {
            Connection conn = Vault.getDbConnection();
            d.watch(conn);
            Statement stmt = conn.createStatement();
            d.watch(stmt);
            String sql = "SELECT * FROM webline WHERE ";
            Set<String> cols = colsAndValues.keySet();
            for(String col : cols){
                String value = colsAndValues.get(col);
                if(col.equals("start_date") || col.equals("rent"))
                    sql += col + " = '" + value + "' and ";
                else
                    sql += col + " LIKE '%"+ value + "%' and ";
            }

            if(cols.size() == 0)
                sql = "SELECT * FROM webline";
            else
                sql = sql.substring(0, sql.length()-4);
            log.debug("search sql: " + sql);
            ResultSet rs = stmt.executeQuery(sql);
            d.watch(rs);
            result = rs2WebLines(rs);
        } finally {
            d.cleanUp();
        }

        return result;
    }

    private WebLine[] rs2WebLines(ResultSet rs)  throws SQLException{
        WebLine[] result = null;
        List<WebLine> list = new ArrayList<WebLine>();

        while(rs.next()){
            WebLine webLine = new WebLine();
            webLine.setId(String.valueOf(rs.getInt("id")));
            webLine.setIp(rs.getString("ip"));
            webLine.setState(rs.getString("state"));
            webLine.setType(rs.getString("type"));
            webLine.setApplicant(rs.getString("applicant"));
            webLine.setApprover(rs.getString("approver"));
            webLine.setContact(rs.getString("contact"));
            webLine.setBank(rs.getString("bank"));
            webLine.setDept(rs.getString("dept"));
            webLine.setAddress(rs.getString("address"));
            webLine.setStart_date(rs.getString("start_date"));
            webLine.setRent(rs.getString("rent"));
            webLine.setVlan_num(rs.getString("vlan_num"));
            webLine.setPort(rs.getString("port"));
            webLine.setInter(rs.getString("interface"));
            webLine.setAttach(rs.getString("attach"));
            webLine.setComment(rs.getString("comment"));
            webLine.setGroup(rs.getString("grp"));
            list.add(webLine);
        }
        result = list.toArray(new WebLine[list.size()]);
        return result;
    }
}
