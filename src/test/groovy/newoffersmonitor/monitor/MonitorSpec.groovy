package newoffersmonitor.monitor

import newoffersmonitor.configurations.Configuration
import newoffersmonitor.configurations.Page
import newoffersmonitor.configurations.loader.ConfigurationsInResourcesLoader
import newoffersmonitor.db.InMemoryOffersRepository
import newoffersmonitor.db.Offer
import newoffersmonitor.notification.NotificationSender
import newoffersmonitor.offerssite.OffersSite
import newoffersmonitor.offerssite.OffersSiteResolver
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import spock.lang.Specification

import java.time.Instant

import static newoffersmonitor.db.Offer.urlAndPrice
import static newoffersmonitor.notification.NotificationSender.IGNORED_ERRORS

class MonitorSpec extends Specification {

    def config1Name = "OlafFlatsTo375k"

    def config2Name = "OlafW204Coupe"

    def olxPage = new Page("https://www.olx.pl/nieruchomosci/mieszkania/sprzedaz/krakow/?search%5Bfilter_float_price%3Ato%5D=375000", true)

    def otodomPage = new Page("https://www.otodom.pl/pl/oferty/sprzedaz/mieszkanie/krakow?priceMax=375000&by=LATEST&direction=DESC", true)

    def otomotoPage = new Page("https://www.otomoto.pl/osobowe/mercedes-benz/c-klasa/seg-coupe/od-2011?search%5Bfilter_enum_generation%5D=gen-w204-2007-2014&search%5Bfilter_enum_fuel_type%5D=petrol&search%5Bfilter_enum_gearbox%5D=automatic&search%5Bfilter_enum_damaged%5D=0&search%5Border%5D=created_at_first%3Adesc&search%5Badvanced_search_expanded%5D=true", true)

    def fetchedOlxPage = Stub(Document)

    def fetchedOtodomPage = Stub(Document)

    def fetchedOtomotoPage = Stub(Document)

    def pageFetcher = Stub(PageFetcher) {
        fetchPage(olxPage) >> fetchedOlxPage
        fetchPage(otodomPage) >> fetchedOtodomPage
        fetchPage(otomotoPage) >> fetchedOtomotoPage
    }

    def olxElement1 = Stub(Element)
    def olxElement1Url = "https://www.olx.pl/d/oferta/kawalerka-29-m2-zlocien-DIC3-ABC62t9.html#6debe4385c"
    def olxElement1Price = "259 000 zł"
    def olxElement2 = Stub(Element)
    def olxElement2Url = "https://www.olx.pl/d/oferta/przestronne-dwa-pokoje-na-parterze-w-prokocimiu-DIC3-I1PXUXN.html#6faae4385c"
    def olxElement2Price = "359 000 zł"
    def olxOffersSite = Stub(OffersSite) {
        getOfferElements(fetchedOlxPage) >> new Elements([olxElement1, olxElement2])
        getOfferUrl(olxElement1) >> olxElement1Url
        getOfferPrice(olxElement1) >> olxElement1Price
        getOfferUrl(olxElement2) >> olxElement2Url
        getOfferPrice(olxElement2) >> olxElement2Price
    }

    def otodomElement = Stub(Element)
    def otodomElementUrl = "https://www.otodom.pl/pl/oferta/kawalerka-20-78-m2-krowodrza-BX9ga4A.html?"
    def otodomElementPrice = "310 000 zł"
    def otodomOffersSite = Stub(OffersSite) {
        getOfferElements(fetchedOtodomPage) >> new Elements([otodomElement])
        getOfferUrl(otodomElement) >> otodomElementUrl
        getOfferPrice(otodomElement) >> otodomElementPrice
    }

    def otomotoElement = Stub(Element)
    def otomotoElementUrl = "https://www.otomoto.pl/oferta/mercedes-benz-klasa-c-c250-7g-tronic-harman-bixenon-skretny-nowy-rozrzad-oryg-lakier-IXE2xFqw.html"
    def otomotoElementPrice = "64 900 PLN"
    def otomotoOffersSite = Stub(OffersSite) {
        getOfferElements(fetchedOtomotoPage) >> new Elements([otomotoElement])
        getOfferUrl(otomotoElement) >> otomotoElementUrl
        getOfferPrice(otomotoElement) >> otomotoElementPrice
    }

