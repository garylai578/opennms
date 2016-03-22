package org.opennms.web.abcbank;

import org.opennms.core.bank.ExportExcel;
import org.opennms.core.bank.IPSegment;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
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
        String tmp = request.getParameter("rows");
        int rows = Integer.parseInt(tmp);
        List<IPSegment> dataset = new ArrayList<IPSegment>();
        for(int i = 0; i < rows; ++i){
            IPSegment line = new IPSegment();
            line.setId(request.getParameter("id-"+i));
            line.setGateway(request.getParameter("gateway-"+i));
            line.setMask(request.getParameter("mask-"+i));
            line.setStartIP(request.getParameter("startIP-"+i));
            line.setEndIP(request.getParameter("endIP-"+i));
            line.setBankname(request.getParameter("bankname-"+i));
            line.setBanktype(request.getParameter("banktype-"+i));
            line.setCreateTime(request.getParameter("createdate-"+i));
            line.setState(request.getParameter("state-"+i));
            line.setComment(request.getParameter("comment-"+i));
            dataset.add(line);
        }

        response.setContentType("octets/stream");
        response.addHeader("Content-Disposition", "attachment;filename=result.xls");
        ExportExcel<IPSegment> ex = new ExportExcel<IPSegment>();
        String[] headers = { "id", "网关", "掩码", "开始IP", "结束IP", "网点名称", "网点类型", "启用日期", "使用情况", "备注"};
        try {
            OutputStream out = response.getOutputStream();
            ex.exportExcel("retulst", headers, dataset, out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
