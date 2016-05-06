package org.opennms.core.bank;

import java.util.Comparator;

/**
 * Created by laiguanhui on 2016/5/5.
 */
public class SwitcherStats {
    private String ip;
    private String name;
    private String group;
    private String flow;
    private String comment;

    public SwitcherStats(String ip, String name){
        this.ip = ip;
        this.name = name;
    }

    public SwitcherStats(String ip){
        this.ip = ip;
    }

    public static Comparator IPComparator=new Comparator(){
        @Override
        public int compare(Object arg0, Object arg1) {
            SwitcherStats ip1=(SwitcherStats)arg0;
            SwitcherStats ip2=(SwitcherStats)arg1;
            return compartTo(ip1.ip,ip2.ip);
        }
    };

    private static long[] parseIp(String ip){
        ip=ip.replace(".", "#");
        long result[]=new long[4];
        String[] ip1=ip.split("#");
        if(ip!=null){
            result[0]=Long.parseLong(ip1[0]);
            result[1]=Long.parseLong(ip1[1]);
            result[2]=Long.parseLong(ip1[2]);
            result[3]=Long.parseLong(ip1[3]);
        }
        return result;
    }

    public static int compartTo(String ip1,String ip2){
        //以下方法不能判断的原因在于：例如10.4.120.5与10.50.0.0按理应该前者小，但将它们转化为数字组合后，后者位数少，所以反而变成后面一个数字更小。
//      String ip11=ip1.replace(".","");
//      String ip22=ip2.replace(".", "");
//      return new Long(ip11).compareTo(new Long(ip22));
        //比较2个IP的顺序，按照数字顺序
        long[] ip11=parseIp(ip1);
        long[] ip22=parseIp(ip2);
        long ip1Result=0,ip2Result=0;
        for(int i=0;i<4;i++){
            ip1Result+=(ip11[i]<<(24-i*8));
        }
        for(int i=0;i<4;i++){
            ip2Result+=(ip22[i]<<(24-i*8));
        }
        if(ip1Result-ip2Result>0){
            return 1;
        }else if(ip1Result-ip2Result<0){
            return -1;
        }else{
            return 0;
        }
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String toInsertValue() {
        String value = "";
        String[] colsString = {ip, name, group, flow, comment};

        for (String col: colsString) {
            if(col == null)
                value += "'', ";
            else
                value += "'" + col + "', ";
        }
        return value.substring(0, value.length()-2);
    }
}
