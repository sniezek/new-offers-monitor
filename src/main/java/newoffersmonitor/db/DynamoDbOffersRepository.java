package newoffersmonitor.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.Collection;
import java.util.Optional;

import static com.google.common.collect.Iterables.partition;

/**
 * This implementation is for use with AWS only.
 * It requires a created DynamoDB table 'Offers' with a `configurationName` partition key and a `urlAndPrice` sort key.
 */
public class DynamoDbOffersRepository implements OffersRepository {

    private static final int BATCH_SIZE = 25;

    private final DynamoDBMapper mapper = new DynamoDBMapper(AmazonDynamoDBClientBuilder.standard().build());

    @Override
    public void save(Collection<Offer> offers) {
        partition(offers, BATCH_SIZE).forEach(mapper::batchSave);
    }

    @Override
    public Optional<Offer> getOffer(Offer offer) {
        return Optional.ofNullable(mapper.load(Offer.class, offer.getConfigurationName(), offer.getUrlAndPrice()));
    }

}
