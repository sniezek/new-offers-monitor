package newoffersmonitor.db;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This implementation is for local use only.
 * It requires a 'filedb_offers.json' file in the main project directory.
 */
@AllArgsConstructor
public class LocalFileOffersRepository implements OffersRepository {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static final String OFFERS_FILE_PATH = "filedb_offers.json";

    private final String offersFilePath;

    @Override
    @SneakyThrows
    public void save(Collection<Offer> offers) {
        final File offersFile = offersFile();
        final Map<String, Map<String, String>> offersToBeSaved = loadOffers(offersFile);

        for (Offer offer : offers) {
            offersToBeSaved.computeIfAbsent(offer.getConfigurationName(), configurationName -> new HashMap<>());
            offersToBeSaved.get(offer.getConfigurationName()).put(offer.getUrlAndPrice(), offer.getAddedAt());
        }

        OBJECT_MAPPER.writeValue(offersFile, offersToBeSaved);
    }

    @Override
    @SneakyThrows
    public Optional<Offer> getOffer(Offer offer) {
        final Map<String, Map<String, String>> offers = loadOffers(offersFile());

        return Optional
                .ofNullable(offers.get(offer.getConfigurationName()))
                .map(configurationOffers -> configurationOffers.get(offer.getUrlAndPrice()))
                .map(addedAt -> new Offer(offer.getConfigurationName(), offer.getUrlAndPrice(), addedAt));
    }

    private File offersFile() {
        return new File(offersFilePath);
    }

    private Map<String, Map<String, String>> loadOffers(File offersFile) throws IOException {
        return OBJECT_MAPPER.readValue(offersFile, new TypeReference<>() {});
    }

}
