package org.opennms.web.abcbank;

import org.opennms.core.bank.BankIPAddress;
import org.opennms.core.bank.ExportExcel;

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
public class ExportIPAddressServlet extends HttpServlet {
    private static final long serialVersionUID = -3000148969246492052L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tmp = request.getParameter("rows");
        int rows = Integer.parseInt(tmp);
        List<BankIPAddress> dataset = new ArrayList<BankIPAddress>();
        for(int i = 0; i < rows; ++i){
            BankIPAddress line = new BankIPAddress();
            line.setId(request.getParameter("id-"+i));
            line.setIp(request.getParameter("ipaddr-"+i));
            line.setNetwork_type(request.getParameter("network_type-"+i));
            line.setMask(request.getParameter("mask-"+i));
            line.setGateway(request.getParameter("gateway-"+i));
            line.setMac(request.getParameter("mac-"+i));
            line.setApply_date(request.getParameter("apply_date-"+i));
            line.setStart_date(request.getParameter("start_date-"+i));
            line.setUsers(request.getParameter("users-"+i));
            line.setBank(request.getParameter("bank-"+i));
            line.setDept(request.getParameter("dept-"+i));
            line.setModel(request.getParameter("model-"+i));
            line.setEquip_brand(request.getParameter("equip_brand-"+i));
            line.setEquip_type(request.getParameter("equip_type-"+i));
            line.setApplication(request.getParameter("app-"+i));
            line.setState(request.getParameter("state-"+i));
            line.setComment(request.getParameter("comment-"+i));
            dataset.add(line);
        }

        response.setContentType("octets/stream");
        response.addHeader("Content-Disposition", "attachment;filename=result.xls");
        ExportExcel<BankIPAddress> ex = new ExportExcel<BankIPAddress>();
        String[] headers = { "id", "ip地址", "网络类型", "掩码", "网关", "mac地址", "申请时间", "启用日期",
                "设备使用人", "所属支行（分行）", "所属网点（部门）", "设备类型", "设备品牌", "设备型号", "用途", "使用情况", "备注"};
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
