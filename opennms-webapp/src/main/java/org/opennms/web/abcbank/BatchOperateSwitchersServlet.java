package org.opennms.web.abcbank;

import org.opennms.core.bank.DesUtil;
import org.opennms.core.bank.TelnetConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by laiguanhui on 2016/4/26.
 */
public class BatchOperateSwitchersServlet extends HttpServlet {

    private static final long serialVersionUID = 8374126776359227757L;
    private String fileName = "";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        fileName = this.getServletContext().getRealPath("/") + "/abcbank/switcher.log";
        String sws = request.getParameter("sws");
        String batchComm = request.getParameter("batchComm");
        response.setContentType("text/html;charset=gb2312");
        PrintWriter pw=response.getWriter();

        String[] swList = sws.split("\t");
        String[] batchCommands = batchComm.split("\n");
        for(int i = 0; i < swList.length; ++i){

            String host = request.getParameter("host-" + swList[i]);
            String user = request.getParameter("user-" + swList[i]);
            String password = request.getParameter("password-" + swList[i]);

            Date nowDate = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            appendFile("\r\n" + format.format(nowDate) + " 对交换机："+ host + "执行批量操作：");

            try {
                DesUtil du = new DesUtil();
                password = du.decrypt(password);
            } catch (Exception e) {
                appendFile("对交换机密码解密错误：");
                appendFile(e.getMessage());
                continue;
            }

            try {
                TelnetConnection tc = new TelnetConnection(host, 23);
                tc.setUsernamePrompt("Username:");
                tc.setLoginPrompt(null);
                tc.login(user, password, "");
                for(String comm : batchCommands){
                    appendFile("发送命令：" + comm);
                    String result = tc.sendCommand(comm);
                    appendFile("返回结果：" + result);
                }
                tc.sendCommand("exit");
            } catch (IOException e) {
                appendFile("连接交换机失败或发送命令失败：");
                appendFile(e.getMessage());
                continue;
            }
        }

        pw.print("<script language='javascript'>alert('完成批量操作，请在日志中查看结果！' );window.location=('/opennms/abcbank/switcher.jsp');</script>");
        pw.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    private void appendFile(String content){
//        System.out.println(content);
        try {
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(content + "\r\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
