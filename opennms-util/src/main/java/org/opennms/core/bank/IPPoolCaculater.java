package org.opennms.core.bank;

/**
 * ipipipipip0.0.0.0
 * getIPPool()
 *
 * Created by laiguanhui on 2016/2/4.
 */
public class IPPoolCaculater {

    private String initIP;
    private String startIP = "0.0.0.0";
    private String endIP = "0.0.0.0";
    private String mask;
    private int num;
    private IPPool ipPool;

    /**
     *
     * @param initIP: caculate from the initIP
     * @param num: the numbers that needed
     */
    public IPPoolCaculater(String initIP, int num){
        this.initIP = initIP;
        this.num = num;
        ipPool = new IPPool();
        caculate();
    }

    /**
     * IPPool
     */
    private void caculate() {
        mask = caculateMask();
        ipPool.setNetMask(mask);
        caculateStartIP();
        ipPool.setStartIP(startIP);
        caculateEndIP();
        ipPool.setEndIP(endIP);
    }

    /**
     * ipipip
     *
     */
    private void caculateStartIP() {

        String[] temp1 = initIP.trim().split("\\.");
        int tmp3 = Integer.parseInt(temp1[3]);
        if(tmp3 + num > 255) {
            startIP = "0.0.0.0";
            return;
        }
        //ip
        String binary = Integer.toBinaryString(tmp3);
        int len = binary.length();
        int n = (int)Math.ceil( Math.log(num) / Math.log(2)); // numn10
        int flag = 0;

        // ipn-11
        for(int i = 1; i <= n && i <= len; ++i){
            if(binary.charAt(len - i) == '1') {
                flag = 1;
                break;
            }
        }

        if(flag == 1){
            int start = (int) Math.pow(2, Math.floor(Math.log(tmp3) / Math.log(2))+1);
            if(start < num)
                start = num;
            startIP = temp1[0] + "." +  temp1[1] + "." + temp1[2] + "." + start;
        } else {
            startIP = initIP;
        }

    }

    private void caculateEndIP() {
        if(startIP.equals("0.0.0.0"))
            endIP =  "0.0.0.0";
        else {
            String[] temp = startIP.trim().split("\\.");
            int i = Integer.parseInt(temp[3]) + num - 1;
            if (i <= 255)
                endIP = temp[0] + "." + temp[1] + "." + temp[2] + "." + i;
        }
    }

    /**
     * ip
     * ip1248163264128
     */
    private String caculateMask() {
        if(num == 1)
            return "255.255.255.255";
        else if(num == 2)
            return "255.255.255.254";
        else if(num > 2 && num <= 4) {
            num = 4;
            return "255.255.255.252";
        }
        else if(num > 4 && num <= 8) {
            num = 8;
            return "255.255.255.248";
        }
        else if(num > 8 && num <= 16) {
            num = 16;
            return "255.255.255.240";
        }
        else if(num > 16 && num <= 32) {
            num = 32;
            return "255.255.255.224";
        }
        else if(num > 32 && num <= 64) {
            num = 64;
            return "255.255.255.192";
        }
        else if(num >64 && num <= 128) {
            num = 128;
            return "255.255.255.128";
        }
        else
            return "0.0.0.0";
    }

    public IPPool getIPPool() {
        return ipPool;
    }
}
