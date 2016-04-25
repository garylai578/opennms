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
    int port = 23;
    TelnetConnection telnet;
    String bundingResult;
    String flag = "@result_split_flag@";

    public SwitcherUtil(String host, String user, String password){
        try {
            DesUtil du = new DesUtil();
            password = du.decrypt(password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.host = host;
        this.user = user;
        this.password = password;
    }

    public SwitcherUtil(String host, String user, String password, int port){
        try {
            DesUtil du = new DesUtil();
            password = du.decrypt(password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.host = host;
        this.user = user;
        this.password = password;
        this.port = port;
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
            String[] lines = result.split("\\n");
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

            connect();
            for(String inter : interfaces){
                //show run interface FastEthernet 0/× 查看单个端口的dot1x状态
                String result = telnet.sendCommand("show run interface " + inter );
                if(result.contains("dot1x port-control auto"))
                    dot1x.add("auto");
                else
                    dot1x.add("none");
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
        telnet.sendCommand("end");
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
        telnet.sendCommand("end");
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
        telnet.sendCommand("end");
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
        telnet.sendCommand("no dot1x port-control auto");
        telnet.sendCommand("end");
        return 1;
    }

    /**
     * 绑定IP
     * @param ips 待绑定的ip列表
     * @param no_dot1x_before 绑定前是否先取消dot1x认证
     * @param dot1x_after 绑定后是否添加dot1x认证
     * @param interRange 进行dot1x操作的端口范围，格式：fastEthernet 0/31-41
     * @return 交换机的操作输出，每个ip的绑定结果以“@result_split_flag@”分隔
     */
    public String bundingIPs(String[] ips, boolean no_dot1x_before, boolean dot1x_after, String interRange){
        bundingResult = "";
        String tmp;

	    connect();
        if(no_dot1x_before){
            bundingResult = "执行前关闭端口：" + interRange + "的认证\n";
            telnet.sendCommand("config");
            telnet.sendCommand("interface range " + interRange);
            telnet.sendCommand("no dot1x port-control auto");
            telnet.sendCommand("end");
        }

        //查看交换机型号，对于不同型号，绑定的流程不一样
        telnet.sendCommand("terminal width 256");
        tmp = telnet.sendCommand("show version");

        //如果是S5750P或者S3760E
        if(tmp.contains("S5750P") || tmp.contains("S3760E")){
            for(int i = 0; i < ips.length; ++i){
                if(ips[i].equals(""))
                    return "";
                ips[i] = ips[i].trim();
                int find = bundingS5750P(ips[i]);
                if(find == -1){
                    bundingResult += "绑定失败\n";
                }else if(find == 1){
                    bundingResult += "重复绑定\n";
                }else if(find == 0){
                    bundingResult += "绑定成功\n";
                }
            }
        }else if(tmp.contains("S3760-48")){  //如果是S3760-48
            for(int i = 0; i < ips.length; ++i) {
                ips[i] = ips[i].trim();
                int find = bundingS3760_48(ips[i]);
                if(find == -1){
                    bundingResult += "绑定失败\n";
                }else if(find == 1){
                    bundingResult += "重复绑定\n";
                }else if(find == 0){
                    bundingResult += "绑定成功\n";
                }
            }
        }

        if(dot1x_after){
            bundingResult += "执行后开启端口：" + interRange + "的认证\n";
            telnet.sendCommand("config");
            telnet.sendCommand("interface range " + interRange);
            telnet.sendCommand("dot1x port-control auto");
            telnet.sendCommand("end");
        }

        bundingResult += flag;
        telnet.disconnect();
        return bundingResult;
    }

    //对锐捷S5750P或者S3760E型号的交换机做ACL安全通道
    private int bundingS5750P(String ip) {
        bundingResult += "对ip=[" +ip + "]进行静态地址绑定...\n";
        telnet.setPrompt("#");
        telnet.sendCommand("ping " + ip + " ntimes 1 timeout 1"); //会返回“<>”符号
        telnet.setPrompt("#$>]");

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


//        String port;
        String mac;
//        String vlan;
        String result = telnet.sendCommand("sh arp " + ip);
        log.debug("sh arp: " + result);
        //找出MAC和vlan号
        String patt = ip + ".*(([0-9a-z]{4}\\.){2}[0-9a-z]{4}).*(VLAN.*[0-9])";
        Pattern p1 = Pattern.compile(patt);
        Matcher matcher = p1.matcher(result);
        if (matcher.find()) {
            mac = matcher.group(1);
//            vlan = matcher.group(3);
//            bundingResult += "MACAddress = [" + mac + "]\n";
            log.debug("mac:" + mac);
        }else{
            bundingResult += "查找IP[" + ip + "]对应MAC地址失败!\n";
            log.debug("查找IP[" + ip + "]对应MAC地址失败!");
            return  -1;
        }

        //检查MAC地址是否已经做了静态绑定
/*
        result = telnet.sendCommand("sh mac-address-table static address " + mac);
        patt = mac + ".*STATIC.*(FastEthernet.*[0-9]$)";
        p1 = Pattern.compile(patt);
        matcher = p1.matcher(result);
        if(matcher.find()) {
            port = matcher.group(1);
            bundingResult += "PortNo = [" + port + "]\n";
            bundingResult += "IP[" + ip + "]对应MAC地址[" + mac + "]已经做了静态绑定!\n";
            return 1;
        }else{
            bundingResult += "IP[" + ip + "]对应MAC地址[" + mac + "]未做静态绑定!\n";
        }
*/

        //对端口进行认证
        bundingResult += "对IP[" + ip + "]对应的MAC地址[" + mac + "]进行ACL认证...\n";
        telnet.sendCommand("configure terminal");
        telnet.sendCommand("expert access-list extended no1x");
        telnet.sendCommand("permit ip any host " + mac + " any any");
        telnet.sendCommand("end");
        return 0;
    }

    //对锐捷S3760-48型号的交换机进行静态地址绑定
    private int bundingS3760_48(String ip) {
        bundingResult += "对ip=[" +ip + "]进行静态地址绑定...\n";
        telnet.setPrompt("#");
        telnet.sendCommand("ping " + ip + " ntimes 1 timeout 1"); //会返回“<>”符号
        telnet.setPrompt("#$>]");
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String port;
        String mac;
        String vlan;
        String result = telnet.sendCommand("sh arp " + ip);
        //找出MAC和vlan号
        String patt = ip + ".*(([0-9a-z]{4}\\.){2}[0-9a-z]{4}).*(VLAN.*[0-9])";
        Pattern p1 = Pattern.compile(patt);
        Matcher matcher = p1.matcher(result);
        if (matcher.find()) {
            mac = matcher.group(1);
            vlan = matcher.group(3);
            bundingResult += "MACAddress = [" + mac + "]\nVLANNO = [" + vlan + "]\n";
            log.debug("mac:" + mac + "\t vlan:" + vlan);
        }else{
            bundingResult += "查找IP[" + ip + "]对应MAC地址失败!\n";
            log.debug("查找IP[" + ip + "]对应MAC地址失败!");
            return  -1;
        }

        //检查MAC地址是否已经做了静态绑定
        result = telnet.sendCommand("sh mac-address-table static address " + mac);
        patt = mac + ".*STATIC.*(FastEthernet.*[0-9])";
        p1 = Pattern.compile(patt);
        matcher = p1.matcher(result);
        if(matcher.find()) {
            port = matcher.group(1);
            bundingResult += "PortNo = [" + port + "]\n";
            bundingResult += "IP[" + ip + "]对应MAC地址[" + mac + "]已经做了静态绑定!\n";
            return 1;
        }else{
            bundingResult += "IP[" + ip + "]对应MAC地址[" + mac + "]未做静态绑定!\n";
        }

        //对没有做端口绑定进行绑定，首选需要找到端口号
        patt = "([0-9]{1,4}).*" + mac + ".*(FastEthernet.*[0-9])";
        p1 = Pattern.compile(patt);
        result = telnet.sendCommand("sh mac address " + mac);
        matcher = p1.matcher(result);
        if(matcher.find()){
            port = matcher.group(2);
            bundingResult += "PortNo = [" + port + "]\n";
            //做静态地址绑定:mac-address-table static 001a.a923.d8f1 vlan 150 interface FastEthernet 0/24
            telnet.sendCommand("configure terminal");
            telnet.sendCommand("mac-address-table static " + mac + " " + vlan + " interface " + port);
            telnet.sendCommand("end");
        }else{
            bundingResult += "查找IP[" + ip + "]MAC地址[" + mac + "]物理端口号失败!\n";
            return  -1;
        }
        return 0;
    }

    /**
     * 对macs地址列表进行解绑
     * @param macs 待解绑的macs地址列表
     * @return 交换机的操作输出，每个mac的解绑结果以“@result_split_flag@”分隔
     */
    public String deletBunding(String[] macs) {
        bundingResult = "";
        String tmp;

	    connect();
        //查看交换机型号，对于不同型号，绑定的流程不一样
        telnet.sendCommand("terminal width 256");
        tmp = telnet.sendCommand("show version");

        //如果是S5750P或者S3760E
        if(tmp.contains("S5750P") || tmp.contains("S3760E")){
            for(int i = 0; i < macs.length; ++i){
                macs[i] = macs[i].trim();
                bundingResult += "对MAC[" + macs[i] + "]进行解绑\n";
                int find = unBundingS5750P(macs[i]);
                if(find == -1){
                    bundingResult += "解除绑定失败\n";
                }else if(find == 0){
                    bundingResult += "解除绑定成功\n";
                }else if(find == 1){
                    bundingResult += "找不到该mac地址对应的ACL";
                }
                bundingResult += flag;
            }
        }else if(tmp.contains("S3760-48")){  //如果是S3760-48
            for(int i = 0; i < macs.length; ++i) {
                macs[i] = macs[i].trim();
                int find = unBundingS3760_48(macs[i]);
                if(find == -1){
                    bundingResult += "解除绑定失败\n";
                }else if(find == 0){
                    bundingResult += "解除绑定成功\n";
                }else if(find == 1){
                    bundingResult += "未绑定该mac地址";
                }
                bundingResult += flag;
            }
        }

        telnet.disconnect();
        return bundingResult;
    }

    //"sh access -list"找到acl的号num，然后"expert access-list extended no1x"-->"no num"
    private int unBundingS5750P(String mac) {
        bundingResult += "对mac=[" +mac + "]进行解除绑定...\n";
        telnet.sendCommand("config");
        String result = telnet.sendCommand("sh access-list");
        String patt = "([0-9]*) permit ip any host " + mac + " any any";
        Pattern p1 = Pattern.compile(patt);
        Matcher matcher = p1.matcher(result);
        String num;
        if(matcher.find()) {
            num = matcher.group(1);
            bundingResult += "num=[" +num + "]\n";
        }else{
            return 1;
        }
        telnet.sendCommand("expert access-list extended no1x");
        telnet.sendCommand("no " + num);
        telnet.sendCommand("end");

        return 0;
    }

    //no mac-address-table static b42c.922e.d06e vlan 160 interface FastEthernet 0/32
    private int unBundingS3760_48(String mac) {
        //通过"sh mac-address-table static address " + mac 获取vlan号和端口号
        bundingResult += "对mac=[" +mac + "]进行解除绑定...\n";
        String result = telnet.sendCommand("sh mac-address-table static address " + mac);
        String patt = "([0-9]{1,4}).*" + mac + ".*(FastEthernet.*[0-9])";
        Pattern p1 = Pattern.compile(patt);
        Matcher matcher = p1.matcher(result);
        String vlan, port;
        if(matcher.find()) {
            vlan = matcher.group(1);
            port = matcher.group(2);
            bundingResult += "vlan no=[" +vlan + "]\tport=["+port+"]\n";
        }else{
            return 1;
        }
        //解除静态地址绑定:no mac-address-table static 001a.a923.d8f1 vlan 150 interface FastEthernet 0/24
        telnet.sendCommand("configure terminal");
        telnet.sendCommand("no mac-address-table static " + mac + " vlan " + vlan + " interface " + port);
        telnet.sendCommand("end");
        return 0;
    }

    public void diconnect(){
        telnet.disconnect();
    }

    private void connect(){
        if(telnet == null || !telnet.isConnected()) {
            telnet = new TelnetConnection(host, port);
            telnet.setUsernamePrompt("Username:");
            telnet.setLoginPrompt(null);
            telnet.login(user, password, "");
        }
    }
}
