package org.opennms.web.abcbank;

import org.opennms.core.bank.BankLogWriter;
import org.opennms.core.bank.ExportExcel;
import org.opennms.core.bank.IPSegment;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by laiguanhui on 2016/3/22.
 */
public class ExportIPSegmentServlet extends HttpServlet {
    private static final long serialVersionUID = -1675062974497371498L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getRemoteUser();
        String tmp = request.getParameter("rows");
        int rows = Integer.parseInt(tmp);
        List<IPSegment> dataset = new ArrayList<IPSegment>();
        String ipSeg = "";
        response.setContentType("octets/stream");
        response.addHeader("Content-Disposition", "attachment;filename=result.xls");
        ExportExcel<IPSegment> ex = new ExportExcel<IPSegment>();
        String[] headers = { "id", "所属IP段", "网关", "掩码", "开始IP", "结束IP", "网点名称", "网点类型", "启用日期", "使用情况", "备注"};
        OutputStream out = response.getOutputStream();

        for(int i = 0; i < rows; ++i){
            IPSegment line = new IPSegment();
            line.setId(request.getParameter("id-"+i));
            line.setSegment(request.getParameter("ipSeg-"+i));
            line.setGateway(request.getParameter("gateway-"+i));
            line.setMask(request.getParameter("mask-"+i));
            line.setStartIP(request.getParameter("startIP-"+i));
            line.setEndIP(request.getParameter("endIP-"+i));
            line.setBankname(request.getParameter("bankname-"+i));
            line.setBanktype(request.getParameter("banktype-"+i));
            line.setCreateTime(request.getParameter("createdate-"+i));
            line.setState(request.getParameter("state-"+i));
            line.setComment(request.getParameter("comment-"+i));
            if(i == 0)
                ipSeg = line.getSegment();
            if(ipSeg == line.getSegment())
                dataset.add(line);
            else{
                ex.exportExcel(ipSeg, headers, dataset, out);
                ipSeg = line.getSegment();
                dataset.clear();
            }
        }

        out.close();


        BankLogWriter.getSingle().writeLog("用户[" + userId +"]导出IP地址段分配报表");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
