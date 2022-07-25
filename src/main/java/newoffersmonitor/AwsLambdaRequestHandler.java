package newoffersmonitor;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import newoffersmonitor.configurations.loader.ConfigurationsInResourcesLoader;
import newoffersmonitor.db.DynamoDbOffersRepository;
import newoffersmonitor.monitor.Monitor;
import newoffersmonitor.monitor.PageFetcher;
import newoffersmonitor.notification.GmailEmailSender;
import newoffersmonitor.offerssite.OffersSiteResolver;

import java.util.Map;

import static newoffersmonitor.configurations.loader.ConfigurationsInResourcesLoader.CONFIGURATIONS_FILE_PATH;
import static newoffersmonitor.notification.GmailEmailSender.ADMIN_EMAIL_ENV_VAR;
import static newoffersmonitor.notification.GmailEmailSender.SMTP_EMAIL_ENV_VAR;
import static newoffersmonitor.notification.GmailEmailSender.SMTP_PASSWORD_ENV_VAR;

public class AwsLambdaRequestHandler implements RequestHandler<Map<String, Object>, String> {

    public String handleRequest(Map<String, Object> input, Context context) {
        final Monitor monitor = new Monitor(
                new ConfigurationsInResourcesLoader(CONFIGURATIONS_FILE_PATH),
                new OffersSiteResolver(),
                new PageFetcher(),
                new DynamoDbOffersRepository(),
                new GmailEmailSender(
                        System.getenv(SMTP_EMAIL_ENV_VAR),
                        System.getenv(SMTP_PASSWORD_ENV_VAR),
                        System.getenv(ADMIN_EMAIL_ENV_VAR)
                )
        );
        monitor.run();

        return "Finished";
    }

}
