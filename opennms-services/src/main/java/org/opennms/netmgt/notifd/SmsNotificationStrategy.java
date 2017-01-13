package org.opennms.netmgt.notifd;

import org.opennms.core.utils.Argument;
import org.opennms.core.utils.ThreadCategory;
import org.opennms.javamail.JavaMailerException;
import org.opennms.javamail.SmsSender;
import org.opennms.netmgt.config.NotificationManager;
import org.opennms.netmgt.model.notifd.NotificationStrategy;

import java.util.List;

/**
 * Implements NotificationStragey pattern used to send notifications via the SMS.
 *
 * Created by laiguanhui on 2017/1/10.
 * @version $Id: $
 */
public class SmsNotificationStrategy implements NotificationStrategy {

    public SmsNotificationStrategy(){
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opennms.netmgt.notifd.NotificationStrategy#send(java.util.List)
     */
    /** {@inheritDoc} */
    public int send(List<Argument> arguments) {
        log().debug("In the SmsNotificationStrategy class.");
        String res = "";
        try {
            SmsSender ss = buildMessage(arguments);
            res = ss.msgSend();
        } catch (Exception e) {
            log().error("send: Error sending notification, return msg: " + res);
            return 1;
        }
        return 0;
    }

    private SmsSender buildMessage(List<Argument> arguments) throws JavaMailerException {
        SmsSender ss = new SmsSender();
        for (int i = 0; i < arguments.size(); i++) {

            Argument arg = arguments.get(i);
            log().debug("Current arg switch: " + i + " of " + arguments.size() + " is: " + arg.getSwitch());
            log().debug("Current arg  value: " + i + " of " + arguments.size() + " is: " + arg.getValue());

            /*
             * Note: The recipient gets set by whichever of the two switches:
             * (PARAM_EMAIL or PARAM_PAGER_EMAIL) are specified last in the
             * notificationCommands.xml file
             *
             * And the message body will get set to whichever is set last
             * (PARAM_NUM_MSG or PARAM_TEXT_MSG)
             */
            if (NotificationManager.PARAM_NUM_MSG.equals(arg.getSwitch())) {
                log().debug("Found: PARAM_NUM_MSG");
                ss.setMessagecontent(arg.getValue());
            } else if (NotificationManager.PARAM_TEXT_MSG.equals(arg.getSwitch())) {
                log().debug("Found: PARAM_TEXT_MSG");
                ss.setMessagecontent(arg.getValue());
            }
        }
        return ss;
    }

    private ThreadCategory log() {
        return ThreadCategory.getInstance(getClass());
    }
}
