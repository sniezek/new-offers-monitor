package newoffersmonitor.notification;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import newoffersmonitor.configurations.Configuration;
import newoffersmonitor.db.Offer;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

@Slf4j
@AllArgsConstructor
public class GmailEmailSender implements NotificationSender {

    public static final String SMTP_EMAIL_ENV_VAR = "SMTP_EMAIL_ENV_VAR";

    public static final String SMTP_PASSWORD_ENV_VAR = "SMTP_PASSWORD_ENV_VAR";

    public static final String ADMIN_EMAIL_ENV_VAR = "ADMIN_EMAIL_ENV_VAR";

    private static final String NEW_OFFER_EMAIL_SUBJECT = "New offer";

    private static final String NEW_SUPER_OFFER_EMAIL_SUBJECT = "New super offer";

    private final String smtpEmail;

    private final String smtpPassword;

    private final String adminEmail;

    private final Set<String> alreadySentWarningEmails = new HashSet<>();

    @Override
    public void sendOfferNotification(Offer offer,
                                      Configuration configuration,
                                      boolean isSuper) throws Exception {
        final String subject = isSuper ? NEW_SUPER_OFFER_EMAIL_SUBJECT : NEW_OFFER_EMAIL_SUBJECT;
        sendEmail(subject, recipients(configuration), content(offer));
    }

    @Override
    public void sendWarningNotificationToAdmin(String warningMessage) {
        if (alreadySentWarningEmails.contains(warningMessage)) {
            return;
        }

        try {
            sendEmail(warningMessage, adminEmail, warningMessage);
            alreadySentWarningEmails.add(warningMessage);
        } catch (Exception e) {
            log.error("Couldn't send a warning email to admin", e);
        }
    }

    private void sendEmail(String subject,
                           String recipients,
                           String content) throws Exception {
        final Session session = getSession();

        final Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(smtpEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
        message.setSubject(subject);
        message.setText(content);

        Transport.send(message);
    }

    private Session getSession() {
        return Session.getInstance(getProperties(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpEmail, smtpPassword);
            }
        });
    }

    private Properties getProperties() {
        final Properties properties = System.getProperties();

        properties.setProperty("mail.smtp.host", "smtp.gmail.com");
        properties.setProperty("mail.smtp.ssl.enable", "true");
        properties.setProperty("mail.smtp.auth", "true");

        return properties;
    }

    private String recipients(Configuration configuration) {
        return String.join(",", configuration.getEmails());
    }

    private String content(Offer offer) {
        return String.format("%s\n%s", offer.getUrl(), offer.getPrice());
    }

}
