package newoffersmonitor.monitor;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import newoffersmonitor.configurations.Configuration;
import newoffersmonitor.configurations.Configurations;
import newoffersmonitor.configurations.Page;
import newoffersmonitor.configurations.loader.ConfigurationsLoader;
import newoffersmonitor.db.Offer;
import newoffersmonitor.db.OffersRepository;
import newoffersmonitor.notification.NotificationSender;
import newoffersmonitor.offerssite.OffersExtractor;
import newoffersmonitor.offerssite.OffersSite;
import newoffersmonitor.offerssite.OffersSiteResolver;
import org.jsoup.nodes.Document;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static newoffersmonitor.notification.NotificationSender.IGNORED_ERRORS;

@Slf4j
@AllArgsConstructor
public class Monitor {

    private final ConfigurationsLoader configurationsLoader;

    private final OffersSiteResolver offersSiteResolver;

    private final PageFetcher pageFetcher;

    private final OffersRepository offersRepository;

    private final NotificationSender notificationSender;

    public void run() {
        final Configurations configurations;

        try {
            configurations = configurationsLoader.loadConfigurations();
        } catch (Exception e) {
            log.error("Couldn't load configurations", e);
            notificationSender.sendWarningNotificationToAdmin("Couldn't load configurations");
            return;
        }

        for (final Configuration configuration : configurations.getActiveConfigurations()) {
            log.info("Processing {} configuration", configuration.getName());

            for (final Page page : configuration.getActivePages()) {
                try {
                    processPage(configuration, page);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    Optional.ofNullable(e.getMessage())
                            .filter(message -> IGNORED_ERRORS.stream().noneMatch(message::startsWith))
                            .ifPresent(notificationSender::sendWarningNotificationToAdmin);
                }
            }
        }

        log.info("Finished processing all configurations");
    }

    private void processPage(Configuration configuration, Page page) throws Exception {
        final OffersSite offersSite = offersSiteResolver.resolve(page);
        final List<Offer> fetchedOffers = fetchOffers(page, offersSite, configuration);
        final List<Offer> newOffers = newOffers(fetchedOffers);
        log.info("{} new offer(s) from {} for configuration {}", newOffers.size(), offersSite, configuration.getName());

        if (configuration.hasEmailsDefined()) {
            for (final Offer offer : newOffers) {
                sendOfferEmail(offer, offersSite, configuration);
            }
        }

        offersRepository.save(newOffers);
    }

    private List<Offer> fetchOffers(Page page,
                                    OffersSite offersSite,
                                    Configuration configuration) throws Exception {
        final Document fetchedPage = pageFetcher.fetchPage(page);

        return OffersExtractor.extractFromFetchedPage(configuration.getName(), offersSite, fetchedPage);
    }

    private List<Offer> newOffers(List<Offer> fetchedOffers) {
        return fetchedOffers
                .stream()
                .filter(offer -> offersRepository.getOffer(offer).isEmpty())
                .distinct()
                .collect(toList());
    }

    private void sendOfferEmail(Offer offer,
                                OffersSite offersSite,
                                Configuration configuration) throws Exception {
        final String offerUrlLowerCase = offer.getUrl().toLowerCase();

        if (offerUrlHasNoIgnoredKeywords(offerUrlLowerCase, offersSite, configuration)) {
            notificationSender.sendOfferNotification(offer, configuration, offerUrlContainsSuperKeyword(offerUrlLowerCase, configuration));
        }
    }

    private boolean offerUrlHasNoIgnoredKeywords(String offerUrlLowerCase,
                                                 OffersSite offersSite,
                                                 Configuration configuration) {
        for (final String ignoredKeyword : configuration.getIgnoredKeywords()) {
            if (offerUrlLowerCase.contains(ignoredKeyword.toLowerCase())) {
                log.info("Ignoring offer containing '{}' keyword from {} for configuration {}", ignoredKeyword, offersSite, configuration.getName());
                return false;
            }
        }

        return true;
    }

    private boolean offerUrlContainsSuperKeyword(String offerUrlLowerCase, Configuration configuration) {
        return configuration
                .getSuperKeywords()
                .stream()
                .anyMatch(superKeyword -> offerUrlLowerCase.contains(superKeyword.toLowerCase()));
    }

}
