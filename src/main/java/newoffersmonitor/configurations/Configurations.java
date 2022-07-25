package newoffersmonitor.configurations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Value
public class Configurations {

    List<Configuration> configurations;

    public Configurations(@JsonProperty(value = "configurations", required = true) List<Configuration> configurations) {
        this.configurations = configurations;
    }

    public List<Configuration> getActiveConfigurations() {
        return configurations
                .stream()
                .filter(Configuration::isActive)
                .collect(toList());
    }

}
