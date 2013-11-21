package europeancentralbank;

import com.sun.jersey.api.client.WebResource;
import europeancentralbank.response.EuropeanCentralBankExchange;
import europeancentralbank.supplier.RestResourceSupplier;

import javax.ws.rs.core.MediaType;

public class EuropaCurrencyApiClient implements CurrencyApiClient {

    public static final String DAILY_RATE_PATH = "eurofxref-daily.xml";
    public static final String LAST_90_DAYS_PATH = "eurofxref-hist-90d.xml";
    private final RestResourceSupplier restResourceSupplier;

    public EuropaCurrencyApiClient(RestResourceSupplier restResourceSupplier) {
        this.restResourceSupplier = restResourceSupplier;
    }

    public EuropeanCentralBankExchange getDailyCurrencies() {
        return getRootTypeFor(DAILY_RATE_PATH);
    }

    @Override
    public EuropeanCentralBankExchange getRatesOverLast90Days() {
        return getRootTypeFor(LAST_90_DAYS_PATH);
    }

    private EuropeanCentralBankExchange getRootTypeFor(String path) {
        WebResource.Builder resource = restResourceSupplier.get()
                .path(path)
                .type(MediaType.TEXT_XML)
                .accept(MediaType.TEXT_XML);

        return resource.get(EuropeanCentralBankExchange.class);
    }
}
