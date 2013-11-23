package dao;

import models.PlayExchangeRate;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.test.UnitTest;

import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;

/**
 * This is connecting to live cassandra, would prefer this to start up with H2
 */
public class ExchangeRateDaoIntegrationTest extends UnitTest {

    public static final int TEST_PORT = 9160;
    private ExchangeRateDao euroExchangeDAO;

    @Before
    public void init() {
        euroExchangeDAO = new ExchangeRateDaoImpl();
        euroExchangeDAO.init(TEST_PORT);
    }

    @After
    public void tearDown() {
        euroExchangeDAO.dropTable();
    }

    /**
     * Pretty bad bunching all of these tests together..should have 1 per situation
     */
    @Test
    public void shouldGetResultsInDatbaseOrderedByTimestamp() {
        Date date1 = new DateTime().plusDays(5).toDate();
        double rate1 = 8.01;
        euroExchangeDAO.insert(date1, "TEST", rate1);
        Date date2 = new DateTime().minusDays(3).toDate();
        double rate2 = 7.001;
        euroExchangeDAO.insert(date2, "TEST", rate2);
        Date date3 = new DateTime().minusDays(1).toDate();
        double rate3 = 2.0;
        euroExchangeDAO.insert(date3, "TEST", rate3);

        DateTime nextDay = new DateTime().plusDays(6);
        DateTime dayBefore = new DateTime().minusDays(4);
        List<PlayExchangeRate> playExchangeRates = euroExchangeDAO.findRatesForCodeBetweenDates("TEST", nextDay.toDate(), dayBefore.toDate());
        assertThat(playExchangeRates.size(), is(3));

        //List order is unpredictable
        assertTrue(playExchangeRates.contains(new PlayExchangeRate(date1, rate1)));
        assertTrue(playExchangeRates.contains(new PlayExchangeRate(date2, rate2)));
        assertTrue(playExchangeRates.contains(new PlayExchangeRate(date3, rate3)));
    }

    @Test
    public void shouldFindNoExchangeRates() {

        Date timestamp1 = new DateTime().plusDays(5).toDate();
        double rate1 = 8.01;
        euroExchangeDAO.insert(timestamp1, "TEST", rate1);

        DateTime nextDay = new DateTime().plusDays(1);
        DateTime dayBefore = new DateTime().minusDays(1);
        List<PlayExchangeRate> playExchangeRates = euroExchangeDAO.findRatesForCodeBetweenDates("TEST", nextDay.toDate(), dayBefore.toDate());
        assertThat(playExchangeRates.size(), is(0));
    }
}
