package newoffersmonitor.offerssite

import newoffersmonitor.configurations.Page
import spock.lang.Specification

import static newoffersmonitor.offerssite.OffersSite.ADRESOWO
import static newoffersmonitor.offerssite.OffersSite.DOMINIUM
import static newoffersmonitor.offerssite.OffersSite.DOMIPORTA
import static newoffersmonitor.offerssite.OffersSite.DOMY
import static newoffersmonitor.offerssite.OffersSite.GRATKA
import static newoffersmonitor.offerssite.OffersSite.MORIZON
import static newoffersmonitor.offerssite.OffersSite.NIERUCHOMOSCI_ONLINE
import static newoffersmonitor.offerssite.OffersSite.OLX
import static newoffersmonitor.offerssite.OffersSite.OTODOM
import static newoffersmonitor.offerssite.OffersSite.OTOMOTO
import static newoffersmonitor.offerssite.OffersSite.SPRZEDAJEMY
import static newoffersmonitor.offerssite.OffersSite.SZYBKO

class OffersSiteResolverSpec extends Specification {

    def offersSiteResolver = new OffersSiteResolver()

    def "should return proper OffersSite based on page url"() {
        expect:
        offersSiteResolver.resolve(new Page(url, true)) == offersSite

        where:
        url                                                                                                                                                                                                                                                                                                                                                                                                                                            || offersSite
        "https://www.olx.pl/nieruchomosci/mieszkania/sprzedaz/krakow/?search%5Bfilter_float_price%3Ato%5D=375000"                                                                                                                                                                                                                                                                                                                                      || OLX
        "https://www.otodom.pl/pl/oferty/sprzedaz/mieszkanie/krakow?priceMax=375000&by=LATEST&direction=DESC"                                                                                                                                                                                                                                                                                                                                          || OTODOM
        "https://adresowo.pl/mieszkania/krakow/p-38_lod"                                                                                                                                                                                                                                                                                                                                                                                               || ADRESOWO
        "https://www.morizon.pl/mieszkania/najnowsze/krakow/?ps%5Bprice_to%5D=375000"                                                                                                                                                                                                                                                                                                                                                                  || MORIZON
        "https://www.domiporta.pl/mieszkanie/sprzedam/malopolskie/krakow?Price.To=375000&SortingOrder=InsertionDate"                                                                                                                                                                                                                                                                                                                                   || DOMIPORTA
        "https://www.nieruchomosci-online.pl/szukaj.html?3,mieszkanie,sprzedaz,,Krak%C3%B3w,,,,-375000"                                                                                                                                                                                                                                                                                                                                                || NIERUCHOMOSCI_ONLINE
        "https://www.dominium.pl/wtorny/mieszkania/sprzedaz/Krak%C3%B3w?cenaDo=375000&sortuj=8a"                                                                                                                                                                                                                                                                                                                                                       || DOMINIUM
        "https://www.szybko.pl/l/na-sprzedaz/lokal-mieszkalny+mieszkanie/Krak%C3%B3w/mieszkania?sort=newest&assetType=lokal-mieszkalny&assetSubType=mieszkanie&localization_search_text=Krak%C3%B3w&market=&price_min_sell=&price_max_sell=375000&price_min_rent=&price_max_rent=&meters_min=&meters_max=&rooms_min=&rooms_max=&add_search_text=mieszkania&price_meters_min=&price_meters_max=&level_min=&level_max=&date_added=&agency_asset_number=" || SZYBKO
        "https://sprzedajemy.pl/krakow/nieruchomosci/mieszkania/sprzedaz?inp_price%5Bto%5D=375000&offset=0&sort=inp_srt_date_d"                                                                                                                                                                                                                                                                                                                        || SPRZEDAJEMY
        "https://gratka.pl/nieruchomosci/mieszkania/krakow/sprzedaz?cena-calkowita:max=375000&sort=newest"                                                                                                                                                                                                                                                                                                                                             || GRATKA
        "https://domy.pl/mieszkania-sprzedaz-krakow-pl?ps%5Badvanced_search%5D=1&ps%5Bsort_order%5D=date&ps%5Bsort_asc%5D=0&ps%5Blocation%5D%5Btype%5D=1&ps%5Btransaction%5D=1&ps%5Btype%5D=1&ps%5Blocation%5D%5Btext_queue%5D%5B%5D=Krak%C3%B3w&ps%5Blocation%5D%5Btext_tmp_queue%5D%5B%5D=Krak%C3%B3w&ps%5Bprice_to%5D=375000"                                                                                                                       || DOMY
        "https://www.otomoto.pl/osobowe/mercedes-benz/c-klasa/seg-coupe/od-2011?search%5Bfilter_enum_generation%5D=gen-w204-2007-2014&search%5Bfilter_enum_fuel_type%5D=petrol&search%5Bfilter_enum_gearbox%5D=automatic&search%5Bfilter_enum_damaged%5D=0&search%5Border%5D=created_at_first%3Adesc&search%5Badvanced_search_expanded%5D=true"                                                                                                        || OTOMOTO
    }

    def "should throw exception for invalid url"() {
        when:
        offersSiteResolver.resolve(new Page("invalid", true))

        then:
        thrown IllegalArgumentException
    }

    def "should throw exception for unhandled site"() {
        when:
        offersSiteResolver.resolve(new Page("https://arandompage.com/offers", true))

        then:
        thrown IllegalArgumentException
    }

}
