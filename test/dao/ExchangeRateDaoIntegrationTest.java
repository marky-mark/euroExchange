package dao;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import play.test.UnitTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;

/**
 * This is connecting to live cassandra, would prefer this to start up with H2
 */
public class ExchangeRateDaoIntegrationTest extends UnitTest {

    private ExchangeRateDao euroExchangeDAO;

    @Before
    public void init() {
        euroExchangeDAO = new ExchangeRateDaoImpl();
        euroExchangeDAO.init();
    }

    /**
     * Pretty bad bunching all of these tests together..should have 1 per situation
     */
    @Test
    public void shouldGetResultsInDatbaseOrderedByTimestamp() {
        Date timestamp1 = new DateTime().plusDays(5).toDate();
        double rate1 = 8.01;
        euroExchangeDAO.insert(timestamp1, "TEST", rate1);
        Date timestamp2 = new DateTime().minusDays(3).toDate();
        double rate2 = 7.001;
        euroExchangeDAO.insert(timestamp2, "TEST", rate2);
        Date timestamp3 = new DateTime().minusDays(1).toDate();
        double rate3 = 2.0;
        euroExchangeDAO.insert(timestamp3, "TEST", rate3);

        DateTime nextDay = new DateTime().plusDays(6);
        DateTime dayBefore = new DateTime().minusDays(4);
        Map<Date, Double> exchangeRates = euroExchangeDAO.findRatesForCodeBetweenDates("TEST", nextDay.toDate(), dayBefore.toDate());
        assertThat(exchangeRates.size(), is(3));

        //make sure in order of time
        List<Date> dates = new ArrayList<Date>(exchangeRates.keySet());
        List<Double> rates = new ArrayList<Double>(exchangeRates.values());
        assertThat(dates.get(0), is(timestamp2));
        assertThat(dates.get(1), is(timestamp3));
        assertThat(dates.get(2), is(timestamp1));
        assertThat(rates.get(0), is(rate2));
        assertThat(rates.get(1), is(rate3));
        assertThat(rates.get(2), is(rate1));
    }
}
