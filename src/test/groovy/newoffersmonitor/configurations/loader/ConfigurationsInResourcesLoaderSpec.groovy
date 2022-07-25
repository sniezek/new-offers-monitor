package newoffersmonitor.configurations.loader

import newoffersmonitor.configurations.Page
import spock.lang.Specification

class ConfigurationsInResourcesLoaderSpec extends Specification {

    def "should properly load configurations"() {
        given:
        def configurationsInResourcesLoader = new ConfigurationsInResourcesLoader("test_configurations/test_configurations.json")

        when:
        def configurations = configurationsInResourcesLoader.loadConfigurations();

        then:
        with(configurations.getConfigurations()) {
            with(it.get(0)) {
                name == "OlafFlatsTo375k"
                active
                emails == [
                        "someemail@email.com",
                        "someotheremail@email.com"
                ] as Set
                ignoredKeywords == [
                        "zlocie",
                        "złocie"
                ] as Set
                superKeywords == [
                        "krowodrz",
                        "bronowic"
                ] as Set
                pages == [
                        new Page("https://www.olx.pl/nieruchomosci/mieszkania/sprzedaz/krakow/?search%5Bfilter_float_price%3Ato%5D=375000",
                                true),
                        new Page("https://www.otodom.pl/pl/oferty/sprzedaz/mieszkanie/krakow?priceMax=375000&by=LATEST&direction=DESC",
                                true),
                        new Page("https://www.morizon.pl/mieszkania/najnowsze/krakow/?ps%5Bprice_to%5D=375000",
                                false)
                ]
            }
            with(it.get(1)) {
                name == "OlafW204Coupe"
                active
                emails == ["someemail@email.com"] as Set
                ignoredKeywords == [] as Set
                superKeywords == [] as Set
                pages == [
                        new Page("https://www.otomoto.pl/osobowe/mercedes-benz/c-klasa/seg-coupe/od-2011?search%5Bfilter_enum_generation%5D=gen-w204-2007-2014&search%5Bfilter_enum_fuel_type%5D=petrol&search%5Bfilter_enum_gearbox%5D=automatic&search%5Bfilter_enum_damaged%5D=0&search%5Border%5D=created_at_first%3Adesc&search%5Badvanced_search_expanded%5D=true",
                                true)
                ]
            }
            with(it.get(2)) {
                name == "SomeInactiveConfiguration"
                !active
                emails == [] as Set
                ignoredKeywords == [] as Set
                superKeywords == [] as Set
                pages == [
                        new Page("https://www.otomoto.pl/osobowe/mercedes-benz/c-klasa/seg-coupe/od-2011?search%5Bfilter_enum_generation%5D=gen-w204-2007-2014&search%5Bfilter_enum_fuel_type%5D=petrol&search%5Bfilter_enum_gearbox%5D=automatic&search%5Bfilter_enum_damaged%5D=0&search%5Border%5D=created_at_first%3Adesc&search%5Badvanced_search_expanded%5D=true",
                                true)
                ]
            }
        }
    }

    def "should throw exception on invalid configuration"() {
        given:
        def invalidConfigurationsLoader = new ConfigurationsInResourcesLoader("test_configurations/test_invalid_configurations.json")

        when:
        invalidConfigurationsLoader.loadConfigurations()

        then:
        thrown Exception
    }

}
