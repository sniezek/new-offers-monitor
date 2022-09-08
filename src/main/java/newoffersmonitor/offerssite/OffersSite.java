package newoffersmonitor.offerssite;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public enum OffersSite implements OffersSiteInterface {

    OLX {
        @Override
        public Elements getOfferElements(Document fetchedPage) {
            return fetchedPage.getElementsByClass("offer-wrapper");
        }

        @Override
        public String getOfferUrl(Element offer) {
            return offer
                    .getElementsByClass("title-cell")
                    .first()
                    .getElementsByTag("a")
                    .first()
                    .attr("href")
                    .replace(";promoted", "");
        }

        @Override
        public String getOfferPrice(Element offer) {
            return Optional
                    .ofNullable(offer
                            .getElementsByClass("price")
                            .first())
                    .map(priceElement -> priceElement
                            .getElementsByTag("strong")
                            .first()
                            .text())
                    .orElse(NOPE);
        }
    },

    OTODOM {
        @Override
        public Elements getOfferElements(Document fetchedPage) {
            return fetchedPage.getElementsByClass("es62z2j19");
        }

        @Override
        public String getOfferUrl(Element offer) {
            return "https://www.otodom.pl" + offer
                    .getElementsByTag("a")
                    .first()
                    .attr("href");
        }

        @Override
        public String getOfferPrice(Element offer) {
            return offer
                    .getElementsByTag("a")
                    .first()
                    .getElementsByTag("article")
                    .first()
                    .getElementsByClass("eclomwz2")
                    .first()
                    .text();
        }
    },

    ADRESOWO {
        @Override
        public Elements getOfferElements(Document fetchedPage) {
            return new Elements(fetchedPage.getElementsByClass("search-results__item"));
        }

        @Override
        public String getOfferUrl(Element offer) {
            final Element element = offer
                    .getElementsByTag("a")
                    .first();

            if (element == null) {
                return NOPE;
            }

            final String url = element.attr("href");
            final Element address = offer
                    .getElementsByClass("result-info__header")
                    .first();

            if (address == null) {
                return NOPE;
            }

            final String place = address
                    .getElementsByTag("strong")
                    .first()
                    .text();

            final String street = address
                    .getElementsByTag("span")
                    .first()
                    .text();

            return place + " " + street + " https://adresowo.pl" + url;
        }

        @Override
        public String getOfferPrice(Element offer) {
            final Element element = offer
                    .getElementsByClass("result-info__price")
                    .first();

            if (element == null) {
                return NOPE;
            }

            return element
                    .getElementsByTag("span")
                    .first()
                    .text();
        }
    },

    MORIZON {
        @Override
        public Elements getOfferElements(Document fetchedPage) {
            final Elements developments = fetchedPage
                    .getElementsByClass("developments")
                    .first()
                    .getElementsByClass("row--property-list");

            return new Elements(fetchedPage
                    .getElementsByClass("row--property-list")
                    .stream()
                    .filter(element -> !developments.contains(element))
                    .collect(toList()));
        }

        @Override
        public String getOfferUrl(Element offer) {
            return offer
                    .getElementsByTag("a")
                    .first()
                    .attr("href");
        }

        @Override
        public String getOfferPrice(Element offer) {
            if (getOfferUrl(offer).contains("campaign")) {
                return NOPE;
            }

            return offer
                    .getElementsByClass("single-result__price")
                    .first()
                    .text();
        }
    },

    DOMIPORTA {
        @Override
        public Elements getOfferElements(Document fetchedPage) {
            return fetchedPage.getElementsByClass("grid-item--cover");
        }

        @Override
        public String getOfferUrl(Element offer) {
            return "https://www.domiporta.pl" + offer
                    .getElementsByTag("a")
                    .attr("href");
        }

        @Override
        public String getOfferPrice(Element offer) {
            return offer
                    .getElementsByClass("sneakpeak__price")
                    .first()
                    .text();
        }
    },

    NIERUCHOMOSCI_ONLINE {
        @Override
        public Elements getOfferElements(Document fetchedPage) {
            final Elements investments = fetchedPage.getElementsByClass("tile-investment");

            return new Elements(fetchedPage
                    .getElementsByClass("tile-tile")
                    .stream()
                    .filter(element -> !investments.contains(element))
                    .collect(toList()));
        }

        @Override
        public String getOfferUrl(Element offer) {
            final Element urlElement = offer
                    .getElementsByTag("h2")
                    .first();

            if (urlElement == null) {
                return NOPE;
            }

            final String url = urlElement
                    .getElementsByTag("a")
                    .first()
                    .attr("href");

            final Element province = offer
                    .getElementsByClass("province")
                    .first();

            if (province == null) {
                return url;
            }

            final Element provinceA = province
                    .getElementsByTag("a")
                    .first();

            if (provinceA == null) {
                return url;
            }

            return provinceA.text() + " " + url;
        }

        @Override
        public String getOfferPrice(Element offer) {
            final Element priceElement = offer
                    .getElementsByClass("primary-display")
                    .first();

            if (priceElement == null) {
                return NOPE;
            }

            return priceElement
                    .getElementsByTag("span")
                    .get(1)
                    .text();
        }
    },

    DOMINIUM {
        @Override
        public Elements getOfferElements(Document fetchedPage) {
            return fetchedPage.getElementsByClass("list-element");
        }

        @Override
        public String getOfferUrl(Element offer) {
            return offer
                    .getElementsByTag("a")
                    .first()
                    .attr("href");
        }

        @Override
        public String getOfferPrice(Element offer) {
            return offer
                    .getElementsByClass("biggest-font")
                    .get(2)
                    .text();
        }
    },

    SZYBKO {
        @Override
        public Elements getOfferElements(Document fetchedPage) {
            return fetchedPage.getElementsByClass("listing-item");
        }

        @Override
        public String getOfferUrl(Element offer) {
            return "https://www.szybko.pl" + offer
                    .getElementsByClass("listing-title-heading")
                    .first()
                    .attr("href");
        }

        @Override
        public String getOfferPrice(Element offer) {
            return offer
                    .getElementsByClass("listing-price")
                    .first()
                    .text();
        }
    },

    SPRZEDAJEMY {
        @Override
        public Elements getOfferElements(Document fetchedPage) {
            return fetchedPage
                    .getElementsByClass("element");
        }

        @Override
        public String getOfferUrl(Element offer) {
            final Element element = offer.getElementsByClass("offerLink").first();

            if (element == null) {
                return NOPE;
            }

            return "https://sprzedajemy.pl" + element.attr("href");
        }

        @Override
        public String getOfferPrice(Element offer) {
            final Element priceElement = offer
                    .getElementsByClass("price")
                    .first();

            if (priceElement == null) {
                return NOPE;
            }

            return priceElement.text();
        }
    },

    GRATKA {
        @Override
        public Elements getOfferElements(Document fetchedPage) {
            final List<Element> elements = fetchedPage
                    .getElementsByClass("listing__teaserWrapper")
                    .stream()
                    .filter(element -> !"Zapytaj o cenę".equals(this.getOfferPrice(element)))
                    .collect(toList());

            return new Elements(elements);
        }

        @Override
        public String getOfferUrl(Element offer) {
            return offer
                    .getElementsByTag("a")
                    .first()
                    .attr("href");
        }

        @Override
        public String getOfferPrice(Element offer) {
            return offer
                    .getElementsByClass("teaserUnified__price")
                    .first()
                    .text();
        }
    },

    DOMY {
        @Override
        public Elements getOfferElements(Document fetchedPage) {
            return fetchedPage.getElementsByClass("headerTextBox");
        }

        @Override
        public String getOfferUrl(Element offer) {
            return offer
                    .getElementsByTag("a")
                    .first()
                    .attr("href");
        }

        @Override
        public String getOfferPrice(Element offer) {
            return offer
                    .getElementsByClass("price")
                    .first()
                    .text();
        }
    },

    OTOMOTO {
        @Override
        public Elements getOfferElements(Document fetchedPage) {
            return fetchedPage.getElementsByClass("e1b25f6f18");
        }

        @Override
        public String getOfferUrl(Element offer) {
            return offer.getElementsByTag("a").first().attr("href");
        }

        @Override
        public String getOfferPrice(Element offer) {
            return offer
                    .getElementsByClass("e1b25f6f8")
                    .first()
                    .text();
        }
    };

    private static final String NOPE = "nope"; // a dummy value used when sometimes there is no price or url in an offer

}
