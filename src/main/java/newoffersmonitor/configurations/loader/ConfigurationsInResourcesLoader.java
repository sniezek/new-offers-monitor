package newoffersmonitor.configurations.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import newoffersmonitor.configurations.Configurations;

import java.io.File;

@AllArgsConstructor
public class ConfigurationsInResourcesLoader implements ConfigurationsLoader {

    public static final String CONFIGURATIONS_FILE_PATH = "configurations/configurations.json";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final String configurationsFilePath;

    @Override
    public Configurations loadConfigurations() throws Exception {
        final File configurationsFile = getConfigurationsFile();

        return OBJECT_MAPPER.readValue(configurationsFile, Configurations.class);
    }

    private File getConfigurationsFile() {
        return new File(getClass().getClassLoader().getResource(configurationsFilePath).getFile());
    }

}
