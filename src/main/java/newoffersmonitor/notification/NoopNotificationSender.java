package newoffersmonitor.notification;

import newoffersmonitor.configurations.Configuration;
import newoffersmonitor.db.Offer;

public class NoopNotificationSender implements NotificationSender {

    @Override
    public void sendOfferNotification(Offer offer, Configuration configuration, boolean isSuper) throws Exception {
        // do nothing
    }

    @Override
    public void sendWarningNotificationToAdmin(String content) {
        // do nothing
    }

}
