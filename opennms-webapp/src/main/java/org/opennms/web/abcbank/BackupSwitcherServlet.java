package org.opennms.web.abcbank;

import org.opennms.core.bank.TelnetConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by laiguanhui on 2016/3/29.
 */
public class BackupSwitcherServlet extends HttpServlet {

    private static final long serialVersionUID = 4035891609154414416L;
    private String host;
    private String user;
    private String pwd;
    private int port = 23;
    private String backup = "";
    private String fileName = "";
    private String cycle1; //每日、每周、每月
    private String cycle2; //如果cycle1是每日，cycle2就是时间；如果是每周或每月，cycle2就是周一至周日，或1日至31日
    private String cycle3; //如果cycle2是每周或每月,cycle3就是时间

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request. setCharacterEncoding("UTF-8");
        String alertMsg;
        String tmp = request.getParameter("rowID");
        int row = Integer.parseInt(tmp);
        host = request.getParameter("host-"+row);
        user = request.getParameter("user-"+row);
        pwd = request.getParameter("password-"+row);
        backup = request.getParameter("backup-"+row);
        String isCycle = request.getParameter("isCycle");
        cycle1 = request.getParameter("cycle1_"+row);
        cycle2 = request.getParameter("cycle2_"+row);
        cycle3 = request.getParameter("cycle3_"+row);

        Date nowDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        fileName = this.getServletContext().getRealPath("/") + "/abcbank/switcher.log";
        System.out.println(fileName);

        //如果没有设置定时器，则马上执行，否则在定时时间执行
        if(isCycle.equals("0") || isCycle.equals("") || isCycle==null) {
            appendFile("\r\n" + format.format(nowDate) + " 执行手工备份：\r\n 交换机IP：" + host + ",\t" +
                    "用户名："+ user + "\r\n");
            String result = backup();
            appendFile("备份结果：\r\n" + result);
            alertMsg = "备份完成，请在日志中查看结果！";
        } else {
            Long[] res = new Long[0];
            try {
                res = getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Runnable runnable = new Runnable() {
                public void run() {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    appendFile("\r\n" + format.format(new Date()) + " 执行定时备份：\r\n 交换机IP：" + host + ",\t用户名：" + user + "\r\n");
                    String result = backup();
                    appendFile("备份结果：\r\n" + result);
                }
            };
            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
            System.out.println("period seconds:"+res[0] + ",\t initial delay seconds:" + res[1]);
            service.scheduleAtFixedRate(runnable, res[1], res[0], TimeUnit.SECONDS);

            alertMsg = "定时备份设置完成！";
            appendFile("\r\n将在 "+ format.format(new Date(nowDate.getTime()+res[1]*1000)) + " 执行定时备份。\r\n");
            /*Long inter = Long.valueOf(0);
            try {
                getTime();
                Long runTime = format.parse(date).getTime();
                Long now = new Date().getTime();
                inter = runTime - now;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    appendFile("\n" + date + " 执行定时备份：\n 交换机IP：" + host + ",\t用户名：" + user + "\n");
                    String result = backup();
                    appendFile("备份结果：\n" + result);
                }
            }, inter);*/
        }
        response.setContentType("text/html;charset=gb2312");
        PrintWriter pw = response.getWriter();
        pw.print("<script language='javascript'>alert('" + alertMsg + "' );window.location=('/opennms/abcbank/switcher.jsp');</script>");
        pw.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    /**
     *
     * @return Long[0]-定时执行的间隔时间，Long[1]-首次执行的延时时间
     * @throws ParseException
     */
    private Long[] getTime() throws ParseException {
        Long[] result = new Long[2];
        Date nowDate = new Date();

//        System.out.println("cycle1:"+ cycle1);
        if(cycle1.equals("每天")){
            result[0] = Long.valueOf(24*60*60);
            int planTime = Integer.parseInt(cycle2.substring(0, cycle2.length()-1));
            result[1] = calculateSecond(planTime);
//            System.out.println("initialDelay:" +  result[1]);
        }else if(cycle1.equals("每周")){
            result[0] = Long.valueOf(7*24*60*60);
            Calendar cal = Calendar.getInstance();
            cal.setTime(nowDate);
            int nowDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
            int planDay = 0;
            int delayDays;
            if("周一".equals(cycle2))
                planDay = 1;
            else if("周二".equals(cycle2))
                planDay = 2;
            else if("周三".equals(cycle2))
                planDay = 3;
            else if("周四".equals(cycle2))
                planDay = 4;
            else if("周五".equals(cycle2))
                planDay = 5;
            else if("周六".equals(cycle2))
                planDay = 6;
            else if("周日".equals(cycle2))
                planDay = 7;

            if(planDay > nowDay)
                delayDays = planDay - nowDay + 1;
            else
                delayDays = planDay - nowDay + 6;
            int planTime = Integer.parseInt(cycle3.substring(0, cycle3.length()-1));
            result[1] = delayDays*24*60*60 + calculateSecond(planTime);
        }
//        System.out.println("per:"+period+", delay:"+initialDelay+", res[0]:"+result[0]+", res[1]"+result[1]);
        return  result;
    }

    /**
     * 计算与目前的时间间隔
     * @param planHour 计划的时间（小时）
     * @return 计划时间和当前时间的间隔（秒）
     */
    private Long calculateSecond(int planHour){
        Long result;
        Date nowDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("HH");
        int nowHour = Integer.parseInt(format.format(nowDate));
        format = new SimpleDateFormat("mm");
        int nowMin = Integer.parseInt(format.format(nowDate));
        format = new SimpleDateFormat("ss");
        int nowSec = Integer.parseInt(format.format(nowDate));
        if(planHour > nowHour){
            result = Long.valueOf(planHour*60*60 - (nowHour*60*60 + nowMin*60 + nowSec));
//            System.out.println("result1:" + result);
        }else{
            result = Long.valueOf((planHour+24)*60*60 - (nowHour*60*60 + nowMin*60 + nowSec));
//            System.out.println("result2:" + result);
        }
        return  result;
    }

    /**
     * backup the switcher by telnet
     * @return the backup result
     */
    private String backup(){
        TelnetConnection telnet;
        try {
            telnet = new TelnetConnection(host, port);
        } catch (IOException e) {
            e.printStackTrace();
            return "连接交换机失败：\r\n" + e.getMessage();
        }
        telnet.setUsernamePrompt(":");
        telnet.setLoginPrompt(null);
        telnet.login(user, pwd, "");
        String result = telnet.sendCommand(backup);
        telnet.disconnect();
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
