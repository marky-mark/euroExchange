package exchangerate;

import dao.ExchangeRateDao;
import dao.ExchangeRateDaoException;
import europeancentralbank.CurrencyApiClient;
import europeancentralbank.EuropeanCentralBankApiException;
import europeancentralbank.response.EuropeanCentralBankExchange;
import europeancentralbank.response.ExchangeRate;
import europeancentralbank.response.ExchangeRateTimes;
import europeancentralbank.response.ExchangeRateWrapper;
import org.junit.Before;
import org.junit.Test;
import play.test.UnitTest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class EuropeanCentralBankExchangeRateServiceTest extends UnitTest {

    private ExchangeRateService exchangeRateService;
    private CurrencyApiClient currencyApiClient;
    private ExchangeRateDao exchangeRateDao;
    private Date now;

    @Before
    public void init() {
        currencyApiClient = mock(CurrencyApiClient.class);
        exchangeRateDao = mock(ExchangeRateDao.class);
        exchangeRateService = new EuropeanCentralBankExchangeRateServiceImpl(currencyApiClient, exchangeRateDao);
    }

    @Test
    public void shouldInsertExchangeRatesFromApiToDao() {
        when(currencyApiClient.getRatesOverLast90Days()).thenReturn(buildExchangeRates());
        exchangeRateService.updateExchangeRatesOverLastNinetyDaysIntoCassandra();
        verify(exchangeRateDao).insert(now, "TEST1", 1.234);
        verify(exchangeRateDao).insert(now, "TEST2", 10.45);
    }

    @Test
    public void shouldFailGracefullyIfCannotWriteToCassandra() {
        when(currencyApiClient.getRatesOverLast90Days()).thenReturn(buildExchangeRates());
        exchangeRateService.updateExchangeRatesOverLastNinetyDaysIntoCassandra();
        doThrow(new ExchangeRateDaoException()).when(exchangeRateDao).insert(org.mockito.Matchers.<Date>any(),
                org.mockito.Matchers.<String>any(),
                org.mockito.Matchers.<Double>any());
    }

    @Test
    public void shouldNotInsertExchangeRatesFromApiToDaoIfEmpty() {
        when(currencyApiClient.getRatesOverLast90Days()).thenReturn(new EuropeanCentralBankExchange());
        exchangeRateService.updateExchangeRatesOverLastNinetyDaysIntoCassandra();
        verify(exchangeRateDao, never()).insert(org.mockito.Matchers.<Date>any(),
                org.mockito.Matchers.<String>any(),
                org.mockito.Matchers.<Double>any());
    }

    @Test
    public void shouldNotInsertExchangeRatesFromApiToDaoIfNull() {
        when(currencyApiClient.getRatesOverLast90Days()).thenReturn(null);
        exchangeRateService.updateExchangeRatesOverLastNinetyDaysIntoCassandra();
        verify(exchangeRateDao, never()).insert(org.mockito.Matchers.<Date>any(),
                org.mockito.Matchers.<String>any(),
                org.mockito.Matchers.<Double>any());
    }

    //TODO: more null tests!!

    @Test
    public void shouldFailElegantlyWhenExceptionThrownByApiClient() {
        when(currencyApiClient.getRatesOverLast90Days()).thenThrow(new EuropeanCentralBankApiException("test"));
        exchangeRateService.updateExchangeRatesOverLastNinetyDaysIntoCassandra();
        verify(exchangeRateDao, never()).insert(org.mockito.Matchers.<Date>any(),
                org.mockito.Matchers.<String>any(),
                org.mockito.Matchers.<Double>any());
    }

    @Test
    public void shouldNotUpdateIfAlreadyInDb() {
        Map<Date, Double> toReturnFromDao = new HashMap<Date, Double>();
        toReturnFromDao.put(new Date(), 1.21);
        when(currencyApiClient.getLatestRates()).thenReturn(buildExchangeRates());
        when(exchangeRateDao.findRatesForCodeBetweenDates(org.mockito.Matchers.<String>any(),
                org.mockito.Matchers.<Date>any(),
                org.mockito.Matchers.<Date>any())).thenReturn(toReturnFromDao);
        exchangeRateService.updateExchangeRatesWithLatest();
                verify(exchangeRateDao, never()).insert(org.mockito.Matchers.<Date>any(),
                org.mockito.Matchers.<String>any(),
                org.mockito.Matchers.<Double>any());
    }

    @Test
    public void shouldUpdateIfNotAlreadyInDb() {
        when(currencyApiClient.getLatestRates()).thenReturn(buildExchangeRates());
        when(exchangeRateDao.findRatesForCodeBetweenDates(org.mockito.Matchers.<String>any(),
                org.mockito.Matchers.<Date>any(),
                org.mockito.Matchers.<Date>any())).thenReturn(new HashMap<Date, Double>());
        exchangeRateService.updateExchangeRatesWithLatest();
                verify(exchangeRateDao, times(2)).insert(org.mockito.Matchers.<Date>any(),
                org.mockito.Matchers.<String>any(),
                org.mockito.Matchers.<Double>any());
    }

    //TODO: some exception tests like the others
    //TODO: some more null tests

    private EuropeanCentralBankExchange buildExchangeRates() {
        EuropeanCentralBankExchange exchange = new EuropeanCentralBankExchange();
        ExchangeRateWrapper wrapper = new ExchangeRateWrapper();
        ExchangeRateTimes times = new ExchangeRateTimes();

        ExchangeRate exchangeRate1 = buildExchangeRate("TEST1", 1.234);
        times.getExchangeRates().add(exchangeRate1);

        ExchangeRate exchangeRate2 = buildExchangeRate("TEST2", 10.45);
        times.getExchangeRates().add(exchangeRate2);

        now = new Date();
        times.setDate(now);
        wrapper.getExchangeRateTimes().add(times);
        exchange.setExchangeRateWrapper(wrapper);
        return exchange;
    }

    private ExchangeRate buildExchangeRate(String code, Double rate) {
        ExchangeRate exchangeRate1 = new ExchangeRate();
        exchangeRate1.setCurrency(code);
        exchangeRate1.setRate(rate);
        return exchangeRate1;
    }
}
