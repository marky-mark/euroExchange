package exchangerate;

import dao.ExchangeRateDao;
import dao.ExchangeRateDaoException;
import europeancentralbank.CurrencyApiClient;
import europeancentralbank.EuropeanCentralBankApiException;
import europeancentralbank.response.EuropeanCentralBankExchange;
import europeancentralbank.response.ExchangeRate;
import europeancentralbank.response.ExchangeRateTimes;
import europeancentralbank.response.ExchangeRateWrapper;
import models.PlayExchangeRate;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import play.test.UnitTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.core.Is.is;
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
    public void shouldReturnASortedListOfExchangeRates() {
        List<PlayExchangeRate> exchangeRatesFromDB = buildExchangeRatesFromDao();
        when(exchangeRateDao.findRatesForCodeBetweenDates(eq("USD"),
                org.mockito.Matchers.<Date>any(),
                org.mockito.Matchers.<Date>any())).thenReturn(exchangeRatesFromDB);
        List<PlayExchangeRate> sortedExchangeRates = exchangeRateService.getExchangeRates("USD");
        assertThat(sortedExchangeRates.get(0).getDate().compareTo(sortedExchangeRates.get(1).getDate()), is(-1));
    }

    //this list is in opposite order for the test should sort them
    private List<PlayExchangeRate> buildExchangeRatesFromDao() {
        List<PlayExchangeRate> exchangeRates = new ArrayList<PlayExchangeRate>();
        exchangeRates.add(new PlayExchangeRate(new DateTime().plusDays(1).toDate(), -1.123));
        exchangeRates.add(new PlayExchangeRate(new DateTime().minusDays(1).toDate(), 1.123));
        return exchangeRates;
    }

    @Test
    public void shouldInsertExchangeRatesFromApiToDaoWithCodeOnly() {
        when(currencyApiClient.getRatesOverLast90Days()).thenReturn(buildExchangeRates());
        when(exchangeRateDao.findRatesForCodeBetweenDates(eq("TEST1"),
                org.mockito.Matchers.<Date>any(),
                org.mockito.Matchers.<Date>any()))
                .thenReturn(new ArrayList<PlayExchangeRate>());
        exchangeRateService.updateExchangeRatesOverLastNinetyDaysIntoCassandra("TEST1");
        verify(exchangeRateDao).insert(now, "TEST1", 1.234);
        verify(exchangeRateDao, never()).insert(now, "TEST2", 10.45);
    }

    @Test
    public void shouldNotInsertExchangeRatesFromApiToDaoIfDataRetreivedFromDB() {

        List<PlayExchangeRate> exchangeRatesAlreadyInDB = new ArrayList<PlayExchangeRate>();
        exchangeRatesAlreadyInDB.add(mock(PlayExchangeRate.class));

        when(currencyApiClient.getRatesOverLast90Days()).thenReturn(buildExchangeRates());
        when(exchangeRateDao.findRatesForCodeBetweenDates(eq("TEST1"),
                org.mockito.Matchers.<Date>any(),
                org.mockito.Matchers.<Date>any()))
                .thenReturn(exchangeRatesAlreadyInDB);
        exchangeRateService.updateExchangeRatesOverLastNinetyDaysIntoCassandra("TEST1");
        verify(exchangeRateDao, never()).insert(now, "TEST1", 1.234);
        verify(exchangeRateDao, never()).insert(now, "TEST2", 10.45);
    }


    @Test
    public void shouldFailGracefullyIfCannotWriteToCassandra() {
        when(currencyApiClient.getRatesOverLast90Days()).thenReturn(buildExchangeRates());
        exchangeRateService.updateExchangeRatesOverLastNinetyDaysIntoCassandra("TEST");
        doThrow(new ExchangeRateDaoException()).when(exchangeRateDao).insert(org.mockito.Matchers.<Date>any(),
                org.mockito.Matchers.<String>any(),
                org.mockito.Matchers.<Double>any());
    }

    @Test
    public void shouldNotInsertExchangeRatesFromApiToDaoIfEmpty() {
        when(currencyApiClient.getRatesOverLast90Days()).thenReturn(new EuropeanCentralBankExchange());
        exchangeRateService.updateExchangeRatesOverLastNinetyDaysIntoCassandra("TEST");
        verify(exchangeRateDao, never()).insert(org.mockito.Matchers.<Date>any(),
                org.mockito.Matchers.<String>any(),
                org.mockito.Matchers.<Double>any());
    }

    @Test
    public void shouldNotInsertExchangeRatesFromApiToDaoIfNull() {
        when(currencyApiClient.getRatesOverLast90Days()).thenReturn(null);
        exchangeRateService.updateExchangeRatesOverLastNinetyDaysIntoCassandra("TEST");
        verify(exchangeRateDao, never()).insert(org.mockito.Matchers.<Date>any(),
                org.mockito.Matchers.<String>any(),
                org.mockito.Matchers.<Double>any());
    }

    //TODO: more null tests!!

    @Test
    public void shouldFailElegantlyWhenExceptionThrownByApiClient() {
        when(currencyApiClient.getRatesOverLast90Days()).thenThrow(new EuropeanCentralBankApiException("test"));
        exchangeRateService.updateExchangeRatesOverLastNinetyDaysIntoCassandra("TEST");
        verify(exchangeRateDao, never()).insert(org.mockito.Matchers.<Date>any(),
                org.mockito.Matchers.<String>any(),
                org.mockito.Matchers.<Double>any());
    }

    @Test
    public void shouldNotUpdateIfAlreadyInDb() {
        List<PlayExchangeRate> toReturnFromDao = new ArrayList<PlayExchangeRate>();
        toReturnFromDao.add(new PlayExchangeRate(new Date(), 1.21));
        when(currencyApiClient.getLatestRates()).thenReturn(buildExchangeRates());
        when(exchangeRateDao.findRatesForCodeBetweenDates(eq("TEST"),
                org.mockito.Matchers.<Date>any(),
                org.mockito.Matchers.<Date>any())).thenReturn(toReturnFromDao);
        exchangeRateService.updateExchangeRatesWithLatest("TEST");
                verify(exchangeRateDao, never()).insert(org.mockito.Matchers.<Date>any(),
                org.mockito.Matchers.<String>any(),
                org.mockito.Matchers.<Double>any());
    }

    @Test
    public void shouldUpdateIfNotAlreadyInDb() {
        when(currencyApiClient.getLatestRates()).thenReturn(buildExchangeRates());
        when(exchangeRateDao.findRatesForCodeBetweenDates(eq("TEST1"),
                org.mockito.Matchers.<Date>any(),
                org.mockito.Matchers.<Date>any())).thenReturn(new ArrayList<PlayExchangeRate>());
        exchangeRateService.updateExchangeRatesWithLatest("TEST1");
        verify(exchangeRateDao, times(1)).insert(org.mockito.Matchers.<Date>any(),
                eq("TEST1"),
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
