package newoffersmonitor.db;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * This class has DynamoDB annotations but is meant to be used with any OffersRepository implementation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "Offers")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Offer {

    private static final String URL_AND_PRICE_SEPARATOR = "|";

    @DynamoDBHashKey
    @EqualsAndHashCode.Include
    private String configurationName;

    @DynamoDBRangeKey
    @EqualsAndHashCode.Include
    private String urlAndPrice;

    private String addedAt;

    @DynamoDBIgnore
    public String getUrl() {
        return urlAndPrice.substring(0, urlAndPrice.lastIndexOf(URL_AND_PRICE_SEPARATOR));
    }

    @DynamoDBIgnore
    public String getPrice() {
        return urlAndPrice.substring(urlAndPrice.lastIndexOf(URL_AND_PRICE_SEPARATOR) + 1);
    }

    public static String urlAndPrice(String url, String price) {
        return String.join(URL_AND_PRICE_SEPARATOR, url, price);
    }

}
