package newoffersmonitor.db

import spock.lang.Specification

import java.time.Instant

import static newoffersmonitor.db.Offer.urlAndPrice

class OfferSpec extends Specification {

    def configurationName = "ConfigurationName"

    def url = "https://www.olx.pl/d/oferta/kawalerka-29-m2-pradnik-czerwony-dobrego-pasterza-DIC3-ABC62t9.html#6debe4385c"

    def price = "259 000 zł"

    def "equals and hashCode work properly"() {
        given:
        def now = Instant.now()
        def nowMinusMinute = now.minusSeconds(60)
        def offerFromPreviousMinute = new Offer(
                configurationName,
                urlAndPrice(url, price),
                nowMinusMinute.toString()
        )
        def offerFromNow = new Offer(
                configurationName,
                urlAndPrice(url, price),
                now.toString()
        )
        def priceUpdatedOffer = new Offer(
                configurationName,
                urlAndPrice(url, "249 000 zł"),
                now.toString()
        )
        def anotherConfigurationOffer = new Offer(
                "AnotherConfigurationName",
                urlAndPrice(url, "249 000 zł"),
                now.toString()
        )

        expect:
        [offerFromPreviousMinute, offerFromNow, priceUpdatedOffer, anotherConfigurationOffer] as Set == [offerFromPreviousMinute, priceUpdatedOffer, anotherConfigurationOffer] as Set
    }

}