    def offersSiteResolver = Stub(OffersSiteResolver) {
        resolve(olxPage) >> olxOffersSite
        resolve(otodomPage) >> otodomOffersSite
        resolve(otomotoPage) >> otomotoOffersSite
    }

    def offersRepository = new InMemoryOffersRepository()

    def notificationSender = Mock(NotificationSender)

    def monitor = new Monitor(
            new ConfigurationsInResourcesLoader("test_configurations/test_configurations.json"),
            offersSiteResolver,
            pageFetcher,
            offersRepository,
            notificationSender
    )

    def "monitor works as expected"() {
        given:
        def now = Instant.now()

        when:
        monitor.run()

        then:
        2 * notificationSender.sendOfferNotification(_ as Offer, _ as Configuration, false);
        1 * notificationSender.sendOfferNotification(_ as Offer, _ as Configuration, true);

        and:
        offersRepository.count() == 4

        and:
        def olxOffer1 = offersRepository.getOffer(
                new Offer(
                        config1Name,
                        urlAndPrice(olxElement1Url, olxElement1Price),
                        null
                )
        )
        olxOffer1.isPresent()
        validateAddedAt(olxOffer1, now)

        and:
        def olxOffer2 = offersRepository.getOffer(
                new Offer(
                        config1Name,
                        urlAndPrice(olxElement2Url, olxElement2Price),
                        null
                )
        )
        olxOffer2.isPresent()
        validateAddedAt(olxOffer2, now)

        and:
        def otodomOffer = offersRepository.getOffer(
                new Offer(
                        config1Name,
                        urlAndPrice(otodomElementUrl, otodomElementPrice),
                        null
                )
        )
        otodomOffer.isPresent()
        validateAddedAt(otodomOffer, now)

        and:
        def otomotoOffer = offersRepository.getOffer(
                new Offer(
                        config2Name,
                        urlAndPrice(otomotoElementUrl, otomotoElementPrice),
                        null
                )
        )
        otomotoOffer.isPresent()
        validateAddedAt(otomotoOffer, now)

        when: 'new offer on Otodom and OLX site changed'
        def newOtodomElement = Stub(Element)
        def newOtodomElementUrl = "https://www.otodom.pl/pl/oferta/mieszkanie-z-tarasem-m-postojowe-8-vat-w-cenie-ID4dAQWC.html?"
        def newOtodomElementPrice = "298 500 zł"
        otodomOffersSite.getOfferElements(fetchedOtodomPage) >> new Elements([newOtodomElement, otodomElement])
        otodomOffersSite.getOfferUrl(newOtodomElement) >> newOtodomElementUrl
        otodomOffersSite.getOfferPrice(newOtodomElement) >> newOtodomElementPrice
        olxOffersSite.getOfferElements(fetchedOlxPage) >> { throw new RuntimeException("Some error") }
        monitor.run()

        then:
        1 * notificationSender.sendOfferNotification(_ as Offer, _ as Configuration, false)
        0 * notificationSender.sendOfferNotification(_ as Offer, _ as Configuration, true)
        1 * notificationSender.sendWarningNotificationToAdmin(_)

        and:
        offersRepository.count() == 5

        and:
        def newOtodomOffer = offersRepository.getOffer(
                new Offer(
                        config1Name,
                        urlAndPrice(newOtodomElementUrl, newOtodomElementPrice),
                        null
                )
        )
        newOtodomOffer.isPresent()
        validateAddedAt(newOtodomOffer, now)

        when: 'no new offers'
        monitor.run()

        then:
        0 * notificationSender.sendOfferNotification(_ as Offer, _ as Configuration, _ as boolean);

        and:
        offersRepository.count() == 5

        when: 'some error happened but it should be ignored'
        pageFetcher.fetchPage(otodomPage) >> { throw new IOException(IGNORED_ERRORS.first()) }
        monitor.run()

        then:
        0 * notificationSender.sendWarningNotificationToAdmin(_)
    }

    def validateAddedAt(Optional<Offer> offer, Instant now) {
        Instant.parse(offer.get().getAddedAt()).isAfter(now.minusMillis(1))
    }

}
