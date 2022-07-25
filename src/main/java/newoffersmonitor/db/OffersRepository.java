package newoffersmonitor.db;

import java.util.Collection;
import java.util.Optional;

public interface OffersRepository {

    void save(Collection<Offer> offer);

    Optional<Offer> getOffer(Offer offer);

}
