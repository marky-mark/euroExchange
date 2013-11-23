package exchangerate;

import dao.ExchangeRateDao;
import dao.ExchangeRateDaoImpl;
import europeancentralbank.CurrencyApiClient;
import europeancentralbank.EuropeanCentralBankApiClient;
import europeancentralbank.supplier.DefaultRestResourceSupplier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.test.UnitTest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Map;

import static org.hamcrest.core.Is.is;

public class ExchangeHandlerIntegrationTest extends UnitTest {

    private ExchangeRateService exchangeRateService;

    @Before
    public void setup() throws URISyntaxException {

        ExchangeRateDao exchangeRateDao = new ExchangeRateDaoImpl();
        exchangeRateDao.init(Context.CASSANDRA_PORT);

        DefaultRestResourceSupplier restResourceSupplier = new DefaultRestResourceSupplier(new URI(Context.EUROPEAN_BANK_ENDPOINT));
        CurrencyApiClient europeanBankApi = new EuropeanCentralBankApiClient(restResourceSupplier);

        exchangeRateService = new EuropeanCentralBankExchangeRateServiceImpl(europeanBankApi, exchangeRateDao);
    }

    @After
    public void tearDown() {
        exchangeRateService.getExchangeRateDao().dropTable();
    }

    @Test
    public void shouldUpdateDatabaseAndGetExchangeRate() {
        exchangeRateService.updateExchangeRatesOverLastNinetyDaysIntoCassandra();
        Map<Date, Double> exchangeRates = exchangeRateService.getExchangeRates("USD");
        assertThat(exchangeRates.size() > 50, is(true));
        exchangeRateService.updateExchangeRatesWithLatest();
        Map<Date, Double> exchangeRatesAfterUpdate = exchangeRateService.getExchangeRates("USD");
        assertThat(exchangeRatesAfterUpdate.size(), is(exchangeRates.size()));
    }
}
