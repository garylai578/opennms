package org.opennms.web.abcbank;

import org.opennms.core.bank.BankLogWriter;
import org.opennms.core.bank.ExportExcel;
import org.opennms.core.bank.IPSegment;
import org.opennms.core.bank.IPSegmentOperater;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by laiguanhui on 2016/3/22.
 */
public class ExportIPSegmentServlet extends HttpServlet {
    private static final long serialVersionUID = -1675062974497371498L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getRemoteUser();

        try {
            IPSegmentOperater op = new IPSegmentOperater();
            IPSegment[] ips = op.selectAll("");
            List<IPSegment> ipSegmentList = new ArrayList<IPSegment>();
            for(IPSegment ip : ips)
                ipSegmentList.add(ip);
            Collections.sort(ipSegmentList, IPSegment.IPComparator);

            List<IPSegment> dataset = new ArrayList<IPSegment>();
            String ipSeg = "";
            response.setContentType("octets/stream");
            response.addHeader("Content-Disposition", "attachment;filename=result.xls");
            ExportExcel<IPSegment> ex = new ExportExcel<IPSegment>();
            String[] headers = { "id", "所属IP段", "网关", "掩码", "开始IP", "结束IP", "网点名称", "网点类型", "启用日期", "使用情况", "备注", "停用时间"};
            OutputStream out = response.getOutputStream();

            for(IPSegment line : ipSegmentList){
                if("".equals(ipSeg))
                    ipSeg = line.getSegment();
                if(ipSeg.equals(line.getSegment()))
                    dataset.add(line);
                else{
                    ex.exportExcel(ipSeg, headers, dataset, out);
                    ipSeg = line.getSegment();
                    dataset.clear();
                    dataset.add(line);
                }
            }
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        BankLogWriter.getSingle().writeLog("用户[" + userId +"]导出IP地址段分配报表");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
