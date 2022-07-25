package newoffersmonitor.configurations

import spock.lang.Specification

class ConfigurationSpec extends Specification {

    def configurationName = "OlafFlatsTo375k"

    def "should properly return active pages"() {
        given:
        def activePage = new Page("https://www.olx.pl/nieruchomosci/mieszkania/sprzedaz/krakow/?search%5Bfilter_float_price%3Ato%5D=375000",
                true)

        expect:
        new Configuration(configurationName,
                true,
                [] as Set,
                [] as Set,
                null,
                []).getActivePages() == []

        and:
        new Configuration(configurationName,
                true,
                [] as Set,
                null,
                null,
                [activePage,
                 new Page("https://www.otodom.pl/pl/oferty/sprzedaz/mieszkanie/krakow?priceMax=375000&by=LATEST&direction=DESC",
                         false)]
        ).getActivePages() == [activePage]
    }

    def "should properly return ignored keywords"() {
        given:
        def ignoredKeyword = "ignoredkeyword"

        expect:
        new Configuration(configurationName,
                true,
                [] as Set,
                null,
                null,
                []).getIgnoredKeywords() == [] as Set

        and:
        new Configuration("OlafFlatsTo375k",
                true,
                [] as Set,
                [ignoredKeyword] as Set,
                null,
                []
        ).getIgnoredKeywords() == [ignoredKeyword] as Set
    }

    def "should properly return super keywords"() {
        given:
        def superKeyword = "superkeyword"

        expect:
        new Configuration(configurationName,
                true,
                [] as Set,
                null,
                null,
                []).getSuperKeywords() == [] as Set

        and:
        new Configuration(configurationName,
                true,
                [] as Set,
                [] as Set,
                [superKeyword] as Set,
                []
        ).getSuperKeywords() == [superKeyword] as Set
    }

}
