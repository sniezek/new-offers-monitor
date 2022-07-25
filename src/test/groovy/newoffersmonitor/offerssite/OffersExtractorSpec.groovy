package newoffersmonitor.offerssite

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import spock.lang.Specification

import java.time.Instant

class OffersExtractorSpec extends Specification {

    def configurationName = "ConfigurationName"

    def url1 = "https://www.olx.pl/d/oferta/kawalerka-29-m2-pradnik-czerwony-dobrego-pasterza-DIC3-ABC62t9.html#6debe4385c"

    def price1 = "259 000 zł"

    def url2 = "https://www.olx.pl/d/oferta/przestronne-dwa-pokoje-na-parterze-w-prokocimiu-DIC3-I1PXUXN.html#6faae4385c"

    def price2 = "359 000 zł"

    def "should properly create offers from fetched page"() {
        given:
        def now = Instant.now()
        def fetchedPage = Stub(Document)

        and:
        def element1 = Stub(Element)
        def element2 = Stub(Element)
        def offersSite = Stub(OffersSite) {
            getOfferElements(fetchedPage) >> new Elements([element1, element2])
            getOfferUrl(element1) >> url1
            getOfferPrice(element1) >> price1
            getOfferUrl(element2) >> url2
            getOfferPrice(element2) >> price2
        }

        expect:
        with(OffersExtractor.extractFromFetchedPage(configurationName, offersSite, fetchedPage)) {
            size() == 2
            with(it.get(0)) {
                url == url1
                price == price1
                urlAndPrice == urlAndPrice(url1, price1)
                Instant.parse(addedAt).isAfter(now.minusMillis(1))
            }
            with(it.get(1)) {
                url == url2
                price == price2
                urlAndPrice == urlAndPrice(url2, price2)
                Instant.parse(addedAt).isAfter(now.minusMillis(1))
            }
        }
    }

    def "should throw exception on invalid offers"() {
        given:
        def fetchedPage = Stub(Document)
        def offersSite = Stub(OffersSite) {
            getOfferElements(fetchedPage) >> { throw new RuntimeException("Some error") }
        }

        when:
        OffersExtractor.extractFromFetchedPage(configurationName, offersSite, fetchedPage)

        then:
        thrown OffersSiteChangeException
    }

    def "should throw exception on empty offers"() {
        given:
        def fetchedPage = Stub(Document)
        def offersSite = Stub(OffersSite) {
            getOfferElements(fetchedPage) >> []
        }

        when:
        OffersExtractor.extractFromFetchedPage(configurationName, offersSite, fetchedPage)

        then:
        thrown OffersSiteChangeException
    }

    def "should throw exception on invalid url in an offer"() {
        given:
        def fetchedPage = Stub(Document)
        def element = Stub(Element)
        def offersSite = Stub(OffersSite) {
            getOfferElements(fetchedPage) >> new Elements([element])
            getOfferUrl(element) >> { throw new RuntimeException("Some error") }
            getOfferPrice(element) >> price1
        }

        when:
        OffersExtractor.extractFromFetchedPage(configurationName, offersSite, fetchedPage)

        then:
        thrown OffersSiteChangeException
    }

    def "should throw exception on invalid price in an offer"() {
        given:
        def fetchedPage = Stub(Document)
        def element = Stub(Element)
        def offersSite = Stub(OffersSite) {
            getOfferElements(fetchedPage) >> new Elements([element])
            getOfferUrl(element) >> url1
            getOfferPrice(element) >> { throw new RuntimeException("Some error") }
        }

        when:
        OffersExtractor.extractFromFetchedPage(configurationName, offersSite, fetchedPage)

        then:
        thrown OffersSiteChangeException
    }

    def "should throw exception on empty url in an offer"() {
        given:
        def fetchedPage = Stub(Document)
        def element = Stub(Element)
        def offersSite = Stub(OffersSite) {
            getOfferElements(fetchedPage) >> new Elements([element])
            getOfferUrl(element) >> ""
            getOfferPrice(element) >> price1
        }

        when:
        OffersExtractor.extractFromFetchedPage(configurationName, offersSite, fetchedPage)

        then:
        thrown OffersSiteChangeException
    }

    def "should throw exception on empty price in an offer"() {
        given:
        def fetchedPage = Stub(Document)
        def element = Stub(Element)
        def offersSite = Stub(OffersSite) {
            getOfferElements(fetchedPage) >> new Elements([element])
            getOfferUrl(element) >> url1
            getOfferPrice(element) >> ""
        }

        when:
        OffersExtractor.extractFromFetchedPage(configurationName, offersSite, fetchedPage)

        then:
        thrown OffersSiteChangeException
    }

}
