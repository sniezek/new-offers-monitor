package newoffersmonitor.offerssite.javascript;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.util.List;

import static java.util.stream.Collectors.toList;

@JsonIgnoreProperties(ignoreUnknown = true)
@Value
public class OtodomData {

    Props props;

    public OtodomData(@JsonProperty(value = "props", required = true) Props props) {
        this.props = props;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Value
    private static class Props {

        PageProps pageProps;

        public Props(@JsonProperty(value = "pageProps", required = true) PageProps pageProps) {
            this.pageProps = pageProps;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        @Value
        private static class PageProps {

            Data data;

            public PageProps(@JsonProperty(value = "data", required = true) Data data) {
                this.data = data;
            }

            @JsonIgnoreProperties(ignoreUnknown = true)
            @Value
            private static class Data {

                SearchAds searchAds;

                public Data(@JsonProperty(value = "searchAds", required = true) SearchAds searchAds) {
                    this.searchAds = searchAds;
                }

                @JsonIgnoreProperties(ignoreUnknown = true)
                @Value
                private static class SearchAds {

                    List<Item> items;

                    public SearchAds(@JsonProperty(value = "items", required = true) List<Item> items) {
                        this.items = items;
                    }

                    @JsonIgnoreProperties(ignoreUnknown = true)
                    @Value
                    private static class Item {

                        String title;

                        String slug;

                        TotalPrice totalPrice;

                        public Item(@JsonProperty(value = "title", required = true) String title,
                                    @JsonProperty(value = "slug", required = true) String slug,
                                    @JsonProperty(value = "totalPrice", required = true) TotalPrice totalPrice) {
                            this.title = title;
                            this.slug = slug;
                            this.totalPrice = totalPrice;
                        }

                        @JsonIgnoreProperties(ignoreUnknown = true)
                        @Value
                        private static class TotalPrice {

                            String value;

                            public TotalPrice(@JsonProperty(value = "value", required = true) String value) {
                                this.value = value;
                            }

                        }

                    }

                }

            }

        }

    }

    public Elements toElements() {
        final List<Element> elements = this.getProps()
                .getPageProps()
                .getData()
                .getSearchAds()
                .getItems()
                .stream()
                .filter(item -> item.getTotalPrice() != null)
                .map(item -> {
                    final Attributes attributes = new Attributes();
                    attributes.put("href", item.getSlug());
                    attributes.put("title", item.getTitle());
                    attributes.put("price", item.getTotalPrice().getValue());

                    return new Element(Tag.valueOf("a"), null, attributes);
                })
                .collect(toList());

        return new Elements(elements);
    }

}
