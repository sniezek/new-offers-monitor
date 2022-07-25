package newoffersmonitor.configurations

import spock.lang.Specification

class PageSpec extends Specification {

    def "should throw exception for empty url"() {
        when:
        new Page("", true)

        then:
        thrown IllegalArgumentException
    }

}
