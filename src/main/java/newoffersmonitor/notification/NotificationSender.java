package newoffersmonitor.notification;

import newoffersmonitor.configurations.Configuration;
import newoffersmonitor.db.Offer;

import java.util.Set;

public interface NotificationSender {

    Set<String> IGNORED_ERRORS = Set.of("HTTP error fetching URL. Status=502");

    void sendOfferNotification(Offer offer, Configuration configuration, boolean isSuper) throws Exception;

    void sendWarningNotificationToAdmin(String content);

}
