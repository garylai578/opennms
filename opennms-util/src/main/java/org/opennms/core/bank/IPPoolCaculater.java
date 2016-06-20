package org.opennms.core.bank;

/**
 * ipipipipip0.0.0.0
 * getIPPool()
 *
 * Created by laiguanhui on 2016/2/4.
 */
public class IPPoolCaculater {

    private String initIP, stopIP;
    private String startIP = "0.0.0.0";
    private String endIP = "0.0.0.0";
    private String mask;
    private int num;
    private IPPool ipPool;

    /**
     *
     * @param initIP: caculate from the initIP to stopIP
     * @param num: the numbers that needed
     */
    public IPPoolCaculater(String initIP, String stopIP, int num){
        this.initIP = initIP;
        this.stopIP = stopIP;
        this.num = num;
        ipPool = new IPPool();
    }

    /**
     *
     * @param initIP: caculate from the initIP to the end the seg
     * @param num: the numbers that needed
     */
    public IPPoolCaculater(String initIP, int num){
        this.initIP = initIP;
        int[] iniIpSegs = getIpSegs(initIP);
        this.stopIP = iniIpSegs[0] + "." + iniIpSegs[1] + "." + iniIpSegs[2] + ".255";
        this.num = num;
        ipPool = new IPPool();
    }

    /** 进行计算
     * @return 1:计算成功；0：该地址段剩余ip数量不够分配； -1：分配失败
     */
    public int caculate() {
        mask = caculateMask();
        ipPool.setNetMask(mask);
        int result = caculateStartIP();
        if( result!= 1)
            return result;
        result = caculateEndIP();
        if(result != 1)
            return result;
        ipPool.setStartIP(startIP);
        ipPool.setEndIP(endIP);
        return 1;
    }

    /**
     * 计算开始ip
     *
     * @return 1:计算成功；0：该地址段剩余ip数量不够分配；-1：分配失败
     */
    private int caculateStartIP() {
        int[] initIpSegs = getIpSegs(initIP);
        int[] stopIpSegs = getIpSegs(stopIP);

        // 先判断本IP段内所剩IP数量是否足够分配,如果不够分配则判断是否有下一段
        if((initIpSegs[2] != stopIpSegs[2] && initIpSegs[3] + num > 255)
                || (initIpSegs[2] == stopIpSegs[2] && initIpSegs[3] + num > stopIpSegs[3])) {
            if(initIpSegs[2] >= stopIpSegs[2]) {
                if(initIpSegs[1] >= stopIpSegs[1])
                    return 0;
                else if( initIpSegs[2] + 1 <= 255)
                    startIP = initIpSegs[0] + "." + initIpSegs[1] + "." + (initIpSegs[2] + 1) + ".0";
                else
                    startIP = initIpSegs[0] + "." + (initIpSegs[1] + 1) + ".0.0";
            }else{
                startIP = initIpSegs[0] + "." + initIpSegs[1] + "." + (initIpSegs[2] + 1) + ".0";
            }
            return 1;
        }

        String binary = Integer.toBinaryString(initIpSegs[3]);
        int len = binary.length();
        int n = (int)Math.ceil( Math.log(num) / Math.log(2)); // 2的n次方大于等于num
        int flag = 0;

        for(int i = 1; i <= n && i <= len; ++i){
            if(binary.charAt(len - i) == '1') {
                flag = 1;
                break;
            }
        }

        if(flag == 1){
            //这里分配ip的时候，只是对单个位置进行考虑（例如第3位是1），没有考虑多个位置（例如第7和第6位同时为1）的情况，所以中间会存在很多空洞
            int start = (int) Math.pow(2, Math.floor(Math.log(initIpSegs[3]) / Math.log(2))+1);

            if(start >= 256) {
                if(initIpSegs[2] + 1 <= stopIpSegs[2]) {
                    startIP = initIpSegs[0] + "." + initIpSegs[1] + "." + (initIpSegs[2] + 1) + ".0";
                    return 1;
                }
                else
                    return 0;
            }else {
                if(start < num)
                    start = num;
                startIP = initIpSegs[0] + "." +  initIpSegs[1] + "." + initIpSegs[2] + "." + start;
            }
        } else {
            startIP = initIP;
        }
        return 1;
    }

    /**
     * 计算结束ip
     *
     * @return 1:计算成功；0：该地址段剩余ip数量不够分配；-1：分配失败
     */
    private int caculateEndIP() {
        String[] temp = startIP.trim().split("\\.");
        int i = Integer.parseInt(temp[3]) + num - 1;
        if (i <= 255) {
            endIP = temp[0] + "." + temp[1] + "." + temp[2] + "." + i;
            return 1;
        }else
            return 0;
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

    /**
     * 根据给定的ip返回各个点分十进制的int值
     * @param ip
     * @return
     */
    private int[] getIpSegs(String ip){
        String[] ipSegs = ip.trim().split("\\.");
        int[] result = new int[4];
        result[0] = Integer.parseInt(ipSegs[0]);
        result[1] = Integer.parseInt(ipSegs[1]);
        result[2] = Integer.parseInt(ipSegs[2]);
        result[3] = Integer.parseInt(ipSegs[3]);
        return result;
    }

    public IPPool getIPPool() {
        return ipPool;
    }
}
