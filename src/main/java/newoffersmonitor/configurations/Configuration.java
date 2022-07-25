package newoffersmonitor.configurations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Value
public class Configuration {

    String name;

    boolean active;

    Set<String> emails;

    Set<String> ignoredKeywords;

    Set<String> superKeywords;

    List<Page> pages;

    public Configuration(@JsonProperty(value = "name", required = true) String name,
                         @JsonProperty(value = "active", required = true) boolean active,
                         @JsonProperty(value = "emails", required = true) Set<String> emails,
                         @JsonProperty("ignoredKeywords") Set<String> ignoredKeywords,
                         @JsonProperty("superKeywords") Set<String> superKeywords,
                         @JsonProperty(value = "pages", required = true) List<Page> pages) {
        this.name = name;
        this.active = active;
        this.emails = emails;
        this.ignoredKeywords = keywords(ignoredKeywords);
        this.superKeywords = keywords(superKeywords);
        this.pages = pages;
    }

    public List<Page> getActivePages() {
        return pages
                .stream()
                .filter(Page::isActive)
                .collect(toList());
    }

    public boolean hasEmailsDefined() {
        return !emails.isEmpty();
    }

    private Set<String> keywords(Set<String> keywords) {
        return keywords == null ? Set.of() : keywords;
    }

}
