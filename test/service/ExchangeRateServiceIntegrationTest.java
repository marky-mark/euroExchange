package service;

import dao.ExchangeRateDao;
import dao.ExchangeRateDaoImpl;
import europeancentralbank.CurrencyApiClient;
import europeancentralbank.EuropeanCentralBankApiClient;
import europeancentralbank.supplier.DefaultRestResourceSupplier;
import models.PlayExchangeRate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.test.UnitTest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.hamcrest.core.Is.is;

public class ExchangeRateServiceIntegrationTest extends UnitTest {

    private EuropeanCentralBankExchangeRateService exchangeRateService;

    @Before
    public void setup() throws URISyntaxException {

        ExchangeRateDao exchangeRateDao = new ExchangeRateDaoImpl();
        exchangeRateDao.init(9160);

        DefaultRestResourceSupplier restResourceSupplier = new DefaultRestResourceSupplier(new URI("http://www.ecb.europa.eu/stats/eurofxref"));
        CurrencyApiClient europeanBankApi = new EuropeanCentralBankApiClient(restResourceSupplier);

        exchangeRateService = new EuropeanCentralBankExchangeRateService(europeanBankApi, exchangeRateDao);
    }

    @After
    public void tearDown() {
        exchangeRateService.getExchangeRateDao().dropTable();
    }

    @Test
    public void shouldUpdateDatabaseAndGetExchangeRate() {
        exchangeRateService.updateExchangeRatesOverLastNinetyDays("USD");
        List<PlayExchangeRate> exchangeRates = exchangeRateService.getExchangeRates("USD");
        assertThat(exchangeRates.size() > 50, is(true));
        exchangeRateService.updateExchangeRatesWithLatest("USD");
        List<PlayExchangeRate> exchangeRatesAfterUpdate = exchangeRateService.getExchangeRates("USD");
        assertThat(exchangeRatesAfterUpdate.size(), is(exchangeRates.size()));
    }
}
