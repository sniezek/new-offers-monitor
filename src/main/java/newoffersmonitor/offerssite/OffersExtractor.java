package newoffersmonitor.offerssite;

import lombok.experimental.UtilityClass;
import newoffersmonitor.db.Offer;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

import static java.time.Instant.now;
import static java.util.stream.Collectors.toList;
import static org.apache.http.util.TextUtils.isEmpty;

@UtilityClass
public class OffersExtractor {

    public static List<Offer> extractFromFetchedPage(String configurationName,
                                                     OffersSite offersSite,
                                                     Document fetchedPage) throws OffersSiteChangeException {
        final Elements offerElements;

        try {
            offerElements = offersSite.getOfferElements(fetchedPage);
        } catch (Exception e) {
            throw new OffersSiteChangeException("Something wrong with offers on " + offersSite, e);
        }

        if (offerElements.isEmpty()) {
            throw new OffersSiteChangeException("Something wrong with offers on " + offersSite);
        }

        return offerElements
                .stream()
                .map(offerElement -> fromOfferElement(configurationName, offersSite, offerElement))
                .collect(toList());
    }

    private static Offer fromOfferElement(String configurationName,
                                          OffersSite offersSite,
                                          Element offerElement) throws OffersSiteChangeException {
        final String url;
        final String price;

        try {
            url = offersSite.getOfferUrl(offerElement);
        } catch (Exception e) {
            throw new OffersSiteChangeException("Something wrong with url on " + offersSite, e);
        }

        try {
            price = offersSite.getOfferPrice(offerElement);
        } catch (Exception e) {
            throw new OffersSiteChangeException("Something wrong with price on " + offersSite, e);
        }

        if (isEmpty(url)) {
            throw new OffersSiteChangeException("Something wrong with url on " + offersSite);
        } else if (isEmpty(price)) {
            throw new OffersSiteChangeException("Something wrong with price on " + offersSite);
        }

        return new Offer(configurationName, Offer.urlAndPrice(url, price), now().toString());
    }

}
