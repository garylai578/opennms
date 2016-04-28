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
        String[] temp1 = initIP.trim().split("\\.");
        int tmp3 = Integer.parseInt(temp1[3]);
        if(tmp3 + num > 255) {
/*            int tmp1 = Integer.parseInt(temp1[1]);
            int tmp2 = Integer.parseInt(temp1[2]) + 1;
            if(tmp2 > 255)
                tmp1++;
            startIP = temp1[0] + "." +  tmp1 + "." + tmp2 + ".0";*/
            return 0;
        }

        String binary = Integer.toBinaryString(tmp3);
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
            int start = (int) Math.pow(2, Math.floor(Math.log(tmp3) / Math.log(2))+1);

            if(start >= 256) {
                /*int tmp2 = Integer.parseInt(temp1[2])+1;
                startIP = temp1[0] + "." +  temp1[1] + "." + tmp2 + ".0";*/
                return 0;
            }else {
                if(start < num)
                    start = num;
                startIP = temp1[0] + "." +  temp1[1] + "." + temp1[2] + "." + start;
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

    public IPPool getIPPool() {
        return ipPool;
    }
}
