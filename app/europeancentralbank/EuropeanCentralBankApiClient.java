package europeancentralbank;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import europeancentralbank.response.EuropeanCentralBankExchange;
import europeancentralbank.supplier.RestResourceSupplier;

import javax.ws.rs.core.MediaType;

public class EuropeanCentralBankApiClient implements CurrencyApiClient {

    public static final String DAILY_RATE_PATH = "eurofxref-daily.xml";
    public static final String LAST_90_DAYS_PATH = "eurofxref-hist-90d.xml";
    private final RestResourceSupplier restResourceSupplier;

    public EuropeanCentralBankApiClient(RestResourceSupplier restResourceSupplier) {
        this.restResourceSupplier = restResourceSupplier;
    }

    public EuropeanCentralBankExchange getLatestRates() {
        return getRootTypeFor(DAILY_RATE_PATH);
    }

    @Override
    public EuropeanCentralBankExchange getRatesOverLast90Days() {
        return getRootTypeFor(LAST_90_DAYS_PATH);
    }

    private EuropeanCentralBankExchange getRootTypeFor(String path) {
        try {
            WebResource.Builder resource = restResourceSupplier.get()
                    .path(path)
                    .type(MediaType.TEXT_XML)
                    .accept(MediaType.TEXT_XML);

            return resource.get(EuropeanCentralBankExchange.class);
        } catch (UniformInterfaceException e) {
            throw new EuropeanCentralBankApiException(path, e);
        } catch (ClientHandlerException e) {
            throw new EuropeanCentralBankApiException(path, e);
        }
    }
}
