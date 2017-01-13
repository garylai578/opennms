package org.opennms.javamail;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.opennms.core.utils.PropertiesUtils;
import org.opennms.core.utils.ThreadCategory;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Properties;

/** 农行的短信发送类，通过调用WebService接口完成，内容如下：
 *  WebService的地址：http://xx.xx.xx.xx:yyyy/sendmsg.asmx，
 *  接口中的Send方法参数
    [SISMSID] [varchar](50) NOT NULL,     调用系统字母简称
    [EXTCODE] [varchar](21) NULL,        调用系统全称
    [DESTADDR] [varchar](200) NOT NULL,  手机号码
    [MESSAGECONTENT] [varchar](2000) NULL, 发送内容
 *  上述地址和参数在javamail-configuration.properties文件中配置
 * Created by laiguanhui on 2017/1/10.
 */
public class SmsSender {

    private String endPoint;    //待调用的web service地址
    private String soapUri;     //待调用的soap uri
    private String optName = "Send";    // 调用到方法名
    //4个待调用的参数名称，主要要和net接口中的名称一致，类型尽量用String
    private static final String argNames[]={"SISMSID", "EXTCODE", "DESTADDR", "MESSAGECONTENT"};
    private String sismsid, extcode, destaddr, messagecontent;
    /*
     * properties from configuration
     */
    private Properties properties;

    public void setEndPoint(String endPoint) { this.endPoint = endPoint; }

    public void setSoapUri(String soapUri) { this.soapUri = soapUri; }

    public void setSismsid(String sismsid) { this.sismsid = sismsid; }

    public void setExtcode(String extcode) { this.extcode = extcode; }

    public void setDestaddr(String destaddr) { this.destaddr = destaddr; }

    public void setMessagecontent(String messagecontent) { this.messagecontent = messagecontent; }

    public SmsSender() throws JavaMailerException {
        this(new Properties());
    }

    public SmsSender(Properties smsSenderProps) throws JavaMailerException {
        try {
            configureProperties(smsSenderProps);
        } catch (IOException e) {
            throw new JavaMailerException("Failed to construct SmsSender", e);
        }
    }

    public String msgSend() throws Exception{
        log().debug("SMS sending... The targer endpoint addr is: " + endPoint + ".\n The Soap action uri is: " + soapUri +
                ".\n The sismsid is: " + sismsid + ".\n The extcode is: " + extcode + ".\n The destaddr is: " + destaddr +
                ".\n The message is: " + messagecontent);
        Call call;
        String res = "";
        Service service = new Service();
        try {
            call = (Call)service.createCall();
            //设置.net的webservice地址
            call.setTargetEndpointAddress(new java.net.URL(endPoint));
            //这里注意有方法名，引用的地址从net接口中找
            call.setUseSOAPAction(true);
            call.setSOAPActionURI(soapUri + "/" + optName);
            call.setOperationName(new QName(soapUri + "/", optName));  //发布的方法名
            //设置参数
            for(int i = 0; i < argNames.length; ++i)
                call.addParameter(new QName(soapUri + "/", argNames[i]), org.apache.axis.encoding.XMLType.XSD_STRING, ParameterMode.IN);

            call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);    //设置返回值的类型
            res =(String)call.invoke(new Object[]{sismsid, extcode, destaddr, messagecontent});
        } catch (ServiceException e) {
            log().error("SMS ServiceException" + e, e);
            e.printStackTrace();
        } catch (MalformedURLException e) {
            log().error("SMS MalformedURLException" + e, e);
            e.printStackTrace();
        } catch (RemoteException e) {
            log().error("SMS RemoteException" + e, e);
            e.printStackTrace();
        }catch (Exception e) {
            log().error("SMS Exception" + e, e);
            e.printStackTrace();
        }
        finally {
        }
        return res;
    }

    /**
     * 用于获取配置文件信息，和javamail共用一个配置文件，即etc/javamail-configuration.properties。
     *
     * @throws IOException
     */
    private void configureProperties(Properties javamailProps) throws IOException {
        //this loads the opennms defined properties
        properties = JavaMailerConfig.getProperties();

        //this sets any javamail defined properties sent in to the constructor
        properties.putAll(javamailProps);

        /*
         * fields from properties used for deterministic behavior of the mailer
         */
        sismsid = PropertiesUtils.getProperty(properties, "org.opennms.core.utils.sismsid", "WGPT");
        extcode = PropertiesUtils.getProperty(properties, "org.opennms.core.utils.extcode", "东莞分行网管平台");
        destaddr = PropertiesUtils.getProperty(properties, "org.opennms.core.utils.destaddr", "");
        endPoint = PropertiesUtils.getProperty(properties, "org.opennms.core.utils.webservice", "http://127.0.0.0");
        soapUri = PropertiesUtils.getProperty(properties, "org.opennms.core.utils.soapuri", "http://tempuri.org");
    }

    private ThreadCategory log() {
        return ThreadCategory.getInstance(getClass());
    }
}
