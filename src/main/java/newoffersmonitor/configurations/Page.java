package newoffersmonitor.configurations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class Page {

    String url;

    boolean active;

    public Page(@JsonProperty(value = "url", required = true) String url,
                @JsonProperty(value = "active", required = true) boolean active) {
        if (url.isEmpty()) {
            throw new IllegalArgumentException("'url' must not be empty in a configuration");
        }
        this.url = url;
        this.active = active;
    }
}
