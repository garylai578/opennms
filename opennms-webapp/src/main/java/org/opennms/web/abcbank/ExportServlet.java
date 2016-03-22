package org.opennms.web.abcbank;

import org.opennms.core.bank.ExportExcel;
import org.opennms.core.bank.WebLine;

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
 * Created by laiguanhui on 2016/3/21.
 */
public class ExportServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tmp = request.getParameter("rows");
        int rows = Integer.parseInt(tmp);
        List<WebLine> dataset = new ArrayList<WebLine>();
        for(int i = 0; i < rows; ++i){
            WebLine line = new WebLine();
            line.setId(request.getParameter("id-"+i));
            line.setType(request.getParameter("type-"+i));
            line.setApplicant(request.getParameter("applicant-"+i));
            line.setApplicant(request.getParameter("contact-"+i));
            line.setApplicant(request.getParameter("approver-"+i));
            line.setApplicant(request.getParameter("dept-"+i));
            line.setApplicant(request.getParameter("address-"+i));
            line.setApplicant(request.getParameter("start_date-"+i));
            line.setApplicant(request.getParameter("rent-"+i));
            line.setApplicant(request.getParameter("vlan_num-"+i));
            line.setApplicant(request.getParameter("port-"+i));
            line.setApplicant(request.getParameter("inter-"+i));
            line.setApplicant(request.getParameter("comment-"+i));
            dataset.add(line);
        }

        response.setContentType("octets/stream");
        response.addHeader("Content-Disposition", "attachment;filename=result.xls");
        ExportExcel<WebLine> ex = new ExportExcel<WebLine>();
        String[] headers = { "id", "专线类型", "申请人", "联系方式", "审批人", "使用机构", "地址", "开通日期", "月租", "VLAN编号", "物理端口号", " 运营商接口号", "备注"};
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
