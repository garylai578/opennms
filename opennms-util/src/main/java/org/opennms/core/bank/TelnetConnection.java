package org.opennms.core.bank;

/**
 * Created by laiguanhui on 2016/3/29.
 */
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.SocketException;

/**
 * @author XUQIANG
 *
 */
public class TelnetConnection {
    final static Logger log =  Logger.getLogger(TelnetConnection.class);

    private TelnetClient telnet = null;
    private String prompt = "#$>]";
    private String loginPrompt = "login";
    private String usernamePrompt = "Username:";
    private String passwordPrompt = "Password:";
    private InputStream in;
    private PrintStream out;

    public TelnetConnection(String host, int port) {
        if(telnet == null) {
            telnet = new TelnetClient();
            try {
                log.debug("telnet connect, host ip:" + host + ", port:"+ port);
                telnet.connect(host, port);
                in = telnet.getInputStream();
                out = new PrintStream(telnet.getOutputStream());
            } catch (SocketException e) {
                log.error(e);
                e.printStackTrace();
            } catch (IOException e) {
                log.error(e);
                e.printStackTrace();
            }
        }
    }

    /**
     * 登录到远程机器中<br>
     * 说明：在登录前，先确认输入用户名的提示符，如果不是Username：，需要设置该值，使用setUsernamePrompt(prompt)；<br>
     *       第二，确认输入密码时的提示符，如果不是Password：，需要设置该值,使用setPasswordPrompt(prompt)；<br>
     *       第三，确认登录后查看是否有登录后的提示信息：如：%Apr 17 04:26:32:256 2000 Quidway SHELL/5/LOGIN:- 1 - admin(191.168.2.227) in unit1 login <br>
     *              如果末尾不是login,需要指定最后一个单词，使用setLoginPrompt(prompt)。
     *              如果没有登录提示，设置setLoginPrompt(null);
     *       第四，执行命令时，如果提示符不是 #、$、>、]中的一个，也需要指定最后一个符号，使用setPrompt(prompt).
     **/
    public void login(String username, String password, String prompt) {
        //处理命令行的提示字符
        if(prompt != null && !"".equals(prompt)) {
            this.prompt = prompt;
        }
        readUntil(this.usernamePrompt);
        log.debug("发送用户名:" + username);
        write(username);
        readUntil(this.passwordPrompt);
        log.debug("发送用密码:" + password);
        write(password);
        log.debug("登录成功");
        readUntilPrompt(this.prompt);

        if(this.loginPrompt != null)
            readUntil(this.loginPrompt);
    }

    /**
     * 读取分析结果
     *
     * @param pattern
     * @return
     */
    public String readUntil(String pattern) {
        try {
            char lastChar = pattern.charAt(pattern.length() - 1);
            StringBuffer sb = new StringBuffer();
            char ch = (char) in.read();
            while (true) {
                sb.append(ch);
//                log.debug("sb:" + sb);
                if (ch == lastChar) {
                    if (sb.toString().endsWith(pattern)) {
                        log.debug(sb);
                        return sb.toString();
                    }
                }
                ch = (char) in.read();
            }
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
        }
        return null;
    }

    /** 读取分析结果
      * @param pattern
     * @return
     * */
    public String readUntilPrompt(String pattern) {
        StringBuffer sb = new StringBuffer();
        try {
            int len = 0;
            while((len = in.read()) != -1) {
                sb.append((char)len);
                if(pattern.indexOf((char)len) != -1 || sb.toString().endsWith(pattern)) {
                    log.debug(sb);
                    return sb.toString();
                }
            }
        } catch (IOException e) {
            log.error(e);
            e.printStackTrace();
        }
        return "";
    }

    /** * 写操作
     *  @param value
     */
    public void write(String value) {
        try {
            out.println(value);
            out.flush();
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
        }
    }

    /**
     * 向目标发送命令字符串
     * @param command
     * @return
     */
    public String sendCommand(String command) {
        log.debug("发送交换机命令：" + command);
        try {
            write(command);
            String result = readUntilPrompt(prompt + "");
            return result;
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
        }
        return "";
    }

    /** 关闭连接 */
    public void disconnect() {
        try {
            telnet.disconnect();
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
        }
    }

    /**
     * @return the prompt
     */
    public String getPrompt() {
        return prompt;
    }

    /**
     * @param prompt the prompt to set
     */
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    /**
     * @return the usernamePrompt
     */
    public String getUsernamePrompt() {
        return usernamePrompt;
    }

    /**
     * @param usernamePrompt the usernamePrompt to set
     */
    public void setUsernamePrompt(String usernamePrompt) {
        this.usernamePrompt = usernamePrompt;
    }

    /**
     * @return the passwordPrompt
     */
    public String getPasswordPrompt() {
        return passwordPrompt;
    }

    /**
     * @param passwordPrompt the passwordPrompt to set
     */
    public void setPasswordPrompt(String passwordPrompt) {
        this.passwordPrompt = passwordPrompt;
    }

    /**
     *  @return the loginPrompt
     */
    public String getLoginPrompt() {
        return loginPrompt;
    }

    /**
     * @param loginPrompt the loginPrompt to set
     */
    public void setLoginPrompt(String loginPrompt) {
        this.loginPrompt = loginPrompt;
    }

    /**
     * 关闭打开的连接
     * @param telnet
     */
    public void close(TelnetClient telnet) {
        if(telnet != null) {
            try {
                telnet.disconnect();
                log.debug("关闭连接！");
            } catch (IOException e) {
                log.error(e);
                e.printStackTrace();
            }
        }
        if(this.telnet != null) {
            try {
                this.telnet.disconnect();
                log.debug("关闭连接！");
            } catch (IOException e) {
                log.error(e);
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            log.debug("启动Telnet...");
            String ip = "172.16.3.254";
            int port = 23;
            String user = "admin";
            String password = "123456";
            TelnetConnection telnet = new TelnetConnection(ip, port);
            telnet.setUsernamePrompt("login:");
            telnet.setLoginPrompt(null);
            telnet.login(user, password, "");
            String r1 = telnet.sendCommand("tftp 172.16.3.57 put startup.cfg");//display snmp-agent local-engineid
            System.out.println("显示结果");
            System.out.println(r1);

            r1 = telnet.sendCommand("restore startup-configuration from 172.16.3.57 startup.cfg");
            r1 = telnet.sendCommand("Y");
            System.out.println("显示结果");
            System.out.println(r1);
            telnet.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}