package europeancentralbannk.response;

import europeancentralbank.CurrencyApiClient;
import europeancentralbank.EuropaCurrencyApiClient;
import europeancentralbank.response.EuropeanCentralBankExchange;
import europeancentralbank.response.ExchangeRate;
import europeancentralbank.response.ExchangeRateTimes;
import europeancentralbank.supplier.DefaultRestResourceSupplier;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import play.test.UnitTest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;

public class EuropaCurrencyApiClientIntegrationTest extends UnitTest {

    public static final String EUROPA_EU_API = "http://www.ecb.europa.eu/stats/eurofxref";
    public static final int NUMBER_OF_EXCHANGE_RATES = 33;
    private CurrencyApiClient europaCurrencyApi;

    @Before
    public void setup() throws URISyntaxException {
        DefaultRestResourceSupplier restResourceSupplier = new DefaultRestResourceSupplier(new URI(EUROPA_EU_API));
        europaCurrencyApi = new EuropaCurrencyApiClient(restResourceSupplier);
    }

    @Test
    public void shouldDownloadDailyRate() {
        EuropeanCentralBankExchange dailyRate = europaCurrencyApi.getDailyCurrencies();
        List<ExchangeRateTimes> dailyRates = dailyRate.getExchangeRateWrapper().getExchangeRateTimes();
        assertThat(dailyRates.size(), is(1));
        assertAllDataIsNotNull(dailyRates.get(0).getExchangeRates());
    }

    @Test
    public void shouldDownloadExchangeRatesOverLast90Days() {
        EuropeanCentralBankExchange dailyRate = europaCurrencyApi.getRatesOverLast90Days();
        List<ExchangeRateTimes> dailyRates = dailyRate.getExchangeRateWrapper().getExchangeRateTimes();
        //64 business days in 90 days?...not consistent
        assertThat(dailyRates.size() > 55, is(true));
        for (ExchangeRateTimes exchangeRateTimes: dailyRates) {
            assertAllDataIsNotNull(exchangeRateTimes.getExchangeRates());
        }
    }

    private void assertAllDataIsNotNull(List<ExchangeRate> exchangeRates) {
        assertThat(exchangeRates.size(), is(NUMBER_OF_EXCHANGE_RATES));

        for (ExchangeRate exchangeRate: exchangeRates) {
            assertThat(exchangeRate.getCurrency(), is(not(CoreMatchers.<Object>nullValue())));
            assertThat(exchangeRate.getRate(), is(not(CoreMatchers.<Object>nullValue())));
        }
    }
}
