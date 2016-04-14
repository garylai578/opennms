package org.opennms.core.bank;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by laiguanhui on 2016/4/12.
 */
public class SwitcherUtil {

    final static Logger log =  Logger.getLogger(SwitcherUtil.class);

    List<String> status = new ArrayList();
    List<String> interfaces = new ArrayList();
    List<String> dot1x = new ArrayList();
    String host;
    String user;
    String password;
    TelnetConnection telnet;

    public SwitcherUtil(String host, String user, String password){
        this.host = host;
        this.user = user;
        this.password = password;
    }

    /**
     * 获取交换机的端口信息
     * @return
     */
    public String[] getInterfaces(){
        if(interfaces.size() == 0) {
            connect();
            //show interfaces status可以查看所有端口的开关状态，show run查看端口的dot1x认证状态
            String result = telnet.sendCommand("show interfaces status");
            String[] lines = result.split("/n");
            Pattern pattern = Pattern.compile("^[a-zA-Z]*Ethernet [0-9]{1,2}/[0-9]{1,2}"); //匹配“FastEthernet 0/48”或“GigabitEthernet 0/50”之类的
            for(String line : lines) {
                log.debug("匹配行：" + line);
                Matcher matcher = pattern.matcher(line);
                if(matcher.find()){
                    interfaces.add(matcher.group(0));
                    if(line.contains("down")){
                        status.add("down");
                    }else if(line.contains("up")){
                        status.add("up");
                    }else{
                        status.add(" ");
                    }
                }
            }
        }
        return interfaces.toArray(new String[interfaces.size()]);
    }

    /**
     * 获取交换机端口的状态（up / down）
     * @return
     */
    public String[] getStates(){
        if(status.size() == 0){
            getInterfaces();
        }
        return status.toArray(new String[status.size()]);
    }

    /**
     * 获取交换机端口的dot1x认证状态
     * @return
     */
    public String[] getDot1x(){
        if(dot1x.size() == 0){
            if(interfaces.size() == 0 )
                getInterfaces();

            for(String inter : interfaces){
                connect();
                //show run interface FastEthernet 0/× 查看单个端口的dot1x状态
                String result = telnet.sendCommand("show run interface " + inter );
                if(result.contains("dot1x port-control auto"))
                    dot1x.add("auto");
                else
                    dot1x.add("none");
                telnet.disconnect();
            }
        }
        return dot1x.toArray(new String[dot1x.size()]);
    }

    /**
     * 开启交换机的端口
     * @param inter 待开启的端口，例如fastEthernet 0/1
     * @return
     */
    public int upInterface(String inter) {
        //在交换机的config模式下，进入interface：interface fastEthernet 0/××，输入no shutdown
        connect();
        telnet.sendCommand("config");
        telnet.sendCommand("interface " + inter);
        telnet.sendCommand("no shutdown");
        telnet.disconnect();
        return 1;
    }

    /**
     * 关闭交换机的端口
     * @param inter 待关闭的端口，例如fastEthernet 0/1
     * @return
     */
    public int downInterface(String inter) {
        //在交换机的config模式下，进入interface：interface fastEthernet 0/××，输入no shutdown
        connect();
        telnet.sendCommand("config");
        telnet.sendCommand("interface " + inter);
        telnet.sendCommand("shutdown");
        telnet.disconnect();
        return 1;
    }

    /**
     * dot1x认证交换机端口
     * @param inter 待认证的端口，例如fastEthernet 0/1
     * @return
     */
    public int dot1X(String inter) {
        //在交换机的config模式下，进入interface：interface fastEthernet 0/××，输入dot1x port-control auto
        connect();
        telnet.sendCommand("config");
        telnet.sendCommand("interface " + inter);
        telnet.sendCommand("dot1x port-control auto");
        telnet.disconnect();
        return 1;
    }

    /**
     * 取消交换机端口的dot1x认证
     * @param inter 待取消dot1x认证的端口，例如fastEthernet 0/1
     * @return
     */
    public int undoDot1X(String inter) {
        //在交换机的config模式下，进入interface：interface fastEthernet 0/××，输入undo dot1x port-control auto
        connect();
        telnet.sendCommand("config");
        telnet.sendCommand("interface " + inter);
        telnet.sendCommand("undo dot1x port-control auto");
        telnet.disconnect();
        return 1;
    }

    private void connect(){
        telnet = new TelnetConnection(host, 23);
        telnet.setUsernamePrompt("Username:");
        telnet.setLoginPrompt(null);
        telnet.login(user, password, "");
        telnet.write("en");
        telnet.readUntil("Password:");
        telnet.write(password);
        telnet.readUntilPrompt("#$>]");
    }
}
