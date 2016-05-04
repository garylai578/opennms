package org.opennms.core.bank;

import org.apache.log4j.Logger;
import org.opennms.core.resource.Vault;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by laiguanhui on 2016/4/29.
 */
public class BankLogWriter3 {

    final static Logger log =  Logger.getLogger(BankLogWriter3.class);
    //Log文件路径
    private String filePath;
    private String fileName;
    private BufferedOutputStream out;
    private BufferedReader in;

    public BankLogWriter3(){
        filePath = Vault.getHomeDir() + System.getProperty("file.separator") + "logs" + System.getProperty("file.separator");

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        fileName = filePath + "abc_" + df.format(new Date()) + ".log";
        log.debug("log file：" + fileName);
/*        File file = new File(fileName);
        if(!file.exists()){
            try {
                file.createNewFile();
                out=new BufferedOutputStream(new FileOutputStream(file,true));
            } catch (IOException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }*/
    }


    /**
     * 将信息写入文件中
     * @param msg 待写入的信息
     */
    public void writeLog(String msg){
/*        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");//设置日期格式
        String time =  df.format(new Date());
        msg = time + msg;
        try {
            if(out == null)
                out=new BufferedOutputStream(new FileOutputStream(new File(fileName),true));
            out.write(msg.getBytes());
            out.write(System.getProperty("line.separator").getBytes());
            out.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
		*/
		log.debug(msg);
    }

    public String readLog() {
        String result="";
        try {
            in = new BufferedReader(new FileReader(new File(fileName)));
            String line;
            // 一次读入一行，直到读入null为文件结束
            while ((line = in.readLine()) != null) {
                result += line + "\n";
            }
            in.close();
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 设置Log文件路径
     * @param filePath 文件路径，必须以系统路径分隔符结尾
     *
     */
    public void setOutputFilePath(String filePath){
    /*    this.filePath = filePath;
        File file = new File(filePath);
        if(!file.exists()){
            try {
                file.createNewFile();
                out=new BufferedOutputStream(new FileOutputStream(file,true));
            } catch (IOException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }*/
    }
}
