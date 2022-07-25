package newoffersmonitor.db;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This implementation is for tests only.
 */
public class InMemoryOffersRepository implements OffersRepository {

    private final Map<Offer, Offer> savedOffers = new HashMap<>();

    @Override
    public void save(Collection<Offer> offers) {
        offers.forEach(offer -> savedOffers.put(offer, offer));
    }

    @Override
    public Optional<Offer> getOffer(Offer offer) {
        return Optional.ofNullable(savedOffers.get(offer));
    }

    public int count() {
        return savedOffers.size();
    }

}
