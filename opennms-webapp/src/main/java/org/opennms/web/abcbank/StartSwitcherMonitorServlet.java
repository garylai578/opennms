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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by laiguanhui on 2016/5/6.
 */
public class StartSwitcherMonitorServlet extends HttpServlet {

    private static final long serialVersionUID = -3419933500338159825L;
    private boolean flag = false;
    private Map resultMap = new HashMap();
    private String hour1,hour2;
    private String day1, day2;
    private int t = 1;

    public void init(){
        flag = true;
        final SimpleDateFormat df = new SimpleDateFormat("k");//设置日期格式,1-24小
        hour1 = df.format(new Date());
        final SimpleDateFormat df2 = new SimpleDateFormat("dd");
        day1 = df2.format(new Date());
        BankLogWriter.getSingle().writeLog("自启动交换机流量监控进程：StartSwitcherMonitorServlet！");
        //设置定时器，每10分钟对所有交换机进行一次流量的获取并插入数据库
        Runnable runnable = new Runnable() {
            public void run() {
//                BankLogWriter.getSingle().writeLog("-----------------执行定时交换机流量监控--------------------");
                hour2 = df.format(new Date());
                day2 = df2.format(new Date());
                startSwitcherMonitor();
                if(!day1.equals(day2)){
                    day1 = day2;
                    hour1 = hour2;
                    t = 1;
                }
                if(hour2.equals(hour1)) {   //同一个小时内的流量进行累计
                    t++;
                }else{
                    hour1 = hour2;
                    t = 1;
                }
            }
        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        service.scheduleAtFixedRate(runnable, 0, 600, TimeUnit.SECONDS);
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
            for(int i = 0; i < sws.length; ++i){
                SwitcherStats sw = sws[i];
                BankLogWriter.getSingle().writeLog("监控交换机的流量：" + sw.getIp());
//                BankLogWriter.getSingle().writeLog("时间hour1：" + hour1 + ", hour2:" + hour2 + "; day1:" + day1 + ", day2: " + day2);

                Flow flow = new Flow(sw.getIp());
                flow.setOcter("abc123");
                long inFlowValue = flow.calcFlowValue(inFlowOidGroup);
                long outFlowValue = flow.calcFlowValue(outFlowOidGroup);
                BankLogWriter.getSingle().writeLog("inflow：" + inFlowValue + ", outFlow:" + outFlowValue);

                //如果是新添加的交换机，则先加入到Map中
                if(!resultMap.containsKey(sw.getIp())){
                    String resultValue = 0 + "\t" + 0;
                    resultMap.put(sw.getIp(), resultValue);
                }

                //如果是新的一天则先重置
                if(hour2.equals("1")){
                    operator.update(sw.getIp(), "flow", "'-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-/t-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-'" );
                }

                if(hour2.equals(hour1)){   //同一个小时内的流量进行累计
                    String resultValue = (String)resultMap.get(sw.getIp());
                    String[] values = resultValue.split("\t");
                    long inFlow = Long.parseLong(values[0]) + inFlowValue;
                    long outFlow = Long.parseLong(values[1]) + outFlowValue;
                    resultMap.put(sw.getIp(), inFlow + "\t" + outFlow);
                    insertDB(sw.getIp());   //进行实时结果的显示
                }else{      //对一个小时内的流量进行计算后插入数据库，并替换原值
                    insertDB(sw.getIp());
                    resultMap.put(sw.getIp(), inFlowValue + "\t" + outFlowValue);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void insertDB(String ip) throws SQLException {
        SwitcherStatsOperator operator = new SwitcherStatsOperator();
        String resultValue = (String)resultMap.get(ip);
        String[] values = resultValue.split("\t");
        long inFlow = Long.parseLong(values[0]);
        long outFlow = Long.parseLong(values[1]);
        BankLogWriter.getSingle().writeLog(hour1 + "点的流入：" + inFlow + "bit, 流出：" + outFlow + "bit");

        // byte换算成KB
        long hourInFlow = (long) (inFlow / (1024 * 8.0) / t);
        long hourOutFlow = (long) (outFlow / (1024 * 8.0) / t);
        String oldValue = operator.getColunm(ip, "flow");
        String[] oldSplit = oldValue.split(",|/t");

        oldSplit[Integer.parseInt(hour1) - 1] = hourInFlow + "";
        oldSplit[Integer.parseInt(hour1) + 24 - 1] = hourOutFlow + "";

        String newString ="";
        for(int j=0; j < oldSplit.length; ++j){
            if(j == 23)
                newString += oldSplit[j] + "/t";
            else
                newString += oldSplit[j] + ",";
        }
        newString = newString.substring(0, newString.length() -1);
        operator.update(ip, "flow", "'" + newString + "'" );
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(!flag) {
            BankLogWriter.getSingle().writeLog("手工启动交换机流量监控进程：StartSwitcherMonitorServlet！");
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
