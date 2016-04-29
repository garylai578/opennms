package org.opennms.web.abcbank;

import org.opennms.core.bank.BundingIP;
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
 * Created by laiguanhui on 2016/4/26.
 */
public class ExportBundingMsgServlet extends HttpServlet {

    private static final long serialVersionUID = 2417982866821484913L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String host = request.getParameter("host");
        String name = request.getParameter("switcherName");
        String tmp = request.getParameter("rows");
        int rows = Integer.parseInt(tmp);
        List<BundingIP> dataset = new ArrayList<BundingIP>();
        for(int i = 0; i < rows; ++i){
            BundingIP bundingIP = new BundingIP();
            bundingIP.setIp(request.getParameter("ip-"+i));
            bundingIP.setMac(request.getParameter("mac-"+i));
            bundingIP.setInter(request.getParameter("inter-"+i));
            bundingIP.setVlan(request.getParameter("vlan-"+i));
            dataset.add(bundingIP);
        }

        response.setContentType("octets/stream");
        response.addHeader("Content-Disposition", "attachment;filename=" + name + "-" + host + ".xls");
        ExportExcel<BundingIP> ex = new ExportExcel<BundingIP>();
        String[] headers = { "IP", "MAC", "端口", "VLAN"};
        try {
            OutputStream out = response.getOutputStream();
            ex.exportExcel(host, headers, dataset, out);
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
