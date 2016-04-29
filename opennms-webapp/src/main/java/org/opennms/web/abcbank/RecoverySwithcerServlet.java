package org.opennms.web.abcbank;

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
 * Created by laiguanhui on 2016/3/29.
 */
public class RecoverySwithcerServlet extends HttpServlet {

    private static final long serialVersionUID = -6889152042825043392L;
    private String host;
    private String user;
    private String pwd;
    private int port = 23;
    private String recovery = "tftp 172.16.3.57 get startup.cfg";
    private String fileName = "";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        fileName = this.getServletContext().getRealPath("/") + "/abcbank/switcher.log";
        String tmp = request.getParameter("rowID");
        int row = Integer.parseInt(tmp);
        host = request.getParameter("host-"+row);
        user = request.getParameter("user-"+row);
        pwd = request.getParameter("password-"+row);

        Date nowDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        appendFile("\r\n" + format.format(nowDate) + " 执行手工恢复：\r\n 交换机IP：" + host + ",\t" +
                "用户名："+ user + "\r\n");
        String result = recovery();
        appendFile("恢复结果：\r\n" + result);

        response.setContentType("text/html;charset=gb2312");
        PrintWriter pw = response.getWriter();
        pw.print("<script language='javascript'>alert('恢复完成，请在日志中查看结果!' );window.location=('/opennms/abcbank/switcher.jsp');</script>");
        pw.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    private String recovery(){
        TelnetConnection telnet;
        String result;
        try {
            telnet = new TelnetConnection(host, port);
            telnet.setUsernamePrompt("login:");
            telnet.setLoginPrompt(null);
            telnet.login(user, pwd, "");
            telnet.sendCommand(recovery);
            result = telnet.sendCommand("Y");
            System.out.println("显示结果:");
            System.out.println(result);
            telnet.sendCommand("exit");
        } catch (IOException e) {
            e.printStackTrace();
            return "交换机连接失败，请稍候再试\r\n" + e.getMessage();
        }
        return result;
    }

    private void appendFile(String content){
//        System.out.println(content);
        try {
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
