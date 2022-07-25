package newoffersmonitor;

import newoffersmonitor.configurations.loader.ConfigurationsInResourcesLoader;
import newoffersmonitor.db.LocalFileOffersRepository;
import newoffersmonitor.monitor.Monitor;
import newoffersmonitor.monitor.PageFetcher;
import newoffersmonitor.notification.GmailEmailSender;
import newoffersmonitor.offerssite.OffersSiteResolver;

import static newoffersmonitor.configurations.loader.ConfigurationsInResourcesLoader.CONFIGURATIONS_FILE_PATH;
import static newoffersmonitor.db.LocalFileOffersRepository.OFFERS_FILE_PATH;
import static newoffersmonitor.notification.GmailEmailSender.ADMIN_EMAIL_ENV_VAR;
import static newoffersmonitor.notification.GmailEmailSender.SMTP_EMAIL_ENV_VAR;
import static newoffersmonitor.notification.GmailEmailSender.SMTP_PASSWORD_ENV_VAR;

public class LocalUseMain {

    private static final int RUN_MONITOR_EVERY_MILLIS = 180000;

    public static void main(String[] args) throws Exception {
        final Monitor monitor = new Monitor(
                new ConfigurationsInResourcesLoader(CONFIGURATIONS_FILE_PATH),
                new OffersSiteResolver(),
                new PageFetcher(),
                new LocalFileOffersRepository(OFFERS_FILE_PATH),
                new GmailEmailSender(
                        System.getenv(SMTP_EMAIL_ENV_VAR),
                        System.getenv(SMTP_PASSWORD_ENV_VAR),
                        System.getenv(ADMIN_EMAIL_ENV_VAR)
                )
        );

        while (true) {
            monitor.run();
            Thread.sleep(RUN_MONITOR_EVERY_MILLIS);
        }
    }

}
