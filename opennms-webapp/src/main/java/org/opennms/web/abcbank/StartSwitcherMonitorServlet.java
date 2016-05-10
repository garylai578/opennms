package org.opennms.web.abcbank;

import org.opennms.core.bank.BankLogWriter;
import org.opennms.core.bank.SwitcherStats;
import org.opennms.core.bank.SwitcherStatsOperator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by laiguanhui on 2016/5/6.
 */
public class StartSwitcherMonitorServlet extends HttpServlet {

    private static final long serialVersionUID = -3419933500338159825L;
    private boolean flag = false;
    private long hourInFlow = 0;
    private long hourOutFlow = 0;
    private String hour1;
    private String day1;
    private int t = 1;

    public void init(){
        flag = true;
        SimpleDateFormat df = new SimpleDateFormat("HH");//设置日期格式,24小时制
        hour1 = df.format(new Date());
        SimpleDateFormat df2 = new SimpleDateFormat("dd");
        day1 = df2.format(new Date());
        BankLogWriter.getSingle().writeLog("自启动StartSwitcherMonitorServlet！");
        //设置定时器，每10分钟对所有交换机进行一次流量的获取并插入数据库
        Runnable runnable = new Runnable() {
            public void run() {
                BankLogWriter.getSingle().writeLog("执行定时交换机流量监控");
                startSwitcherMonitor();
            }
        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        service.scheduleAtFixedRate(runnable, 0, 300, TimeUnit.SECONDS);
    }

    private void startSwitcherMonitor(){
        SwitcherStatsOperator operator = new SwitcherStatsOperator();
        ArrayList<String> inFlowOidGroup = new ArrayList<String>();
        ArrayList<String> outFlowOidGroup = new ArrayList<String>();
        for(int i = 1; i <=52; ++i) {
            inFlowOidGroup.add(".1.3.6.1.2.1.2.2.1.10." + i);
            outFlowOidGroup.add(".1.3.6.1.2.1.2.2.1.16." + i);
        }
        try {
            SwitcherStats[] sws = operator.selectAll();
            for(SwitcherStats sw : sws){
                BankLogWriter.getSingle().writeLog("监控交换机的流量：" + sw.getIp());
                SimpleDateFormat df = new SimpleDateFormat("HH");//设置日期格式
                String hour2 = df.format(new Date());;
                SimpleDateFormat df2 = new SimpleDateFormat("dd");
                String day2 = df2.format(new Date());
                BankLogWriter.getSingle().writeLog("时间hour1：" + hour1 + ", hour2:" + hour2 + "; day1:" + day1 + ", day2: " + day2);

                Flow flow = new Flow(sw.getIp());
                flow.setOcter("abc123");
                long inFlowValue = flow.calcFlowValue(inFlowOidGroup);
                long outFlowValue = flow.calcFlowValue(outFlowOidGroup);
                BankLogWriter.getSingle().writeLog("inflow：" + inFlowValue + ", outFlow:" + outFlowValue);

                //如果是新的一天则先重置
                if(!day1.equals(day2)){
                    operator.update(sw.getIp(), "flow", "'-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-/t-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-'" );
                    day1 = day2;
                    hour1 = hour2;
                    t = 1;
                }

                if(hour2.equals(hour1) && t < 2){   //同一个小时内的流量进行累计
                    t++;
                    hourInFlow += inFlowValue;
                    hourOutFlow += outFlowValue;
                }else{      //对一个小时内的流量进行计算，并替换原值
                    hourInFlow = hourInFlow / t;
                    hourOutFlow = hourOutFlow / t;
                    String oldValue = operator.getColunm(sw.getIp(), "flow");
                    String[] oldSplit = oldValue.split(",");

                    BankLogWriter.getSingle().writeLog("时间：" + hour1);
                    oldSplit[Integer.parseInt(hour1) - 1] = hourInFlow + "";
                    oldSplit[Integer.parseInt(hour1) + 24 - 1] = hourOutFlow + "";

                    String newString ="";
                    for(int j=0; j < oldSplit.length; ++j){
                        newString += oldSplit[j] + ",";
                    }
                    newString = newString.substring(0, newString.length() -1);
                    operator.update(sw.getIp(), "flow", "'" + newString + "'" );

                    hour1 = hour2;
                    t = 1;
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(!flag) {
            BankLogWriter.getSingle().writeLog("手工启动StartSwitcherMonitorServlet！");
            init();
        }
        else {
            response.setContentType("text/html;charset=gb2312");
            PrintWriter pw=response.getWriter();
            pw.print("<script language='javascript'>alert('交换机流量监控已经启动！' );window.location=('/opennms/report/switcher.jsp');</script>");
            pw.close();
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
