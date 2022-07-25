package newoffersmonitor.offerssite;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public interface OffersSiteInterface {

    Elements getOfferElements(Document fetchedPage);

    String getOfferUrl(Element offer);

    String getOfferPrice(Element offer);

}
