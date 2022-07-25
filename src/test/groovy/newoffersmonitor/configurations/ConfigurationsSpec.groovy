package newoffersmonitor.configurations

import spock.lang.Specification

class ConfigurationsSpec extends Specification {

    def "should properly return active configurations"() {
        given:
        def activeConfiguration = new Configuration(
                "OlafFlatsTo375k",
                true,
                ["someemail@email.com"] as Set,
                null,
                null,
                [new Page("https://www.olx.pl/nieruchomosci/mieszkania/sprzedaz/krakow/?search%5Bfilter_float_price%3Ato%5D=375000",
                        true)]
        )
        def inactiveConfiguration = new Configuration(
                "SomeInactiveConfiguration",
                false,
                [] as Set,
                null,
                null,
                []
        )

        expect:
        new Configurations([activeConfiguration, inactiveConfiguration]).getActiveConfigurations() == [activeConfiguration]
    }

}
