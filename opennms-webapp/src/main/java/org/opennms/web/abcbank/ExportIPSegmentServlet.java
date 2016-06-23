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
import java.util.*;

/**
 * Created by laiguanhui on 2016/3/22.
 */
public class ExportIPSegmentServlet extends HttpServlet {
    private static final long serialVersionUID = -1675062974497371498L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getRemoteUser();
		String tmp = request.getParameter("rows");
        int rows = Integer.parseInt(tmp);
		List<IPSegment> ipSegmentList = new ArrayList<IPSegment>();
		
		for(int i = 0; i < rows; ++i){
            IPSegment ips = new IPSegment();
            ips.setId(request.getParameter("id-"+i));
            ips.setSegment(request.getParameter("ipSeg-"+i));
            ips.setGateway(request.getParameter("gateway-"+i));
            ips.setMask(request.getParameter("mask-"+i));
            ips.setStartIP(request.getParameter("startIP-"+i));
            ips.setEndIP(request.getParameter("endIP-"+i));
            ips.setBankname(request.getParameter("bankname-"+i) + "/" + request.getParameter("dept-"+i));
            ips.setBanktype(request.getParameter("banktype-"+i));
            ips.setCreateTime(request.getParameter("createdate-"+i));
            ips.setState(request.getParameter("state-"+i));
            ips.setStopTime(request.getParameter("stopTime-"+i));
            ips.setComment(request.getParameter("comment-"+i));
            ipSegmentList.add(ips);
        }

        try {
            Collections.sort(ipSegmentList, IPSegment.IPComparator);

            List<IPSegment> dataset = new ArrayList<IPSegment>();
            String ipSeg = "";
            response.setContentType("octets/stream");
            response.addHeader("Content-Disposition", "attachment;filename=result.xls");
            ExportExcel<IPSegment> ex = new ExportExcel<IPSegment>();
            String[] headers = { "id", "所属IP段", "网关", "掩码", "开始IP", "结束IP", "网点名称", "网点类型", "启用日期", "使用情况", "备注", "停用时间"};
            OutputStream out = response.getOutputStream();
            Map titleAndData = new LinkedHashMap<String, IPSegment>();

            for(IPSegment line : ipSegmentList){
                if("".equals(ipSeg))
                    ipSeg = line.getSegment();
                if(ipSeg.equals(line.getSegment())) {
                    dataset.add(line);
                }
                else{
                    titleAndData.put(ipSeg, dataset);
                    ipSeg = line.getSegment();
                    dataset = new ArrayList<IPSegment>();
                    dataset.add(line);
                }
            }
            titleAndData.put(ipSeg, dataset);
            ex.exportExcels(headers, titleAndData, out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } 

        BankLogWriter.getSingle().writeLog("用户[" + userId +"]导出IP地址段分配报表");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
