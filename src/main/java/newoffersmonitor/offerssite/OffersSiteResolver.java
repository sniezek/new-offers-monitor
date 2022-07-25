package newoffersmonitor.offerssite;

import newoffersmonitor.configurations.Page;

import java.util.Arrays;
import java.util.List;

public class OffersSiteResolver {

    private static final List<String> URL_PREFIXES = List.of("https://www.", "https://", "http://www.", "http://");

    public OffersSite resolve(Page page) {
        final String pageUrl = page.getUrl();
        final String pageUrlPrefix = pageUrlPrefix(pageUrl);
        final String pageUrlWithoutPrefix = pageUrl.substring(pageUrl.indexOf(pageUrlPrefix) + pageUrlPrefix.length());
        final String pageName = pageUrlWithoutPrefix.substring(0, pageUrlWithoutPrefix.indexOf('.'));

        return Arrays
                .stream(OffersSite.values())
                .filter(offersSite -> offersSite.name().replace("_", "-").equalsIgnoreCase(pageName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unrecognized OffersSite from url " + pageUrl));
    }

    private static String pageUrlPrefix(String pageUrl) {
        return URL_PREFIXES
                .stream()
                .filter(pageUrl::startsWith)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid url " + pageUrl));
    }

}
