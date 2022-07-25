package newoffersmonitor.offerssite;

public class OffersSiteChangeException extends RuntimeException {

    public OffersSiteChangeException(String message) {
        super(message);
    }

    public OffersSiteChangeException(String message, Throwable cause) {
        super(message, cause);
    }
}
